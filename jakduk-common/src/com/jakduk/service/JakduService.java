package com.jakduk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakduk.authentication.common.CommonPrincipal;
import com.jakduk.common.CommonConst;
import com.jakduk.dao.JakdukDAO;
import com.jakduk.exception.RepositoryExistException;
import com.jakduk.model.db.*;
import com.jakduk.model.embedded.CommonWriter;
import com.jakduk.model.embedded.LocalName;
import com.jakduk.model.web.MyJakduRequest;
import com.jakduk.repository.JakduRepository;
import com.jakduk.repository.JakduScheduleRepository;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.*;

/**
 * Created by pyohwan on 15. 12. 26.
 */

@Service
public class JakduService {

    @Autowired
    private JakdukDAO jakdukDAO;

    @Autowired
    private CommonService commonService;

    @Autowired
    private UserService userService;

    @Autowired
    private JakduScheduleRepository jakduScheduleRepository;

    @Autowired
    private JakduRepository jakduRepository;

    private Logger logger = Logger.getLogger(this.getClass());

    public void getSchedule(Model model, Locale locale) {

        model.addAttribute("dateTimeFormat", commonService.getDateTimeFormat(locale));
    }

    public void getDataScheduleList(Model model, String language, int page, int size) {

        Sort sort = new Sort(Sort.Direction.ASC, Arrays.asList("group", "date"));
        Pageable pageable = new PageRequest(page - 1, size, sort);

        Set<ObjectId> fcIds = new HashSet<>();
        Set<ObjectId> competitionIds = new HashSet<>();
        List<JakduSchedule> jakduSchedules = jakduScheduleRepository.findAll(pageable).getContent();

        for (JakduSchedule jakduSchedule : jakduSchedules) {
            fcIds.add(new ObjectId(jakduSchedule.getHome().getId()));
            fcIds.add(new ObjectId(jakduSchedule.getAway().getId()));
            if (jakduSchedule.getCompetition() != null)
                competitionIds.add(new ObjectId(jakduSchedule.getCompetition().getId()));
        }

        Map<String, LocalName> fcNames = new HashMap<>();
        Map<String, LocalName> competitionNames = new HashMap<>();

        List<FootballClub> footballClubs = jakdukDAO.getFootballClubList(new ArrayList<>(fcIds), language, CommonConst.NAME_TYPE.fullName);
        List<Competition> competitions = jakdukDAO.getCompetitionList(new ArrayList<>(competitionIds), language);

        for (FootballClub fc : footballClubs) {
            fcNames.put(fc.getOrigin().getId(), fc.getNames().get(0));
        }

        for (Competition competition : competitions) {
            competitionNames.put(competition.getId(), competition.getNames().get(0));
        }

        try {
            model.addAttribute("fcNames", new ObjectMapper().writeValueAsString(fcNames));
            model.addAttribute("competitionNames", new ObjectMapper().writeValueAsString(competitionNames));
        } catch (IOException e) {
            e.printStackTrace();
        }

        model.addAttribute("schedules", jakduSchedules);
    }

    public Map getDataSchedule(String id) {

        Map<String, Object> result = new HashMap<>();

        JakduSchedule jakduSchedule = jakduScheduleRepository.findOne(id);

        result.put("jakduSchedule", jakduSchedule);

        return result;
    }

    public Map getDataWrite(Locale locale) {

        Map<String, Object> result = new HashMap<>();

        CommonPrincipal principal = userService.getCommonPrincipal();
        String accountId = principal.getId();
        String accountUsername = principal.getUsername();
        String accountType = principal.getType();

        if (accountId == null)
            return result;

        CommonWriter writer = new CommonWriter();
        writer.setUserId(accountId);
        writer.setUsername(accountUsername);
        writer.setType(accountType);

        String language = commonService.getLanguageCode(locale, null);

        Set<ObjectId> fcIds = new HashSet<>();
        List<Jakdu> jakdus = new ArrayList<>();
        Set<ObjectId> competitionIds = new HashSet<>();
        List<JakduSchedule> schedules = jakduScheduleRepository.findByTimeUpOrderByDateAsc(false);

        for (JakduSchedule jakduSchedule : schedules) {
            Jakdu jakdu = new Jakdu();
            jakdu.setSchedule(jakduSchedule);
            jakdu.setWriter(writer);
            jakdus.add(jakdu);

            fcIds.add(new ObjectId(jakduSchedule.getHome().getId()));
            fcIds.add(new ObjectId(jakduSchedule.getAway().getId()));
            if (jakduSchedule.getCompetition() != null)
                competitionIds.add(new ObjectId(jakduSchedule.getCompetition().getId()));
        }

        Map<String, LocalName> fcNames = new HashMap<>();
        Map<String, LocalName> competitionNames = new HashMap<>();

        List<FootballClub> footballClubs = jakdukDAO.getFootballClubList(new ArrayList<>(fcIds), language, CommonConst.NAME_TYPE.fullName);
        List<Competition> competitions = jakdukDAO.getCompetitionList(new ArrayList<>(competitionIds), language);

        for (FootballClub fc : footballClubs) {
            fcNames.put(fc.getOrigin().getId(), fc.getNames().get(0));
        }

        for (Competition competition : competitions) {
            competitionNames.put(competition.getId(), competition.getNames().get(0));
        }

        result.put("dateTimeFormat", commonService.getDateTimeFormat(locale));
        result.put("jakdus", jakdus);
        result.put("fcNames", fcNames);
        result.put("competitionNames", competitionNames);

        return result;
    }

    public void getView(Model model, String id) {

        model.addAttribute("id", id);
    }

    /**
     * 작두 타기 입력
     * @param locale
     * @param myJakdu
     */
    public Jakdu setMyJakdu(Locale locale, MyJakduRequest myJakdu) {
        CommonPrincipal principal = userService.getCommonPrincipal();
        String accountId = principal.getId();
        String username = principal.getUsername();
        String type = principal.getType();

        if (accountId == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages.common", locale);
            throw new AccessDeniedException(bundle.getString("common.msg.access.denied.exception"));
        }

        CommonWriter writer = new CommonWriter();
        writer.setUserId(accountId);
        writer.setUsername(username);
        writer.setType(type);

        JakduSchedule jakduSchedule = jakduScheduleRepository.findOne(myJakdu.getJakduScheduleId());

        if (Objects.isNull(jakduSchedule)) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages.jakdu", locale);
            throw new NoSuchElementException(bundle.getString("jakdu.msg.not.found.jakdu.schedule.exception"));
        }

        Jakdu existJakdu = jakduRepository.findByScheduleAndWriter(jakduSchedule, writer);

        if (!Objects.isNull(existJakdu)) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages.jakdu", locale);
            throw new RepositoryExistException(bundle.getString("jakdu.msg.already.join.jakdu.exception"));
        }

        Jakdu jakdu = new Jakdu();
        jakdu.setSchedule(jakduSchedule);
        jakdu.setWriter(writer);

        jakdu.setHomeScore(myJakdu.getHomeScore());
        jakdu.setAwayScore(myJakdu.getAwayScore());

        jakduRepository.save(jakdu);

        return jakdu;
    }
}

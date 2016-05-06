package com.jakduk.restcontroller;

import com.jakduk.exception.SuccessButNoContentException;
import com.jakduk.model.db.Encyclopedia;
import com.jakduk.service.CommonService;
import com.jakduk.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by pyohwan on 16. 3. 20.
 */

@RestController
@RequestMapping("/api")
public class HomeRestController {

    @Resource
    LocaleResolver localeResolver;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HomeService homeService;

    @RequestMapping(value = "/home/encyclopedia", method = RequestMethod.GET)
    public Encyclopedia getEncyclopedia(HttpServletRequest request,
                            @RequestParam(required = false) String lang) {

        Locale locale = localeResolver.resolveLocale(request);
        String language = commonService.getLanguageCode(locale, lang);

        Encyclopedia encyclopedia = homeService.getEncyclopedia(language);

        if (Objects.isNull(encyclopedia))
            throw new SuccessButNoContentException(commonService.getResourceBundleMessage(locale, "messages.common", "common.exception.no.such.element"));

        return encyclopedia;
    }
}

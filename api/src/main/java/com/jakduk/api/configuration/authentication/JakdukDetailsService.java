package com.jakduk.api.configuration.authentication;

import com.jakduk.api.common.util.UserUtils;
import com.jakduk.api.configuration.authentication.user.JakdukUserDetails;
import com.jakduk.core.common.CoreConst;
import com.jakduk.core.common.util.CoreUtils;
import com.jakduk.core.exception.ServiceError;
import com.jakduk.core.exception.ServiceException;
import com.jakduk.core.model.db.User;
import com.jakduk.core.model.db.UserPicture;
import com.jakduk.core.model.embedded.UserPictureInfo;
import com.jakduk.core.repository.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

@Slf4j
@Component
public class JakdukDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Resource
	private UserUtils userUtils;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		if (ObjectUtils.isEmpty(email)) {
			throw new IllegalArgumentException("email 은 꼭 필요한 값입니다.");
		} else {
			User user = userRepository.findOneByEmail(email)
					.orElseThrow(() -> new ServiceException(ServiceError.NOT_FOUND_ACCOUNT,
							CoreUtils.getExceptionMessage("exception.not.found.jakduk.account", email)));

			if (! user.getProviderId().equals(CoreConst.ACCOUNT_TYPE.JAKDUK))
				throw new ServiceException(ServiceError.NOT_FOUND_ACCOUNT,
						CoreUtils.getExceptionMessage("exception.not.jakduk.user", email, user.getProviderId()));

			JakdukUserDetails jakdukUserDetails = new JakdukUserDetails(user.getEmail(), user.getId(),
					user.getPassword(), user.getUsername(), CoreConst.ACCOUNT_TYPE.JAKDUK,
					true, true, true, true, UserUtils.getAuthorities(user.getRoles()));

			UserPicture userPicture = user.getUserPicture();

			if (! ObjectUtils.isEmpty(userPicture)) {
				UserPictureInfo userPictureInfo = new UserPictureInfo(userPicture,
						userUtils.generateUserPictureUrl(CoreConst.IMAGE_SIZE_TYPE.SMALL, userPicture.getId()),
						userUtils.generateUserPictureUrl(CoreConst.IMAGE_SIZE_TYPE.LARGE, userPicture.getId()));

				jakdukUserDetails.setPicture(userPictureInfo);
			}

			log.info("load Jakduk username=" + jakdukUserDetails.getUsername());

			return jakdukUserDetails;
		}
	}

}

package com.jakduk.core.repository.user;

import com.jakduk.core.common.CommonConst;
import com.jakduk.core.model.db.User;
import com.jakduk.core.model.simple.OAuthProfile;
import com.jakduk.core.model.simple.SocialUserOnAuthentication;
import com.jakduk.core.model.simple.UserOnAuthentication;
import com.jakduk.core.model.simple.UserOnPasswordUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
	
	Optional<User> findOneById(String id);
	User findByUsername(String username);
	User findOneByUsername(String username);
	User findByEmail(String email);
	User findOneByEmail(String email);

	UserOnAuthentication findOneByEmailAndProviderId(String email, CommonConst.ACCOUNT_TYPE providerId);
	
	@Query(value="{'id' : ?0}", fields="{'username' : 1}")
	User writerFindById(String id);

	@Query(value="{'id' : ?0}")
	UserOnPasswordUpdate findUserOnPasswordUpdateById(String id);

	// SNS 계정으로 가입한 회원 찾기(로그인).
	User findOneByProviderIdAndProviderUserId(CommonConst.ACCOUNT_TYPE providerId, String providerUserId);

	@Query(value="{'email' : ?0}")
	SocialUserOnAuthentication findSocialUserByEmail(String email);

	@Query(value="{'socialInfo.providerId' : ?0, 'socialInfo.oauthId' : ?1}")
	SocialUserOnAuthentication findByOauthUser(CommonConst.ACCOUNT_TYPE providerId, String oauthId);
	
	@Query(value="{'socialInfo.oauthId' : {$ne : ?0}, 'username' : ?1}", fields="{'id' : 1, 'username' : 1, 'socialInfo' : 1}")
	OAuthProfile userFindByNEOauthIdAndUsername(String oauthId, String username);
	
	@Query(value="{'socialInfo.providerId' : ?0, 'socialInfo.oauthId' : ?1}")
	OAuthProfile userfindByOauthUser(CommonConst.ACCOUNT_TYPE providerId, String oauthId);
	
	@Query(value="{'email' : ?0}")
	UserOnAuthentication findAuthUserByEmail(String email);

	@Query(value="{'providerUserId' : ?0}")
	UserOnAuthentication userFindByProviderUserId(String providerUserId);
}
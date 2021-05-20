/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package org.springblade.auth.granter;

import com.winkey.hyproca.king.manager.api.IManagerApi;
import com.winkey.hyproca.pcadminservice.api.MiniProgramMemberApi;
import org.springblade.core.redis.cache.BladeRedis;
import org.springblade.core.social.props.SocialProperties;
import org.springblade.system.user.feign.IUserClient;
import org.springblade.system.user.feign.ThirdSystemApi;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 自定义拓展TokenGranter
 *
 * @author Chill
 */
public class BladeTokenGranter {

	/**
	 * 自定义tokenGranter
	 */
	public static TokenGranter getTokenGranter(final AuthenticationManager authenticationManager,
											   final AuthorizationServerEndpointsConfigurer endpoints,
											   BladeRedis bladeRedis,
											   IUserClient userClient,
											   SocialProperties socialProperties,
											   MiniProgramMemberApi miniProgramMemberApi,
											   IManagerApi managerApi,
											   com.winkey.neolac.pcadminservice.api.MiniProgramMemberApi neolacMiniProgramMemberApi,
											   ThirdSystemApi thirdSystemApi) {
		// 默认tokenGranter集合
		List<TokenGranter> granters = new ArrayList<>(Collections.singletonList(endpoints.getTokenGranter()));
		// 增加验证码模式
		granters.add(new CaptchaTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), bladeRedis));
		// 增加第三方登陆模式
		granters.add(new SocialTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), userClient, socialProperties));
		// 小程序会员编码授权模式
		granters.add(new TencentMiniProgramOpenIdTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), miniProgramMemberApi));
		granters.add(new TencentMiniProgramMemberNumberTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), miniProgramMemberApi, managerApi, neolacMiniProgramMemberApi));
		granters.add(new ThirdSystemTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory(), thirdSystemApi));
		// 组合tokenGranter集合
		return new CompositeTokenGranter(granters);
	}

}

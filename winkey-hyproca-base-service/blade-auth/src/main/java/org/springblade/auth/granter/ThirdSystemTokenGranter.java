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

import com.winkey.hyproca.pcadminservice.api.MiniProgramMemberApi;
import com.winkey.hyproca.pcadminservice.vo.MiniProgramMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springblade.auth.service.BladeUserDetails;
import org.springblade.auth.utils.TokenUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.WebUtil;
import org.springblade.system.user.entity.ThirdSystem;
import org.springblade.system.user.feign.ThirdSystemApi;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 第三方授权
 *
 * @author ggz
 */
@Slf4j
public class ThirdSystemTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "third_system";
	private static final String DEFAULT_ROLE = "ADMIN";

	private ThirdSystemApi thirdSystemApi;

	protected ThirdSystemTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, ThirdSystemApi thirdSystemApi){
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.thirdSystemApi = thirdSystemApi;
	}

	@Override
	protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
		// 请求头租户信息
		HttpServletRequest request = WebUtil.getRequest();
		String tenantId = request.getHeader(TokenUtil.TENANT_HEADER_KEY);

		if(StringUtils.isEmpty(tenantId)){
			throw new InvalidGrantException("请求头租户id不能为空!");
		}

		Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
		// 开放平台来源
		String systemId = parameters.get("systemId");
		String systemSecret = parameters.get("systemSecret");
		if(StringUtils.isEmpty(systemId) || StringUtils.isEmpty(systemSecret)){
			throw new InvalidGrantException("第三方系统id或者密钥不能为空!");
		}

		R<ThirdSystem> r = thirdSystemApi.getThirdSystemById(systemId, tenantId);
		log.debug("第三方系统授权接口响应数据为: {}", r);
		if(!r.isSuccess() || Objects.isNull(r.getData())){
			log.error(">>> 第三方系统授权接口响应数据为: {}", r);
			throw new InvalidGrantException("第三方系统授权失败!");
		}

		ThirdSystem thirdSystem = r.getData();

		// 密钥判断
		if(!systemSecret.equals(r.getData().getSystemSecretOrigin())){
			throw new InvalidGrantException("第三方系统密钥错误!");
		}

		BladeUserDetails bladeUserDetails = new BladeUserDetails(thirdSystem.getId(),
				tenantId, thirdSystem.getSystemId(),
				thirdSystem.getSystemId(),
				thirdSystem.getSystemId(),
				org.apache.commons.lang3.StringUtils.EMPTY,
				org.apache.commons.lang3.StringUtils.EMPTY,
				org.apache.commons.lang3.StringUtils.EMPTY,
				org.apache.commons.lang3.StringUtils.EMPTY,
				org.apache.commons.lang3.StringUtils.EMPTY,
				thirdSystem.getSystemId(),
				systemSecret,
				Kv.create(),
				true, true, true, true,
				AuthorityUtils.commaSeparatedStringToAuthorityList(DEFAULT_ROLE));


		// 组装认证数据，关闭密码校验
		Authentication userAuth = new UsernamePasswordAuthenticationToken(bladeUserDetails, null, bladeUserDetails.getAuthorities());
		((AbstractAuthenticationToken) userAuth).setDetails(parameters);
		OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);

		// 返回 OAuth2Authentication
		return new OAuth2Authentication(storedOAuth2Request, userAuth);
	}

}

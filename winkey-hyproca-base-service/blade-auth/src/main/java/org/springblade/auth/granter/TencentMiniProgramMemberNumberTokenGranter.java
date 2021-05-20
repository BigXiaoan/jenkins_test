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
import com.winkey.hyproca.king.manager.vo.ManagerVO;
import com.winkey.hyproca.pcadminservice.api.MiniProgramMemberApi;
import com.winkey.hyproca.pcadminservice.vo.MiniProgramMemberVO;
import lombok.extern.slf4j.Slf4j;
import org.springblade.auth.service.BladeUserDetails;
import org.springblade.auth.utils.TokenUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.support.Kv;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 腾讯小程序会员编码授权
 *
 * @author ggz
 */
@Slf4j
public class TencentMiniProgramMemberNumberTokenGranter extends AbstractTokenGranter {
	private static final String GRANT_TYPE = "member_number";
	private static final String DEFAULT_ROLE = "ADMIN";

	private final MiniProgramMemberApi miniProgramMemberApi;
	private final com.winkey.neolac.pcadminservice.api.MiniProgramMemberApi neolacMiniProgramMemberApi;
	private final IManagerApi managerApi;


	protected TencentMiniProgramMemberNumberTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory,
														 MiniProgramMemberApi miniProgramMemberApi,
														 IManagerApi managerApi,
	com.winkey.neolac.pcadminservice.api.MiniProgramMemberApi neolacMiniProgramMemberApi){
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.miniProgramMemberApi = miniProgramMemberApi;
		this.managerApi = managerApi;
		this.neolacMiniProgramMemberApi = neolacMiniProgramMemberApi;
	}

	/*protected TencentMiniProgramMemberNumberTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, IManagerApi managerApi){
		super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
		this.managerApi = managerApi;
	}*/


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
		String memberNumber = parameters.get("memberNumber");
		if(StringUtils.isEmpty(memberNumber)){
			throw new InvalidGrantException("会员手机号不能为空!");
		}

		Map<String, String> authenticationData = getAuthenticationData(parameters.get("channel"), tenantId, memberNumber, parameters.get("openId"));

		BladeUserDetails bladeUserDetails = new BladeUserDetails(Long.parseLong(authenticationData.get("memberId")),
			tenantId, authenticationData.get("openId"),
			authenticationData.get("memberNumber"),
			authenticationData.get("memberNumber"),
			org.apache.commons.lang3.StringUtils.EMPTY,
			org.apache.commons.lang3.StringUtils.EMPTY,
			org.apache.commons.lang3.StringUtils.EMPTY,
			org.apache.commons.lang3.StringUtils.EMPTY,
			org.apache.commons.lang3.StringUtils.EMPTY,
			authenticationData.get("memberNumber"),
			org.apache.commons.lang3.StringUtils.EMPTY,
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

	private Map<String, String> getAuthenticationData(String authenticationChannel, String tenantId, String memberNumber, String openId){
		if(StringUtils.isEmpty(authenticationChannel)){
			return getAuthenticationData1897(tenantId, memberNumber);
		}
		// 悠蓝渠道
		if(authenticationChannel.equals("neolac")){
			return getAuthenticationDataNeolac(tenantId, memberNumber, openId);
		}
		return getAuthenticationDataKingManager(tenantId, memberNumber);
	}

	private Map<String, String> getAuthenticationDataNeolac(String tenantId, String memberNumber, String openId) {
		if(StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(memberNumber) || StringUtils.isEmpty(openId)){
			log.error(">>> 悠蓝小程序会员获取token参数异常: 租户id: {}, openId: {}, 手机号: {}", tenantId, openId, memberNumber);
			throw new InvalidGrantException("小程序会员授权失败,参数非法!");
		}
		//
		R<com.winkey.neolac.pcadminservice.vo.MiniProgramMemberVO> r = neolacMiniProgramMemberApi.getMemberInfoByMemberNumberAndTenantIdAndOpenIdByNeolac(memberNumber, tenantId, openId);
		log.debug("悠蓝小程序会员授权接口响应数据为: {}", r);
		if(!r.isSuccess() || Objects.isNull(r.getData())){
			log.error(">>> 悠蓝小程序会员数据为空: {}", r);
			throw new InvalidGrantException("小程序会员授权失败!");
		}
		com.winkey.neolac.pcadminservice.vo.MiniProgramMemberVO data = r.getData();
		Map<String, String> authenticationData = new ConcurrentHashMap<>();
		if(Objects.isNull(data.getId()) || StringUtils.isEmpty(data.getNeolacOpenId()) || StringUtils.isEmpty(data.getMemberNumber())){
			log.error(">>> 悠蓝会员数据为空: {}, 查询参数为手机号: {}, 租户id: {}, openId: {}", data, memberNumber, tenantId, openId);
			throw new InvalidGrantException("小程序会员授权失败!");
		}
		authenticationData.put("memberId", Objects.toString(data.getId()));
		authenticationData.put("openId", data.getNeolacOpenId());
		authenticationData.put("memberNumber", data.getMemberNumber());
		return authenticationData;
	}

	private Map<String, String> getAuthenticationData1897(String tenantId, String memberNumber){
		R<MiniProgramMemberVO> r = miniProgramMemberApi.getMemberByMemberNumberAndTenantId(memberNumber, tenantId);
		log.debug("小程序会员授权接口响应数据为: {}", r);
		if(!r.isSuccess() || Objects.isNull(r.getData())){
			log.error(">>> 1897小程序会员数据为空: {}", r);
			throw new InvalidGrantException("小程序会员授权失败!");
		}

		MiniProgramMemberVO miniProgramMemberVO = r.getData();
		if(Objects.isNull(miniProgramMemberVO.getId()) || StringUtils.isEmpty(miniProgramMemberVO.getOpenId()) || StringUtils.isEmpty(miniProgramMemberVO.getMemberNumber())){
			log.error(">>> 1897小程序会员数据为空: {}, 查询参数手机号为: {}, 租户id: {}", miniProgramMemberVO, memberNumber, tenantId);
			throw new InvalidGrantException("小程序会员授权失败!");
		}
		Map<String, String> authenticationData = new ConcurrentHashMap<>();
		authenticationData.put("memberId", Objects.toString(miniProgramMemberVO.getId()));
		authenticationData.put("openId", miniProgramMemberVO.getOpenId());
		authenticationData.put("memberNumber", miniProgramMemberVO.getMemberNumber());
		return authenticationData;
	}

	private Map<String, String> getAuthenticationDataKingManager(String tenantId, String phoneNumber){
		R<ManagerVO> r = managerApi.getManagerByPhoneNumberAndTenantId(phoneNumber, tenantId);
		log.debug("金管家小程序授权接口响应数据为: {}", r);
		if(!r.isSuccess() || Objects.isNull(r.getData())){
			log.error(">>> 1897小程序管家数据为空: {}", r);
			throw new InvalidGrantException("金管家服务授权失败!");
		}

		ManagerVO managerVO = r.getData();
		Map<String, String> authenticationData = new ConcurrentHashMap<>();
		authenticationData.put("memberId", Objects.toString(managerVO.getId()));
		authenticationData.put("openId", managerVO.getOpenId());
		authenticationData.put("memberNumber", managerVO.getPhoneNumber());
		return authenticationData;
	}

}

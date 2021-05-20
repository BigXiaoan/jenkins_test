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
package org.springblade.resource.feign;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springblade.resource.vo.FileInfoVO;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * ISmsClient
 *
 * @author Chill
 */
@FeignClient(
	value = AppConstant.APPLICATION_RESOURCE_NAME,
//	fallback = IOssClientFallback.class,
	configuration = IOssClient.MultipartSupportConfig.class
)
public interface IOssClient {
	String API_PREFIX = "/client";
	String PUT_FILE = API_PREFIX + "/put-file";
	/**
	 * 上传文件
	 * @param file 文件
	 * @return R
	 */
	@PostMapping(value = PUT_FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	R<FileInfoVO> putFile(@RequestPart(value = "file") MultipartFile file);

	@Configuration
	class MultipartSupportConfig {

		@Autowired
		private ObjectFactory<HttpMessageConverters> messageConverters;

		@Bean
		public Encoder feignFormEncoder () {
			return new SpringFormEncoder(new SpringEncoder(messageConverters));
		}
	}
}

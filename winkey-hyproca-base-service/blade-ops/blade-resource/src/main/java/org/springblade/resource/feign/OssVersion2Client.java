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

import lombok.SneakyThrows;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.tenant.annotation.NonDS;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.StringPool;
import org.springblade.resource.builder.oss.OssBuilder;
import org.springblade.resource.vo.FileInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * oss调用服务
 *
 * @author Chill
 */
@NonDS
@RestController
public class OssVersion2Client implements IOssVersion2Client {
	/**
	 * 对象存储构建类
	 */
	@Autowired
	private OssBuilder ossBuilder;

	@Value("${hyproca.default.tenantId:100000}")
	private String tenantId;

	@Override
	@SneakyThrows
	@PostMapping(value = PUT_FILE_VERSION2, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public R<FileInfoVO> putFileVersion2(@RequestPart(value = "file") MultipartFile file) {


		BladeFile bladeFile = ossBuilder.template(StringPool.EMPTY, tenantId).putFile(file.getOriginalFilename(), file.getInputStream());
		// 路径处理
		String domain = bladeFile.getDomain()+"/upload";
		bladeFile.setDomain(domain);

		// name
		String name = bladeFile.getName();
		String newName = name.substring(6);
		bladeFile.setName(newName);

		FileInfoVO fileInfoVO = new FileInfoVO();
		BeanUtils.copyProperties(bladeFile, fileInfoVO);

		return R.data(fileInfoVO);
	}

}

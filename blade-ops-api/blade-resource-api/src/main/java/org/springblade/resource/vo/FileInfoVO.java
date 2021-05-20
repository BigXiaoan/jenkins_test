package org.springblade.resource.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 图片信息
 * @author lxs
 */
@Data
@ApiModel("图片信息")
public class FileInfoVO {

	private String link;
	private String domain;
	private String name;
	private String originalName;
	private Long attachId;

}

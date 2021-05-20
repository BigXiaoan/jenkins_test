package org.springblade.resource.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.InputStream;

/**
 * 上传文件指定桶、文件名、文件
 * @author lxs
 */
@Data
@ApiModel("上传文件指定桶、文件名、文件")
public class FileInfo {

	@ApiModelProperty("文件流")
	private InputStream inputStream;

	@ApiModelProperty("文件名")
	private String fileName;

	@ApiModelProperty("存放桶名")
	private String bucketName;

}

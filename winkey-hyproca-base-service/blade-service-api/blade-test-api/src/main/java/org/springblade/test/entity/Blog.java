package org.springblade.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springblade.core.mp.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("blade_blog")
public class Blog extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键
	 */
	@TableId(value = "id",type = IdType.ID_WORKER)
	private Long id;
	/**
	 * 标题
	 */
	private String blogTitle;
	/**
	 * 内容
	 */
	private String blogContent;
	/**
	 * 时间
	 */
	private Date blogDate;
	/**
	 * 是否已删除
	 */
	@TableLogic
	private Integer isDeleted;

}

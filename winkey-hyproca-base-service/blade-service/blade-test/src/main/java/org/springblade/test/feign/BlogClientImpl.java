package org.springblade.test.feign;


import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springblade.test.entity.Blog;
import org.springblade.test.service.BlogService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
public class BlogClientImpl implements BlogClient {

	private BlogService blogService;

	@Override
	public R<Blog> detail(Integer id) {
		return R.data(blogService.getById(id));
	}
}

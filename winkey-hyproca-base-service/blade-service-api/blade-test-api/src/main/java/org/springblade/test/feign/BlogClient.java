package org.springblade.test.feign;

import org.springblade.core.tool.api.R;
import org.springblade.test.entity.Blog;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
	value = "blade-test",
	//定义hystrix配置类
	fallback = BlogClientFallback.class

)
public interface BlogClient {

	/**
	 * 接口前缀
	 */
	String API_PREFIX = "/api/blog";


	@GetMapping(API_PREFIX+"/detail")
	R<Blog> detail(@RequestParam("id") Integer id );



}

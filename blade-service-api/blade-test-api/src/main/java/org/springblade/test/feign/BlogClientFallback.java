package org.springblade.test.feign;

import org.springblade.core.tool.api.R;
import org.springblade.test.entity.Blog;
import org.springframework.stereotype.Component;


import java.util.Date;

@Component
public class BlogClientFallback implements BlogClient{

	@Override
	public R<Blog> detail(Integer id) {
		Blog blog = new Blog();
		blog.setBlogTitle("Hystrix");
		blog.setBlogContent("FallBack Success");
		blog.setBlogDate(new Date());
		blog.setIsDeleted(0);
		return R.data(blog);

	}
}

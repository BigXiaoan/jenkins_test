package org.springblade.test.service.impl;


import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.test.mapper.BlogMapper;
import org.springblade.test.entity.Blog;
import org.springblade.test.service.BlogService;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends BaseServiceImpl<BlogMapper, Blog> implements BlogService {
}

package org.springblade.test.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.test.entity.Blog;
import org.springblade.test.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api")
@AllArgsConstructor
public class LuaController {

	@Autowired
	private BlogService service;


	@GetMapping("/info")
	@Cacheable(cacheNames = "test-info",key = "#name")
	public R<String> info(String name){

		log.info("本条消息未从缓存中获取");
		// 返回规范数据
		return R.data(name);
	}

	@GetMapping("removeinfo")
	@CacheEvict(cacheNames = "test-info",key = "#name")
	public R<String> removeInfo(String name){
		//log.info("从缓存中删除数据成功");
		return R.success("删除成功");
	}


	@PostMapping("/save")
	 public R save(@RequestBody Blog blog){
		boolean flag = service.save(blog);
		System.out.println(flag);
		return R.status(service.save(blog));
	}
}

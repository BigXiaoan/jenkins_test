package org.springblade.test.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.springblade.**.mapper.**")
public class MybatisPlusConfiguration {
}

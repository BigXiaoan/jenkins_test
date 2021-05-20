package org.springblade.test.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BladeClientConfiguration {

	@Bean
	public BladeClientConfiguration bladeClientConfiguration (){
		return  new BladeClientConfiguration();
	}
}

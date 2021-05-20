package org.springblade.system.user.feign;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.ThirdSystem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author ggz
 */
@FeignClient(value = AppConstant.APPLICATION_USER_NAME, path = "third-system")
public interface ThirdSystemApi {

	@GetMapping
	R<ThirdSystem> getThirdSystemById(@RequestParam("systemId") String systemId, @RequestParam("tenantId") String tenantId);
}

package org.springblade.system.feign;

import org.springblade.core.launch.constant.AppConstant;
import org.springblade.core.tool.api.R;
import org.springblade.system.entity.Region;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 系统服务 - 行政区服务
 * @author ggz
 */
@FeignClient(name = AppConstant.APPLICATION_SYSTEM_NAME,
	path = RegionApi.WINKEY_DEFINE_REGION_PREFIX,
	fallback = RegionApiFallback.class
)
public interface RegionApi {
	String WINKEY_DEFINE_REGION_PREFIX = "winkey_region";

	/**
	 * 根据父级编号查询子级列表
	 *
	 * @param parentCode
	 * @return
	 */
	@GetMapping("/region-by-parent-code")
	R<List<Region>> getRegionByParentCode(@RequestParam("parentCode") String parentCode);

}

package org.springblade.system.feign;

import org.springblade.core.tool.api.R;
import org.springblade.system.entity.Region;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 系统服务 - 行政区服务
 * @author ggz
 */
@Component
public class RegionApiFallback implements RegionApi {

	@Override
	public R<List<Region>> getRegionByParentCode(@RequestParam("parentCode") String parentCode){
		return R.fail("获取行政数据失败");
	}

}

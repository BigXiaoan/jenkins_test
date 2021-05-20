package org.springblade.system.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.tool.api.R;
import org.springblade.system.entity.Region;
import org.springblade.system.feign.RegionApi;
import org.springblade.system.service.IRegionService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ggz
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping(RegionApi.WINKEY_DEFINE_REGION_PREFIX)
public class WinkeyRegionController implements RegionApi {
	private final IRegionService regionService;
	@Override
	public R<List<Region>> getRegionByParentCode(String parentCode) {
		return R.data(regionService.list(Wrappers.<Region>lambdaQuery().eq(Region::getParentCode, parentCode)));
	}
}

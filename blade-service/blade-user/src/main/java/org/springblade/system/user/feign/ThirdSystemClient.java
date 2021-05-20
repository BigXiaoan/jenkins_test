package org.springblade.system.user.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import org.springblade.core.tenant.mp.TenantEntity;
import org.springblade.core.tool.api.R;
import org.springblade.system.user.entity.ThirdSystem;
import org.springblade.system.user.service.IThirdSystemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ggz
 */
@RestController
@RequestMapping("third-system")
@AllArgsConstructor
public class ThirdSystemClient implements ThirdSystemApi {
	private final IThirdSystemService thirdSystemService;

	@Override
	public R<ThirdSystem> getThirdSystemById(String systemId, String tenantId) {
		return R.data(thirdSystemService.getOne(Wrappers.<ThirdSystem>lambdaQuery()
			.eq(TenantEntity::getTenantId, tenantId)
			.eq(ThirdSystem::getSystemId, systemId)));
	}
}

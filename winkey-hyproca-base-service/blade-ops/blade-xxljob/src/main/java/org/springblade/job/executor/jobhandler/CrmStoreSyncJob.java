package org.springblade.job.executor.jobhandler;

import com.winkey.hyproca.activity.api.SeckillActivityApi;
import com.winkey.hyproca.crm.api.CrmBaseDataApi;
import com.winkey.hyproca.crm.dto.StoreRequestDTO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、在Spring Bean实例中，开发Job方法，方式格式要求为 "public ReturnT<String> execute(String param)"
 * 2、为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli
 */
@Component
@AllArgsConstructor
public class CrmStoreSyncJob {
	private final CrmBaseDataApi crmBaseDataApi;

	/**
	 * 门店同步
	 */
	@XxlJob("crmStoreSync")
	public ReturnT<String> crmStoreSync(int page, int pageSize) throws Exception {
		XxlJobLogger.log("门店同步开始....");
		StoreRequestDTO storeRequestDTO = new StoreRequestDTO();
		storeRequestDTO.setPage(page);
		storeRequestDTO.setPageSize(pageSize);
		LocalDateTime endSyncTime = LocalDateTime.now();
		LocalDateTime startSyncTime = endSyncTime.minusDays(1);
		storeRequestDTO.setStartTime(startSyncTime);
		storeRequestDTO.setEndTime(endSyncTime);
		XxlJobLogger.log("门店同步参数为: {}", storeRequestDTO);

		crmBaseDataApi.updateStoreInfo(storeRequestDTO);
		XxlJobLogger.log("门店同步结束....");
		return ReturnT.SUCCESS;
	}
}

package org.springblade.job.executor.jobhandler;

import com.winkey.hyproca.activity.api.SeckillActivityApi;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.core.util.ShardingUtil;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

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
public class ActivityStatusXxlJob {
	private final SeckillActivityApi seckillActivityApi;

	/**
	 * 开始
	 */
	@XxlJob("activityStatusStart")
	public ReturnT<String> activityStatusStart() throws Exception {
		XxlJobLogger.log("活动开始状态监控, 开始执行活动状态更新任务....");
		seckillActivityApi.activityAutoStart();
		XxlJobLogger.log("活动开始状态监控, 结束执行活动状态更新任务....");
		return ReturnT.SUCCESS;
	}

	/**
	 * 结束
	 * @return
	 * @throws Exception
	 */
	@XxlJob("activityStatusEnd")
	public ReturnT<String> activityStatusEnd() throws Exception {
		XxlJobLogger.log("活动结束状态监控, 开始执行活动状态更新任务....");
		seckillActivityApi.activityAutoEnd();
		XxlJobLogger.log("活动结束状态监控, 结束执行活动状态更新任务....");
		return ReturnT.SUCCESS;
	}
}

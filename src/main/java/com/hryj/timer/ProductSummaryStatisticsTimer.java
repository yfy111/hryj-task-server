package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.ProductSummaryStatisticsService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 王光银
 * @className: BackupJobsTimer
 * @description: 任务定时器-商品销量统计
 * @create 2018/7/10 13:50
 **/
@Slf4j
@Component
public class ProductSummaryStatisticsTimer {

    @Autowired
    private ProductSummaryStatisticsService productSummaryStatisticsService;

    /**
     * @author 王光银
     * @methodName: productSummaryStatictics
     * @methodDesc: 每天凌晨30分的时候统计前一天的商品销量数据
     * @description:
     * @param: [day_date]
     * @return void
     * @create 2018-08-01 19:00
     **/
    @Scheduled(cron = "0 30 0 * * ?")
    public void productSummaryStatictics(){
        log.info("每天统计商品销量开始:" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        try {
            productSummaryStatisticsService.productSalesStatistics(null, null);
        }catch (Exception e){
            log.error("每天统计商品销量失败：" + e.getMessage());
            log.error("程序异常" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("每天统计商品销量",ExceptionUtil.stacktraceToString(e));
        }
        log.info("每天统计商品销量结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}

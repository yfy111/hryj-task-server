package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.GrossProfitStatisService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 李道云
 * @className: GrossProfitJobsTimer
 * @description: 任务定时器-统计部门毛利
 * @create 2018/8/16 17:19
 **/
@Slf4j
@Component
public class GrossProfitJobsTimer {

    @Autowired
    private GrossProfitStatisService grossProfitStatisService;

    /**
     * @author 李道云
     * @methodName: statisDeptGrossProfit
     * @methodDesc: 统计部门毛利，每5分钟执行一次
     * @description:
     * @param: []
     * @return void
     * @create 2018-08-16 17:21
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void statisDeptGrossProfit(){
        log.info("统计部门毛利开始：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
        String statis_month = DateUtil.format(DateUtil.date(),"yyyy-MM");
        try {
            grossProfitStatisService.statisDeptGrossProfit(statis_month);
        } catch (Exception e) {
            log.error("统计部门毛利,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("统计部门毛利",ExceptionUtil.stacktraceToString(e));
        }
        log.info("统计部门毛利结束：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: statisDeptGrossProfit
     * @methodDesc: 统计部门毛利，每月计算一次最终的数据
     * @description: 每月1日0:10分执行
     * @param: []
     * @return void
     * @create 2018-08-16 17:21
     **/
    @Scheduled(cron = "0 10 0 1 * ?")
    public void statisMonthDeptGrossProfit(){
        log.info("统计部门毛利开始：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
        String statis_month = DateUtil.format(DateUtil.lastMonth(),"yyyy-MM");
        try {
            grossProfitStatisService.statisDeptGrossProfit(statis_month);
        } catch (Exception e) {
            log.error("统计部门毛利,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("统计部门毛利",ExceptionUtil.stacktraceToString(e));
        }
        log.info("统计部门毛利结束：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}

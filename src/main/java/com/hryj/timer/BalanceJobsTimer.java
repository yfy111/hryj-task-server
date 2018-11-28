package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.BalanceCalculateService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 李道云
 * @className: BalanceJobsTimer
 * @description: 任务定时器-区域公司、门店、员工结算
 * @create 2018/7/17 21:01
 **/
@Slf4j
@Component
public class BalanceJobsTimer {

    @Autowired
    private BalanceCalculateService balanceCalculateService;

    /**
     * @author 李道云
     * @methodName: calDeptBalance
     * @methodDesc: 计算区域公司、门店、员工结算
     * @description: 按月结算,每月1日1点开始
     * @param: []
     * @return void
     * @create 2018-07-17 21:22
     **/
    @Scheduled(cron = "0 0 1 1 * ?")
    //@Scheduled(cron = "0 30 17 * * ?")
    public void calDeptBalance(){
        log.info("计算区域公司、门店、员工结算开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String balance_month = DateUtil.format(DateUtil.lastMonth(),"yyyy-MM");
        //String balance_month = "2018-06";
        try {
            balanceCalculateService.calDeptBalance(balance_month);
        } catch (Exception e) {
            log.error("计算区域公司、门店、员工结算,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("计算区域公司、门店、员工结算",ExceptionUtil.stacktraceToString(e));
        }
        log.info("计算区域公司、门店、员工结算结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: calWHBalance
     * @methodDesc: 计算仓库结算
     * @description: 按月结算,每月1日1点开始
     * @param: []
     * @return void
     * @create 2018-07-23 21:47
     **/
    @Scheduled(cron = "0 0 1 1 * ?")
    //@Scheduled(cron = "0 30 17 * * ?")
    public void calWHBalance(){
        log.info("计算仓库结算开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String balance_month = DateUtil.format(DateUtil.lastMonth(),"yyyy-MM");
        //String balance_month = "2018-06";
        try {
            balanceCalculateService.calWHBalance(balance_month);
        } catch (Exception e) {
            log.error("计算仓库结算,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("计算仓库结算",ExceptionUtil.stacktraceToString(e));
        }
        log.info("计算仓库结算结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}

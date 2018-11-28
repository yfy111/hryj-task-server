package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.CostCalculateService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 李道云
 * @className: CostCalJobsTimer
 * @description: 任务定时器-成本计算
 * @create 2018/7/17 17:36
 **/
@Slf4j
@Component
public class CostCalJobsTimer {

    @Autowired
    private CostCalculateService costCalculateService;

    /**
     * @author 李道云
     * @methodName: calStaffDaySalary
     * @methodDesc: 计算员工的日工资
     * @description: 每天0点30分钟执行
     * @param: []
     * @return void
     * @create 2018-07-17 19:30
     **/
    @Scheduled(cron = "0 30 0 * * ?")
    //@Scheduled(cron = "0 25 17 * * ?")
    public void calStaffDaySalary(){
        log.info("员工日工资计算开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String cal_date = DateUtil.format(DateUtil.offsetDay(new Date(),-1),DatePattern.NORM_DATE_PATTERN);
        //String start_date = "2018-06-01";
        //for (int i=0; i<30; i++) {
        //String cal_date = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(start_date, DatePattern.NORM_DATE_PATTERN), i));
            try {
                costCalculateService.calStaffDaySalary(cal_date);
            } catch (Exception e) {
                log.error("员工日工资计算,失败：" + e.getMessage());
                log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
                SendEmail.sendEmail("员工日工资计算",ExceptionUtil.stacktraceToString(e));
            }
        //}
        log.info("员工日工资计算结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}

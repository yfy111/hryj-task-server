package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.BackupDataService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 李道云
 * @className: BackupJobsTimer
 * @description: 任务定时器-备份数据
 * @create 2018/7/10 13:50
 **/
@Slf4j
@Component
public class BackupJobsTimer {

    @Autowired
    private BackupDataService backupDataService;

    /**
     * @author 李道云
     * @methodName: backupDataByMonth
     * @methodDesc: 按月备份数据,每月1日0点1分开始
     * @description: 1-备份部门成本数据表，2-备份部门分润配置表
     * @param: []
     * @return void
     * @create 2018-07-11 22:20
     **/
    @Scheduled(cron = "0 1 0 1 * ?")
    //@Scheduled(cron = "0 20 18 * * ?")
    public void backupDataByMonth(){
        log.info("按月备份数据开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String backup_month = DateUtil.format(DateUtil.lastMonth(),"yyyy-MM");
        //String backup_month = "2018-06";
        try {
            backupDataService.backupDeptCostData(backup_month);
            backupDataService.backupDeptShareConfig(backup_month);
        }catch (Exception e){
            log.error("按月备份数据,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
        }
        log.info("按月备份数据结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: backupDataByDay
     * @methodDesc: 按天备份数据,每天0点1分开始
     * @description: 1-备份部门组织表，2-备份员工部门组织关系表
     * @param: []
     * @return void
     * @create 2018-07-11 22:20
     **/
    @Scheduled(cron = "0 1 0 * * ?")
    //@Scheduled(cron = "0 20 18 * * ?")
    public void backupDataByDay(){
        log.info("按天备份数据开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String backup_date = DateUtil.format(DateUtil.offsetDay(new Date(),-1),DatePattern.NORM_DATE_PATTERN);
        //String start_date = "2018-06-01";
        //for (int i=0; i<30; i++){
        //String backup_date = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(start_date,DatePattern.NORM_DATE_PATTERN),i));
            try {
                backupDataService.backupDeptGroup(backup_date);
                backupDataService.backupStaffDeptRelation(backup_date);
            }catch (Exception e){
                log.error("按天备份数据,失败：" + e.getMessage());
                log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
                SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
            }
        //}
        log.info("按天备份数据结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

}

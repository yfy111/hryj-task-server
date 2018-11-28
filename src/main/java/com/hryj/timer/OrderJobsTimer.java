package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.service.OrderHandelService;
import com.hryj.service.OrderStatisService;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 李道云
 * @className: OrderJobsTimer
 * @description: 任务定时器-订单处理
 * @create 2018/7/14 14:37
 **/
@Slf4j
@Component
public class OrderJobsTimer {

    @Autowired
    private OrderHandelService orderHandelService;

    @Autowired
    private OrderStatisService orderStatisService;

    /**
     * @author 李道云
     * @methodName: handelWaitPayOrder
     * @methodDesc: 处理待支付订单，每5分钟执行一次
     * @description: 待支付订单24小时变成已取消
     * @param: []
     * @return void
     * @create 2018-07-14 14:55
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void handelWaitPayOrder(){
        log.info("处理待支付订单开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        try {
            orderHandelService.handelWaitPayOrder();
        } catch (Exception e) {
            log.error("处理待支付订单,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("处理待支付订单",ExceptionUtil.stacktraceToString(e));
        }
        log.info("处理待支付订单结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: handelCompleteOrder
     * @methodDesc: 处理已完成订单，每5分钟执行一次
     * @description: 已发货+10天
     * @param: []
     * @return void
     * @create 2018-07-14 15:24
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void handelCompleteOrder(){
        log.info("处理已完成订单开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        try {
            orderHandelService.handelCompleteOrder();
        } catch (Exception e) {
            log.error("处理已完成订单,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("处理已完成订单",ExceptionUtil.stacktraceToString(e));
        }
        log.info("处理已完成订单结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: statisTodayOrderData
     * @methodDesc: 统计仓库、门店、员工当天的订单数据
     * @description: 每隔5分钟执行一次
     * @param: []
     * @return void
     * @create 2018-07-14 15:55
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void statisTodayOrderData(){
        log.info("统计仓库、门店、员工当天的订单数据开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String statis_date = DateUtil.today();
        try {
            orderStatisService.statisWHOrderData(statis_date);
        } catch (Exception e) {
            log.error("统计仓库订单数据,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("统计仓库订单数据",ExceptionUtil.stacktraceToString(e));
        }
        try {
            orderStatisService.statisStoreOrderData(statis_date);
        } catch (Exception e) {
            log.error("统计门店订单数据,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("统计门店订单数据",ExceptionUtil.stacktraceToString(e));
        }
        try {
            orderStatisService.statisStaffOrderData(statis_date);
        } catch (Exception e) {
            log.error("统计员工订单数据,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("统计员工订单数据",ExceptionUtil.stacktraceToString(e));
        }
        log.info("统计仓库、门店、员工当天的订单数据结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

    /**
     * @author 李道云
     * @methodName: statisStaffStoreOrderData
     * @methodDesc: 统计仓库、门店、员工的订单数据
     * @description: 每天0点30分执行
     * @param: []
     * @return void
     * @create 2018-07-14 15:55
     **/
    @Scheduled(cron = "0 30 0 * * ?")
    //@Scheduled(cron = "0 25 17 * * ?")
    public void statisOrderData(){
        log.info("统计仓库、门店、员工订单数据开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        String statis_date = DateUtil.format(DateUtil.offsetDay(new Date(),-1),DatePattern.NORM_DATE_PATTERN);
        //String start_date = "2018-06-01";
        //for (int i=0; i<30; i++){
        //String statis_date = DateUtil.formatDate(DateUtil.offsetDay(DateUtil.parse(start_date,DatePattern.NORM_DATE_PATTERN),i));
            try {
                orderStatisService.statisWHOrderData(statis_date);
            } catch (Exception e) {
                log.error("统计仓库订单数据,失败：" + e.getMessage());
                log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
                SendEmail.sendEmail("统计仓库订单数据",ExceptionUtil.stacktraceToString(e));
            }
            try {
                orderStatisService.statisStoreOrderData(statis_date);
            } catch (Exception e) {
                log.error("统计门店订单数据,失败：" + e.getMessage());
                log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
                SendEmail.sendEmail("统计门店订单数据",ExceptionUtil.stacktraceToString(e));
            }
            try {
                orderStatisService.statisStaffOrderData(statis_date);
            } catch (Exception e) {
                log.error("统计员工订单数据,失败：" + e.getMessage());
                log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
                SendEmail.sendEmail("统计员工订单数据",ExceptionUtil.stacktraceToString(e));
            }
        //}
        log.info("统计仓库、门店、员工订单数据结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }

}

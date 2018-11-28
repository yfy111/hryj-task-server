package com.hryj.timer;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.feign.OrderFeignClient;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author 罗秋涵
 * @className: CrossBorderOrderJobsTimer
 * @description: 第三方订单定时任务
 * @create 2018/9/19 0019 14:48
 **/
@Slf4j
@Component
public class CrossBorderOrderJobsTimer {

    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * @author 罗秋涵
     * @description: 光彩订单同步任务，每五分钟执行一次
     * @param: []
     * @return void
     * @create 2018-09-19 14:50
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void gcSynchronizationOrder(){
        try {
            orderFeignClient.synchronizationOrder();
        }catch (Exception e){
            log.error("订单同步,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("订单同步",ExceptionUtil.stacktraceToString(e));
        }
    }

    /**
     * @author 罗秋涵
     * @description: 轮询光彩接口查询订单，每五分钟执行一次
     * @param: []
     * @return void
     * @create 2018-09-19 15:07
     **/
    @Scheduled(cron = "0 0/5 * * * ?")
    public void findOrderForGCStatus(){
        try {
            orderFeignClient.findOrderForGCStatus();
        }catch (Exception e){
            log.error("轮询光彩接口查询订单,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("轮询光彩接口查询订单",ExceptionUtil.stacktraceToString(e));
        }
    }
}

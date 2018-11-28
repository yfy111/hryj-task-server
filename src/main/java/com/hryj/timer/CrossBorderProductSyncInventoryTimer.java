package com.hryj.timer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.feign.ProductFeignClient;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author 汪豪
 * @className: CrossBorderProductSyncInventoryTimer
 * @description:
 * @create 2018/9/18 0018 16:41
 **/
@Slf4j
@Component
public class CrossBorderProductSyncInventoryTimer {

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @return void
     * @author 汪豪
     * @methodName: syncInventory
     * @methodDesc: 每天凌晨4点同步跨境商品库存
     * @description:
     * @param: []
     * @create 2018-09-19 9:43
     **/
    @Scheduled(cron = "0 0 4 * * ?")
    public void syncInventory() {
        log.info("同步跨境商品库存开始：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        try {
            productFeignClient.syncCrossBorderProductInventory();
        }catch (Exception e){
            log.error("同步跨境商品库存失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
        }
        log.info("同步跨境商品库存结束：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
    }
}

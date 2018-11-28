package com.hryj.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 罗秋涵
 * @className: OrderFeignClient
 * @description: 订单服务接口
 * @create 2018/9/19 0019 14:51
 **/
@FeignClient(name = "order-server")
public interface OrderFeignClient {

    /**
     * @author 罗秋涵
     * @description: 订单同步
     * @param: []
     * @return void
     * @create 2018-09-19 14:55
     **/
    @RequestMapping(value = "/orderForThirdParty/synchronizationOrder", method = RequestMethod.POST)
    void synchronizationOrder();

    /**
     * @author 罗秋涵
     * @description: 轮询光彩接口查询订单
     * @param: []
     * @return void
     * @create 2018-09-19 14:55
     **/
    @RequestMapping(value = "/orderForThirdParty/findOrderForGCStatus", method = RequestMethod.POST)
    void findOrderForGCStatus();
}

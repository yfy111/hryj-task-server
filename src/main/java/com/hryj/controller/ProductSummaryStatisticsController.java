package com.hryj.controller;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.service.ProductSummaryStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductSummaryStatisticsController
 * @description: 商品销量统计
 * @create 2018/7/19 22:36
 **/
@Slf4j
@RestController
@RequestMapping("/productStatistics")
public class ProductSummaryStatisticsController {

    @Autowired
    private ProductSummaryStatisticsService productSummaryStatisticsService;


    @PostMapping("/statisticsProductSalesData")
    public Result statisticsProductSalesData(@RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date) throws RuntimeException {
        try {
            productSummaryStatisticsService.productSalesStatistics(start_date, end_date);
        } catch (RuntimeException e) {
            return new Result(CodeEnum.FAIL_SERVER, "商品销量统计失败:" + e.getMessage());
        } catch (Exception e) {
            return new Result(CodeEnum.FAIL_SERVER, "商品销量统计失败:" + e.getMessage());
        }
        return new Result(CodeEnum.SUCCESS);
    }

}

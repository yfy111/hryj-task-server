package com.hryj.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.hryj.common.Result;
import com.hryj.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author 李道云
 * @className: ProfitCalculateController
 * @description: 分润计算控制器
 * @create 2018/7/19 22:36
 **/
@Slf4j
@RestController
@RequestMapping("/profit")
public class ProfitCalculateController {

    @Autowired
    private BackupDataService backupDataService;

    @Autowired
    private ProfitCalculateService profitCalculateService;

    @Autowired
    private BalanceCalculateService balanceCalculateService;

    @Autowired
    private CostCalculateService costCalculateService;

    @Autowired
    private OrderStatisService orderStatisService;

    @Autowired
    private GrossProfitStatisService grossProfitStatisService;

    /**
     * @author 李道云
     * @methodName: calculateProfit
     * @methodDesc: 最后一步计算分润
     * @description:
     * @param: [summary_id,operator_id]
     * @return javax.xml.transform.Result
     * @create 2018-07-19 22:38
     **/
    @PostMapping("/calculateProfit")
    public Result calculateProfit(@RequestParam("summary_id") Long summary_id, @RequestParam("operator_id") Long operator_id)throws Exception{
        log.info("最后一步计算分润开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("区域公司结算汇总表：summary_id=" + summary_id);
        log.info("操作人id：operator_id=" + operator_id);
        Result result = profitCalculateService.calculateProfit(summary_id,operator_id);
        log.info("最后一步计算分润结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: calDeptBalance
     * @methodDesc: 计算区域公司、门店、员工结算
     * @description: 此方法便于手动执行
     * @param: [balance_month]
     * @return void
     * @create 2018-07-28 9:57
     **/
    @PostMapping("/calDeptBalance")
    public Result calDeptBalance(@RequestParam("balance_month") String balance_month)throws Exception{
        log.info("计算区域公司、门店、员工结算开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("计算区域公司、门店、员工结算：balance_month=" + balance_month);
        Result result = balanceCalculateService.calDeptBalance(balance_month);
        log.info("计算区域公司、门店、员工结算结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: calWHBalance
     * @methodDesc: 计算仓库结算
     * @description: 此方法便于手动执行
     * @param: [balance_month]
     * @return com.hryj.common.Result
     * @create 2018-07-28 10:02
     **/
    @PostMapping("/calWHBalance")
    public Result calWHBalance(@RequestParam("balance_month") String balance_month)throws Exception{
        log.info("计算仓库结算开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("计算仓库结算：balance_month=" + balance_month);
        Result result = balanceCalculateService.calWHBalance(balance_month);
        log.info("计算仓库结算结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: calStaffDaySalary
     * @methodDesc: 计算员工的日工资
     * @description: 此方法便于手动执行
     * @param: [cal_date]
     * @return com.hryj.common.Result
     * @create 2018-07-28 10:05
     **/
    @PostMapping("/calStaffDaySalary")
    public Result calStaffDaySalary(@RequestParam("cal_date") String cal_date)throws Exception{
        log.info("计算员工的日工资开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("计算员工的日工资：cal_date=" + cal_date);
        Result result = costCalculateService.calStaffDaySalary(cal_date);
        log.info("计算员工的日工资结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: statisOrderData
     * @methodDesc: 统计仓库、门店、员工的订单数据
     * @description: 此方法便于手动执行
     * @param: [statis_date]
     * @return com.hryj.common.Result
     * @create 2018-07-28 11:04
     **/
    @PostMapping("/statisOrderData")
    public Result statisOrderData(@RequestParam("statis_date") String statis_date){
        log.info("统计仓库、门店、员工的订单数据开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("统计仓库、门店、员工的订单数据：statis_date=" + statis_date);
        Result result = orderStatisService.statisWHOrderData(statis_date);
        result = orderStatisService.statisStoreOrderData(statis_date);
        result = orderStatisService.statisStaffOrderData(statis_date);
        log.info("统计仓库、门店、员工的订单数据结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: statisDeptGrossProfit
     * @methodDesc: 统计部门毛利分润
     * @description:
     * @param: [statis_month]
     * @return com.hryj.common.Result
     * @create 2018-08-17 11:07
     **/
    @PostMapping("/statisDeptGrossProfit")
    public Result statisDeptGrossProfit(@RequestParam("statis_month") String statis_month){
        log.info("统计部门毛利分润开始===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        log.info("统计部门毛利分润：statis_month=" + statis_month);
        Result result = grossProfitStatisService.statisDeptGrossProfit(statis_month);
        log.info("统计部门毛利分润结束===========================：" + DateUtil.format(new Date(),DatePattern.NORM_DATETIME_MS_PATTERN));
        return result;
    }

    /**
     * @author 李道云
     * @methodName: backupDataByMonth
     * @methodDesc: 按月备份数据
     * @description: 此方法便于手动执行
     * @param: [backup_month]
     * @return void
     * @create 2018-08-10 16:44
     **/
    @PostMapping("/backupDataByMonth")
    public void backupDataByMonth(@RequestParam("backup_month") String backup_month)throws Exception{
        backupDataService.backupDeptCostData(backup_month);
        backupDataService.backupDeptShareConfig(backup_month);
    }

    /**
     * @author 李道云
     * @methodName: backupDataByDay
     * @methodDesc: 按日备份数据
     * @description: 此方法便于手动执行
     * @param: [backup_date]
     * @return void
     * @create 2018-08-10 16:44
     **/
    @PostMapping("/backupDataByDay")
    public void backupDataByDay(@RequestParam("backup_date") String backup_date)throws Exception{
        backupDataService.backupDeptGroup(backup_date);
        backupDataService.backupStaffDeptRelation(backup_date);
    }
}

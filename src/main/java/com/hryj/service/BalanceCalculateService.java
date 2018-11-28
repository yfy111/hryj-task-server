package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.*;
import com.hryj.exception.BizException;
import com.hryj.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: BalanceCalculateService
 * @description: 区域公司、门店、员工结算
 * @create 2018/7/17 21:20
 **/
@Slf4j
@Service
public class BalanceCalculateService {

    @Autowired
    private BackupDataMapper backupDataMapper;

    @Autowired
    private StaffDaySalaryMapper staffDaySalaryMapper;

    @Autowired
    private WhOrderStatisMapper whOrderStatisMapper;

    @Autowired
    private StoreOrderStatisMapper storeOrderStatisMapper;

    @Autowired
    private StaffOrderStatisMapper staffOrderStatisMapper;

    @Autowired
    private HelpOrderDetailMapper helpOrderDetailMapper;

    @Autowired
    private RegionBalanceSummaryMapper regionBalanceSummaryMapper;

    @Autowired
    private RegionBalanceDetailService regionBalanceDetailService;

    @Autowired
    private StoreDeuceRegionCostMapper storeDeuceRegionCostMapper;

    @Autowired
    private StaffBalanceSummaryMapper staffBalanceSummaryMapper;

    @Autowired
    private StoreDeuceRegionCostService storeDeuceRegionCostService;

    @Autowired
    private StoreBalanceSummaryService storeBalanceSummaryService;

    @Autowired
    private StaffCostDetailService staffCostDetailService;

    @Autowired
    private StaffBalanceSummaryService staffBalanceSummaryService;

    @Autowired
    private WhProfitDetailService whProfitDetailService;

    /**
     * @author 李道云
     * @methodName: calDeptBalance
     * @methodDesc: 区域公司、门店、员工结算
     * @description:
     * @param: [balance_month]
     * @return void
     * @create 2018-07-18 12:38
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result calDeptBalance(String balance_month) throws Exception{
        if(StrUtil.isEmpty(balance_month)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"结算月份不能为空");
        }
        //查询当月的区域公司列表
        List<Map> deptList = backupDataMapper.findMonthRegionList(balance_month);
        if(CollectionUtil.isNotEmpty(deptList)){
            //遍历区域公司列表
            for (Map regionMap : deptList){
                log.info("遍历区域公司列表:regionMap=" + JSON.toJSONString(regionMap));
                Long region_id = (Long) regionMap.get("dept_id");
                String region_name = (String) regionMap.get("dept_name");
                String region_dept_path = (String) regionMap.get("dept_path");
                BigDecimal region_salary_cost = BigDecimal.ZERO;
                //计算当月的实际天数
                Map days_map = backupDataMapper.countOpenDays(balance_month,region_id);
                Integer days = Integer.parseInt(days_map.get("open_days").toString());
                BackupDeptGroup firstBackupDeptGroup = backupDataMapper.findMonthFirstBackupDeptGroup(balance_month,region_id);
                Date beginDate = DateUtil.parse(firstBackupDeptGroup.getBackup_date(),DatePattern.NORM_DATE_PATTERN);
                //计算区域公司当月的工资成本
                for(int i=0; i<days; i++){
                    String cal_date = DateUtil.format(DateUtil.offsetDay(beginDate,i),DatePattern.NORM_DATE_PATTERN);
                    //计算区域公司每天的工资成本
                    Map salary_map = staffDaySalaryMapper.calRegionDaySalary(cal_date,region_id);
                    BigDecimal day_salary_amt = (BigDecimal) salary_map.get("day_salary_amt");
                    //将区域公司每天的工资成本平摊到当天的门店
                    List<BackupDeptGroup> storeList = backupDataMapper.findRegionDayStoreList(cal_date,region_dept_path);
                    if(CollectionUtil.isNotEmpty(storeList)){
                        int store_num = storeList.size();
                        BigDecimal dept_salary_cost = NumberUtil.div(day_salary_amt,store_num,2);
                        List<StoreDeuceRegionCost> storeDeuceRegionCostList = new ArrayList<>();
                        for (BackupDeptGroup storeDept : storeList){
                            StoreDeuceRegionCost storeDeuceRegionCost = new StoreDeuceRegionCost();
                            storeDeuceRegionCost.setCal_date(cal_date);
                            storeDeuceRegionCost.setRegion_id(region_id);
                            storeDeuceRegionCost.setStore_id(storeDept.getDept_id());
                            storeDeuceRegionCost.setStore_name(storeDept.getDept_name());
                            storeDeuceRegionCost.setDept_id(region_id);
                            storeDeuceRegionCost.setDept_name(region_name);
                            storeDeuceRegionCost.setDept_salary_cost(dept_salary_cost);
                            storeDeuceRegionCostList.add(storeDeuceRegionCost);
                        }
                        storeDeuceRegionCostService.insertBatch(storeDeuceRegionCostList);
                        log.info("门店平摊区域公司的成本：storeDeuceRegionCostList.size===" + storeDeuceRegionCostList.size());
                    }
                    region_salary_cost = NumberUtil.add(region_salary_cost,day_salary_amt);
                }
                //保存区域公司结算汇总表
                RegionBalanceSummary regionBalanceSummary = new RegionBalanceSummary();
                regionBalanceSummary.setBalance_month(balance_month);
                regionBalanceSummary.setDept_id(region_id);
                regionBalanceSummary.setDept_name(region_name);
                regionBalanceSummary.setDept_path(region_dept_path);
                regionBalanceSummary.setRegion_salary_cost(region_salary_cost);
                regionBalanceSummaryMapper.insert(regionBalanceSummary);
                log.info("区域公司结算汇总表：regionBalanceSummary===" + JSON.toJSONString(regionBalanceSummary));
                Long summary_id = regionBalanceSummary.getId();
                //查询区域公司当月的员工列表（普通部门，不包含门店和仓库）
                List<BackupStaffDeptRelation> staffList = backupDataMapper.findRegionMonthStaffList(balance_month,region_dept_path,CodeCache.getValueByKey("DeptType","S03"));
                if(CollectionUtil.isNotEmpty(staffList)){
                    List<RegionBalanceDetail> regionBalanceDetailList = new ArrayList<>();
                    for (BackupStaffDeptRelation staffUser : staffList){
                        //计算员工当月的工资
                        Map salary_map = staffDaySalaryMapper.calStaffMonthSalary(balance_month,staffUser.getStaff_id());
                        BigDecimal month_salary_amt = (BigDecimal) salary_map.get("month_salary_amt");
                        RegionBalanceDetail regionBalanceDetail = new RegionBalanceDetail();
                        regionBalanceDetail.setSummary_id(summary_id);
                        regionBalanceDetail.setDept_id(staffUser.getDept_id());
                        regionBalanceDetail.setDept_name(staffUser.getDept_name());
                        regionBalanceDetail.setStaff_id(staffUser.getStaff_id());
                        regionBalanceDetail.setStaff_name(staffUser.getStaff_name());
                        regionBalanceDetail.setStaff_job(staffUser.getStaff_job());
                        regionBalanceDetail.setSalary_amt(month_salary_amt);
                        regionBalanceDetailList.add(regionBalanceDetail);
                    }
                    //保存区域公司结算汇总明细表
                    regionBalanceDetailService.insertBatch(regionBalanceDetailList);
                    log.info("结算月份：{}，区域公司id：{}，区域公司名称：{}，区域公司结算明细表：regionBalanceDetailList.size==={}",
                            balance_month,region_id,region_name,regionBalanceDetailList.size());
                }
                //查询区域公司当月的门店列表
                List<BackupDeptGroup> storeList = backupDataMapper.findRegionMonthStoreList(balance_month,region_dept_path);
                if(CollectionUtil.isNotEmpty(storeList)){
                    List<StoreBalanceSummary> storeBalanceSummaryList = new ArrayList<>();
                    //计算门店当月的成本
                    for (BackupDeptGroup storeDept : storeList){
                        Long store_id = storeDept.getDept_id();
                        String store_name = storeDept.getDept_name();
                        String store_dept_path = storeDept.getDept_path();
                        //门店当月的开店天数
                        Map open_days_map = backupDataMapper.countOpenDays(balance_month,store_id);
                        Integer open_days = Integer.parseInt(open_days_map.get("open_days").toString());
                        if(open_days ==0){
                            continue;
                        }
                        //计算门店当月平摊区域公司的工资成本总和
                        Map region_salary_map = storeDeuceRegionCostMapper.calStoreRegionMonthCost(balance_month,store_id);
                        BigDecimal store_region_salary_cost = (BigDecimal) region_salary_map.get("store_region_salary_cost");
                        //门店当月固定成本
                        Map fixed_cost_map = backupDataMapper.calDeptFixedCost(balance_month,store_id);
                        BigDecimal month_store_fixed_cost = (BigDecimal) fixed_cost_map.get("store_fixed_cost");
                        BigDecimal store_fixed_cost = month_store_fixed_cost;
                        //门店当月工资成本
                        Map salary_map = staffDaySalaryMapper.calStoreMonthSalary(balance_month,store_id);
                        BigDecimal month_salary_amt = (BigDecimal) salary_map.get("month_salary_amt");
                        //门店当月配送成本
                        Map distribution_cost_map = storeOrderStatisMapper.calStoreMonthDistributionCost(store_id,balance_month);
                        BigDecimal month_store_distribution_cost = (BigDecimal) distribution_cost_map.get("store_distribution_cost");
                        StoreBalanceSummary storeBalanceSummary = new StoreBalanceSummary();
                        storeBalanceSummary.setBalance_month(balance_month);
                        storeBalanceSummary.setRegion_id(region_id);
                        storeBalanceSummary.setStore_id(store_id);
                        storeBalanceSummary.setStore_name(store_name);
                        storeBalanceSummary.setDept_path(store_dept_path);
                        storeBalanceSummary.setRegion_salary_cost(store_region_salary_cost);
                        storeBalanceSummary.setStore_fixed_cost(store_fixed_cost);
                        storeBalanceSummary.setStore_salary_cost(month_salary_amt);
                        storeBalanceSummary.setStore_distribution_cost(month_store_distribution_cost);
                        storeBalanceSummaryList.add(storeBalanceSummary);
                        //计算店员，平摊门店的区域公司工资成本、门店的工资成本、门店的固定成本、门店的配送成本
                        BigDecimal day_store_fixed_cost = NumberUtil.div(store_fixed_cost,open_days);//门店每天的固定成本
                        for(int j=0; j<days; j++){
                            String cal_date_new = DateUtil.format(DateUtil.offsetDay(beginDate,j),DatePattern.NORM_DATE_PATTERN);
                            log.info("平摊门店的区域公司工资成本、门店的工资成本、门店的固定成本、门店的配送成本，days:{},beginDate:{},j:{},cal_date_new:{},store_id:{}",days,beginDate,j,cal_date_new,store_id);
                            //查询门店当天的状态：开店关店
                            BackupDeptGroup deptGroup = backupDataMapper.findBackupDeptGroup(cal_date_new,store_id);
                            Integer dept_status = deptGroup==null?0:deptGroup.getDept_status();
                            //门店当天平摊区域公司的工资成本
                            BigDecimal day_dept_salary_cost =BigDecimal.ZERO;
                            //门店当天的工资成本
                            BigDecimal day_store_salary_cost = BigDecimal.ZERO;
                            if(dept_status ==1){
                                StoreDeuceRegionCost storeDeuceRegionCost = storeDeuceRegionCostMapper.findStoreDeuceRegionCost(cal_date_new,store_id,region_id);
                                if(storeDeuceRegionCost !=null){
                                    day_dept_salary_cost = storeDeuceRegionCost.getDept_salary_cost();
                                }
                                Map day_salary_map = staffDaySalaryMapper.calStoreDaySalary(cal_date_new,store_id);
                                day_store_salary_cost = (BigDecimal) day_salary_map.get("day_salary_amt");
                            }
                            //门店当天的配送成本
                            Map day_distribution_cost_map = storeOrderStatisMapper.calStoreDayDistributionCost(store_id,cal_date_new);
                            BigDecimal day_store_distribution_cost = (BigDecimal) day_distribution_cost_map.get("store_distribution_cost");
                            //保存店员成本明细表
                            List<BackupStaffDeptRelation> storeStaffList = backupDataMapper.findStoreDayStaffList(cal_date_new,store_id);
                            if(CollectionUtil.isNotEmpty(storeStaffList)){
                                List<StaffCostDetail> staffCostDetailList = new ArrayList<>();
                                for(BackupStaffDeptRelation staffUser : storeStaffList){
                                    StaffCostDetail staffCostDetail = new StaffCostDetail();
                                    staffCostDetail.setCal_date(cal_date_new);
                                    staffCostDetail.setRegion_id(region_id);
                                    staffCostDetail.setStaff_id(staffUser.getStaff_id());
                                    staffCostDetail.setStaff_name(staffUser.getStaff_name());
                                    staffCostDetail.setStore_id(store_id);
                                    staffCostDetail.setStore_name(store_name);
                                    if(dept_status ==1) {
                                        BigDecimal share_cost_ratio = staffUser.getShare_cost_ratio();//分摊成本比例
                                        BigDecimal staff_store_salary_cost = NumberUtil.mul(day_store_salary_cost,NumberUtil.div(share_cost_ratio,100));//平摊门店的工资成本
                                        BigDecimal staff_store_fixed_cost = NumberUtil.mul(day_store_fixed_cost,NumberUtil.div(share_cost_ratio,100));//平摊门店的固定成本
                                        BigDecimal staff_store_distribution_cost = NumberUtil.mul(day_store_distribution_cost,NumberUtil.div(share_cost_ratio,100));//平摊门店的配送成本
                                        BigDecimal staff_dept_salary_cost = NumberUtil.mul(day_dept_salary_cost,NumberUtil.div(share_cost_ratio,100));//平摊门店的区域公司工资成本
                                        staffCostDetail.setStore_salary_cost(NumberUtil.round(staff_store_salary_cost,2));
                                        staffCostDetail.setStore_fixed_cost(NumberUtil.round(staff_store_fixed_cost,2));
                                        staffCostDetail.setStore_distribution_cost(NumberUtil.round(staff_store_distribution_cost,2));
                                        staffCostDetail.setDept_salary_cost(NumberUtil.round(staff_dept_salary_cost,2));
                                    }
                                    staffCostDetailList.add(staffCostDetail);
                                }
                                staffCostDetailService.insertBatch(staffCostDetailList);
                                log.info("门店id：{}，门店名称：{},计算日期：{}，保存店员成本明细表：staffCostDetailList.size==={}",
                                        store_id,store_name,cal_date_new,staffCostDetailList.size());
                            }
                        }
                    }
                    storeBalanceSummaryService.insertBatch(storeBalanceSummaryList);
                    log.info("结算月份：{}，门店结算汇总表：storeBalanceSummaryList.size==={}",balance_month,storeBalanceSummaryList.size());
                }
                //查询区域公司当月的店员列表
                List<BackupStaffDeptRelation> soreStaffList = backupDataMapper.findRegionMonthStaffList(balance_month,region_dept_path,CodeCache.getValueByKey("DeptType","S01"));
                if(CollectionUtil.isNotEmpty(soreStaffList)){
                    List<StaffBalanceSummary> staffBalanceSummaryList = new ArrayList<>();
                    for (BackupStaffDeptRelation storeStaff : soreStaffList){
                        Long staff_id = storeStaff.getStaff_id();
                        //员工服务分润、代下单分润、配送分润
                        Map staff_month_profit_map = staffOrderStatisMapper.calStaffMonthProfit(balance_month,staff_id);
                        BigDecimal service_profit = (BigDecimal)staff_month_profit_map.get("service_profit");
                        BigDecimal help_order_profit = (BigDecimal)staff_month_profit_map.get("help_order_profit");
                        BigDecimal distribution_profit = (BigDecimal)staff_month_profit_map.get("distribution_profit");
                        Integer distribution_num = Integer.parseInt(staff_month_profit_map.get("distribution_num").toString());
                        //店长还有店员代下单的分润
                        if(CodeCache.getValueByKey("StaffJob","S01").equals(storeStaff.getStaff_job())){
                            Map store_manage_map = helpOrderDetailMapper.calStoreManagerHelpOrderPorfit(balance_month,staff_id);
                            BigDecimal store_staff_order_profit = (BigDecimal)store_manage_map.get("store_staff_order_profit");
                            help_order_profit = NumberUtil.add(help_order_profit,store_staff_order_profit);
                        }
                        BigDecimal total_profit = NumberUtil.add(service_profit,help_order_profit);//总分润:没有剔除成本
                        //计算上个月的遗留成本
                        BigDecimal last_month_cost = BigDecimal.ZERO;
                        String last_month = DateUtil.format(DateUtil.offsetMonth(beginDate,-1),"yyyy-MM");
                        StaffBalanceSummary lastStaffBalanceSummary = staffBalanceSummaryMapper.findStaffBalanceSummary(last_month,region_id,staff_id);
                        if(lastStaffBalanceSummary !=null){
                            BigDecimal last_month_total_cost = lastStaffBalanceSummary.getTotal_cost();
                            BigDecimal last_month_total_profit = lastStaffBalanceSummary.getTotal_profit();
                            if(NumberUtil.isLessOrEqual(last_month_total_profit,last_month_total_cost)){
                                last_month_cost = NumberUtil.sub(last_month_total_cost,last_month_total_profit);
                            }
                        }
                        //保存员工结算汇总表
                        StaffBalanceSummary staffBalanceSummary = new StaffBalanceSummary();
                        staffBalanceSummary.setBalance_month(balance_month);
                        staffBalanceSummary.setRegion_id(region_id);
                        staffBalanceSummary.setStore_id(storeStaff.getDept_id());
                        staffBalanceSummary.setStore_name(storeStaff.getDept_name());
                        staffBalanceSummary.setDept_path(storeStaff.getDept_path());
                        staffBalanceSummary.setStaff_id(staff_id);
                        staffBalanceSummary.setStaff_name(storeStaff.getStaff_name());
                        staffBalanceSummary.setStaff_job(storeStaff.getStaff_job());
                        staffBalanceSummary.setStaff_job_name(CodeCache.getNameByValue("StaffJob",storeStaff.getStaff_job()));
                        staffBalanceSummary.setLast_month_cost(last_month_cost);
                        staffBalanceSummary.setService_profit(service_profit);
                        staffBalanceSummary.setHelp_order_profit(help_order_profit);
                        staffBalanceSummary.setDistribution_profit(distribution_profit);
                        staffBalanceSummary.setDistribution_num(distribution_num);
                        staffBalanceSummary.setTotal_profit(total_profit);
                        staffBalanceSummaryList.add(staffBalanceSummary);
                    }
                    staffBalanceSummaryService.insertBatch(staffBalanceSummaryList);
                    log.info("结算月份：{}，员工结算汇总表：staffBalanceSummaryList.size==={}",balance_month,staffBalanceSummaryList.size());
                }
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @methodName: calWHBalance
     * @methodDesc: 计算仓库结算
     * @description:
     * @param: [balance_month]
     * @return void
     * @create 2018-07-23 21:45
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result calWHBalance(String balance_month) throws Exception{
        if(StrUtil.isEmpty(balance_month)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"结算月份不能为空");
        }
        //查询当月的仓库列表
        List<Map> deptList = backupDataMapper.findAllWHDeptList(balance_month);
        if(CollectionUtil.isEmpty(deptList)){
            throw new BizException("部门组织数据备份出现问题");
        }
        for (Map regionMap : deptList) {
            Long wh_id = (Long) regionMap.get("dept_id");
            String wh_name = (String) regionMap.get("dept_name");
            //计算仓库当月分润总额
            Map wh_profit_map = whOrderStatisMapper.calWHMonthProfit(balance_month,wh_id);
            BigDecimal wh_order_profit = (BigDecimal) wh_profit_map.get("wh_order_profit");
            //订单分润金额大于0才分润
            if(NumberUtil.isGreater(wh_order_profit,BigDecimal.ZERO)){
                BigDecimal used_profit = BigDecimal.ZERO;//已分配利润
                //查询分润配置
                List<BackupDeptShareConfig> deptShareConfigList = backupDataMapper.findDeptShareConfigList(balance_month,wh_id);
                List<WhProfitDetail> whProfitDetailList = new ArrayList<>();
                if(CollectionUtil.isNotEmpty(deptShareConfigList)){
                    for (BackupDeptShareConfig deptShareConfig : deptShareConfigList){
                        BigDecimal share_ratio = deptShareConfig.getShare_ratio();
                        //分润比例大于0才计算
                        if(NumberUtil.isGreater(share_ratio,BigDecimal.ZERO)) {
                            BigDecimal profit_amt = NumberUtil.round(NumberUtil.mul(wh_order_profit, NumberUtil.div(share_ratio, 100)), 2);
                            WhProfitDetail whProfitDetail = new WhProfitDetail();
                            whProfitDetail.setProfit_month(balance_month);
                            whProfitDetail.setWh_id(wh_id);
                            whProfitDetail.setWh_name(wh_name);
                            whProfitDetail.setDept_id(deptShareConfig.getDept_id());
                            whProfitDetail.setDept_staff_id(deptShareConfig.getStaff_id());
                            whProfitDetail.setProfit_amt(profit_amt);
                            whProfitDetailList.add(whProfitDetail);
                            used_profit = NumberUtil.add(used_profit,profit_amt);
                        }
                    }
                }
                //剩余利润分配给仓库
                BigDecimal wh_profit = NumberUtil.sub(wh_order_profit,used_profit);
                WhProfitDetail whProfitDetail = new WhProfitDetail();
                whProfitDetail.setProfit_month(balance_month);
                whProfitDetail.setWh_id(wh_id);
                whProfitDetail.setWh_name(wh_name);
                whProfitDetail.setDept_id(wh_id);
                whProfitDetail.setDept_staff_id(null);
                whProfitDetail.setProfit_amt(wh_profit);
                whProfitDetailList.add(whProfitDetail);
                whProfitDetailService.insertBatch(whProfitDetailList);
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

}

package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.*;
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
 * @className: ProfitCalculateService
 * @description: 计算分润service
 * @create 2018/7/19 22:39
 **/
@Slf4j
@Service
public class ProfitCalculateService {

    @Autowired
    private RegionBalanceSummaryMapper regionBalanceSummaryMapper;

    @Autowired
    private StoreBalanceSummaryMapper storeBalanceSummaryMapper;

    @Autowired
    private StaffBalanceSummaryMapper staffBalanceSummaryMapper;

    @Autowired
    private StoreDeuceRegionCostMapper storeDeuceRegionCostMapper;

    @Autowired
    private StaffCostDetailMapper staffCostDetailMapper;

    @Autowired
    private RegionBalanceDetailMapper regionBalanceDetailMapper;

    @Autowired
    private RegionProfitDetailMapper regionProfitDetailMapper;

    @Autowired
    private BackupDataMapper backupDataMapper;

    @Autowired
    private StaffDeptChangeRecordMapper staffDeptChangeRecordMapper;

    @Autowired
    private WhProfitDetailMapper whProfitDetailMapper;

    @Autowired
    private GroupBalanceSummaryMapper groupBalanceSummaryMapper;

    @Autowired
    private StoreDeuceRegionCostService storeDeuceRegionCostService;

    @Autowired
    private StoreBalanceSummaryService storeBalanceSummaryService;

    @Autowired
    private StaffCostDetailService staffCostDetailService;

    @Autowired
    private StaffBalanceSummaryService staffBalanceSummaryService;

    @Autowired
    private RegionProfitDetailService regionProfitDetailService;

    @Autowired
    private RegionBalanceDetailService regionBalanceDetailService;

    @Autowired
    private GroupBalanceSummaryService groupBalanceSummaryService;


    /**
     * @author 李道云
     * @methodName: calculateProfit
     * @methodDesc: 计算分润
     * @description:
     * @param: [summary_id,operator_id]
     * @return com.hryj.common.Result
     * @create 2018-07-19 23:21
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result calculateProfit(Long summary_id, Long operator_id) throws Exception{
        if(summary_id ==null || operator_id==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"区域公司结算汇总id和操作人id不能为空");
        }
        //1、平摊区域公司非固定成本到门店
        RegionBalanceSummary regionBalanceSummary = regionBalanceSummaryMapper.selectById(summary_id);
        Long region_id = regionBalanceSummary.getDept_id();
        String balance_month = regionBalanceSummary.getBalance_month();
        //计算当月的实际天数
        Map days_map = backupDataMapper.countOpenDays(balance_month,region_id);
        Integer days = Integer.parseInt(days_map.get("open_days").toString());
        BackupDeptGroup firstBackupDeptGroup = backupDataMapper.findMonthFirstBackupDeptGroup(balance_month,region_id);
        Date beginDate = DateUtil.parse(firstBackupDeptGroup.getBackup_date(),DatePattern.NORM_DATE_PATTERN);
        BigDecimal region_non_fixed_cost = regionBalanceSummary.getRegion_non_fixed_cost();
        //区域公司平均每天的非固定成本
        BigDecimal day_region_non_fixed_cost = NumberUtil.div(region_non_fixed_cost,days);
        log.info("区域公司平均每天的非固定成本,day_region_non_fixed_cost=" + day_region_non_fixed_cost);
        for(int i=0; i<days; i++){
            String cal_date = DateUtil.format(DateUtil.offsetDay(beginDate,i),DatePattern.NORM_DATE_PATTERN);
            //查询当天的门店
            EntityWrapper<StoreDeuceRegionCost> storeDeuceRegionCostWrapper = new EntityWrapper<>();
            storeDeuceRegionCostWrapper.eq("cal_date",cal_date);
            storeDeuceRegionCostWrapper.eq("region_id",region_id);
            List<StoreDeuceRegionCost> storeDeuceRegionCostList = storeDeuceRegionCostMapper.selectList(storeDeuceRegionCostWrapper);
            if(CollectionUtil.isNotEmpty(storeDeuceRegionCostList)){
                int store_num = storeDeuceRegionCostList.size();
                BigDecimal store_day_region_non_fixed_cost = NumberUtil.div(day_region_non_fixed_cost,store_num,2);
                log.info("门店平摊区域公司每天的非固定成本,store_num={}，store_day_region_non_fixed_cost={}",store_num,store_day_region_non_fixed_cost);
                for (StoreDeuceRegionCost storeDeuceRegionCost : storeDeuceRegionCostList){
                    storeDeuceRegionCost.setDept_non_fixed_cost(store_day_region_non_fixed_cost);
                    storeDeuceRegionCost.setUpdate_time(DateUtil.date());
                }
                //批量更新门店平摊区域公司的成本
                storeDeuceRegionCostService.updateBatchById(storeDeuceRegionCostList);
            }
        }
        //2、更新门店结算汇总表的区域公司非固定成本，并计算员工成本明细
        EntityWrapper<StoreBalanceSummary> storeBalanceSummaryWrapper = new EntityWrapper<>();
        storeBalanceSummaryWrapper.eq("region_id",region_id);
        storeBalanceSummaryWrapper.eq("balance_month",balance_month);
        List<StoreBalanceSummary> storeBalanceSummaryList = storeBalanceSummaryMapper.selectList(storeBalanceSummaryWrapper);
        if(CollectionUtil.isNotEmpty(storeBalanceSummaryList)){
            for (StoreBalanceSummary storeBalanceSummary : storeBalanceSummaryList){
                storeBalanceSummary.setOperator_id(operator_id);
                Long store_id = storeBalanceSummary.getStore_id();
                //计算门店当月平摊区域公司的非固定成本总和
                Map region_non_fixed_map = storeDeuceRegionCostMapper.calStoreRegionMonthCost(balance_month,store_id);
                BigDecimal store_region_non_fixed_cost = (BigDecimal) region_non_fixed_map.get("store_region_non_fixed_cost");
                storeBalanceSummary.setRegion_non_fixed_cost(store_region_non_fixed_cost);
                storeBalanceSummary.setUpdate_time(DateUtil.date());
                //门店非固定成本
                BigDecimal store_non_fixed_cost = storeBalanceSummary.getStore_non_fixed_cost();
                //门店当月的开店天数
                Map open_days_map = backupDataMapper.countOpenDays(balance_month,store_id);
                Integer open_days = Integer.parseInt(open_days_map.get("open_days").toString());
                BigDecimal day_store_non_fixed_cost = NumberUtil.div(store_non_fixed_cost,open_days);
                //将区域公司平摊到门店非固定成本、门店的固定成本平均到天，再平分到当天的店员身上
                for(int j=0; j<days; j++){
                    String cal_date = DateUtil.format(DateUtil.offsetDay(beginDate,j),DatePattern.NORM_DATE_PATTERN);
                    //查询门店当天的状态：开店关店
                    BackupDeptGroup deptGroup = backupDataMapper.findBackupDeptGroup(cal_date,store_id);
                    Integer dept_status = deptGroup.getDept_status();
                    if(dept_status ==1) {//开店状态才平摊区域公司和门店的非固定成本
                        StoreDeuceRegionCost storeDeuceRegionCost = storeDeuceRegionCostMapper.findStoreDeuceRegionCost(cal_date,store_id,region_id);
                        BigDecimal day_store_region_non_fixed_cost = BigDecimal.ZERO;
                        if(storeDeuceRegionCost !=null){
                            day_store_region_non_fixed_cost = storeDeuceRegionCost.getDept_non_fixed_cost();
                        }
                        EntityWrapper<StaffCostDetail> staffCostDetailWrapper = new EntityWrapper<>();
                        staffCostDetailWrapper.eq("cal_date",cal_date);
                        staffCostDetailWrapper.eq("store_id",store_id);
                        staffCostDetailWrapper.eq("region_id",region_id);
                        List<StaffCostDetail> staffCostDetailList = staffCostDetailMapper.selectList(staffCostDetailWrapper);
                        if(CollectionUtil.isNotEmpty(staffCostDetailList)){
                            for(StaffCostDetail staffCostDetail : staffCostDetailList){
                                BackupStaffDeptRelation backupStaffDeptRelation =  backupDataMapper.findStaffDeptRelationByStaffId(staffCostDetail.getCal_date(),staffCostDetail.getStaff_id());
                                BigDecimal share_cost_ratio = backupStaffDeptRelation.getShare_cost_ratio();//成本分摊比例
                                if(share_cost_ratio !=null && NumberUtil.isGreater(share_cost_ratio,BigDecimal.ZERO)){
                                    BigDecimal staff_day_store_non_fixed_cost = NumberUtil.mul(day_store_non_fixed_cost,NumberUtil.div(share_cost_ratio,100));
                                    BigDecimal staff_day_store_region_non_fixed_cost = NumberUtil.mul(day_store_region_non_fixed_cost,NumberUtil.div(share_cost_ratio,100));
                                    staffCostDetail.setStore_non_fixed_cost(NumberUtil.round(staff_day_store_non_fixed_cost,2));
                                    staffCostDetail.setDept_non_fixed_cost(NumberUtil.round(staff_day_store_region_non_fixed_cost,2));
                                }
                                staffCostDetail.setUpdate_time(DateUtil.date());
                            }
                            //批量更新员工成本明细表
                            staffCostDetailService.updateBatchById(staffCostDetailList);
                        }
                    }
                }
            }
            //批量更新门店结算汇总表
            storeBalanceSummaryService.updateBatchById(storeBalanceSummaryList);
        }
        //3、计算员工结算汇总表的当月实际成本
        EntityWrapper<StaffBalanceSummary> staffBalanceSummaryWrapper = new EntityWrapper<>();
        staffBalanceSummaryWrapper.eq("balance_month",balance_month);
        staffBalanceSummaryWrapper.eq("region_id",region_id);
        List<StaffBalanceSummary> staffBalanceSummaryList = staffBalanceSummaryMapper.selectList(staffBalanceSummaryWrapper);
        if(CollectionUtil.isNotEmpty(staffBalanceSummaryList)){
            for (StaffBalanceSummary staffBalanceSummary : staffBalanceSummaryList){
                Long staff_id = staffBalanceSummary.getStaff_id();
                Long store_id = staffBalanceSummary.getStore_id();
                BigDecimal last_month_cost = staffBalanceSummary.getLast_month_cost();//上月剩余成本
                Map staff_cost_map = staffCostDetailMapper.calStaffMonthCost(balance_month,region_id,store_id,staff_id);
                BigDecimal this_month_cost = (BigDecimal) staff_cost_map.get("month_cost");//本月成本
                BigDecimal total_cost = NumberUtil.add(last_month_cost,this_month_cost);
                staffBalanceSummary.setThis_month_cost(this_month_cost);
                staffBalanceSummary.setTotal_cost(total_cost);//承担总成本
                staffBalanceSummary.setOperator_id(operator_id);
                staffBalanceSummary.setUpdate_time(DateUtil.date());
            }
            //批量更新员工结算汇总表
            staffBalanceSummaryService.updateBatchById(staffBalanceSummaryList);
        }
        //4、计算店长转移成本，店员离职，成本转移至店长
        String change_type = CodeCache.getValueByKey("StaffDeptChangeType","S01");//员工离职
        List<StaffDeptChangeRecord> staffDeptChangeRecordList = staffDeptChangeRecordMapper.findStaffDeptChangeRecordList(balance_month,change_type);
        List<String> leaveIdList = new ArrayList<>();//临时存放离职员工的结算汇总id
        if(CollectionUtil.isNotEmpty(staffDeptChangeRecordList)){
            for (StaffDeptChangeRecord staffDeptChangeRecord : staffDeptChangeRecordList){
                EntityWrapper<StaffBalanceSummary> staffSummaryWrapper = new EntityWrapper<>();
                staffSummaryWrapper.eq("balance_month",balance_month);
                staffSummaryWrapper.eq("region_id",region_id);
                staffSummaryWrapper.eq("staff_id",staffDeptChangeRecord.getStaff_id());
                staffSummaryWrapper.eq("store_id",staffDeptChangeRecord.getPre_dept_id());
                StaffBalanceSummary staffBalanceSummary = staffBalanceSummaryService.selectOne(staffSummaryWrapper);
                //如果该店员承担成本小于总分润，将扣除分润后的剩余成本转移至店长
                if(staffBalanceSummary !=null && NumberUtil.isLess(staffBalanceSummary.getTotal_profit(),staffBalanceSummary.getTotal_cost())){
                    BigDecimal transfer_cost = NumberUtil.sub(staffBalanceSummary.getTotal_cost(),staffBalanceSummary.getTotal_profit());
                    //查询店长当月的结算汇总表
                    EntityWrapper<StaffBalanceSummary> storeManagerWrapper = new EntityWrapper<>();
                    storeManagerWrapper.eq("balance_month",balance_month);
                    storeManagerWrapper.eq("region_id",region_id);
                    storeManagerWrapper.eq("store_id",staffBalanceSummary.getStore_id());
                    storeManagerWrapper.eq("staff_job",CodeCache.getValueByKey("StaffJob","S01"));//01-店长
                    StaffBalanceSummary storeManagerSummary = staffBalanceSummaryService.selectOne(storeManagerWrapper);
                    if(storeManagerSummary !=null){//更新店长转移成本和承担总成本
                        storeManagerSummary.setTransfer_cost(NumberUtil.add(storeManagerSummary.getTransfer_cost(),transfer_cost));
                        BigDecimal total_cost = NumberUtil.add(storeManagerSummary.getTotal_cost(),storeManagerSummary.getTransfer_cost());
                        storeManagerSummary.setTotal_cost(total_cost);
                        storeManagerSummary.setUpdate_time(DateUtil.date());
                        staffBalanceSummaryMapper.updateById(storeManagerSummary);
                        //不能将离职员工的成本设置为0，否则后面计算实得分润有问题
                        //staffBalanceSummary.setTotal_cost(BigDecimal.ZERO);
                        //staffBalanceSummaryMapper.updateById(staffBalanceSummary);
                        leaveIdList.add(staffBalanceSummary.getId().toString());
                    }
                }
                staffDeptChangeRecord.setBalance_flag(1);//1-已结算转移成本
                staffDeptChangeRecord.setUpdate_time(DateUtil.date());
                staffDeptChangeRecordMapper.updateById(staffDeptChangeRecord);
            }
        }
        //5、计算员工实际分润，并根据分润配置，将纯利润分配给对应部门和人
        EntityWrapper<StaffBalanceSummary> staffSummaryWrapper = new EntityWrapper<>();
        staffSummaryWrapper.eq("balance_month",balance_month);
        staffSummaryWrapper.eq("region_id",region_id);
        List<StaffBalanceSummary> staffSummaryList = staffBalanceSummaryMapper.selectList(staffSummaryWrapper);
        if(CollectionUtil.isNotEmpty(staffSummaryList)){
            for (StaffBalanceSummary staffBalanceSummary : staffSummaryList){
                Long store_id = staffBalanceSummary.getStore_id();
                BigDecimal total_cost = staffBalanceSummary.getTotal_cost();
                BigDecimal total_profit = staffBalanceSummary.getTotal_profit();
                //总分润大于总成本，才分配纯利
                if(NumberUtil.isGreater(total_profit,total_cost)){
                    BigDecimal available_profit = NumberUtil.sub(total_profit,total_cost);//可用分润
                    BigDecimal used_profit = BigDecimal.ZERO;//已分配利润
                    //查询分润配置
                    List<BackupDeptShareConfig> deptShareConfigList = backupDataMapper.findDeptShareConfigList(balance_month,store_id);
                    if(CollectionUtil.isNotEmpty(deptShareConfigList)){
                        List<RegionProfitDetail> regionProfitDetailList = new ArrayList<>();
                        for (BackupDeptShareConfig deptShareConfig : deptShareConfigList){
                            BigDecimal share_ratio = deptShareConfig.getShare_ratio();
                            //分润比例大于0才计算
                            if(NumberUtil.isGreater(share_ratio,BigDecimal.ZERO)){
                                BigDecimal profit_amt = NumberUtil.round(NumberUtil.mul(available_profit,NumberUtil.div(share_ratio,100)),2);
                                RegionProfitDetail regionProfitDetail = new RegionProfitDetail();
                                regionProfitDetail.setProfit_month(balance_month);
                                regionProfitDetail.setRegion_id(region_id);
                                regionProfitDetail.setStore_id(store_id);
                                regionProfitDetail.setStore_name(staffBalanceSummary.getStore_name());
                                regionProfitDetail.setStore_staff_id(staffBalanceSummary.getStaff_id());
                                regionProfitDetail.setStore_staff_name(staffBalanceSummary.getStaff_name());
                                regionProfitDetail.setDept_id(deptShareConfig.getDept_id());
                                regionProfitDetail.setDept_staff_id(deptShareConfig.getStaff_id());
                                regionProfitDetail.setProfit_amt(profit_amt);
                                regionProfitDetailList.add(regionProfitDetail);
                                used_profit = NumberUtil.add(used_profit,profit_amt);
                            }
                        }
                        //批量保存区域公司分润明细表
                        regionProfitDetailService.insertBatch(regionProfitDetailList);
                    }
                    //剩余利润分配给店员
                    BigDecimal staff_profit = NumberUtil.sub(available_profit,used_profit);
                    staffBalanceSummary.setActual_profit(staff_profit);//店员实得分润
                }else if(leaveIdList.contains(staffBalanceSummary.getId().toString())){
                    //将离职员工的成本置为0
                    staffBalanceSummary.setTotal_cost(BigDecimal.ZERO);
                }
                staffBalanceSummary.setUpdate_time(DateUtil.date());
            }
            staffBalanceSummaryService.updateBatchById(staffSummaryList);
        }
        //6、更新区域公司结算汇总表和明细表的实得分润
        BigDecimal region_actual_profit = BigDecimal.ZERO;
        EntityWrapper<RegionBalanceDetail> regionBalanceDetailWrapper = new EntityWrapper<>();
        regionBalanceDetailWrapper.eq("summary_id",summary_id);
        List<RegionBalanceDetail> regionBalanceDetailList = regionBalanceDetailMapper.selectList(regionBalanceDetailWrapper);
        if(CollectionUtil.isNotEmpty(regionBalanceDetailList)){
            for (RegionBalanceDetail regionBalanceDetail : regionBalanceDetailList){
                //计算实得分润总和
                Map dept_profit_map = regionProfitDetailMapper.calRegionBalanceProfit(balance_month,region_id,regionBalanceDetail.getDept_id(),regionBalanceDetail.getStaff_id());
                BigDecimal dept_actual_profit = (BigDecimal) dept_profit_map.get("dept_actual_profit");
                regionBalanceDetail.setActual_profit(dept_actual_profit);
                regionBalanceDetail.setUpdate_time(DateUtil.date());
                region_actual_profit = NumberUtil.add(region_actual_profit,dept_actual_profit);
            }
            //批量更新区域公司结算明细表
            regionBalanceDetailService.updateBatchById(regionBalanceDetailList);
        }
        //更新区域结算汇总表
        regionBalanceSummary.setActual_profit(region_actual_profit);
        regionBalanceSummary.setUpdate_time(DateUtil.date());
        regionBalanceSummaryMapper.updateById(regionBalanceSummary);
        log.info("更新区域结算汇总表：regionBalanceSummary==={}",JSON.toJSONString(regionBalanceSummary));
        //7、更新集团结算汇总表
        GroupBalanceSummary groupSummary = new GroupBalanceSummary();
        groupSummary.setBalance_month(balance_month);
        GroupBalanceSummary groupBalanceSummary = groupBalanceSummaryMapper.selectOne(groupSummary);
        if(groupBalanceSummary ==null){
            groupBalanceSummary = new GroupBalanceSummary();
            groupBalanceSummary.setBalance_month(balance_month);
        }
        Long group_id = 100000L;
        //门店分润总额
        Map group_store_profit_map = regionProfitDetailMapper.calGroupBalanceProfit(balance_month,group_id);//集团id固定的
        BigDecimal store_profit = (BigDecimal) group_store_profit_map.get("dept_actual_profit");
        groupBalanceSummary.setStore_profit(store_profit);
        //仓库分润总额
        Map group_wh_profit_map = whProfitDetailMapper.calWHDeptProfit(balance_month,group_id);
        BigDecimal wh_profit = (BigDecimal) group_wh_profit_map.get("dept_profit_amt");
        groupBalanceSummary.setWh_profit(wh_profit);
        groupBalanceSummary.setCreate_time(DateUtil.date());
        groupBalanceSummaryService.insertOrUpdate(groupBalanceSummary);
        log.info("更新集团结算汇总表：groupBalanceSummary==={}",JSON.toJSONString(groupBalanceSummary));
        return new Result(CodeEnum.SUCCESS);
    }

}

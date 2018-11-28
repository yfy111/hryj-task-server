package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.DeptGrossProfitBalance;
import com.hryj.entity.bo.profit.PartyGrossProfitStatis;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.mapper.BackupDataMapper;
import com.hryj.mapper.DeptGrossProfitBalanceMapper;
import com.hryj.mapper.PartyGrossProfitStatisMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: GrossProfitStatisService
 * @description: 毛利统计service
 * @create 2018/8/16 17:11
 **/
@Slf4j
@Service
public class GrossProfitStatisService extends ServiceImpl<PartyGrossProfitStatisMapper,PartyGrossProfitStatis> {

    @Autowired
    private BackupDataMapper backupDataMapper;

    @Autowired
    private DeptGrossProfitBalanceMapper deptGrossProfitBalanceMapper;

    @Autowired
    private DeptGrossProfitBalanceService deptGrossProfitBalanceService;

    /**
     * @author 李道云
     * @methodName: statisDeptGrossProfit
     * @methodDesc: 统计部门毛利分润
     * @description:
     * @param: [statis_month]
     * @return com.hryj.common.Result
     * @create 2018-08-16 17:16
     **/
    public Result statisDeptGrossProfit(String statis_month){
        if(StrUtil.isEmpty(statis_month)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"统计月份不能为空");
        }
        //查询所有门店和仓库
        List<DeptGroup> deptGroupList = backupDataMapper.findAllStoreAndWhList();
        if(CollectionUtil.isNotEmpty(deptGroupList)){
            List<PartyGrossProfitStatis> partyGrossProfitStatisList = new ArrayList<>();
            List<DeptGrossProfitBalance> deptGrossProfitBalanceList = new ArrayList<>();
            for (DeptGroup deptGroup : deptGroupList){
                Long party_id = deptGroup.getId();
                String party_name = deptGroup.getDept_name();
                String dept_path = deptGroup.getDept_path();
                //统计门店或仓库当月的毛利
                Map<String,Object> gross_map = baseMapper.statisPartyGrossProfit(statis_month,party_id);
                BigDecimal total_gross_profit = (BigDecimal) gross_map.get("total_gross_profit");
                log.info("统计门店或仓库当月的毛利，party_id={},party_name={},total_gross_profit={}", party_id, party_name, total_gross_profit);
                EntityWrapper<PartyGrossProfitStatis> wrapper = new EntityWrapper<>();
                wrapper.eq("statis_month",statis_month);
                wrapper.eq("party_id",party_id);
                PartyGrossProfitStatis partyGrossProfitStatis = super.selectOne(wrapper);
                if(partyGrossProfitStatis ==null){
                    partyGrossProfitStatis = new PartyGrossProfitStatis();
                    partyGrossProfitStatis.setStatis_month(statis_month);
                    partyGrossProfitStatis.setParty_id(party_id);
                    partyGrossProfitStatis.setParty_name(party_name);
                    partyGrossProfitStatis.setDept_path(dept_path);
                }
                partyGrossProfitStatis.setTotal_gross_profit(total_gross_profit);
                partyGrossProfitStatis.setCreate_time(null);
                BigDecimal used_gross_profit = BigDecimal.ZERO;//已分配毛利
                //查询最新的部门分润配置
                List<DeptShareConfig> deptShareConfigList = backupDataMapper.findLatestShareConfigList(party_id);
                if(CollectionUtil.isNotEmpty(deptShareConfigList)){
                    for (DeptShareConfig deptShareConfig : deptShareConfigList){
                        Long staff_id = deptShareConfig.getStaff_id();
                        Long dept_id = deptShareConfig.getDept_id();
                        BigDecimal share_ratio = deptShareConfig.getShare_ratio();
                        //分润比例大于0才计算
                        if(NumberUtil.isGreater(share_ratio,BigDecimal.ZERO)){
                            //所分得的毛利
                            BigDecimal gross_profit_amt = NumberUtil.round(NumberUtil.mul(total_gross_profit,NumberUtil.div(share_ratio,100)),2);
                            //更新部门当月的毛利分润
                            DeptGrossProfitBalance deptGrossProfitBalanceWrapper = new DeptGrossProfitBalance();
                            deptGrossProfitBalanceWrapper.setBalance_month(statis_month);
                            deptGrossProfitBalanceWrapper.setDept_id(dept_id);
                            deptGrossProfitBalanceWrapper.setParty_id(party_id);
                            DeptGrossProfitBalance deptGrossProfitBalance = deptGrossProfitBalanceMapper.selectOne(deptGrossProfitBalanceWrapper);
                            if(deptGrossProfitBalance ==null){
                                DeptGroup deptGroup1 = backupDataMapper.getDeptGroupById(dept_id);
                                deptGrossProfitBalance = new DeptGrossProfitBalance();
                                deptGrossProfitBalance.setBalance_month(statis_month);
                                deptGrossProfitBalance.setStaff_id(staff_id);
                                deptGrossProfitBalance.setDept_id(dept_id);
                                deptGrossProfitBalance.setParty_id(party_id);
                                deptGrossProfitBalance.setDept_name(deptGroup1.getDept_name());
                                deptGrossProfitBalance.setDept_path(deptGroup1.getDept_path());
                            }
                            deptGrossProfitBalance.setGross_profit_amt(gross_profit_amt);
                            deptGrossProfitBalance.setCreate_time(null);
                            deptGrossProfitBalance.setUpdate_time(null);
                            log.info("更新部门当月的毛利分润，deptGrossProfitBalance = {} ", JSON.toJSON(deptGrossProfitBalance));
                            deptGrossProfitBalanceList.add(deptGrossProfitBalance);
                            used_gross_profit = NumberUtil.add(used_gross_profit,gross_profit_amt);
                        }
                    }
                }
                //门店或仓库的剩余毛利
                BigDecimal party_gross_profit = NumberUtil.sub(total_gross_profit,used_gross_profit);
                //更新门店或仓库当月的毛利分润
                DeptGrossProfitBalance deptGrossProfitBalanceWrapper = new DeptGrossProfitBalance();
                deptGrossProfitBalanceWrapper.setBalance_month(statis_month);
                deptGrossProfitBalanceWrapper.setDept_id(party_id);
                deptGrossProfitBalanceWrapper.setParty_id(party_id);
                DeptGrossProfitBalance deptGrossProfitBalance = deptGrossProfitBalanceMapper.selectOne(deptGrossProfitBalanceWrapper);
                if(deptGrossProfitBalance ==null){
                    deptGrossProfitBalance = new DeptGrossProfitBalance();
                    deptGrossProfitBalance.setBalance_month(statis_month);
                    deptGrossProfitBalance.setDept_id(party_id);
                    deptGrossProfitBalance.setParty_id(party_id);
                    deptGrossProfitBalance.setDept_name(party_name);
                    deptGrossProfitBalance.setDept_path(dept_path);
                }
                deptGrossProfitBalance.setGross_profit_amt(party_gross_profit);
                deptGrossProfitBalance.setCreate_time(null);
                deptGrossProfitBalance.setUpdate_time(null);
                log.info("更新门店或仓库当月的毛利分润，deptGrossProfitBalance = {} ", JSON.toJSON(deptGrossProfitBalance));
                deptGrossProfitBalanceList.add(deptGrossProfitBalance);
                partyGrossProfitStatisList.add(partyGrossProfitStatis);
            }
            //批量插入或更新部门毛利分润
            if(CollectionUtil.isNotEmpty(deptGrossProfitBalanceList)){
                deptGrossProfitBalanceService.insertOrUpdateBatch(deptGrossProfitBalanceList);
            }
            //批量插入或更新门店仓库的毛利统计
            if(CollectionUtil.isNotEmpty(partyGrossProfitStatisList)){
                super.insertOrUpdateBatch(partyGrossProfitStatisList);
            }
            log.info("批量插入或更新部门毛利分润,deptGrossProfitBalanceList.size=" + deptGrossProfitBalanceList.size());
            log.info("批量插入或更新门店仓库的毛利统计,partyGrossProfitStatisList.size=" + partyGrossProfitStatisList.size());
        }
        return new Result(CodeEnum.SUCCESS);
    }
}

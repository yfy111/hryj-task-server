package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.*;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.exception.BizException;
import com.hryj.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: OrderStatisService
 * @description: 订单统计service
 * @create 2018/7/14 15:52
 **/
@Slf4j
@Service
public class OrderStatisService {

    @Autowired
    private BackupDataMapper backupDataMapper;

    @Autowired
    private StaffOrderStatisMapper staffOrderStatisMapper;

    @Autowired
    private StoreOrderStatisMapper storeOrderStatisMapper;

    @Autowired
    private UserStatisMapper userStatisMapper;

    @Autowired
    private HelpOrderDetailMapper helpOrderDetailMapper;

    @Autowired
    private WhOrderStatisMapper whOrderStatisMapper;

    @Autowired
    private StaffDeptChangeRecordMapper staffDeptChangeRecordMapper;

    /**
     * @author 李道云
     * @methodName: statisWHOrderData
     * @methodDesc: 统计仓库订单数据
     * @description:
     * @param: [statis_date]
     * @return void
     * @create 2018-07-23 20:39
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result statisWHOrderData(String statis_date){
        if(StrUtil.isEmpty(statis_date)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"统计日期不能为空");
        }
        String today = DateUtil.today();
        //1、获取所有仓库列表
        List<Map> deptList = null;
        if(today.equals(statis_date)){
            deptList = backupDataMapper.findTodayAllWHList();
        }else{
            deptList = backupDataMapper.findAllWHList(statis_date);
            if(CollectionUtil.isEmpty(deptList)){
                throw new BizException("部门组织数据备份出现问题");
            }
        }
        //2、统计仓库的订单数据
        for (Map deptGroup: deptList){
            Long wh_id = (Long) deptGroup.get("dept_id");
            String wh_name = (String) deptGroup.get("dept_name");
            String dept_path = (String) deptGroup.get("dept_path");
            Map order_map = storeOrderStatisMapper.statisPartyOrderData(wh_id,statis_date);
            Integer wh_order_num = Integer.parseInt(order_map.get("party_order_num").toString());
            BigDecimal wh_order_amt = (BigDecimal) order_map.get("party_order_amt");
            BigDecimal wh_order_profit = (BigDecimal) order_map.get("party_order_profit");
            Integer new_trade_user_num = Integer.parseInt(order_map.get("new_trade_user_num").toString());
            Map order_prodcut_map = storeOrderStatisMapper.statisPartyOrderProductData(wh_id,statis_date);
            Integer order_product_num = Integer.parseInt(order_prodcut_map.get("order_product_num").toString());
            Map user_map = userStatisMapper.statisStoreReferralRegisterNum(wh_id,statis_date);
            Integer referral_register_num = Integer.parseInt(user_map.get("user_num").toString());
            WhOrderStatis wrapper = new WhOrderStatis();
            wrapper.setStatis_date(statis_date);
            wrapper.setWh_id(wh_id);
            WhOrderStatis whOrderStatis = whOrderStatisMapper.selectOne(wrapper);
            if(whOrderStatis ==null){
                whOrderStatis = new WhOrderStatis();
            }
            whOrderStatis.setStatis_date(statis_date);
            whOrderStatis.setWh_id(wh_id);
            whOrderStatis.setWh_name(wh_name);
            whOrderStatis.setDept_path(dept_path);
            whOrderStatis.setWh_order_num(wh_order_num);
            whOrderStatis.setWh_order_amt(wh_order_amt);
            whOrderStatis.setWh_order_profit(wh_order_profit);
            whOrderStatis.setWh_order_product_num(order_product_num);
            whOrderStatis.setReferral_register_num(referral_register_num);
            whOrderStatis.setNew_trade_user_num(new_trade_user_num);
            whOrderStatis.setCreate_time(DateUtil.date());
            if(whOrderStatis.getId() ==null){
                whOrderStatisMapper.insert(whOrderStatis);
            }else{
                whOrderStatisMapper.updateById(whOrderStatis);
            }
            log.info("保存仓库订单数据：whOrderStatis==={}",JSON.toJSONString(whOrderStatis));
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @methodName: statisStoreOrderData
     * @methodDesc: 统计门店订单数据
     * @description:
     * @param: [statis_date]
     * @return void
     * @create 2018-07-14 15:55
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result statisStoreOrderData(String statis_date){
        if(StrUtil.isEmpty(statis_date)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"统计日期不能为空");
        }
        String today = DateUtil.today();
        //1、获取所有门店列表
        List<Map> deptList = null;
        if(today.equals(statis_date)){
            deptList = backupDataMapper.findTodayAllStoreList();
        }else{
            deptList = backupDataMapper.findAllStoreList(statis_date);
            if(CollectionUtil.isEmpty(deptList)){
                throw new BizException("部门组织数据备份出现问题");
            }
        }
        //2、统计门店的订单数据
        for (Map deptGroup: deptList){
            Long store_id = (Long) deptGroup.get("dept_id");
            String dept_name = (String) deptGroup.get("dept_name");
            String dept_path = (String) deptGroup.get("dept_path");
            //2.1、配送单数据
            Map distribution_map = storeOrderStatisMapper.statisStoreDistributionData(store_id,statis_date);
            Integer distribution_num = Integer.parseInt(distribution_map.get("distribution_num").toString());
            Integer take_back_num = Integer.parseInt(distribution_map.get("take_back_num").toString());
            Integer timeout_distribution_num = Integer.parseInt(distribution_map.get("timeout_distribution_num").toString());
            BigDecimal distribution_cost = (BigDecimal) distribution_map.get("distribution_cost");
            //2.2、门店订单数据
            Map order_map = storeOrderStatisMapper.statisPartyOrderData(store_id,statis_date);
            Integer store_order_num = Integer.parseInt(order_map.get("party_order_num").toString());
            BigDecimal store_order_amt = (BigDecimal) order_map.get("party_order_amt");
            BigDecimal store_order_profit = (BigDecimal) order_map.get("party_order_profit");
            Integer new_trade_user_num = Integer.parseInt(order_map.get("new_trade_user_num").toString());
            //2.3、门店订单商品数据
            Map order_prodcut_map = storeOrderStatisMapper.statisPartyOrderProductData(store_id,statis_date);
            Integer order_product_num = Integer.parseInt(order_prodcut_map.get("order_product_num").toString());
            //2.4、门店代下单数据
            Map help_order_map = storeOrderStatisMapper.statisHelpOrderData(store_id,statis_date);
            Integer help_order_num = Integer.parseInt(help_order_map.get("help_order_num").toString());
            BigDecimal help_order_amt = (BigDecimal) help_order_map.get("help_order_amt");
            //2.5、门店代下单商品数据
            Map help_order_product_map = storeOrderStatisMapper.statisHelpOrderProductData(store_id,statis_date);
            Integer help_product_num = Integer.parseInt(help_order_product_map.get("help_product_num").toString());
            //2.6、门店推荐注册用户数
            Map user_map = userStatisMapper.statisStoreReferralRegisterNum(store_id,statis_date);
            Integer referral_register_num = Integer.parseInt(user_map.get("user_num").toString());
            //保存门店每日的订单统计数据
            StoreOrderStatis wrapper = new StoreOrderStatis();
            wrapper.setStatis_date(statis_date);
            wrapper.setStore_id(store_id);
            StoreOrderStatis storeOrderStatis = storeOrderStatisMapper.selectOne(wrapper);
            if(storeOrderStatis ==null){
                storeOrderStatis = new StoreOrderStatis();
            }
            storeOrderStatis.setStatis_date(statis_date);
            storeOrderStatis.setStore_id(store_id);
            storeOrderStatis.setStore_name(dept_name);
            storeOrderStatis.setDept_path(dept_path);
            storeOrderStatis.setDistribution_num(distribution_num);
            storeOrderStatis.setTake_back_num(take_back_num);
            storeOrderStatis.setTimeout_distribution_num(timeout_distribution_num);
            storeOrderStatis.setDistribution_cost(distribution_cost);
            storeOrderStatis.setHelp_order_num(help_order_num);
            storeOrderStatis.setHelp_order_amt(help_order_amt);
            storeOrderStatis.setHelp_order_product_num(help_product_num);
            storeOrderStatis.setStore_order_num(store_order_num);
            storeOrderStatis.setStore_order_amt(store_order_amt);
            storeOrderStatis.setStore_order_profit(store_order_profit);
            storeOrderStatis.setStore_order_product_num(order_product_num);
            storeOrderStatis.setNew_trade_user_num(new_trade_user_num);
            storeOrderStatis.setReferral_register_num(referral_register_num);
            storeOrderStatis.setCreate_time(DateUtil.date());
            if(storeOrderStatis.getId() ==null){
                storeOrderStatisMapper.insert(storeOrderStatis);
            }else{
                storeOrderStatisMapper.updateById(storeOrderStatis);
            }
            log.info("保存门店订单数据：storeOrderStatis==={}",JSON.toJSONString(storeOrderStatis));
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 李道云
     * @methodName: statisStaffOrderData
     * @methodDesc: 统计员工订单数据
     * @description:
     * @param: [statis_date]
     * @return void
     * @create 2018-07-14 15:55
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result statisStaffOrderData(String statis_date){
        if(StrUtil.isEmpty(statis_date)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"统计日期不能为空");
        }
        String today = DateUtil.today();
        //1、获取所有员工列表
        List<Map> staffList = null;
        if(today.equals(statis_date)){
            staffList = backupDataMapper.findTodayAllStaffList();
        }else{
            staffList = backupDataMapper.findAllStaffList(statis_date);
            if(CollectionUtil.isEmpty(staffList)){
                throw new BizException("部门组织数据备份出现问题");
            }
        }
        //2、统计员工的订单数据
        for (Map staffUser : staffList){
            Long staff_id = (Long) staffUser.get("staff_id");//员工id
            Long store_id = (Long) staffUser.get("dept_id");//员工部门id
            String dept_name = (String) staffUser.get("dept_name");
            String dept_path = (String) staffUser.get("dept_path");
            String staff_job = (String) staffUser.get("staff_job");//员工角色
            Boolean staff_status = (Boolean) staffUser.get("staff_status");//员工状态
            BigDecimal service_ratio = (BigDecimal) staffUser.get("service_ratio");//服务提成比例
            //离职员工，如果不是当天离职的，直接跳过
            if(!staff_status){
                StaffDeptChangeRecord staffDeptChangeRecord = staffDeptChangeRecordMapper.findStaffDeptChangeRecord(staff_id,statis_date);
                if(staffDeptChangeRecord==null){
                    continue;
                }
            }
            //2.1、配送单数据
            Map distribution_map = staffOrderStatisMapper.statisStaffDistributionData(staff_id,statis_date);
            Integer distribution_num = Integer.parseInt(distribution_map.get("distribution_num").toString());
            Integer take_back_num = Integer.parseInt(distribution_map.get("take_back_num").toString());
            Integer timeout_distribution_num = Integer.parseInt(distribution_map.get("timeout_distribution_num").toString());
            BigDecimal distribution_profit = (BigDecimal) distribution_map.get("distribution_profit");
            //2.2、代下单数据
            BigDecimal help_order_ratio = BigDecimal.ZERO;//代下单提成比例
            Integer help_order_num = 0;//代下订单数量
            Integer help_order_product_num = 0;//代下订单商品数量
            BigDecimal help_order_amt = BigDecimal.ZERO;//代下订单总金额
            BigDecimal help_order_profit = BigDecimal.ZERO;//代下订单分润
            List<Map> help_order_list = staffOrderStatisMapper.findHelpOrderList(staff_id,statis_date);
            log.info("员工id：staff_id=={},代下单数据：help_order_list==={}",staff_id,JSON.toJSONString(help_order_list));
            if(CollectionUtil.isNotEmpty(help_order_list)){
                help_order_num = help_order_list.size();
                for (Map help_order_map : help_order_list){
                    Long order_id = (Long) help_order_map.get("order_id");
                    Long help_store_id = (Long) help_order_map.get("help_store_id");//代下单门店id
                    String pay_date = (String) help_order_map.get("pay_date");//支付日期
                    BigDecimal one_order_amt = (BigDecimal) help_order_map.get("pay_amt");
                    help_order_amt = NumberUtil.add(help_order_amt, one_order_amt);
                    BigDecimal one_order_profit = (BigDecimal) help_order_map.get("order_profit");
                    //查询订单支付当天的代下单提成比例，如果没有就取订单完成当天的代下单提成比例
                    BackupStaffDeptRelation bsdr = backupDataMapper.findStaffDeptRelationByStaffId(pay_date,staff_id);
                    if(bsdr !=null && NumberUtil.isGreater(bsdr.getHelp_order_ratio(),BigDecimal.ZERO)){
                        help_order_ratio = bsdr.getHelp_order_ratio();
                    }else{
                        help_order_ratio = (BigDecimal) staffUser.get("help_order_ratio");
                    }
                    //代下单商品总数
                    Map product_map = staffOrderStatisMapper.findOrderProductData(order_id);
                    Integer one_product_num = Integer.parseInt(product_map.get("product_num").toString());
                    help_order_product_num = help_order_product_num + one_product_num;
                    BigDecimal one_help_order_profit = BigDecimal.ZERO;
                    if(NumberUtil.isGreater(one_order_profit,BigDecimal.ZERO)){//订单毛利大于0才计算
                        //角色为店员，订单毛利需要跟店长一起分润
                        if(CodeCache.getValueByKey("StaffJob","S02").equals(staff_job) && NumberUtil.isGreater(help_order_ratio,BigDecimal.ZERO)){
                            one_help_order_profit = NumberUtil.round(NumberUtil.mul(one_order_profit,NumberUtil.div(help_order_ratio,100)),4);
                            //剩余的毛利分润给自己门店的店长
                            BackupStaffDeptRelation storeManager = backupDataMapper.findStoreManagerByStoreId(statis_date,help_store_id);
                            BigDecimal store_help_order_profit = NumberUtil.sub(one_order_profit,one_help_order_profit);
                            if(storeManager !=null){
                                //保存代下单数据明细
                                HelpOrderDetail wrapper = new HelpOrderDetail();
                                wrapper.setStatis_date(statis_date);
                                wrapper.setOrder_id(order_id);
                                HelpOrderDetail helpOrderDetail = helpOrderDetailMapper.selectOne(wrapper);
                                if(helpOrderDetail ==null){
                                    helpOrderDetail = new HelpOrderDetail();
                                }
                                helpOrderDetail.setStatis_date(statis_date);
                                helpOrderDetail.setOrder_id(order_id);
                                helpOrderDetail.setOrder_amt(one_order_amt);
                                helpOrderDetail.setOrder_profit(one_order_profit);
                                helpOrderDetail.setOrder_product_num(one_product_num);
                                helpOrderDetail.setHelp_order_ratio(help_order_ratio);
                                helpOrderDetail.setStaff_id(staff_id);
                                helpOrderDetail.setStaff_order_profit(one_help_order_profit);
                                helpOrderDetail.setStore_id(storeManager.getDept_id());
                                helpOrderDetail.setStore_staff_id(storeManager.getStaff_id());
                                helpOrderDetail.setStore_staff_order_profit(store_help_order_profit);
                                helpOrderDetail.setCreate_time(DateUtil.date());
                                if(helpOrderDetail.getId() ==null){
                                    helpOrderDetailMapper.insert(helpOrderDetail);
                                }else{
                                    helpOrderDetailMapper.updateById(helpOrderDetail);
                                }
                                log.info("代下单明细数据：helpOrderDetail==={}",JSON.toJSONString(helpOrderDetail));
                            }else{
                                one_help_order_profit = one_order_profit;
                            }
                        }else if(CodeCache.getValueByKey("StaffJob","S01").equals(staff_job)){
                            //角色为店长，代下单分润为订单全部毛利
                            one_help_order_profit = one_order_profit;
                        }else{
                            one_help_order_profit = one_order_profit;
                        }
                    }
                    help_order_profit = NumberUtil.add(help_order_profit,one_help_order_profit);
                }
            }
            //2.3、统计员工的推荐注册用户数
            Map user_map = userStatisMapper.statisStaffReferralRegisterNum(staff_id,statis_date);
            Integer referral_register_num = Integer.parseInt(user_map.get("user_num").toString());
            //2.4、计算员工的服务分润(排除代下单数据)
            Map<String,Object> service_order_map = staffOrderStatisMapper.statisServiceOrderProfit(store_id,statis_date);
            BigDecimal order_profit = (BigDecimal) service_order_map.get("order_profit");
            BigDecimal staff_service_profit = BigDecimal.ZERO;
            if(statis_date.equals(today)){
                StoreInfo storeInfo = backupDataMapper.getStoreInfoById(store_id);
                if(storeInfo !=null){
                    String service_rule = storeInfo.getService_rule();//服务提成规则
                    if(CodeCache.getValueByKey("ServiceRule","S01").equals(service_rule)){//自定义
                        staff_service_profit = NumberUtil.mul(order_profit,NumberUtil.div(service_ratio,100));
                    }else{//平均分配
                        Long staff_num = backupDataMapper.countDeptTodayStaffNum(store_id);//如果为今天就查询今天的员工数量
                        staff_service_profit = NumberUtil.div(order_profit,staff_num,2);
                    }
                }
            }else{
                BackupDeptGroup backupDeptGroup = backupDataMapper.findBackupDeptGroup(statis_date,store_id);
                if(backupDeptGroup !=null){
                    String service_rule = backupDeptGroup.getService_rule();//服务提成规则
                    if(CodeCache.getValueByKey("ServiceRule","S01").equals(service_rule)){//自定义
                        staff_service_profit = NumberUtil.mul(order_profit,NumberUtil.div(service_ratio,100));
                    }else{//平均分配
                        Long staff_num = backupDataMapper.countDeptStaffNum(statis_date,store_id);//完成时间当天的员工数量
                        staff_service_profit = NumberUtil.div(order_profit,staff_num,2);
                    }
                }
            }
            //保存员工每日的订单统计数据
            StaffOrderStatis wrapper = new StaffOrderStatis();
            wrapper.setStatis_date(statis_date);
            wrapper.setStaff_id(staff_id);
            StaffOrderStatis staffOrderStatis = staffOrderStatisMapper.selectOne(wrapper);
            if(staffOrderStatis ==null){
                staffOrderStatis = new StaffOrderStatis();
            }
            staffOrderStatis.setStatis_date(statis_date);
            staffOrderStatis.setStaff_id(staff_id);
            staffOrderStatis.setStore_id(store_id);
            staffOrderStatis.setDept_name(dept_name);
            staffOrderStatis.setDept_path(dept_path);
            staffOrderStatis.setDistribution_num(distribution_num);
            staffOrderStatis.setTake_back_num(take_back_num);
            staffOrderStatis.setTimeout_distribution_num(timeout_distribution_num);
            staffOrderStatis.setDistribution_profit(distribution_profit);
            staffOrderStatis.setHelp_order_num(help_order_num);
            staffOrderStatis.setHelp_order_amt(help_order_amt);
            staffOrderStatis.setHelp_order_product_num(help_order_product_num);
            staffOrderStatis.setHelp_order_profit(NumberUtil.round(help_order_profit,2));
            staffOrderStatis.setService_profit(NumberUtil.round(staff_service_profit,2));
            staffOrderStatis.setReferral_register_num(referral_register_num);
            staffOrderStatis.setCreate_time(DateUtil.date());
            if(staffOrderStatis.getId() ==null){
                staffOrderStatisMapper.insert(staffOrderStatis);
            }else{
                staffOrderStatisMapper.updateById(staffOrderStatis);
            }
            log.info("保存员工订单数据：staffOrderStatis==={}",JSON.toJSONString(staffOrderStatis));
        }
        return new Result(CodeEnum.SUCCESS);
    }

}

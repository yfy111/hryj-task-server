package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffOrderStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: StaffOrderStatisMapper
 * @description: 员工订单统计mapper
 * @create 2018/7/14 18:57
 **/
@Component
public interface StaffOrderStatisMapper extends BaseMapper<StaffOrderStatis> {

    /**
     * @author 李道云
     * @methodName: findHelpOrderList
     * @methodDesc: 查询员工代下单订单数据
     * @description:
     * @param: [staff_id, statis_date]
     * @return java.util.List<java.util.Map>
     * @create 2018-07-16 20:24
     **/
    List<Map> findHelpOrderList(@Param("staff_id") Long staff_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: findOrderProductData
     * @methodDesc: 查询订单商品数据
     * @description:
     * @param: [order_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 11:30
     **/
    Map<String,Object> findOrderProductData(@Param("order_id") Long order_id);

    /**
     * @author 李道云
     * @methodName: statisStaffDistributionData
     * @methodDesc: 统计员工配送单数据
     * @description:
     * @param: [staff_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 13:18
     **/
    Map<String,Object> statisStaffDistributionData(@Param("staff_id") Long staff_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: calStaffMonthProfit
     * @methodDesc: 计算员工当月的分润
     * @description:
     * @param: [balance_month, staff_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 16:59
     **/
    Map<String,Object> calStaffMonthProfit(@Param("balance_month") String balance_month, @Param("staff_id") Long staff_id);

    /**
     * @author 李道云
     * @methodName: statisServiceOrderProfit
     * @methodDesc: 统计服务订单毛利
     * @description:
     * @param: [party_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-08-09 11:05
     **/
    Map<String,Object> statisServiceOrderProfit(@Param("party_id") Long party_id, @Param("statis_date") String statis_date);

}

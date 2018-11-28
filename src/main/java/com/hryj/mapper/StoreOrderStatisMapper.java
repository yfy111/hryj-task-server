package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StoreOrderStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: StoreOrderStatisMapper
 * @description: 门店订单数据统计
 * @create 2018/7/16 22:37
 **/
@Component
public interface StoreOrderStatisMapper extends BaseMapper<StoreOrderStatis> {

    /**
     * @author 李道云
     * @methodName: statisStoreDistributionData
     * @methodDesc: 统计门店配送单数据
     * @description:
     * @param: [store_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 23:07
     **/
    Map<String,Object> statisStoreDistributionData(@Param("store_id") Long store_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: statisPartyOrderData
     * @methodDesc: 统计门店或仓库订单数据
     * @description:
     * @param: [store_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 22:57
     **/
    Map<String,Object> statisPartyOrderData(@Param("party_id") Long party_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: statisPartyOrderProductData
     * @methodDesc: 统计门店或仓库订单商品数据
     * @description:
     * @param: [party_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 22:57
     **/
    Map<String,Object> statisPartyOrderProductData(@Param("party_id") Long party_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: statisHelpOrderData
     * @methodDesc: 统计代下单订单数据
     * @description:
     * @param: [party_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-08-07 10:08
     **/
    Map<String,Object> statisHelpOrderData(@Param("party_id") Long party_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: statisHelpOrderProductData
     * @methodDesc: 统计代下单商品数据
     * @description:
     * @param: [party_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-08-07 10:34
     **/
    Map<String,Object> statisHelpOrderProductData(@Param("party_id") Long party_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: calStoreMonthDistributionCost
     * @methodDesc: 计算门店当月的配送成本
     * @description:
     * @param: [store_id, balance_month]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-18 22:01
     **/
    Map<String,Object> calStoreMonthDistributionCost(@Param("store_id") Long store_id, @Param("balance_month") String balance_month);

    /**
     * @author 李道云
     * @methodName: calStoreDayDistributionCost
     * @methodDesc: 计算门店当天的配送成本
     * @description:
     * @param: [store_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-18 22:01
     **/
    Map<String,Object> calStoreDayDistributionCost(@Param("store_id") Long store_id, @Param("statis_date") String statis_date);
}

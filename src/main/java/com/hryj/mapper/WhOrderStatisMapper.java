package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.WhOrderStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: WhOrderStatisMapper
 * @description: 仓库订单数据统计
 * @create 2018/7/23 21:28
 **/
@Component
public interface WhOrderStatisMapper extends BaseMapper<WhOrderStatis> {

    /**
     * @author 李道云
     * @methodName: calWHMonthProfit
     * @methodDesc: 计算仓库当月的分润
     * @description:
     * @param: [balance_month, wh_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 16:59
     **/
    Map<String,Object> calWHMonthProfit(@Param("balance_month") String balance_month, @Param("wh_id") Long wh_id);
}

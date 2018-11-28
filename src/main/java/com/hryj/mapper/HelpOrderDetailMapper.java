package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.HelpOrderDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: HelpOrderDetailMapper
 * @description: 代下单数据明细
 * @create 2018/7/16 21:31
 **/
@Component
public interface HelpOrderDetailMapper extends BaseMapper<HelpOrderDetail> {

    /**
     * @author 李道云
     * @methodName: calStoreManagerHelpOrderPorfit
     * @methodDesc: 计算店长代下单分润
     * @description:
     * @param: [balance_month, staff_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 17:38
     **/
    Map<String,Object> calStoreManagerHelpOrderPorfit(@Param("balance_month") String balance_month, @Param("staff_id") Long staff_id);

}

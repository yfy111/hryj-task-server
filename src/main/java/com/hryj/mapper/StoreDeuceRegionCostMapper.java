package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StoreDeuceRegionCost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: StoreDeuceRegionCostMapper
 * @description: 门店平摊区域公司的成本
 * @create 2018/7/19 10:39
 **/
@Component
public interface StoreDeuceRegionCostMapper extends BaseMapper<StoreDeuceRegionCost> {
    /**
     * @author 李道云
     * @methodName: calStoreRegionMonthCost
     * @methodDesc: 计算门店当月平摊区域公司的成本总和
     * @description:
     * @param: [balance_month, store_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 11:09
     **/
    Map<String,Object> calStoreRegionMonthCost(@Param("balance_month") String balance_month, @Param("store_id") Long store_id);

    /**
     * @author 李道云
     * @methodName: findStoreDeuceRegionCost
     * @methodDesc: 查询门店平摊区域公司的成本
     * @description:
     * @param: [cal_date, store_id, region_id]
     * @return com.hryj.entity.bo.profit.StoreDeuceRegionCost
     * @create 2018-08-03 11:18
     **/
    StoreDeuceRegionCost findStoreDeuceRegionCost(@Param("cal_date") String cal_date, @Param("store_id") Long store_id, @Param("region_id") Long region_id);


}

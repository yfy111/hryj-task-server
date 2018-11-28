package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.RegionProfitDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: RegionProfitDetailMapper
 * @description: 区域公司分润明细
 * @create 2018/7/19 22:42
 **/
@Component
public interface RegionProfitDetailMapper extends BaseMapper<RegionProfitDetail> {

    /**
     * @author 李道云
     * @methodName: calRegionBalanceProfit
     * @methodDesc: 计算区域公司结算分润
     * @description:
     * @param: [balance_month, region_id, dept_id, staff_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-20 15:18
     **/
    Map<String,Object> calRegionBalanceProfit(@Param("balance_month")String balance_month, @Param("region_id") Long region_id,
                                              @Param("dept_id") Long dept_id, @Param("staff_id") Long staff_id);
    /**
     * @author 李道云
     * @methodName: calGroupBalanceProfit
     * @methodDesc: 计算集团结算分润
     * @description:
     * @param: [balance_month, dept_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-23 22:44
     **/
    Map<String,Object> calGroupBalanceProfit(@Param("balance_month")String balance_month,@Param("dept_id") Long dept_id);

}

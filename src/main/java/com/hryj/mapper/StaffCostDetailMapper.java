package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffCostDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: StaffCostDetailMapper
 * @description: 员工成本明细mapper
 * @create 2018/7/19 14:43
 **/
@Component
public interface StaffCostDetailMapper extends BaseMapper<StaffCostDetail> {

    /**
     * @author 李道云
     * @methodName: calStaffMonthCost
     * @methodDesc: 计算员工当月的成本总和
     * @description:
     * @param: [balance_month, region_id, store_id, staff_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-20 9:55
     **/
    Map<String,Object> calStaffMonthCost(@Param("balance_month")String balance_month, @Param("region_id") Long region_id,
                                         @Param("store_id") Long store_id, @Param("staff_id") Long staff_id);

}

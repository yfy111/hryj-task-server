package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffBalanceSummary;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: StaffBalanceSummaryMapper
 * @description: 员工结算汇总mapper
 * @create 2018/7/19 15:57
 **/
@Component
public interface StaffBalanceSummaryMapper extends BaseMapper<StaffBalanceSummary> {

    /**
     * @author 李道云
     * @methodName: findStaffBalanceSummary
     * @methodDesc: 查询员工结算汇总表
     * @description:
     * @param: [balance_month, region_id, staff_id]
     * @return com.hryj.entity.bo.profit.StaffBalanceSummary
     * @create 2018-07-19 16:27
     **/
    StaffBalanceSummary findStaffBalanceSummary(@Param("balance_month") String balance_month, @Param("region_id") Long region_id, @Param("staff_id") Long staff_id);

}

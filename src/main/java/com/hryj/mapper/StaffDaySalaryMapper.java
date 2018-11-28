package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffDaySalary;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: StaffDaySalaryMapper
 * @description: 员工日工资mapper
 * @create 2018/7/17 19:35
 **/
@Component
public interface StaffDaySalaryMapper extends BaseMapper<StaffDaySalary> {

    /**
     * @author 李道云
     * @methodName: calStaffMonthSalary
     * @methodDesc: 计算员工当月工资成本
     * @description:
     * @param: [balance_month, staff_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-18 19:50
     **/
    Map<String,Object> calStaffMonthSalary(@Param("balance_month") String balance_month, @Param("staff_id") Long staff_id);

    /**
     * @author 李道云
     * @methodName: calStoreMonthSalary
     * @methodDesc: 计算门店当月工资成本
     * @description:
     * @param: [balance_month, store_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-18 21:55
     **/
    Map<String,Object> calStoreMonthSalary(@Param("balance_month") String balance_month, @Param("store_id") Long store_id);

    /**
     * @author 李道云
     * @methodName: calRegionDaySalary
     * @methodDesc: 计算区域公司当天的工资成本，不包含门店仓库
     * @description:
     * @param: [cal_date, region_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 10:29
     **/
    Map<String,Object> calRegionDaySalary(@Param("cal_date") String cal_date, @Param("region_id") Long region_id);

    /**
     * @author 李道云
     * @methodName: calStoreDaySalary
     * @methodDesc: 计算门店当天的工资成本
     * @description:
     * @param: [cal_date, store_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-19 14:32
     **/
    Map<String,Object> calStoreDaySalary(@Param("cal_date") String cal_date, @Param("store_id") Long store_id);

}

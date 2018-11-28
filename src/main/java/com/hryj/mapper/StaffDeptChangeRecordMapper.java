package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffDeptChangeRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: StaffDeptChangeRecordMapper
 * @description: 员工部门变动记录
 * @create 2018/7/19 22:43
 **/
@Component
public interface StaffDeptChangeRecordMapper extends BaseMapper<StaffDeptChangeRecord> {

    /**
     * @author 李道云
     * @methodName: findStaffDeptChangeRecordList
     * @methodDesc: 查询当月的员工部门变动记录
     * @description: (转移成本未结算)
     * @param: [balance_month,change_type]
     * @return java.util.List<com.hryj.entity.bo.profit.StaffDeptChangeRecord>
     * @create 2018-07-20 13:23
     **/
    List<StaffDeptChangeRecord> findStaffDeptChangeRecordList(@Param("balance_month") String balance_month,@Param("change_type") String change_type);

    /**
     * @author 李道云
     * @methodName: findStaffDeptChangeRecord
     * @methodDesc: 查询员工的部门变动记录
     * @description:
     * @param: [staff_id, change_date]
     * @return com.hryj.entity.bo.profit.StaffDeptChangeRecord
     * @create 2018-07-25 19:48
     **/
    StaffDeptChangeRecord findStaffDeptChangeRecord(@Param("staff_id") Long staff_id, @Param("change_date") String change_date);

}

package com.hryj.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: UserStatisMapper
 * @description: 用户统计mapper
 * @create 2018/7/14 18:57
 **/
@Component
public interface UserStatisMapper {

    /**
     * @author 李道云
     * @methodName: statisStaffReferralRegisterNum
     * @methodDesc: 统计员工推荐注册用户数
     * @description:
     * @param: [staff_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 22:13
     **/
    Map<String,Object> statisStaffReferralRegisterNum(@Param("staff_id") Long staff_id, @Param("statis_date") String statis_date);

    /**
     * @author 李道云
     * @methodName: statisStoreReferralRegisterNum
     * @methodDesc: 统计门店推荐注册用户数
     * @description:
     * @param: [store_id, statis_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 22:13
     **/
    Map<String,Object> statisStoreReferralRegisterNum(@Param("store_id") Long store_id, @Param("statis_date") String statis_date);



}

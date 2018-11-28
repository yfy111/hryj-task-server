package com.hryj.mapper;

import com.hryj.entity.bo.profit.BackupDeptGroup;
import com.hryj.entity.bo.profit.BackupDeptShareConfig;
import com.hryj.entity.bo.profit.BackupStaffDeptRelation;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.entity.bo.staff.store.StoreInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: BackupDataMapper
 * @description: 备份数据mapper
 * @create 2018/7/11 22:39
 **/
@Component
public interface BackupDataMapper {

    /**
     * @author 李道云
     * @methodName: backupDeptCostData
     * @methodDesc: 备份部门成本数据
     * @description:
     * @param: [backup_month]
     * @return void
     * @create 2018-07-11 22:45
     **/
    void backupDeptCostData(@Param("backup_month") String backup_month);

    /**
     * @author 李道云
     * @methodName: backupDeptShareConfig
     * @methodDesc: 备份部门分润配置
     * @description:
     * @param: [backup_month]
     * @return void
     * @create 2018-07-11 23:09
     **/
    void backupDeptShareConfig(@Param("backup_month") String backup_month);

    /**
     * @author 李道云
     * @methodName: backupDeptGroup
     * @methodDesc: 备份部门组织数据
     * @description:
     * @param: [backup_date]
     * @return void
     * @create 2018-07-11 23:18
     **/
    void backupDeptGroup(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: backupStaffDeptRelation
     * @methodDesc: 备份员工部门组织关系
     * @description:
     * @param: [backup_date]
     * @return void
     * @create 2018-07-11 23:27
     **/
    void backupStaffDeptRelation(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: findAllStaffList
     * @methodDesc: 查询当天的所有员工
     * @description:
     * @param: [backup_date]
     * @return java.util.List<java.util.Map>
     * @create 2018-07-14 18:44
     **/
    List<Map> findAllStaffList(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: findTodayAllStaffList
     * @methodDesc: 查询今天所有的员工
     * @description:
     * @param: []
     * @return java.util.List<java.util.Map>
     * @create 2018-08-06 18:52
     **/
    List<Map> findTodayAllStaffList();

    /**
     * @author 李道云
     * @methodName: findStoreManagerByStoreId
     * @methodDesc: 根据门店id查询店长信息
     * @description:
     * @param: [backup_date,store_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-16 10:53
     **/
    BackupStaffDeptRelation findStoreManagerByStoreId(@Param("backup_date") String backup_date, @Param("store_id") Long store_id);

    /**
     * @author 李道云
     * @methodName: findStaffDeptRelationByStaffId
     * @methodDesc: 查询员工当天的部门关系
     * @description:
     * @param: [backup_date, staff_id]
     * @return com.hryj.entity.bo.profit.BackupStaffDeptRelation
     * @create 2018-07-30 21:56
     **/
    BackupStaffDeptRelation findStaffDeptRelationByStaffId(@Param("backup_date") String backup_date, @Param("staff_id") Long staff_id);

    /**
     * @author 李道云
     * @methodName: findAllWHList
     * @methodDesc: 查询所有的仓库列表
     * @description:
     * @param: [backup_date]
     * @return java.util.List<java.util.Map>
     * @create 2018-07-16 15:32
     **/
    List<Map> findAllWHList(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: findTodayAllWHList
     * @methodDesc: 查询今天所有的仓库列表
     * @description:
     * @param: []
     * @return java.util.List<java.util.Map>
     * @create 2018-08-06 18:37
     **/
    List<Map> findTodayAllWHList();

    /**
     * @author 李道云
     * @methodName: findAllStoreList
     * @methodDesc: 查询所有的门店列表
     * @description:
     * @param: [backup_date]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupDeptGroup>
     * @create 2018-07-16 15:32
     **/
    List<Map> findAllStoreList(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: findTodayAllStoreList
     * @methodDesc: 查询今天所有的门店列表
     * @description:
     * @param: []
     * @return java.util.List<java.util.Map>
     * @create 2018-08-06 18:44
     **/
    List<Map> findTodayAllStoreList();

    /**
     * @author 李道云
     * @methodName: countStoreStaffNum
     * @methodDesc: 计算部门下员工数量
     * @description:
     * @param: [backup_date, dept_id]
     * @return java.lang.Integer
     * @create 2018-07-17 14:22
     **/
    Long countDeptStaffNum(@Param("backup_date") String backup_date, @Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: countDeptTodayStaffNum
     * @methodDesc: 计算部门下员工数量
     * @description:
     * @param: [dept_id]
     * @return java.lang.Integer
     * @create 2018-07-17 14:22
     **/
    Long countDeptTodayStaffNum(@Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: findMonthRegionList
     * @methodDesc: 查询当月所有区域公司列表
     * @description:
     * @param: [balance_month]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-17 19:39
     **/
    List<Map> findMonthRegionList(@Param("balance_month") String balance_month);

    /**
     * @author 李道云
     * @methodName: findDayRegionList
     * @methodDesc: 查询当天所有区域公司列表
     * @description:
     * @param: [backup_date]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-17 19:39
     **/
    List<Map> findDayRegionList(@Param("backup_date") String backup_date);

    /**
     * @author 李道云
     * @methodName: findAllWHDeptList
     * @methodDesc: 查询当月所有仓库列表
     * @description:
     * @param: [balance_month]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-17 19:39
     **/
    List<Map> findAllWHDeptList(@Param("balance_month") String balance_month);

    /**
     * @author 李道云
     * @methodName: findRegionDayStaffList
     * @methodDesc: 查询区域公司当天的员工列表
     * @description:
     * @param: [backup_date, dept_path]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupStaffDeptRelation>
     * @create 2018-07-17 19:50
     **/
    List<BackupStaffDeptRelation> findRegionDayStaffList(@Param("backup_date") String backup_date, @Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: findRegionMonthStaffList
     * @methodDesc: 查询区域公司当月的员工列表
     * @description:
     * @param: [balance_month, dept_path, region_id]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupStaffDeptRelation>
     * @create 2018-07-17 19:50
     **/
    List<BackupStaffDeptRelation> findRegionMonthStaffList(@Param("balance_month") String balance_month, @Param("dept_path") String dept_path, @Param("dept_type") String dept_type);

    /**
     * @author 李道云
     * @methodName: findStoreDayStaffList
     * @methodDesc: 查询门店当天的员工列表
     * @description:
     * @param: [backup_date, store_id]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupStaffDeptRelation>
     * @create 2018-07-19 13:07
     **/
    List<BackupStaffDeptRelation> findStoreDayStaffList(@Param("backup_date") String backup_date, @Param("store_id") Long store_id);

    /**
     * @author 李道云
     * @methodName: findRegionStoreList
     * @methodDesc: 查询区域公司当天的分摊成本的门店列表
     * @description:
     * @param: [backup_date, dept_path]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupDeptGroup>
     * @create 2018-07-18 20:43
     **/
    List<BackupDeptGroup> findRegionDayStoreList(@Param("backup_date") String backup_date, @Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: findRegionMonthStoreList
     * @methodDesc: 查询区域公司当月的门店列表
     * @description:
     * @param: [balance_month, dept_path]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupDeptGroup>
     * @create 2018-07-18 20:43
     **/
    List<BackupDeptGroup> findRegionMonthStoreList(@Param("balance_month") String balance_month, @Param("dept_path") String dept_path);

    /**
     * @author 李道云
     * @methodName: sumDeptFixedCost
     * @methodDesc: 计算部门的固定成本
     * @description:
     * @param: [backup_month, dept_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-18 21:37
     **/
    Map<String,Object> calDeptFixedCost(@Param("backup_month") String backup_month, @Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: countOpenDays
     * @methodDesc: 计算部门当月的实际天数
     * @description:
     * @param: [balance_month, dept_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-31 20:46
     **/
    Map<String,Object> countOpenDays(@Param("balance_month") String balance_month, @Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: findMonthFirstBackupDeptGroup
     * @methodDesc: 查询当月第一条备份
     * @description:
     * @param: [balance_month, dept_id]
     * @return com.hryj.entity.bo.profit.BackupDeptGroup
     * @create 2018-08-03 14:35
     **/
    BackupDeptGroup findMonthFirstBackupDeptGroup(@Param("balance_month") String balance_month, @Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: findDeptShareConfigList
     * @methodDesc: 查询部门分润配置表
     * @description:
     * @param: [backup_month, store_id]
     * @return java.util.List<com.hryj.entity.bo.profit.BackupDeptShareConfig>
     * @create 2018-07-20 14:15
     **/
    List<BackupDeptShareConfig> findDeptShareConfigList(@Param("backup_month") String backup_month, @Param("store_id") Long store_id);

    /**
     * @author 李道云
     * @methodName: findBackupDeptGroup
     * @methodDesc: 查询部门组织备份
     * @description:
     * @param: [backup_date, dept_id]
     * @return com.hryj.entity.bo.profit.BackupDeptGroup
     * @create 2018-07-31 21:16
     **/
    BackupDeptGroup findBackupDeptGroup(@Param("backup_date") String backup_date, @Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: findAllStoreAndWhList
     * @methodDesc: 查询所有的门店和仓库列表
     * @description:
     * @param: []
     * @return java.util.List<com.hryj.entity.bo.staff.dept.DeptGroup>
     * @create 2018-08-16 17:41
     **/
    List<DeptGroup> findAllStoreAndWhList();

    /**
     * @author 李道云
     * @methodName: findLatestShareConfigList
     * @methodDesc: 查询最新的部门分润配置表
     * @description:
     * @param: [party_id]
     * @return java.util.List<com.hryj.entity.bo.staff.dept.DeptShareConfig>
     * @create 2018-08-17 9:36
     **/
    List<DeptShareConfig> findLatestShareConfigList(@Param("party_id") Long party_id);

    /**
     * @author 李道云
     * @methodName: getDeptGroupById
     * @methodDesc: 根据id查询部门组织
     * @description:
     * @param: [dept_id]
     * @return com.hryj.entity.bo.staff.dept.DeptGroup
     * @create 2018-08-17 10:13
     **/
    DeptGroup getDeptGroupById(@Param("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: getStoreInfoById
     * @methodDesc: 根据门店id获取门店信息
     * @description:
     * @param: [store_id]
     * @return com.hryj.entity.bo.staff.store.StoreInfo
     * @create 2018-08-17 13:55
     **/
    StoreInfo getStoreInfoById(@Param("store_id") Long store_id);

}

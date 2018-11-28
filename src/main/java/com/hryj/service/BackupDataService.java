package com.hryj.service;

import com.hryj.mapper.BackupDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 李道云
 * @className: BackupDataService
 * @description: 备份数据service
 * @create 2018/7/11 23:01
 **/
@Service
public class BackupDataService {

    @Autowired
    private BackupDataMapper backupDataMapper;

    /**
     * @author 李道云
     * @methodName: backupDeptCostData
     * @methodDesc: 备份部门成本数据
     * @description:
     * @param: [backup_month]
     * @return void
     * @create 2018-07-11 23:03
     **/
    @Transactional(rollbackFor = Exception.class)
    public void backupDeptCostData(String backup_month) throws Exception{
        backupDataMapper.backupDeptCostData(backup_month);
    }

    /**
     * @author 李道云
     * @methodName: backupDeptShareConfig
     * @methodDesc: 备份部门分润配置
     * @description:
     * @param: [backup_month]
     * @return void
     * @create 2018-07-11 23:12
     **/
    @Transactional(rollbackFor = Exception.class)
    public void backupDeptShareConfig(String backup_month) throws Exception{
        backupDataMapper.backupDeptShareConfig(backup_month);
    }

    /**
     * @author 李道云
     * @methodName: backupDeptGroup
     * @methodDesc: 备份部门组织数据
     * @description:
     * @param: [backup_date]
     * @return void
     * @create 2018-07-11 23:21
     **/
    @Transactional(rollbackFor = Exception.class)
    public void backupDeptGroup(String backup_date) throws Exception{
        backupDataMapper.backupDeptGroup(backup_date);
    }

    /**
     * @author 李道云
     * @methodName: backupStaffDeptRelation
     * @methodDesc: 备份员工部门组织关系
     * @description:
     * @param: [backup_date]
     * @return void
     * @create 2018-07-11 23:27
     **/
    @Transactional(rollbackFor = Exception.class)
    public void backupStaffDeptRelation(String backup_date) throws Exception{
        backupDataMapper.backupStaffDeptRelation(backup_date);
    }
}

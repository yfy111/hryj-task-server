package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.profit.BackupDeptGroup;
import com.hryj.entity.bo.profit.BackupStaffDeptRelation;
import com.hryj.entity.bo.profit.StaffDaySalary;
import com.hryj.mapper.BackupDataMapper;
import com.hryj.mapper.StaffDaySalaryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: CostCalculateService
 * @description: 成本计算service
 * @create 2018/7/17 17:37
 **/
@Slf4j
@Service
public class CostCalculateService extends ServiceImpl<StaffDaySalaryMapper,StaffDaySalary> {

    @Autowired
    private BackupDataMapper backupDataMapper;

    /**
     * @author 李道云
     * @methodName: calStaffDaySalary
     * @methodDesc: 计算员工的日工资
     * @description:
     * @param: [cal_date]
     * @return void
     * @create 2018-07-17 19:33
     **/
    @Transactional(rollbackFor = Exception.class)
    public Result calStaffDaySalary(String cal_date) throws Exception{
        if(StrUtil.isEmpty(cal_date)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"计算日期不能为空");
        }
        //1、获取当天所有区域公司
        List<Map> deptList = backupDataMapper.findDayRegionList(cal_date);
        //获取当月自然日天数
        Date endOfMonth = DateUtil.endOfMonth(DateUtil.parse(cal_date,DatePattern.NORM_DATE_PATTERN));
        int days = DateUtil.dayOfMonth(endOfMonth);
        if(CollectionUtil.isNotEmpty(deptList)){
            //2、获取区域公司下所有员工
            for (Map regionMap : deptList){
                Long region_id = (Long) regionMap.get("dept_id");
                String dept_name = (String) regionMap.get("dept_name");
                String dept_path = (String) regionMap.get("dept_path");
                List<BackupStaffDeptRelation> staffList = backupDataMapper.findRegionDayStaffList(cal_date,dept_path);
                if(CollectionUtil.isNotEmpty(staffList)){
                    List<StaffDaySalary> salaryList = new ArrayList<>();
                    for (BackupStaffDeptRelation staffDeptRelation : staffList){
                        Long dept_id = staffDeptRelation.getDept_id();
                        BackupDeptGroup deptGroup = backupDataMapper.findBackupDeptGroup(cal_date,dept_id);
                        if(deptGroup.getDept_status() ==1){//部门状态为关闭时不计算工资
                            BigDecimal salary_amt = staffDeptRelation.getSalary_amt();
                            BigDecimal day_salary = NumberUtil.div(salary_amt,days,2);
                            StaffDaySalary staffDaySalary = new StaffDaySalary();
                            staffDaySalary.setSalary_date(cal_date);
                            staffDaySalary.setDept_id(staffDeptRelation.getDept_id());
                            staffDaySalary.setRegion_id(region_id);
                            staffDaySalary.setDept_id(staffDeptRelation.getDept_id());
                            staffDaySalary.setDept_type(staffDeptRelation.getDept_type());
                            staffDaySalary.setDept_name(staffDeptRelation.getDept_name());
                            staffDaySalary.setStaff_id(staffDeptRelation.getStaff_id());
                            staffDaySalary.setStaff_name(staffDeptRelation.getStaff_name());
                            staffDaySalary.setStaff_type(staffDeptRelation.getStaff_type());
                            staffDaySalary.setStaff_job(staffDeptRelation.getStaff_job());
                            staffDaySalary.setDay_salary(day_salary);
                            salaryList.add(staffDaySalary);
                        }
                    }
                    super.insertBatch(salaryList);
                    log.info("区域公司：{},员工日工资记录,salaryList.size ==={}",dept_name,salaryList.size());
                }
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

}

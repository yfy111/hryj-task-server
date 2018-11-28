package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.WhProfitDetail;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: WhProfitDetailMapper
 * @description: 仓库分润明细
 * @create 2018/7/23 21:28
 **/
@Component
public interface WhProfitDetailMapper extends BaseMapper<WhProfitDetail> {

    /**
     * @author 李道云
     * @methodName: calWHDeptProfit
     * @methodDesc: 计算仓库部门分润
     * @description:
     * @param: [balance_month, dept_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-23 22:53
     **/
    Map<String,Object> calWHDeptProfit(@Param("balance_month")String balance_month, @Param("dept_id") Long dept_id);

}

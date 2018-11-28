package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.PartyGrossProfitStatis;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 李道云
 * @className: PartyGrossProfitStatisMapper
 * @description: 门店或仓库毛利统计
 * @create 2018/8/16 17:13
 **/
@Component
public interface PartyGrossProfitStatisMapper extends BaseMapper<PartyGrossProfitStatis> {

    /**
     * @author 李道云
     * @methodName: statisPartyGrossProfit
     * @methodDesc: 统计门店或仓库的毛利
     * @description:
     * @param: [statis_month,party_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-08-17 9:16
     **/
    Map<String,Object> statisPartyGrossProfit(@Param("statis_month") String statis_month, @Param("party_id") Long party_id);

}

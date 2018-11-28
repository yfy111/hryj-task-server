package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderProduct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 王光银
 * @className: OrderProductMapper
 * @description:
 * @create 2018/8/1 0001 15:44
 **/
@Component
public interface OrderProductMapper extends BaseMapper<OrderProduct> {

    /**
     * 查询一段时间内的付款的或者付款后取消的订单商品统计数据
     * @param condition
     * @return
     */
    List<OrderProduct> findOrderProdByCondition(Map<String, Object> condition);

    /**
     * 查询一段时间内退货的订单商品数据
     * @param condition
     * @return
     */
    List<OrderProduct> findOrderProdReturnedData(Map<String, Object> condition);

    /**
     * 查询一时间内完成订单的商品数量
     * @param condition
     * @return
     */
    List<OrderProduct> findFinishedOrderProductData(Map<String, Object> condition);
}

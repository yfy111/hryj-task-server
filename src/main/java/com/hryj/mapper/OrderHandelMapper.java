package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: OrderHandelMapper
 * @description: 订单处理mapper
 * @create 2018/7/14 14:39
 **/
@Component
public interface OrderHandelMapper extends BaseMapper<OrderInfo> {

    /**
     * @author 李道云
     * @methodName: findWaitPayOrderList
     * @methodDesc: 查询待支付订单列表
     * @description:
     * @param: []
     * @return List<Map>
     * @create 2018-07-14 15:18
     **/
    List<Map> findWaitPayOrderList();

    /**
     * @author 李道云
     * @methodName: findOrderProductList
     * @methodDesc: 查询订单商品列表
     * @description:
     * @param: [order_id]
     * @return java.util.List<java.util.Map>
     * @create 2018-07-14 18:04
     **/
    List<Map> findOrderProductList(@Param("order_id") Long order_id);

    /**
     * @author 李道云
     * @methodName: updatePartyProductStock
     * @methodDesc: 更新商品库存
     * @description:
     * @param: [party_id, product_id,adjust_num]
     * @return void
     * @create 2018-08-06 16:35
     **/
    void updatePartyProductStock(@Param("party_id") Long party_id, @Param("product_id") Long product_id, @Param("adjust_num") Integer adjust_num);


    /**
     * @author 李道云
     * @methodName: updateOrderProductStockReleaseFlag
     * @methodDesc: 更新订单商品表释放库存标识
     * @description:
     * @param: [order_id]
     * @return void
     * @create 2018-07-19 20:42
     **/
    void updateOrderProductStockReleaseFlag(@Param("order_id") Long order_id);

    /**
     * @author 李道云
     * @methodName: findCompleteTimeOutOrderList
     * @methodDesc: 查询完成超时的订单列表
     * @description:
     * @param: []
     * @return com.hryj.entity.bo.order.OrderInfo
     * @create 2018-08-01 11:11
     **/
    List<OrderInfo> findCompleteTimeOutOrderList();


}

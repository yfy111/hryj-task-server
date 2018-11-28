package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.vo.inventory.request.ProductInventoryLockItem;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.feign.ProductFeignClient;
import com.hryj.mapper.OrderHandelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: OrderHandelService
 * @description: 订单处理service
 * @create 2018/7/14 14:39
 **/
@Slf4j
@Service
public class OrderHandelService extends ServiceImpl<OrderHandelMapper,OrderInfo> {

    @Autowired
    private OrderHandelMapper orderHandelMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @author 李道云
     * @methodName: handelWaitPayOrder
     * @methodDesc: 处理待支付订单
     * @description:
     * @param: []
     * @return void
     * @create 2018-07-14 15:18
     **/
    @Transactional(rollbackFor = Exception.class)
    public void handelWaitPayOrder() throws Exception{
        //查询待支付订单列表
        List<Map> orderList = orderHandelMapper.findWaitPayOrderList();
        if(CollectionUtil.isNotEmpty(orderList)){
            log.info("处理待支付订单,orderList=" + JSON.toJSONString(orderList));
            for (Map order_map : orderList){
                Long order_id = (Long) order_map.get("order_id");
                //更新订单状态为已取消
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setId(order_id);
                orderInfo.setOrder_status(CodeCache.getValueByKey("OrderStatus","S07"));
                orderInfo.setOrder_remark("支付超时取消订单");
                orderHandelMapper.updateById(orderInfo);
                //查询订单的商品数据
                List<Map> productList = orderHandelMapper.findOrderProductList(order_id);
                //释放订单的商品库存
                if(CollectionUtil.isNotEmpty(productList)){
                    List<ProductInventoryLockItem> lock_items = new ArrayList<>();
                    for (Map product_map : productList){
                        Long party_id = (Long) product_map.get("party_id");
                        Long product_id = (Long) product_map.get("product_id");
                        Long activity_id = (Long) product_map.get("activity_id");
                        Integer adjust_num = Integer.parseInt(product_map.get("quantity").toString());
                        ProductInventoryLockItem lockItem = new ProductInventoryLockItem();
                        lockItem.setParty_id(party_id);
                        lockItem.setProduct_id(product_id);
                        lockItem.setActivity_id(activity_id);
                        lockItem.setLock_quantity(adjust_num);
                        lock_items.add(lockItem);
                    }
                    ProductsInventoryLockRequestVO requestVO = new ProductsInventoryLockRequestVO();
                    requestVO.setLock_items(lock_items);
                    requestVO.setLock_model(CommonConstantPool.ADD);
                    Result<ProductsInventoryLockResponseVO> result = productFeignClient.lockProductInventory(requestVO);
                    if(result.isSuccess()){
                        //库存释放成功，更新订单商品表的释放库存标识
                        orderHandelMapper.updateOrderProductStockReleaseFlag(order_id);
                    }
                }
            }
            log.info("处理待支付订单:" + orderList.size() + "条订单");
        }
    }

    /**
     * @author 李道云
     * @methodName: handelCompleteOrder
     * @methodDesc: 处理已完成订单
     * @description: 已发货+10天
     * @param: []
     * @return void
     * @create 2018-07-14 15:24
     **/
    @Transactional(rollbackFor = Exception.class)
    public void handelCompleteOrder() throws Exception{
        List<OrderInfo> orderInfoList = orderHandelMapper.findCompleteTimeOutOrderList();
        if(CollectionUtil.isNotEmpty(orderInfoList)){
            for (OrderInfo orderInfo : orderInfoList){
                OrderInfo info = new OrderInfo();
                info.setId(orderInfo.getId());
                info.setOrder_status(CodeCache.getValueByKey("OrderStatus","S08"));
                info.setComplete_time(DateUtil.date());
                info.setOrder_remark("定时任务处理已完成");
                //判断用户该笔订单是否为首次交易订单
                EntityWrapper<OrderInfo> wrapper = new EntityWrapper<>();
                wrapper.eq("user_id",orderInfo.getUser_id());
                wrapper.eq("new_trade_flag",1);
                List<OrderInfo> orderList = baseMapper.selectList(wrapper);
                if(CollectionUtil.isEmpty(orderList)){
                    info.setNew_trade_flag(1);//新增交易
                }
                baseMapper.updateById(info);
            }
        }
    }
}

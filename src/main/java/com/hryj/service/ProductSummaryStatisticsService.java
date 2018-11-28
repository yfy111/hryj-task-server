package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.order.OrderProduct;
import com.hryj.entity.bo.product.ProductSummary;
import com.hryj.entity.bo.product.ProductSummaryItem;
import com.hryj.mapper.OrderProductMapper;
import com.hryj.mapper.ProductSummaryItemMapper;
import com.hryj.mapper.ProductSummaryMapper;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: ProductSummaryStatisticsService
 * @description:
 * @create 2018/8/1 0001 14:48
 **/
@Slf4j
@Service
public class ProductSummaryStatisticsService extends ServiceImpl<ProductSummaryMapper, ProductSummary> {

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private ProductSummaryItemMapper productSummaryItemMapper;

    /**
     * @author 王光银
     * @methodName: productSalesStatistics
     * @methodDesc: 商品销量统计
     * @description:
     * @param: []
     * @return void
     * @create 2018-08-01 14:53
     **/
    @Transactional(rollbackFor = RuntimeException.class)
    public void productSalesStatistics(String date_time_begin, String date_time_end) {
        Date begin_date;
        Date end_date;
        boolean start_need_sub = true;
        boolean end_need_sub = true;
        if (UtilValidate.isNotEmpty(date_time_begin)) {
            try {
                begin_date = DateUtil.parseDate(date_time_begin);
                start_need_sub = false;
            } catch (Exception e) {
                begin_date = new Date();
            }
        } else {
            begin_date = new Date();
        }

        if (!UtilValidate.isEmpty(date_time_end)) {
            try {
                end_date = DateUtil.parseDate(date_time_end);
                end_need_sub = false;
            } catch (Exception e) {
                end_date = new Date();
            }
        } else {
            end_date = new Date();
        }


        Calendar calendar_limit_begin = Calendar.getInstance();
        calendar_limit_begin.setTime(begin_date);
        if (start_need_sub) {
            calendar_limit_begin.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar_limit_begin.set(Calendar.HOUR_OF_DAY, 0);
        calendar_limit_begin.set(Calendar.MINUTE, 0);
        calendar_limit_begin.set(Calendar.SECOND, 0);

        Calendar calendar_limit_end = Calendar.getInstance();
        calendar_limit_end.setTime(end_date);
        if (end_need_sub) {
            calendar_limit_end.add(Calendar.DAY_OF_MONTH, -1);
        }
        calendar_limit_end.set(Calendar.HOUR_OF_DAY, 23);
        calendar_limit_end.set(Calendar.MINUTE, 59);
        calendar_limit_end.set(Calendar.SECOND, 59);

        System.out.println("销量统计的订单限制开始时间:" + DateUtil.formatDateTime(calendar_limit_begin.getTime()));
        System.out.println("销量统计的订单限制结束时间:" + DateUtil.formatDateTime(calendar_limit_end.getTime()));

        Calendar condition_begin = Calendar.getInstance();
        condition_begin.setTime(calendar_limit_begin.getTime());

        Calendar condition_end = Calendar.getInstance();
        condition_end.setTime(calendar_limit_end.getTime());
        condition_end.set(Calendar.YEAR, condition_begin.get(Calendar.YEAR));
        condition_end.set(Calendar.MONTH, condition_begin.get(Calendar.MONTH));
        condition_end.set(Calendar.DAY_OF_MONTH, condition_begin.get(Calendar.DAY_OF_MONTH));


        Map<String, Object> params_map = new HashMap<>(3);

        Map<Integer, ProductSummary> prod_summary_map = new HashMap<>(5000);

        Map<Integer, ProductSummaryItem> prod_summary_item_map = new HashMap<>(5000 * 4);

        //按每天循环统计处理数据
        while (!condition_end.after(calendar_limit_end)) {

            System.out.println("统计自然天日期:" + DateUtil.formatDate(condition_end.getTime()));

            List<OrderProduct> ordered_list = null;
            List<OrderProduct> canceled_list = null;
            List<OrderProduct> returned_list = null;
            List<OrderProduct> finished_list = null;

            String this_day = DateUtil.formatDate(condition_begin.getTime());

            try {
                params_map.put("date_time_begin", condition_begin.getTime());
                params_map.put("date_time_end", condition_end.getTime());

                //加载下单已支付的订单商品数据
                ordered_list = orderProductMapper.findOrderProdByCondition(params_map);

                //加载下单已支付后取消的订单商品数据
                params_map.put("order_status", SysCodeEnmu.ORDERSTATUS_07.getCodeValue());
                canceled_list = orderProductMapper.findOrderProdByCondition(params_map);

                //加载退货商品数据
                returned_list = orderProductMapper.findOrderProdReturnedData(params_map);

                //加载已完成订单
                finished_list = orderProductMapper.findFinishedOrderProductData(params_map);

                //处理下单的并且支付完成的数据
                if (UtilValidate.isNotEmpty(ordered_list)) {
                    for (OrderProduct orderProduct : ordered_list) {
                        if (isInvalidData(orderProduct)) {
                            continue;
                        }
                        int hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode();
                        ProductSummary prod_summary;
                        if (prod_summary_map.containsKey(hash_key)) {
                            prod_summary = prod_summary_map.get(hash_key);
                        } else {
                            prod_summary = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id());
                            prod_summary_map.put(hash_key, prod_summary);
                        }
                        prod_summary.setTotal_quantity_ordered(prod_summary.getTotal_quantity_ordered().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));

                        hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode() * this_day.hashCode();
                        ProductSummaryItem prod_summary_item;
                        if (prod_summary_item_map.containsKey(hash_key)) {
                            prod_summary_item = prod_summary_item_map.get(hash_key);
                        } else {
                            prod_summary_item = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id(), this_day);
                            prod_summary_item_map.put(hash_key, prod_summary_item);
                        }
                        prod_summary_item.setTotal_quantity_ordered(prod_summary_item.getTotal_quantity_ordered().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));
                    }
                }

                //处理下单支付后取消的数据
                if (UtilValidate.isNotEmpty(canceled_list)) {
                    for (OrderProduct orderProduct : canceled_list) {
                        if (isInvalidData(orderProduct)) {
                            continue;
                        }
                        int hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode();
                        ProductSummary prod_summary;
                        if (prod_summary_map.containsKey(hash_key)) {
                            prod_summary = prod_summary_map.get(hash_key);
                        } else {
                            prod_summary = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id());
                            prod_summary_map.put(hash_key, prod_summary);
                        }
                        prod_summary.setTotal_quantity_canceled(prod_summary.getTotal_quantity_canceled().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));

                        hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode() * this_day.hashCode();
                        ProductSummaryItem prod_summary_item;
                        if (prod_summary_item_map.containsKey(hash_key)) {
                            prod_summary_item = prod_summary_item_map.get(hash_key);
                        } else {
                            prod_summary_item = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id(), this_day);
                            prod_summary_item_map.put(hash_key, prod_summary_item);
                        }
                        prod_summary_item.setTotal_quantity_canceled(prod_summary_item.getTotal_quantity_canceled().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));
                    }
                }

                //处理退货的数据
                if (UtilValidate.isNotEmpty(returned_list)) {
                    for (OrderProduct orderProduct : returned_list) {
                        if (isInvalidData(orderProduct)) {
                            continue;
                        }
                        int hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode();
                        ProductSummary prod_summary;
                        if (prod_summary_map.containsKey(hash_key)) {
                            prod_summary = prod_summary_map.get(hash_key);
                        } else {
                            prod_summary = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id());
                            prod_summary_map.put(hash_key, prod_summary);
                        }
                        prod_summary.setTotal_quantity_retured(prod_summary.getTotal_quantity_retured().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));

                        hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode() * this_day.hashCode();
                        ProductSummaryItem prod_summary_item;
                        if (prod_summary_item_map.containsKey(hash_key)) {
                            prod_summary_item = prod_summary_item_map.get(hash_key);
                        } else {
                            prod_summary_item = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id(), this_day);
                            prod_summary_item_map.put(hash_key, prod_summary_item);
                        }
                        prod_summary_item.setTotal_quantity_retured(prod_summary_item.getTotal_quantity_retured().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));
                    }
                }

                //处理完成订单的数据
                if (UtilValidate.isNotEmpty(finished_list)) {
                    for (OrderProduct orderProduct : finished_list) {
                        if (isInvalidData(orderProduct)) {
                            continue;
                        }
                        int hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode();
                        ProductSummary prod_summary;
                        if (prod_summary_map.containsKey(hash_key)) {
                            prod_summary = prod_summary_map.get(hash_key);
                        } else {
                            prod_summary = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id());
                            prod_summary_map.put(hash_key, prod_summary);
                        }
                        prod_summary.setTotal_quantity_finished(prod_summary.getTotal_quantity_finished().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));

                        hash_key = orderProduct.getParty_id().hashCode() * orderProduct.getProduct_id().hashCode() * this_day.hashCode();
                        ProductSummaryItem prod_summary_item;
                        if (prod_summary_item_map.containsKey(hash_key)) {
                            prod_summary_item = prod_summary_item_map.get(hash_key);
                        } else {
                            prod_summary_item = checkExists(orderProduct.getParty_id(), orderProduct.getProduct_id(), this_day);
                            prod_summary_item_map.put(hash_key, prod_summary_item);
                        }
                        prod_summary_item.setTotal_quantity_finished(prod_summary_item.getTotal_quantity_finished().add(new BigDecimal(orderProduct.getQuantity())).setScale(0));
                    }
                }

            } catch (Exception e) {
                log.error("商品销量统计任务 - 加载商品销售订单数据失败", e);
                throw new RuntimeException(e);
            } finally {
                params_map.clear();
                condition_begin.add(Calendar.DAY_OF_MONTH, 1);
                condition_end.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        if (UtilValidate.isNotEmpty(prod_summary_map)) {
            Collection<ProductSummary> collection = prod_summary_map.values();
            List<ProductSummary> to_add_list = new ArrayList<>(collection.size());
            List<ProductSummary> to_update_list = new ArrayList<>(collection.size());

            for (ProductSummary summary : collection) {
                if (summary.getId() != null && summary.getId() > 0L) {
                    to_update_list.add(summary);
                    continue;
                }
                to_add_list.add(summary);
            }

            try {
                if (UtilValidate.isNotEmpty(to_add_list)) {
                    super.insertBatch(to_add_list);
                }
            } catch (Exception e) {
                log.error("新增商品销量统计数据失败:", e);
                throw new RuntimeException(e);
            }

            try {
                if (UtilValidate.isNotEmpty(to_update_list)) {
                    super.updateAllColumnBatchById(to_update_list);
                }
            } catch (Exception e) {
                log.error("更新商品销量统计数据失败", e);
                throw new RuntimeException(e);
            }

            try {
                for (ProductSummaryItem summaryItem : prod_summary_item_map.values()) {
                    summaryItem.insert();
                }
            } catch (Exception e) {
                log.error("新增商品每日销量统计数据失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isInvalidData(OrderProduct orderProduct) {
        if (orderProduct.getParty_id() == null
                || orderProduct.getParty_id() <= 0L
                || orderProduct.getProduct_id() == null
                || orderProduct.getProduct_id() <= 0L
                || orderProduct.getQuantity() == null
                || orderProduct.getQuantity() <= 0L
                ) {
            return true;
        }
        return false;
    }

    private ProductSummary checkExists(Long party_id, Long product_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("party_id", party_id);
        wrapper.eq("product_id", product_id);
        List<ProductSummary> list = super.selectList(wrapper);
        if (UtilValidate.isEmpty(list)) {
            ProductSummary ps = new ProductSummary();
            ps.setParty_id(party_id);
            ps.setProduct_id(product_id);
            ps.setTotal_quantity_ordered(BigDecimal.ZERO);
            ps.setTotal_quantity_canceled(BigDecimal.ZERO);
            ps.setTotal_quantity_retured(BigDecimal.ZERO);
            ps.setTotal_quantity_finished(BigDecimal.ZERO);
            return ps;
        }
        ProductSummary ps = list.remove(0);
        if (ps.getTotal_quantity_canceled() == null) {
            ps.setTotal_quantity_canceled(BigDecimal.ZERO);
        }
        if (ps.getTotal_quantity_retured() == null) {
            ps.setTotal_quantity_retured(BigDecimal.ZERO);
        }
        if (ps.getTotal_quantity_ordered() == null) {
            ps.setTotal_quantity_ordered(BigDecimal.ZERO);
        }
        if (ps.getTotal_quantity_finished() == null) {
            ps.setTotal_quantity_finished(BigDecimal.ZERO);
        }
        return ps;
    }

    private ProductSummaryItem checkExists(Long party_id, Long product_id, String day_date) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("party_id", party_id);
        wrapper.eq("product_id", product_id);
        wrapper.eq("day_date", day_date);
        productSummaryItemMapper.delete(wrapper);
        ProductSummaryItem psi = new ProductSummaryItem();
        psi.setParty_id(party_id);
        psi.setProduct_id(product_id);
        psi.setDay_date(day_date);
        psi.setTotal_quantity_ordered(BigDecimal.ZERO);
        psi.setTotal_quantity_canceled(BigDecimal.ZERO);
        psi.setTotal_quantity_retured(BigDecimal.ZERO);
        psi.setTotal_quantity_finished(BigDecimal.ZERO);
        return psi;
    }
}

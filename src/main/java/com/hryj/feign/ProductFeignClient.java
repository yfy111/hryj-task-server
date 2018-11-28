package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李道云
 * @className: ProductFeignClient
 * @description: 商品服务接口
 * @create 2018/8/27 15:11
 **/
@FeignClient(name = "product-server")
public interface ProductFeignClient {

    /**
     * @author 李道云
     * @methodName: lockProductInventory
     * @methodDesc: 更新商品库存
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO>
     * @create 2018-08-27 15:12
     **/
    @RequestMapping(value = "/productCommon/lockProductInventory", method = RequestMethod.POST)
    Result<ProductsInventoryLockResponseVO> lockProductInventory(@RequestBody ProductsInventoryLockRequestVO requestVO);

    /**
     * @author 汪豪
     * @methodName: syncCrossBorderProductInventory
     * @methodDesc: 同步跨境商品库存
     * @description:
     * @param: []
     * @return void
     * @create 2018-09-19 15:35
     **/
    @RequestMapping(value = "/productCommon/syncCrossBorderProductInventory", method = RequestMethod.POST)
    void syncCrossBorderProductInventory();

}

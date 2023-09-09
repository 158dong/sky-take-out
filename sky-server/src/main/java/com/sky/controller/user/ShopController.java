package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController(value = "userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    private static final String shopStatus = "Shop_Status";

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取店铺状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(shopStatus);
        return Result.success(status);
    }

}

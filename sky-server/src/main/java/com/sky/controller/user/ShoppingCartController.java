package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Api(tags = "C端-购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @ApiOperation(value = "添加购物车")
    @PostMapping("/add")
    public Result shoppingCartAdd(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.cartAdd(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查看购物车")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> selectShoppingCart(){
        List<ShoppingCart> list = shoppingCartService.cartSelect();
        return Result.success(list);
    }

    @ApiOperation("减少购物车对应商品")
    @PostMapping("/sub")
    public Result subCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subCart(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("清空购物车")
    @DeleteMapping("/clean")
    public Result cleanShoppingCart(){
        shoppingCartService.clean();
        return Result.success();
    }

}

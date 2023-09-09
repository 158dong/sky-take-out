package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 新增菜品
     * @return
     */
    @PostMapping()
    @ApiOperation(value = "新增菜品")
    public Result add(@RequestBody DishDTO dishDTO) {
        dishService.addWithFlavors(dishDTO);

        //新增菜品的话需要清理redis缓存数据以确保查询数据跟原数据一致
        String key = "dish_" + dishDTO.getCategoryId();
        CleanCache(key);


        return Result.success();

    }

     /**
     * 分页查询（这里的DishPageQueryDTO是通过get也就是页面上面？的形式传过来的所以不用加RequestBody）
     * @param
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO)
    {
        PageResult result = dishService.page(dishPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 批量删除菜品(如果需要springmvc容器帮我们解析ids就需要写上RequestParam注解)
     */
    @DeleteMapping()
    @ApiOperation(value = "批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){

        dishService.deleteByids(ids);

        //新增菜品的话需要清理redis缓存数据以确保查询数据跟原数据一致
        String key = "dish_*";
        CleanCache(key);

        return Result.success();
    }

    /**
     * 根据ID查询菜品（用于回显修改菜品）
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据ID查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        DishVO dishVO = dishService.getByIdWithFlavors(id);

        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     */
    @PutMapping()
    @ApiOperation(value = "修改菜品")
    public Result update(@RequestBody DishDTO dishDTO)
    {
        dishService.update(dishDTO);

        //新增菜品的话需要清理redis缓存数据以确保查询数据跟原数据一致
        String key = "dish_*";
        CleanCache(key);
        return Result.success();
    }

    /**
     * 修改状态
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "修改状态")
    public Result updateStatus(@PathVariable Integer status,Long id)
    {
        dishService.updateStatus(status,id);

        //新增菜品的话需要清理redis缓存数据以确保查询数据跟原数据一致
        String key = "dish_*";
        CleanCache(key);
        return Result.success();
    }


    /**
     * 根据分类id查询菜品(用于新增套餐时回显数据可以让用户选择)
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 清理redis缓存的方法
     * @param pattern
     */
    private void CleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}

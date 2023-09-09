package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     */
    @PostMapping()
    @ApiOperation(value = "新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result add(@RequestBody SetmealDTO setmealDTO){
       setmealService.add(setmealDTO);
       return Result.success();
    }

    /**
     * 根据id查询菜品回显到添加套餐上
    @GetMapping("/list")
    @ApiOperation(value = "根据id查询菜品回显到添加套餐上")
    public Result<SetmealDish> getDishById(Long id){
        SetmealDish smd = setmealService.list(id);
        return Result.success(smd);
    }*/

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        PageResult result = setmealService.page(setmealPageQueryDTO);
        return Result.success(result);
    }

    /**
     * 删除套餐
     */
    @DeleteMapping()
    @ApiOperation(value = "批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam List<Long> ids){
        setmealService.delete(ids);
        return Result.success();

    }

    /**
     * 根据id查询套餐（用于回显修改）
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        SetmealVO sv = setmealService.getByid(id);
        return Result.success(sv);
    }

    /**
     * 修改套餐
     */
    @PutMapping()
    @ApiOperation(value = "修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result update(@RequestBody SetmealDTO setmealDTO){
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 修改状态
     */
    @PostMapping("/status/{status}")
    @ApiOperation("修改状态")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result updateStatus(@PathVariable Integer status,Long id){
        setmealService.updateStatus(status,id);
        return Result.success();
    }


}

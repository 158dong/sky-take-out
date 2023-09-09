package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
@Api(tags = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加菜品
     * @param cDto
     * @return
     */
    @ApiOperation(value = "新增分类")
    @PostMapping()
    public Result add(@RequestBody CategoryDTO cDto){
       categoryService.add(cDto);
       return Result.success();
    }

    /**
     * 分页查询
     *
     * */

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO){

        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);

        return Result.success(pageResult);
    }


    /**
     * 修改状态
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启用禁用状态")
    @PostMapping("/status/{status}")
    public Result ChangeStatus(@PathVariable Integer status,Long id){
        categoryService.startOrStop(status,id);

        return Result.success();
    }

    /**
     * 修改分类
     * @param id
     * @return
     */
    @ApiOperation(value = "修改分类")
    @PutMapping()
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 根据id查询，用于回显
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<CategoryDTO> selectById(@PathVariable Long id){
        CategoryDTO c = categoryService.select(id);

        return Result.success(c);
    }


    /**
     * 根据ID删除菜品
     * @param id
     * @return
     */
    @ApiOperation(value = "根据ID删除分类")
    @DeleteMapping()
    public Result deleteById(Long id){
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     */
    @ApiOperation(value = "根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> lc = categoryService.list(type);
        return Result.success(lc);
    }

}

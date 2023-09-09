package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 添加菜品
     * @param cDto
     */
    void add(CategoryDTO cDto);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult  pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用禁用状态
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 修改分类
     * @param id
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 根据id查询，用于回显
     * @param id
     * @return
     */
    CategoryDTO select(Long id);

    /**
     * 根据id删除菜品
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据类型查询分类
     * @return
     */
    List<Category> list(Integer type);
}

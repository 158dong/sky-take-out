package com.sky.service;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void addWithFlavors(DishDTO dishDTO);

    /**
     * 分页查询
     * @param dishDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishDTO);

    /**
     * 批量删除
     * @param ids
     */
    void deleteByids(List<Long> ids);

    /**
     * 根据ID查询菜品（用于回显修改菜品）
     * @return
     */
    DishVO getByIdWithFlavors(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void update(DishDTO dishDTO);

    /**
     * 修改状态
     * @param status
     */
    void updateStatus(Integer status,Long id);

    /**
     * 根据分类id查询菜品(用于新增套餐时回显数据可以让用户选择)
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}

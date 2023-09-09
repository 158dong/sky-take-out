package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorsMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorsMapper dishFlavorsMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    //因为设计到多个表之间的操作，为保证一致性，需要加上@Transactional保证数据的一致性
    @Transactional
    @Override
    public void addWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        //向菜品表中插入一条数据
        dishMapper.insert(dish);

        //获取插入菜品的ID（用于菜品口味表的插入）
        Long id = dish.getId();

        //向菜品口味表中插入0或n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){
            //遍历flavors口味表内的每个实体类，然后给id赋值用于后续插入数据库中
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(id);
            }
            dishFlavorsMapper.insert(flavors);
        }

    }

    /**
     * 分页查询
     * @param
     * @return
     */
    //因为设计到多个表之间的操作，为保证一致性，需要加上@Transactional保证数据的一致性
    @Transactional
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        Page<DishVO> pd = dishMapper.page(dishPageQueryDTO);


        return new PageResult(pd.getTotal(),pd.getResult());
    }

    /**
     * 批量删除
     * @param ids
     */
    @Override
    public void deleteByids(List<Long> ids) {
        //起售中的菜品不能删除
        for (Long id : ids) {
           Dish d = dishMapper.select(id);
            if(d.getStatus() == StatusConstant.ENABLE)
            {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //被套餐关联的菜品不能删除
        List<Long> setMealIds = setmealDishMapper.selectBySetmealId(ids);
        if(setMealIds != null && setMealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //批量删除菜品
        for (Long id : ids) {
            dishMapper.deleteBatch(id);
            //删除关联的口味表数据
            dishFlavorsMapper.deleteFlavorsByDishId(id);
        }


    }

    /**
     * 根据ID查询菜品（用于回显修改菜品）
     * @return
     */
    @Override
    public DishVO getByIdWithFlavors(Long id) {
       //根据id查询菜品信息
        Dish d = dishMapper.select(id);
        //根据id查询口味信息
        List<DishFlavor> dishFlavorLong = dishFlavorsMapper.select(id);
        //封装进DishVo
        DishVO DV = new DishVO();
        BeanUtils.copyProperties(d,DV);
        DV.setFlavors(dishFlavorLong);
        return DV;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //修改菜品
        dishMapper.update(dish);

        //对于口味表可能有多种操作，所以可以先进行删除口味表操作
        dishFlavorsMapper.deleteFlavorsByDishId(dishDTO.getId());

        //再把传过来的口味插入口味表中
        //向菜品口味表中插入0或n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){
            //遍历flavors口味表内的每个实体类，然后给id赋值用于后续插入数据库中
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
            dishFlavorsMapper.insert(flavors);
        }
    }

    /**
     * 修改状态
     * @param status
     */
    @Override
    public void updateStatus(Integer status,Long id) {
        Dish d = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(d);
    }

    /**
     * 根据分类id查询菜品(用于新增套餐时回显数据可以让用户选择)
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorsMapper.select(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}

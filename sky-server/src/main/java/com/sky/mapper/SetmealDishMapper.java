package com.sky.mapper;

import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDish;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> selectBySetmealId(List<Long> ids);


    void insert(List<SetmealDish> setmealDishes);


    void delete(List<Long> setmeal_id);

    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getByid(Long setmealId);
}

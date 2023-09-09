package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 根据id查询，用于回显
     * @param id
     * @return
     */
    @Select("select * from category where id = #{id}")
    CategoryDTO select(Long id);

    /**
     * 添加菜品
     * @param c
     */
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUES " + "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)   //AOP切面执行插入时更新公共字段
    void insert(Category c);

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用和禁用状态
     * 根据id修改菜品信息
     */
    @AutoFill(value = OperationType.UPDATE)  //AOP切面执行修改时更新公共字段
    void update(Category c);



    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据类型查询分类
     * @return
     */
    List<Category> list(Integer type);
}

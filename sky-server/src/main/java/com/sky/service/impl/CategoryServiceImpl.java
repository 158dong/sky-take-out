package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 添加菜品
     * @param cDto
     */
    @Override
    public void add(CategoryDTO cDto) {
       /*  已用AOP实现
       Category c = Category.builder()
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())
                .updateUser(BaseContext.getCurrentId())
                .build();*/
        Category c = new Category();

        BeanUtils.copyProperties(cDto,c);

        categoryMapper.insert(c);


    }


    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult  pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {

        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        Page<Category> r = categoryMapper.pageQuery(categoryPageQueryDTO);

        List<Category> result = r.getResult();
        long total = r.getTotal();

        return new PageResult(total,result);
    }

    /**
     * 启用和禁用状态
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category c = Category.builder()
                .id(id)
                .status(status)
                .build();

        categoryMapper.update(c);
    }

    /**
     * 修改分类
     * @param
     */
    @Override
    public void update(CategoryDTO categoryDTO) {

       /*  已用AOP实现
       Category c = Category.builder()
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();*/
        Category c = new Category();

        BeanUtils.copyProperties(categoryDTO,c);

        categoryMapper.update(c);
    }

    /**
     * 根据ID查看，用于回显
     * @param id
     * @return
     */
    @Override
    public CategoryDTO select(Long id) {
        CategoryDTO CDTO  = categoryMapper.select(id);
        return CDTO;
    }

    /**
     * 根据id删除菜品
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        //查询当前分类是否关联了菜品，如果关联了就抛出业务异常
        Integer count = dishMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }

        //查询当前分类是否关联了套餐，如果关联了就抛出业务异常
        count = setmealMapper.countByCategoryId(id);
        if(count > 0){
            //当前分类下有菜品，不能删除
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        categoryMapper.deleteById(id);
    }

    /**
     * 根据类型查询分类
     * 分页处以实现（多余）
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        List<Category> lc = categoryMapper.list(type);
        return lc;
    }


}

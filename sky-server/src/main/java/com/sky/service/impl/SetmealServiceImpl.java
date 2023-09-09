package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增套餐
     * 思路：1.先将setmealDTO用前端接收
     *      2.建一个setmeal（含时间，不含菜品）用于接收dto（不含时间，含添加的菜品）内的数据，用于将数据插入setmeal表中（表中含时间，不含菜品）
     *      3.将DTO内的用户前端添加的菜品逐个遍历并将套餐的id（setmeal表插入后自动生成）逐个设置进setmealDish中
     *      4.将DTO内的数据插入setmeal_dish表中
     *      5.再dishcontroller内写上根据套餐id查询的功能（用于用户添加菜品时回显数据可进行操作）
     * @param setmealDTO
     * @return
     */
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO,setmeal);

        Setmeal s = setmealMapper.insert(setmeal);

        Long id = s.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && setmealDishes.size() > 0){
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(id);
            }
            //保存套餐和菜品的关联关系
            setmealDishMapper.insert(setmealDishes);
        }

    }

    /**
     * 分页查询
     * 思路：1.先将前端的当前页面和总页数传过来  还有搜索框上面的名称，套餐ID，起售状态
     *      2.先导入PageHelper依赖
     *      3.利用静态方法PageHelper.startPage（用完后在写分页sql的时候会自动计算然后在sql后面加上limit）
     *      4.最后把总页数和sql查询出来的内容返回回去
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

        Page<SetmealVO> p = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 批量删除套餐（起售中的套餐不能删除，如果删除要一起把套餐关联的菜品一起删掉（切掉菜品跟套餐的联系））
     * 思路：1.先接收前端传过来的批量删除的id
     *      2.查询出要删除的套餐
     *      3.判断是否为起售中的套餐，如果为起售中的就不允许删除
     *      4.如果不为起售中的就将其删除，并要一起把套餐关联的菜品一起删掉（切掉菜品跟套餐的联系）
     * @param ids
     */
    @Transactional
    @Override
    public void delete(List<Long> ids) {
        List<Setmeal> s = setmealMapper.list(ids);
        for (Setmeal setmeal : s) {
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }else{
                setmealMapper.delete(ids);
                setmealDishMapper.delete(ids);
            }
        }
    }

    /**
     * 根据id查询套餐（用于回显修改）
     * 思路  1.接收前端传过来要回显的套餐id
     *      2.new一个setmealVO用于返回数据（内含套餐关联的菜品，用于返回显示给前端）
     *      3.分别用id查询setmeal和套餐关联的菜单表setmealDish
     *      4.将查出来的数据赋值给setmealVO然后返回给前端
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByid(Long id) {
        SetmealVO setmealVO = new SetmealVO();

        Setmeal setmeal = setmealMapper.getByid(id);

        List<SetmealDish> ls = setmealDishMapper.getByid(id);

        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(ls);

        return setmealVO;
    }

    /**
     * 修改套餐
     * 思路：1.new一个setmeal（用于修改除了套餐关联的菜品表的其他数据）
     *      2.将前端传过来的DTO内的数据赋值给setmeal用于修改
     *      3.获取DTO内的id（用于删除套餐关联的菜单数据和设置新的菜单数据的id），判断是否此套餐是否有关联的菜单数据，如果有则删除
     *      4.如果没有的话则直接把传过来的菜单表插入（要先将传过来的菜单数据设置进新的套餐id）
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.update(setmeal);

        Long id = setmealDTO.getId();
        if(setmealDTO.getSetmealDishes() != null && setmealDTO.getSetmealDishes().size()>0){
            setmealMapper.deleteBySetmealId(id);
        }

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(id);
        });


        setmealDishMapper.insert(setmealDishes);


    }

    /**
     * 修改状态
     * 思路：1.接收前端传过来的状态和要设置状态的套餐id
     *      2.先查看套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
     *      3.如果没有则将套餐id和状态设置进setmeal然后调用update修改全部数据修改状态
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {

            //起售套餐时，判断套餐内是否有停售菜品，有停售菜品提示"套餐内包含未启售菜品，无法启售"
            if(status == StatusConstant.ENABLE){
                //select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
                List<Dish> dishList = dishMapper.getBySetmealId(id);
                if(dishList != null && dishList.size() > 0){
                    dishList.forEach(dish -> {
                        if(StatusConstant.DISABLE == dish.getStatus()){
                            throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                        }
                    });
                }
            }

            Setmeal setmeal = Setmeal.builder()
                    .id(id)
                    .status(status)
                    .build();
            setmealMapper.update(setmeal);
        }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list1(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list1(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
    }



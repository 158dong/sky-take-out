package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 使用MD5对用户的密码进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    //新增员工
    @Override
    public void add(EmployeeDTO emDTO) {
        //一次性将相同属性的e内的属性添加到emDTO内
        Employee e = new Employee();
        BeanUtils.copyProperties(emDTO,e);

        //设置默认密码（用md5加密）
        e.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置e内其余属性的值
        //设置状态  1为启用  0为禁用
        e.setStatus(StatusConstant.ENABLE);

        //设置修改时间和新增时间
        //e.setCreateTime(LocalDateTime.now());
        //e.setUpdateTime(LocalDateTime.now());

        //设置修改用户和新增用户
        //e.setCreateUser(BaseContext.getCurrentId());
        //e.setUpdateUser(BaseContext.getCurrentId());

        //调用mapper将数据跟数据库连接
        employeeMapper.insert(e);



    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //利用PageHelper插件进行分页查询（会直接根据页码和每页显示记录数计算，然后直接连接到sql后面），需要加依赖
        //page为当前页数，pageSize为每页记录数  （当前页数-1）*每页记录数 = 开始索引   limit 开始索引，结束索引
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //查询返回一个Page，内部封装的是每个员工对象和页数
        Page<Employee> p = employeeMapper.pageQuery(employeePageQueryDTO);

        //获取得到的返回结果
        long total = p.getTotal();
        List<Employee> result = p.getResult();

        //返回封装后的结果
        return new PageResult(total,result);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    @Override
    public void updateStatus(Integer status, Long id) {
        //传过去一个Employee里面的全部数据

        //在Employee类中加上了Builder注解，所以可以直接这样赋值，也可以用new的方式直接赋值
        //注：修改的时候需要修改一下修改的时间
        Employee emp = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(emp);
    }

    /**
     * 根据ID查询员工（用于回显）
     * @param id
     * @return
     */
    @Override
    public EmployeeDTO selectByid(Long id) {
        EmployeeDTO emp = employeeMapper.selectByid(id);
        return emp;
    }

    /**
     * 修改员工信息
     * @param
     */
    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        Employee emp = new Employee();
        //将emDTO内的属性复制给emp内，因为emDTO内有的属性emp都有
        BeanUtils.copyProperties(employeeDTO,emp);
        //修改修改时间和修改人
         //emp.setUpdateTime(LocalDateTime.now());
         //emp.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(emp);
    }

}

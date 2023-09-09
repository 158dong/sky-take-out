package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.aspect.AutoFillAspect;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 添加新员工（注：下划线要用驼峰命名）
     * @param e
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user)" +
            " values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
   @AutoFill(value = OperationType.INSERT)
    void insert(Employee e);

    /**
     * 分页查询（利用xml写动态sql）
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);


    /**
     * 功能1：修改员工的状态并更新修改时间
     * 功能2：修改员工信息
     * @param emp
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee emp);

    /**
     * 根据ID查询单条数据（用于回显）
     * @param id
     * @return
     */
    @Select("SELECT * FROM employee WHERE id = #{id}")
    EmployeeDTO selectByid(Long id);

}

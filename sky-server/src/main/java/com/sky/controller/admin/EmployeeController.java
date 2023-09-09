package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param emDTO
     * @return
     */
    @ApiOperation(value = "新增员工")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO emDTO){
        log.info("新增员工：{}",emDTO);
       //调用Service层将前端传过去的数据传过去服务层处理
        employeeService.add(emDTO);
        return Result.success();
    }

    /**
     * 分页查询
     */

    @ApiOperation(value = "分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
       //根据前端传过来的员工姓名，页码和查询页数进行分页查询并将结果封装后返回
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);

        return Result.success(pageResult);
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @ApiOperation(value = "启用禁用员工账号")
    @PostMapping("/status/{status}")
    public Result updateStatus(@PathVariable Integer status,
                                Long id ){

        log.info("员工状态和id {}，{}",status,id);
        employeeService.updateStatus(status,id);

        return Result.success();
    }

    /**
     * 查询单条数据（用于回显）
     */
    @ApiOperation(value = "根据ID查询员工")
    @GetMapping("/{id}")
    public Result<EmployeeDTO> selectById (@PathVariable Long id){
        EmployeeDTO emp = employeeService.selectByid(id);
        return Result.success(emp);
    }

    /**
     * 回显数据后修改员工信息
     */
    @ApiOperation(value = "编辑员工信息")
    @PutMapping
    public Result updateEmployee(@RequestBody EmployeeDTO emDTO){
        employeeService.updateEmployee(emDTO);
        return Result.success();
    }

}

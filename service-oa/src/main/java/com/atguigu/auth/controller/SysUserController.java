package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.atguigu.vo.system.SysUserQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    SysUserService sysUserService;


    //page当前页 limit每页显示记录数 SysUserQueryVo条件对象
    @ApiOperation(value = "条件分页查询")
    @GetMapping("/{page}/{limit}")
    public Result<Page<SysUser>> pageQueryUser(@PathVariable(value = "page") Long page,
                                               @PathVariable(value = "limit") Long limit,
                                               SysUserQueryVo sysUserQueryVo){
        //1.创建Page对象,传递分页相关参数
        Page<SysUser> pageParam = new Page<>(page,limit);
        //2.封装条件,判断条件是否为空,不为空进行封装
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        //获取条件值
        String username = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();
        //like模糊查询
        if(!StringUtils.isEmpty(username)){
            wrapper.like(SysUser::getUsername,username);
        }
        //ge 大于等于
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime,createTimeBegin);
        }
        //le 小于等于
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.ge(SysUser::getCreateTime,createTimeEnd);
        }
        //3.调用方法实现
        Page<SysUser> pageModel = sysUserService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }


    @ApiOperation(value = "增加用户")
    @PostMapping("/")
    public Result saveUser(@RequestBody SysUser user){
        //对密码加密,使用MD5
        String password = user.getPassword();
        String passwordMD5 = MD5.encrypt(password);
        user.setPassword(passwordMD5);
        boolean bool = sysUserService.save(user);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("/{id}")
    public Result deleteUserById(@PathVariable("id") Integer id){
        boolean bool = sysUserService.removeById(id);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "修改用户")
    @PutMapping("/")
    public Result updateUser(@RequestBody SysUser user){
        boolean bool = sysUserService.updateById(user);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据id查找用户")
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable(value = "id") Integer id){
        SysUser user = sysUserService.getById(id);
        Result<SysUser> ok = Result.ok(user);
        return ok;
    }


    /**
     *
     * @param id 用户id
     * @return Result的data为Map类型,mapA键为所有角色,mapB键为用户id涉及的角色
     */
    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{id}")
    public Result toAssign(@PathVariable("id") Long id){
        Map<String,Object> map = sysUserService.findRoleByUserId(id);
        return Result.ok(map);
    }

    /**
     *
     * @param assginRoleVo 内有field用户id、field角色id列表
     * @return
     */
    @ApiOperation(value = "根据用户分配角色")
    @PutMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo){
        Boolean bool = sysUserService.doAssign(assginRoleVo);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "更新用户状态")
    @PutMapping("/changeStatus/{id}/{status}")
    public Result changeStatus(@PathVariable("id") Long id,@PathVariable("status") Integer status){
        sysUserService.changeStatus(id,status);
        return Result.ok();
    }
}


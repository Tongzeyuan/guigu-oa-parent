package com.atguigu.auth.controller;

import com.atguigu.auth.service.SysRoleService;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.result.Result;
import com.atguigu.model.system.SysRole;
import com.atguigu.vo.system.SysRoleQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping(value = "/admin/system/sysRole")
public class SysRoleController {

    @Autowired
    SysRoleService sysRoleService;

    @ApiOperation(value = "获取全部角色列表")
    @GetMapping("/")
    public Result<List<SysRole>> findAll() {
        List<SysRole> roleList = sysRoleService.list();
        Result<List<SysRole>> result = Result.ok(roleList);
        return result;
    }

    //page当前页 limit每页显示记录数 SysRoleQueryVo条件对象
    @ApiOperation(value = "条件分页查询")
    @GetMapping("/{page}/{limit}")
    public Result<Page<SysRole>> pageQueryRole(@PathVariable(value = "page") Long page,
                                @PathVariable(value = "limit") Long limit,
                                SysRoleQueryVo sysRoleQueryVo){
        //1.创建Page对象,传递分页相关参数
        Page<SysRole> pageParam = new Page<>(page,limit);
        //2.封装条件,判断条件是否为空,不为空进行封装
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName,roleName);
        }
        //3.调用方法实现
        Page<SysRole> pageModel = sysRoleService.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "添加角色")
    @PostMapping("/")
    public Result saveRole(@RequestBody SysRole role){
        boolean bool = sysRoleService.save(role);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "修改角色")
    @PutMapping("/")
    public Result updateRole(@RequestBody SysRole role){
        boolean bool = sysRoleService.updateById(role);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据id查询角色")
    @GetMapping("/{id}")
    public Result getRoleById(@PathVariable(value = "id") Integer id){
        SysRole role = sysRoleService.getById(id);
        Result<SysRole> ok = Result.ok(role);
        return ok;
    }

    @ApiOperation(value = "删除角色")
    @DeleteMapping("/{id}")
    public Result deleteRoleById(@PathVariable("id") Integer id){
        boolean bool = sysRoleService.removeById(id);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据id批量删除角色")
    @DeleteMapping("/batch")
    public Result deleteBatchById(@RequestBody ArrayList<Long> list){
        boolean bool = sysRoleService.removeByIds(list);
        if(bool == true){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }
}

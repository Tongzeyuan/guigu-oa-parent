package com.atguigu.auth.controller;


import com.atguigu.auth.service.SysUserService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.common.jwt.JwtHelper;
import com.atguigu.common.result.Result;
import com.atguigu.common.utils.MD5;
import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.LoginVo;
import com.atguigu.vo.system.RouterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {

    @Autowired
    private SysUserService sysUserService;

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo){
        SysUser sysUser = sysUserService.getByUsername(loginVo.getUsername());
        if(null == sysUser) {
            throw new GuiguException(201,"用户不存在");
        }
        if(MD5.encrypt(loginVo.getPassword()).equals(sysUser.getPassword()) == false) {
            throw new GuiguException(201,"密码错误");
        }
        if(sysUser.getStatus().intValue() == 0) {
            throw new GuiguException(201,"用户被禁用");
        }

        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtHelper.createToken(sysUser.getId(), sysUser.getUsername()));
        return Result.ok(map);
    }

    @ApiOperation(value = "获取用户信息")
    @GetMapping ("/info")
    public Result info(HttpServletRequest httpServletRequest){

        //{"code":20000,"data":{"roles":["admin"],"introduction":"I am a super administrator","avatar":"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif","name":"Super Admin"}}

        //1. 从请求头获取用户信息
        String token = httpServletRequest.getHeader("token");
        //2. 从token字符串获取用户id或者用户名称
        Long userId = JwtHelper.getUserId(token);
        //3. 根据用户id查询数据库，把用户信息获取出来
        SysUser sysUser = sysUserService.getById(userId);
        //4. 根据用户id获取用户可以操作菜单列表
        //查询数据库动态构建路由结构
        List<RouterVo> userMenuList = sysUserService.findUserMenuList(userId);
        //5. 根据用户id获取用户可以操作的按钮
        List<String> permsList = sysUserService.findUserPermsByUserId(userId);
        //6. 返回相应的数据
        Map<String,Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name",sysUser.getName());
        //返回用户可以操作菜单
        map.put("routers",userMenuList);
        //返回用户可以操作按钮
        map.put("buttons",permsList);
        return Result.ok(map);
    }

    @ApiOperation(value = "登出")
    @PostMapping("/logout")
    public Result logout(){
        return Result.ok();
    }

}

package com.atguigu.auth.service;

import com.atguigu.model.system.SysUser;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-09
 */
public interface SysUserService extends IService<SysUser> {

    Map<String, Object> findRoleByUserId(Long id);

    Boolean doAssign(AssginRoleVo assginRoleVo);

    void changeStatus(Long id,Integer status);

    SysUser getByUsername(String username);

    List<RouterVo> findUserMenuList(Long userId);

    List<String> findUserPermsByUserId(Long userId);
}

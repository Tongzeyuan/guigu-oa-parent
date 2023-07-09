package com.atguigu.auth.mapper;


import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户角色 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-06-10
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    List<Long> getRoleIdsByUserId(Long id);
}

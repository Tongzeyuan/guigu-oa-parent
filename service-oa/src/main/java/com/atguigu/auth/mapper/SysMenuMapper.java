package com.atguigu.auth.mapper;

import com.atguigu.model.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author atguigu
 * @since 2023-06-11
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    //根据用户id查询菜单
    List<SysMenu> findListByUserid(@Param("userId") Long userId);
}

package com.atguigu.auth.service.impl;

import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.mapper.SysRoleMenuMapper;
import com.atguigu.auth.service.SysMenuService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.common.config.exception.GuiguException;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRoleMenu;
import com.atguigu.vo.system.AssginMenuVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-11
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    //查找所有菜单(树形)
    @Override
    public List<SysMenu> findMenuTrees() {
        ArrayList<SysMenu> menuTrees = new ArrayList<>();
        //1.查找所有菜单
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        //2.找到起始父菜单,递归寻找子菜单,构建tree
        for(SysMenu sysMenu: sysMenus){
            if(sysMenu.getParentId() == 0){
                SysMenu menuTree = MenuHelper.buildTree(sysMenu,sysMenus);
                menuTrees.add(menuTree);
            }
        }
        return menuTrees;
    }

    @Override
    public void removeMenuById(Long id) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId,id);
        Integer count = baseMapper.selectCount(wrapper);
        //若该菜单下有子菜单，不可删除
        if(count > 0){
            throw new GuiguException(201,"有子菜单,不可以删除");
        }else{
            //否则直接删除
            baseMapper.deleteById(id);
        }
    }

    //根据角色获取菜单
    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //1.获取全部菜单(status=1表示可用)
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> sysMenus = baseMapper.selectList(wrapper);
        //2.获取 角色id 对应的 菜单id列表
        List<Long> menuIdList = sysRoleMenuMapper.getMenuIdListByRoleId(roleId);
        //3.根据 菜单id列表 获取 对应的菜单信息(select设为true)
        for(SysMenu sysMenu:sysMenus){
            if(menuIdList.contains(sysMenu.getId())){
                sysMenu.setSelect(true);
            }else{
                sysMenu.setSelect(false);
            }
        }
        //4.把全部菜单转为树形
        List<SysMenu> menuTrees = new ArrayList<>();
        for(SysMenu sysMenu: sysMenus){
            if(sysMenu.getParentId() == 0){
                SysMenu menuTree = MenuHelper.buildTree(sysMenu,sysMenus);
                menuTrees.add(menuTree);
            }
        }
        return menuTrees;
    }


    //给角色分配权限
    @Override
    public void doAssign(AssginMenuVo assignMenuVo) {
        //1.根据 角色id 删除记录
        Long roleId = assignMenuVo.getRoleId();
        sysRoleMenuMapper.deleteById(roleId);
        //2.根据 角色id 和选中的 菜单id列表 添加记录
        List<Long> menuIdList = assignMenuVo.getMenuIdList();
        for(Long menuId:menuIdList){
            if(StringUtils.isEmpty(menuId)){
                continue;
            }
            SysRoleMenu sysRoleMenu = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(sysRoleMenu);
        }
    }
}

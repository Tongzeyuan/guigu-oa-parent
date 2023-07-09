package com.atguigu.auth.service.impl;


import com.atguigu.auth.mapper.SysMenuMapper;
import com.atguigu.auth.mapper.SysRoleMapper;
import com.atguigu.auth.mapper.SysUserMapper;
import com.atguigu.auth.mapper.SysUserRoleMapper;
import com.atguigu.auth.service.SysRoleService;
import com.atguigu.auth.service.SysUserRoleService;
import com.atguigu.auth.service.SysUserService;
import com.atguigu.auth.utils.MenuHelper;
import com.atguigu.model.system.SysMenu;
import com.atguigu.model.system.SysRole;
import com.atguigu.model.system.SysUser;
import com.atguigu.model.system.SysUserRole;
import com.atguigu.vo.system.AssginRoleVo;
import com.atguigu.vo.system.MetaVo;
import com.atguigu.vo.system.RouterVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-06-09
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;

    //根据用户获取角色数据
    //@param id 用户id
    //@return map:mapA键为所有角色,mapB键为用户id涉及的角色
    @Override
    public Map<String, Object> findRoleByUserId(Long id) {
        //1.获取所有角色
        List<SysRole> roles = sysRoleMapper.selectList(null);
        //2.获取 用户id 匹配的 角色id列表
        List<Long> roleIds = sysUserRoleMapper.getRoleIdsByUserId(id);
        //3.根据角色id列表获取角色
        List<SysRole> rolesMatchUserId = new ArrayList<>();
        for(SysRole role: roles){
            if(roleIds.contains(role.getId())){
                rolesMatchUserId.add(role);
            }
        }
        //3.把以上两个列表放入Map集合
        HashMap<String,Object> map = new HashMap<>();
        map.put("allRolesList", roles);
        map.put("assginRoleList",rolesMatchUserId);
        return map;
    }

    //根据用户分配角色
    //* @param assginRoleVo 内有field用户id、field角色id列表
    //* @return Boolean
    @Override
    public Boolean doAssign(AssginRoleVo assginRoleVo) {
        Long userId = assginRoleVo.getUserId();
        List<Long> roleIdList = assginRoleVo.getRoleIdList();
        //1.删除sys_user_role里指定user_id的记录
        QueryWrapper<SysUserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        sysUserRoleMapper.delete(wrapper);
        //2.新增 角色id列表与用户id 匹对记录
        for(Long roleId:roleIdList){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            sysUserRoleMapper.insert(sysUserRole);
        }
        return true;
    }

    //更新用户状态
    @Override
    public void changeStatus(Long id,Integer status) {
        baseMapper.changeStatus(id,status);
    }

    //根据用户名获取用户
    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = baseMapper.selectOne(wrapper);
        return sysUser;
    }


    //根据用户id查询数据库动态构建路由结构
    @Override
    public List<RouterVo> findUserMenuList(Long userId) {
        List<SysMenu> sysMenuList = null;
        //超级管理员admin账号id为1,查询所有菜单
        if(userId == 1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1).orderByAsc(SysMenu::getSortValue);
            sysMenuList = sysMenuMapper.selectList(wrapper);
        }else{
            sysMenuList = sysMenuMapper.findListByUserid(userId);
        }
        //构建树形数据
        List<SysMenu> sysMenuTreeList = MenuHelper.buildTreeA(sysMenuList);
        List<RouterVo> routerVoList = this.buildMenus(sysMenuTreeList);
        return routerVoList;
    }
    //根据树化的菜单列表 构建 路由列表
    public List<RouterVo> buildMenus(List<SysMenu> sysMenuTreeList){
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for(SysMenu sysMenu : sysMenuTreeList){
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(sysMenu));
            router.setComponent(sysMenu.getComponent());
            router.setMeta(new MetaVo(sysMenu.getName(),sysMenu.getIcon()));
            List<SysMenu> children = sysMenu.getChildren();
            //如果当前是菜单,需将按钮对应的路由加载出来,如:"角色授权"按钮对应的路由在"系统管理"下面
            if(sysMenu.getType().intValue() == 1) {
                List<SysMenu> hiddenMenuList = children.stream().filter(item ->
                        !StringUtils.isEmpty(item.getComponent())).collect(Collectors.toList());
                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else{
                    if(!CollectionUtils.isEmpty(children)){
                        if(children.size() > 0){
                            router.setAlwaysShow(true);
                        }
                        router.setChildren(buildMenus(children));
                    }
                }
                routers.add(router);
        }
        return routers;
    }

    //获取路由地址
    public String getRouterPath(SysMenu menu){
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0){
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    //根据用户id查询用户可以操作的按钮
    @Override
    public List<String> findUserPermsByUserId(Long userId) {
        List<SysMenu> menus = null;
        //是超级管理员
        if(userId.longValue() == 1){
            //获取全部菜单
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            menus = sysMenuMapper.selectList(wrapper);
        }else{
            //根据用户id 获取 菜单
            menus = sysMenuMapper.findListByUserid(userId);
        }
        //遍历菜单,获取其中的按钮
        List<String> perms = new ArrayList<>();
        for(SysMenu sysMenu:menus){
            //type=2是按钮
            if(sysMenu.getType() == 2){
                perms.add(sysMenu.getPerms());
            }
        }
        return perms;
    }
}

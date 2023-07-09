package com.atguigu.auth.utils;

import com.atguigu.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {


    //根据父菜单构建tree(递归)
    public static SysMenu buildTree(SysMenu parentMenu, List<SysMenu> sysMenus) {
        parentMenu.setChildren(new ArrayList<SysMenu>());
        for(SysMenu childMenu: sysMenus){
            if(childMenu.getParentId() == parentMenu.getId()){
                SysMenu result = buildTree(childMenu,sysMenus);
                parentMenu.getChildren().add(result);
            }
        }
        return parentMenu;
    }

    //根据菜单列表构建 tree列表(递归)
    public static List<SysMenu> buildTreeA(List<SysMenu> sysMenuList) {
        List<SysMenu> menuTreeList = new ArrayList<>();
        for(SysMenu sysMenu: sysMenuList){
            if(sysMenu.getParentId() == 0){
                SysMenu menuTree = MenuHelper.buildTree(sysMenu,sysMenuList);
                menuTreeList.add(menuTree);
            }
        }
        return menuTreeList;
    }


}

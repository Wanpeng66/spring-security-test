package com.wp.security;

import com.wp.dao.PermissionRepository;
import com.wp.dao.RoleRepository;
import com.wp.domain.SysPermission;
import com.wp.domain.SysRole;
import com.wp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author: wp
 * @Title: CustomPermissionEvaluator
 * @Description: TODO
 * @date 2020/1/7 17:14
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    PermissionRepository permissionRepository;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public boolean hasPermission( Authentication authentication, Object targetUrl, Object permission ) {
        User principal = (User) authentication.getPrincipal();
        Collection<GrantedAuthority> authorities = principal.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String roleName = authority.getAuthority();
            SysRole byRoleName = roleRepository.findByRoleName( roleName );
            List<SysPermission> byRoleId = permissionRepository.findByRoleId( byRoleName.getId() );
            for (SysPermission sysPermission : byRoleId) {
                // 获取权限集
                List permissions = sysPermission.getPermissions();
                // 如果访问的Url和权限用户符合的话，返回true
                if(targetUrl.equals(sysPermission.getUrl())
                        && permissions.contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission( Authentication authentication, Serializable targetId, String targetType, Object permission ) {
        return false;
    }
}

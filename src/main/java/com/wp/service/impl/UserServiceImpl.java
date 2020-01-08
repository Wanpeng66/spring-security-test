package com.wp.service.impl;

import com.wp.dao.UserRepository;
import com.wp.dao.UserRoleRepository;
import com.wp.domain.SysUser;
import com.wp.domain.SysUserRole;
import com.wp.service.RoleService;
import com.wp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: wp
 * @Title: UserServiceImpl
 * @Description: TODO
 * @date 2020/1/6 15:46
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    @Override
    public SysUser findById( Integer id ) {
        return userRepository.findById( id ).get();
    }

    @Override
    public SysUser findByName( String name ) {
        return userRepository.findByUsername( name );
    }

    @Override
    public List<SysUserRole> listByUserId( Integer id ) {

        return userRoleRepository.findById(id);
    }
}

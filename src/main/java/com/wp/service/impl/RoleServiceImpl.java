package com.wp.service.impl;

import com.wp.dao.RoleRepository;
import com.wp.domain.SysRole;
import com.wp.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: wp
 * @Title: RoleServiceImpl
 * @Description: TODO
 * @date 2020/1/6 15:47
 */
@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository roleRepository;

    @Override
    public SysRole findById( Integer id ) {
        return roleRepository.findById( id ).get();
    }
}

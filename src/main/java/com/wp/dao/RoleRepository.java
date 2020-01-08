package com.wp.dao;

import com.wp.domain.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: wp
 * @Title: RoleRepository
 * @Description: TODO
 * @date 2020/1/6 15:38
 */
@Repository
public interface RoleRepository extends JpaRepository<SysRole,Integer> {

    SysRole findByRoleName(String name);
}

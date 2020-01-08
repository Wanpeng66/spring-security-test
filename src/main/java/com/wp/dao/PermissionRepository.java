package com.wp.dao;

import com.wp.domain.SysPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wp
 * @Title: PermissionRepository
 * @Description: TODO
 * @date 2020/1/8 10:30
 */
@Repository
public interface PermissionRepository extends JpaRepository<SysPermission,Integer> {

    List<SysPermission> findByRoleId(Integer id );
}

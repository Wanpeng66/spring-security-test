package com.wp.dao;

import com.wp.domain.SysUserRole;
import com.wp.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: wp
 * @Title: UserRoleRepository
 * @Description: TODO
 * @date 2020/1/6 15:39
 */
@Repository
public interface UserRoleRepository extends JpaRepository<SysUserRole, UserRole> {
    @Query(nativeQuery = true,value = " select * from user_role where user_id = :id ")
    List<SysUserRole> findById(@Param( "id" ) Integer id);
}

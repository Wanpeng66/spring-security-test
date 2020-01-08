package com.wp.dao;

import com.wp.domain.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: wp
 * @Title: UserRepository
 * @Description: TODO
 * @date 2020/1/6 15:37
 */
@Repository
public interface UserRepository extends JpaRepository<SysUser,Integer> {

    SysUser findByUsername(String name);
}

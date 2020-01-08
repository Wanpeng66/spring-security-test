package com.wp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author: wp
 * @Title: SysUserRole
 * @Description: TODO
 * @date 2020/1/6 15:16
 */
@Data
@Embeddable
public class UserRole implements Serializable {
    static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "role_id")
    private Integer roleId;
}

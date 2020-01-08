package com.wp.domain;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author: wp
 * @Title: SysUserRole
 * @Description: TODO
 * @date 2020/1/6 15:16
 */
@Data
@Entity
@Table(name ="user_role")
public class SysUserRole implements Serializable {
    static final long serialVersionUID = 1L;

    @EmbeddedId
    private UserRole id;


}

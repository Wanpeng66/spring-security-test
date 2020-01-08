package com.wp.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author: wp
 * @Title: SysPermission
 * @Description: TODO
 * @date 2020/1/8 9:12
 */
@Data
@Entity
@Table(name = "permission")
public class SysPermission implements Serializable {

    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String url;

    @Column
    private Integer roleId;

    @Column
    private String permission;


    @Transient
    private List permissions;


    public List getPermissions() {
        return Arrays.asList(this.permission.trim().split(","));
    }

    public void setPermissions(List permissions) {
        this.permissions = permissions;
    }


}

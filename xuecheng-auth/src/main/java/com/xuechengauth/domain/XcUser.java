package com.xuechengauth.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author joker
 * @since 2023-01-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class XcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private String id;

    private String username;

    private String password;

    private String salt;

    /**
     * 微信unionid
     */
    private String wxUnionid;

    /**
     * 昵称
     */
    private String nickname;

    private String name;

    /**
     * 头像
     */
    private String userpic;

    private String companyId;

    private String utype;

    private Date birthday;

    private String sex;

    private String email;

    private String cellphone;

    private String qq;

    /**
     * 用户状态
     */
    private String status;

    private Date createTime;

    private Date updateTime;


}

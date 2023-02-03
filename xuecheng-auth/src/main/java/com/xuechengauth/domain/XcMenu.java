package com.xuechengauth.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class XcMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    /**
     * 菜单编码
     */
    private String code;

    /**
     * 父菜单ID
     */
    private String pId;

    /**
     * 名称
     */
    private String menuName;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 是否是菜单
     */
    private String isMenu;

    /**
     * 菜单层级
     */
    private Integer level;

    /**
     * 菜单排序
     */
    private Integer sort;

    private String status;

    private String icon;

    private Date createTime;

    private Date updateTime;


}

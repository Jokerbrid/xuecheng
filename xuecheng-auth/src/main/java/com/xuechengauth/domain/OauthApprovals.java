package com.xuechengauth.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class OauthApprovals implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("userId")
    private String userid;

    @TableField("clientId")
    private String clientid;

    private String scope;

    private String status;

    @TableField("expiresAt")
    private Date expiresat;

    @TableField("lastModifiedAt")
    private Date lastmodifiedat;


}

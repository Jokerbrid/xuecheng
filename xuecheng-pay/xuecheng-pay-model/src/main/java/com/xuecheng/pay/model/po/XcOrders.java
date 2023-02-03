package com.xuecheng.pay.model.po;

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
 * @since 2023-02-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class XcOrders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 总价
     */
    private Float totalPrice;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 交易状态
     */
    private String status;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单名称
     */
    private String orderName;

    /**
     * 订单描述
     */
    private String orderDescrip;

    /**
     * 订单明细json
     */
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    private String outBusinessId;


}

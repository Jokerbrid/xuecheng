package com.xuecheng.pay.model.dto;

import lombok.Data;

@Data
public class AddOrderDto {

    //总价：
    private Float totalPrice;

    //订单类型：
    private String orderType;

    //订单名称：
    private String orderName;

    //订单描述：
    private String orderDescrip;

    /***
     * 订单明细:
     * json:
     * {
     *     goodsId:
     *     goodsType:
     *     goodsName:
     *     goodsPrice:
     *     goodsDetail:
     * }
     */
    private String orderDetail;

    //外部系统订单：
    private String outBusinessId;

}

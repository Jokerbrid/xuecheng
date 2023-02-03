package com.xuecheng.pay.model.dto;

import lombok.Data;


/***
 * 支付结果dto:
 *@author jokerBird
 *@Data create 2023-02-02 12:01
*/
@Data
public class PayStatusDto {

    //支付结果：
    private String trade_status;

    //支付流水号：
    private String out_trade_on;

    //支付宝交易号：
    private String trade_on;

    //支付金额：
    private String total_amount;

    //appId:
    private String app_id;
}

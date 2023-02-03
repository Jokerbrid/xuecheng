package com.xuecheng.pay.service;

import com.xuecheng.pay.model.dto.AddOrderDto;
import com.xuecheng.pay.model.dto.PayRecordDto;
import com.xuecheng.pay.model.dto.PayStatusDto;
import com.xuecheng.pay.model.po.XcPayRecord;

/***
 *订单接口：
 *@author jokerBird
 *@Data create 2023-02-02 10:56
*/
public interface OrderService {

    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    XcPayRecord getPayRecordByPayNo(String payNo);

    void saveAliPayStatus(PayStatusDto payStatusDto);
}

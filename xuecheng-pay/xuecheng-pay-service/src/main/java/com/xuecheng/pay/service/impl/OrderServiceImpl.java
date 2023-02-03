package com.xuecheng.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xuecheng.base.util.IdWorkerUtils;
import com.xuecheng.pay.mapper.XcOrdersGoodsMapper;
import com.xuecheng.pay.mapper.XcOrdersMapper;
import com.xuecheng.pay.mapper.XcPayRecordMapper;
import com.xuecheng.pay.model.dto.AddOrderDto;
import com.xuecheng.pay.model.dto.PayRecordDto;
import com.xuecheng.pay.model.dto.PayStatusDto;
import com.xuecheng.pay.model.po.AlipayConfig;
import com.xuecheng.pay.model.po.XcOrders;
import com.xuecheng.pay.model.po.XcOrdersGoods;
import com.xuecheng.pay.model.po.XcPayRecord;
import com.xuecheng.pay.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;


@Component
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    XcOrdersMapper xcOrdersMapper;
    @Autowired
    XcOrdersGoodsMapper xcOrdersGoodsMapper;
    @Autowired
    XcPayRecordMapper xcPayRecordMapper;

    @Transactional
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        //添加商品订单：
        XcOrders order = saveXcOrders(userId, addOrderDto);
        //添加支付记录：
        XcPayRecord payRecord = createXcPayRecord(order);
        PayRecordDto payRecordDto=new PayRecordDto();
        BeanUtils.copyProperties(payRecord,payRecordDto);
        return payRecordDto;
    }

    @Override
    public XcPayRecord getPayRecordByPayNo(String payNo) {
        XcPayRecord xcPayRecord = xcPayRecordMapper.selectOne(new LambdaQueryWrapper<XcPayRecord>()
                .eq(XcPayRecord::getPayNo, payNo));

        return xcPayRecord;
    }

    @Override
    @Transactional
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        String trade_status = payStatusDto.getTrade_status();
        if(trade_status.equals("TRADE_SUCCESS")){
            //
            String payNo = payStatusDto.getOut_trade_on();
            XcPayRecord payRecord = getPayRecordByPayNo(payNo);
            //将支付金额变为分：
            Float totalPrice=payRecord.getTotalPrice()*100;
            Float total_amount= Float.parseFloat(payStatusDto.getTotal_amount())*100;
            if(payRecord!=null &&
                    payStatusDto.getApp_id().equals(AlipayConfig.APPID) &&
                    totalPrice.intValue()==total_amount.intValue()){
                String status=payRecord.getStatus();
                if("601001".equals(status)) {//未进行支付：
                    log.debug("更新支付结果,支付交易流水号:{},支付结果:{}", payNo, trade_status);
                    XcPayRecord xcPayRecordUpdate = new XcPayRecord();
                    xcPayRecordUpdate.setStatus("601002");//已支付
                    xcPayRecordUpdate.setOutPayChannel("Alipay");//使用支付宝支付的。
                    xcPayRecordUpdate.setOutPayNo(payStatusDto.getTrade_on());
                    xcPayRecordUpdate.setPaySuccessTime(new Date());
                    //
                    int update1 = xcPayRecordMapper.update(xcPayRecordUpdate, new LambdaQueryWrapper<XcPayRecord>()
                            .eq(XcPayRecord::getPayNo, payNo));
                    if (update1 > 0) {
                        log.info("收到支付通知,更新支付状态成功,支付交易流水号:{},支付结果:{}", payNo, trade_status);
                    } else {
                        log.error("收到支付通知,更新支付状态失败,支付交易流水号:{},支付结果:{}", payNo, trade_status);
                    }
                    //关联的订单号:
                    Long orderId = payRecord.getOrderId();
                    XcOrders orders = xcOrdersMapper.selectById(orderId);
                    if (orders != null) {
                        //
                        XcOrders xcOrdersUpdate = new XcOrders();
                        xcOrdersUpdate.setStatus("600002");//已支付
                        int update = xcOrdersMapper.update(xcOrdersUpdate, new LambdaQueryWrapper<XcOrders>()
                                .eq(XcOrders::getId, orderId));
                        if (update > 0) {
                            log.info("收到支付通知,更新订单状态成功,支付交易流水号:{},支付结果:{},订单号:{},状态:{}"
                                    , payNo, trade_status, orderId, "600002");
                        } else {
                            log.error("收到支付通知,更新订单状态失败,支付交易流水号:{},支付结果:{},订单号:{},状态:{}"
                                    , payNo, trade_status, orderId, "600002");
                        }
                    } else {
                        //无关联的订单
                    }
                }
            }
        }

    }

    @Transactional
    public XcOrders saveXcOrders(String userId,AddOrderDto addOrderDto){

        XcOrders order = getOrderByBusinessId(addOrderDto.getOutBusinessId());
        if(order!=null){
            return order;
        }
        order=new XcOrders();
        //生成订单号：
        long orderId= IdWorkerUtils.getInstance().nextId();
        order.setId(orderId);
        order.setUserId(userId);
        order.setCreateDate(new Date());
        order.setStatus("600001");//未支付
        BeanUtils.copyProperties(addOrderDto,order);
        //
        xcOrdersMapper.insert(order);
        String orderDetailJSon= addOrderDto.getOrderDetail();
        //通过反射拿到List<XcOrdersGods> 的类型：
        Type listType = new TypeToken<List<XcOrdersGoods>>() {}.getType();
        //将json 转为 list<object>
        List<XcOrdersGoods> xcOrdersGoodsList=new Gson().fromJson(orderDetailJSon,listType);
        xcOrdersGoodsList.forEach(goods->{
            XcOrdersGoods xcOrdersGoods = new XcOrdersGoods();
            BeanUtils.copyProperties(goods,xcOrdersGoods);
            xcOrdersGoods.setOrderId(orderId);
            //插入goods表中：
            xcOrdersGoodsMapper.insert(xcOrdersGoods);
        });
        return order;
    }

    private  XcOrders getOrderByBusinessId(String outBusinessId){
        XcOrders order = xcOrdersMapper.selectOne(new LambdaQueryWrapper<XcOrders>()
                .eq(XcOrders::getOutBusinessId, outBusinessId));
        return order;
    }

    public XcPayRecord createXcPayRecord(XcOrders order){
        XcPayRecord payRecord = new XcPayRecord();
        //生成支付流水号：
        long payNo=IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo);
        payRecord.setOrderId(order.getId());
        payRecord.setOrderName(order.getOrderName());
        payRecord.setTotalPrice(order.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(new Date());
        payRecord.setStatus("601001");//未支付状态码
        payRecord.setUserId(order.getUserId());

        xcPayRecordMapper.insert(payRecord);
        return payRecord;
    }
}

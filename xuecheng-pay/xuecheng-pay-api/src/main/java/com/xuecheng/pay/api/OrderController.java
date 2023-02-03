package com.xuecheng.pay.api;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.base.util.QRCodeUtil;
import com.xuecheng.pay.model.dto.PayStatusDto;
import com.xuecheng.pay.model.po.AlipayConfig;
import com.xuecheng.pay.model.dto.AddOrderDto;
import com.xuecheng.pay.model.dto.PayRecordDto;
import com.xuecheng.pay.model.po.XcPayRecord;
import com.xuecheng.pay.service.OrderService;
import com.xuecheng.pay.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class OrderController {
    @Autowired
    OrderService orderService;

    //生成支付二维码：
    @PostMapping("/generatepaycode")
    public PayRecordDto generatePayCode(@RequestBody AddOrderDto addOrderDto){
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if(user==null){
            xueChengException.cast("请登录后继续选课");
        }
        String userId = user.getId();
        PayRecordDto payRecordDto = orderService.createOrder(userId, addOrderDto);
        //生成二维码：
        String qrCode=null;
        try {
            //生成二维码，路径经过网关。
            qrCode = new QRCodeUtil().createQRCode(
                    "http://localhost:6040/pay/requestpay?payNo="+
                    payRecordDto.getPayNo(),
                    200, 200);
        } catch (IOException e) {
            xueChengException.cast("生成二维码出错");
        }
        payRecordDto.setQrcode(qrCode);
        return payRecordDto;
    }
    //请求下单：

    @GetMapping("/requstpay")
    public void RequestOrder(@RequestParam("payNo") String payNo, HttpServletResponse response) throws IOException {
        XcPayRecord payRecord = orderService.getPayRecordByPayNo(payNo);
        if(payRecord==null){
            xueChengException.cast("请重新点击支付获取二维码");
        }
        //构建sdk的客户端对象实现第三方支付：
        AlipayClient alipayClient= new DefaultAlipayClient(AlipayConfig.serverUrl,
                AlipayConfig.APPID,AlipayConfig.RSA_PRIVATE_KEY,AlipayConfig.FORMAT,
                AlipayConfig.CHARSET,AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
        alipayRequest.setReturnUrl("");

        alipayRequest.setNotifyUrl("..<Geteway>/pay/paynotify");
        alipayRequest.setBizContent("");//json，填充业务参数；
        //请求下单接口：
        String form=null;
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        //将完整的表单html输出到页面：
        response.setContentType("text/html;charset="+ AlipayConfig.CHARSET);
        response.getWriter().write(form);
        response.getWriter().flush();
        response.getWriter().close();
    }
    //通知端口：
    @PostMapping("/paynotify")
    public void payNotify(HttpServletRequest request) throws UnsupportedEncodingException, AlipayApiException {
        Map<String,String> params=new HashMap<>();
        Map requestParams=request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();){
            String name= (String) iter.next();
            String [] values= (String[]) requestParams.get(name);
            String valStr="";
            for(int i=0;i<values.length;i++){
                valStr=(i== values.length-1)?valStr+values[i]:valStr+values[i]+",";
            }
//            valStr=new String(valStr.getBytes(StandardCharsets.ISO_8859_1),"gbk");
            params.put(name,valStr);
            //.....其他参数：
            //检验签名：

            boolean verify_res = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY,
                    AlipayConfig.CHARSET, "RSA2");
            if(verify_res){
                //商户订单号：
                String out_trade_no = new String(request.getParameter("out_trade_no").getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
                //支付宝交易号：
                String trade_no = new String(request.getParameter("trade_no").getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
                //交易状态：
                String trade_status=new String(request.getParameter("trade_status").getBytes(StandardCharsets.ISO_8859_1),"UTF-8");
                //appid:
                String app_id = new String(request.getParameter("app_id").getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
                //total_amount:
                String total_amount = new String(request.getParameter("total_amount").getBytes(StandardCharsets.ISO_8859_1), "UTF-8");
                //交易成功处理：
                if(trade_status.equals("TRADE_SUCCESS")){
                    //操作：
                    PayStatusDto payStatusDto=new PayStatusDto();
                    payStatusDto.setApp_id(app_id);
                    payStatusDto.setTrade_status(trade_status);
                    payStatusDto.setOut_trade_on(out_trade_no);
                    payStatusDto.setTotal_amount(total_amount);
                    payStatusDto.setTrade_on(trade_no);
                    //
                    orderService.saveAliPayStatus(payStatusDto);
                }
            }
        }
    }

    //
}

package com.xuecheng.pay.model.dto;

import com.xuecheng.pay.model.po.XcPayRecord;
import lombok.Data;

@Data
public class PayRecordDto extends XcPayRecord {

    //二维码：
    private String qrcode;
}

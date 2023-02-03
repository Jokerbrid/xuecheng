package com.xuechengauth.model.po;

import com.xuechengauth.domain.XcUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class XcUserExt extends XcUser {

    List<String> permissions=new ArrayList<>();
}

package com.xuechengauth.service;

import com.xuechengauth.model.dto.AuthParamsDto;
import com.xuechengauth.model.po.XcUserExt;

public interface AuthService {

    XcUserExt execute(AuthParamsDto authParamsDto);
}

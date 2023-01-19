package com.xuecheng.base.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *分页类。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {
    private Integer page;
    private Integer pageSize;
}

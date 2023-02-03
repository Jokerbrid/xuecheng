package com.xuecheng.context.model.dto;

import com.xuecheng.context.model.po.Teachplan;
import com.xuecheng.context.model.po.TeachplanMedia;
import lombok.Data;

import java.util.List;

@Data
public class TeachplanDto extends Teachplan {

    //媒资信息：
    TeachplanMedia teachplanMedia;
    //子节点：
    List<TeachplanDto> teachplanTreeNodes;
}

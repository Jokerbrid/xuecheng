package com.xuecheng.context.service;

import com.xuecheng.context.model.dto.BindTeachplanMediaDto;
import com.xuecheng.context.model.dto.CourseCategoryTreeDto;
import com.xuecheng.context.model.dto.SaveTeachplanDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.model.po.TeachplanMedia;

import java.util.List;

public interface TeachplanService {

     List<TeachplanDto> queryTreeNodes(Long courseId);

    void saveTeachplan(SaveTeachplanDto teachplan);

    void deleteTeachplan(Long id);

    void moveupTeachplan(Long techplanId);
    void movedownTeachplan(Long teachplanId);

    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    void delAassociationMedia(Long teachplanId,String mediaId);


}

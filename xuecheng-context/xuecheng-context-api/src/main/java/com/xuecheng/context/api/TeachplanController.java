package com.xuecheng.context.api;

import com.xuecheng.context.model.dto.BindTeachplanMediaDto;
import com.xuecheng.context.model.dto.SaveTeachplanDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.service.TeachplanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TeachplanController {

    @Autowired
    TeachplanService teachplanService;

    //查询教学计划：
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachplanDto> getTeachplanTree(@PathVariable Long courseId){
        return teachplanService.queryTreeNodes(courseId);
    }
    //添加教学计划
    @PostMapping("/teachplan")
    public void createTeachplan(@RequestBody SaveTeachplanDto teachplan){
        teachplanService.saveTeachplan(teachplan);
        return ;
    }
    //删除教学计划
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Long id){
        teachplanService.deleteTeachplan(id);return;
    }
    //上移计划：
    @PostMapping("/teachplan/moveup/{teachplanId}")
    public void moveupTeachplan(@PathVariable Long teachplanId){
        teachplanService.moveupTeachplan(teachplanId);
    }
    //下移移计划：
    @PostMapping("/teachplan/movedown/{teachplanId}")
    public void movedownTeacholan(@PathVariable Long teachplanId){
        teachplanService.movedownTeachplan(teachplanId);
    }

    //绑定媒资：
    @PostMapping("/teachplan/association/media")
    public void BindTeachplanMedia(@RequestBody BindTeachplanMediaDto dto){
        teachplanService.associationMedia(dto);
    }
    //删除媒资绑定
    @DeleteMapping("/teachplan/association/media/{teachPlanId}/{mediaId}")
    void delAssociationMedia(@PathVariable Long teachPlanId,@PathVariable String mediaId){
        teachplanService.delAassociationMedia(teachPlanId,mediaId);
    }

}

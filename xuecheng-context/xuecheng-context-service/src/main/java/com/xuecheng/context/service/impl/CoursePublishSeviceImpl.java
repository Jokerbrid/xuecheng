package com.xuecheng.context.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.xuecheng.base.exception.CommonError;
import com.xuecheng.base.exception.xueChengException;
import com.xuecheng.context.dao.CourseBaseMapper;
import com.xuecheng.context.dao.CourseMarketMapper;
import com.xuecheng.context.dao.CoursePublishMapper;
import com.xuecheng.context.dao.CoursePublishPreMapper;
import com.xuecheng.context.job.CoursePublishTask;
import com.xuecheng.context.model.dto.CourseBaseInfoDto;
import com.xuecheng.context.model.dto.TeachplanDto;
import com.xuecheng.context.model.po.*;
import com.xuecheng.context.service.CourseBaseInfoService;
import com.xuecheng.context.service.CoursePublishService;
import com.xuecheng.context.service.CourseTeacherService;
import com.xuecheng.context.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CoursePublishSeviceImpl implements CoursePublishService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    TeachplanService teachplanService;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        //查询课程的所有信息：
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //获取审核状态：
        String auditStatus=courseBase.getAuditStatus();
        //判断是否为审核已提交状态：
        if("202003".equals(auditStatus)){
            xueChengException.cast("当前为等待审核状态，等待审核结束后，可再次提交");
        }
        //只允许本机构提交本机构课程：
        if(!courseBase.getCompanyId().equals(companyId)){
            xueChengException.cast("不允许提交其他机构的课程");
        }
        //课程图片不为空：

        //添加到预审核表中：
        CoursePublishPre coursePublishPre=new CoursePublishPre();
        //课程基本信息：
        CourseBaseInfoDto courseBaseInfo=courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        //课程营销信息 json存储：
        //通过gson工具转换成json字符串存储：
        Gson gson=new Gson();
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJson= gson.toJson(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        //查询课程计划信息：
        List<TeachplanDto> teachplanTree=teachplanService.queryTreeNodes(courseId);
        if(teachplanTree.size()<=0){
            xueChengException.cast("提交失败，还没有添加课程计划");
        }
        //转json ：
        String teachplanJson=gson.toJson(teachplanTree);
        coursePublishPre.setTeachplan(teachplanJson);
        //查询老师信息：
        List<CourseTeacher> teachers=courseTeacherService.listByCourseId(courseId);
        String teachJson=gson.toJson(teachers);
        coursePublishPre.setTeachers(teachJson);

        //设置预发布状态：
        coursePublishPre.setStatus("202003");
        //设置机构id:
        coursePublishPre.setCompanyId(companyId);
        //设置提交时间：
        coursePublishPre.setCreateDate(new Date());
        //查询表：
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(coursePublishPre);
        if(coursePublishPreUpdate==null){
            //添加：
            coursePublishPreMapper.insert(coursePublishPre);
        }else{
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //基本表 更新审核状态： 已提交
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);
    }

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
        //查询预发布表：
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre==null){
            xueChengException.cast("先提交课程审核，审核通过才能发布");
        }
        if(coursePublishPre.getCompanyId().equals(courseId)){
            xueChengException.cast("只能提交本机构的课程");
        }
        //查看审核状态：
        String auditStatus=coursePublishPre.getStatus();
        //验证审核状态：
        if(!"202004".equals(auditStatus)){
            xueChengException.cast("审核通过才可以发布");
        }
        //保存课程发布信息：
        saveCoursePublish(courseId);
        //保存消息表：
        saveCoursePublishMessage(courseId);
        //删除预发布表对应的记录：
        coursePublishPreMapper.deleteById(coursePublishPre);
    }

    @Override
    @Transactional
    public void OffLine(Long companyId, Long courseId) {
        //修改课程发布表的发布状态：
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate==null){
            xueChengException.cast("课程未发布");
        }
        //设置下线状态：
        coursePublishUpdate.setStatus("203003");
        coursePublishMapper.updateById(coursePublishUpdate);


        //基本表设置下线状态：
        CourseBase courseBaseUpdate = courseBaseMapper.selectById(courseId);
        if(courseBaseUpdate==null){
            xueChengException.cast("没有找到该课程");
        }
        courseBaseUpdate.setStatus("203003");
        courseBaseMapper.updateById(courseBaseUpdate);

    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File htmlFile=null;
        try {
            //配置freemarker
            Configuration configuration=new Configuration(Configuration.getVersion());
            //获取指定路径：
            String classpath="E:\\joker\\ideaItem\\xuecheng\\xuecheng-context\\xuecheng-context-api\\src\\main\\resources";
            configuration.setDirectoryForTemplateLoading(new File(classpath+"/templates/"));
            //设置字符编码：
            configuration.setDefaultEncoding("utf-8");
            //指定模板文件名称：
            Template template=configuration.getTemplate("course_template.ftl");
            //装配数据：

            Map<String,Object> map=new HashMap<>();
            map.put("model",null);
            //静态化：
            String content= FreeMarkerTemplateUtils.processTemplateIntoString(template,map);
            //将静态化内容输出到文件：
            InputStream inputStream= IOUtils.toInputStream(content);
            //创建静态文件：
            htmlFile=File.createTempFile("course",".html");
            //

            //输出流：
            FileOutputStream outputStream=new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream,outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
//        MultipartFile multipartFile =MultipartSupportConfig.getMultipartFile(file);
//        String course = mediaServiceClient.uploadFile(multipartFile,"course", courseId+".html");
    }


    public CoursePublish getCoursePublish(Long courseId) {
        return coursePublishMapper.selectById(courseId);
    }

    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        String jsonString = (String) redisTemplate.opsForValue().get("course_" + courseId);
        Gson gson=new Gson();
        if(StringUtils.isNotBlank(jsonString)){
            System.out.println("========从redis查找==========");
            //特殊值处理：
            if(jsonString.equals("null")){
                return null;
            }
            CoursePublish coursePublish = gson.fromJson(jsonString, CoursePublish.class);
            return coursePublish;
        }else{
            CoursePublish coursePublish=null;
            synchronized (this) {
                //设置本地同步锁，对查询数据库只做一次操作。
                //从数据库中查找：
                System.out.println("==============从数据库中查找===============");
                 coursePublish = getCoursePublish(courseId);
                if (coursePublish != null) {
                    //添加到缓存中：
                    //设置过期时间为300s
                    redisTemplate.opsForValue().set("course_" + courseId, gson.toJson(coursePublish), 300, TimeUnit.SECONDS);
                }
            }
            return coursePublish;
        }
    }

    //保存课程发布信息，并更新基本表的状态信息：
    private void saveCoursePublish(Long courseId){
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre==null){
            xueChengException.cast("预发布表信息为空");
        }
        CoursePublish coursePublish=new CoursePublish();
        //拷贝数据：
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //设置发布状态：已发布。
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(coursePublish);
        if(coursePublishUpdate==null){
            //插入表：
            coursePublishMapper.insert(coursePublish);
        }else{
            //更新：
            coursePublishMapper.updateById(coursePublish);
        }
        //更新基本表的状态信息:
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }

    //保存发布消息：
    private void saveCoursePublishMessage(Long courseId){
        MqMessage mqMessage = mqMessageService.addMessage(CoursePublishTask.MESSAGE_TYPE,
                String.valueOf(courseId), null, null);
        if(mqMessage==null){
            xueChengException.cast(CommonError.UNKNOWN_ERROR);
        }
    }

}

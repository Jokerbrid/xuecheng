package com.xuecheng.learning.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author joker
 * @since 2023-02-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class XcCourseTables implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 选课订单id
     */
    private Long chooseCourseId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 机构id
     */
    private Long companyId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程类型
     */
    private String courseType;

    /**
     * 添加时间
     */
    private Date createDate;

    /**
     * 开始服务时间
     */
    private Date validtimeStart;

    /**
     * 到期时间
     */
    private Date validtimeEnd;

    /**
     * 更新时间
     */
    private Date updateDate;

    /**
     * 备注
     */
    private String remarks;


}

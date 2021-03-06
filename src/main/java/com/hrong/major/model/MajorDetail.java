package com.hrong.major.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author hrong
 * @since 2019-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MajorDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 专业名字
     */
    private String name;
    /**
     * 编码
     */
    private String code;
    /**
     * 学制
     */
    private String year;

    /**
     * 培养目标
     */
    private String description;

    /**
     * 核心课程
     */
    private String course;

    /**
     * 就业方向
     */
    private String job;

    /**
     * 能力要求
     */
    private String ability;
    /**
     * 专业外键
     */
    private Integer majorId;
    /**
     * 删除标志
     */
    private Integer deleted;

}

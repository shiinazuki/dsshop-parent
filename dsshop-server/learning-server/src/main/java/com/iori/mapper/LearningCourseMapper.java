package com.iori.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iori.bean.LearningCourse;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName LearningCourseMapper
 * @Description mapper接口
 * @Author iori
 * @Date 2023/07/13 15:15
 **/
@Mapper
public interface LearningCourseMapper extends BaseMapper<LearningCourse> {

}

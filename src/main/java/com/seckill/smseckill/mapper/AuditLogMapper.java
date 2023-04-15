package com.seckill.smseckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.smseckill.entity.Auditlog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.mapper
 * @ClassName: AuditLogMapper
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 11:34
 * @Version: 1.0
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<Auditlog> {
    //@Select("SELECT * FROM `t_auditlog` WHERE timestamp BETWEEN #{start} AND #{end}" +
    //        "<if test=' ${userId} != null and ${userId} != \"\"'> AND user_id LIKE CONCAT('%', #{userId}, '%')</if>")
    @Select("SELECT * FROM `t_auditlog` WHERE (timestamp BETWEEN #{start} AND #{end} OR #{start} IS NULL OR #{end} IS NULL) AND (user_id = #{userId} OR #{userId} IS NULL)")
    List<Auditlog> findByTimestampBetweenAndUserIdContaining(@Param("start") Date start, @Param("end") Date end, @Param("userId") Long userId);
}


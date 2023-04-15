package com.seckill.smseckill.service.impl;

import com.seckill.smseckill.entity.Auditlog;
import com.seckill.smseckill.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.service
 * @ClassName: AuditLogService
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 11:30
 * @Version: 1.0
 */
@Service
public class  AuditLogServiceImpl {
    @Autowired
    private AuditLogMapper logMapper;

    public List<Auditlog> getLogs(Date start, Date end, Long userId){
        return logMapper.findByTimestampBetweenAndUserIdContaining(start, end, userId);
    }
}

package com.seckill.smseckill.controller;

import com.seckill.smseckill.entity.Auditlog;
import com.seckill.smseckill.service.impl.AuditLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.controller
 * @ClassName: LogController
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/21 11:28
 * @Version: 1.0
 */
@RestController
public class LogController {

    @Autowired
    private AuditLogServiceImpl logService;

    @GetMapping("/logs")
    public List<Auditlog> getLogs(@RequestParam(required=false) String startDate,
                                  @RequestParam(required=false) String endDate,
                                  @RequestParam(required=false) Long userId){
        // 将参数转换为Date类型，如果参数为空则设置为null
        Date start = null;
        Date end = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(startDate != null && !startDate.equals("")) {
                start = sdf.parse(startDate);
            }
            if(endDate != null && !endDate.equals("")) {
                end = sdf.parse(endDate);
                // 结束日期加1天，保证查询结果包括结束日期当天的数据
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(end);
                calendar.add(Calendar.DATE, 1);
                end = calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return logService.getLogs(start, end, userId);
    }
}

package com.seckill.smseckill.utils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seckill.smseckill.entity.User;
import com.seckill.smseckill.service.UserService;
import com.seckill.smseckill.vo.RespBean;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: sm-seckill
 * @Package: com.seckill.smseckill.utils
 * @ClassName: UserUtil
 * @Author: Vanessa
 * @Description:
 * @Date: 2023/3/4 18:00
 * @Version: 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserUtil {
    @Autowired
    UserService userService;
    private void createUser(int count) throws IOException {
        List<User> userList = new ArrayList<>(count);
        for(int i = 0; i < count; i++){
            User user = new User();
            user.setId(15800000000L + i);
            user.setNickname("user" + i);
            user.setSalt("1a2b3c4d");
            user.setPassword(MD5Util.inputPassToDBPass("123456","1a2b3c4d"));
            userList.add(user);
        }
        //userService.saveBatch(userList);
        String url = "http://localhost:8080/smseckill/doLogin";
        File file = new File("C:\\Users\\54381\\Desktop\\config.txt");
        if(file.exists()){
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.seek(0);
        for(int i = 0; i < userList.size(); i++){
            User user = userList.get(i);
            URL url1 = new URL(url);
            HttpURLConnection co = (HttpURLConnection) url1.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile="+user.getId()+"&password="+MD5Util.inputPassToFromPass("123456");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = 0;
            while((len=inputStream.read(bytes))>=0){
                bout.write(bytes, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = (String) respBean.getObj();
            String row = user.getId() + "," + userTicket;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\r\n".getBytes());
        }
        randomAccessFile.close();
    }
    @Test
    public void doCreate() throws IOException {
        createUser(5000);
    }
}

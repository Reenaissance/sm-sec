package com.seckill.smseckill;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.seckill.smseckill.mapper")
public class SmSeckillApplication {
	public static void main(String[] args) {
		SpringApplication.run(SmSeckillApplication.class, args);
	}

}

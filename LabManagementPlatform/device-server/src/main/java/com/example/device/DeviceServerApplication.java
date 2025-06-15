package com.example.device;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.device", "org.example.core"})
@MapperScan("com.example.device.mapper")
public class DeviceServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceServerApplication.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  实验设备管理服务启动成功：时间{}   ლ(´ڡ`ლ)ﾞ  \n", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}

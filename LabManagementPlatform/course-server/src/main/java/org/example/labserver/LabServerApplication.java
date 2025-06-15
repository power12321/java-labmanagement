package org.example.labserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"org.example.labserver", "org.example.core"})
@MapperScan("org.example.labserver.mapper")
public class LabServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabServerApplication.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  实验室管理服务启动成功：时间{}   ლ(´ڡ`ლ)ﾞ  \n", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}

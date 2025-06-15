package org.example.configserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import java.util.Date;

@Slf4j
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  配置中心服务启动成功：时间{}   ლ(´ڡ`ლ)ﾞ  \n", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }

}

package org.example.gatewayserver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;

@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {"org.example.gatewayserver", "org.example.core.util"})
public class GatewayServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
        log.info("(♥◠‿◠)ﾉﾞ  网关服务启动成功：时间{}   ლ(´ڡ`ლ)ﾞ  \n", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
    }
}

package cn.springcloud.service.a;

import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.DefaultFeignLoggerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import feign.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by saleson on 2017/10/18.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(  )
public class ServiceAApplication {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ServiceAApplication.class);


    public static void main(String[] args) throws UnknownHostException {
        Environment env = new SpringApplicationBuilder(ServiceAApplication.class).run(args).getEnvironment();
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\thttp://127.0.0.1:{}\n\t"
                        + "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(), env.getProperty("server.port"));

        String configServerStatus = env.getProperty("configserver.status");
        log.info(
                "\n----------------------------------------------------------\n\t"
                        + "Config Server: \t{}\n----------------------------------------------------------",
                configServerStatus == null ? "Not found or not setup for this application" : configServerStatus);
    }
    @Bean
    Logger.Level feignLoggerLevel() {

    	return Logger.Level.FULL;

      }
}

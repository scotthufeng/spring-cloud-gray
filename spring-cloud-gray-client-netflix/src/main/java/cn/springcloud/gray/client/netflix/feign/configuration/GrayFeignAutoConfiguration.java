package cn.springcloud.gray.client.netflix.feign.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.ILoadBalancer;

import cn.springcloud.gray.GrayManager;
import cn.springcloud.gray.client.netflix.feign.GrayTrackFeignRequestInterceptor;
import cn.springcloud.gray.request.RequestLocalStorage;
import feign.Feign;
import feign.Logger;

/**
 * Created by saleson on 2017/11/9.
 */
@Configuration
@ConditionalOnBean(GrayManager.class)
@ConditionalOnClass(value = {ILoadBalancer.class, Feign.class})
@EnableFeignClients(defaultConfiguration = {GrayFeignClientsConfiguration.class})
public class GrayFeignAutoConfiguration {


    @Configuration
    @ConditionalOnProperty(value = "gray.request.track.enabled", matchIfMissing = true)
    public static class GrayTrackFeignConfiguration {


//        @Bean
//        public FeignRequestInterceptor feignRequestInterceptor() {
//            return new FeignRequestInterceptor();
//        }

        @Bean
        public GrayTrackFeignRequestInterceptor grayTrackFeignRequestInterceptor(RequestLocalStorage requestLocalStorage) {
            return new GrayTrackFeignRequestInterceptor(requestLocalStorage);
        }
        
        @Bean
        Logger.Level feignLoggerLevel() {

        	return Logger.Level.FULL;

          }
    }


}

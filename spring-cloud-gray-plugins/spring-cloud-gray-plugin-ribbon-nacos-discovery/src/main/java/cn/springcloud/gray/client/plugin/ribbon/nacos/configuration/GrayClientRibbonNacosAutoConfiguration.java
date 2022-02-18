package cn.springcloud.gray.client.plugin.ribbon.nacos.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServerList;
import com.netflix.loadbalancer.Server;
import com.netflix.ribbon.Ribbon;

import cn.springcloud.gray.client.config.properties.GrayHoldoutServerProperties;
import cn.springcloud.gray.client.plugin.ribbon.nacos.NacosServerExplainer;
import cn.springcloud.gray.client.plugin.ribbon.nacos.NacosServerListProcessor;
import cn.springcloud.gray.servernode.ServerExplainer;
import cn.springcloud.gray.servernode.ServerListProcessor;
import cn.springcloud.gray.servernode.VersionExtractor;

@Configuration
@ConditionalOnProperty(value = "gray.enabled")
@ConditionalOnClass({Ribbon.class, NacosServerList.class})
@AutoConfigureBefore(name = {"cn.springcloud.gray.client.netflix.configuration.NetflixRibbonGrayAutoConfiguration"})
public class GrayClientRibbonNacosAutoConfiguration {


    @Bean
    @ConditionalOnProperty(value = "gray.holdout-server.enabled")
    public ServerListProcessor<Server> serverListProcessor(
            GrayHoldoutServerProperties grayHoldoutServerProperties, NacosDiscoveryProperties discoveryProperties){
        return new NacosServerListProcessor(grayHoldoutServerProperties, discoveryProperties.namingServiceInstance());
    }


    @Bean
    public ServerExplainer<Server> serverExplainer(
            SpringClientFactory springClientFactory, VersionExtractor<Server> versionExtractor) {
        return new NacosServerExplainer(springClientFactory, versionExtractor);
    }

}

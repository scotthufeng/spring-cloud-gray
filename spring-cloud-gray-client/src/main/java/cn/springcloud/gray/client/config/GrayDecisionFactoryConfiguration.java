package cn.springcloud.gray.client.config;

import cn.springcloud.gray.decision.DefaultGrayDecisionFactoryKeeper;
import cn.springcloud.gray.decision.GrayDecisionFactoryKeeper;
import cn.springcloud.gray.decision.factory.*;
import cn.springcloud.gray.dynamic.decision.DynamicGrayDecisionFactoryKeeper;
import cn.springcloud.gray.dynamiclogic.DynamicLogicDriver;
import cn.springcloud.gray.dynamiclogic.DynamicLogicManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;

@Configuration
public class GrayDecisionFactoryConfiguration {


    @Configuration
    public static class WebGrayDecisionFactoryConfiguration {

        @Bean
        public HttpHeaderGrayDecisionFactory httpHeaderGrayDecisionFactory() {
            return new HttpHeaderGrayDecisionFactory();
        }

        @Bean
        public HttpMethodGrayDecisionFactory httpMethodGrayDecisionFactory() {
            return new HttpMethodGrayDecisionFactory();
        }

        @Bean
        public HttpParameterGrayDecisionFactory httpParameterGrayDecisionFactory() {
            return new HttpParameterGrayDecisionFactory();
        }

        @Bean
        public TraceIpGrayDecisionFactory traceIpGrayDecisionFactory() {
            return new TraceIpGrayDecisionFactory();
        }

        @Bean
        public TraceIpsGrayDecisionFactory traceIpsGrayDecisionFactory() {
            return new TraceIpsGrayDecisionFactory();
        }

        @Bean
        public HttpTrackParameterGrayDecisionFactory httpTrackParameterGrayDecisionFactory() {
            return new HttpTrackParameterGrayDecisionFactory();
        }

        @Bean
        public HttpTrackHeaderGrayDecisionFactory httpTrackHeaderGrayDecisionFactory() {
            return new HttpTrackHeaderGrayDecisionFactory();
        }

        @Bean
        public TrackAttributeGrayDecisionFactory trackAttributeGrayDecisionFactory() {
            return new TrackAttributeGrayDecisionFactory();
        }

        @Bean
        public TrackAttributesGrayDecisionFactory trackAttributesGrayDecisionFactory() {
            return new TrackAttributesGrayDecisionFactory();
        }

        @Bean
        public FlowRateGrayDecisionFactory flowRateGrayDecisionFactory() {
            return new FlowRateGrayDecisionFactory();
        }
    }


    @Configuration
    @ConditionalOnBean(DynamicLogicDriver.class)
    public class DynamicGrayDecisionFactoryConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public GrayDecisionFactoryKeeper grayDecisionFactoryKeeper(
                DynamicLogicDriver dynamicLogicDriver,
                Validator validator, List<GrayDecisionFactory> decisionFactories) {
            DynamicLogicManager dynamicLogicManager =
                    dynamicLogicDriver.getDynamicLogicManager(DynamicGrayDecisionFactoryKeeper.GRAY_DECISION_DYNAMIC_TYPE);
            if (Objects.isNull(dynamicLogicManager)) {
                return new DefaultGrayDecisionFactoryKeeper(
                        DefaultConversionService.getSharedInstance(), validator, decisionFactories);
            }
            return new DynamicGrayDecisionFactoryKeeper(
                    dynamicLogicManager, DefaultConversionService.getSharedInstance(), validator, decisionFactories);

        }
    }


    /**
     * ????????????spring mvc??????ConversionService??? ???????????????feign ?????????????????????ServletContext??? ??????????????????:No ServletContext set
     *
     * @param validator         ?????????
     * @param decisionFactories ???????????????????????????
     * @return ???????????????????????????
     */
    @Bean
    @ConditionalOnMissingBean
    public GrayDecisionFactoryKeeper grayDecisionFactoryKeeper(
            Validator validator, List<GrayDecisionFactory> decisionFactories) {
        return new DefaultGrayDecisionFactoryKeeper(
                DefaultConversionService.getSharedInstance(), validator, decisionFactories);

    }


}

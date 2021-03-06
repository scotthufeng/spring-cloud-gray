package cn.springcloud.gray;

import cn.springcloud.gray.changed.notify.ChangedListener;
import cn.springcloud.gray.changed.notify.ChangedNotifyDriver;
import cn.springcloud.gray.choose.PolicyPredicate;
import cn.springcloud.gray.choose.ServerChooser;
import cn.springcloud.gray.choose.loadbalance.factory.LoadBalancerFactory;
import cn.springcloud.gray.client.switcher.GraySwitcher;
import cn.springcloud.gray.decision.PolicyDecisionManager;
import cn.springcloud.gray.request.LocalStorageLifeCycle;
import cn.springcloud.gray.request.RequestLocalStorage;
import cn.springcloud.gray.request.track.GrayTrackHolder;
import cn.springcloud.gray.servernode.ServerExplainer;
import cn.springcloud.gray.servernode.ServerListProcessor;
import cn.springcloud.gray.spring.SpringEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

@Slf4j
public class GrayClientInitializer implements ApplicationContextAware, InitializingBean {
    private ApplicationContext cxt;

    @Override
    public void afterPropertiesSet() throws Exception {
        GrayClientHolder.setGrayManager(getBean("grayManager", GrayManager.class));
        GrayClientHolder.setRequestLocalStorage(getBean("requestLocalStorage", RequestLocalStorage.class));
        GrayClientHolder.setLocalStorageLifeCycle(getBean("localStorageLifeCycle", LocalStorageLifeCycle.class));
        GrayClientHolder.setServerExplainer(getBean("serverExplainer", ServerExplainer.class));
        GrayClientHolder.setServerListProcessor(
                getBean("serverListProcessor", ServerListProcessor.class, new ServerListProcessor.Default()));
        GrayClientHolder.setGraySwitcher(
                getBean("graySwitcher", GraySwitcher.class, new GraySwitcher.DefaultGraySwitcher()));
        GrayClientHolder.setServerChooser(getBean("serverChooser", ServerChooser.class));
        GrayClientHolder.setGrayTrackHolder(getBean("grayTrackHolder", GrayTrackHolder.class));
        GrayClientHolder.setPolicyDecisionManager(getBean("policyDecisionManager", PolicyDecisionManager.class));
        GrayClientHolder.setSpringEventPublisher(getBean("springEventPublisher", SpringEventPublisher.class));
        GrayClientHolder.setLoadBalancerFactory(getBean("loadBalancerFactory", LoadBalancerFactory.class));

        initGrayManagerRequestInterceptors();

        registerPolicyPredicates();

        initChangedNotifyDriver();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.cxt = applicationContext;
    }


    private <T> T getBean(String beanName, Class<T> cls) {
        T t = null;
        try {
            t = cxt.getBean(beanName, cls);
        } catch (BeansException e) {
            log.warn("?????????spring???????????????name???'{}', class???'{}'???Bean", beanName, cls);
        }
        if (t == null) {
            t = cxt.getBean(cls);
        }
        return t;
    }

    private <T> T getBean(String beanName, Class<T> cls, T defaultBean) {
        try {
            return getBean(beanName, cls);
        } catch (BeansException e) {
            log.warn("?????????spring???????????????name???'{}', class???'{}'???Bean, ???????????????bean:{}", beanName, cls, defaultBean);
            return defaultBean;
        }
    }

    private <T> T getBeanNullable(String beanName, Class<T> cls) {
        try {
            return getBean(beanName, cls);
        } catch (BeansException e) {
            log.warn("?????????spring???????????????name???'{}', class???'{}'???Bean", beanName, cls);
            return null;
        }
    }


    /**
     * ???????????????????????????GrayManger??????Sping????????????RequestInterceptor?????????????????????
     */
    private void initGrayManagerRequestInterceptors() {
        Map<String, RequestInterceptor> requestInterceptors = cxt.getBeansOfType(RequestInterceptor.class);
        GrayManager grayManager = GrayClientHolder.getGrayManager();
        if (grayManager instanceof UpdateableGrayManager) {
            ((UpdateableGrayManager) grayManager).setRequestInterceptors(requestInterceptors.values());
        }
        grayManager.setup();
    }


    /**
     * ???PolicyDecisionManager??????PolicyPredicate
     */
    private void registerPolicyPredicates() {
        Map<String, PolicyPredicate> policyPredicates = cxt.getBeansOfType(PolicyPredicate.class);
        PolicyDecisionManager policyDecisionManager = GrayClientHolder.getPolicyDecisionManager();
        policyPredicates.values().forEach(policyDecisionManager::registerPolicyPredicate);
    }


    private void initChangedNotifyDriver() {
        ChangedNotifyDriver changedNotifyDriver = getBean("changedNotifyDriver", ChangedNotifyDriver.class);
        changedNotifyDriver.registerListeners(cxt.getBeansOfType(ChangedListener.class).values());
        GrayClientHolder.setChangedNotifyDriver(changedNotifyDriver);
    }

}

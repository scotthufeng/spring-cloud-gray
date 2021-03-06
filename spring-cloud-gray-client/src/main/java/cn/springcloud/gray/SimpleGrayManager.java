package cn.springcloud.gray;

import cn.springcloud.gray.decision.PolicyDecisionManager;
import cn.springcloud.gray.local.InstanceLocalInfo;
import cn.springcloud.gray.model.GrayInstance;
import cn.springcloud.gray.model.GrayService;
import cn.springcloud.gray.model.ServiceRouteInfo;
import cn.springcloud.gray.request.track.GrayTrackHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SimpleGrayManager extends AbstractGrayManager {


    protected Map<String, GrayService> grayServices = new ConcurrentHashMap<>();
    protected Lock lock = new ReentrantLock();
    private GrayTrackHolder grayTrackHolder;
    private PolicyDecisionManager policyDecisionManager;

    public SimpleGrayManager(GrayTrackHolder grayTrackHolder, PolicyDecisionManager policyDecisionManager) {
        this.grayTrackHolder = grayTrackHolder;
        this.policyDecisionManager = policyDecisionManager;
    }

    @Override
    public boolean hasInstanceGray(String serviceId) {
        GrayService grayService = grayServices.get(serviceId);
        return grayService != null && !grayService.getGrayInstances().isEmpty();
    }

    @Override
    public Collection<GrayService> allGrayServices() {
        return Collections.unmodifiableCollection(grayServices.values());
    }

    @Override
    public void clearAllGrayServices() {
        grayServices.clear();
    }

    @Override
    public GrayService getGrayService(String serviceId) {
        return grayServices.get(serviceId);
    }

    @Override
    public GrayService createGrayService(String serviceId) {
        lock.lock();
        try {
            GrayService grayService = grayServices.get(serviceId);
            if (Objects.isNull(grayService)) {
                grayService = new GrayService();
                grayService.setServiceId(serviceId);
                grayServices.put(serviceId, grayService);
            }
            return grayService;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateServiceRouteInfo(ServiceRouteInfo serviceRouteInfo) {
        lock.lock();
        try {
            super.updateServiceRouteInfo(serviceRouteInfo);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeGrayService(String serviceId) {
        lock.lock();
        try {
            grayServices.remove(serviceId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public GrayInstance getGrayInstance(String serviceId, String instanceId) {
        GrayService service = getGrayService(serviceId);
        return service != null ? service.getGrayInstance(instanceId) : null;
    }


    @Override
    public void updateGrayInstance(GrayInstance instance) {
        if (instance == null) {
            return;
        }

        lock.lock();
        try {
            //?????????????????????????????????
            if (!instance.isGray()) {
                closeGray(instance);
                return;
            }
            updateGrayInstance(grayServices, GrayInstance.copyof(instance));
        } finally {
            lock.unlock();
        }
    }

    protected void updateGrayInstance(Map<String, GrayService> grayServices, GrayInstance instance) {
        InstanceLocalInfo instanceLocalInfo = GrayClientHolder.getInstanceLocalInfo();
        if (instanceLocalInfo != null) {
            if (StringUtils.equals(instanceLocalInfo.getServiceId(), instance.getServiceId())) {
                return;
            }
        }

        GrayService service = grayServices.get(instance.getServiceId());
        if (service == null) {
            service = new GrayService();
            service.setServiceId(instance.getServiceId());
            grayServices.put(service.getServiceId(), service);
        }
        log.debug("??????????????????, serviceId:{}, instanceId:{}", instance.getServiceId(), instance.getInstanceId());
        service.setGrayInstance(instance);
    }

    @Override
    public void closeGray(GrayInstance instance) {
        closeGray(instance.getServiceId(), instance.getInstanceId());
    }

    @Override
    public void closeGray(String serviceId, String instanceId) {
        GrayService service = getGrayService(serviceId);
        if (service == null) {
            log.debug("????????????????????????:{}, ????????????????????????:{} ???????????????", serviceId, instanceId);
            return;
        }
        log.debug("??????????????????????????????, serviceId:{}, instanceId:{}", serviceId, instanceId);
        lock.lock();
        try {
            service.removeGrayInstance(instanceId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void setup() {

    }


    @Override
    public void shutdown() {

    }

    @Override
    public GrayTrackHolder getGrayTrackHolder() {
        return grayTrackHolder;
    }

    @Override
    public PolicyDecisionManager getPolicyDecisionManager() {
        return policyDecisionManager;
    }


    @Override
    public void setGrayServices(Object grayServices) {
        if (grayServices instanceof Map) {
            this.grayServices = (Map<String, GrayService>) grayServices;
        } else {
            throw new UnsupportedOperationException("setGrayServices(grayServices) ???????????????????????????");
        }
    }


}

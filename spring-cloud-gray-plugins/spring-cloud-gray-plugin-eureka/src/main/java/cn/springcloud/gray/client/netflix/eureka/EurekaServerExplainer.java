package cn.springcloud.gray.client.netflix.eureka;

import cn.springcloud.gray.servernode.ServerExplainer;
import cn.springcloud.gray.servernode.ServerSpec;
import cn.springcloud.gray.servernode.VersionExtractor;
import com.netflix.loadbalancer.Server;
import org.springframework.cloud.netflix.ribbon.DefaultServerIntrospector;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.util.Map;

public class EurekaServerExplainer implements ServerExplainer<Server> {

    private SpringClientFactory springClientFactory;
    private VersionExtractor<Server> versionExtractor;

    public EurekaServerExplainer(SpringClientFactory springClientFactory, VersionExtractor<Server> versionExtractor) {
        this.springClientFactory = springClientFactory;
        this.versionExtractor = versionExtractor;
    }

    @Override
    public VersionExtractor getVersionExtractor() {
        return versionExtractor;
    }

    @Override
    public ServerSpec<Server> apply(Server server) {
        Map metadata = getServerMetadata(server.getMetaInfo().getServiceIdForDiscovery(), server);
        return ServerSpec.<Server>builder()
                .server(server)
                .instanceId(getInstaceId(server))
                .serviceId(getServiceId(server))
                .metadata(metadata).build();
    }

    @Override
    public String getServiceId(Server server) {
        return server.getMetaInfo().getServiceIdForDiscovery();
    }

    @Override
    public String getInstaceId(Server server) {
        return server.getMetaInfo().getInstanceId();
    }

    @Override
    public Map getMetadata(Server server) {
        return serverIntrospector(getServiceId(server)).getMetadata(server);
    }

    public ServerIntrospector serverIntrospector(String serviceId) {
        if (springClientFactory == null) {
            return new DefaultServerIntrospector();
        }
        ServerIntrospector serverIntrospector = this.springClientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }

    /**
     * ???????????????metadata??????
     *
     * @param serviceId ??????id
     * @param server    ribbon?????????(????????????)
     * @return ???????????????metadata??????
     */
    public Map<String, String> getServerMetadata(String serviceId, Server server) {
        return serverIntrospector(serviceId).getMetadata(server);
    }
}

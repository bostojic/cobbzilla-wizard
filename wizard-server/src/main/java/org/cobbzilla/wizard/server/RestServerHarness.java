package org.cobbzilla.wizard.server;

import edu.emory.mathcs.backport.java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.cobbzilla.util.reflect.ReflectionUtil;
import org.cobbzilla.wizard.server.config.RestServerConfiguration;
import org.cobbzilla.wizard.server.config.factory.ConfigurationSource;
import org.cobbzilla.wizard.server.config.factory.RestServerConfigurationFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class RestServerHarness<C extends RestServerConfiguration, S extends RestServer<C>> {

    @Getter @Setter private Class<S> restServerClass;
    @Getter @Setter private List<ConfigurationSource> configurations = new ArrayList<>();
    @Getter private S server = null;
    @Getter private C configuration = null;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public RestServerHarness(Class<S> restServerClass) {
        this.restServerClass = restServerClass;
    }

    public void addConfiguration(ConfigurationSource source) { configurations.add(source); }
    public void addConfigurations(List<ConfigurationSource> sources) { configurations.addAll(sources); }

    public synchronized void startServer() throws Exception { startServer(null); }

    public synchronized void startServer(Map<String, String> env) throws Exception {
        if (!started.getAndSet(true)) {
            if (server == null) init(env);
            server.startServer();
        } else {
            log.warn("startServer: server already started");
        }
    }

    public synchronized void init(Map<String, String> env) throws InstantiationException, IllegalAccessException, IOException {
        if (server == null) {
            server = getRestServerClass().newInstance();

            final Class<C> configurationClass = ReflectionUtil.getTypeParameter(getRestServerClass(), RestServerConfiguration.class);
            final RestServerConfigurationFactory<C> factory = new RestServerConfigurationFactory<>(configurationClass);
            configuration = filterConfiguration(factory.build(configurations, env));
            configuration.setEnvironment(env);
            server.setConfiguration(configuration);
            log.info("starting " + configuration.getServerName() + ": " + server.getClass().getName() + " with config: " + configuration);
        }
    }

    private List<RestServerConfigurationFilter> configurationFilters = new ArrayList<>();
    public void addConfigurationFilter(RestServerConfigurationFilter filter) {
        configurationFilters.add(filter);
    }

    protected C filterConfiguration(C configuration) {
        for (RestServerConfigurationFilter filter : configurationFilters) {
            configuration = (C) filter.filterConfiguration(configuration);
        }
        return configuration;
    }

    public synchronized void stopServer () throws Exception {
        if (server != null) {
            server.stopServer();
            server = null;
        }
    }

    public ConfigurableApplicationContext springServer(List<ConfigurationSource> configurationSources,
                                                       Map<String, String> env) throws Exception {
        addConfigurations(configurationSources);
        init(env == null || env.isEmpty() ? System.getenv() : env);
        return server.buildSpringApplicationContext();
    }
}

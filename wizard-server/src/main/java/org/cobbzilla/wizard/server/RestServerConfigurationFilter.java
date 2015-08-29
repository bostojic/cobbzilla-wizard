package org.cobbzilla.wizard.server;

import org.cobbzilla.wizard.server.config.RestServerConfiguration;

public interface RestServerConfigurationFilter<C extends RestServerConfiguration> {

    public C filterConfiguration(C configuration);

}

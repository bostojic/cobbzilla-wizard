package org.cobbzilla.wizard.server.config;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpConfiguration {

    @Getter @Setter private Integer port = null;
    public boolean hasPort() { return port != null && port != 0; }

    @Getter @Setter private String baseUri;

    public String getHost () throws URISyntaxException {
        return new URI(baseUri).getHost();
    }
}

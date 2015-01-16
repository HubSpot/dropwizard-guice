package com.hubspot.dropwizard.guice;

import javax.inject.Inject;
import javax.inject.Named;

public class SimpleService {

    private final String host;

    @Inject
    public SimpleService(@Named("HostName") String host) {
        this.host = host;
    }

    public String getHost () {
        return host;
    }

}

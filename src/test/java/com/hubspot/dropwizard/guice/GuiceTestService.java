package com.hubspot.dropwizard.guice;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class GuiceTestService {

    private final String host;

    @Inject
    public GuiceTestService(@Named("HostName") String host) {
        this.host = host;
    }

    public String getHost () {
        return host;
    }

}

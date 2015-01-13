package com.hubspot.dropwizard.guice;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class JavaxInjectedService {

    private final String host;

    @Inject
    public JavaxInjectedService( @Named("HostName") String host) {
        this.host = host;
    }

    public String getHost () {
        return host;
    }

}

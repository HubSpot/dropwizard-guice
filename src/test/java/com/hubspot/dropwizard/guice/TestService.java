package com.hubspot.dropwizard.guice;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TestService {

    private final TestConfiguration configuration;

    @Inject
    public TestService(TestConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getHost () {
        return configuration.getHost();
    }

}

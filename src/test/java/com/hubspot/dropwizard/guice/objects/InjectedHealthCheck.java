package com.hubspot.dropwizard.guice.objects;

import com.hubspot.dropwizard.guice.InjectableHealthCheck;

public class InjectedHealthCheck extends InjectableHealthCheck {
    @Override
    public String getName() {
        return "healthcheck";
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}

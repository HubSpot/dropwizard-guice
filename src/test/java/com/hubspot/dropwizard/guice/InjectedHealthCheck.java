package com.hubspot.dropwizard.guice;

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

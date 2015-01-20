package com.hubspot.dropwizard.guice;

import io.dropwizard.lifecycle.Managed;

import javax.inject.Singleton;

@Singleton
public class InjectedManaged implements Managed {
    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }
}

package com.hubspot.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class GuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("HostName")).to("localhost");
    }
}


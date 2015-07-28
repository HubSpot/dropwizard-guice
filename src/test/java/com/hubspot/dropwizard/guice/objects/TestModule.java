package com.hubspot.dropwizard.guice.objects;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExplicitDAO.class);
        bindConstant().annotatedWith(Names.named("TestTaskName")).to("test task");
    }
}


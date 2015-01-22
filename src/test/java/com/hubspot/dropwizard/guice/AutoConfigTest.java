package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.TestModule;
import com.hubspot.dropwizard.guice.objects.*;
import io.dropwizard.Bundle;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.AdminEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Set;
import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AutoConfigTest {

    private final Injector injector = Guice.createInjector(new TestModule());

    @Spy
    private Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
    private AutoConfig autoConfig;

    @Before
    public void setUp() {
        //when
        autoConfig = new AutoConfig(getClass().getPackage().getName());
    }

    @Test
    public void addBundlesDuringBootStrap() {
        //given
        final Bootstrap bootstrap = mock(Bootstrap.class);
        Bundle singletonBundle = injector.getInstance(InjectedBundle.class);

        //when
        autoConfig.initialize(bootstrap, injector);

        verify(bootstrap).addBundle(singletonBundle);
    }

    @Test
    public void addInjectableHealthChecks() {
        //when
        autoConfig.run(environment, injector);

        // then
        SortedSet<String> healthChecks = environment.healthChecks().getNames();
        assertThat(healthChecks).contains(new InjectedHealthCheck().getName());
    }

    @Test
    public void shouldAddProviders() {
        // when
        autoConfig.run(environment, injector);

        //then
        Set<Class<?>> components = environment.jersey().getResourceConfig().getClasses();
        assertThat(components).containsOnlyOnce(InjectedProvider.class);
    }

    @Test
    public void shouldAddResources() {
        //when
        autoConfig.run(environment, injector);

        //then
        Set<Class<?>> components = environment.jersey().getResourceConfig().getClasses();
        assertThat(components).containsOnlyOnce(InjectedResource.class);
    }

    @Test
    public void addInjectableTasks() throws Exception {
        //given
        Task task = injector.getInstance(InjectedTask.class);
        when(environment.admin()).thenReturn(mock(AdminEnvironment.class));

        //when
        autoConfig.run(environment, injector);

        //then
        verify(environment.admin()).addTask(task);

    }

    @Test
    public void shouldAddManaged() {
        //given
        Managed managed = injector.getInstance(InjectedManaged.class);
        when(environment.lifecycle()).thenReturn(mock(LifecycleEnvironment.class));

        //when
        autoConfig.run(environment, injector);

        //then
        verify(environment.lifecycle()).manage(managed);
    }
}
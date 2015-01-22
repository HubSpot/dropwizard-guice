package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.*;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class GuiceBundleTest {

    @Mock
    Environment environment;

    private GuiceBundle<Configuration> guiceBundle;

    @Before
    public void setUp() {
        //given
        environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
        guiceBundle = GuiceBundle.newBuilder()
                .addModule(new TestModule())
                .build();
        Bootstrap bootstrap = mock(Bootstrap.class);
        guiceBundle.initialize(bootstrap);
        guiceBundle.run(new Configuration(), environment);
    }

    @Test
    public void createsInjectorWhenInit() throws ServletException {
        //then
        Injector injector = guiceBundle.getInjector();
        assertThat(injector).isNotNull();
    }

    @Test
    public void serviceLocatorIsAvaliable () throws ServletException {
        ServiceLocator serviceLocator = guiceBundle.getInjector().getInstance(ServiceLocator.class);
        assertThat(serviceLocator).isNotNull();
    }

    @Test
    public void guiceBindingsAreBridgedToHk2() throws ServletException {
        Injector injector = guiceBundle.getInjector();
        ServiceLocator serviceLocator = injector.getInstance(ServiceLocator.class);
        //given
        InjectedTask guiceTask = injector.getInstance(InjectedTask.class);
        ExplicitBindingService guiceService = injector.getInstance(ExplicitBindingService.class);

        // when then
        assertThat(serviceLocator.getService(ExplicitBindingService.class)).isNotNull();

        // when then
        InjectedTask hk2Task = serviceLocator.createAndInitialize(InjectedTask.class);
        ExplicitBindingService hk2Service = serviceLocator.createAndInitialize(ExplicitBindingService.class);
        assertThat(guiceTask).isEqualToComparingFieldByField(hk2Task);
        assertThat(hk2Task.getName()).isEqualTo("test task");
        assertThat(guiceService).isEqualToComparingFieldByField(hk2Service);
    }

    @Test
    public void jitGuiceBindingsAreNotBridgedToHk2() throws ServletException {
        // when
        ServiceLocator serviceLocator = guiceBundle.getInjector().getInstance(ServiceLocator.class);

        // then
        assertThat(serviceLocator.getService(InjectedBundle.class)).isNull();
        assertThat(serviceLocator.getService(InjectedHealthCheck.class)).isNull();
        assertThat(serviceLocator.getService(InjectedManaged.class)).isNull();
        assertThat(serviceLocator.getService(InjectedProvider.class)).isNull();
        assertThat(serviceLocator.getService(InjectedResource.class)).isNull();
    }
}
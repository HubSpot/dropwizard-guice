package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.TestModule;
import com.squarespace.jersey2.guice.BootstrapUtils;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.AfterClass;
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

    @AfterClass
    public static void tearDown() {
        BootstrapUtils.reset();
    }

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
}
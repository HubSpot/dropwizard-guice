package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GuiceBundleTest {

    @Mock
    Bootstrap<Configuration> bootstrap;

    @Mock
    Configuration configuration;

    @Mock
    Environment environment;

    @Test
    public void canCreateInjector() throws ServletException {
        //given
        final GuiceBundle<io.dropwizard.Configuration> guiceBundle = GuiceBundle.newBuilder()
                .addModule(new GuiceTestModule())
                .enableAutoConfig("com.apmasphere.platform.server.resources")
                .build();

        //when
        guiceBundle.initialize(bootstrap);

        //then
        final Injector injector = guiceBundle.getInjector();
        assertThat(injector).isNotNull();

        GuiceTestService service = injector.getProvider(GuiceTestService.class).get();
        assertThat(service.getHost()).isEqualTo("localhost");
    }

}
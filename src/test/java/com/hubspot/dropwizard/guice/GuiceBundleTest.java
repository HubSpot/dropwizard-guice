package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
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

    @Test
    public void canBridgeGuiceToHK2() throws ServletException {
        //given
        final ServiceLocator hk2Injector = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        final GuiceBundle<io.dropwizard.Configuration> guiceBundle = GuiceBundle.newBuilder()
                .addModule(new GuiceModule())
                .enableAutoConfig("com.apmasphere.platform.server.resources")
                .build();
        guiceBundle.initialize(bootstrap);
        final Injector guiceInjector = guiceBundle.getInjector();

        //when
        guiceBundle.bridgeGuiceInjector(guiceInjector, hk2Injector);
        JavaxInjectedService guiceService = guiceInjector.getProvider(JavaxInjectedService.class).get();
        JavaxInjectedService hk2Service = hk2Injector.createAndInitialize(JavaxInjectedService.class);

        //then
        assertThat(guiceService).isEqualToComparingFieldByField(hk2Service);
        assertThat(hk2Service.getHost()).isEqualTo("localhost");
    }

}
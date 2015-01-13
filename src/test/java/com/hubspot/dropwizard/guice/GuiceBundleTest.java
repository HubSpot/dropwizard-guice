package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
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
    Bootstrap<TestConfiguration> bootstrap;

    @Test
    public void canBridgeGuiceToHK2() throws ServletException {
        //given
        final ServiceLocator hk2Injector = ServiceLocatorUtilities.createAndPopulateServiceLocator();
        final GuiceBundle<TestConfiguration> guiceBundle = GuiceBundle.<TestConfiguration>newBuilder()
                .addModule(new TestModule())
                .enableAutoConfig("com.apmasphere.platform.server.resources")
                .build();
        guiceBundle.initialize(bootstrap);
        final Injector guiceInjector = guiceBundle.getInjector();

        //when
        guiceBundle.bridgeGuiceInjector(guiceInjector, hk2Injector);
        TestService guiceService = guiceInjector.getProvider(TestService.class).get();
        TestService hk2Service = hk2Injector.createAndInitialize(TestService.class);

        //then
        assertThat(guiceService).isEqualToComparingFieldByField(hk2Service);
        assertThat(hk2Service.getHost()).isEqualTo("localhost");
    }

}
package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.servlet.ServletContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DropwizardEnvironmentModuleTest {

    @Mock
    Environment testEnvironment;


    @Test
    public void canResolveContainerFromEnvironment () {
        //given
        ServletContainer servletContainer = new ServletContainer();
        DropwizardEnvironmentModule<Configuration> dropwizardEnvironmentModule =
                new DropwizardEnvironmentModule<>(Configuration.class);
        Injector injector = Guice.createInjector();
        when(testEnvironment.getJerseyServletContainer()).thenReturn(servletContainer);

        //when
        dropwizardEnvironmentModule.setEnvironmentData(null, testEnvironment);
        ServletContainer result = injector.getProvider(ServletContainer.class).get();

        //then
        assertThat(result).isNotNull().isEqualToComparingFieldByField(servletContainer);
    }

}
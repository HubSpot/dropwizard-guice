package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GuiceContainerTest {

    ServletConfig dummyServletConfig = new ServletConfig() {
        @Override
        public String getServletName() {
            return "com.hubspot.dropwizard.guice.GuiceContainer";
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public String getInitParameter(String s) {
            return "";
        }

        @Override
        public Enumeration<String> getInitParameterNames() {
            return Collections.emptyEnumeration();
        }
    };

    @Test
    public void initHasNoCyclicDependency () throws ServletException {
        //given
        Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
        DropwizardEnvironmentModule<Configuration> dropwizardEnvironmentModule =
                new DropwizardEnvironmentModule<>(Configuration.class);
        Injector injector = Guice.createInjector(dropwizardEnvironmentModule);
        dropwizardEnvironmentModule.setEnvironmentData(null, environment);

        //when
        GuiceContainer container = injector.getProvider(GuiceContainer.class).get();
        //then
        assertThat(container).isNotNull();

        //when
        container.init(dummyServletConfig);
        //then
        assertThat(container.getApplicationHandler().getServiceLocator()).isNotNull();

    }


    @Test
    public void containerIsSingleton() {
        Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
        DropwizardEnvironmentModule<Configuration> dropwizardEnvironmentModule =
                new DropwizardEnvironmentModule<>(Configuration.class);
        Injector injector = Guice.createInjector(dropwizardEnvironmentModule);
        dropwizardEnvironmentModule.setEnvironmentData(null, environment);

        GuiceContainer container1 = injector.getProvider(GuiceContainer.class).get();
        GuiceContainer container2 = injector.getProvider(GuiceContainer.class).get();

        assertThat(container1).isSameAs(container2);

    }

    @Test
    public void bridgeGuiceToServiceLocator() throws ServletException {
        //given
        final ResourceConfig resourceConfig = new DropwizardResourceConfig();
        final Injector injector = Guice.createInjector(new GuiceTestModule());
        GuiceContainer container = new GuiceContainer(resourceConfig, injector);

        // when
        container.init(dummyServletConfig);
        GuiceTestService guiceService = injector.getProvider(GuiceTestService.class).get();
        GuiceTestService hk2Service = container.getServiceLocator().createAndInitialize(GuiceTestService.class);

        //then
        assertThat(guiceService).isEqualToComparingFieldByField(hk2Service);
        assertThat(hk2Service.getHost()).isEqualTo("localhost");
    }


}
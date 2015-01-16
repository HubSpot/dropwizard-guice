package com.hubspot.dropwizard.guice;

import com.google.inject.*;
import com.google.inject.name.Names;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

@RunWith(MockitoJUnitRunner.class)
public class GuiceContainerTest {

    private Injector injector;

    @Inject
    GuiceContainer guiceContainer;

    @Inject
    SimpleService injectedService;

    @Before
    public void setUp () {
        //given
        Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
        DropwizardEnvironmentModule<Configuration> dwModule = new DropwizardEnvironmentModule<>(Configuration.class);
        dwModule.setEnvironmentData(new Configuration(), environment);
        injector = Guice.createInjector(dwModule,
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(Names.named("HostName")).to("localhost");
                    }
                });
        injector.injectMembers(this);
    }

    @Test
    public void containerIsSingleton() {
        //when
        GuiceContainer guiceContainer2 = injector.getInstance(GuiceContainer.class);
        GuiceContainer guiceContainer3 = injector.getInstance(GuiceContainer.class);

        //then
        assertThat(guiceContainer).isSameAs(guiceContainer2).isSameAs(guiceContainer3);
    }

    @Test
    public void shouldGetServiceLocatorAfterInit () throws ServletException {

        final String SERVLET_UNINITIALISED = "[Cannot obtain HK2 ServiceLocator before ServletContainer init]";

        try {
            guiceContainer.getServiceLocator();
            failBecauseExceptionWasNotThrown(ProvisionException.class);
        } catch (ProvisionException ex) {
            assertThat(ex.getErrorMessages().toString()).isEqualTo(SERVLET_UNINITIALISED);
        }

        //when
        guiceContainer.init(dummyServletConfig);
        //then
        assertThat(guiceContainer.getServiceLocator()).isNotNull();

    }

    @Test
    public void bridgeGuiceToServiceLocator() throws ServletException {
        //given
        GuiceContainer container = new GuiceContainer(new DropwizardResourceConfig(), injector);

        // when
        container.init(dummyServletConfig);

        ServiceLocator serviceLocator = container.getServiceLocator();
        SimpleService hk2Service = serviceLocator.createAndInitialize(SimpleService.class);

        //then
        assertThat(injectedService).isEqualToComparingFieldByField(hk2Service);
        assertThat(hk2Service.getHost()).isEqualTo("localhost");
    }

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

}
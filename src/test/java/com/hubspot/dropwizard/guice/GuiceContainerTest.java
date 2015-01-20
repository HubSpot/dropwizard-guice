package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuiceContainerTest {

    private Injector injector;

    @Inject
    private GuiceContainer guiceContainer;

    @Mock
    private ServletConfig servletConfig;

    @Mock
    private ServletContext servletContext;

    @Before
    public void setUp () {
        //given
        Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
        DropwizardEnvironmentModule<Configuration> dwModule = new DropwizardEnvironmentModule<>(Configuration.class);
        dwModule.setEnvironmentData(new Configuration(), environment);
        injector = Guice.createInjector(dwModule, new TestModule());
        injector.injectMembers(this);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
        when(servletConfig.getInitParameterNames()).thenReturn(Collections.<String>emptyEnumeration());
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
        guiceContainer.init(servletConfig);
        //then
        assertThat(guiceContainer.getServiceLocator()).isNotNull();

    }

    @Test
    public void explicitGuiceBindingsAreBridgedToHk2() throws ServletException {
        //given
        InjectedTask guiceTask = injector.getInstance(InjectedTask.class);
        ExplicitBindingService guiceService = injector.getInstance(ExplicitBindingService.class);

        // when then
        guiceContainer.init(servletConfig);
        ServiceLocator serviceLocator = guiceContainer.getServiceLocator();

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
        guiceContainer.init(servletConfig);
        ServiceLocator serviceLocator = guiceContainer.getServiceLocator();

        // TODO @yunspace: no JIT bindings found in this test. Need to re-vsit
    }

}
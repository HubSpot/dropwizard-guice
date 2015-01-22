//package com.hubspot.dropwizard.guice;
//
//import com.google.inject.Guice;
//import com.google.inject.Inject;
//import com.google.inject.Injector;
//import com.google.inject.ProvisionException;
//import com.hubspot.dropwizard.guice.objects.ExplicitResource;
//import com.hubspot.dropwizard.guice.objects.JitResource;
//import io.dropwizard.Configuration;
//import io.dropwizard.jackson.Jackson;
//import io.dropwizard.setup.Environment;
//import org.glassfish.hk2.api.ServiceLocator;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import javax.servlet.ServletConfig;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import java.util.Collections;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class GuiceContainerTest {
//
//    private Injector injector;
//
//    @Inject
//    private GuiceContainer guiceContainer;
//
//    @Mock
//    private ServletConfig servletConfig;
//
//    @Mock
//    private ServletContext servletContext;
//
//    @Before
//    public void setUp () {
//        //given
//        Environment environment = new Environment("test env", Jackson.newObjectMapper(), null, null, null);
//        DropwizardEnvironmentModule<Configuration> dwModule = new DropwizardEnvironmentModule<>(Configuration.class);
//        dwModule.setEnvironmentData(new Configuration(), environment);
//        injector = Guice.createInjector(dwModule, new TestModule());
//        injector.injectMembers(this);
//
//        when(servletConfig.getServletContext()).thenReturn(servletContext);
//        when(servletConfig.getInitParameterNames()).thenReturn(Collections.<String>emptyEnumeration());
//    }
//
//    @Test
//    public void containerIsSingleton() {
//        //when
//        GuiceContainer guiceContainer2 = injector.getInstance(GuiceContainer.class);
//        GuiceContainer guiceContainer3 = injector.getInstance(GuiceContainer.class);
//
//        //then
//        assertThat(guiceContainer).isSameAs(guiceContainer2).isSameAs(guiceContainer3);
//    }
//
//    @Test
//    public void shouldGetServiceLocatorAfterInit () throws ServletException {
//
//        final String SERVLET_UNINITIALISED = "[Cannot obtain HK2 ServiceLocator before ServletContainer init]";
//
//        try {
//            guiceContainer.getServiceLocator();
//            failBecauseExceptionWasNotThrown(ProvisionException.class);
//        } catch (ProvisionException ex) {
//            assertThat(ex.getErrorMessages().toString()).isEqualTo(SERVLET_UNINITIALISED);
//        }
//
//        //when
//        guiceContainer.init(servletConfig);
//        //then
//        assertThat(guiceContainer.getServiceLocator()).isNotNull();
//
//    }
//
//    @Test
//    public void explicitGuiceBindingsAreBridgedToHk2() throws ServletException {
//        // given
//        guiceContainer.init(servletConfig);
//        ServiceLocator serviceLocator = guiceContainer.getServiceLocator();
//
//        // when
//        ExplicitResource resource = serviceLocator.createAndInitialize(ExplicitResource.class);
//
//        // then
//        assertThat(resource).isNotNull();
//        assertThat(resource.getDAO()).isNotNull();
//    }
//
//    @Test
//    public void jitGuiceBindingsAreBridgedToHk2() throws ServletException {
//        //given
//        guiceContainer.init(servletConfig);
//        ServiceLocator serviceLocator = guiceContainer.getServiceLocator();
//
//
//        // when
//        try {
//            JitResource resource = serviceLocator.createAndInitialize(JitResource.class);
//
//            // then
//            assertThat(resource).isNotNull();
//            assertThat(resource.getDAO()).isNotNull();
//        } catch (Exception e) {
//            // TODO add JIT resolver to avoid UnsatisfiedDependency error
//        }
//    }
//
//}
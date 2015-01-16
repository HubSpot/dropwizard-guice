package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import io.dropwizard.Configuration;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        guiceBundle.initialize(mock(Bootstrap.class));
    }

    @Test
    public void createsInjectorWhenInit() throws ServletException {
        //then
        Injector injector = guiceBundle.getInjector();
        assertThat(injector).isNotNull();
    }

    @Test
    public void replacesContainerWhenRun () {
        //given
        Servlet originalContainer = environment.getJerseyServletContainer();
        assertThat(environment.getJerseyServletContainer()).isNotInstanceOf(GuiceContainer.class);

        //when
        guiceBundle.run(new Configuration(), environment);
        Servlet replacedContainer = environment.getJerseyServletContainer();

        //then
        assertThat(replacedContainer).isInstanceOf(GuiceContainer.class);
        assertThat(replacedContainer).isNotSameAs(originalContainer);
    }
}
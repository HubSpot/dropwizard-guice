package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.ExplicitResource;
import com.hubspot.dropwizard.guice.objects.JitResource;
import com.hubspot.dropwizard.guice.objects.TestModule;
import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

public class HK2LinkerTest {

    private static Injector injector;
    final ServiceLocator serviceLocator = injector.getInstance(ServiceLocator.class);

    @BeforeClass
    public static void setup() {

        final GuiceBundle bundle = new GuiceBundle.Builder().addModule(new TestModule()).build();
        bundle.initialize(new Bootstrap<Configuration>(new Application<Configuration>() {
            @Override
            public void run(Configuration configuration, Environment environment) throws Exception {

            }
        }));
        injector = bundle.getInjector();

    }

    @AfterClass
    public static void tearDown() {
        JerseyGuiceUtils.reset();
    }

    @Test
    public void explicitGuiceBindingsAreBridgedToHk2() throws ServletException {
        // when
        ExplicitResource resource = serviceLocator.createAndInitialize(ExplicitResource.class);

        // then
        assertThat(resource).isNotNull();
        assertThat(resource.getDAO()).isNotNull();
    }

    @Test
    public void jitGuiceBindingsAreBridgedToHk2() throws ServletException {
        // when
        JitResource resource = serviceLocator.createAndInitialize(JitResource.class);

        // then
        assertThat(resource).isNotNull();
        assertThat(resource.getDAO()).isNotNull();
    }
}

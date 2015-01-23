package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.ExplicitResource;
import com.hubspot.dropwizard.guice.objects.JitResource;
import org.glassfish.hk2.api.ServiceLocator;
import org.junit.Test;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

public class HK2LinkerTest {

    final Injector injector = Guice.createInjector(new JerseyModule(), new TestModule());
    final ServiceLocator serviceLocator = injector.getInstance(ServiceLocator.class);

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
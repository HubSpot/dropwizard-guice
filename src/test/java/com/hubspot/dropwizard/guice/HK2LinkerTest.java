package com.hubspot.dropwizard.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hubspot.dropwizard.guice.objects.ExplicitResource;
import com.hubspot.dropwizard.guice.objects.JitResource;
import com.hubspot.dropwizard.guice.objects.TestModule;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.Test;

import javax.servlet.ServletException;

import static org.assertj.core.api.Assertions.assertThat;

public class HK2LinkerTest {

    final static Injector injector = Guice.createInjector(new TestModule());
    final static ServiceLocator serviceLocator = ServiceLocatorUtilities.createAndPopulateServiceLocator();
    final static HK2Linker linker = new HK2Linker(injector, serviceLocator);

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
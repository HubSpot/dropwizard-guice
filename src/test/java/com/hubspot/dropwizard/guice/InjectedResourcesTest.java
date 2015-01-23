package com.hubspot.dropwizard.guice;

import com.hubspot.dropwizard.guice.objects.ExplicitDAO;
import com.hubspot.dropwizard.guice.objects.ExplicitResource;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * this test is created to address to Null Pointer Exceptions in JerseyTest.teardown() related to ServiceLocator
 * See: https://github.com/dropwizard/dropwizard/issues/828 and http://permalink.gmane.org/gmane.comp.java.dropwizard.devel/376
 */
public class InjectedResourcesTest {

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new ExplicitResource(new ExplicitDAO()))
            .build();

    @Test
    public void shouldGetMessage() {
        // when
        String message = resources.client().target("/explicit/message").request().get(String.class);

        // then
        assertThat(message).isEqualTo("hello world");
    }

    @After
    public void tearDown() throws Exception {
        resources.getJerseyTest().tearDown();
    }

}
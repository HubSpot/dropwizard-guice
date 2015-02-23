package com.hubspot.dropwizard.guice;

import com.google.common.io.Resources;
import com.hubspot.dropwizard.guice.objects.TestApplication;
import com.squarespace.jersey2.guice.BootstrapUtils;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class InjectedIntegrationTest {

    @ClassRule
    public static final DropwizardAppRule<Configuration> RULE =
            new DropwizardAppRule<>(TestApplication.class, resourceFilePath("test-config.yml"));

    protected static Client client;

    @BeforeClass
    public static void setUp() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");
    }

    @AfterClass
    public static void tearDown() {
        BootstrapUtils.reset();
    }

    public static String resourceFilePath(String resourceClassPathLocation) {
        try {
            return new File(Resources.getResource(resourceClassPathLocation).toURI()).getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldGetExplicitMessage() {

        // when
        final String message = client.target(
                String.format("http://localhost:%d//explicit/message", RULE.getLocalPort()))
                .request()
                .get(String.class);

        // then
        assertThat(message).isEqualTo("this DAO was bound explicitly");
    }

    @Test
    public void shouldGetJitMessage() {

        // when
        final String message = client.target(
                String.format("http://localhost:%d//jit/message", RULE.getLocalPort()))
                .request()
                .get(String.class);

        // then
        assertThat(message).isEqualTo("this DAO was bound just-in-time");
    }

}

package com.hubspot.dropwizard.guice;

import io.dropwizard.Configuration;

class TestConfiguration extends Configuration {

    String host = "localhost";

    public String getHost() {
        return host;
    }

}

package com.hubspot.dropwizard.guice.objects;

public class ExplicitDAO {

    // this service is explicitly bound and should be picked up by HK2 bridge";
    public String getMessage() {
        return "hello world";
    }

}

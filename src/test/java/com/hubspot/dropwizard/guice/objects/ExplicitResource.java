package com.hubspot.dropwizard.guice.objects;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("auto-config")
public class ExplicitResource {

    private final ExplicitDAO dao;

    @Inject
    public ExplicitResource(ExplicitDAO dao) {
        this.dao = dao;;
    }

    public ExplicitDAO getDAO() {
        return dao;
    }
}

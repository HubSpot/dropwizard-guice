package com.hubspot.dropwizard.guice.objects;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/jit")
@Produces(APPLICATION_JSON)
public class JitResource {

    private final JitDAO dao;

    @Inject
    public JitResource(JitDAO dao) {
        this.dao = dao;
    }

    @GET
    @Path("/message")
    public String getMessage () {
        return dao.getMessage();
    }

    public JitDAO getDAO() {
        return dao;
    }

}

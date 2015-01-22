package com.hubspot.dropwizard.guice.objects;

import javax.inject.Inject;

public class JitResource {

    private final JitDAO dao;

    @Inject
    public JitResource(JitDAO dao) {
        this.dao = dao;
    }

    public JitDAO getDAO() {
        return dao;
    }
}

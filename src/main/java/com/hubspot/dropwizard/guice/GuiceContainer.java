package com.hubspot.dropwizard.guice;

import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebServletConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class GuiceContainer extends ServletContainer {

    private static final String SERVLET_UNINITIALISED = "Cannot obtain HK2 ServiceLocator before ServletContainer init";
    final private Injector injector;

    @Inject
    public GuiceContainer(ResourceConfig resourceConfig, Injector injector) {
        super(resourceConfig);
        this.injector = injector;
    }

    @Override
    public void init () throws ServletException {
        super.init(new WebServletConfig(this));
        bridgeGuiceInjector(injector, getServiceLocator());
    }

    private void bridgeGuiceInjector(Injector injector, ServiceLocator serviceLocator) {
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }

    public ServiceLocator getServiceLocator() {
        try {
            return super.getApplicationHandler().getServiceLocator();
        } catch (NullPointerException e) {
            throw new ProvisionException(SERVLET_UNINITIALISED, e);
        }
    }
}

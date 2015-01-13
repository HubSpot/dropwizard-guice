package com.hubspot.dropwizard.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.WebServletConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.servlet.ServletException;

@Singleton
public class GuiceContainer extends ServletContainer {

    private static final long serialVersionUID = 1931878850157940335L;

    private final Injector injector;

    private ServiceLocator serviceLocator;

    @Inject
    public GuiceContainer(Injector injector) {
        super(injector.getProvider(ResourceConfig.class).get());
        this.injector = injector;
    }


//    public void setResourceConfig(ResourceConfig resourceConfig) {
//	    this.resourceConfig = resourceConfig;
//    }

//    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
//    	return resourceConfig;
//    }

    @Override
    public void init() throws ServletException {
        super.init(new WebServletConfig(this));
        bridgeGuiceInjector(injector);
    }

    private void bridgeGuiceInjector(Injector injector) {
        this.serviceLocator = super.getApplicationHandler().getServiceLocator();
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
    }
}
package com.hubspot.dropwizard.guice;

import java.util.Map;

import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scope;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletScopes;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.guice.spi.container.GuiceComponentProviderFactory;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

@Singleton
public class GuiceContainer extends ServletContainer {
    
    private static final long serialVersionUID = 1931878850157940335L;

    @Inject
    private Injector injector;
    
    private WebApplication webapp;
    
    private ResourceConfig resourceConfig = new DefaultResourceConfig();

    public class ServletGuiceComponentProviderFactory extends GuiceComponentProviderFactory {
        public ServletGuiceComponentProviderFactory(ResourceConfig config, Injector injector) {
            super(config, injector);
        }
        
        @Override
        public Map<Scope, ComponentScope> createScopeMap() {
            Map<Scope, ComponentScope> m = super.createScopeMap();

            m.put(ServletScopes.REQUEST, ComponentScope.PerRequest);
            return m;
        }
    }

    public GuiceContainer() {
    }
		
    public GuiceContainer(Application app) {
      super(app);
    }
		
    public GuiceContainer(Class<? extends Application> app) {
      super(app);
    }
    
    public void setResourceConfig(ResourceConfig resourceConfig) {
	    this.resourceConfig = resourceConfig;
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props, WebConfig webConfig) throws ServletException {
    	return resourceConfig;
    }

    @Override
    protected void initiate(ResourceConfig config, WebApplication webapp) {
        this.webapp = webapp;
        webapp.initiate(config, new ServletGuiceComponentProviderFactory(config, injector));
    }

    public WebApplication getWebApplication() {
        return webapp;
    }
}
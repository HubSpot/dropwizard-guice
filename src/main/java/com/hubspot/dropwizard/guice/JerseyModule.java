package com.hubspot.dropwizard.guice;

import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.BootstrapModule;
import com.squarespace.jersey2.guice.BootstrapUtils;
import org.glassfish.hk2.api.ServiceLocator;

//Inspired by gwizard-jersey - https://github.com/stickfigure/gwizard
public class JerseyModule extends ServletModule {

    @Override
    protected void configureServlets() {
        // The order these operations (including the steps in the linker) are important
        ServiceLocator locator = new ServiceLocatorDecorator(BootstrapUtils.newServiceLocator()) {

            @Override
            public void shutdown() {
                // don't shutdown, see issue #67. Remove once jersey2-guice supports Jersey 2.21
            }
        };
        install(new BootstrapModule(locator));

        bind(HK2Linker.class).asEagerSingleton();
    }
}

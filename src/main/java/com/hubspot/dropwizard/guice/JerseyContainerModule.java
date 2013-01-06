package com.hubspot.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.spi.container.WebApplication;

public class JerseyContainerModule extends AbstractModule {
	private GuiceContainer container;

	public JerseyContainerModule() {
	}

	public void setContainer(final GuiceContainer container) {
		this.container = container;
	}

	@Override
	protected void configure() {
	}

	@Provides
	public GuiceContainer providesGuiceContainer() {
		return container;
	}

	@Provides
	public WebApplication webApp() {
		return container.getWebApplication();
	}
}

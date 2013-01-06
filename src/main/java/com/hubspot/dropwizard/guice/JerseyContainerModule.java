package com.hubspot.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.sun.jersey.spi.container.WebApplication;

public class JerseyContainerModule extends AbstractModule {

	private final GuiceContainer container;

	public JerseyContainerModule(final GuiceContainer container) {
		this.container = container;
	}

	@Override
	protected void configure() {
		bind(GuiceContainer.class).toInstance(container);
	}

	@Provides
	public WebApplication webApp() {
		return container.getWebApplication();
	}
}

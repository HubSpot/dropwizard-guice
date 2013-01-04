package com.hubspot.dropwizard.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class DropwizardEnvironmentModule extends AbstractModule {
	private Configuration configuration;
	private Environment environment;

	@Override
	protected void configure() {
	}

	public void setEnvironmentData(Configuration configuration, Environment environment) {
		this.configuration = configuration;
		this.environment = environment;
	}

	@Provides
	public Environment providesEnvironment() {
		return environment;
	}

	@Provides
	public Configuration providesConfiguration() {
		return configuration;
	}
}

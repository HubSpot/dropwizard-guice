package com.hubspot.dropwizard.guice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.util.Modules;
import com.sun.jersey.guice.JerseyServletModule;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {

	final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

	private final AutoConfig autoConfig;
	private final List<Module> modules;
	private Injector injector;
	private JerseyContainerModule jerseyContainerModule;
	private DropwizardEnvironmentModule dropwizardEnvironmentModule;
	private GuiceContainer container;
	
	public static class Builder<T extends Configuration> {
		private AutoConfig autoConfig;
		private List<Module> modules = Lists.newArrayList();
		
		public Builder<T> addModule(Module module) {
			Preconditions.checkNotNull(module);
			modules.add(module);
			return this;
		}

		public Builder<T> enableAutoConfig(String... basePackages) {
			Preconditions.checkNotNull(basePackages.length > 0);
			Preconditions.checkArgument(autoConfig == null, "autoConfig already enabled!");
			autoConfig = new AutoConfig(basePackages);
			return this;
		}
		
		public GuiceBundle<T> build() {
			return new GuiceBundle<T>(autoConfig, modules);
		}

	}
	
	public static <T extends Configuration> Builder<T> newBuilder() {
		return new Builder<T>();
	}

	private GuiceBundle(AutoConfig autoConfig, List<Module> modules) {
		Preconditions.checkNotNull(modules);
		Preconditions.checkArgument(!modules.isEmpty());
		this.modules = modules;
		this.autoConfig = autoConfig;
	}
	
	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		container = new GuiceContainer();
		jerseyContainerModule = new JerseyContainerModule(container);
		dropwizardEnvironmentModule = new DropwizardEnvironmentModule();
		modules.add(Modules.override(new JerseyServletModule()).with(jerseyContainerModule));
		modules.add(dropwizardEnvironmentModule);
		injector = Guice.createInjector(modules);
		if (autoConfig != null) {
			autoConfig.initialize(bootstrap, injector);
		}
	}

	@Override
	public void run(final Configuration configuration, final Environment environment) {
		container.setResourceConfig(environment.getJerseyResourceConfig());
		environment.setJerseyServletContainer(container);
		dropwizardEnvironmentModule.setEnvironmentData(configuration, environment);
		environment.addFilter(GuiceFilter.class, configuration.getHttpConfiguration().getRootPath());

		if (autoConfig != null) {
			autoConfig.run(environment, injector);
		}
	}

}

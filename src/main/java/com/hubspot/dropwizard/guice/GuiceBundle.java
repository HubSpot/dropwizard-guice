package com.hubspot.dropwizard.guice;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.util.Modules;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {

	final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

	private final AutoConfig autoConfig;
	private final List<Module> modules;
	
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

	}

	@Override
	public void run(final Configuration configuration, final Environment environment) {
		@SuppressWarnings("serial")
		GuiceContainer container = new GuiceContainer() {
			protected ResourceConfig getDefaultResourceConfig(
					Map<String, Object> props, WebConfig webConfig)
					throws javax.servlet.ServletException {
				return environment.getJerseyResourceConfig();
			};
		};
		environment.setJerseyServletContainer(container);
		environment.addFilter(GuiceFilter.class, configuration.getHttpConfiguration().getRootPath());

		modules.add(Modules.override(new JerseyServletModule()).with(new JerseyContainerModule(container)));
		modules.add(new AbstractModule() {

			@Override
			protected void configure() {
				bind(Configuration.class).toInstance(configuration);
				bind(Environment.class).toInstance(environment);
			}
		});
		Injector injector = Guice.createInjector(modules);
		if (autoConfig != null) {
			autoConfig.run(environment, injector);
		}
	}

}

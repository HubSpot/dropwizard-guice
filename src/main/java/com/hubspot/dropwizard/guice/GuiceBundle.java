package com.hubspot.dropwizard.guice;

import java.util.Collection;
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

public class GuiceBundle implements ConfiguredBundle<Configuration> {

	final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

	private AutoConfig autoConfig = null;
	private List<Module> modules = Lists.newArrayList();

	@Override
	public void initialize(Bootstrap<?> bootstrap) {

	}

	public GuiceBundle addModule(Module module) {
		Preconditions.checkNotNull(module);
		modules.add(module);
		return this;
	}
	
	public GuiceBundle enableAutoConfig(String... basePackages) {
		autoConfig = new AutoConfig(basePackages);
		return this;
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
		environment.addFilter(GuiceFilter.class, configuration
				.getHttpConfiguration().getRootPath());

		Collection<Module> modules = Lists.newArrayList();
		modules.add(Modules.override(new JerseyServletModule()).with(
				new JerseyContainerModule(container)));
		modules.add(new AbstractModule() {

			@Override
			protected void configure() {
				binder().bind(Configuration.class).toInstance(configuration);
			}
		});
		modules.addAll(modules);
		Injector injector = Guice.createInjector(modules);
		if (autoConfig != null) {
			autoConfig.run(environment, injector);
		}
	}

}

package com.hubspot.dropwizard.guice;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.util.Modules;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.spi.container.servlet.WebConfig;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.tasks.Task;
import com.yammer.metrics.core.HealthCheck;

public class GuiceBundle<T extends Configuration> implements
		ConfiguredBundle<T> {

	final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

	private Reflections reflections;

	public GuiceBundle(String... basePackages) {
		ConfigurationBuilder cfgBldr = new ConfigurationBuilder();
		FilterBuilder filterBuilder = new FilterBuilder();
		for (String basePkg : basePackages) {
			cfgBldr.addUrls(ClasspathHelper.forPackage(basePkg));
			filterBuilder.include(FilterBuilder.prefix(basePkg));
		}

		cfgBldr.filterInputsBy(filterBuilder).setScanners(
				new SubTypesScanner(), new TypeAnnotationsScanner());
		this.reflections = new Reflections(cfgBldr);
	}

	@Override
	public void initialize(Bootstrap<?> bootstrap) {

	}

	protected Collection<? extends Module> configureModules(T configuration) {
		return Lists.newArrayList();
	}

	@Override
	public void run(T configuration, final Environment environment) {
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
		modules.addAll(configureModules(configuration));
		Injector injector = Guice.createInjector(modules);
		if (reflections != null) {
			autoConfigure(environment, injector);
		}
	}

	private void autoConfigure(Environment environment, Injector injector) {
		addHealthChecks(environment, injector);
		addProviders(environment, injector);
		addInjectableProviders(environment, injector);
		addResources(environment, injector);
		addTasks(environment, injector);
		addManaged(environment, injector);
	}

	private void addManaged(Environment environment, Injector injector) {
		Set<Class<? extends Managed>> managedClasses = reflections
				.getSubTypesOf(Managed.class);
		for (Class<? extends Managed> managed : managedClasses) {
			environment.manage(injector.getInstance(managed));
			logger.info("Added managed: " + managed);
		}
	}

	private void addTasks(Environment environment, Injector injector) {
		Set<Class<? extends Task>> taskClasses = reflections
				.getSubTypesOf(Task.class);
		for (Class<? extends Task> task : taskClasses) {
			environment.addTask(injector.getInstance(task));
			logger.info("Added task: " + task);
		}
	}

	private void addHealthChecks(Environment environment, Injector injector) {
		Set<Class<? extends HealthCheck>> healthCheckClasses = reflections
				.getSubTypesOf(HealthCheck.class);
		for (Class<? extends HealthCheck> healthCheck : healthCheckClasses) {
			environment.addHealthCheck(injector.getInstance(healthCheck));
			logger.info("Added healthCheck: " + healthCheck);
		}
	}

	@SuppressWarnings("rawtypes")
	private void addInjectableProviders(Environment environment,
			Injector injector) {
		Set<Class<? extends InjectableProvider>> injectableProviders = reflections
				.getSubTypesOf(InjectableProvider.class);
		for (Class<? extends InjectableProvider> injectableProvider : injectableProviders) {
			environment.addProvider(injectableProvider);
			logger.info("Added injectableProvider: " + injectableProvider);
		}
	}

	private void addProviders(Environment environment, Injector injector) {
		Set<Class<?>> providerClasses = reflections
				.getTypesAnnotatedWith(Provider.class);
		for (Class<?> provider : providerClasses) {
			environment.addProvider(provider);
			logger.info("Added provider class: " + provider);
		}
	}

	private void addResources(Environment environment, Injector injector) {
		Set<Class<?>> resourceClasses = reflections
				.getTypesAnnotatedWith(Path.class);
		for (Class<?> resource : resourceClasses) {
			environment.addResource(resource);
			logger.info("Added resource class: " + resource);
		}
	}

}

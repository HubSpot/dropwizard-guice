package com.hubspot.dropwizard.guice;

import io.dropwizard.Bundle;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import org.glassfish.jersey.server.model.Resource;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.util.Set;

public class AutoConfig {

	final Logger logger = LoggerFactory.getLogger(AutoConfig.class);

	private Reflections reflections;

	public AutoConfig(String... basePackages) {
		Preconditions.checkArgument(basePackages.length > 0);
		
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

	public void run(Environment environment, Injector injector) {
		addHealthChecks(environment, injector);
		addProviders(environment);
		addResources(environment);
		addTasks(environment, injector);
		addManaged(environment, injector);
		addParamConverterProviders(environment);
	}

	public void initialize(Bootstrap<?> bootstrap, Injector injector) {
		addBundles(bootstrap, injector);
		addConfiguredBundles(bootstrap, injector);
	}

	private void addManaged(Environment environment, Injector injector) {
		Set<Class<? extends Managed>> managedClasses = reflections
				.getSubTypesOf(Managed.class);
		for (Class<? extends Managed> managed : managedClasses) {
			environment.lifecycle().manage(injector.getInstance(managed));
			logger.info("Added managed: {}", managed);
		}
	}

	private void addTasks(Environment environment, Injector injector) {
		Set<Class<? extends Task>> taskClasses = reflections
				.getSubTypesOf(Task.class);
		for (Class<? extends Task> task : taskClasses) {
			environment.admin().addTask(injector.getInstance(task));
			logger.info("Added task: {}", task);
		}
	}

	private void addHealthChecks(Environment environment, Injector injector) {
		Set<Class<? extends InjectableHealthCheck>> healthCheckClasses = reflections
				.getSubTypesOf(InjectableHealthCheck.class);
		for (Class<? extends InjectableHealthCheck> healthCheck : healthCheckClasses) {
            InjectableHealthCheck instance = injector.getInstance(healthCheck);
            environment.healthChecks().register(instance.getName(), instance);
			logger.info("Added injectableHealthCheck: {}", healthCheck);
		}
	}

	private void addProviders(Environment environment) {
		Set<Class<?>> providerClasses = reflections
				.getTypesAnnotatedWith(Provider.class);
		for (Class<?> provider : providerClasses) {
			environment.jersey().register(provider);
			logger.info("Added provider class: {}", provider);
		}
	}

	private void addResources(Environment environment) {
		Set<Class<?>> resourceClasses = reflections
				.getTypesAnnotatedWith(Path.class);
		for (Class<?> resource : resourceClasses) {
			if(Resource.isAcceptable(resource)) {
				environment.jersey().register(resource);
				logger.info("Added resource class: {}", resource);
			}
		}
	}

	private void addBundles(Bootstrap<?> bootstrap, Injector injector) {
		Set<Class<? extends Bundle>> bundleClasses = reflections
				.getSubTypesOf(Bundle.class);
		for (Class<? extends Bundle> bundle : bundleClasses) {
			bootstrap.addBundle(injector.getInstance(bundle));
			logger.info("Added bundle class {} during bootstrap", bundle);
		}
	}

	@SuppressWarnings("unchecked")
	private void addConfiguredBundles(Bootstrap<?> bootstrap, Injector injector) {
		Set<Class<? extends ConfiguredBundle>> configuredBundleClasses = reflections
						.getSubTypesOf(ConfiguredBundle.class);
		for (Class<? extends ConfiguredBundle> configuredBundle : configuredBundleClasses) {
			if (configuredBundle != GuiceBundle.class) {
				bootstrap.addBundle(injector.getInstance(configuredBundle));
				logger.info("Added configured bundle class {} during bootstrap", configuredBundle);
			}
		}
	}

	private void addParamConverterProviders(Environment environment) {
		Set<Class<? extends ParamConverterProvider>> providerClasses = reflections
			    .getSubTypesOf(ParamConverterProvider.class);
		for (Class<?> provider : providerClasses) {
			environment.jersey().register(provider);
			logger.info("Added ParamConverterProvider class: {}", provider);
		}
	}
}

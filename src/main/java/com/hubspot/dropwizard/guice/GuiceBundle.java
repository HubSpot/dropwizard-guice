package com.hubspot.dropwizard.guice;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceFilter;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {

	final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

	private final AutoConfig autoConfig;
	private final List<Module> modules;
	private Injector injector;
	private JerseyContainerModule jerseyContainerModule;
	private DropwizardEnvironmentModule dropwizardEnvironmentModule;
	private Optional<Class<T>> configurationClass;
	private GuiceContainer container;
	
	public static class Builder<T extends Configuration> {
		private AutoConfig autoConfig;
		private List<Module> modules = Lists.newArrayList();
		private Optional<Class<T>> configurationClass = Optional.<Class<T>>absent();
		
		public Builder<T> addModule(Module module) {
			Preconditions.checkNotNull(module);
			modules.add(module);
			return this;
		}

		public Builder<T> setConfigClass(Class<T> clazz) {
			configurationClass = Optional.of(clazz);
			return this;
		}

		public Builder<T> enableAutoConfig(String... basePackages) {
			Preconditions.checkNotNull(basePackages.length > 0);
			Preconditions.checkArgument(autoConfig == null, "autoConfig already enabled!");
			autoConfig = new AutoConfig(basePackages);
			return this;
		}
		
		public GuiceBundle<T> build() {
			return new GuiceBundle<T>(autoConfig, modules, configurationClass);
		}

	}
	
	public static <T extends Configuration> Builder<T> newBuilder() {
		return new Builder<T>();
	}

	private GuiceBundle(AutoConfig autoConfig, List<Module> modules, Optional<Class<T>> configurationClass) {
		Preconditions.checkNotNull(modules);
		Preconditions.checkArgument(!modules.isEmpty());
		this.modules = modules;
		this.autoConfig = autoConfig;
		this.configurationClass = configurationClass;
	}
	
	@Override
	public void initialize(Bootstrap<?> bootstrap) {
		container = new GuiceContainer();
		jerseyContainerModule = new JerseyContainerModule(container);
		if (configurationClass.isPresent()) {
			dropwizardEnvironmentModule = new DropwizardEnvironmentModule<T>(configurationClass.get());
		} else {
			dropwizardEnvironmentModule = new DropwizardEnvironmentModule<Configuration>(Configuration.class);
		}
		modules.add(jerseyContainerModule);
		modules.add(dropwizardEnvironmentModule);
        try {
			injector = Guice.createInjector(modules);
        } catch(Exception ie) {
            logger.error("Exception occurred when creating Guice Injector - exiting", ie);
            System.exit(-1);
        }
		if (autoConfig != null) {
			autoConfig.initialize(bootstrap, injector);
		}
	}

	@Override
	public void run(final T configuration, final Environment environment) {
		container.setResourceConfig(environment.getJerseyResourceConfig());
		environment.setJerseyServletContainer(container);
		environment.addFilter(GuiceFilter.class, configuration.getHttpConfiguration().getRootPath());
		setEnvironment(configuration, environment);

		if (autoConfig != null) {
			autoConfig.run(environment, injector);
		}
	}

	@SuppressWarnings("unchecked")
	private void setEnvironment(final T configuration, final Environment environment) {
		dropwizardEnvironmentModule.setEnvironmentData(configuration, environment);
	}

	public Injector getInjector() {
		return injector;
	}
}

package com.hubspot.dropwizard.guice;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;

import java.util.HashSet;
import java.util.Set;

public class DropwizardEnvironmentModule<T extends Configuration> extends AbstractModule {
	private static final String ILLEGAL_DROPWIZARD_MODULE_STATE = "The dropwizard environment has not yet been set. This is likely caused by trying to access the dropwizard environment during the bootstrap phase.";
	private T configuration;
	private Environment environment;
	private Set<Class<? super T>> configurationClasses;

	public DropwizardEnvironmentModule(Class<T> configurationClass) {
		this.configurationClasses = findConfigurationClasses(configurationClass);
	}

	@Override
	protected void configure() {
		Provider<T> provider = new CustomConfigurationProvider();
		for (Class<? super T> configurationClass : configurationClasses) {
      bind(configurationClass).toProvider(provider);
    }
	}

	public void setEnvironmentData(T configuration, Environment environment) {
		this.configuration = configuration;
		this.environment = environment;
	}

	@Provides
	public Environment providesEnvironment() {
		if (environment == null) {
			throw new ProvisionException(ILLEGAL_DROPWIZARD_MODULE_STATE);
		}
		return environment;
	}

  private Set<Class<? super T>> findConfigurationClasses(Class<T> initialClass) {
    Set<Class<? super T>> classes = new HashSet<>();
    classes.add(initialClass);

    Class<? super T> currentClass = initialClass;
    while (currentClass != Configuration.class) {
      currentClass = currentClass.getSuperclass();
      classes.add(currentClass);
    }

    return classes;
  }

	private class CustomConfigurationProvider implements Provider<T> {
		@Override
		public T get() {
			if (configuration == null) {
				throw new ProvisionException(ILLEGAL_DROPWIZARD_MODULE_STATE);
			}
			return configuration;
		}
	}
}

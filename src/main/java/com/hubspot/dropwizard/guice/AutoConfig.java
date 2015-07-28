package com.hubspot.dropwizard.guice;

import com.google.common.base.Optional;
import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
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
import java.lang.reflect.Modifier;
import java.util.Set;

public class AutoConfig {
  private static final Logger logger = LoggerFactory.getLogger(AutoConfig.class);

  private final Reflections reflections;

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
      Optional<? extends Managed> maybeManaged = getFromGuiceIfPossible(injector, managed);
      if (maybeManaged.isPresent()) {
        environment.lifecycle().manage(maybeManaged.get());
        logger.info("Added managed: {}", managed);
      }
    }
  }

  private void addTasks(Environment environment, Injector injector) {
    Set<Class<? extends Task>> taskClasses = reflections
        .getSubTypesOf(Task.class);
    for (Class<? extends Task> task : taskClasses) {
      Optional<? extends Task> maybeTask = getFromGuiceIfPossible(injector, task);
      if (maybeTask.isPresent()) {
        environment.admin().addTask(maybeTask.get());
        logger.info("Added task: {}", task);
      }
    }
  }

  private void addHealthChecks(Environment environment, Injector injector) {
    Set<Class<? extends InjectableHealthCheck>> healthCheckClasses = reflections
        .getSubTypesOf(InjectableHealthCheck.class);
    for (Class<? extends InjectableHealthCheck> healthCheck : healthCheckClasses) {
      Optional<? extends InjectableHealthCheck> maybeHealthCheck = getFromGuiceIfPossible(injector, healthCheck);
      if (maybeHealthCheck.isPresent()) {
        environment.healthChecks().register(maybeHealthCheck.get().getName(), maybeHealthCheck.get());
        logger.info("Added injectableHealthCheck: {}", healthCheck);
      }
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
      Optional<? extends Bundle> maybeBundle = getFromGuiceIfPossible(injector, bundle);
      if (maybeBundle.isPresent()) {
        bootstrap.addBundle(maybeBundle.get());
        logger.info("Added bundle class {} during bootstrap", bundle);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void addConfiguredBundles(Bootstrap<?> bootstrap, Injector injector) {
    Set<Class<? extends ConfiguredBundle>> configuredBundleClasses = reflections
        .getSubTypesOf(ConfiguredBundle.class);
    for (Class<? extends ConfiguredBundle> configuredBundle : configuredBundleClasses) {
      if (configuredBundle != GuiceBundle.class) {
        Optional<? extends ConfiguredBundle> maybeConfiguredBundle = getFromGuiceIfPossible(injector, configuredBundle);
        if (maybeConfiguredBundle.isPresent()) {
          bootstrap.addBundle(maybeConfiguredBundle.get());
          logger.info("Added configured bundle class {} during bootstrap", configuredBundle);
        }
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

  private <T> Optional<T> getFromGuiceIfPossible(Injector injector, Class<T> type) {
    // if it's a concrete class get it from Guice
    if (concreteClass(type) || hasBinding(injector, type)) {
      return Optional.of(injector.getInstance(type));
    } else {
      logger.info("Not attempting to retrieve abstract class {} from injector", type);
      return Optional.absent();
    }
  }

  private static boolean concreteClass(Class<?> type) {
    return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
  }

  private static boolean hasBinding(Injector injector, Class<?> type) {
    return injector.getExistingBinding(Key.get(type)) != null || hasBindingAnnotation(type);
  }

  private static boolean hasBindingAnnotation(Class<?> type) {
    return type.isAnnotationPresent(ImplementedBy.class) || type.isAnnotationPresent(ProvidedBy.class);
  }
}

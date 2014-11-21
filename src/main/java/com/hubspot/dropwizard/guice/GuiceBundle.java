package com.hubspot.dropwizard.guice;

import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class GuiceBundle<T extends Configuration> implements ConfiguredBundle<T> {

    final Logger logger = LoggerFactory.getLogger(GuiceBundle.class);

    private final AutoConfig autoConfig;
    private final List<Module> modules;
    private final InjectorFactory injectorFactory;
    private Injector injector;
    private DropwizardEnvironmentModule dropwizardEnvironmentModule;
    private Optional<Class<T>> configurationClass;
    private GuiceContainer container;
    private Stage stage;

    public static class Builder<T extends Configuration> {
        private AutoConfig autoConfig;
        private List<Module> modules = Lists.newArrayList();
        private Optional<Class<T>> configurationClass = Optional.absent();
        private InjectorFactory injectorFactory = new InjectorFactoryImpl();

        public Builder<T> addModule(Module module) {
            Preconditions.checkNotNull(module);
            modules.add(module);
            return this;
        }

        public Builder<T> setConfigClass(Class<T> clazz) {
            configurationClass = Optional.of(clazz);
            return this;
        }
        
        public Builder<T> setInjectorFactory(InjectorFactory factory) {
            Preconditions.checkNotNull(factory);
            injectorFactory = factory;
            return this;
        }

        public Builder<T> enableAutoConfig(String... basePackages) {
            Preconditions.checkNotNull(basePackages.length > 0);
            Preconditions.checkArgument(autoConfig == null, "autoConfig already enabled!");
            autoConfig = new AutoConfig(basePackages);
            return this;
        }

        public GuiceBundle<T> build() {
            return build(Stage.PRODUCTION);
        }

        public GuiceBundle<T> build(Stage s) {
            return new GuiceBundle<T>(s, autoConfig, modules, configurationClass, injectorFactory);
        }

    }
    
    public static <T extends Configuration> Builder<T> newBuilder() {
        return new Builder<T>();
    }

    private GuiceBundle(Stage stage, AutoConfig autoConfig, List<Module> modules, Optional<Class<T>> configurationClass, InjectorFactory injectorFactory) {
        Preconditions.checkNotNull(modules);
        Preconditions.checkArgument(!modules.isEmpty());
        Preconditions.checkNotNull(stage);
        this.modules = modules;
        this.autoConfig = autoConfig;
        this.configurationClass = configurationClass;
        this.injectorFactory = injectorFactory;
        this.stage = stage;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        container = new GuiceContainer();
        JerseyContainerModule jerseyContainerModule = new JerseyContainerModule(container);
        if (configurationClass.isPresent()) {
            dropwizardEnvironmentModule = new DropwizardEnvironmentModule<T>(configurationClass.get());
        } else {
            dropwizardEnvironmentModule = new DropwizardEnvironmentModule<Configuration>(Configuration.class);
        }
        modules.add(jerseyContainerModule);
        modules.add(dropwizardEnvironmentModule);

        initInjector();

        if (autoConfig != null) {
            autoConfig.initialize(bootstrap, injector);
        }
    }

    private void initInjector() {
        try {
            injector = injectorFactory.create(this.stage,ImmutableList.copyOf(this.modules));
        } catch(Exception ie) {
            logger.error("Exception occurred when creating Guice Injector - exiting", ie);
            System.exit(1);
        }
    }

    @Override
    public void run(final T configuration, final Environment environment) {
        container.setResourceConfig(environment.jersey().getResourceConfig());
        environment.jersey().replace(new Function<ResourceConfig, ServletContainer>() {
            @Nullable
            @Override
            public ServletContainer apply(ResourceConfig resourceConfig) {
                return container;
            }
        });
        environment.servlets().addFilter("Guice Filter", GuiceFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");
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

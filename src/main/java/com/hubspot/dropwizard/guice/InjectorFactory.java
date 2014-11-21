package com.hubspot.dropwizard.guice;

import java.util.List;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;

/**
 * Factory to create Guice Injector with supplied Stage and List of Modules.
 *
 * Idea behind separating this out is to enable integrating applications to
 * use alternate Guice factories like, - Mycila
 * (https://code.google.com/p/mycila/), - Governator
 * (https://github.com/Netflix/governator)
 */
public interface InjectorFactory {
    Injector create(final Stage stage, final List<Module> modules);
}

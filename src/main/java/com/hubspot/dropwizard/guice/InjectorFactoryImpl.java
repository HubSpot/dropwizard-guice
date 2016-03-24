package com.hubspot.dropwizard.guice;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InjectorFactoryImpl implements InjectorFactory {
  @Override
  public Injector create(final List<Module> modules) {
    return Guice.createInjector(modules);
  }
}

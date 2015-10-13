package com.hubspot.dropwizard.guice;

import com.google.inject.Binder;
import com.google.inject.Module;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class ConfigurationAwareModule<Configuration> implements Module {
  private volatile Configuration configuration = null;

  @Override
  public final void configure(Binder binder) {
    configure(binder, getConfiguration());
  }

  public void setConfiguration(Configuration configuration) {
    checkState(this.configuration == null, "configuration was already set!");
    this.configuration = checkNotNull(configuration, "configuration is null");
  }

  protected Configuration getConfiguration() {
    return checkNotNull(this.configuration, "configuration was not set!");
  }

  protected abstract void configure(final Binder binder, final Configuration configuration);
}

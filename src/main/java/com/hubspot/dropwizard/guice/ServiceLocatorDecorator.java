package com.hubspot.dropwizard.guice;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.api.Unqualified;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class ServiceLocatorDecorator implements ServiceLocator {
  private final ServiceLocator delegate;

  public ServiceLocatorDecorator(ServiceLocator delegate) {
    this.delegate = delegate;
  }

  @Override
  public <T> T getService(Class<T> aClass, Annotation... annotations) throws MultiException {
    return delegate.getService(aClass, annotations);
  }

  @Override
  public <T> T getService(Type type, Annotation... annotations) throws MultiException {
    return delegate.getService(type, annotations);
  }

  @Override
  public <T> T getService(Class<T> aClass, String s, Annotation... annotations) throws MultiException {
    return delegate.getService(aClass, s, annotations);
  }

  @Override
  public <T> T getService(Type type, String s, Annotation... annotations) throws MultiException {
    return delegate.getService(type, s, annotations);
  }

  @Override
  public <T> List<T> getAllServices(Class<T> aClass, Annotation... annotations) throws MultiException {
    return delegate.getAllServices(aClass, annotations);
  }

  @Override
  public <T> List<T> getAllServices(Type type, Annotation... annotations) throws MultiException {
    return delegate.getAllServices(type, annotations);
  }

  @Override
  public <T> List<T> getAllServices(Annotation annotation, Annotation... annotations) throws MultiException {
    return delegate.getAllServices(annotation, annotations);
  }

  @Override
  public List<?> getAllServices(Filter filter) throws MultiException {
    return delegate.getAllServices(filter);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(Class<T> aClass, Annotation... annotations) throws MultiException {
    return delegate.getServiceHandle(aClass, annotations);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(Type type, Annotation... annotations) throws MultiException {
    return delegate.getServiceHandle(type, annotations);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(Class<T> aClass, String s, Annotation... annotations) throws MultiException {
    return delegate.getServiceHandle(aClass, s, annotations);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(Type type, String s, Annotation... annotations) throws MultiException {
    return delegate.getServiceHandle(type, s, annotations);
  }

  @Override
  public <T> List<ServiceHandle<T>> getAllServiceHandles(Class<T> aClass, Annotation... annotations) throws MultiException {
    return delegate.getAllServiceHandles(aClass, annotations);
  }

  @Override
  public List<ServiceHandle<?>> getAllServiceHandles(Type type, Annotation... annotations) throws MultiException {
    return delegate.getAllServiceHandles(type, annotations);
  }

  @Override
  public List<ServiceHandle<?>> getAllServiceHandles(Annotation annotation, Annotation... annotations) throws MultiException {
    return delegate.getAllServiceHandles(annotation, annotations);
  }

  @Override
  public List<ServiceHandle<?>> getAllServiceHandles(Filter filter) throws MultiException {
    return delegate.getAllServiceHandles(filter);
  }

  @Override
  public List<ActiveDescriptor<?>> getDescriptors(Filter filter) {
    return delegate.getDescriptors(filter);
  }

  @Override
  public ActiveDescriptor<?> getBestDescriptor(Filter filter) {
    return delegate.getBestDescriptor(filter);
  }

  @Override
  public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor, Injectee injectee) throws MultiException {
    return delegate.reifyDescriptor(descriptor, injectee);
  }

  @Override
  public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor) throws MultiException {
    return delegate.reifyDescriptor(descriptor);
  }

  @Override
  public ActiveDescriptor<?> getInjecteeDescriptor(Injectee injectee) throws MultiException {
    return delegate.getInjecteeDescriptor(injectee);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> activeDescriptor, Injectee injectee) throws MultiException {
    return delegate.getServiceHandle(activeDescriptor, injectee);
  }

  @Override
  public <T> ServiceHandle<T> getServiceHandle(ActiveDescriptor<T> activeDescriptor) throws MultiException {
    return delegate.getServiceHandle(activeDescriptor);
  }

  @Override
  @Deprecated
  public <T> T getService(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> serviceHandle) throws MultiException {
    return delegate.getService(activeDescriptor, serviceHandle);
  }

  @Override
  public <T> T getService(ActiveDescriptor<T> activeDescriptor, ServiceHandle<?> serviceHandle, Injectee injectee) throws MultiException {
    return delegate.getService(activeDescriptor, serviceHandle, injectee);
  }

  @Override
  public String getDefaultClassAnalyzerName() {
    return delegate.getDefaultClassAnalyzerName();
  }

  @Override
  public void setDefaultClassAnalyzerName(String s) {
    delegate.setDefaultClassAnalyzerName(s);
  }

  @Override
  public Unqualified getDefaultUnqualified() {
    return delegate.getDefaultUnqualified();
  }

  @Override
  public void setDefaultUnqualified(Unqualified unqualified) {
    delegate.setDefaultUnqualified(unqualified);
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public long getLocatorId() {
    return delegate.getLocatorId();
  }

  @Override
  public ServiceLocator getParent() {
    return delegate.getParent();
  }

  @Override
  public void shutdown() {
    delegate.shutdown();
  }

  @Override
  public ServiceLocatorState getState() {
    return delegate.getState();
  }

  @Override
  public boolean getNeutralContextClassLoader() {
    return delegate.getNeutralContextClassLoader();
  }

  @Override
  public void setNeutralContextClassLoader(boolean b) {
    delegate.setNeutralContextClassLoader(b);
  }

  @Override
  public <T> T create(Class<T> aClass) {
    return delegate.create(aClass);
  }

  @Override
  public <T> T create(Class<T> aClass, String s) {
    return delegate.create(aClass, s);
  }

  @Override
  public void inject(Object o) {
    delegate.inject(o);
  }

  @Override
  public void inject(Object o, String s) {
    delegate.inject(o, s);
  }

  @Override
  public void postConstruct(Object o) {
    delegate.postConstruct(o);
  }

  @Override
  public void postConstruct(Object o, String s) {
    delegate.postConstruct(o, s);
  }

  @Override
  public void preDestroy(Object o) {
    delegate.preDestroy(o);
  }

  @Override
  public void preDestroy(Object o, String s) {
    delegate.preDestroy(o, s);
  }

  @Override
  public <U> U createAndInitialize(Class<U> aClass) {
    return delegate.createAndInitialize(aClass);
  }

  @Override
  public <U> U createAndInitialize(Class<U> aClass, String s) {
    return delegate.createAndInitialize(aClass, s);
  }
}

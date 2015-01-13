package com.hubspot.dropwizard.guice;

import com.google.inject.servlet.ServletModule;

public class JerseyContainerModule extends ServletModule {

  @Override
  protected void configureServlets() {
    bind(GuiceContainer.class).to(GuiceContainer.class);
  }

//  @Provides
//  public WebApplication webApp(GuiceContainer guiceContainer) {
//    return container.getWebApplication();
//  }
//
//  @Provides
//  public Providers providers(WebApplication webApplication) {
//    return webApplication.getProviders();
//  }
//
//  @Provides
//  public FeaturesAndProperties featuresAndProperties(WebApplication webApplication) {
//    return webApplication.getFeaturesAndProperties();
//  }
//
//  @Provides
//  public MessageBodyWorkers messageBodyWorkers(WebApplication webApplication) {
//    return webApplication.getMessageBodyWorkers();
//  }
//
//  @Provides
//  public ExceptionMapperContext exceptionMapperContext(WebApplication webApplication) {
//    return webApplication.getExceptionMapperContext();
//  }
//
//  @Provides
//  public ResourceContext resourceContext(WebApplication webApplication) {
//    return webApplication.getResourceContext();
//  }
//
//  @RequestScoped
//  @Provides
//  public HttpContext httpContext(WebApplication webApplication) {
//    return webApplication.getThreadLocalHttpContext();
//  }
//
//  @Provides
//  @RequestScoped
//  public UriInfo uriInfo(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getUriInfo();
//  }
//
//  @Provides
//  @RequestScoped
//  public ExtendedUriInfo extendedUriInfo(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getUriInfo();
//  }
//
//  @RequestScoped
//  @Provides
//  public HttpRequestContext requestContext(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getRequest();
//  }
//
//  @RequestScoped
//  @Provides
//  public HttpHeaders httpHeaders(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getRequest();
//  }
//
//  @RequestScoped
//  @Provides
//  public Request request(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getRequest();
//  }
//
//  @RequestScoped
//  @Provides
//  public SecurityContext securityContext(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getRequest();
//  }
//
//  @RequestScoped
//  @Provides
//  public HttpResponseContext responseContext(WebApplication wa) {
//    return wa.getThreadLocalHttpContext().getResponse();
//  }
}

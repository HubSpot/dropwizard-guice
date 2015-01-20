Dropwizard-Guice
================

A simple DropWizard extension for integrating Guice via a bundle. It optionally uses classpath 
scanning courtesy of the Reflections project to discover resources and more to install into 
the dropwizard environment upon service start.

### Usage

```xml
    <dependencies>
        <dependency>
            <groupId>com.hubspot.dropwizard</groupId>
            <artifactId>dropwizard-guice</artifactId>
            <version>0.8.1</version>
        </dependency>
    </dependencies>
```

Simply install a new instance of the bundle during your service initialization
```java
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

  private GuiceBundle<HelloWorldConfiguration> guiceBundle;

  public static void main(String[] args) throws Exception {
    new HelloWorldApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

    guiceBundle = GuiceBundle.<HelloWorldConfiguration>newBuilder()
      .addModule(new HelloWorldModule())
      .setConfigClass(HelloWorldConfiguration.class)
      .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public String getName() {
    return "hello-world";
  }

  @Override
  public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment) throws Exception {
    environment.jersey().register(HelloWorldResource.class);
    environment.lifecycle().manage(bundle.getInjector().getInstance(TemplateHealthCheck.class));
  }
}
```

### Auto Config

Lastly, you can enable auto configuration via package scanning.
```java
public class HelloWorldApplication extends Application<HelloWorldConfiguration> {

  public static void main(String[] args) throws Exception {
    new HelloWorldApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

    GuiceBundle<HelloWorldConfiguration> guiceBundle = GuiceBundle.<HelloWorldConfiguration>newBuilder()
      .addModule(new HelloWorldModule())
      .enableAutoConfig(getClass().getPackage().getName())
      .setConfigClass(HelloWorldConfiguration.class)
      .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public String getName() {
    return "hello-world";
  }

  @Override
  public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment) throws Exception {
    // now you don't need to add resources, tasks, healthchecks, providers, bundles or managed
    // you must have your health checks inherit from InjectableHealthCheck in order for them to be injected
    // as of dropwizard 0.8.0, to AutoConfig `Task`, its constructor needs `@Named() String` injection for the task name.
    // See: `InjectedTask` example in test package
  }
}
```

### Just-In-Time Bindings

HK2 Guice Bridge does not pick up [just-in-time](https://github.com/google/guice/wiki/JustInTimeBindings) bindings
(ie any objects that are not configured or provided by the Module). You can easily convert your JIT binding to be
explicit in your Module by using `bind()`:

```java
public class HelloWorldModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(jitObject1.class)
  }
```

There also many other ways to configure explicit bindings, including using a Provider (see below):

### Guice Providers

If you are having trouble accessing your Configuration or Environment inside a Guice Module, you could try using a provider.

```java
public class HelloWorldModule extends AbstractModule {

  @Override
  protected void configure() {
    // anything you'd like to configure, such as explicit bindings
  }

  @Provides
  public SomePool providesSomethingThatNeedsConfiguration(HelloWorldConfiguration configuration) {
    return new SomePool(configuration.getPoolName());
  }

  @Provides
  public SomeManager providesSomenthingThatNeedsEnvironment(Environment env) {
    return new SomeManager(env.getSomethingFromHere()));
  }
}
```
### Injector Factory

You can also replace the default Guice `Injector` by implementing your own `InjectorFactory`. For example if you want 
to use [Governator](https://github.com/Netflix/governator) you can create the following implementation:

```java
public class GovernatorInjectorFactory implements InjectorFactory {

  @Override
  public Injector create( final Stage stage, final List<Module> modules ) {
    return LifecycleInjector.builder().inStage( stage ).withModules( modules ).build()
        .createInjector();
  }
}
```

and then set the InjectorFactory when initializing the GuiceBundle:

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

  GuiceBundle<HelloWorldConfiguration> guiceBundle = GuiceBundle.<HelloWorldConfiguration>newBuilder()
    .addModule(new HelloWorldModule())
    .enableAutoConfig(getClass().getPackage().getName())
    .setConfigClass(HelloWorldConfiguration.class)
    .setInjectorFactory( new GovernatorInjectorFactory() )
    .build();

 bootstrap.addBundle(guiceBundle);
}
```

*NOTE:* Dropwizard-Guice has been tested with Governator version 1.2.20. If you use the latest 1.3.x Governator release
 you may encounter errors.


Please fork [an example project](https://github.com/eliast/dropwizard-guice-example) if you'd like to get going right away. 

Enjoy!

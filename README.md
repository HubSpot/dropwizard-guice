# Dropwizard-Guice
A simple DropWizard extension for integrating Guice via a bundle. It optionally uses classpath 
scanning courtesy of the Reflections project to discover resources and more to install into 
the dropwizard environment upon service start.

## Usage
```xml
    <dependencies>
        <dependency>
            <groupId>com.hubspot.dropwizard</groupId>
            <artifactId>dropwizard-guice</artifactId>
            <version>0.8.4.0</version>
        </dependency>
    </dependencies>
```

A list of available versions can be found at https://github.com/HubSpot/dropwizard-guice/releases

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
    environment.lifecycle().manage(guiceBundle.getInjector().getInstance(TemplateHealthCheck.class));
  }
}
```

## Auto Configuration
You can enable auto configuration via package scanning.
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
    // now you don't need to add resources, tasks, healthchecks or providers
    // you must have your health checks inherit from InjectableHealthCheck in order for them to be injected
  }
}
```

Dropwizard `Task` requires a TaskName. Therefore when Auto Configuring a `Task`, you need to inject in the TaskName:

    @Singleton
    public class MyTask extends Task {

        @Inject
        protected MyTask(@Named("MyTaskName") String name) {
            super(name);
        }

        @Override
        public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {

        }
    }

And bind the TaskName in your module:

    bindConstant().annotatedWith(Names.named("MyTaskName")).to("my awesome task");

See the test cases: `InjectedTask` and `TestModule` for more details.

## Environment and Configuration
If you are having trouble accessing your Configuration or Environment inside a Guice Module, you could try using a provider.

```java
public class HelloWorldModule extends AbstractModule {

  @Override
  protected void configure() {
    // anything you'd like to configure
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

## Injector Factory
You can also replace the default Guice `Injector` by implementing your own `InjectorFactory`. For example if you want 
to use [Governator](https://github.com/Netflix/governator), you can set the following InjectorFactory (using Java 8 Lambda)
when initializing the GuiceBundle:

```java
@Override
public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {

  GuiceBundle<HelloWorldConfiguration> guiceBundle = GuiceBundle.<HelloWorldConfiguration>newBuilder()
    .addModule(new HelloWorldModule())
    .enableAutoConfig(getClass().getPackage().getName())
    .setConfigClass(HelloWorldConfiguration.class)
    .setInjectorFactory((stage, modules) -> LifecycleInjector.builder()
        .inStage(stage)
        .withModules(modules)
        .build()
        .createInjector()))
    .build();

 bootstrap.addBundle(guiceBundle);
}
```

## Testing
As of Dropwizard 0.8.x, when writing Integration Tests using `DropwizardAppRule`, you need to reset
[jersey2-guice](https://github.com/Squarespace/jersey2-guice) by running:

    BootstrapUtils.reset();

## Examples
Please fork [an example project](https://github.com/eliast/dropwizard-guice-example) if you'd like to get going right away. 

You may also find more updated and comprehensive examples in the [test cases](https://github.com/HubSpot/dropwizard-guice/tree/master/src/test).

Enjoy!

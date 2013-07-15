Dropwizard-Guice
================

A simple DropWizard extension for integrating Guice via a bundle. It optionally uses classpath 
scanning courtesy of the Reflections project to discover resources and more to install into 
the dropwizard environment upon service start.

### Usage

Simply install a new instance of the bundle during your service initialization
```java
public class HelloWorldService extends Application<HelloWorldConfiguration> {

  public static void main(String[] args) throws Exception {
		new HelloWorldService().run(args);
	}

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.addBundle(GuiceBundle.newBuilder()
            .addModule(new HelloWorldModule())
            .build()
        );
    }

	@Override
	public void run(HelloWorldConfiguration configuration, final Environment environment) {
		environment.jersey().register(HelloWorldResource.class);
		environment.healthChecks().register("Template", TemplateHealthCheck.class);
	}

}
```

Lastly, you can enable auto configuration via package scanning.
```java
public class HelloWorldService extends Service<HelloWorldConfiguration> {

  public static void main(String[] args) throws Exception {
        new HelloWorldService().run(args);
    }

    @Override
    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
        bootstrap.setName("hello-world");
        bootstrap.addBundle(GuiceBundle.newBuilder()
            .addModule(new HelloWorldModule())
            .enableAutoConfig(getClass().getPackage().getName())
            .build()
        );
    }

    @Override
    public void run(HelloWorldConfiguration configuration, final Environment environment) {
        // now you don't need to add resources, tasks, healthchecks or providers
        // you must have your health checks inherit from InjectableHealthCheck in order for them to be injected
    }

}
```

Please fork [an example project](https://github.com/eliast/dropwizard-guice-example) if you'd like to get going right away. 

Enjoy!

Dropwizard-Guice
================

A simple DropWizard extension for integrating Guice via bundle. 
It optionally uses classpath scanning courtesy of the Reflections project to discover resources and more to install into the dropwizard environment upon service start.

### Usage

Extend the GuiceBundle so you can create all your necessary Guice Modules by overriding configureModules

```
public class HelloWorldGuiceBundle extends GuiceBundle<HelloWorldConfiguration> {

  public HelloWorldGuiceBundle(String ... basePackages) {
		super(basePackages);
	}
	
	@Override
	protected Collection<? extends Module> configureModules(HelloWorldConfiguration configuration) {
		return Lists.newArrayList(new HelloWorldModule(configuration));
	}
	
}
```

Then simply install the a new instance of the bundle during your service initialization

```
public class HelloWorldService extends Service<HelloWorldConfiguration> {

  public static void main(String[] args) throws Exception {
		new HelloWorldService().run(args);
	}

	@Override
	public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
		bootstrap.setName("hello-world");
		bootstrap.addBundle(new HelloWorldGuiceBundle());
	}

	@Override
	public void run(HelloWorldConfiguration configuration, final Environment environment) {
		environment.addResource(HelloWorldResource.class);
		environment.addResource(TemplateHealthCheck.class);
	}

}
```

Lastly, you can pass one or more base packages to the GuiceBundle subclass constructor
to enable auto configuration via package scanning.

Please fork [an example project](https://github.com/HubSpot/dropwizard-guice) if you'd like to get going right away. 

Enjoy!
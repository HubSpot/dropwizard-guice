package com.hubspot.dropwizard.guice.objects;

import com.google.common.collect.ImmutableMultimap;
import com.google.inject.Singleton;
import io.dropwizard.servlets.tasks.Task;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.PrintWriter;

@Singleton
public class NamedTask extends Task {

    @Inject
    protected NamedTask(@Named("TestTaskName") String name) {
        super(name);
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {

    }
}

package com.github.abirmingham.gradle.jasquire;

import org.gradle.api.Plugin;
import org.gradle.api.Project
import org.gradle.api.GradleException;


class JasquirePlugin implements Plugin<Project> {

    public void apply(Project project) {
        project.extensions.create("jasquire", JasquireExtension)

        JasquireResourcesTask resourcesTask = project.tasks.add("jasquireMain", JasquireResourcesTask.class);
        JasquireRunnerTask    runnerTask    = project.tasks.add("jasquireTest", JasquireRunnerTask.class);

        project.afterEvaluate { proj, state ->
            if (state.getFailure() == null) {
                def config = project.jasquire.properties;

                // Validate required
                ['requireJS', 'requireJSConfig', 'requireJSBaseUrl', 'jsSourcesDir'].each { key ->
                    if (! config[key]) throw new GradleException("Jasquire required configuration is missing or blank: " + key);
                }

                // Specify task inputs/outputs
                resourcesTask.with {
                    inputs.dir(config.jsSpecsDir);
                    outputs.dir(config.reportTargetDir + "/lib");
                    outputs.dir(config.htmlRunner);
                }

                runnerTask.with {
                    inputs.dir(config.jsSpecsDir);
                    inputs.dir(config.jsSourcesDir);
                    outputs.dir(config.consoleOut);
                }
            }
        }

        runnerTask.dependsOn(resourcesTask);

        if (project.tasks.findByName("test") != null) {
            project.test.dependsOn(runnerTask);
        }
    }
}

class JasquireExtension {
    // Required
    String requireJS;
    String requireJSConfig;
    String requireJSBaseUrl;
    String jsSourcesDir; // used exclusively for triggering task reruns

    // Optional
    String jsSpecsDir      = "src/test/javascript";
    String reportTargetDir = "build/reports/jasquire";
    String htmlRunner      = "build/reports/jasquire/AllSpecsRunner.html";
    String consoleOut      = "build/reports/jasquire/jasquire.out";
    boolean failBuildOnFailure = true;
}

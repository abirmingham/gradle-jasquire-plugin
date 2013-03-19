package com.github.abirmingham.gradle.jasquire;


import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class JasquireRunnerTask extends DefaultTask {

    File targetDir;
    File outputFile;

    @TaskAction
    def run() {
        def config = project.jasquire.properties;
        targetDir = project.file(config.reportTargetDir);
        outputFile = project.file("${targetDir}/jasmine.out");

        runSpecs();
        processResult();
    }

    def runSpecs() {
        project.ant.java(
                fork: true,
                jar: "${targetDir}/lib/js.jar",
                resultProperty: "jasmineResult",
                classpath: project.files("${targetDir}"),
                output: outputFile
        ) {
            arg(value: "-opt")
            arg(value: "-1")
            arg(value: project.file("${targetDir}/lib/envJS.js"))
            arg(value: project.file("${targetDir}/AllSpecsRunner.html"))
        }

        if ( ant.jasmineResult != "0" ) {
            throw new Exception( "Jasmine test runner process exited with error code: " + ant.jasmineResult );
        }
    }

    def processResult() {
        def config = project.jasquire.properties;
        List<String> outputLines = outputFile.readLines("UTF-8");
        String failureMessage = "";
        int failureCount = 0;

        outputLines.each {
            if (it.startsWith("FAILED: ") || it.startsWith("Exception") || it.startsWith("failed to open")) {
                failureCount++
                failureMessage += it + "\n"
            } else if (it.startsWith("- SPEC ")) {
                failureMessage += it + "\n"
            }
        };

        if ( failureCount > 0 ) {
            failureMessage = "Jasmine Specs reported " + failureCount + " failure(s):\n\n" + failureMessage + "\nJasmine Specs Failed; see ${outputFile}";

            if (config.failBuildOnFailure) {
                throw new RuntimeException( failureMessage );
            } else {
                println(failureMessage);
            }
        }
    }

}
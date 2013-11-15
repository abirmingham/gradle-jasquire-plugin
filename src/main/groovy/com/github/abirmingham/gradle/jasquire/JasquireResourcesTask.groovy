package com.github.abirmingham.gradle.jasquire;

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.DefaultTask;
import groovy.xml.MarkupBuilder

class JasquireResourcesTask extends DefaultTask {

    File targetDir;

    static STATIC_RESOURCES = [
            "jasmine.css",
            "jasmine.js",
            "jasmine-html.js",
            "jasmine.console_reporter.js",
            "env.rhino.1.2.js",
            "envjs.bootstrap.js",
            "jasquire-r.js",
            "js.jar"
    ];

    @TaskAction
	def copyAndGenerateResources() {
        targetDir = project.file(project.jasquire.properties.reportTargetDir);
        targetDir.mkdirs();

        STATIC_RESOURCES.each {
            copyResource( ["lib"], it )
        };

        generateEnvJS();
        generateSpecRunnerHTML();
	}

    def copyResource( List<String> pathElements, String resourceName ) {
        File outdir = targetDir;
        String s = "";

        pathElements.each( {
            outdir = new File( outdir, it );
            s += it + "/";
        } );

        outdir.mkdirs();

        def outfile = new File( outdir, resourceName );
        if (outfile.exists()) outfile.delete();

        outfile.createNewFile();
        outfile.append( JasquireResourcesTask.class.getClassLoader().getResourceAsStream(resourceName));
    }

    def generateEnvJS() {
        File output = project.file("${targetDir}/lib/envJS.js");
        String targetURL = "${targetDir}".replaceAll("\\\\", "/");
        StringBuffer content = new StringBuffer("");
        content.append("load(\"${targetURL}/lib/env.rhino.1.2.js\");\n");
        content.append("load(\"${targetURL}/lib/jasquire-r.js\");\n");
        content.append("load(\"${targetURL}/lib/envjs.bootstrap.js\");");
        output.write( content.toString() );
    }

    def generateSpecRunnerHTML() {
        def config = project.jasquire.properties;

        File output = project.file(config.htmlRunner);
        if (output.exists()) output.delete();
        output.createNewFile();

        List<String> specFiles = new ArrayList<String>();

        project.fileTree (project.file(config.jsSpecsDir)).each {
            String filePath = it.getCanonicalPath().replaceAll("\\\\", "/");
            specFiles.add("\"file:///${filePath}\"");
        }

        def writer = new StringWriter()
        def html   = new MarkupBuilder(writer)

        def requireJSAsUrl = project.file(config.requireJS).getCanonicalPath().replaceAll("\\\\", "/");
        def requireJSConfigAsUrl = project.file(config.requireJSConfig).getCanonicalPath().replaceAll("\\\\", "/");
        def requireJSBaseUrlAsUrl = project.file(config.requireJSBaseUrl).getCanonicalPath().replaceAll("\\\\", "/");

        html.html {
            head {
                title { mkp.yield( "Jasmine Spec Runner" ) }
                link ( rel: "stylesheet", href: "lib/jasmine.css" ) {}
                script ( type: "text/javascript", src: "lib/jasmine.js" ) { mkp.yield( "" ) }
                script ( type: "text/javascript", src: "lib/jasmine-html.js" ) { mkp.yield( "" ) }
                script ( type: "text/javascript", src: "lib/jasmine.console_reporter.js" ) { mkp.yield( "" ) }
                script ( type: "text/javascript", src: "file:///${requireJSAsUrl}" ) { mkp.yield( "" ) }
                script ( type: "text/javascript" ) {
                    mkp.yield( "\n\trequire([\"file:///${requireJSConfigAsUrl}\"], function() {            ");
                    mkp.yield( "\n\t\trequire.config({ baseUrl: \"file:///${requireJSBaseUrlAsUrl}\" });   ");
                    mkp.yield( "\n\t\trequire( ${specFiles.toArray().toString()} , function() {            ");
                    mkp.yield( "\n\t\t\tjasmine.getEnv().addReporter(new jasmine.TrivialReporter());       ");
                    mkp.yield( "\n\t\t\tjasmine.getEnv().addReporter(new jasmine.CCMConsoleReporter());    ");
                    mkp.yield( "\n\t\t\tjasmine.getEnv().execute();                                        ");
                    mkp.yield( "\n\t\t});                                                                  ");
                    mkp.yield( "\n\t});                                                                    ");
                }
            }
            body {
                mkp.yield( "");
            }
        }

        output.write( writer.toString() )
    }

}
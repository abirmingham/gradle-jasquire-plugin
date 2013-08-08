Generates/executes a jasmine spec runner for an AMD-compliant project

## Use
```
buildscript {
    dependencies {
        classpath 'com.github.abirmingham:gradle-jasquire-plugin:1.1'
    }
}

apply plugin: 'jasquire'
```

## Configure
```
jasquire {
    // Required
    requireJSBaseUrl    = "$projectDir/src/main/webapp/js/main";
    requireJS           = "src/main/webapp/js/vendor/require/require-jquery.js";
    requireJSConfig     = "src/main/webapp/js/util/commonConfig.js";
    jsSourcesDir        = "src/main/webapp/js";
    
    // Optional
    jsSpecsDir         = "src/test/javascript";
    reportTargetDir    = "build/reports/jasquire";
    htmlRunner         = "build/reports/jasquire/AllSpecsRunner.html";
    consoleOut         = "build/reports/jasquire/jasquire.out";
    failBuildOnFailure = true;
}
```

## Profit
All files in jsSpecsDir will be collected and run automatically as part of the build's test task. Additionally, the 'jasquireMain' and 'jasquireTest' tasks may be run independently. Finally, the htmlRunner can be opened in a browser for debugging purposes, and changes to specs/source will be reflected automatically.

## Credit
Matías Rodríguez

Serials Solutions

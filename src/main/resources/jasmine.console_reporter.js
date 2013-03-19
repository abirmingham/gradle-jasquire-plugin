(function() {

    if (! jasmine) {
        throw new Exception("jasmine library does not exist in global namespace!");
    }

    // overriding the TrivialReporter.log function, only CCMConsoleReporter should do this
    if (jasmine.TrivialReporter) {
        jasmine.TrivialReporter.prototype.log = function() {};
    }

    /**
     * Basic reporter that outputs spec results to the browser console.
     * Useful if you need to test an html page and don't want the TrivialReporter
     * markup mucking things up.
     *
     * Usage:
     *
     * jasmine.getEnv().addReporter(new jasmine.ConsoleReporter());
     * jasmine.getEnv().execute();
     */
    var CCMConsoleReporter = function() {
        this.started = false;
        this.finished = false;
    };

    CCMConsoleReporter.prototype = {

        reportRunnerStarting: function(runner) {
            this.started = true;
            //this.log("<jasmine>");
        },

        reportRunnerResults: function(runner) {
            this.finished = true;
            //this.log("</jasmine>");
        },

        reportSpecResults: function(spec) {
            var results = spec.results();

            var status = results.passed() ? "PASSED" : "FAILED";

            var message = status + ": " + spec.suite.description + " - " + spec.description;

            if(!results.passed()) {
                var items = results.getItems();
                for(var i = 0; i < items.length; i++) {
                    var result = items[i];
                    if (result.type === "expect") {
                        message += "\n- SPEC " + (i+1) + ": " + result.message + " ";
                        if (result.trace.stack) {
                            message += result.trace.stack;
                        }
                    }
                }
            }

            this.log(message);
        },

        reportSuiteResults: function(suite) {
            var results = suite.results();

            //this.log(suite.description + ": " + results.passedCount + " of " + results.totalCount + " passed.");
        },

        log: function(str) {
            var console = jasmine.getGlobal().console;

            if (console && console.log) {
                console.log(str);
            }
        }
    };

    // export public
    jasmine.CCMConsoleReporter = CCMConsoleReporter;
})();
Envjs.afterScriptLoad = {
  '.*': function(scriptNode) {

   // trigger script load event
    var event = document.createEvent('Event');
    event.initEvent('load', true, true);
    scriptNode.dispatchEvent(event);
  }
};

Envjs.scriptTypes['text/javascript'] = true;
//window.location = 'SpecRunner.html';
for (var i = 0; i < arguments.length; i++) {
    var specFile = arguments[i];
    
    console.log("Loading: " + specFile);
    
    window.location = specFile
}

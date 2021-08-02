(function(window, setupBindings){
    if (!window.appHost){
        let callbackMemory = {}
        window.appHost = {
            on: function(eventName, callback){
                if (!callbackMemory[eventName]){
                    callbackMemory[eventName] = []
                }
                callbackMemory[eventName].push(callback)
                
                return function(){
                    callbackMemory[eventName].splice(
                        callbackMemory[eventName].indexOf(callback),
                        1
                    )
                }
            },
            emit: function(eventName, value){
                for(let callback of callbackMemory[eventName]){
                    callback(value)
                }
            }
        }

        setupBindings(window.appHost)
    }
})(window, function(appHost){
    window.addEventListener("message", (event) => {
        let payload = JSON.parse(event.data)
        appHost.emit(payload.type, payload.payload)
    }, false);
});
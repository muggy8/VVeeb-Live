(function(Live2D){
    let canvas = document.createElement('canvas')

    let gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl')

    if (!gl) {
        alert('Cannot initialize WebGL. This browser does not support.');
        gl = null;
        return false;
    }

    document.body.appendChild(canvas);

    gl.enable(gl.BLEND)
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA)

    let options = new Live2D.Option()
    Live2D.CubismFramework.startUp(options)
    Live2D.CubismFramework.initialize()


})(window.Live2D)
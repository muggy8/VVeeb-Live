package com.muggy.vveeb2d

import android.content.Context
import android.os.Environment
import fi.iki.elonen.NanoHTTPD
import android.webkit.MimeTypeMap
import java.io.File

class RendererServer(val port:Int, val context:Context) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        var uri = session.uri
        if (uri.endsWith("/")){
            uri += "index.html"
        }
        uri = uri.removePrefix("/")

        println("fetched: ${uri}")

        try {
            if (uri.startsWith("Resources/Haru/")) {
                return serveDynamicModel(uri)
            }

            return serveStaticResponse(uri)
        }
        catch (e: Exception) {
            val message = "Failed to load asset $uri because $e"
            println(message)
            e.printStackTrace()
            return newFixedLengthResponse(message)
        }
    }

    protected fun serveStaticResponse(uri:String): Response{
        return newChunkedResponse(
            Response.Status.OK,
            getMime(uri),
            context.assets.open("web-renderer/$uri")
        )
    }

    protected var root: String = Environment.getExternalStorageDirectory().toString()
    protected fun serveDynamicModel(uri:String): Response{
        var correctedUri = uri.removePrefix("Resources/Haru")

        var modelDir = File("$root/VVeeb2D/model/")
        if (!modelDir.exists()) {
            modelDir.mkdirs();
        }

        if (correctedUri.endsWith("model3.json")){
            var updated = false
            modelDir.listFiles().forEach { file:File ->
                if (!updated && file.name.endsWith("model3.json")){
                    correctedUri = correctedUri.replaceAfterLast("/", file.name)
                }
            }
        }

        correctedUri = correctedUri.removePrefix("/")

//        println("correctedUri: ${correctedUri}")
        val requestedAsset = File(modelDir, correctedUri)
        if (!requestedAsset.exists()){
            return serveStaticResponse(uri)
        }
//        println("requested ${uri} but actually sending ${requestedAsset}")

        return newChunkedResponse(
            Response.Status.OK,
            getMime(uri),
            requestedAsset.inputStream()
        )
    }

    protected fun serveOverlay(uri:String): Response{
        var requestedAsset = File("$root/VVeeb2D", uri)
        if (!requestedAsset.exists()){
            if (uri.endsWith("index.html")){
                return newFixedLengthResponse(
                    Response.Status.OK,
                    getMime("/index.html"),
                    "<html><head></head><body></body></html>"
                )
            }
            else{
                return newFixedLengthResponse(
                    Response.Status.NOT_FOUND,
                    getMime("data.txt"),
                    "$uri is not found"
                )
            }
        }
        return newChunkedResponse(
            Response.Status.OK,
            getMime(uri),
            requestedAsset.inputStream()
        )
    }

    protected fun getMime(uri:String) : String? {
        var mime: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri)
        if (extension != null) {
            mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }

        return mime
    }

}

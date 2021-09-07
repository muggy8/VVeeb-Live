package com.muggy.vveeb2d

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import android.webkit.MimeTypeMap

class RendererServer(val port:Int, val context:Context) : NanoHTTPD(port) {
    override fun serve(session: IHTTPSession): Response {
        //            session.uri

        var uri = session.uri
        if (uri.endsWith("/")){
            uri += "index.html"
        }
        uri = uri.removePrefix("/")
        try{
            var mime: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(uri)
            if (extension != null) {
                mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }

            return newChunkedResponse(
                Response.Status.OK,
                mime,
                context.assets.open("web-renderer/$uri")
            )
        }
        catch (e: Exception) {
            val message = "Failed to load asset $uri because $e"
            println(message)
            e.printStackTrace()
            return newFixedLengthResponse(message)
        }
    }

}

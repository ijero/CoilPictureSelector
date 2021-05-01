package cn.ijero.coilpictureselector

import android.os.Build
import android.widget.ImageView
import coil.ImageLoader
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import java.io.File

fun ImageView.loadImage(
    url: String,
    imageLoader: ImageLoader = context.imageLoader,
    builder: ImageRequest.Builder.() -> Unit = {}
) {
    if (url.startsWith("http") || url.startsWith("https") || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        load(url, imageLoader, builder)
    } else {
        load(File(url), imageLoader, builder)
    }
}

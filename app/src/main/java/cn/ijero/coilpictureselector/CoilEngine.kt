package cn.ijero.coilpictureselector

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION.SDK_INT
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView

class CoilEngine : ImageEngine {
    companion object{
        @Volatile
        private var mInstance: CoilEngine? = null

        val instance: CoilEngine?
            get() {
                if (mInstance == null) {
                    synchronized(CoilEngine::class.java) {
                        if (mInstance == null) {
                            mInstance = CoilEngine()
                        }
                    }
                }
                return mInstance
            }
    }


    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
//        logE("加载的图片地址11111111$url")
        imageView.load(url)
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @param callback      网络图片加载回调监听 {link after version 2.5.1 Please use the #OnImageCompleteCallback#}
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView, callback: OnImageCompleteCallback?) {
        imageView.load(url) {
            target({
                // onStart
                callback?.onShowLoading()
            }, {
                // onError
                callback?.onHideLoading()
            }) {
                // onSuccess
                callback?.onHideLoading()
                val eqLongImage = MediaUtils.isLongImg(
                    it.intrinsicWidth,
                    it.intrinsicHeight
                )
                longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                if (eqLongImage) {
                    // 加载长图
                    longImageView.isQuickScaleEnabled = true
                    longImageView.isZoomEnabled = true
                    longImageView.setDoubleTapZoomDuration(100)
                    longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                    longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                    longImageView.setImage(
                        ImageSource.bitmap((it as BitmapDrawable).bitmap),
                        ImageViewState(0f, PointF(0f, 0f), 0)
                    )
                } else {
                    // 普通图片
                    imageView.load(it)
                }
            }
        }
    }


    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @ 已废弃
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    }


    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.load(url) {
            size(180, 180)
            transformations()
            placeholder(R.drawable.picture_image_placeholder)
            target {
                val circularBitmapDrawable =
                    RoundedBitmapDrawableFactory.create(
                        context.resources,
                        (it as BitmapDrawable).bitmap
                    )
                circularBitmapDrawable.cornerRadius = 8f
                imageView.setImageDrawable(circularBitmapDrawable)
            }
        }
    }

    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        val imageLoader = ImageLoader.Builder(context).componentRegistry {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }.build()
        imageView.load(url, imageLoader)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.load(url) {
            size(200, 200)
            placeholder(R.drawable.picture_image_placeholder)
        }
    }
}
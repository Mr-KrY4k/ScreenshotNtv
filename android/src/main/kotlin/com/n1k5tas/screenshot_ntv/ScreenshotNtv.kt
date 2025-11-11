package com.n1k5tas.screenshot_ntv

import ScreenshotNtvApi
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import java.io.ByteArrayOutputStream


class ScreenshotNtv : ScreenshotNtvApi {
    private val pixelCopyThread = HandlerThread("ScreenshotPixelCopy").apply { start() }
    private val pixelCopyHandler = Handler(pixelCopyThread.looper)
    private val compressionThread = HandlerThread("ScreenshotCompression").apply { start() }
    private val compressionHandler = Handler(compressionThread.looper)
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun takeScreenshot() {
        val activity = ScreenshotNtvPlugin.currentActivity ?: return
        val window = activity.window
        val view = window.decorView.rootView

        val bitmap: Bitmap

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val width = view.width.takeIf { it > 0 } ?: window.decorView.width
            val height = view.height.takeIf { it > 0 } ?: window.decorView.height

            if (width <= 0 || height <= 0) {
                Log.e("ScreenshotNtv", "Window has invalid size: $width x $height")
                return
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            try {
                PixelCopy.request(window, bitmap, { copyResult ->
                    if(copyResult == PixelCopy.SUCCESS){
                        deliverBitmap(bitmap)
                    } else {
                        Log.e("ScreenshotNtv", "PixelCopy failed with code $copyResult")
                        bitmap.recycle()
                    }
                }, pixelCopyHandler)
            } catch (e: IllegalArgumentException) {
                Log.e("ScreenshotNtv", "PixelCopy threw IllegalArgumentException", e)
                deliverBitmap(bitmap)
                }
        }
        else{
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.setBitmap(null)
            deliverBitmap(bitmap)
        }
    }

    private fun getSurfaceView(view: View): SurfaceView?{
        var surfaceView: SurfaceView? = null
        traverseView(view) {
                v -> if(v is SurfaceView) surfaceView = v
        }
        return surfaceView
    }

    private fun traverseView(view: View, callback: (View) -> Unit){
        callback(view)
        if(view is ViewGroup){
            for (i in 0 until view.childCount){
                traverseView(view.getChildAt(i), callback)
            }
        }
    }

    private fun deliverBitmap(bitmap: Bitmap) {
        compressionHandler.post {
            val byteArray = bitmapToByteArray(bitmap)
            bitmap.recycle()
            mainHandler.post {
                ScreenshotNtvPlugin.screenshotNtvFlutterListener.takeResultScreenshot(byteArray) {}
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    fun dispose() {
        pixelCopyThread.quitSafely()
        compressionThread.quitSafely()
    }
}
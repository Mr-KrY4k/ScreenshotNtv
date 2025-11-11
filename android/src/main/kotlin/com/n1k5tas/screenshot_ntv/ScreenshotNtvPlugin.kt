package com.n1k5tas.screenshot_ntv

import ScreenshotNtvApi
import ScreenshotNtvFlutterListener
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding


/** ScreenshotNtvPlugin */
class ScreenshotNtvPlugin: FlutterPlugin, ActivityAware, ActivityLifecycleCallbacks {
  companion object {
    @SuppressLint("StaticFieldLeak")
    var currentActivity: Activity? = null
    @SuppressLint("StaticFieldLeak")
    lateinit var screenshotNtvFlutterListener: ScreenshotNtvFlutterListener
  }

  private var api: ScreenshotNtv? = null

  override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    val context = binding.getApplicationContext();
    if (context is Application) {
      val application = context as Application
      application.registerActivityLifecycleCallbacks(this)
    }
    val screenshotApi = ScreenshotNtv()
    ScreenshotNtvApi.setUp(binding.getBinaryMessenger(), screenshotApi)
    api = screenshotApi
    screenshotNtvFlutterListener = ScreenshotNtvFlutterListener(binding.getBinaryMessenger())
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    api?.dispose()
    api = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
  }

  override fun onDetachedFromActivityForConfigChanges() {

  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onDetachedFromActivity() {
  }

  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    currentActivity = activity
  }

  override fun onActivityStarted(activity: Activity) {
    currentActivity = activity
  }

  override fun onActivityResumed(activity: Activity) {
    currentActivity = activity
  }

  override fun onActivityPaused(activity: Activity) {
    currentActivity = null
  }

  override fun onActivityStopped(activity: Activity) {
    currentActivity = null
  }

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
  }

  override fun onActivityDestroyed(activity: Activity) {
    currentActivity = null
  }
}

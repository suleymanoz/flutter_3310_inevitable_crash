package dev.overtech.inevitable_crash

import kotlin.concurrent.*

import android.util.Log
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "crash_repro"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
            call, result ->
            when (call.method) {
                "errorResult" -> result.error("ERROR", "This will be a platform error.", null)
                "throwException" -> throw Exception("This will be a platform exception.")
                "syncDoubleResult" -> {
                    result.success("Sync-Double Result-1")
                    Log.d("syncDoubleResult", "Result 1")
                    result.success("Sync-Double Result-2")
                    Log.d("syncDoubleResult", "Result 2")
                }
                "syncDelayedDoubleResult" -> {
                    Log.d("syncDelayedDoubleResult", "Double sync result")
                    result.success("Sync-Delayed-Double Result-1")
                    Log.d("syncDelayedDoubleResult", "Result 1")
                    Thread.sleep(3000) // delay execution after a while
                    Log.d("syncDelayedDoubleResult", "Result 2-before")
                    result.success("Sync-Delayed-Double Result-2")
                    Log.d("syncDelayedDoubleResult", "Result 2-after")
                }
                "asyncDoubleResult" -> {
                    Log.d("asyncDoubleResult", "Test crash")
                    result.success("Async-Double-Result-1")

                    thread {
                        Log.d("asyncDoubleResult", "Async before delay")
                        Thread.sleep(3000) // delay execution after a while
                        Log.d("asyncDoubleResult", "Async after delay")
                        result.success("Async-Double-Result-2")
                    }
                }
                "avoidCrash" -> {
                    Log.d("avoidCrash", "Avoid crash")
                    Log.d("avoidCrash", "Send first result")
                    result.success("Avoid crash-1")
                    if (Looper.getMainLooper().isCurrentThread()) {
                        Log.d("Avoid crash: out thread", "main thread")
                    }else {
                        Log.d("Avoid crash: out thread", "not main thread")
                    }
   
                    val replyThread = object : Thread() {
                        override fun run() {
                            Log.d("avoidCrash", "Async before delay")
                            Thread.sleep(1000) // delay execution after a while
                            Log.d("avoidCrash", "Async after delay")
                            if (Looper.getMainLooper().isCurrentThread()) {
                                Log.d("Avoid crash: in thread", "main thread")
                            }else {
                                Log.d("Avoid crash: in thread", "not main thread")
                            }

                            Handler(Looper.getMainLooper()).post(Runnable {
                                if (Looper.getMainLooper().isCurrentThread()) {
                                    Log.d("Avoid crash: in thread switch uiThread", "main thread")
                                }else {
                                    Log.d("Avoid crash: in thread switch uiThread", "not main thread")
                                }

                                Log.d("avoidCrash", "Send second result")
                                result.success("Avoid crash-2")
                            })
                        }
                    }
                    replyThread.start()
                    replyThread.join()
                }
                else -> {
                    Log.d("MainActivity", "Unknown method: ${call.method}")
                    result.notImplemented()
                }
            }
        }
    }
}

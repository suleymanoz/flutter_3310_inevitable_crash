package dev.overtech.inevitable_crash

import kotlinx.coroutines.*

import android.util.Log
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
                "testCrash" -> {
                    Log.d("MainActivity", "Test crash")
                    GlobalScope.launch {
                        Log.d("MainActivity", "Async before delay")
                        delay(3000) // delay execution after a while
                        Log.d("MainActivity", "Async after delay")
                        result.success("Test crash-2")
                    }

                    result.success("Test crash-1")
                }
                else -> {
                    Log.d("MainActivity", "Unknown method: ${call.method}")
                    result.notImplemented()
                }
            }
        }
    }
}

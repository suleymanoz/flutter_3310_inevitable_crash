import 'dart:ui';

import 'package:flutter/material.dart';

import 'crash_app.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  FlutterError.onError = (details) {
    debugPrint('Flutter error caught: ${details.exception}');
    return;
  };
  PlatformDispatcher.instance.onError = (error, stack) {
    debugPrint('Platform error caught: $error, $stack');
    return true;
  };

  runApp(const MyApp());
}

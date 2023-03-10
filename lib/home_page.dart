import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key, required this.title});

  final String title;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  static const platform = MethodChannel('crash_repro');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ElevatedButton(
              onPressed: () async {
                await platform.invokeMethod('errorResult');
              },
              child: const Text('Raise Error'),
            ),
            ElevatedButton(
              onPressed: () async {
                await platform.invokeMethod('throwException');
              },
              child: const Text('Throw Exception'),
            ),
            ElevatedButton(
              onPressed: () async {
                try {
                  await platform.invokeMethod('syncDoubleResult');
                } on PlatformException catch (e) {
                  debugPrint('syncDoubleResult: Platform error caught: $e');
                }
              },
              child: const Text('Sync Double Result'),
            ),
            ElevatedButton(
              onPressed: () async {
                try {
                  await platform.invokeMethod('syncDelayedDoubleResult');
                } on PlatformException catch (e) {
                  debugPrint('syncDelayedDoubleResult: Platform error caught: $e');
                }
              },
              child: const Text('Sync Delayed Double Result'),
            ),
            ElevatedButton(
              onPressed: () async {
                try {
                  await platform.invokeMethod('asyncDoubleResult');
                } on PlatformException catch (e) {
                  debugPrint('asyncDoubleResult: Platform error caught: $e');
                }
              },
              child: const Text('Async Double Result'),
            ),
            ElevatedButton(
              onPressed: () async {
                try {
                  await platform.invokeMethod('avoidCrash');
                } on PlatformException catch (e) {
                  debugPrint('avoidCrash: Platform error caught: $e');
                }
              },
              child: const Text('Avoid Crash'),
            ),
          ],
        ),
      ),
    );
  }
}

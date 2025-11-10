// ignore_for_file: public_member_api_docs, sort_constructors_first
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_inappwebview/flutter_inappwebview.dart';

import 'package:screenshot_ntv/screenshot_ntv.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(title: 'Screenshot_ntv Demo', home: Home());
  }
}

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<StatefulWidget> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  final images = <Uint8List>[];

  final GlobalKey webViewKey = GlobalKey();

  InAppWebViewController? webViewController;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Plugin example app')),
      floatingActionButton: FloatingActionButton(
        onPressed: () async {
          final result = await ScreenshotNtv.takeScreenshot();
          if (result != null) {
            images.add(result);
            setState(() {});
          }
        },
        child: const Icon(Icons.screenshot),
      ),
      body: images.isEmpty
          ? const Center(child: Text("not screenshot"))
          : SingleChildScrollView(
              padding: const EdgeInsets.all(10),
              child: Column(
                children: [
                  SizedBox(
                    height: 300,
                    width: double.infinity,
                    child: InAppWebView(
                      key: webViewKey,
                      initialUrlRequest: URLRequest(
                        url: WebUri('https://www.ya.ru'),
                      ),
                      onWebViewCreated: (controller) async {
                        webViewController = controller;
                      },
                      initialSettings: InAppWebViewSettings(
                        mediaPlaybackRequiresUserGesture: true,
                        allowsInlineMediaPlayback: true,
                        iframeAllow: "camera; microphone",
                        iframeAllowFullscreen: true,
                        useOnLoadResource: true,
                        javaScriptEnabled: true,
                        allowContentAccess: true,
                        allowFileAccess: true,
                        allowsBackForwardNavigationGestures: false,
                      ),
                    ),
                  ),
                  Center(
                    child: Wrap(
                      spacing: 10,
                      runSpacing: 10,
                      children: List.generate(
                        images.length,
                        (index) => GestureDetector(
                          onTap: () {
                            showDialog(
                              context: context,
                              builder: (_) =>
                                  ScreenshotView(data: images[index]),
                            );
                          },
                          child: Container(
                            decoration: BoxDecoration(
                              boxShadow: [
                                BoxShadow(
                                  blurRadius: 15,
                                  color: Colors.black.withOpacity(0.15),
                                ),
                              ],
                            ),
                            child: Image.memory(images[index], height: 250),
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
    );
  }
}

class ScreenshotView extends StatelessWidget {
  const ScreenshotView({super.key, required this.data});

  final Uint8List data;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        Navigator.pop(context);
      },
      child: Container(
        margin: const EdgeInsets.all(20),
        child: Image.memory(data),
      ),
    );
  }
}

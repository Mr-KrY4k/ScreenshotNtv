# screenshot_ntv

A Flutter plugin to take(support Platform Views) for Android and iOS with native code.

It's a workaround for the issue [RepaintBoundary can't take screenshot of Platform Views](https://github.com/flutter/flutter/issues/102866) .

## Usage

``` yaml
dependencies:
  screenshot_ntv: ^1.0.6
```

## Take Screenshot

``` dart
Uint8List? data = await ScreenshotNtv.takeScreenshot();
```
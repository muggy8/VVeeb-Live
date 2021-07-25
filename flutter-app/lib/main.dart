import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:camera/camera.dart';
import 'package:google_ml_vision/google_ml_vision.dart';
import 'package:flutter/foundation.dart';
import 'dart:async';

void main() {
  runApp(App());
}

class App extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.teal,
      ),
      home: HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {

  late CameraController _cameraController;
  CameraLensDirection _cameraDirection = CameraLensDirection.front;
  ResolutionPreset _captureResolution = ResolutionPreset.high;
  FaceDetector _faceDetector  = GoogleVision.instance.faceDetector(FaceDetectorOptions(
    minFaceSize: 0.5,
    enableLandmarks: true,
    mode: FaceDetectorMode.fast,
    enableClassification: true,
    enableContours: false,
    enableTracking: true,
  ));

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    // setup the camera system
    _setupCamera();
  }

  Future<void> _setupCamera() async {
    final CameraDescription description = await _getCamera(_cameraDirection);
    _cameraController = CameraController(
      description,
      _captureResolution,
      enableAudio: false,
    );

    await _cameraController.initialize();

    _cameraController.startImageStream((CameraImage image) => {
      _faceDetector.processImage(GoogleVisionImage.fromBytes(
        _concatenateImagePlanes(image.planes),
        _buildMetaData(image, _rotationIntToImageRotation(description.sensorOrientation)),
      )).then((value) => print(value))
    });


  }

  // support functions
  Future<CameraDescription> _getCamera(CameraLensDirection dir) async {
    return availableCameras().then(
      (List<CameraDescription> cameras) => cameras.firstWhere(
        (CameraDescription camera) => camera.lensDirection == dir,
      ),
    );
  }

  GoogleVisionImageMetadata _buildMetaData(
    CameraImage image,
    ImageRotation rotation,
  ) {
    return GoogleVisionImageMetadata(
      rawFormat: image.format.raw,
      size: Size(image.width.toDouble(), image.height.toDouble()),
      rotation: rotation,
      planeData: image.planes.map(
            (Plane plane) {
          return GoogleVisionImagePlaneMetadata(
            bytesPerRow: plane.bytesPerRow,
            height: plane.height,
            width: plane.width,
          );
        },
      ).toList(),
    );
  }

  Uint8List _concatenateImagePlanes(List<Plane> planes) {
    final WriteBuffer allBytes = WriteBuffer();
    planes.forEach((Plane plane) => allBytes.putUint8List(plane.bytes));
    return allBytes.done().buffer.asUint8List();
  }

  ImageRotation _rotationIntToImageRotation(int rotation) {
    switch (rotation) {
      case 0:
        return ImageRotation.rotation0;
      case 90:
        return ImageRotation.rotation90;
      case 180:
        return ImageRotation.rotation180;
      default:
        assert(rotation == 270);
        return ImageRotation.rotation270;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Container(),
      ),
    );
  }
}

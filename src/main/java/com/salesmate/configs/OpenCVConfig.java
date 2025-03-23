package com.salesmate.configs;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

public class OpenCVConfig {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final String FACE_CASCADE_PATH = "path/to/haarcascade_frontalface_default.xml";

    public static CascadeClassifier getFaceCascade() {
        return new CascadeClassifier(FACE_CASCADE_PATH);
    }

    public static Rect[] detectFaces(Mat image) {
        CascadeClassifier faceCascade = getFaceCascade();
        MatOfRect faceDetections = new MatOfRect();
        faceCascade.detectMultiScale(image, faceDetections);
        return faceDetections.toArray();
    }

    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath);
    }
}

package com.salesmate.configs;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamLockException;

public class WebcamConfig {

    public static Webcam configureWebcam() {
        Webcam webcam = Webcam.getDefault();
        try {
            if (webcam != null && !webcam.isOpen()) {
                webcam.open();
            }
        } catch (WebcamLockException e) {
            // Xử lý lỗi nếu webcam bị khóa
            System.err.println("Webcam is already locked. Please close any other application using the webcam.");
            e.printStackTrace();
        }
        return webcam;
    }
}

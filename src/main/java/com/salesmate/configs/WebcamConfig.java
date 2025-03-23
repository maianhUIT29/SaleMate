package com.salesmate.configs;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;

public class WebcamConfig {

    public static Webcam configureWebcam() {
        Webcam webcam = Webcam.getDefault();
        if (webcam != null) {
            if (webcam.isOpen()) {
                webcam.close(); // Close the webcam if it is open
            }
            webcam.setViewSize(WebcamResolution.VGA.getSize());
        }
        return webcam;
    }
}

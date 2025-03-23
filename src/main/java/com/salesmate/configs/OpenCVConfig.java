package com.salesmate.configs;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

public class OpenCVConfig {

    // Tải thư viện OpenCV khi ứng dụng khởi động
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Đảm bảo tên thư viện đúng
    }

    // Đường dẫn đến mô hình Haar Cascade của OpenCV dùng để phát hiện khuôn mặt
    private static final String FACE_CASCADE_PATH = "src/main/resources/haarcascade_frontalface_default.xml"; // Cập nhật đường dẫn

    // Hàm trả về đối tượng CascadeClassifier đã được cấu hình với mô hình Haar Cascade
    public static CascadeClassifier getFaceCascade() {
        return new CascadeClassifier(FACE_CASCADE_PATH);
    }

    // Hàm phát hiện khuôn mặt trong ảnh
    public static Rect[] detectFaces(Mat image) {
        CascadeClassifier faceCascade = getFaceCascade(); // Lấy đối tượng CascadeClassifier
        MatOfRect faceDetections = new MatOfRect(); // Tạo đối tượng MatOfRect để chứa các khuôn mặt phát hiện được
        faceCascade.detectMultiScale(image, faceDetections); // Phát hiện khuôn mặt trong ảnh
        return faceDetections.toArray(); // Trả về các khuôn mặt dưới dạng mảng Rect
    }

    // Hàm để tải ảnh từ đường dẫn và chuyển thành đối tượng Mat của OpenCV
    public static Mat loadImage(String imagePath) {
        return Imgcodecs.imread(imagePath); // Dùng Imgcodecs của OpenCV để đọc ảnh
    }
}

package com.salesmate.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.SIFT;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.salesmate.controller.UserController;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;
import com.salesmate.utils.UIHelper;

public class LoginFaceID extends JFrame {

    private UserController userController;
    private Webcam webcam;
    private WebcamPanel webcamPanel;
    private ScheduledExecutorService timer;
    private CascadeClassifier faceCascade;
    private JLabel statusLabel;
    private boolean processingImage = false;
    private int noFaceCount = 0;
    private static final int MAX_NO_FACE_COUNT = 10; // Number of frames without a face before showing a message
    private static final double MATCH_THRESHOLD = 30.0; // Threshold for face matching (lower is stricter)
    
    // Paths for OpenCV native libraries
    private static final String[] OPENCV_NATIVE_LIBRARY_PATHS = {
        "opencv_java451",                                  // Default library name
        "lib/opencv_java451",                              // Relative to working directory
        "native/opencv_java451",                           // Another common location
        "C:/OpenCV/build/java/x64/opencv_java451",         // Typical Windows install path
        System.getProperty("user.home") + "/opencv/build/java/x64/opencv_java451" // User home directory
    };
    
    // For cascade classifier
    private static final String[] CASCADE_FILE_PATHS = {
        "haarcascades/haarcascade_frontalface_alt.xml",           // Standard location
        "src/main/resources/haarcascades/haarcascade_frontalface_alt.xml", // Maven standard
        "resources/haarcascades/haarcascade_frontalface_alt.xml"   // Alternative location
    };

    static {
        loadOpenCVLibrary();
    }
    
    /**
     * Attempts to load OpenCV native library from various possible locations
     */
    private static void loadOpenCVLibrary() {
        boolean loaded = false;
        
        // First try: Load using System.loadLibrary
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("OpenCV loaded successfully using System.loadLibrary");
            loaded = true;
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Failed to load OpenCV using System.loadLibrary: " + e.getMessage());
        }
        
        // Second try: Load using explicit paths if first attempt fails
        if (!loaded) {
            for (String libPath : OPENCV_NATIVE_LIBRARY_PATHS) {
                try {
                    System.load(libPath);
                    System.out.println("OpenCV loaded successfully from " + libPath);
                    loaded = true;
                    break;
                } catch (UnsatisfiedLinkError e) {
                    System.out.println("Failed to load from " + libPath);
                }
            }
        }
        
        // Third try: Extract from JAR if included
        if (!loaded) {
            try {
                // Create a temporary directory to extract the library
                Path tempDir = Files.createTempDirectory("opencv_natives");
                tempDir.toFile().deleteOnExit();
                
                // Determine platform-specific library name
                String osName = System.getProperty("os.name").toLowerCase();
                String libName = osName.contains("win") ? "opencv_java451.dll" : 
                               osName.contains("mac") ? "libopencv_java451.dylib" : 
                               "libopencv_java451.so";
                
                // Path in JAR
                String resourcePath = "/opencv-natives/" + libName;
                
                // Extract and load
                try (InputStream in = LoginFaceID.class.getResourceAsStream(resourcePath)) {
                    if (in != null) {
                        Path extractedLib = tempDir.resolve(libName);
                        Files.copy(in, extractedLib, StandardCopyOption.REPLACE_EXISTING);
                        System.load(extractedLib.toString());
                        System.out.println("OpenCV loaded from extracted resource: " + extractedLib);
                        loaded = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (!loaded) {
            System.err.println("ERROR: Could not load OpenCV native library. Face ID login will not work.");
            System.err.println("Please ensure OpenCV is installed and its native libraries are in the Java library path.");
            System.err.println("Current java.library.path: " + System.getProperty("java.library.path"));
        }
    }
    
    /**
     * Returns a valid path to the face cascade classifier file
     */
    private String findCascadeFile() {
        // Try to find the cascade classifier file in various locations
        for (String cascadePath : CASCADE_FILE_PATHS) {
            // Check if file exists as a resource
            try (InputStream is = getClass().getClassLoader().getResourceAsStream(cascadePath)) {
                if (is != null) {
                    // Extract to temporary file
                    File tempFile = File.createTempFile("cascade", ".xml");
                    tempFile.deleteOnExit();
                    Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    return tempFile.getAbsolutePath();
                }
            } catch (IOException e) {
                // Continue to next location
            }
            
            // Check if file exists on filesystem
            File file = new File(cascadePath);
            if (file.exists()) {
                return file.getAbsolutePath();
            }
        }
        
        // If not found, try to extract from JAR
        try (InputStream is = getClass().getResourceAsStream("/haarcascade_frontalface_alt.xml")) {
            if (is != null) {
                File tempFile = File.createTempFile("cascade", ".xml");
                tempFile.deleteOnExit();
                Files.copy(is, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return tempFile.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public LoginFaceID() {
        // Use our custom look and feel helper
        UIHelper.setupLookAndFeel();

        userController = new UserController();
        setTitle("🔑 Đăng nhập bằng FaceID - SalesMate");
        setSize(800, 600); // Tăng kích thước cửa sổ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Load face cascade classifier
        String cascadePath = findCascadeFile();
        if (cascadePath == null) {
            JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy file cascade classifier. FaceID sẽ không hoạt động.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginForm().setVisible(true);
            return;
        }
        
        faceCascade = new CascadeClassifier(cascadePath);
        
        if (faceCascade.empty()) {
            JOptionPane.showMessageDialog(this, 
                    "Failed to load face cascade classifier", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            new LoginForm().setVisible(true);
            return;
        }

        initComponents();

        // Apply no-focus styling to all components
        UIHelper.removeFocusFromAll(this);

        startFaceDetection();
    }
    
    private void initComponents() {
        webcam = Webcam.getDefault();
        if (webcam == null) {
            JOptionPane.showMessageDialog(this, 
                    "No webcam detected. Please connect a webcam and restart.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        // Panel chính với gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                Color color1 = new Color(224, 255, 255);
                Color color2 = new Color(173, 216, 230);
                GradientPaint gradient = new GradientPaint(0, 0, color1, 0, height, color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, width, height);
            }
        };
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Title với icon
        JLabel titleLabel = new JLabel("ĐĂNG NHẬP BẰNG FACE ID");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Vui lòng nhìn thẳng vào camera để nhận diện");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Status label
        statusLabel = new JLabel("Đang dò tìm khuôn mặt...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(0, 123, 255));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.add(subtitleLabel, BorderLayout.CENTER);
        labelPanel.add(statusLabel, BorderLayout.SOUTH);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(labelPanel, BorderLayout.CENTER);

        // Camera panel với viền và padding
        JPanel cameraContainer = new JPanel(new BorderLayout());
        cameraContainer.setOpaque(false);
        cameraContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        webcamPanel = new WebcamPanel(webcam);
        webcamPanel.setFPSDisplayed(true);
        webcamPanel.setDisplayDebugInfo(true);
        webcamPanel.setMirrored(true);
        webcamPanel.setFitArea(true);
        cameraContainer.add(webcamPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        // Back button
        JButton backButton = createStyledButton("Quay lại đăng nhập thường", new Color(108, 117, 125));
        backButton.addActionListener(e -> {
            stopFaceDetection();
            webcam.close();
            dispose();
            new LoginForm().setVisible(true);
        });

        buttonPanel.add(backButton);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(cameraContainer, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void startFaceDetection() {
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(this::detectAndRecognizeFace, 1000, 500, TimeUnit.MILLISECONDS);
    }
    
    private void stopFaceDetection() {
        if (timer != null && !timer.isShutdown()) {
            timer.shutdownNow();
            timer = null;
        }
    }

    private void detectAndRecognizeFace() {
        if (processingImage || webcam == null || !webcam.isOpen()) {
            return;
        }
        
        processingImage = true;
        
        try {
            // Capture image from webcam
            BufferedImage image = webcam.getImage();
            if (image == null) {
                processingImage = false;
                return;
            }
            
            // Convert BufferedImage to Mat
            File tempFile = File.createTempFile("webcam", ".jpg");
            ImageIO.write(image, "jpg", tempFile);
            Mat frame = Imgcodecs.imread(tempFile.getAbsolutePath());
            tempFile.delete();
            
            if (frame.empty()) {
                processingImage = false;
                return;
            }
            
            // Detect faces
            MatOfRect faces = new MatOfRect();
            faceCascade.detectMultiScale(
                frame, 
                faces, 
                1.1, 
                3, 
                0 | Objdetect.CASCADE_SCALE_IMAGE, 
                new org.opencv.core.Size(60, 60), 
                new org.opencv.core.Size(400, 400)
            );
            
            Rect[] facesArray = faces.toArray();
            
            if (facesArray.length > 0) {
                // Reset counter if face is detected
                noFaceCount = 0;
                
                // Draw rectangle around the face
                Rect faceRect = facesArray[0];
                Imgproc.rectangle(
                    frame, 
                    new Point(faceRect.x, faceRect.y), 
                    new Point(faceRect.x + faceRect.width, faceRect.y + faceRect.height), 
                    new Scalar(0, 255, 0), 
                    2
                );
                
                // Extract face region
                Mat faceROI = new Mat(frame, faceRect);
                
                // Save face image to temp file
                File faceFile = File.createTempFile("face", ".jpg");
                Imgcodecs.imwrite(faceFile.getAbsolutePath(), faceROI);
                
                // Compare with stored face images
                String userId = recognizeFace(faceFile);
                faceFile.delete();
                
                if (userId != null) {
                    // Successfully recognized face
                    User user = userController.getUserById(Integer.parseInt(userId));
                    if (user != null) {
                        SwingUtilities.invokeLater(() -> {
                            stopFaceDetection();
                            SessionManager.getInstance().setLoggedInUser(user);
                            showSuccessDialog(user);
                        });
                    }
                } else {
                    // Face detected but not recognized
                    updateStatus("Khuôn mặt chưa được nhận diện, vui lòng thử lại...");
                }
            } else {
                // No face detected
                noFaceCount++;
                if (noFaceCount >= MAX_NO_FACE_COUNT) {
                    updateStatus("Không tìm thấy khuôn mặt, vui lòng đảm bảo khuôn mặt của bạn hiển thị rõ");
                } else {
                    updateStatus("Đang dò tìm khuôn mặt...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Lỗi xử lý hình ảnh");
        } finally {
            processingImage = false;
        }
    }

    private String recognizeFace(File capturedFace) {
        try {
            // Load the captured face
            Mat queryFace = Imgcodecs.imread(capturedFace.getAbsolutePath());
            
            // Find all face files in the faceid directory
            Path faceidDir = getFaceIdDirectory();
            if (!Files.exists(faceidDir)) {
                updateStatus("Không tìm thấy thư mục FaceID");
                return null;
            }
            
            List<Path> faceFiles = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(faceidDir, 1)) {
                faceFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> {
                        String filename = p.getFileName().toString().toLowerCase();
                        return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png");
                    })
                    .collect(Collectors.toList());
            }
            
            if (faceFiles.isEmpty()) {
                updateStatus("Không có dữ liệu khuôn mặt nào trong thư mục");
                return null;
            }
            
            // Initialize feature detector (SIFT)
            SIFT sift = SIFT.create();
            MatOfKeyPoint queryKeypoints = new MatOfKeyPoint();
            Mat queryDescriptors = new Mat();
            
            // Detect keypoints and compute descriptors for query image
            sift.detectAndCompute(queryFace, new Mat(), queryKeypoints, queryDescriptors);
            
            // Check if there are enough keypoints
            if (queryKeypoints.rows() < 10) {
                updateStatus("Không đủ đặc trưng khuôn mặt để xác thực");
                return null;
            }
            
            // Initialize feature matcher
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
            
            String bestMatchId = null;
            double bestMatchScore = Double.MAX_VALUE;
            
            // Compare with each stored face
            for (Path faceFile : faceFiles) {
                String filename = faceFile.getFileName().toString();
                String userId = filename.substring(0, filename.lastIndexOf('.'));
                
                Mat trainFace = Imgcodecs.imread(faceFile.toString());
                MatOfKeyPoint trainKeypoints = new MatOfKeyPoint();
                Mat trainDescriptors = new Mat();
                
                // Detect keypoints and compute descriptors for training image
                sift.detectAndCompute(trainFace, new Mat(), trainKeypoints, trainDescriptors);
                
                // Skip if not enough keypoints
                if (trainKeypoints.rows() < 10 || trainDescriptors.empty()) {
                    continue;
                }
                
                // Match descriptors
                MatOfDMatch matches = new MatOfDMatch();
                matcher.match(queryDescriptors, trainDescriptors, matches);
                
                // Calculate match score
                double matchDistance = calculateMatchDistance(matches);
                
                // Update if this is the best match so far
                if (matchDistance < bestMatchScore && matchDistance < MATCH_THRESHOLD) {
                    bestMatchScore = matchDistance;
                    bestMatchId = userId;
                }
            }
            
            if (bestMatchId != null) {
                updateStatus("Nhận diện thành công! Đang đăng nhập...");
                return bestMatchId;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("Lỗi khi xác thực khuôn mặt");
            return null;
        }
    }
    
    private double calculateMatchDistance(MatOfDMatch matches) {
        // Calculate average distance of top matches
        double totalDistance = 0;
        int numMatches = (int) Math.min(matches.total(), 25); // Use top matches
        
        if (numMatches == 0) {
            return Double.MAX_VALUE;
        }
        
        // Sort matches by distance
        java.util.List<org.opencv.core.DMatch> matchesList = matches.toList();
        matchesList.sort((a, b) -> Float.compare(a.distance, b.distance));
        
        // Calculate average distance of top matches
        for (int i = 0; i < numMatches; i++) {
            totalDistance += matchesList.get(i).distance;
        }
        
        return totalDistance / numMatches;
    }
    
    private Path getFaceIdDirectory() {
        // Try to find the directory in different locations
        Path[] possiblePaths = {
            Paths.get("src/main/resources/img/faceid"),
            Paths.get("resources/img/faceid"),
            Paths.get("img/faceid"),
            Paths.get(System.getProperty("user.dir"), "src/main/resources/img/faceid")
        };
        
        for (Path path : possiblePaths) {
            if (Files.exists(path)) {
                return path;
            }
        }
        
        // If not found, create the directory
        try {
            Path defaultPath = Paths.get("src/main/resources/img/faceid");
            Files.createDirectories(defaultPath);
            return defaultPath;
        } catch (IOException e) {
            e.printStackTrace();
            return Paths.get("img/faceid"); // Return a default path
        }
    }

    private void updateStatus(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void showSuccessDialog(User user) {
        JOptionPane.showOptionDialog(this,
            "Xin chào " + user.getUsername() + "!\nĐăng nhập thành công!",
            "Thành công",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            new Object[]{"OK"},
            "OK");
        dispose();
        webcam.close();
        new CashierView().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIHelper.setupLookAndFeel();
            new LoginFaceID();
        });
    }
}

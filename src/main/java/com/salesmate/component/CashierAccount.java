package com.salesmate.component;

import com.salesmate.controller.UserController;
import com.salesmate.model.User;
import com.salesmate.utils.SessionManager;

import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.BasicStroke;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.beans.Beans;

import javax.imageio.ImageIO;
import javax.swing.Timer;

public class CashierAccount extends javax.swing.JPanel {

    private final UserController userController = new UserController();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String selectedAvatarPath = null;
    private int avatarSize = 200; // Giảm xuống 200x200
    private JDialog imageEditorDialog;
    private ImageIcon editingImage;
    private double zoomFactor = 1.0;
    private int rotationAngle = 0;
    private Point dragStart;
    private Point imagePosition = new Point(0, 0);
    private Point lastMousePoint;

    private String originalUsername;
    private String originalCCCD;
    private JDialog toastDialog;

    public CashierAccount() {
        initComponents();
        if (!Beans.isDesignTime()) {
            setupComponents();
            loadUserData(); // Load dữ liệu user khi khởi tạo
        }
    }

    private void setupComponents() {
        // Style cho buttons với kích thước cụ thể và màu mới
        styleButton(btnUpdate, new Color(255, 193, 7));  // Warning yellow
        styleButton(btnSave, new Color(40, 167, 69));    // Success green 
        styleButton(btnResetPW, new Color(0, 150, 136)); // Teal color
        styleButton(btnUpdateAvatar, new Color(0, 123, 255)); // Primary blue

        // Ban đầu disable các field và nút Save
        setFieldsEditable(false);
        btnSave.setEnabled(false);

        // Fix hiển thị avatar
        lblAvatar.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                loadAvatar();
            }
        });

        // Thêm action listeners với chức năng cancel
        btnUpdate.addActionListener(e -> {
            if (btnUpdate.getText().equals("Cập nhật")) {
                handleUpdate();
                btnUpdate.setText("Huỷ");
                btnUpdate.setBackground(new Color(220, 53, 69)); // Màu đỏ cho nút huỷ
            } else {
                handleCancel();
                btnUpdate.setText("Cập nhật"); 
                btnUpdate.setBackground(new Color(255, 193, 7)); // Màu vàng cho nút cập nhật
            }
        });

        btnSave.addActionListener(e -> handleSave());
        btnResetPW.addActionListener(e -> showChangePasswordDialog());

        // Xử lý sự kiện chọn ảnh - chỉ để một handler
        btnUpdateAvatar.addActionListener(e -> showImagePicker());
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));
        field.setBackground(Color.WHITE);
    }

    private void handleUpdate() {
        setFieldsEditable(true);
        btnSave.setEnabled(true);
        // Lưu lại giá trị ban đầu để có thể khôi phục
        originalUsername = txtUsername.getText();
        originalCCCD = txtCCCD.getText();
    }

    private void handleCancel() {
        // Khôi phục các giá trị ban đầu
        txtUsername.setText(originalUsername);
        txtCCCD.setText(originalCCCD);
        
        setFieldsEditable(false);
        btnSave.setEnabled(false);
    }

    private void handleSave() {
        User user = SessionManager.getInstance().getLoggedInUser();
        if (user != null) {
            // Update only allowed fields
            user.setUsername(txtUsername.getText());
            user.setStatus(txtCCCD.getText());

            if (userController.updateUser(user)) {
                showToast("Cập nhật thông tin thành công!", new Color(40, 167, 69));
                setFieldsEditable(false);
                btnSave.setEnabled(false);
                btnUpdate.setText("Cập nhật");
                btnUpdate.setBackground(new Color(255, 193, 7));
                loadUserData(); // Reload data
            } else {
                showToast("Cập nhật thất bại!", new Color(220, 53, 69));
            }
        }
    }

    private void loadAvatar() {
        if (lblAvatar.getWidth() <= 0 || lblAvatar.getHeight() <= 0) return;

        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null && loggedInUser.getAvatar() != null) {
            try {
                String avatarPath = "/img/avt/" + loggedInUser.getAvatar();
                URL imageUrl = getClass().getResource(avatarPath);
                if (imageUrl != null) {
                    ImageIcon originalIcon = new ImageIcon(imageUrl);
                    Image originalImage = originalIcon.getImage();

                    // Scale ảnh giữ nguyên tỷ lệ và fit vào lblAvatar 
                    int labelWidth = lblAvatar.getWidth();
                    int labelHeight = lblAvatar.getHeight();
                    int imageWidth = originalImage.getWidth(null);
                    int imageHeight = originalImage.getHeight(null);

                    double scale = Math.min(
                        (double) labelWidth / imageWidth,
                        (double) labelHeight / imageHeight
                    );

                    int scaledWidth = (int) (imageWidth * scale);
                    int scaledHeight = (int) (imageHeight * scale);

                    Image scaledImage = originalImage.getScaledInstance(
                        scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                    
                    lblAvatar.setIcon(new ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Load ảnh default nếu không load được avatar
                try {
                    URL defaultImageUrl = getClass().getResource("/img/avt/default-avatar.png");
                    if (defaultImageUrl != null) {
                        lblAvatar.setIcon(new ImageIcon(defaultImageUrl));
                    }
                } catch (Exception ex) {
                    lblAvatar.setIcon(null);
                }
            }
        }
    }

    private void showImagePicker() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Image files", "jpg", "jpeg", "png"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            showImageEditor(file);
        }
    }

    private void showImageEditor(File imageFile) {
        imageEditorDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chỉnh sửa ảnh", JDialog.ModalityType.APPLICATION_MODAL);
        imageEditorDialog.setSize(800, 600);
        imageEditorDialog.setLocationRelativeTo(this);

        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel chứa ảnh với khung crop 250x250
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Vẽ ảnh
                if (editingImage != null) {
                    Image img = editingImage.getImage();
                    int newWidth = (int)(img.getWidth(null) * zoomFactor);
                    int newHeight = (int)(img.getHeight(null) * zoomFactor);
                    
                    // Xoay ảnh nếu cần
                    g2d.translate(getWidth()/2, getHeight()/2);
                    g2d.rotate(Math.toRadians(rotationAngle));
                    g2d.translate(-getWidth()/2, -getHeight()/2);
                    
                    // Vẽ ảnh với vị trí và zoom
                    g2d.drawImage(img, 
                        imagePosition.x, imagePosition.y, 
                        newWidth, newHeight, null);
                }

                // Vẽ khung crop 250x250 ở giữa
                int frameX = (getWidth() - 250) / 2;
                int frameY = (getHeight() - 250) / 2;
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(frameX, frameY, 250, 250);
            }
        };

        // Thêm mouse listeners để kéo ảnh
        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                lastMousePoint = e.getPoint();
                
                // Right click to rotate
                if (e.getButton() == MouseEvent.BUTTON3) {
                    lastMousePoint = e.getPoint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                lastMousePoint = null;
            }
        });

        imagePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                    // Rotate with right click drag
                    if (lastMousePoint != null) {
                        int dx = e.getX() - lastMousePoint.x;
                        rotationAngle += dx * 0.5; // Adjust rotation sensitivity
                        imagePanel.repaint();
                    }
                    lastMousePoint = e.getPoint();
                } else if (dragStart != null) {
                    // Move image with left click drag
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    imagePosition.translate(dx, dy);
                    dragStart = e.getPoint();
                    imagePanel.repaint();
                }
            }
        });

        // Panel controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        // Zoom controls
        JButton zoomInBtn = new JButton("+");
        JButton zoomOutBtn = new JButton("-");
        
        zoomInBtn.addActionListener(e -> {
            zoomFactor *= 1.1;
            imagePanel.repaint();
        });
        
        zoomOutBtn.addActionListener(e -> {
            zoomFactor *= 0.9;
            imagePanel.repaint();
        });

        // Rotation controls
        JButton rotateLeftBtn = new JButton("⟲");
        JButton rotateRightBtn = new JButton("⟳");
        
        rotateLeftBtn.addActionListener(e -> {
            rotationAngle -= 90;
            imagePanel.repaint();
        });
        
        rotateRightBtn.addActionListener(e -> {
            rotationAngle += 90;
            imagePanel.repaint();
        });

        // Buttons
        JButton saveBtn = new JButton("Lưu");
        JButton cancelBtn = new JButton("Huỷ");

        styleButton(saveBtn, new Color(40, 167, 69));
        styleButton(cancelBtn, new Color(220, 53, 69));

        saveBtn.addActionListener(e -> saveEditedImage());
        cancelBtn.addActionListener(e -> imageEditorDialog.dispose());

        // Add controls
        controlPanel.add(zoomInBtn);
        controlPanel.add(zoomOutBtn);
        controlPanel.add(rotateLeftBtn);
        controlPanel.add(rotateRightBtn);
        controlPanel.add(saveBtn);
        controlPanel.add(cancelBtn);

        // Load image
        try {
            editingImage = new ImageIcon(imageFile.getAbsolutePath());
            // Center image initially
            imagePosition.x = (800 - editingImage.getIconWidth()) / 2;
            imagePosition.y = (600 - editingImage.getIconHeight()) / 2;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải ảnh: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainPanel.add(imagePanel, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        imageEditorDialog.add(mainPanel);
        imageEditorDialog.setVisible(true);
    }

    private void saveEditedImage() {
        try {
            // Get current user ID
            User user = SessionManager.getInstance().getLoggedInUser();
            if (user == null) return;
            
            // Create buffered image from editor view
            int width = 250;
            int height = 250;
            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = result.createGraphics();
            
            // Set background
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, width, height);
            
            // Get center point of crop frame
            JPanel imagePanel = (JPanel) imageEditorDialog.getContentPane()
                .getComponent(0);
            int centerX = imagePanel.getWidth() / 2;
            int centerY = imagePanel.getHeight() / 2;
            
            // Setup transformation
            g2d.translate(width/2, height/2);
            g2d.rotate(Math.toRadians(rotationAngle));
            g2d.translate(-width/2, -height/2);
            
            // Calculate image drawing position
            int drawX = width/2 - (centerX - imagePosition.x);
            int drawY = height/2 - (centerY - imagePosition.y);
            
            // Draw scaled and positioned image
            Image img = editingImage.getImage();
            int newWidth = (int)(img.getWidth(null) * zoomFactor);
            int newHeight = (int)(img.getHeight(null) * zoomFactor);
            g2d.drawImage(img, drawX, drawY, newWidth, newHeight, null);
            
            g2d.dispose();

            // Save file with userId as name
            String fileName = user.getUsersId() + ".jpg";
            String resourcePath = "src/main/resources/img/avt/" + fileName;
            File outputFile = new File(resourcePath);
            ImageIO.write(result, "jpg", outputFile);

            // Update database
            user.setAvatar(fileName);
            if (userController.updateUser(user)) {
                showToast("Cập nhật ảnh đại diện thành công!", new Color(40, 167, 69));
                loadAvatar(); // Reload avatar
                imageEditorDialog.dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(imageEditorDialog,
                "Lỗi khi lưu ảnh: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showToast(String message, Color bgColor) {
        if (toastDialog != null && toastDialog.isVisible()) {
            toastDialog.dispose();
        }

        toastDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this));
        toastDialog.setUndecorated(true);
        toastDialog.setBackground(new Color(0, 0, 0, 0));

        // Create panel with rounded corners
        JPanel toastPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        toastPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        toastPanel.setOpaque(false);

        // Create message label
        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        toastPanel.add(messageLabel);

        toastDialog.add(toastPanel);
        toastDialog.pack();

        // Center on parent window
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        int x = parent.getX() + (parent.getWidth() - toastDialog.getWidth()) / 2;
        int y = parent.getY() + parent.getHeight() - toastDialog.getHeight() - 50;
        toastDialog.setLocation(x, y);

        // Show toast and set timer to hide
        toastDialog.setVisible(true);
        new Timer(2000, (e) -> {
            toastDialog.dispose();
        }).start();
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                                   "Đổi mật khẩu", true);
        // Đặt dialog ở giữa frame chính
        dialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add password fields
        JPasswordField oldPwField = new JPasswordField(20);
        JPasswordField newPwField = new JPasswordField(20);
        JPasswordField confirmPwField = new JPasswordField(20);

        // Style fields
        styleTextField(oldPwField);
        styleTextField(newPwField);
        styleTextField(confirmPwField);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Mật khẩu cũ:"), gbc);
        gbc.gridx = 1;
        panel.add(oldPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mật khẩu mới:"), gbc);
        gbc.gridx = 1;
        panel.add(newPwField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Xác nhận mật khẩu:"), gbc);
        gbc.gridx = 1;
        panel.add(confirmPwField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Xác nhận");
        JButton cancelButton = new JButton("Hủy");

        styleButton(okButton, new Color(40, 167, 69));
        styleButton(cancelButton, new Color(108, 117, 125));

        okButton.addActionListener(e -> {
            String oldPw = new String(oldPwField.getPassword());
            String newPw = new String(newPwField.getPassword());
            String confirmPw = new String(confirmPwField.getPassword());

            if (!newPw.equals(confirmPw)) {
                JOptionPane.showMessageDialog(dialog,
                    "Mật khẩu xác nhận không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = SessionManager.getInstance().getLoggedInUser();
            if (userController.resetPassword(user.getEmail(), oldPw, newPw)) {
                JOptionPane.showMessageDialog(dialog,
                    "Đổi mật khẩu thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Mật khẩu cũ không đúng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void setFieldsEditable(boolean editable) {
        txtUsername.setEditable(editable);
        txtCCCD.setEditable(editable);
        // Always keep these fields non-editable
        txtEmail.setEditable(false);
        txtPW.setEditable(false);
        
        // Update background color based on editable state
        Color bgColor = editable ? Color.WHITE : new Color(240, 240, 240);
        txtUsername.setBackground(bgColor);
        txtCCCD.setBackground(bgColor);
        // Keep email and password fields always grey
        txtEmail.setBackground(new Color(240, 240, 240));
        txtPW.setBackground(new Color(240, 240, 240));
    }

    private void loadUserData() {
        User loggedInUser = SessionManager.getInstance().getLoggedInUser();
        if (loggedInUser != null) {
            txtUsername.setText(loggedInUser.getUsername());
            txtEmail.setText(loggedInUser.getEmail());
            txtDOB.setText(loggedInUser.getCreatedAt() != null ? dateFormat.format(loggedInUser.getCreatedAt()) : "");
            txtCCCD.setText(loggedInUser.getStatus());
            lblStatusValue.setText(loggedInUser.getStatus());
            lblCreateAtValue.setText(loggedInUser.getCreatedAt() != null ? dateFormat.format(loggedInUser.getCreatedAt()) : "");
            loadAvatar(); // Ensure the avatar is loaded when loading user data
        } else {
            JOptionPane.showMessageDialog(this, "No user is logged in.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ConfirmUpdateDialog = new javax.swing.JDialog();
        AccountPanel = new javax.swing.JPanel();
        UserInfoPanel = new javax.swing.JPanel();
        lblUsername = new javax.swing.JLabel();
        txtCCCD = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        lblPW = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        HeaderSPR = new javax.swing.JSeparator();
        lblUserHeader = new javax.swing.JLabel();
        lblDOB = new javax.swing.JLabel();
        lblCCCD = new javax.swing.JLabel();
        txtDOB = new javax.swing.JTextField();
        txtPW = new javax.swing.JPasswordField();
        InfoSPR = new javax.swing.JSeparator();
        btnResetPW = new javax.swing.JButton();
        UserAvatarPanel = new javax.swing.JPanel();
        ActionPanel = new javax.swing.JPanel();
        btnUpdateAvatar = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        lblCreateAt = new javax.swing.JLabel();
        AvatarSpt = new javax.swing.JSeparator();
        lblCreateAtValue = new javax.swing.JLabel();
        lblStatusValue = new javax.swing.JLabel();
        lblAvatar = new javax.swing.JLabel();

        javax.swing.GroupLayout ConfirmUpdateDialogLayout = new javax.swing.GroupLayout(ConfirmUpdateDialog.getContentPane());
        ConfirmUpdateDialog.getContentPane().setLayout(ConfirmUpdateDialogLayout);
        ConfirmUpdateDialogLayout.setHorizontalGroup(
            ConfirmUpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        ConfirmUpdateDialogLayout.setVerticalGroup(
            ConfirmUpdateDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setLayout(new java.awt.BorderLayout());

        AccountPanel.setBackground(new java.awt.Color(220, 240, 242));

        UserInfoPanel.setBackground(new java.awt.Color(255, 255, 255));
        UserInfoPanel.setBorder(javax.swing.BorderFactory.createLineBorder(null));

        lblUsername.setBackground(new java.awt.Color(241, 241, 241));
        lblUsername.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblUsername.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUsername.setText("Tên người dùng");

        txtCCCD.setBackground(new java.awt.Color(204, 204, 204));
        txtCCCD.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtCCCD.setText("0123456789");
        txtCCCD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCCCDActionPerformed(evt);
            }
        });

        lblEmail.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblEmail.setText("Email");

        txtUsername.setBackground(new java.awt.Color(204, 204, 204));
        txtUsername.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtUsername.setText("Unknown");
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });

        txtEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtEmail.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        txtEmail.setText("Forexample@gmail.com");

        lblPW.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblPW.setText("Mật khẩu");

        btnUpdate.setBackground(new java.awt.Color(255, 255, 51));
        btnUpdate.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnUpdate.setText("Cập nhật");

        btnSave.setBackground(new java.awt.Color(0, 204, 0));
        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSave.setForeground(new java.awt.Color(255, 255, 255));
        btnSave.setText("Lưu");

        lblUserHeader.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblUserHeader.setForeground(new java.awt.Color(0, 0, 255));
        lblUserHeader.setText("THÔNG TIN TÀI KHOẢN");

        lblDOB.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblDOB.setText("Ngày sinh");

        lblCCCD.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        lblCCCD.setText("CCCD");

        txtDOB.setBackground(new java.awt.Color(204, 204, 204));
        txtDOB.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtDOB.setText("01/01/2003");
        txtDOB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDOBActionPerformed(evt);
            }
        });

        txtPW.setBackground(new java.awt.Color(204, 204, 204));
        txtPW.setText("jPasswordField1");

        btnResetPW.setBackground(new java.awt.Color(255, 51, 51));
        btnResetPW.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetPW.setForeground(new java.awt.Color(255, 255, 255));
        btnResetPW.setText("Đổi mật khẩu");

        javax.swing.GroupLayout UserInfoPanelLayout = new javax.swing.GroupLayout(UserInfoPanel);
        UserInfoPanel.setLayout(UserInfoPanelLayout);
        UserInfoPanelLayout.setHorizontalGroup(
            UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserInfoPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(HeaderSPR)
                    .addComponent(InfoSPR, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCCCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblPW, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDOB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCCCD)
                            .addComponent(txtEmail)
                            .addComponent(txtPW)
                            .addComponent(txtDOB)
                            .addComponent(txtUsername)))
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UserInfoPanelLayout.createSequentialGroup()
                                .addComponent(lblUserHeader)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, UserInfoPanelLayout.createSequentialGroup()
                                .addGap(189, 189, 189)
                                .addComponent(btnResetPW, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );
        UserInfoPanelLayout.setVerticalGroup(
            UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserInfoPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblUserHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(HeaderSPR)
                .addGap(3, 3, 3)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(17, 17, 17))
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblEmail, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtPW, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPW, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(UserInfoPanelLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblDOB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCCCD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCCCD, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addComponent(InfoSPR, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(UserInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(btnResetPW, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        UserAvatarPanel.setBackground(new java.awt.Color(255, 255, 255));

        ActionPanel.setBackground(new java.awt.Color(255, 255, 255));

        btnUpdateAvatar.setBackground(java.awt.SystemColor.textHighlight);
        btnUpdateAvatar.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnUpdateAvatar.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateAvatar.setText("Đổi ảnh đại diện");

        javax.swing.GroupLayout ActionPanelLayout = new javax.swing.GroupLayout(ActionPanel);
        ActionPanel.setLayout(ActionPanelLayout);
        ActionPanelLayout.setHorizontalGroup(
            ActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(ActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ActionPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnUpdateAvatar)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        ActionPanelLayout.setVerticalGroup(
            ActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 35, Short.MAX_VALUE)
            .addGroup(ActionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ActionPanelLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(btnUpdateAvatar)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        lblStatus.setText("Trạng thái");

        lblCreateAt.setText("Ngày tạo");

        lblCreateAtValue.setText("01/01/2003");

        lblStatusValue.setText("Hoạt động");

        lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/avt/N (2).jpg"))); // NOI18N

        javax.swing.GroupLayout UserAvatarPanelLayout = new javax.swing.GroupLayout(UserAvatarPanel);
        UserAvatarPanel.setLayout(UserAvatarPanelLayout);
        UserAvatarPanelLayout.setHorizontalGroup(
            UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserAvatarPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAvatar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ActionPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AvatarSpt, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(UserAvatarPanelLayout.createSequentialGroup()
                        .addGroup(UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(UserAvatarPanelLayout.createSequentialGroup()
                                .addComponent(lblCreateAt, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                                .addGap(9, 9, 9))
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCreateAtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(lblStatusValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(30, 30, 30))
        );
        UserAvatarPanelLayout.setVerticalGroup(
            UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(UserAvatarPanelLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblAvatar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ActionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(AvatarSpt, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCreateAtValue)
                    .addComponent(lblCreateAt))
                .addGap(6, 6, 6)
                .addGroup(UserAvatarPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(lblStatusValue))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout AccountPanelLayout = new javax.swing.GroupLayout(AccountPanel);
        AccountPanel.setLayout(AccountPanelLayout);
        AccountPanelLayout.setHorizontalGroup(
            AccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(UserAvatarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(UserInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
        AccountPanelLayout.setVerticalGroup(
            AccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(AccountPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(UserInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(UserAvatarPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        add(AccountPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void txtDOBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDOBActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDOBActionPerformed

    private void txtCCCDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCCCDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCCCDActionPerformed

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AccountPanel;
    private javax.swing.JPanel ActionPanel;
    private javax.swing.JSeparator AvatarSpt;
    private javax.swing.JDialog ConfirmUpdateDialog;
    private javax.swing.JSeparator HeaderSPR;
    private javax.swing.JSeparator InfoSPR;
    private javax.swing.JPanel UserAvatarPanel;
    private javax.swing.JPanel UserInfoPanel;
    private javax.swing.JButton btnResetPW;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpdateAvatar;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblCCCD;
    private javax.swing.JLabel lblCreateAt;
    private javax.swing.JLabel lblCreateAtValue;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblPW;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatusValue;
    private javax.swing.JLabel lblUserHeader;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtCCCD;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JPasswordField txtPW;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}

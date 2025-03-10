package client;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField displayNameField;

    private Register register;

    public RegisterController() {
        try {
            String url = "jdbc:mysql://localhost:3306/dbnguoidung";
            String user = "root"; 
            String password = "123456789@Vy"; 
            register = new Register(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Kết Nối Cơ Sở Dữ Liệu", "Không thể kết nối đến cơ sở dữ liệu.");
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String displayName = displayNameField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty()) {
            showAlert("Lỗi Nhập Liệu", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Lỗi Mật Khẩu", "Mật khẩu không khớp.");
            return;
        }

        try {
            // Đăng ký người dùng
            if (register.registerUser(username, password, displayName)) {
                showAlert("Thành Công", "Đăng ký thành công!");
                switchToLogin(username, password); // Chuyển sang màn hình đăng nhập với thông tin vừa đăng ký
            } else {
                showAlert("Lỗi Đăng Ký", "Tên đăng nhập đã tồn tại.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi Đăng Ký", "Không thể đăng ký người dùng. Vui lòng thử lại.");
        }
    }
    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to return to the login screen.");
        }
    }

    private void switchToLogin(String username, String password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml")); // Đường dẫn đến tệp FXML của màn hình đăng nhập
            Parent root = loader.load();

            // Lấy controller của LoginController
            LoginController loginController = loader.getController();
            loginController.setCredentials(username, password); // Truyền tên người dùng và mật khẩu

            Scene scene = new Scene(root);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể chuyển sang màn hình đăng nhập.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;

	private Login login;

	public void initialize() {
		String url = "jdbc:mysql://localhost:3306/dbnguoidung";
		String user = "root";
		String password = "123456789@Vy";

		try {
			login = new Login(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
			thongBao("Kết nối cơ sở dữ liệu không thành công!", AlertType.ERROR);
		}
	}

	@FXML
	private void handleLogin() {
		String username = usernameField.getText();
		String passwordInput = passwordField.getText();

		try {
			if (login.authenticate(username, passwordInput)) {
				String displayName = login.getDisplayName(username);
				thongBao("Đăng nhập thành công! Chào mừng, " + displayName + "!", AlertType.INFORMATION);
				switchToDieuKhienClient(displayName);
			} else {
				thongBao("Tên người dùng hoặc mật khẩu không hợp lệ.", AlertType.WARNING);
			}
		} catch (SQLException ex) {
			thongBao("Lỗi trong quá trình xác thực.", AlertType.ERROR);
			ex.printStackTrace();
		}
	}

	public void setCredentials(String username, String password) {
		usernameField.setText(username);
		passwordField.setText(password);
	}

	private void switchToDieuKhienClient(String displayName) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewClient.fxml"));
			Parent root = loader.load();

			// Lấy controller của DieuKhienClient
			DieuKhienClient dieuKhienClientController = loader.getController();
			dieuKhienClientController.dangNhap(displayName); // Truyền displayName

			Scene scene = new Scene(root);
			Stage stage = (Stage) usernameField.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void switchToRegister() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterView.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);
			Stage stage = (Stage) usernameField.getScene().getWindow();
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
			thongBao("Không thể chuyển sang màn hình đăng ký.", AlertType.ERROR);
		}
	}

	private void thongBao(String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
		alert.setContentText(message); //
		alert.setHeaderText(null);
		alert.setTitle("Thông báo");
		alert.showAndWait();
	}

	public void closeConnection() {
		if (login != null) {
			try {
				login.closeConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

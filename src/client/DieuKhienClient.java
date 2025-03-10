package client;

import java.io.IOException;
import javafx.scene.Node;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DieuKhienClient implements Initializable {

    @FXML
    private Label labelBan;
    @FXML
    private TextArea textAreaTrucTuyen;
    @FXML
    private TextArea textAreaNoiDung;
    @FXML
    private TextField textFieldSoanThao;
    @FXML
    private ComboBox<String> comboBoxChonNguoiNhan;

    private String serverName = "localhost";
    private int port = 3333;

    private ThreadNhapXuat t;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Không cần gọi ketNoiMayChu ở đây nữa
    }

    // Phương thức này sẽ được gọi sau khi đăng nhập thành công
    public void dangNhap(String displayName) {
        ketNoiMayChu(displayName); // Gọi phương thức kết nối và truyền displayName
    }
    
 // Phương thức đăng xuất
    @FXML
    public void hanhDongDangXuat(ActionEvent event) {
        // Tạo hộp thoại xác nhận
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText("Bạn có chắc chắn muốn đăng xuất?");
        alert.setContentText("Tất cả các phiên làm việc sẽ bị kết thúc.");

        // Xử lý kết quả của hộp thoại xác nhận
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Nếu người dùng chọn OK, gọi phương thức dừng thread và thực hiện đăng xuất
                if (t != null) {
                    t.stopThread();                  
                }
             // Đóng cửa sổ hiện tại
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();

                // Mở lại cửa sổ đăng nhập
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
                    Parent loginRoot = loader.load();
                    Scene loginScene = new Scene(loginRoot);

                    Stage loginStage = new Stage();
                    loginStage.setTitle("Ứng dụng - miniZalo");
                    loginStage.setScene(loginScene);
                    loginStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        });
    }


    // Phương thức kết nối với server
    private void ketNoiMayChu(String displayName) {
        try {

            t = new ThreadNhapXuat(serverName, port, labelBan, textAreaTrucTuyen, textAreaNoiDung, textFieldSoanThao, comboBoxChonNguoiNhan, displayName);
            t.start();
        } catch (Exception e) {
            System.out.println("Lỗi: " + e.getMessage());
        }
    }
    
    @FXML
    public void hanhDongGui(ActionEvent event) {
        t.gui();
    }
}

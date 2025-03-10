package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ThreadNhapXuat extends Thread {
	private String serverName;
	private int port;

	private Label labelBan;
	private TextArea textAreaTrucTuyen;
	private TextArea textAreaNoiDung;
	private TextField textFieldSoanThao;
	private ComboBox<String> comboBoxChonNguoiNhan;

	private Socket socket;
	private BufferedWriter bufferedWriter;
	private BufferedReader bufferedReader;
	private String displayName;
	
	// Constructor nhận các tham số cần thiết
	public ThreadNhapXuat(String serverName, int port, Label labelBan, TextArea textAreaTrucTuyen,
			TextArea textAreaNoiDung, TextField textFieldSoanThao, ComboBox<String> comboBoxChonNguoiNhan,
			String displayName) {
		this.serverName = serverName;
		this.port = port;
		this.labelBan = labelBan;
		this.textAreaTrucTuyen = textAreaTrucTuyen;
		this.textAreaNoiDung = textAreaNoiDung;
		this.textFieldSoanThao = textFieldSoanThao;
		this.comboBoxChonNguoiNhan = comboBoxChonNguoiNhan;
		this.displayName = displayName;
	}

	public ThreadNhapXuat() {
	}

	@Override
	public void run() {
	    try {
	        socket = new Socket(serverName, port);
	        System.out.println("Kết nối thành công!");

	        // Mở kênh nhập dữ liệu
	        InputStream in = socket.getInputStream();
	        InputStreamReader reader = new InputStreamReader(in);
	        bufferedReader = new BufferedReader(reader);

	        OutputStream out = socket.getOutputStream();
	        OutputStreamWriter writer = new OutputStreamWriter(out);
	        bufferedWriter = new BufferedWriter(writer);

	        System.out.println("Máy trạm: Mở kênh nhập xuất dữ liệu");

	        // Gửi yêu cầu đăng nhập
	        xuat("LOGIN#~" + displayName);

	        // Set label ban ngay khi bắt đầu
	        Platform.runLater(() -> {
	            labelBan.setText("Bạn: " + displayName);
	        });

	        // Bắt đầu nhận tất cả các thông điệp
	        // Kiểm tra nếu thread đang chạy
	        nhan();  // Nhận thông điệp
	        
	    } catch (Exception e) {
	        System.out.println("Lỗi: " + e.getMessage());
	    } finally {
	        try {
	            if (bufferedReader != null)
	                bufferedReader.close();
	            if (bufferedWriter != null)
	                bufferedWriter.close();
	            if (socket != null)
	                socket.close();
	        } catch (IOException e) {
	            System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
	        }
	    }
	}

	public void nhan() {
		nhanThongDiep();
	}

	public void nhanThongDiep() {
	    String thongDiepNhan;
	    while (true) {
	        try {
	            thongDiepNhan = bufferedReader.readLine();
	            if (thongDiepNhan != null) {
	                String[] phanTachThongDiep = thongDiepNhan.split("#~");
	                System.out.println("Nhận thông điệp: " + thongDiepNhan);
	                if (phanTachThongDiep[0].equals("LOGIN_FAILED")) {
	                	Platform.runLater(() -> {
	                        // Hiển thị thông báo
	                        thongBao(phanTachThongDiep[1]);
	                        
	                        // Đóng socket và thoát ứng dụng
	                        try {
	                            socket.close();
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                        showLoginScreen();
	                        	                      
	                    });
	                    return;
	                }
	                // Cập nhật danh sách đang online
	                else if (phanTachThongDiep[0].equals("capNhatDSOnline")) {
	                    capNhatDSOnline(phanTachThongDiep[2]);
	                }
	                // Hiển thị ra màn hình 1 người đăng nhập hoặc đăng xuất
	                else if (phanTachThongDiep[0].equals("capNhatDangNhapDangXuat")) {
	                    Platform.runLater(() -> {
	                        textAreaNoiDung.appendText(phanTachThongDiep[2] + "\n");
	                    });
	                }
	                // Hiển thị nội dung tin nhắn của một người gửi đến cho riêng
	                else if (phanTachThongDiep[0].equals("guiMotNguoi")) {
	                    Platform.runLater(() -> {
	                        textAreaNoiDung.appendText(phanTachThongDiep[1] + ": " + phanTachThongDiep[2] + "\n");
	                    });
	                }
	                // Hiển thị nội dung tin nhắn của một người gửi tới cho nhiều người
	                else if (phanTachThongDiep[0].equals("guiMoiNguoi")) {
	                    Platform.runLater(() -> {
	                        textAreaNoiDung.appendText(
	                                phanTachThongDiep[1] + " (gửi mọi người): " + phanTachThongDiep[2] + "\n");
	                    });
	                }
	            }
	        } catch (IOException e) {
	            System.out.println("Lỗi: " + e.getMessage());
	            return;
	        }
	    }
	}

	public void capNhatDSOnline(String danhSachOnline) {
	    String[] tachDanhSachOnline = danhSachOnline.split("-");
	    StringBuilder usernameOnline = new StringBuilder();
	    List<String> dsOnline = new ArrayList<>();

	    for (String i : tachDanhSachOnline) {
	        if (!i.isEmpty()) {
	            dsOnline.add(i);
	            usernameOnline.append(i).append("\n");
	        }
	    }

	    Platform.runLater(() -> {
	        textAreaTrucTuyen.setText(usernameOnline.toString());
	        capNhatComboBoxChonNguoiNhan(dsOnline);
	    });
	}

	private void capNhatComboBoxChonNguoiNhan(List<String> dsOnline) {
		Platform.runLater(() -> {
			comboBoxChonNguoiNhan.getItems().clear();
			comboBoxChonNguoiNhan.setPromptText("Chọn người nhận");
			comboBoxChonNguoiNhan.getItems().add("Mọi người");
			for (String i : dsOnline) {
				if (!i.equals(this.displayName)) {
					comboBoxChonNguoiNhan.getItems().add(i);
				}
			}
		});
	}

	public void gui() {
		String thongDiep = textFieldSoanThao.getText();
		String nguoiNhan = comboBoxChonNguoiNhan.getValue();

		if (thongDiep.isBlank() || nguoiNhan == null) {
			thongBao("Bạn chưa nhập thông điệp hoặc chưa chọn người nhận");
		} else {
			if (nguoiNhan.equals("Mọi người")) {
				xuat("guiMoiNguoi#~" + thongDiep);
				textAreaNoiDung.appendText("Bạn (gửi mọi người): " + thongDiep + "\n");
			} else {
				xuat("guiMotNguoi#~" + thongDiep + "#~" + nguoiNhan);
				textAreaNoiDung.appendText("Bạn (gửi " + nguoiNhan + "): " + thongDiep + "\n");
			}
			textFieldSoanThao.clear();
		}
	}

	public void xuat(String thongDiep) {
		try {
			bufferedWriter.write(thongDiep);
			bufferedWriter.newLine();
			bufferedWriter.flush();
		} catch (IOException e) {
			System.out.println("Lỗi rồi: " + e.getMessage());
		}
	}

	// Đưa ra thông báo cho người dùng
	public void thongBao(String tb) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Thông báo");
		alert.setHeaderText(null);
		alert.setContentText(tb);
		alert.showAndWait();
	}
	
	public void stopThread() {
	    
    	String message = "DANG_XUAT#" + displayName;
        xuat(message);
        System.out.println("Đang gửi thông báo đăng xuất đến server...");
	    try {
	        if (socket != null) {
	            socket.close();
	        }
	    } catch (IOException e) {
	        System.out.println("Lỗi khi đóng kết nối: " + e.getMessage());
	    }
	}
	public void showLoginScreen() {

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
}
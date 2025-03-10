package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ThreadNhapXuat extends Thread {
	private Socket socket;
	private String displayName;
	private BufferedWriter bufferedWriter;
	private BufferedReader bufferedReader;

	public ThreadNhapXuat(Socket socket) {
		this.socket = socket;
		this.displayName = null; // Sẽ được set sau khi nhận từ client
	}

	@Override
	public void run() {
		try {
			// Mở kênh nhập dữ liệu - Nhận
			InputStream in = socket.getInputStream();
			InputStreamReader reader = new InputStreamReader(in);
			bufferedReader = new BufferedReader(reader);

			// Mở kênh xuất dữ liệu - Gửi
			OutputStream out = socket.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(out);
			bufferedWriter = new BufferedWriter(writer);

			// Đọc displayName từ client
			String loginMessage = nhap();
			if (loginMessage != null && loginMessage.startsWith("LOGIN#~")) {
				String requestedDisplayName = loginMessage.split("#~")[1];

				// Kiểm tra displayName trước khi thêm vào danh sách
				if (Server.xuLy.isDisplayNameExist(requestedDisplayName)) {
					xuat("LOGIN_FAILED#~Rất tiếc: Tài khoản này đang online. Vui lòng đăng nhập bằng tài khoản khác!");
					return; // Kết thúc thread nếu tên đã tồn tại
				}

				this.displayName = requestedDisplayName;
				System.out.println("Máy chủ: Máy trạm " + displayName + " đã đăng nhập");

				Server.xuLy.themVao(this);
				Server.xuLy.guiDanhSachUserDangOnline();
				Server.xuLy.guiMoiNguoi("capNhatDangNhapDangXuat#~server#~ ***" + displayName + " đã đăng nhập***",
						displayName);
			} else {
				xuat("LOGIN_FAILED#~Yêu cầu đăng nhập không hợp lệ");
				return; // Kết thúc thread nếu yêu cầu đăng nhập không hợp lệ
			}
					

			
			String thongDiep;
			
			while ((thongDiep = nhap()) != null) {
				if (thongDiep.startsWith("DANG_XUAT#")) {
					
						String userDisplayName = thongDiep.split("#")[1];
						System.out.println("Máy trạm " + userDisplayName + " đã đăng xuất.");

						// Cập nhật danh sách người dùng đang online
						Server.xuLy.loaiRa(userDisplayName); // Loại bỏ người dùng khỏi danh sách online
						Server.xuLy.guiDanhSachUserDangOnline(); // Cập nhật lại danh sách online

						// Gửi thông báo đăng xuất cho tất cả các client còn lại
						Server.xuLy.guiMoiNguoi(
								"capNhatDangNhapDangXuat#~server#~ ***" + userDisplayName + " đã đăng xuất***",
								userDisplayName);
					
					break; // Kết thúc vòng lặp sau khi đăng xuất
				} else {
					System.out.println("TẠI ĐÂY THONG DIEP là "+ thongDiep.split("#")[1]);
					Server.xuLy.chuyenTiepThongDiep(thongDiep, displayName);
				}
			}
		} catch (IOException e) {
			System.out.println("Lỗi kết nối với client: " + e.getMessage());
		} finally {
			try {
				if (displayName != null) {
					Server.xuLy.loaiRa(displayName);
					System.out.println("Máy chủ: Máy trạm " + displayName + " đã thoát; Số thread đang chạy là: "
							+ Server.xuLy.getKichThuoc());
					Server.xuLy.guiDanhSachUserDangOnline();
					Server.xuLy.guiMoiNguoi("capNhatDangNhapDangXuat#~server#~ ***" + displayName + " đã thoát***",
							displayName);
				}
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

	public String nhap() throws IOException {
		return bufferedReader.readLine();
	}

	public void xuat(String thongDiep) throws IOException {
		bufferedWriter.write(thongDiep);
		bufferedWriter.newLine();
		bufferedWriter.flush();
	}

	public String getDisplayName() {
		return displayName;
	}
}
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) throws IOException {
        Server sv = new Server(3333);
        sv.chay();
    }

    public static volatile XuLy xuLy;

    private ServerSocket serverSocket = null;

    public Server(int port) throws IOException {
        try {
            serverSocket = new ServerSocket(port);
            xuLy = new XuLy();
        } catch (Exception e) {
            System.out.println("Lỗi rồi: " + e.getMessage());
        }
    }

    public void chay() {
        while (true) {
            try {
                System.out.println("Máy chủ: Đang chờ máy trạm đăng nhập...");
                // Chấp nhận yêu cầu kết nối từ client
                Socket incomingSocket = serverSocket.accept();
                System.out.println("Máy chủ: Đã có máy trạm " + incomingSocket.getRemoteSocketAddress()
                        + " kết nối vào máy chủ " + incomingSocket.getLocalSocketAddress());

                // Tạo Thread Nhập Xuất để quản lý từng client
                ThreadNhapXuat threadNhapXuat = new ThreadNhapXuat(incomingSocket);
                threadNhapXuat.start();

                // Thêm Thread Nhập-Xuất của từng client vào lớp xử lý
//                xuLy.themVao(threadNhapXuat);
                System.out.println("Máy chủ: Số thread đang chạy là: " + xuLy.getKichThuoc());

            } catch (IOException e) {
                System.out.println("Lỗi rồi (2): " + e.getMessage());
            }
        }
    }
}

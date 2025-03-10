package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KiemThuLogin extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent giaoDien = FXMLLoader.load(getClass().getResource("LoginView.fxml"));
			Scene scene = new Scene(giaoDien);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("Ứng dụng - miniZalo");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}


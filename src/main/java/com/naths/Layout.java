package com.naths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Layout extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//Add Layout
			Parent root = FXMLLoader.load(getClass().getResource("/Scene/MenuScene.fxml"));
			Scene scene = new Scene(root);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Account");
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void run() {
		launch("");
	}
}

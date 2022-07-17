package Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MenuController implements Initializable {
	/*Components*/
	@FXML
	private AnchorPane content;
	@FXML
	private Button searchBtn, addBtn, exitBtn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {}
	
	/*Button Functions*/
	public void onBtnClick(ActionEvent event) {
		if(event.getSource() == searchBtn) {
			try {
				//Switch Scene -> InputScene
				Stage stage = (Stage) content.getScene().getWindow();
				stage.setScene(new Scene((Parent)FXMLLoader.load(getClass().getResource("/Scene/SearchScene.fxml"))));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(event.getSource() == addBtn) {
			try {
				//Switch Scene -> AboutScene
				Stage stage = (Stage) content.getScene().getWindow();
				stage.setScene(new Scene((Parent)FXMLLoader.load(getClass().getResource("/Scene/AddScene.fxml"))));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(event.getSource() == exitBtn) {
			//Close Application
			Platform.exit();
		}
	}
}

package Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.naths.Main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/* data.bin
 * 
 * Category Format:
 * ;[Category Name]
 * [Account] - [Password]
 * ...
 * :End;
 */

public class AddController implements Initializable {
	/*Variables*/
	private File file;
	private BufferedReader br;
	private BufferedWriter bw;
	
	/*Components*/
	@FXML
	private AnchorPane content;
	@FXML
	private RadioButton cateRB, acRB;
	@FXML
	private Pane catePane, acPane;
	@FXML
	private ChoiceBox cateCB;
	@FXML
	private TextField cnTF, acTF, pwTF, ecTF;
	@FXML
	private Text statusLbl;
	@FXML
	private Button addBtn, backBtn;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			//Initiate data.bin
			file = new File(Main.path);
			
			//If data.bin Not Exist Then Create
			if (!file.getParentFile().exists())
			    file.getParentFile().mkdirs();
			if(!file.exists())
				file.createNewFile();
			
			System.out.println("File OK");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Category Selected
		cateRB.setSelected(true);
		catePane.setVisible(true);
		acPane.setVisible(false);
	}
	
	/*Other Functions*/
	public String[] getAccount(String target) {
		String[] contents = new String[2];
		contents[0] = target.substring(0, target.indexOf("###"));
		contents[1] = target.substring(target.indexOf("###")+3);
		return contents;
	}
	public String encryptString(String target, String code) {
		String encrypted = "";
		int count = 0;
		for(char c : target.toCharArray()) {
			encrypted += (char)(((byte)(c) + (byte)(code.charAt(count))) % 255);
			
			count++;
			if(count >= code.length())
				count = 0;
		}
		
		return encrypted;
	}
	public void readCategory() {
		//Clear All Category ChoiceBox Item
		cateCB.getItems().clear();
		
		try {
			br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null) {
				if(str.substring(0, 1).equals(";"))
					cateCB.getItems().add(str.substring(1));
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Add Choice
		if(cateCB.getItems().size() > 0)
			cateCB.setValue(cateCB.getItems().get(0));
	}
	public boolean isCategoryExist(String target) {
		try {
			//Initiate File Reader
			br = new BufferedReader(new FileReader(file));
			
			//Get Category Name
			String str;
			while((str = br.readLine()) != null) {
				if(str.substring(0, 1).equals(";")) {
					if(str.toLowerCase().substring(1).equals(target)) {
						//Exist
						br.close();
						return true;
					}
				}
			}
			
			//End Reading
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	public void resetInput() {
		cnTF.setText("");
		acTF.setText("");
		pwTF.setText("");
		ecTF.setText("");
		statusLbl.setText("");
	}
	
	/*RadioButton Function*/
	public void onRBClick(ActionEvent event) {
		//Change, Reset Pane
		if(event.getSource() == cateRB) {
			catePane.setVisible(true);
			acPane.setVisible(false);
			addBtn.setDisable(false);
			
			resetInput();
		} else if(event.getSource() == acRB) {
			catePane.setVisible(false);
			acPane.setVisible(true);
			
			readCategory();
			if(cateCB.getItems().size() <= 0)
				addBtn.setDisable(true);
			
			resetInput();
		}
	}
	
	/*Button Function*/
	public void onBtnClick(ActionEvent event) {
		if(event.getSource() == addBtn) {
			if(cateRB.isSelected()) {
				//Check Null
				if(cnTF.getText().length() == 0) {
					statusLbl.setFill(Color.RED);
					statusLbl.setText("Please Enter Category Name");
					return;
				}
				
				//Check Exist Category
				if(isCategoryExist(cnTF.getText().toLowerCase())) {
					resetInput();
					statusLbl.setFill(Color.RED);
					statusLbl.setText("This Category Is Existed");
					return;
				}
				
				//Valid
				try {
					//Initiate File Writer
					bw = new BufferedWriter(new FileWriter(file, true));
					
					//Add New Category
					bw.append(";" + cnTF.getText().trim() + "\n");
					bw.append(":" + cnTF.getText().trim() + "\n");
					
					//End Writing
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//Successful
				resetInput();
				statusLbl.setFill(Color.GREEN);
				statusLbl.setText("Add Category Successful");
			} else {
				//Check Null
				if(acTF.getText().trim().length() == 0) {
					statusLbl.setText("Please Enter Account");
					return;
				}
				if(pwTF.getText().trim().length() == 0) {
					statusLbl.setText("Please Enter Password");
					return;
				}
				
				//Valid
				try {
					//Get All Content Of File
					ArrayList<String> contents = new ArrayList<String>();
					ArrayList<String> targetContents = new ArrayList<String>();
					br = new BufferedReader(new FileReader(file));
					String str;
					while((str = br.readLine()) != null) {
						//Get Target Category Contents
						if(str.equals(";" + cateCB.getValue().toString().trim())) {
							while(!((str = br.readLine()).substring(0, 1).equals(":"))) {
								targetContents.add(str);
							}
						} else {
							contents.add(str);
						}
					}
					br.close();
					
					//Check Exist Account
					for(String s: targetContents) {
						if(acTF.getText().trim().equals(getAccount(s)[0])) {
							statusLbl.setText("This Account Is Existed");
							return;
						}
					}
					
					//Add New Account+Password
					targetContents.add(
							//encryptString(acTF.getText().trim(), ecTF.getText().trim())
							acTF.getText().trim()
							+ "###" 
							+ (ecTF.getText().length() == 0 ? pwTF.getText().trim() : encryptString(pwTF.getText().trim(), ecTF.getText()))
					);
					
					//Initiate File Writer
					bw = new BufferedWriter(new FileWriter(file));
					
					//Write All Contents
					for(String s: contents)
						bw.append(s + "\n");
					bw.append(";" + cateCB.getValue().toString().trim() + "\n");
					for(String s: targetContents)
						bw.append(s + "\n");
					bw.append(":" + cateCB.getValue().toString().trim() + "\n");
					
					//End Writing
					bw.flush();
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				//Successful
				resetInput();
				statusLbl.setFill(Color.GREEN);
				statusLbl.setText("Add Account Successful");
			}
		} else if(event.getSource() == backBtn) {
			try {
				//Switch Scene -> AboutScene
				Stage stage = (Stage) content.getScene().getWindow();
				stage.setScene(new Scene((Parent)FXMLLoader.load(getClass().getResource("/Scene/MenuScene.fxml"))));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

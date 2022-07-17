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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SearchController implements Initializable {
	/*Variables*/
	private File file;
	private BufferedReader br;
	private BufferedWriter bw;
	private int selectIndex, cateIndex;
	private String lastAC, lastPW;
	
	/*Components*/
	@FXML
	private AnchorPane content;
	@FXML
	private ChoiceBox cateCB;
	@FXML
	private TableView<dataType> TV;
	@FXML
	private TableColumn acCol, pwCol;
	@FXML
	private Text statusLbl;
	@FXML
	private TextField ecTF;
	@FXML
	private Button saveBtn, delBtn, backBtn;
	
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
			
			//Read Category
			readCategory();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Selection Index
		selectIndex = -1;
		TV.setEditable(true);
		pwCol.setCellFactory(TextFieldTableCell.forTableColumn());
		pwCol.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
			@Override
			public void handle(CellEditEvent event) {
				((dataType)event.getRowValue()).setPassword(event.getNewValue().toString());
			}
		});
		TV.setRowFactory(new Callback<TableView<dataType>, TableRow<dataType>>() {
			@Override
			public TableRow<dataType> call(TableView<dataType> param) {
				final TableRow<dataType> row = new TableRow<dataType>();
	            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						if(row.getIndex() >= TV.getItems().size()) 
							return;
						if(row.getIndex() == selectIndex)
							return;
						
						//OKK
						ecTF.setText("");
						
						if(selectIndex != -1)
							TV.getItems().set(selectIndex, new dataType(lastAC, lastPW));
						
						selectIndex = row.getIndex();
						lastAC = TV.getItems().get(selectIndex).getAccount();
						lastPW = TV.getItems().get(selectIndex).getPassword();
					}
				});
	            return row ;
			}
		});
		/*TV.setRowFactory(tv -> {
            TableRow<dataType> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
            	if(row.getIndex() >= TV.getItems().size()) 
            		return;
            	if(row.getIndex() == selectIndex)
            		return;
            	
            	//OKK
            	ecTF.setText("");
            	
            	if(selectIndex != -1)
            		TV.getItems().set(selectIndex, new dataType(lastAC, lastPW));
            	
            	selectIndex = row.getIndex();
            	lastAC = TV.getItems().get(selectIndex).getAccount();
            	lastPW = TV.getItems().get(selectIndex).getPassword();
            });
            return row ;
        });*/
		
		//Encrypt Code TextField
		ecTF.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(selectIndex >= 0) {
					if(newValue.length() > 0) {
						TV.getItems().set(selectIndex, 
							new dataType(
								//decryptString(lastAC, newValue.trim()),
								lastAC,
								decryptString(lastPW, newValue.trim())
							));
					} else {
						TV.getItems().set(selectIndex, new dataType(lastAC, lastPW));
					}
				}
			}
		});
		
		//Add SelectionMode Listener
		acCol.setCellValueFactory(new PropertyValueFactory<dataType, String>("account"));
		pwCol.setCellValueFactory(new PropertyValueFactory<dataType, String>("password"));
		cateCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableNumber, Number number1, Number number2) {
				selectIndex = -1;
				cateIndex = number2.intValue();
				readContent(cateIndex);
			}
		});
		
		//Select First
		if(cateCB.getItems().size() > 0)
			readContent(0);
	}
	
	/*Other Function*/
	public void saveContent() {
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
			
			//Change Content
			for(int i = 0; i < targetContents.size(); i++) {
				//System.out.println(targetContents.get(i));
				if(getAccount(targetContents.get(i))[0].equals(lastAC)) {
					String content;
					if(ecTF.getText().trim().length() == 0) {
						content = lastAC + "###" + TV.getItems().get(i).getPassword();
					} else {
						content = lastAC + "###" + encryptString(TV.getItems().get(i).getPassword(), ecTF.getText().trim());
					}
					targetContents.set(i, content);
					
					//Finish
					lastPW = getAccount(content)[1];
					ecTF.setText("");
					break;
				}
			}
			
			//Write All Contents
			bw = new BufferedWriter(new FileWriter(file));
			for(String s: contents)
				bw.append(s + "\n");
			bw.append(";" + cateCB.getValue().toString().trim() + "\n");
			for(String s: targetContents)
				bw.append(s + "\n");
			bw.append(":" + cateCB.getValue().toString().trim() + "\n");
			
			//End Writing
			bw.flush();
			bw.close();
			statusLbl.setFill(Color.GREEN);
			statusLbl.setText("Save Successful");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void delContent() {
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
			
			//Change Content
			for(int i = 0; i < targetContents.size(); i++) {
				//System.out.println(targetContents.get(i));
				if(getAccount(targetContents.get(i))[0].equals(lastAC)) {
					targetContents.remove(i);
					
					//Finish
					selectIndex = -1;
					lastPW = "";
					lastAC = "";
					ecTF.setText("");
					break;
				}
			}
			
			//Write All Contents
			bw = new BufferedWriter(new FileWriter(file));
			for(String s: contents)
				bw.append(s + "\n");
			bw.append(";" + cateCB.getValue().toString().trim() + "\n");
			for(String s: targetContents)
				bw.append(s + "\n");
			bw.append(":" + cateCB.getValue().toString().trim() + "\n");
			
			//End Writing
			bw.flush();
			bw.close();
			statusLbl.setFill(Color.GREEN);
			statusLbl.setText("Delete Successful");
			readContent(cateIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public String decryptString(String target, String code) {
		int count = 0;
		String decrypted = "";
		byte tmp;	
		for(char c : target.toCharArray()) {
			tmp = (byte) ((byte)(c) - (byte)(code.charAt(count)));
			
			if(tmp < 0) {
				tmp = (byte) ((byte)code.charAt(count) - (byte)c);
			}
			
			//Append Char
			decrypted += (char)tmp;
			
			count++;
			if(count >= code.length())
				count = 0;
		}
		return decrypted;
	}
	public void readContent(int index) {
		//Read Target Category Data
		try {
			String target = cateCB.getItems().get(index).toString();
			String[] ac;
			
			ObservableList<dataType> list = FXCollections.observableArrayList();
			
			br = new BufferedReader(new FileReader(file));
			String str;
			while((str = br.readLine()) != null) {
				if(str.equals(";" + target)) {
					//Get All Account
					while((str = br.readLine()) != null) {
						if(str.equals(":" + target)) {
							break;
						} else {
							//Add To TV
							//System.out.println(str);
							ac = getAccount(str);
							list.add(new dataType(ac[0], ac[1]));
						}
					}
				}
			}
			TV.setItems(list);
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String[] getAccount(String target) {
		String[] contents = new String[2];
		contents[0] = target.substring(0, target.indexOf("###"));
		contents[1] = target.substring(target.indexOf("###")+3);
		return contents;
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
	
	/*Button Function*/
	public void onBtnClick(ActionEvent event) {
		if(event.getSource() == backBtn) {
			try {
				//Switch Scene -> AboutScene
				Stage stage = (Stage) content.getScene().getWindow();
				stage.setScene(new Scene((Parent)FXMLLoader.load(getClass().getResource("/Scene/MenuScene.fxml"))));
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(event.getSource() == saveBtn) {
			if(selectIndex == -1) {
				statusLbl.setFill(Color.RED);
				statusLbl.setText("No Account Selected");
				return;
			}
			
			saveContent();
		} else if(event.getSource() == delBtn) {
			if(selectIndex == -1) {
				statusLbl.setFill(Color.RED);
				statusLbl.setText("No Account Selected");
				return;
			}
				
			delContent();
		}
	}
	
	/*TableView Data Type*/
	public static class dataType {
		private final StringProperty account;
		private final StringProperty password;
		public dataType(String account, String password) {
			this.account = new SimpleStringProperty(account);
			this.password = new SimpleStringProperty(password);
		}
		public void setPassword(String password) { this.password.set(password); }
		public String getAccount() { return account.get(); }
		public String getPassword() { return password.get(); }
	}
}

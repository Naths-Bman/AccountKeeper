package com.naths;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main {
	public static String path;
	
	public static void main(String[] args) {
		try {
			path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString();
			path = path.substring(5, path.lastIndexOf('/')+1) + "data.bin";
			System.out.println(path);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		/*String str = "uvwxyz{|}~";
		String code = "9";
		String encrypted = "";
		
		for(char c : str.toCharArray()) {
			encrypted += (char)(((byte)(c) + (byte)(code.charAt(0))) % 255);
		}
		System.out.println(encrypted);
		
		byte tmp;
		for(char c : encrypted.toCharArray()) {
			tmp = (byte) ((byte)(c) - (byte)(code.charAt(0)));
			
			if(tmp < 0) {
				tmp = (byte) ((byte)code.charAt(0) - (byte)c);
			}
			
			System.out.print((char)tmp);
			
		}*/
		
		new Layout().run();
	}
}
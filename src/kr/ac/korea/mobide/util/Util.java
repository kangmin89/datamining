package kr.ac.korea.mobide.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class Util {
	public static void makeDirectory(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void serialize(String filePath, String fileName, Object object) {
		Util.makeDirectory(filePath);
		try {
			FileOutputStream fos = new FileOutputStream(filePath+fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	public static Object deserialize(String filePath, String fileName) {
		Object object = null;
		try {
			FileInputStream fis = new FileInputStream(filePath+fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			object = ois.readObject();
			ois.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		return object;
	}
		
	public static ArrayList<String> tokenizer(String document){
		ArrayList<String> termList;
		String result="";
		result = document.replace("'", "").replace(".", "")
				.replace(":", "").replace(",", "").replace("\"", "")
				.replace("?", "").replace("!", "").replace(";", "").replace("&amp;", "");
		String []tokens = result.split(" ");
		termList = new ArrayList<String>(Arrays.asList(tokens));
		return termList;
	}

}

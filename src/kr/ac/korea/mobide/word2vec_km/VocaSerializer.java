package kr.ac.korea.mobide.word2vec_km;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;


public class VocaSerializer {

	public static HashMap<String, Integer> ReadVoca(){
		LearnVoca lv = new LearnVoca();
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream("Voca.txt");
			ois = new ObjectInputStream(fis);
			lv = (LearnVoca)ois.readObject();
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			try{
				ois.close();
				fis.close();
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
		return lv.getVoca(); 
	}
	public static void SaveVoca(LearnVoca lv){
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = new FileOutputStream("Voca.txt");
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(lv);
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			try{
				oos.close();
				fos.close();
			}catch(Exception e){
				e.printStackTrace(System.err);
			}
		}
	}


}

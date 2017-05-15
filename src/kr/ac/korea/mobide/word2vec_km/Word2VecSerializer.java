package kr.ac.korea.mobide.word2vec_km;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;




public class Word2VecSerializer {
	public static void SaveWord2Vec(Word2Vec wv){
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = new FileOutputStream("Word2Vec.txt");
			oos = new ObjectOutputStream(fos);
			
			oos.writeObject(wv);
			
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
	public static Word2Vec ReadWord2Vec(){
		Word2Vec wv = null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream("Word2Vec.txt");
			ois = new ObjectInputStream(fis);
			wv = (Word2Vec)ois.readObject();
			
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
		return wv; 
	}

}

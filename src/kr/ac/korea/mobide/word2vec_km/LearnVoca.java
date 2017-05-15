package kr.ac.korea.mobide.word2vec_km;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;


public class LearnVoca implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1503784431401087854L;
	private HashMap<String, Integer> Voca = new HashMap<String, Integer>();
	
	//private int voca_size = 0;
	private int min_reduce=1;
	public HashMap<String, Integer> getVoca(){
		return this.Voca;
	}
	public void setVoca(HashMap<String, Integer> Voca)
	{
		this.Voca = Voca;
	}
	public void AddWordToVoca(String word){
		if(word.length()>Constants_km.MAX_STRING) word = word.substring(0, 99);
		if(this.Voca.containsKey(word))
		{
			int temp = this.Voca.get(word)+1;
			this.Voca.replace(word, temp);
		}
		else
		{
			this.Voca.put(word, 1);
		}
	}
	public void ReduceVoca(){
		HashSet<String> removeWord = new HashSet<String>();
		Iterator<String> words = this.Voca.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			if(this.Voca.get(word)<min_reduce)
				removeWord.add(word);
				
		}
		Iterator<String> rwords = removeWord.iterator();
		while(rwords.hasNext()){
			this.Voca.remove(rwords.next());
		}
		min_reduce++;
	}
	public void ModifyVoca(){
		HashSet<String> removeWord = new HashSet<String>();
		Iterator<String> words = this.Voca.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			if(this.Voca.get(word)<Constants_km.MIN_COUNT || checkStrangeWord(word)/*||!mapTermTermID.containsKey(word)*//*|| !mapCIDMC.get(482442).getMapTermIDWeight().containsKey(mapTermTermID.get(word))*/)
			{
				if(!word.equals("."))
					removeWord.add(word);
			}
		}
		Iterator<String> rwords = removeWord.iterator();
		while(rwords.hasNext()){
			String rword = rwords.next();
			System.out.println(rword);
			this.Voca.remove(rword);
		}
	}
	private  boolean checkStrangeWord(String word) {
	      // TODO Auto-generated method stub
	      
	      // 깨진 단어들 제거
	      for (int i = 0; i < word.length(); i++) {
	         if (word.charAt(i) - 'a' < 0 || word.charAt(i) - 'z' > 0) {
	            if (word.charAt(i) - '0' < 0 || word.charAt(i) - '9' > 0) {
	               return true;
	            }
	         }
	      }

	      // 숫자만 있는 단어들 제거
	      for (int i = 0; i < word.length(); i++) {
	         if (word.charAt(i) - 'a' >= 0 && word.charAt(i) - 'z' <= 0)
	            return false;
	      }
	      return true;
	   }
	public void LearnVocaFromTrainFile(/*HashMap<Integer,Vector>mapACIDIndex*/){
		BufferedReader reader = null;
		try {
			for(int docNum=0;docNum<100;docNum++)
			{
				reader = new BufferedReader(new FileReader(Constants_km.CORPUS_NAME+Integer.toString(docNum)+Constants_km.CORPUS_NAME2));
				String line = reader.readLine();
				System.out.println(line);
				while ( line != null ) {
					StringTokenizer st = new StringTokenizer(line, "\t");
					while (st.hasMoreTokens()) {
						String tline = st.nextToken();
						StringTokenizer st2 = new StringTokenizer(tline, " ");
						while(st2.hasMoreTokens()){
							String word = st2.nextToken();
							AddWordToVoca(word);
							if(this.Voca.size()>Constants_km.VOCA_HASH_SIZE) ReduceVoca();
						}
			        }
					line = reader.readLine();
			    }
			}
			ModifyVoca();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            try { if (reader!=null) reader.close(); } catch (Exception e) {}
        }
		
	}
	public void PrintVoca(){
		try{
			FileWriter fw = new FileWriter("VocaCheck.txt");
			Iterator<String> words = this.Voca.keySet().iterator();
			while(words.hasNext()){
				String word = words.next();
				fw.write(word +" "+this.Voca.get(word)+" ");
			}
			fw.close();
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}
	public int TrainWords(){
		int train_words = 0;
		Iterator<String> words = this.Voca.keySet().iterator();
		while(words.hasNext()){
			train_words += this.Voca.get(words.next());
		}
		return train_words;
	}
	
}

package kr.ac.korea.mobide.word2vec_km;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import kr.ac.korea.mobide.util.Constants;

public class UnigramDistribution implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7980078383494965879L;
	private HashMap<Integer, String> UDIndex = new HashMap<Integer, String>();
	private HashMap<String, Integer> WordIndex = new HashMap<String, Integer>();
	private int [] UDTable;
	public int getWIndex(String word){
		if(WordIndex.containsKey(word))
			return this.WordIndex.get(word);
		else
			return -1;
	}
	public String getRealWord(int word){
		if(UDIndex.containsKey(word))
			return this.UDIndex.get(word);
		else
			return null;
	}	
	public void InitUnigramTable(HashMap<String, Integer> Voca){
		double power = 0.75;
		double train_words_pow =0.0;
		int index=0;
		UDTable = new int [Constants_km.UNIGRAM_DISTRIBUTION_TABLE_SIZE];
		Iterator<String> words = Voca.keySet().iterator();
		while(words.hasNext())
		{
			String word = words.next();
			UDIndex.put(index, word);
			WordIndex.put(word, index);
			index++;
			train_words_pow += Math.pow(Voca.get(word), power);
		}
		
		index =0;
		double d1 = Math.pow(Voca.get(UDIndex.get(index)), power);
		for(int a =0; a<Constants_km.UNIGRAM_DISTRIBUTION_TABLE_SIZE;a++)
		{
			UDTable[a] = index; 
			if(a/(double)Constants_km.UNIGRAM_DISTRIBUTION_TABLE_SIZE >d1){
				index++;
				d1 += Math.pow(Voca.get(UDIndex.get(index)), power)/ train_words_pow;
			}
			if(index >= Voca.size()) index = Voca.size()-1;
		}
		
	}
	
}

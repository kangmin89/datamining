package kr.ac.korea.mobide.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;


import kr.ac.korea.mobide.tokenizer.Tokenizer;
import kr.ac.korea.mobide.util.DocAnswer;
import kr.ac.korea.mobide.word2vec_km.Word2Vec;
import kr.ac.korea.mobide.word2vec_km.Word2VecSerializer;
import kr.ac.korea.mobide.word2vec_km.Word2VecUtil_km;


public class ClassifierUtil {
	protected static ArrayList<HashMap<Integer,HashMap<String, Double>>> getTFIDFVector() {
		ArrayList<HashMap<Integer,HashMap<String, Double>>> vsm = new ArrayList<HashMap<Integer,HashMap<String, Double>>>();
		HashMap<String, Integer> voca = new HashMap<String, Integer>();
		try {
			for(int c=1;c<21;c++)
			{
				String dir = "20news/"+Integer.toString(c)+"/";
				File fdir = new File(dir);
				BufferedReader reader = null;
				HashMap<Integer, HashMap<String, Double>> document = new HashMap<Integer, HashMap<String, Double>>();
				if(fdir.isDirectory())
				{
					String list[] = fdir.list();
					for(String file:list)
					{
						String doc = "";
						double sumf = 0.0;
						reader = new BufferedReader(new FileReader(dir+file));
						HashMap<String, Double> temp = new HashMap<String, Double>();
						String line = reader.readLine();
						while ( line != null ) {
							doc += line +" ";
							line = reader.readLine();
					    }
						for (String term : Tokenizer.getListToken(doc)) {
							//System.out.println(term);
							sumf += 1.0;
							if(!temp.containsKey(term)){
								temp.put(term, 1.0);
								if(!voca.containsKey(temp)) voca.put(term, 1);
								else voca.replace(term,voca.get(term)+1);
							}
							else temp.replace(term, temp.get(term)+1.0);
						}
						//System.out.println(" ");
						/************tf modify**************/
						for(String term:temp.keySet()) temp.replace(term, temp.get(term)/sumf);
						document.put(Integer.parseInt(file), temp);
					}
				}
				vsm.add(document);
			}
			/************applu idf **************/
			int N = 0;//document size
			for(int i =0; i<vsm.size();i++) N +=vsm.get(i).size();
			for(int i =0; i<vsm.size();i++) for(int document:vsm.get(i).keySet()){
				for(String term:vsm.get(i).get(document).keySet())
					vsm.get(i).get(document).replace(term, vsm.get(i).get(document).get(term)*Math.log10(N/voca.get(term)));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		return vsm;
	}
	protected static HashMap<DocAnswer, double[]>makeDocVector(ArrayList<HashMap<Integer,HashMap<String, Double>>> vsm) {
		HashMap<DocAnswer, double[]> docVector = new HashMap<DocAnswer, double[]>();
		Word2Vec vec = Word2VecSerializer.ReadWord2Vec();
		int num =0;
		for(int i =0; i<vsm.size();i++) 
		{
			for(int document:vsm.get(i).keySet())
			{
				DocAnswer ds = new DocAnswer(document, i);
				docVector.put(ds, Word2VecUtil_km.documentVector(vec, vsm.get(i).get(document)));
				num++;
				if(num%100==0)
					System.out.println(num);
			}
		}	
		return docVector;
	}
	
}

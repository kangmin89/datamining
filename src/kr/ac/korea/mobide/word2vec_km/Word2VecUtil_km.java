package kr.ac.korea.mobide.word2vec_km;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class Word2VecUtil_km {
	public static double WordSim(Word2Vec vec, String w1, String w2){	
		return vec.WordSim(w1, w2);
	}
	public static String NearestWord(Word2Vec vec, String term){
		if(vec.containWord(term)!=-1)
			return vec.NearestWord(term, 2).get(1).getWord();
		else
		{
			return "-1";
		}
	}
	public static int containWord(Word2Vec vec, String word)
	{
		return vec.containWord(word);
	}
	public static double[] WordCentroid(Word2Vec vec, HashMap<Integer, Double> tfidfVector, HashMap<Integer, String> mapTermIDTerm){
		int layerSize = vec.getLayerSize();
		double [] WCD = new double [layerSize];
		double [] Word2Vec = vec.getWord2VecModel();
		for(int termID : tfidfVector.keySet())
		{
			int index = vec.containWord(mapTermIDTerm.get(termID));
			if(index==-1)
				continue;
			double weight = tfidfVector.get(termID);
			for(int i=0;i<layerSize;i++)
			{
//				System.out.println(mapTermIDTerm.get(termID));
				WCD[i] += Word2Vec[index*layerSize+i]*weight;
			}
		}
		double Scalar = 0.0;
		for(int j = 0;j<layerSize;j++) Scalar += WCD[j]*WCD[j];
		for(int k = 0;k<layerSize;k++) WCD[k] = WCD[k]/Math.sqrt(Scalar);		
		return WCD;
	}
	public static double[] documentVector(Word2Vec vec, HashMap<String, Double> tfidfVector){
		int layerSize = vec.getLayerSize();
		double [] WCD = new double [layerSize];
		double [] Word2Vec = vec.getWord2VecModel();
		for(String term : tfidfVector.keySet())
		{
			int index = vec.containWord(term);
			if(index==-1)
				continue;
			double weight = tfidfVector.get(term);
			for(int i=0;i<layerSize;i++)
			{
				WCD[i] += Word2Vec[index*layerSize+i]*weight;
			}
		}
		double Scalar = 0.0;
		for(int j = 0;j<layerSize;j++) Scalar += WCD[j]*WCD[j];
		for(int k = 0;k<layerSize;k++) WCD[k] = WCD[k]/Math.sqrt(Scalar);		
		return WCD;
	}
	public static double vectorSim(double[] v1, double[] v2){
		double sim = 0.0;
		for(int i=0;i<v1.length;i++)
			sim +=v1[i]*v2[i];
		return sim;
	}
	public static ArrayList<String> VectorNearest(Word2Vec vec, double[] v1, int num){
		ArrayList<WordNSim> nearestWords= new ArrayList<WordNSim>();
		ArrayList<String> nearestFinal= new ArrayList<String>();
		double [] Word2Vec = vec.getWord2VecModel();
		for(int i = 0;i<vec.getVocaSize();i++){
			double sim = 0.0;
			for(int j =0;j<vec.getLayerSize();j++){
				sim += Word2Vec[i*vec.getLayerSize()+j]
						*v1[j];
			}
			WordNSim wns = new WordNSim(vec.getRealWord(i), sim);
			nearestWords.add(wns);
		}
		SortArray(nearestWords);
		for(int i=0;i<num;i++)
			nearestFinal.add(nearestWords.get(i).getWord());
		return nearestFinal;
	}
	public static void SortArray(ArrayList<WordNSim> list){
		
		Comparator<WordNSim> sort = new Comparator<WordNSim>(){
	
			@Override
			public int compare(WordNSim o1, WordNSim o2) {
				// TODO Auto-generated method stub
				return ((Double)o2.getSim()).compareTo(((Double)o1.getSim()));
			}
		};
		Collections.sort(list,sort);
	}
	public static double[] getVector(Word2Vec vec, String word){
		int layerSize = vec.getLayerSize();
		double [] wordVector = new double [layerSize];
		double [] Word2Vec = vec.getWord2VecModel();
		for(int i =0;i<layerSize;i++)
			wordVector[i] = Word2Vec[layerSize*containWord(vec, word)+i]; 
		return wordVector;
	}
	public static void lowerCase(){
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			for(int docNum=1;docNum<100;docNum++)
			{
				reader = new BufferedReader(new FileReader(Constants_km.CORPUS_NAME+Integer.toString(docNum)+Constants_km.CORPUS_NAME2));
				writer = new BufferedWriter(new FileWriter(Constants_km.CORPUS_NAME+Integer.toString(docNum)+Constants_km.CORPUS_NAME2+".txt"));
				String line = reader.readLine();
				while ( line != null ) {
					writer.write(line.toLowerCase()+"\n");
					line = reader.readLine();
			    }
			}
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            try { if (reader!=null) reader.close();writer.close(); } catch (Exception e) {}
        }
	}
}

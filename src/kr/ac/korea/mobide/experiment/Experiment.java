package kr.ac.korea.mobide.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import kr.ac.korea.mobide.classifier.KNNClassifier;
import kr.ac.korea.mobide.util.Constants;
import kr.ac.korea.mobide.util.DocAnswer;
import kr.ac.korea.mobide.util.ScoreData;
import kr.ac.korea.mobide.util.Util;





public class Experiment {
	KNNClassifier classifier;
	private HashMap<DocAnswer, double[]> docVector;
	private HashMap<Integer, HashSet<DocAnswer>> tenFoldDoc;
	//CentroidClassifier classifier2;
	@SuppressWarnings("unchecked")
	public Experiment(String filePath){
		classifier = new KNNClassifier("");
		this.docVector = (HashMap<DocAnswer, double[]>) Util.deserialize(filePath, Constants.FILE_MAP_DOCUMENT_VECTOR);
		this.tenFoldDoc = new HashMap<Integer, HashSet<DocAnswer>>();
	}
	public void tenFoldVal(){
		Random rd = new Random(System.currentTimeMillis());
		for(DocAnswer da : docVector.keySet())
		{
			int index = rd.nextInt(10);
			if(!tenFoldDoc.containsKey(index))
			{
				HashSet<DocAnswer> docSet = new HashSet<DocAnswer>();
				docSet.add(da);
				tenFoldDoc.put(index, docSet);
			}
			else
			{
				tenFoldDoc.get(index).add(da);
				tenFoldDoc.replace(index, tenFoldDoc.get(index));
			}
		}
	}
	
	public void tenFoldTest(){
		this.tenFoldVal();
		double totalAccuracy = 0.0;
		int knn = 3;
		for(int index:this.tenFoldDoc.keySet())
		{
			double accuracy = 0.0;
			for(DocAnswer da: this.tenFoldDoc.get(index))
			{
				ArrayList<ScoreData> listScore = classifier.topK(da, knn, this.tenFoldDoc.get(index));
				if(listScore.get(0).getID()==listScore.get(0+knn).getID())
				{
					if(listScore.get(0).getID()==da.getAnswer())
						accuracy +=1.0;
				}
				else
				{
					if(listScore.get(0+knn).getID()==da.getAnswer())
						accuracy+=1.0;
				}
			}
			accuracy = accuracy/this.tenFoldDoc.get(index).size();
			System.out.println(index +" accuracy: "+accuracy);
			totalAccuracy += accuracy;
		}
		totalAccuracy = totalAccuracy/10;
		System.out.println("total accuracy: "+totalAccuracy);
		
	}
	public void sampleTest(){
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Experiment e = new Experiment("");
		e.tenFoldTest();
	}

}

package kr.ac.korea.mobide.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import kr.ac.korea.mobide.util.DocAnswer;
import kr.ac.korea.mobide.util.ScoreData;

public abstract class Classifier {
	public ArrayList<ScoreData> getSortedListScore(String query) {
		HashMap<Integer, Double> mapCIDScore = this.getMapCIDScore(query);
		ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
		for (int cid : mapCIDScore.keySet()) {
			listScore.add(new ScoreData(cid, mapCIDScore.get(cid)));
		}
		mapCIDScore.clear();
		Collections.sort(listScore);
		return listScore;
	}
	
	public ArrayList<ScoreData> topK(int k, String query) {
		ArrayList<ScoreData> listScore = this.getSortedListScore(query);
		if (k >= listScore.size()) {
			return listScore;
		} else {
			ArrayList<ScoreData> listTopK = new ArrayList<ScoreData>();
			for (int index = 0; index < k; index++) {
				listTopK.add(listScore.get(index));
			}
			listScore.clear();
			return listTopK;
		}
	}
	public ArrayList<ScoreData> topK(DocAnswer id, int knn, HashSet<DocAnswer> testSet) {
		HashMap<DocAnswer, Double> mapDIDScore = this.getMapDIDScore(id, knn, testSet);
		// document ranking
		ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
		for (DocAnswer docAns : mapDIDScore.keySet()) {
			listScore.add(new ScoreData(docAns, mapDIDScore.get(docAns)));
		}
		mapDIDScore.clear();
		Collections.sort(listScore);
		//k-nearest neighbor
		ArrayList<ScoreData> listTopK = new ArrayList<ScoreData>();
		for (int index = 0; index < knn; index++) {
			listTopK.add(listScore.get(index));
		}
		listScore.clear();
		//class selection
		//count base
		HashMap<Integer, Integer> classCount = new HashMap<Integer, Integer>();
		//distance base
		HashMap<Integer, Double> averageDistance = new HashMap<Integer, Double>();
		for(ScoreData sd:listTopK)
		{
			if(!classCount.containsKey(sd.getDocAnswer().getAnswer()))
				classCount.put(sd.getDocAnswer().getAnswer(), 1);
			else
				classCount.put(sd.getDocAnswer().getAnswer(), classCount.get(sd.getDocAnswer().getAnswer())+1);
			if(!averageDistance.containsKey(sd.getDocAnswer().getAnswer()))
			{
				averageDistance.put(sd.getDocAnswer().getAnswer(), sd.getScore());
			}
			else
			{
				averageDistance.put(sd.getDocAnswer().getAnswer(), averageDistance.get(sd.getDocAnswer().getAnswer())+sd.getScore());
			}
			
		}
		
		ArrayList<ScoreData> listCount = new ArrayList<ScoreData>();
		for (int answer:classCount.keySet()) {
			listCount.add(new ScoreData(answer, classCount.get(answer)));
		}
		Collections.sort(listCount);
		
		ArrayList<ScoreData> listDistance = new ArrayList<ScoreData>();
		for(int answer:averageDistance.keySet()){
			listDistance.add(new ScoreData(answer, averageDistance.get(answer)/classCount.get(answer)));
		}
		Collections.sort(listDistance);
		listCount.addAll(listDistance);
		return listCount;
	}
	
	protected abstract HashMap<Integer, Double> getMapCIDScore(String query);
	protected abstract HashMap<Integer, Double> getMapCIDScore(int id);
	protected abstract HashMap<DocAnswer, Double> getMapDIDScore(DocAnswer id, int knn, HashSet<DocAnswer>testSet);
}


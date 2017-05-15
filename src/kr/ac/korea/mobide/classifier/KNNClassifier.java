package kr.ac.korea.mobide.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import kr.ac.korea.mobide.util.Constants;
import kr.ac.korea.mobide.util.DocAnswer;
import kr.ac.korea.mobide.util.ScoreData;
import kr.ac.korea.mobide.util.Util;

import kr.ac.korea.mobide.word2vec_km.Word2VecUtil_km;

public class KNNClassifier extends Classifier {
	private HashMap<DocAnswer, double[]> docVector;
	
	@SuppressWarnings("unchecked")
	public KNNClassifier(String filePath) {
		this.docVector = (HashMap<DocAnswer, double[]>) Util.deserialize(filePath, Constants.FILE_MAP_DOCUMENT_VECTOR);
		}
	@Override
	protected HashMap<Integer, Double> getMapCIDScore(String query) {
		HashMap<Integer, Double> mapCIDScore = new HashMap<Integer, Double>();
		return mapCIDScore;
	}
	
	@Override
	protected HashMap<Integer, Double> getMapCIDScore(int k) {
		HashMap<Integer, Double> mapCIDScore = new HashMap<Integer, Double>();
		return mapCIDScore;
	}
	
	@Override
	protected HashMap<DocAnswer, Double> getMapDIDScore(DocAnswer id, int knn, HashSet<DocAnswer> testSet) {
		HashMap<DocAnswer, Double> mapDIDScore = new HashMap<DocAnswer, Double>();
		for (DocAnswer docAns : this.docVector.keySet()) {
			if(testSet.contains(id))
				continue;
			mapDIDScore.put(docAns, Word2VecUtil_km.vectorSim(this.docVector.get(docAns), this.docVector.get(id)));
		}
		
		return mapDIDScore;
	}
}

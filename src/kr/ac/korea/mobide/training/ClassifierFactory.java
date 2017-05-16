package kr.ac.korea.mobide.training;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.korea.mobide.util.Constants;
import kr.ac.korea.mobide.util.DocAnswer;
import kr.ac.korea.mobide.util.Util;






public class ClassifierFactory {
	private ArrayList<HashMap<Integer,HashMap<String, Double>>> vsm;
	private HashMap<DocAnswer, double[]> docVector;
	
	public ClassifierFactory() throws IOException {
		long begin, end;
		
		begin = System.currentTimeMillis();
		this.vsm = ClassifierUtil.getTFIDFVector();
		end = System.currentTimeMillis();
		System.out.printf("Create tf-idf vectors for all document: %.2f min.\n", (end-begin)/60000.0);
		
		begin = System.currentTimeMillis();
		this.docVector = ClassifierUtil.makeDocVector(this.vsm);
		this.vsm.clear();
		end = System.currentTimeMillis();
		System.out.printf("Create document vector for each document: %.2f min.\n", (end-begin)/60000.0);
	}
	
	public void exportIntoFile(String filePath) {
		Util.serialize(filePath, Constants.FILE_MAP_DOCUMENT_VECTOR, this.docVector);
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ClassifierFactory cf = new ClassifierFactory(); 
		cf.exportIntoFile("");
	}	
}

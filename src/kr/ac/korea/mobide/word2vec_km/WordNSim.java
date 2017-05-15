package kr.ac.korea.mobide.word2vec_km;

public class WordNSim {
	private String word;
	private double sim;
	
	public WordNSim(String word, double sim){
		this.word = word;
		this.sim = sim;
	}
	public String getWord(){
		return this.word;
	}
	public double getSim(){
		return this.sim;
	}
}

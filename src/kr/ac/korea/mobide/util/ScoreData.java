package kr.ac.korea.mobide.util;

public class ScoreData implements Comparable<ScoreData> {
	public ScoreData(int id, double score) {
		this.id = id;
		this.score = score;
	}
	public ScoreData(DocAnswer da, double score) {
		this.da = da;
		this.score = score;
	}
	public DocAnswer getDocAnswer(){
		return this.da;
	}
	public int getID() {
		return this.id;
	}
	
	public double getScore() {
		return this.score;
	}
	
	public int compareTo(ScoreData other) {
		if (this.score > other.score) return -1;
		else if (this.score < other.score) return 1;
		else return 0;
	}
	
	private int id;
	private DocAnswer da;
	private double score;
}

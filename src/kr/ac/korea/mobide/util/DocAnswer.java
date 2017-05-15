package kr.ac.korea.mobide.util;

public class DocAnswer {
	private int id;
	private int answer;
	public DocAnswer(int id, int answer){
		this.id =id;
		this.answer = answer;
	}
	public int getID(){
		return this.id;
	}
	public int getAnswer(){
		return this.answer;
	}
}

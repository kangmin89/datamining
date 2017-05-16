package kr.ac.korea.mobide.util;

import java.io.Serializable;

public class DocAnswer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7273171648148546374L;
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

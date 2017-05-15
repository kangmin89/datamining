package kr.ac.korea.mobide.word2vec_km;


public class TrainModel {

	
 
	public void ModelTraining(){
		
		                     //cbow,negative,layer,iter,alpha,window_size)
		//Word2Vec wv = new Word2Vec(true, 15, 100, 20, 0.025, 5);
		Word2Vec wv = new Word2Vec(false, 15, 300, 5, 0.025, 5);
		wv.NNTraining();
		Word2VecSerializer.SaveWord2Vec(wv);
		System.out.println("boat-ship similarity : "+wv.WordSim("boat", "ship"));
		
	}
	public void ModelLoad(boolean vanilla){
		Word2Vec wv = null;
		wv = Word2VecSerializer.ReadWord2Vec();
		System.out.println("one-zero similarity : "+wv.WordSim("one", "zero"));
	
		wv.NearestWord("president", 20);
		wv.NearestWord("obama", 20);
		wv.NearestWord("boat", 20);

		System.out.println("pres president : "+wv.WordSim("pres", "president"));
		System.out.println("melbourne sydney : "+wv.WordSim("melbourne", "sydney"));
		wv.EvaluationAnalogy(vanilla);
		/*System.out.println("kfc chicken : "+wv.WordSim("peace", "war", 100));
		System.out.println("kfc pizza : "+wv.WordSim("kfc", "bbq", 100));
		System.out.println("apple google : "+wv.WordSim("kfc", "kentucky", 100));*/
	}

	public static void main(String[] args){
		// TODO Auto-generated method stub
		TrainModel tm = new TrainModel();
//		tm.ModelTraining();
		tm.ModelLoad(false);
//		Word2VecUtil_km.lowerCase();
//		Word2VecUtil_km.ODPCorpus();
	}

}

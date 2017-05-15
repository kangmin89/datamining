package kr.ac.korea.mobide.word2vec_km;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;



public class Word2Vec implements Serializable{
	/**
	 *word2vec.c
	 *starting_alpha 0.001-->0.025
	 *
	 */
	private static final long serialVersionUID = -6376232365958411470L;
	private boolean cbow;
	private int negative;
	private int layer_size;
	private int voca_size;
	private int iter;
	private double[]syn0;
	private double[]syn1;
	private double[]expTable;
	private double alpha;
	private int window_size;
	private LearnVoca lv = new LearnVoca();
	private UnigramDistribution ud = new UnigramDistribution();
	
	public Word2Vec(boolean cbow, int negative, int layer_size, int iter, double alpha, int window_size){
		this.cbow = cbow;
		this.negative = negative;
		this.layer_size = layer_size;
		this.iter = iter;
		this.alpha = alpha;
		this.window_size = window_size;
		this.expTable = new double [Constants_km.EXP_TABLE_SIZE];
	}
	public int getLayerSize(){
		return this.layer_size;
	}
	public double[] getWord2VecModel(){
		return this.syn0;
	}
	public int containWord(String word){
		return ud.getWIndex(word); 
	}
	public int getVocaSize(){
		return this.voca_size;
	}
	public String getRealWord(int index){
		return ud.getRealWord(index);
	}
	public void ConstructVoca(){
		File fv = new File("Voca.txt");
		if(fv.exists())
		{
			lv.setVoca(VocaSerializer.ReadVoca());
		}
		else
		{
			lv.LearnVocaFromTrainFile();
			VocaSerializer.SaveVoca(lv);
			lv.PrintVoca();
		}
		ud.InitUnigramTable(lv.getVoca());
		this.voca_size = lv.getVoca().size();
		this.syn0 = new double[voca_size*this.layer_size];
		this.syn1 = new double[voca_size*this.layer_size];
		/*this.ODPsyn0 = new double[voca_size*this.layer_size*this.mapACIDIndex.size()];
		this.ODPsyn1 = new double[voca_size*this.layer_size*this.mapACIDIndex.size()];	*/
	}
	public void InitNet(){
		Random rd = new Random(System.currentTimeMillis());
		for(int i=0;i<voca_size;i++) for(int j=0;j<this.layer_size;j++){
			double random = Math.abs(rd.nextDouble());
			syn1[i*this.layer_size + j] = 0;//random2-0.5;
			syn0[i*this.layer_size + j] = random-0.5;
		}
		/*for(int k=0;k<this.mapACIDIndex.size();k++)for(int i=0;i<voca_size;i++) for(int j=0;j<this.layer_size;j++){
			double random = Math.abs(rd.nextDouble());
			ODPsyn1[k*this.layer_size*this.voca_size+i*this.layer_size + j] = 0;//random2-0.5;
			ODPsyn0[k*this.layer_size*this.voca_size+i*this.layer_size + j] = random-0.5;
		}*/
	}
	public int ReadWordIndex(String word){
		if(ud.getWIndex(word)!=-1) return ud.getWIndex(word);
		else return -1;
	}
	public void ConstructExpTable(){
		for(int i =0; i < Constants_km.EXP_TABLE_SIZE;i++)
		{
			this.expTable[i] = Math.exp((i/(double)Constants_km.EXP_TABLE_SIZE*2-1)*Constants_km.MAX_EXP);
			this.expTable[i] = this.expTable[i] / (this.expTable[i] + 1);
		}
	}
	public void Normalization(){
		double length = 0.0;
		for(int i =0;i<this.voca_size*this.layer_size;i++){
			length +=this.syn0[i]*this.syn0[i];
			//System.out.println(i+" : "+this.syn0[i]);
			if(i%this.layer_size==this.layer_size-1)
			{
				length = Math.sqrt(length);
				for(int j = i-this.layer_size+1;j<=i;j++){
					this.syn0[j] = this.syn0[j]/length;
				}
				length = 0.0;
				//System.out.println(i+" : "+this.syn0[i]);
			}
		}
		length = 0.0;
		/*for(int k=0;k<this.mapACIDIndex.size();k++)for(int i =0;i<this.voca_size*this.layer_size;i++){
			length +=this.ODPsyn0[k*this.layer_size*this.voca_size+i]*this.ODPsyn0[k*this.layer_size*this.voca_size+i];
			//System.out.println(i+" : "+this.syn0[i]);
			if(i%this.layer_size==this.layer_size-1)
			{
				length = Math.sqrt(length);
				for(int j = k*this.layer_size*this.voca_size+i-this.layer_size+1;j<=k*this.layer_size*this.voca_size+i;j++){
					this.ODPsyn0[j] = this.ODPsyn0[j]/length;
				}
				length = 0.0;
				//System.out.println(i+" : "+this.syn0[i]);
			}
		}*/
	}
	public double WordSim(String w1, String w2){
		double sim = 0.0;
		//System.out.println(this.layer_size);
		for(int i =0;i<this.layer_size;i++){
			//System.out.println(this.syn0[this.ud.getWIndex(w1)*this.layer_size+i]);
			sim += this.syn0[this.ud.getWIndex(w1)*this.layer_size+i]
					*this.syn0[this.ud.getWIndex(w2)*this.layer_size+i];
		}

		
		return sim;
	}
	public String WordAnalogy(String w1, String w2, String w3, boolean vanilla, HashMap<String, ArrayList<WordNSim>> mapAnalogyQA){
		double []analVec = new double[this.layer_size];
		if(ud.getWIndex(w1)!=-1 && ud.getWIndex(w2)!=-1 && ud.getWIndex(w3)!=-1)
		{
			double normal = 0.0;
			for(int i =0;i<this.layer_size;i++){
				analVec[i]= this.syn0[this.ud.getWIndex(w1)*this.layer_size+i]-this.syn0[this.ud.getWIndex(w2)*this.layer_size+i]
					+ this.syn0[this.ud.getWIndex(w3)*this.layer_size+i];
				normal += analVec[i]*analVec[i];
			}
			Math.sqrt(normal);
			ArrayList<WordNSim> nearestWords= new ArrayList<WordNSim>();
			for(int i = 0;i<this.voca_size;i++){
				double sim = 0.0;
				for(int j =0;j<this.layer_size;j++){
					sim += this.syn0[i*this.layer_size+j]*analVec[j]/normal;
				}
				WordNSim wns = null;
				if(vanilla)
				{
					wns = new WordNSim(ud.getRealWord(i), sim);
				}
				else
				{
					if(!ud.getRealWord(i).equals(w1) && !ud.getRealWord(i).equals(w2) && !ud.getRealWord(i).equals(w3))
					{
						wns = new WordNSim(ud.getRealWord(i), sim);
					}
					else
					{
						continue;
					}
				}
				nearestWords.add(wns);
				if(i>=4)
				{
					SortArray(nearestWords);
					nearestWords.remove(4);
				}
			}
			mapAnalogyQA.put(w1+" - "+w2+" + "+w3, nearestWords);
			return nearestWords.get(0).getWord();
		}
		else
		{
			return "";
		}
	}
	public void EvaluationAnalogy(boolean vanilla){
		BufferedReader reader = null;
		HashMap<String, ArrayList<WordNSim>> mapAnalogyQA = new HashMap<String, ArrayList<WordNSim>>();
		int accuracy =0;
		int total =0;
		try {
			reader = new BufferedReader(new FileReader("questions-words.txt"));
			String line = reader.readLine();
			line = reader.readLine();//첫 줄 버려
			while ( line != null ) {// Print read line
		        StringTokenizer st = new StringTokenizer(line, " ");
		        while (st.hasMoreTokens() && st.countTokens()==4) {
		        	
		        	String a  = st.nextToken().toLowerCase();
		          	String b = st.nextToken().toLowerCase();
		          	//System.out.println(subtrahend);
		          	String c = st.nextToken().toLowerCase();
		          	String d = st.nextToken().toLowerCase();
		     
		          	if(WordAnalogy(a, c, d, vanilla, mapAnalogyQA).equals(b)) accuracy++;
		        }
				// Read next line for while condition
				line = reader.readLine();
				total++;
			}
			System.out.println("Accuracy : " + accuracy/(double)total);
			System.out.println(total);
			printAnalogy(mapAnalogyQA);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            try { if (reader!=null) reader.close(); } catch (Exception e) {}
        }
	}
	public void printAnalogy(HashMap<String, ArrayList<WordNSim>> mapAnalogyQA) throws IOException{
		FileWriter fw = new FileWriter("Analogy.txt");
		try{
			Iterator<String> questions = mapAnalogyQA.keySet().iterator();
			while(questions.hasNext()){
			String q = questions.next();
			
			fw.write(q + " = ");
			fw.write("\n");
			for(int i = 0;i<mapAnalogyQA.get(q).size();i++)
				fw.write(Integer.toString(i+1) + ". "+ mapAnalogyQA.get(q).get(i).getWord() + " " );
			fw.write("\n");
			}		
			
		}catch(Exception e){
			e.printStackTrace(System.err);
		}finally{
			try { if (fw!=null) fw.close(); } catch (Exception e) {}
		}
		
	}
	public void SortArray(ArrayList<WordNSim> list){
		
		Comparator<WordNSim> sort = new Comparator<WordNSim>(){
	
			@Override
			public int compare(WordNSim o1, WordNSim o2) {
				// TODO Auto-generated method stub
				return ((Double)o2.getSim()).compareTo(((Double)o1.getSim()));
			}
		};
		Collections.sort(list,sort);
	}
	public ArrayList<WordNSim> NearestWord(String word, int num){
		//for(int cat:this.mapACIDIndex.keySet()) System.out.println(cat+" : "+this.mapACIDIndex.get(cat));
		
		ArrayList<WordNSim> nearestWords= new ArrayList<WordNSim>();
		ArrayList<WordNSim> nearestFinal= new ArrayList<WordNSim>();

		for(int i = 0;i<this.voca_size;i++){
			double sim = 0.0;
			for(int j =0;j<this.layer_size;j++){
				sim += this.syn0[i*this.layer_size+j]
						*this.syn0[this.ud.getWIndex(word)*this.layer_size+j];
			}
			WordNSim wns = new WordNSim(ud.getRealWord(i), sim);
			nearestWords.add(wns);
		}
		SortArray(nearestWords);
		for(int i =0;i<num;i++)
		{
			nearestFinal.add(nearestWords.get(i));
		}
		for(WordNSim wordNsim : nearestFinal){
			System.out.print(wordNsim.getWord()+", ");
		}
		System.out.println(" ");
		return nearestFinal;
	
		
	}
	public void NNTraining(){
		ConstructVoca();
		InitNet();
		ConstructExpTable();
		int a = 0;
		int b = 0;
		int c = 0;
		int d = 0;
		double f = 0.0;
		double g = 0.0;
		int l1 = 0;
		int l2 = 0;
		int cw = 0;
		int sentence_length = 0;
		int sentence_position = 0;
		int word_count = 0;
		int last_word_count = 0;
		double word_count_actual =0.0;
		String ODPword = "";
		int word =0;
		int target = 0;
		int last_word=0;
		double sample = 1e-3; 
		double starting_alpha = this.alpha;//1e-3;
		int train_words = 0;
		int local_iter = this.iter;
		int label =0;
		String line = "";
		StringTokenizer st;
		StringTokenizer st2;
		double []neural = new double [this.layer_size];
		double []neuralE = new double [this.layer_size];
		String []ODPsen = new String [Constants_km.MAX_SENTENCE_LENGTH];
		int []sen = new int [Constants_km.MAX_SENTENCE_LENGTH];
		int docNum = 0;
		Random rd = new Random(System.currentTimeMillis());
		BufferedReader reader = null;
		train_words = this.lv.TrainWords();
		System.out.println("Train_words" + train_words);
		System.out.println("Voca size" + this.voca_size);
		/********************read word*****************************/
		try {
			reader = new BufferedReader(new FileReader(Constants_km.CORPUS_NAME+Integer.toString(docNum)+Constants_km.CORPUS_NAME2));
			line = reader.readLine();
			st = new StringTokenizer(line, "\t");
			st2 = new StringTokenizer(st.nextToken(), " ");
			while(true)
			{
				if(word_count - last_word_count > 100000){
					word_count_actual += (double) (word_count - last_word_count);
					last_word_count = word_count;
					System.out.println("Alpha : " + alpha +" Progress : "+ (word_count_actual/100) / (double)(iter * (train_words/100)) * 100);
					//System.out.println("word_count_actual : "+word_count_actual);
					//System.out.println("(double)(iter * train_words + 1) : " + (double)(iter * train_words + 1));
					alpha = starting_alpha * (1 - (word_count_actual/100) / (double)(iter * (train_words/100)));
				    if (alpha < starting_alpha * 0.0001) alpha = starting_alpha * 0.0001;
				}
				if (sentence_length == 0) {
					while(true)
					{
						if(st2.hasMoreTokens())
						{
							ODPword = st2.nextToken();
							word = ReadWordIndex(ODPword);
							
						}
						else
						{
							if (st.hasMoreTokens()) 
							{
								st2 = new StringTokenizer(st.nextToken(), " ");
								ODPword = st2.nextToken();
								word = ReadWordIndex(ODPword);
								
							}
							else
							{
								line = reader.readLine();
								if(line == null) break;
								st = new StringTokenizer(line, "\t");
								st2 = new StringTokenizer(st.nextToken(), " ");
								ODPword = st2.nextToken();
								word = ReadWordIndex(ODPword);
								
							}
						}
						if(line == null) break;
						if (word == -1) continue;
				        word_count++;
				        if (word == ud.getWIndex(".")) break;
				        // The subsampling randomly discards frequent words while keeping the ranking same
				        if (sample > 0) {
				          double ran = (Math.sqrt(lv.getVoca().get(ud.getRealWord(word)) / (sample * train_words)) + 1) * (sample * train_words) / lv.getVoca().get(ud.getRealWord(word));
				          double random = Math.abs(rd.nextDouble());
				          if (ran < random) continue;
				        }
				        sen[sentence_length] = word;
				        ODPsen[sentence_length] = ODPword;
				        //System.out.println(word);
				        sentence_length++;
				        if (sentence_length >= Constants_km.MAX_SENTENCE_LENGTH) break;
					}
					sentence_position = 0;
				}
				if(line == null)
				{
					docNum++;
					if(docNum==100)
					{
						local_iter--;
						docNum=0;
					}
					reader = new BufferedReader(new FileReader(Constants_km.CORPUS_NAME+Integer.toString(docNum)+Constants_km.CORPUS_NAME2));
					word_count_actual += (double)(word_count - last_word_count);
					if(local_iter == 0) break;
					word_count = 0;
					last_word_count = 0;
					sentence_length = 0;
					continue;
				}
				//System.out.println(sentence_length);
				word = sen[sentence_position];
				if(word == -1) continue;
				//System.out.println(word);
				for(c = 0; c < this.layer_size; c++) neural[c] = 0;
				for(c = 0; c < this.layer_size; c++) neuralE[c] = 0;
				b = rd.nextInt(this.window_size);
				//System.out.println(b);
				/***********************************cbow negative-sampling********************/
				//odpweight = Math.pow(mapCIDMC.get(482442).getWeight((mapTermTermID.get(ud.getRealWord(word))))/mapCIDScalar.get(482442),1.0/10.0);
				if(cbow)//train the topic-specific cbow architecture
				{
					//in -> hidden
					cw = 0;
					for(a = b; a<this.window_size*2 + 1 -b; a++)if(a != this.window_size){
						c = sentence_position - this.window_size + a;
						if(c<0) continue;
						if(c >=sentence_length) continue;
						last_word = sen[c];
						//System.out.println(ud.getRealWord(last_word));
						if(last_word == -1) continue;
						for(c=0;c<this.layer_size;c++){
							neural[c] += this.syn0[c + last_word*this.layer_size];
							//System.out.println(this.syn0[c + last_word*this.layer_size]);
							//System.out.println(neural[c]);
						}
						cw++;
					}
					//negative sampling
					if(this.negative>0&&cw>0) for(d=0; d< this.negative +1; d++)
					{
						if(d==0)
						{
							target = word;
							label = 1;
							//System.out.println(ud.getRealWord(target));
						}
						else//1<d<negative
						{
							target = rd.nextInt(lv.getVoca().size()-1);
							if(target == ud.getWIndex(".")) target = rd.nextInt(lv.getVoca().size()-1); 
							if(target == word) continue;
							label =0;
						}
						l2 = target * this.layer_size;
						f = 0.0;
						for(c = 0;c<this.layer_size;c++){
							//System.out.println(neural[c]);
							//System.out.println(this.syn1[c+l2]);
							f += neural[c] * this.syn1[c+l2];
						}
						//System.out.println(f);
						if(f > Constants_km.MAX_EXP)
						{
							g = (label-1) * alpha;
							//System.out.println(g);
						}
						else if(f<-Constants_km.MAX_EXP)
						{
							g = (label-0) * alpha;
							//System.out.println(g);
						}
						else
						{ 
							g = (label-expTable[(int)((f+Constants_km.MAX_EXP)*(Constants_km.EXP_TABLE_SIZE/Constants_km.MAX_EXP/2))]) * alpha;
							//System.out.println(g);
						}
						for(c=0;c<this.layer_size;c++) neuralE[c] += g * this.syn1[c+l2];
						for(c=0;c<this.layer_size;c++) this.syn1[c+l2] += g*neural[c];
					}
					for(a = b; a <this.window_size*2 + 1 -b;a++) if(a != this.window_size){
						c = sentence_position - this.window_size +a;
						if(c<0) continue;
						if(c >=sentence_length) continue;
						last_word = sen[c];
						if(last_word==-1) continue;
						for(c=0; c < this.layer_size;c++) this.syn0[c + last_word*this.layer_size] += neuralE[c];
					}
				}
				else
				{
					//in -> hidden
					cw = 0;
					for(a = b; a<this.window_size*2 + 1 -b; a++)if(a != this.window_size){
						c = sentence_position - this.window_size + a;
						if(c<0) continue;
						if(c >=sentence_length) continue;
						last_word = sen[c];
						//System.out.println(ud.getRealWord(last_word));
						if(last_word == -1) continue;
						for(c=0;c<this.layer_size;c++){
							neural[c] += this.syn0[c + last_word*this.layer_size];
							//System.out.println(this.syn0[c + last_word*this.layer_size]);
							//System.out.println(neural[c]);
						}
						cw++;
					}
					//negative sampling
					if(this.negative>0&&cw>0) for(d=0; d< this.negative +1; d++)
					{
						for(a = b; a< this.window_size * 2 + 1 - b; a++) if(a != this.window_size){
							c = sentence_position - this.window_size + a;
							if(c < 0) continue;
							if(c >= sentence_length) continue;
							last_word = sen[c];
							if(last_word == -1) continue;
							//System.out.println(ud.getRealWord(last_word));
							l1 = last_word * this.layer_size;
							for(c=0; c < this.layer_size; c++) neuralE[c] = 0;
							//negative sampling
							if(this.negative>0) for(d=0; d< this.negative +1; d++)
							{
								if(d == 0)
								{
									target = word;
									label = 1;
									//System.out.println(ud.getRealWord(target));
								}
								else
								{
									target = rd.nextInt(lv.getVoca().size()-1);
									if(target == ud.getWIndex(".")) target = rd.nextInt(lv.getVoca().size()-1); 
									if(target == word) continue;
									label =0;
								}
								l2 = target * this.layer_size;
								f = 0;
								for(c = 0; c < this.layer_size; c++) f += syn0[c + l1] * syn1[c + l2];
								if(f > Constants_km.MAX_EXP) g = (label -1) * alpha;
								else if (f < -Constants_km.MAX_EXP) g = (label - 0) * alpha;
								else g = (label-expTable[(int)((f+Constants_km.MAX_EXP)*(Constants_km.EXP_TABLE_SIZE/Constants_km.MAX_EXP/2))]) * alpha;
								for(c = 0; c < this.layer_size; c++) neuralE[c] += g * syn1[c + l2];
								for(c = 0; c < this.layer_size; c++) syn1[c+l2] += g * syn0[c + l1];
							}
							for(c = 0; c < this.layer_size; c++) syn0[c+l1] += neuralE[c];
						}
				}
				
				sentence_position++;
				if(sentence_position >= sentence_length){
					sentence_length=0;
					continue;
				}
			}
		}
		} catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            try { if (reader!=null) reader.close(); } catch (Exception e) {}
        }
		Normalization();
	}
}

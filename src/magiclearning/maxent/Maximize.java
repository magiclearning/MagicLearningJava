package magiclearning.maxent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import data.Text;
import magiclearning.Dataset;
import magiclearning.Element;
import magiclearning.Model;
import magiclearning.Settings;
import tools.CSVGenerator;
import tools.Chrono;
import tools.Statistics;

public class Maximize<RawDataType, ResultDataType,FeatureType extends Comparable<FeatureType>> extends Learning<FeatureType> {
	/** écart-type maximum en dessous duquel l'entrainement est stoppé (sur une echelle de 0 à 100 ) */
	protected static float STD_DEV_LIMIT = Settings.STD_DEV_LIMIT;
	/** nombre de résultats utilisés pour cacluler l'écart-type de la condition d'arrêt de l'apprentissage */
	protected static int STOP_VAL = Settings.STOP_VAL;
	/** nombre d'itérations maximum pour l'apprenstissage */
	private int maxIter;
	private Dataset<Element<RawDataType, ResultDataType, FeatureType>> data;
	private Model<FeatureType,ResultDataType> model;
	
	private CSVGenerator statistics;
	
	/**
	 * Maximize basic constructor
	 * Assuming model and data are entirely loaded
	 * @param model
	 * @param data
	 * @param maxIteration
	 */
	public Maximize(Model<FeatureType,ResultDataType> model, Dataset<Element<RawDataType, ResultDataType, FeatureType>> data, int maxIteration) {
		this.model = model;
		maxIter = maxIteration;
		this.data = data;
		statistics = new CSVGenerator();
		statistics.addLine("Statistics on training");
		statistics.addLine("Iter", "Error (nb)", "Error Rate", "StdDev", "StdDev %", "Time");
		
	}
	
	/**
	 * Start learning and save model
	 */
	@Override
	public void run() {		
		//learning
		learn();
		//save model
		try {
			System.out.println("Save Model");
			model.save(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Apprentissage:
	 * 
	 */
	private void learn() {
		System.out.println(">>> Start Learning on " + data.getName());
		boolean stop = false;
		ArrayList<Integer> nbError = new ArrayList<>(); // nombre d'erreur par itération
		int i = 1;
		float step = 1;
		int nbData = data.size();
		ResultDataType winner;
		Chrono learningTimer = new Chrono("learning");
		learningTimer.start();
		Chrono iterTimer = new Chrono("iterTimer");
		
		System.out.println("Load features ID");
		for (Element<RawDataType, ResultDataType, FeatureType> data: data.getData()) {
			data.getFeatures().loadFeaturesID(model);
		}
		System.out.println("Learn.......");
		while(!stop) {
			iterTimer.reset();
			iterTimer.start();
			int tmpError = 0;
			System.out.println("iter=" + i + " step=" + step);
			data.shuffle();
			System.out.println("shuffle: done");

			for (Element<RawDataType, ResultDataType, FeatureType> data: data.getData()) {

				winner = winner(data);
				
				data.setPrediction(winner);
				//modification des scores si erreurs AVEC FREQUENCE
				if(winner != null && !data.getResult().equals(winner)) {
					for (int id: data.getFeatures().getFeaturesID()) {
						int labelWinIndex = model.getLabels().getLabelList().indexOf(winner);
						int labelLoseIndex = model.getLabels().getLabelList().indexOf(data.getResult());
						model.setScore(labelWinIndex, id, model.getScore(labelWinIndex, id)-step);
						model.setScore(labelLoseIndex, id, model.getScore(labelLoseIndex, id)+step);
					}
					tmpError++;
				}
			}
			nbError.add(tmpError);
			
			i++;
			// reduce step weigth
			step = 1/(float)i;
			// stop condition
			stop = stopLearning(i, nbError, nbData);
			
			iterTimer.stop();
			statistics.addColumn(iterTimer.getDuration()/1000+"");
		}
		learningTimer.stop();
		chronos.add(learningTimer);
	}
	
	/** return the model's winner language for a text
	 * @param t
	 * @return
	 */
	public ResultDataType winner(Element<RawDataType, ResultDataType, FeatureType> data){
		ResultDataType winner = null;
		float winnerScore = - Float.MAX_VALUE;
		
		for (ResultDataType label : model.getLabels().getLabelList()) {
			int labelIndex = model.getLabels().getLabelList().indexOf(label);
			float tmpScore = 0;
			for (int id: data.getFeatures().getFeaturesID()) {
				tmpScore += model.getScore(labelIndex, id);
			}
			if (tmpScore >= winnerScore) {
				winner = label;
				winnerScore = tmpScore;
			}
		}
		return winner;
	}
	
	/**
	 * condition d'arret de l'apprentissage. L'apprentissage s'arrête si:
	 * - Le nombre maximum d'itération est atteint
	 * - 
	 * @param i
	 * @param nbError
	 * @return
	 */
	private boolean stopLearning(int i, ArrayList<Integer> nbError, int nbData) {
		boolean stop = false;
		float prediction = (nbError.get(nbError.size()-1)*100/nbData);
		// conditions d'arrêt écart-type
		if (nbError.size()-STOP_VAL>=0) {
			List<Integer> tmp = nbError.subList(nbError.size()-STOP_VAL, nbError.size()-1);
			Statistics stopStats = new Statistics();
			for (Integer nb: tmp) {
				stopStats.add(nb.floatValue());
			}
			if ((stopStats.StdDev()*100/nbData) < STD_DEV_LIMIT) {
				stop = true;
			}
			stopStats.max();
			stopStats.min();
			stopStats.mean();
			statistics.addLine(i-1, nbError.get(nbError.size()-1), prediction, stopStats.StdDev(), (stopStats.StdDev()*100/nbData));
		} else {
			statistics.addLine(i-1, nbError.get(nbError.size()-1), prediction, "", "");
		}
		
		if (i > maxIter) {
			stop = true;
		}
		
		
		return stop;
	}

	public static float getSTD_DEV_LIMIT() {
		return STD_DEV_LIMIT;
	}

	public static void setSTD_DEV_LIMIT(float sTD_DEV_LIMIT) {
		STD_DEV_LIMIT = sTD_DEV_LIMIT;
	}

	public static int getSTOP_VAL() {
		return STOP_VAL;
	}

	public static void setSTOP_VAL(int sTOP_VAL) {
		STOP_VAL = sTOP_VAL;
	}

	public int getMaxIter() {
		return maxIter;
	}

	public Dataset<Element<RawDataType, ResultDataType, FeatureType>> getData() {
		return data;
	}

	public Model<FeatureType, ResultDataType> getModel() {
		return model;
	}

	@Override
	public CSVGenerator report(boolean save) throws IOException {
		
		report.merge(statistics, true, false);
		report.addLineBreak();
		report.addLineBreak();
		report.addLine("","Times","Seconds");
		for (Chrono c: chronos) {
			report.addLine("",c.getName(),c.getDuration()/1000);
		}
		
		report.addLineBreak();
		report.addLine("","Stats");
		report.addLine("","Dataset", data.size());
		report.addLine("","Labels", model.getLabels().size());
		report.addLine("","Features", model.getDictionary().size());
		// save
		if (save) report.save();
		return report;
	}








}

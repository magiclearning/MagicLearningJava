package magiclearning.maxent;

import java.io.IOException;
import java.util.ArrayList;

import magiclearning.Dataset;
import magiclearning.Element;
import magiclearning.Model;
import tools.CSVGenerator;
import tools.Statistics;
/**
 * Predictor is the prediction class
 * @author Antoine
 *
 * @param <RawDataType>
 * @param <ResultDataType>
 * @param <FeatureType>
 */
public class Predictor<RawDataType, ResultDataType,FeatureType extends Comparable<FeatureType>> extends Learning<FeatureType> {

	/** le dataset utilisé pour lequel on va faire une prédiction */
	private Dataset<Element<RawDataType, ResultDataType, FeatureType>> data;
	/** le model contenant le diciotnnaire et les scores associés */
	private Model<FeatureType,ResultDataType> model;
	/** si la prediction se fait sur un échantillon avec le résultat connut */
	private boolean compare;
	/** matrice de confusion, elle permet de voir en un tableau les confusions les plus fortes */
	private int[][] confusionMatrix;
	
	private int nbError;
	
	public Predictor(Model<FeatureType,ResultDataType> model, Dataset<Element<RawDataType, ResultDataType, FeatureType>> data) {
		super();
		this.model = model;
		this.data = data;
		compare = true;
		confusionMatrix = new int[model.getLabels().size()][model.getLabels().size()];
		nbError = 0;
	}
	
	@Override
	public void run() {
		System.out.println(">>> Start Prediction on " + data.getName());
		ResultDataType winner;
		
		
		System.out.println("Load features ID");
		for (Element<RawDataType, ResultDataType, FeatureType> data: data.getData()) {
			data.getFeatures().loadFeaturesID(model);
		}
		System.out.println("Predict.......");
		for (Element<RawDataType, ResultDataType, FeatureType> data: data.getData()) {

			winner = winner(data);
			data.setPrediction(winner);
			//((data.Character)data.getRaw()).setPrediction((String)winner);
			
			if (compare && data.getResult() != null) {
				if (!data.getResult().equals(winner)) {
					// pas le bon résultat
					nbError++;
				}
				
				// confused matrix
				int win = model.getLabels().indexOf(winner);
				int origin = model.getLabels().indexOf(data.getResult());
				if (origin >= 0 && win >= 0)  {
					confusionMatrix[origin][win]++;
				}
			}
		}
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
				if (id >= 0) {
					tmpScore += model.getScore(labelIndex, id);
				}
			}
			if (tmpScore >= winnerScore) {
				winner = label;
				winnerScore = tmpScore;
			}
		}
		return winner;
	}
	
	
	public void save(String filename) throws IOException {
		data.save(filename, false);
	}

	public Dataset<Element<RawDataType, ResultDataType, FeatureType>> getData() {
		return data;
	}

	public void setData(Dataset<Element<RawDataType, ResultDataType, FeatureType>> data) {
		this.data = data;
	}

	public Model<FeatureType, ResultDataType> getModel() {
		return model;
	}

	public void setModel(Model<FeatureType, ResultDataType> model) {
		this.model = model;
	}

	public boolean isCompare() {
		return compare;
	}

	public void setCompare(boolean compare) {
		this.compare = compare;
	}

	public int[][] getConfusionMatrix() {
		return confusionMatrix;
	}

	@Override
	public CSVGenerator report(boolean save) throws IOException {
		report.addLineBreak();
		report.addLine("Prediction on sample", data.getName());
		report.addLineBreak();
		report.addLine("Dataset size",data.size());
		report.addLine("Nb of Features",model.getDictionary().size());
		
		
		if (compare) {
			// taux d'erreur
			report.addLine("Error rate", nbError*100f/data.size(), "Nb of Errors", nbError);
			report.addLineBreak();
			//matrice de confusions
			report.addLine("Confusion matrix");
			report.addLine("Ground Truth  \\ Found");
			for (int i = 0;i<confusionMatrix.length;i++) {
				report.addColumn(model.getLabels().getLabelList().get(i).toString());
			}
			report.addColumn("TOTAL");
			report.addColumn("Errors");
			for (int i = 0;i<confusionMatrix.length;i++) {
				int total=0;
				int bon_rep=0;
				report.addLine(model.getLabels().getLabelList().get(i).toString());
				for (int j = 0;j<confusionMatrix.length;j++) {
					if(i==j){
						bon_rep = confusionMatrix[i][j];
					}
					report.addColumn(""+confusionMatrix[i][j]);
					total += confusionMatrix[i][j];
				}
				report.addColumn(""+total);
				report.addColumn(""+(100*(float)(total-bon_rep)/total));
			}
			
			
			report.addLine("TOTAL");
			ArrayList<Float> errors = new ArrayList<>();
			for (int i = 0;i<confusionMatrix.length;i++) {
				int total=0;
				int bon_rep=0;
				for (int j = 0;j<confusionMatrix.length;j++) {
					if(i==j){
						bon_rep = confusionMatrix[i][j];
					}
					
					total += confusionMatrix[j][i];
				}
				report.addColumn(""+total);
				errors.add(100*(float)(total-bon_rep)/total);
			}	
			report.addLine("ERRORS");
			for (int i = 0;i<errors.size();i++) {
				report.addColumn(""+errors.get(i));
			}	
			report.addLineBreak();
		}
		if (save) report.save();
		return report;
	}

	
}

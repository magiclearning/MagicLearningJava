package magiclearning.maxent;

import java.io.IOException;
import java.util.ArrayList;

import data.Character;
import data.Corpus;
import data.DecryptorFeatures;
import data.Text;
import magiclearning.Dataset;
import magiclearning.Element;
import magiclearning.FeatureSet;
import magiclearning.FeaturesLoaderInt;
import magiclearning.Labels;
import magiclearning.Model;
import tools.CSVGenerator;
import tools.Chrono;
import tools.FileTools;

public class CrossValidator<RawDataType, ResultDataType,FeatureType extends Comparable<FeatureType>> extends Learning<FeatureType> {
	
	private ArrayList<Validator> trainings;
	private ArrayList< Dataset<Element<RawDataType, ResultDataType, FeatureType>>> dataSets;
	
	private Dataset<Element<RawDataType, ResultDataType, FeatureType>> data;
	private ArrayList<CSVGenerator> reports;
	private FeaturesLoaderInt<RawDataType, FeatureType> featuresBuilder;

	private int nbSample;
	private int sampleSize;
	private int maxIteration;
	
	public CrossValidator(Dataset<Element<RawDataType, ResultDataType, FeatureType>> data,
			FeaturesLoaderInt<RawDataType, FeatureType> featuresBuilder,
			int nbSample, 
			int maxIteration) {
		super();
		this.maxIteration = maxIteration;
		this.nbSample = nbSample;
		
		if (data.size() > nbSample) {
			this.nbSample = nbSample;
		} else if (data.size() > 10) {
			this.nbSample = 10;
		} else {
			this.nbSample = data.size();
		}
		
		trainings = new ArrayList<>();
		dataSets = new ArrayList<>();
		reports = new ArrayList<>();
		this.featuresBuilder = featuresBuilder;
		sampleSize = data.size()/this.nbSample;
		this.data = data;

	}
	
	@Override
	public void run() {
		
		for (int i = 0; i < this.nbSample; i++) {
			System.out.println("Cross validation sample: " + i);
			// create labelsand dict object
			Labels<ResultDataType> tmpLabels = new Labels<>();// liste des labels utilisés
			FeatureSet<FeatureType> tmpDictionary = new FeatureSet<>(false);// dictionnaire regroupant toutes les features
			
			// create training and predicting dataSet
			int min = sampleSize*i;
			int max = (i+1 == nbSample)?data.size()-1:sampleSize*i+sampleSize;
			Dataset<Element<RawDataType, ResultDataType, FeatureType>> tmpDataPredict = new Dataset<>(i+"_pred",data.getData().subList(min, max));
			Dataset<Element<RawDataType, ResultDataType, FeatureType>> tmpDatatraining = new Dataset<>();
			tmpDatatraining.setName(i+"_train");
			if (min > 0) {
				tmpDatatraining.getData().addAll(data.getData().subList(0, min));
			}
			if (max < data.size()) {
				tmpDatatraining.getData().addAll(data.getData().subList(max+1, data.size()));
			}
			//load features of training dataset
			System.out.println(">> Loading features and dictionary...");
			for (Element<RawDataType, ResultDataType, FeatureType> elem : tmpDatatraining.getData()) {
				elem.getFeatures().clear();
				elem.getFeatures().addAllFeatures(featuresBuilder.getFeatures(elem.getRaw()));
				tmpLabels.addLabel(elem.getResult());
				tmpDictionary.addAllFeatures(elem.getFeatures().getFeaturesList());
			}
			
			// create Model
			Model<FeatureType, ResultDataType> tmpModel = new Model<>(tmpDictionary,tmpLabels);
			tmpModel.setFilename("crossValidation.model." + i + ".csv");
			
			//launch Validator
			System.out.println(">> Running training and prediction...");
			Validator<RawDataType, ResultDataType, FeatureType> tmpValidator = new Validator<>(tmpModel, tmpDatatraining, tmpDataPredict, maxIteration, i);
			tmpValidator.run();
			
			// keep report
			try {
				reports.add(tmpValidator.report(false));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}


	@Override
	public CSVGenerator report(boolean save) throws IOException {
		report.setTitle(this.getClass().getSimpleName());
		report.setAddTitle(true);
		report.addLine("","Times","Seconds");
		for (Chrono c: chronos) {
			report.addLine("",c.getName(),c.getDuration()/1000);
		}
		
		report.addLineBreak();
		for (CSVGenerator csv : reports) {
			report.merge(csv, false, false);
			report.addLineBreak();
		}
		
		if (save) report.save();
		return report;
	}

}

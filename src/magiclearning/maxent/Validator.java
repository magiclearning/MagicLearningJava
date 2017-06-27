package magiclearning.maxent;

import java.io.IOException;
import java.util.ArrayList;

import data.Corpus;
import data.Text;
import magiclearning.Dataset;
import magiclearning.Element;
import magiclearning.Model;
import tools.CSVGenerator;
import tools.Chrono;

public class Validator<RawDataType, ResultDataType,FeatureType extends Comparable<FeatureType>> extends Learning<FeatureType> {
	private Maximize<RawDataType, ResultDataType, FeatureType> training;
	private Predictor<RawDataType, ResultDataType, FeatureType> predictor;
	private int predictorSize;
	private Dataset<Element<RawDataType, ResultDataType, FeatureType>> trainingData;
	private Dataset<Element<RawDataType, ResultDataType, FeatureType>> predictorData;
	private int sampleId;
	public Validator(Model<FeatureType,ResultDataType> model, Dataset<Element<RawDataType, ResultDataType, FeatureType>> data,
			int predictorSize, 
			int maxIteration,
			int sampleId) {
		super();
		//nombre de data garder pour le test de prediction
		this.predictorSize = (predictorSize > 0 && predictorSize < 100)?predictorSize:10;
		
		
		int limit = (data.size()*this.predictorSize)/100;
		trainingData = new Dataset<>("trainingData", new ArrayList<>(data.getData().subList(limit, data.size())));
		predictorData = new Dataset<>("predictorData", new ArrayList<>(data.getData().subList(0, limit)));
		
		
		training = new Maximize<>(model, trainingData, maxIteration);
		predictor =  new Predictor<>(model, predictorData);
		this.sampleId = sampleId;
	}
	
	public Validator(Model<FeatureType,ResultDataType> model, 
			Dataset<Element<RawDataType, ResultDataType, FeatureType>> dataTraining, 
			Dataset<Element<RawDataType, ResultDataType, FeatureType>> dataPredictor, 
			int maxIteration, 
			int sampleId) {
		super();
		this.predictorSize = (dataPredictor.size()*100)/dataTraining.size();
		trainingData = dataTraining;
		predictorData = dataPredictor;

		training = new Maximize<>(model, trainingData, maxIteration);
		predictor =  new Predictor<>(model, predictorData);
		this.sampleId = sampleId;
	}
	@Override
	public void run() {
		Chrono total = new Chrono("Total (training & prediction)");
		total.start();
		// run training
		training.run();
		
		// run predictor
		predictor.run();
		total.stop();
		chronos.add(total);
	}

	@Override
	public CSVGenerator report(boolean save) throws IOException {
		report.setTitle(this.getClass().getSimpleName()+""+sampleId);
		report.setAddTitle(true);
		report.addLine("","Times","Seconds");
		for (Chrono c: chronos) {
			report.addLine("",c.getName(),c.getDuration()/1000);
		}
		report.addLineBreak();
		report.merge(training.report(false), false, false);
		report.addLineBreak();
		report.merge(predictor.report(false), false, false);
		
		if (save) report.save();
		return report;
	}
	
	

}

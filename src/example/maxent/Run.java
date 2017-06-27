package example.maxent;

import java.io.IOException;

import magiclearning.Dataset;
import magiclearning.Element;
import magiclearning.FeatureSet;
import magiclearning.Labels;
import magiclearning.Model;
import magiclearning.maxent.CrossValidator;
import magiclearning.maxent.Learning;
import magiclearning.maxent.Maximize;
import magiclearning.maxent.Predictor;
import magiclearning.maxent.Validator;

/**
 * Run basic example: 
 * This example is provided to show you how to use magiclearning 
 * maximum entropy algorithm to train and predict content.
 * @author Antoine
 *
 */
public class Run {
	
	
	
	public static void main(String[] args) throws IOException {
		
		/** load dataset */
		System.out.println("Load Dataset...");
		Dataset<Element<Text, String, String>> data = loadDataSet(false);// dataSet contenant toutes les données utilisés
		LanguageFeatures lf = new LanguageFeatures();// générateur de features
		Learning<String> learn;
		//learn = crossValidator(data, lf);// use cross validation system
		learn = validator(data, lf); // train and test model with a part of dataset
		//learn = maximize(data, lf); // train and build model with all dataset
		//learn = predictor(data, lf); // predict unknow dataset
		learn.run(); // run system
		learn.report(true);// save report
		
		System.out.println("END");
	}
	
	private static Dataset<Element<Text, String, String>> loadDataSet(boolean predict) {
		Corpus corp = (predict)?(new Corpus("predict","data/dataset_predict.txt")):(new Corpus("dataset"));
		Dataset<Element<Text, String, String>> data = new Dataset<>();// dataSet contenant toutes les données utilisés
		LanguageFeatures lf = new LanguageFeatures();// générateur de features
		
		for (Text text : corp.getTexts()) {
			Element<Text, String, String> elem = new Element<Text, String, String>(text.getGroundTruth(), text);
			data.add(elem);
		}
		data.shuffle();
		return data;
	}
	
	private static CrossValidator<Text, String, String> crossValidator(Dataset<Element<Text, String, String>> dataset, LanguageFeatures lf) {
		return new CrossValidator<>(dataset, lf, 10, 100);
	}
	
	private static Validator<Text, String, String> validator(Dataset<Element<Text, String, String>> dataset, LanguageFeatures lf){
		System.out.println("=== Validator ===");
		Labels<String> labels = new Labels<>();// liste des labels utilisés
		FeatureSet<String> dictionary = new FeatureSet<>(false);// dictionnaire regroupant toutes les features
		
		// load features, labels and dictionary
		System.out.println("Load Model...");
		for (Element<Text, String, String> elem : dataset.getData()) {
			elem.getFeatures().addAllFeatures(lf.getFeatures(elem.getRaw()));
			labels.addLabel(elem.getResult());
			dictionary.addAllFeatures(elem.getFeatures().getFeaturesList());
		}
		Model<String, String> model = new Model<>(dictionary,labels);
		
		return new Validator<>(model, dataset, 10, 100, 1);
	}
	
	private static Maximize<Text, String, String> maximize(Dataset<Element<Text, String, String>> dataset, LanguageFeatures lf){
		System.out.println("=== Maximize ===");
		Labels<String> labels = new Labels<>();// liste des labels utilisés
		FeatureSet<String> dictionary = new FeatureSet<>(false);// dictionnaire regroupant toutes les features
		
		// load features, labels and dictionary
		System.out.println("Load Model...");
		for (Element<Text, String, String> elem : dataset.getData()) {
			elem.getFeatures().addAllFeatures(lf.getFeatures(elem.getRaw()));
			labels.addLabel(elem.getResult());
			dictionary.addAllFeatures(elem.getFeatures().getFeaturesList());
		}
		Model<String, String> model = new Model<>(dictionary,labels);
		Maximize<Text, String, String> max = new Maximize<>(model, dataset, 100);
		
		return new Maximize<>(model, dataset, 100);
	}
	
	private static Predictor<Text, String, String> predictor(Dataset<Element<Text, String, String>> dataset, LanguageFeatures lf) throws IOException {
		System.out.println("=== Predictor ===");
		
		System.out.println("Load Model...");
		Model<String, String> model = new Model<>();
		model.load("data/example.model.csv");

		System.out.println("Prepare Predictor");
		Predictor<Text, String, String> predict = new Predictor<>(model, dataset);
		System.out.println("Predict...");
		
		predict.save("test_predict.csv");
		return predict;
	}
}

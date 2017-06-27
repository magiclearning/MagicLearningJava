package magiclearning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * MagicLearning Maxent Model
 * @author Antoine
 *
 */
public class Model<FeatureType extends Comparable<FeatureType>, LabelType> {
	/**
     * The maximum size of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    /** model's name */
	private String name;
	/** dictionary: contains all used features */
	private FeatureSet<FeatureType> dictionary;
	/** labels are possible solutions */
	private Labels<LabelType> labels;
	/** score table */
	private float scores[][];
	/** model filename */
	private String filename;
	/** model file extension */
	private static String EXT = ".csv";
	
	/**
	 * default constructor
	 */
	public Model() {
		dictionary = new FeatureSet<>();
		labels = new Labels<>();
		scores = new float[10][10];
		name = "defaultModel";
		filename = name + EXT;
	}
	
	/**
	 * 
	 * @param featsDictionary all features for the model dictionary feature dataset)
	 */
	public Model(FeatureSet<FeatureType> featsDictionary) {
		dictionary = featsDictionary;
		labels = new Labels<>();
		scores = new float[1][dictionary.getFeaturesList().size()];
		name = "defaultModel";
		filename = name + "." + dictionary.getName() + EXT;
	}
	
	public Model(FeatureSet<FeatureType> featsDictionary, Labels<LabelType> labels) {
		dictionary = featsDictionary;
		this.labels = labels;
		scores = new float[labels.getLabelList().size()][dictionary.getFeaturesList().size()];
		name = "defaultModel";
		filename = name + "." + dictionary.getName() + EXT;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FeatureSet<FeatureType> getDictionary() {
		return dictionary;
	}
	
	/**
	 * add feature to dictionary
	 * @param feature
	 */
	public void addFeature(FeatureType feature){
		dictionary.addFeature(feature);
		ensureCapacityInternalFeatures(dictionary.size());		
	}
	
	/**
	 * add label to model
	 * @param label
	 */
	public void addLabel(LabelType label) {
		labels.addLabel(label);
		ensureCapacityInternalLabels(labels.size());
	}

	/**
	 * set the dictionary
	 * @warning will reload scores data
	 * @param dictionary
	 */
	public void setDictionary(FeatureSet<FeatureType> dictionary) {
		this.dictionary = dictionary;
		int labelSize = (labels.getLabelList().size() > 0)?labels.getLabelList().size():1;
		int dictSize = (dictionary.getFeaturesList().size() > 0)?dictionary.getFeaturesList().size():1;
		scores = new float[labelSize][dictSize];
		
		
	}

	public Labels<LabelType> getLabels() {
		return labels;
	}

	/**
	 * Set labels list and reload scores table to 0
	 * @warning will reload scores data!
	 * @param labels
	 */
	public void setLabels(Labels<LabelType> labels) {
		this.labels = labels;
		int labelSize = (labels.getLabelList().size() > 0)?labels.getLabelList().size():1;
		int dictSize = (dictionary.getFeaturesList().size() > 0)?dictionary.getFeaturesList().size():1;
		scores = new float[labelSize][dictSize];
		
	}

	public float[][] getScores() {
		return scores;
	}
	
	/**
	 * gte score by label and feature instances
	 * @param label
	 * @param feature
	 * @return score or throw NullPointerException if not found
	 */
	public float getScore(FeatureType label, FeatureType feature) {
		int featIndex = dictionary.indexOf(feature);
		int labelIndex = labels.getLabelList().indexOf(label);
		if (featIndex >= 0 && featIndex < dictionary.size() && labelIndex >= 0 && labelIndex < labels.size()) {
			return scores[labelIndex][featIndex];
		} else {
			throw new NullPointerException("Cannot find score, label and/or feature are not loaded in model");
		}
	}
	
	/**
	 * get score by indexes
	 * @param labelIndex
	 * @param featureIndex
	 * @return score or NullPointerException if not found
	 */
	public float getScore(int labelIndex, int featureIndex) {
		if (featureIndex >= 0 && featureIndex < dictionary.size() && labelIndex >= 0 && labelIndex < labels.size()) {
			return scores[labelIndex][featureIndex];
		} else {
			throw new NullPointerException("Cannot find score, indexes are outside model range: label index:" + labelIndex + ", feature index:" + featureIndex + " dictionary size:" + dictionary.size());
		}
	}
	
	/**
	 * set score by label and feature object
	 * @param label
	 * @param feature
	 * @param value
	 */
	public void setScore(FeatureType label, FeatureType feature, float value) {
		int featIndex = dictionary.indexOf(feature);
		int labelIndex = labels.getLabelList().indexOf(label);
		if (featIndex >= 0 && featIndex < dictionary.size() && labelIndex >= 0 && labelIndex < labels.size()) {
			scores[labelIndex][featIndex] = value;
		}
	}
	
	/**
	 * set score by label and feature indexes
	 * @param labelIndex
	 * @param featureIndex
	 * @param value
	 */
	public void setScore(int labelIndex, int featureIndex, float value) {
		if (featureIndex >= 0 && featureIndex < dictionary.size() && labelIndex >= 0 && labelIndex < labels.size()) {
			scores[labelIndex][featureIndex] = value;
		}
	}
	
	/**
	 * save model into file filename
	 * @param filename
	 * @param append true: if file exists, add data at the end then rewrite the file fully
	 * @throws IOException 
	 */
	public void save(boolean append) throws IOException{
		try {
			//filename = (filename == "" || filename == null)?this.filename:filename;
			FileWriter writer = new FileWriter(filename, append);
			// write file
			writer.write("<<MagicLearning[Model:" + name +"]>>" + Settings.CSV_SEPARATOR + labels);
			writer.write(System.lineSeparator());
			// writing scores
			System.out.println("dico:"+dictionary.size());
			for(int i=0; i < dictionary.size();i++) {
				writer.write(dictionary.getFeaturesList().get(i).toString()+ Settings.CSV_SEPARATOR);
				for(int j=0; j < labels.size();j++) {
					writer.write(Float.toString(scores[j][i])+ Settings.CSV_SEPARATOR);
				}
				writer.write(System.lineSeparator());
			}
			writer.close();
		} finally {}
	}
	
	/**
	 * Load model(scores, features, labels) from file filename 
	 * Extract data from the model file
	 * @param filename the model filename
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @TODO
	 */
	public void load(String filename) throws FileNotFoundException, IOException {
		boolean first = true;
		ArrayList<String> list = new ArrayList<>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	list.add(line);
		    	if (first) {
		    		exctractLabels(line);
		    		first = false;
		    	} else {
		    		extractFeatureAndScore(line);
		    	}
		    }
		}
	}
	
	/**
	 * Extract labels from the string line
	 * This method is used to extract labels from saved model file
	 * @param line
	 */
	private void exctractLabels(String line) {
		String elem[] = line.split(Settings.CSV_SEPARATOR);
		elem[0] = elem[0].replace("<<MagicLearning[Model:","");
		elem[0] = elem[0].replace("]>>","");
		this.name = elem[0];
		
		// ajout des labels
		for (int i = 1; i < elem.length; i++){
			if (elem[i] instanceof Comparable<?>) {
				addLabel((LabelType)elem[i]);
			}
			
		}
	}
	
	/**
	 * Extract features and score from the a String line
	 * This method is used to extract saved model file
	 * @param line
	 */
	private void extractFeatureAndScore(String line) {
		String elem[] = line.split(Settings.CSV_SEPARATOR);
		addFeature((FeatureType)elem[0]);
		// ajout des scores
		for (int i = 1; i < elem.length; i++){
			if (elem[i] instanceof Comparable<?>) {
				scores[i-1][dictionary.size()-1] = Float.parseFloat(elem[i]);
				
			}
			
		}
	}	

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	/*******************************
	 * Management of score table
	 ***************************************/
	
    private int grow(int minCapacity, int oldCapacity) {
        // overflow-conscious code
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        return newCapacity;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
    
    private void ensureCapacityInternalFeatures(int minCapacity) {
        if (minCapacity - scores[0].length > 0) {
        	int newCapacity = grow(minCapacity, scores[0].length);
    		float tmp[][] = new float[labels.size()][newCapacity];
    		for(int i=0; i<labels.size(); i++) {
    			tmp[i] = Arrays.copyOf(scores[i], newCapacity);
    		}
    		scores = tmp;
        }
            
    }
    
    private void ensureCapacityInternalLabels(int minCapacity) {
        if (minCapacity - scores.length > 0) {
        	int newCapacity = grow(minCapacity, scores.length);
    		float tmp[][] = new float[newCapacity][dictionary.getFeaturesList().size()];
    		for(int i=0; i < scores.length; i++) {
    			tmp[i] = Arrays.copyOf(scores[i], dictionary.getFeaturesList().size());
    		}
    		scores = tmp;
        }
            
    }
    
    
    
	
}

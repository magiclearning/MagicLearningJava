package magiclearning;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * FeatureSet manage collection of feature T
 * @author Antoine
 *
 * @param <T>
 */
public class FeatureSet<T extends Comparable<T>> {
	private String name; // name of feature set
	private boolean duplicates;// allow duplicated features
	private Set<T> features;
	private List<T> featuresList;
	private List<Integer> featuresID; // correspondance des features avec celles du dictionnaire
	private boolean featuresIdLoaded;
	
	public FeatureSet() {
		features = new TreeSet<>();
		featuresList = new ArrayList<>();
		duplicates = false;
		featuresID = new ArrayList<>();
		featuresIdLoaded = false;
	}
	
	public FeatureSet(boolean allowDuplicate) {
		features = new TreeSet<>();
		featuresList = new ArrayList<>();
		duplicates = allowDuplicate;
		featuresID = new ArrayList<>();
		featuresIdLoaded = false;
	}
	
	
	public void addFeature(T feature) {
		if (duplicates) {
			featuresList.add(feature);
		} else {
			features.add(feature);
			featuresList = new ArrayList<>(features);
		}
	}
	
	public void addAllFeatures(T[] featureTab) {
		if (duplicates) {
			featuresList.addAll(Arrays.asList(featureTab));
		} else {
			features.addAll(Arrays.asList(featureTab));
			featuresList = new ArrayList<>(features);
		}
	}
	
	public void addAllFeatures(List<T> featureList) {
		if (duplicates) {
			this.featuresList.addAll(featureList);
		} else {
			features.addAll(featureList);
			this.featuresList = new ArrayList<>(features);
		}
	}
	
	/**
	 * removes all features
	 */
	public void clear() {
		featuresList.clear();
		features.clear();
		featuresID.clear();
	}
	
	/**
	 * check if given feature is already in the set
	 * @param feature
	 * @return
	 */
	public boolean hasFeature(T feature) {
		if (duplicates) {
			return featuresList.contains(feature);
		}
		return features.contains(feature);
	}
	
	/**
	 * Search for the feature in the list. If no duplicates allowed, search is faster and used binary search
	 * @param feature
	 * @return index of feature or not found: return negative index where feature could be placed +1 
	 * (because of 0) if duplicates not allowed else -1
	 */
	public int indexOf(T feature) {
		if (duplicates) {
			return featuresList.indexOf(feature);
		}
		// ELSE: binary search
		int min = 0;
		int max = featuresList.size()-1;
		int middle;
		
		while (min<=max) {
			middle = (min+max)/2;
			int compare = featuresList.get(middle).compareTo(feature) ;
			if (compare == 0) // found
				return middle;
			else if (compare > 0) // then after middle
				max = middle - 1;
			else min = middle + 1; // then before middle
		}
		return -1 - min; // not found: return negative index where feature could be placed +1 (because of 0)
	}
	
	/**
	 * Merge main featureSet with the second one
	 * @param set featureSet to add
	 */
	public void merge(FeatureSet<T> set) {
		addAllFeatures(set.getFeaturesList());
	}
	
	/**
	 * Load all features ID
	 * @param model
	 * @return 
	 */
	public <LabelType> void loadFeaturesID(Model<T, LabelType> model) {
		featuresID.clear();// remove all elements before
		for (T feature : features) {
			featuresID.add(model.getDictionary().indexOf(feature));
		}
		featuresIdLoaded = true;
	}

	/**
	 * 
	 * @return name of FeatureSet object
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getFeaturesList() {
		return featuresList;
	}

	public void setFeaturesList(List<T> featuresList) {
		this.featuresList = featuresList;
	}

	public boolean isDuplicates() {
		return duplicates;
	}
	
	public List<Integer> getFeaturesID() {
		return featuresID;
	}

	public void setFeaturesID(List<Integer> featuresID) {
		this.featuresID = featuresID;
	}

	public boolean isFeaturesIdLoaded() {
		return featuresIdLoaded;
	}

	@Override
	public String toString() {
		String s = "<<MagicLearning:" + name + ">>\n";
		for (T feat : features) {
			s +=  feat + "\n";
		}
		return s;
	}
	
	public int size() {
		return (duplicates)?featuresList.size():features.size();
	}
	
	/**
	 * Write features in file
	 * @param filename
	 * @param append true: if file exist, add feature after else false: erase before 
	 * @return
	 * @throws IOException
	 */
	public void save(String filename, boolean append) throws IOException {
		try {
			FileWriter writer = new FileWriter(filename, append);
			// write file
			writer.write("<<MagicLearning[FeatureSet:" + name + "]>>");
			writer.write(System.lineSeparator());
			for (T feat : features) {
				writer.write(feat.toString());
				writer.write(System.lineSeparator());
			}
			writer.close();
		} finally {}
	}
	
}

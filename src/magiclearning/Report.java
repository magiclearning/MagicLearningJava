package magiclearning;

import java.util.Date;

import magiclearning.maxent.Learning;

public class Report<FeatureType extends Comparable<FeatureType>> {
	private String name;
	private Date date;
	private Learning<FeatureType> system;
	
	
	public Report(String name, Learning<FeatureType> learningSystem) {
		system = learningSystem;
		this.name = name;
	}
	
	

}

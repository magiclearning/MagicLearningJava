package magiclearning;

/**
 * Define a data (ground truth element for instance)
 * @author Antoine
 *
 * @param <Raw> type for the raw
 * @param <Result> type for the result
 * @param <Feat>
 */
public class Element<Raw,Result, Feat extends Comparable<Feat>> {
	private Result result; // groundtruth result
	private Raw raw; // raw data
	private FeatureSet<Feat> features;
	private Result prediction; // computed result
	
	
	public Element(Result result, Raw raw) {
		this.result = result;
		this.raw = raw;
		features = new FeatureSet<>();
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	public Raw getRaw() {
		return raw;
	}

	public void setRaw(Raw raw) {
		this.raw = raw;
	}

	public FeatureSet<Feat> getFeatures() {
		return features;
	}

	public void setFeatures(FeatureSet<Feat> features) {
		this.features = features;
	}

	public Result getPrediction() {
		return prediction;
	}

	public void setPrediction(Result prediction) {
		this.prediction = prediction;
	}
	
	@Override
	public String toString() {
		return ((result != null)?result.toString():"") + "," + ((raw != null)?raw.toString():"") + "," + ((prediction != null)?prediction.toString():"");
	}
}

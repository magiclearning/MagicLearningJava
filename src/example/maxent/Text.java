package example.maxent;

public class Text {
	private String text;
	private String prediction;
	private String groundTruth;
	
	public Text(String lang, String text) {
		this.text = text;
		groundTruth = lang;
		prediction = "";
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPrediction() {
		return prediction;
	}

	public void setPrediction(String prediction) {
		this.prediction = prediction;
	}

	public String getGroundTruth() {
		return groundTruth;
	}

	public void setGroundTruth(String groundTruth) {
		this.groundTruth = groundTruth;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[Text:" + text + "]";
	}
	
	
}

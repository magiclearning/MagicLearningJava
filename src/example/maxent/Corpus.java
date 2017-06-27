package example.maxent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import magiclearning.Settings;
/**
 * Corpus will load data from dataset files
 * @author Antoine
 *
 */
public class Corpus {
	private String name;
	private List<Text> texts;
	private static final String[] LANG = {"FR", "EN", "ES"};
	
	public Corpus(String name) {
		texts = new ArrayList<>();
		this.name = name;
		
		for (String lang : LANG) {
			try (BufferedReader br = new BufferedReader(new FileReader("data/dataset_" + lang + ".txt"))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (!line.isEmpty()) {
						Text temp = new Text(lang, line);
						texts.add(temp);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// load dataset to predict
	public Corpus(String name, String filename) {
		texts = new ArrayList<>();
		this.name = name;
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = br.readLine()) != null) {
				Text temp = new Text("unknow", line);
				texts.add(temp);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	@Override
	public String toString() {
		String s = "";
		
		for (Text text : texts) {
			s += text+"\n";
		}
		
		return s;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<Text> getTexts() {
		return texts;
	}


	public void setTexts(List<Text> texts) {
		this.texts = texts;
	}
	
	public void save(String filename, boolean append) throws IOException {
		try {
			FileWriter writer = new FileWriter(filename, append);
			// write file
			writer.write("<<MagicLearning[CorpusExample:" + name +"]>>" + Settings.CSV_SEPARATOR);
			writer.write(System.lineSeparator());
			// writing 
			for (Text text : texts) {
				writer.write(text.getGroundTruth() + Settings.CSV_SEPARATOR + text.getPrediction() + Settings.CSV_SEPARATOR + text.getText() + Settings.CSV_SEPARATOR);
				writer.write(System.lineSeparator());
			}
			writer.close();
		} finally {}
	}
}

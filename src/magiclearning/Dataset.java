package magiclearning;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.Text;

/**
 * MagicLearning Dataset used to create dataset in order to process learning/predicting
 * @author Antoine
 *
 * @param <T>
 */
public class Dataset<T> {
	private String name;
	private List<T> data;
	
	public Dataset() {
		this.name = "default";
		this.data = new ArrayList<T>();
	}
	
	public Dataset(String name,List<T> list) {
		this.name = name;
		this.data = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<T> getData() {
		return data;
	}
	
	public void add(T elem) {
		data.add(elem);
	}
	
	public int size() {
		return data.size();
	}
	
	/**
	 * mélanger les data
	 */
	public void shuffle() {
		Collections.shuffle(data);
	}
	
	/**
	 * save dataset to file:
	 * features need toString method
	 * @param filename
	 * @param append
	 * @throws IOException
	 */
	public void save(String filename, boolean append) throws IOException {
		try {
			FileWriter writer = new FileWriter(filename, append);
			// write file
			writer.write("<<MagicLearning[Dataset:" + name + "]>>");
			writer.write(System.lineSeparator());
			for (T feat : data) {
				writer.write(feat.toString());
				writer.write(System.lineSeparator());
			}
			writer.close();
		} finally {}
	}
	
}

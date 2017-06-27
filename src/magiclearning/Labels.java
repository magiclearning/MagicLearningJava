package magiclearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * toutes les étiquettes utilisées par le model
 * @author Antoine
 *
 */
public class Labels<T> {
	private Set<T> list; // allow to keep unique label
	private ArrayList<T> labelList;
	public Labels() {
		list = new TreeSet<>();
		labelList = new ArrayList<>();
	}
	
	public Labels(T[] labels) {
		list = new TreeSet<>(Arrays.asList(labels));
		labelList = new ArrayList<>(list);
	}
	
	/**
	 * add label to labels list
	 * do nothing if already exist 
	 * @param label
	 */
	public void addLabel(T label) {
		list.add(label);
		labelList = new ArrayList<>(list);
	}
	
	/**
	 * add all labels in labels' list
	 * @param labels
	 */
	public void addLabels(T[] labels) {
		list.addAll(Arrays.asList(labels));
		labelList = new ArrayList<>(list);
	}
	
	/**
	 * add all labels in labels' list
	 * @param labels
	 */
	public void addLabels(ArrayList<T> labels) {
		list.addAll(labels);
	}
	
	public int indexOf(T label) {
		return labelList.indexOf(label);
	}
	
	public ArrayList<T> getLabelList() {
		return labelList;
	}
	
	public int size() {
		return list.size();
	}

	@Override
	public String toString() {
		String s = "";
		for (T label : labelList) {
			s += label + Settings.CSV_SEPARATOR;
		}
		return s;
	}

}

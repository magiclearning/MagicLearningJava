package example.maxent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import magiclearning.FeaturesLoaderInt;

/**
 * This class will generate features for a text given in parameter:
 * In our example, we extract words (a word will be considerate as feature to detect the text language).
 * @author Antoine
 *
 */
public class LanguageFeatures implements FeaturesLoaderInt<Text, String> {

	@Override
	public List<String> getFeatures(Text data) {
		String[] temp = data.getText().split(" ");
		ArrayList<String> list = new ArrayList<>();
		// we keep words with length > 2
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].length() > 2) {
				list.add(temp[i]);
			}
		}
		return list;
	}

}

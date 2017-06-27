package magiclearning;

import java.util.List;

public interface FeaturesLoaderInt<DataType, FeatureType> {
	public List<FeatureType> getFeatures(DataType data);
}

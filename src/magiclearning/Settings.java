package magiclearning;

/**
 * Settings
 * Some parameters for magicLearning system
 * @author Antoine
 *
 */
public class Settings {
	/** csv delimiters */
	public static String CSV_SEPARATOR = ";";
	
	/** écart-type maximum en dessous duquel l'entrainement est stoppé (sur une echelle de 0 à 100 ) */
	public static float STD_DEV_LIMIT = 1.0f;
	/** nombre de résultats utilisés pour cacluler l'écart-type de la condition d'arrêt de l'apprentissage */
	public static int STOP_VAL = 7;
}

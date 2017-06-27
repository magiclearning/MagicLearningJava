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
	
	/** �cart-type maximum en dessous duquel l'entrainement est stopp� (sur une echelle de 0 � 100 ) */
	public static float STD_DEV_LIMIT = 1.0f;
	/** nombre de r�sultats utilis�s pour cacluler l'�cart-type de la condition d'arr�t de l'apprentissage */
	public static int STOP_VAL = 7;
}

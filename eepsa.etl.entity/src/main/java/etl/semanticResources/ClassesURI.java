package etl.semanticResources;

public class ClassesURI {

	//Classes
	private static String class_Observation = Namespaces.getNs_EXN4EEPSA() + "Observation";
	private static String class_Sensor = Namespaces.getNs_EXR4EEPSA() + "Sensor";
	private static String class_WeatherStation = Namespaces.getNs_AEMET() + "WeatherStation";
	
	//Getters
	public static String getClass_Observation() {
		return class_Observation;
	}
	public static String getClass_Sensor() {
		return class_Sensor;
	}
	public static String getClass_WeatherStation() {
		return class_WeatherStation;
	}
	
}

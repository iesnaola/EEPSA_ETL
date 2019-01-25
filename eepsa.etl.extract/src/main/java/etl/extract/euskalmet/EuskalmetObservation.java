package etl.extract.euskalmet;

public class EuskalmetObservation {

	//XML Elements for Observations Information
	private static String euskalmetObs_Month_Attr= "mes";
	private static String euskalmetObs_Day_Attr = "Dia";
	private static String euskalmetObs_Day_Tag = "dia";
	private static String euskalmetObs_Hour_Attr = "Hora";
	private static String euskalmetObs_Hour_Tag = "hora";
	private static String euskalmetObs_Sensors= "Meteoros";
	
	//Getters
	public static String getEuskalmetObs_Month_Attr() {
		return euskalmetObs_Month_Attr;
	}
	public static String getEuskalmetObs_Day_Attr() {
		return euskalmetObs_Day_Attr;
	}
	public static String getEuskalmetObs_Day_Tag() {
		return euskalmetObs_Day_Tag;
	}
	public static String getEuskalmetObs_Hour_Attr() {
		return euskalmetObs_Hour_Attr;
	}
	public static String getEuskalmetObs_Hour_Tag() {
		return euskalmetObs_Hour_Tag;
	}
	public static String getEuskalmetObs_Sensors() {
		return euskalmetObs_Sensors;
	}

}

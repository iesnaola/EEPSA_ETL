package etl.extract.euskalmet;

public class EuskalmetWeatherStation {

	
	//XML Elements for Weather Station Information
	private static String euskalmetStation_Id = "stationID";
	private static String euskalmetStation_Name = "stationName";
	private static String euskalmetStation_LongUTM = "longitudeUTM";
	private static String euskalmetStation_LatUTM = "latitudeUTM";
	private static String euskalmetStation_Alt = "altitude";
	private static String euskalmetStation_Province = "province";
	private static String euskalmetStation_Location = "location";
	
	//XML Elements for Sensor Information
	
	
	private static String SENSOR_ALT = "sensorAltitude";
	
	private static String SENSOR_DATA = "sensorData";
	private static String SENSOR_ID= "sensorId";
	private static String SENSOR_DESC= "meteoroDescription";
	private static String DEVICE= "sensorType";
	
	//XML Observations Attributes => Esto también se debería de coger del XSD para automatizarlo??
	private static String DAY_ATTR= "Dia";
	private static String DAY_TAG= "dia";
	private static String HOUR_ATTR= "Hora";
	private static String HOUR_TAG= "hora";
	private static String METEOROS_TAG= "Meteoros";
	private static String MONTH_TAG= "mes";
	private static String STATION_ATTR= "xsi:noNamespaceSchemaLocation";
	
	//Getters
	public static String getEuskalmetStation_Id() {
		return euskalmetStation_Id;
	}
	public static String getEuskalmetStation_Name() {
		return euskalmetStation_Name;
	}
	public static String getEuskalmetStation_LongUTM() {
		return euskalmetStation_LongUTM;
	}
	public static String getEuskalmetStation_LatUTM() {
		return euskalmetStation_LatUTM;
	}
	public static String getEuskalmetStation_Alt() {
		return euskalmetStation_Alt;
	}
	public static String getEuskalmetStation_Province() {
		return euskalmetStation_Province;
	}
	public static String getEuskalmetStation_Location() {
		return euskalmetStation_Location;
	}
	
}

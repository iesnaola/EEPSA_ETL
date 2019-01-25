package etl.entity;

public class WeatherStation {

	//General information
	public String stationURI = new String();
	public String stationID = new String();
	public String stationName = new String();
	public String stationOwnerURI = new String();
	public String stationLocation = new String();
	
	//Location information
	public String stationProvinceURI = new String();
	public String stationLatitude = new String();
	public String stationLongitude = new String();
	public String stationAltitude = new String();
	
	//Sensors
	public Sensor[] stationSensorList = new Sensor[]{};
	
	
	
}

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
	
	//Other XML elements that are not currently being stored
	private static String euskalmetStation_DateFrom = "dateFrom"; //Weather station working since
	private static String euskalmetStation_DateTo = "dateTo"; //Weather station working until
	private static String euskalmetStation_Fictitious = "fictitious"; //Fictitious weather station?
	private static String euskalmetStation_Type = "stationType"; //Weather station type
	
	//XML Elements for Sensor Information
	private static String euskalmetStation_Sensor_List = "sensorList";
	private static String euskalmetStation_Sensor_Data = "sensorData";
	private static String euskalmetStation_Sensor_Id= "sensorId";
	private static String euskalmetStation_Sensor_Desc= "meteoroDescription";
	private static String euskalmetStation_Sensor_Name= "sensorType";
	private static String euskalmetStation_Sensor_Alt = "sensorAltitude";
	
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

	public static String getEuskalmetStation_DateFrom() {
		return euskalmetStation_DateFrom;
	}

	public static String getEuskalmetStation_DateTo() {
		return euskalmetStation_DateTo;
	}

	public static String getEuskalmetStation_Fictitious() {
		return euskalmetStation_Fictitious;
	}

	public static String getEuskalmetStation_Type() {
		return euskalmetStation_Type;
	}

	public static String getEuskalmetStation_Sensor_List() {
		return euskalmetStation_Sensor_List;
	}

	public static String getEuskalmetStation_Sensor_Data() {
		return euskalmetStation_Sensor_Data;
	}

	public static String getEuskalmetStation_Sensor_Id() {
		return euskalmetStation_Sensor_Id;
	}

	public static String getEuskalmetStation_Sensor_Desc() {
		return euskalmetStation_Sensor_Desc;
	}

	public static String getEuskalmetStation_Sensor_Name() {
		return euskalmetStation_Sensor_Name;
	}

	public static String getEuskalmetStation_Sensor_Alt() {
		return euskalmetStation_Sensor_Alt;
	}
	
}

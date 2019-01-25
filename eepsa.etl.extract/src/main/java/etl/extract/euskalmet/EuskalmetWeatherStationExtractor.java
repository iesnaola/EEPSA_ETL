package etl.extract.euskalmet;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import etl.entity.Coordinates;
import etl.entity.Sensor;
import etl.entity.WeatherStation;

public class EuskalmetWeatherStationExtractor {

	
	private static Log log = LogFactory.getLog(EuskalmetWeatherStationExtractor.class);
	
	//Constants
	public static String baseURI = "http://www.tekniker.es/euskalmetWeatherStations#";
	public static String weatherStationBase = "weatherStation_Euskalmet_";
	public static String weatherStationOwnerURI = "http://es.dbpedia.org/page/Euskalmet";
	
	
	public static WeatherStation createEuskalmetWeatherStationFromXMLFile(String inputFileString){
		log.debug("createEuskalmetWeatherStationFromXMLFile START");
		
		if(inputFileString.isEmpty()){
			log.error("ERROR: in createEuskalmetWeatherStationFromXMLFile");
			log.error("ERROR DESC: createEuskalmetWeatherStationFromXMLFile " + inputFileString + " IS EMPTY");
		}
		else
			log.debug("createEuskalmetWeatherStationFromXMLFile inputFileString IS NOT EMPTY");
			
		File inputFile = new File(inputFileString);
		WeatherStation ws = new WeatherStation();
		
		//Create a Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			
			//Create a Document from a file
			try {
				Document doc = builder.parse(inputFile);
				
				//get weather Station Elements Elements
				ws = createEuskalmetWeatherStationFromXMLDoc(doc);
	
			} catch (SAXException e) {
				log.error("ERROR: in readXMLFile");
				log.error("ERROR DESC: Error ", e);
				return ws;
			} catch (IOException e) {
				log.error("ERROR: in readXMLFile");
				log.error("ERROR DESC: Error ", e);
				return ws;
			}
		} catch (ParserConfigurationException e1) {
			log.error("ERROR: in readXMLFile");
			log.error("ERROR DESC: Error ", e1);
			return ws;
		}
		
		log.debug("createEuskalmetWeatherStationFromXMLFile END");
		return ws;
	}
	
	public static WeatherStation createEuskalmetWeatherStationFromXMLDoc(Document xmlDoc){
		log.debug("createEuskalmetWeatherStationFromXML START");
		
		//Create Euskalmet Weather Station
		WeatherStation ws = new WeatherStation();
		
		//Fill Euskalmet Weather Station
		Element root = xmlDoc.getDocumentElement();
		
		if (!root.getAttribute(EuskalmetWeatherStation.getEuskalmetStation_Id()).isEmpty()){
			//Fill General information
			ws.stationID = root.getAttribute(
					EuskalmetWeatherStation.getEuskalmetStation_Id());
			log.debug("Station ID: " + ws.stationID);
			
			ws.stationURI = baseURI + weatherStationBase + ws.stationID;
			log.debug("Station URI: " + ws.stationURI);
			
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Name()).getLength() > 0){
				ws.stationName = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_Name()).item(0).getTextContent();
				log.debug("Station Name: " + ws.stationName);
			}
			
			ws.stationOwnerURI = weatherStationOwnerURI;
			log.debug("Station Owner: " + ws.stationOwnerURI);
			
			
			//Fill Location information
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Location()).getLength() > 0){
				ws.stationLocation = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_Location()).item(0).getTextContent();
				log.debug("Station Location: " + ws.stationLocation);
			}
			
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Province()).getLength() > 0){
				String xmlProvinceValue = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_Province()).item(0).getTextContent();
				ws.stationProvinceURI = getProvinceURI(xmlProvinceValue);
				log.debug("Station Province: " + ws.stationProvinceURI);
			}
			
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_LatUTM()).getLength() > 0
					&&
				root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_LongUTM()).getLength() > 0 ){
				
				String latUTM = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_LatUTM()).item(0).getTextContent();
				String longUTM =  root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_LongUTM()).item(0).getTextContent();
				
				Coordinates coordinates = convertUTMToWGS84Coordinates(latUTM, longUTM);
				ws.stationLatitude = coordinates.latitude;
				ws.stationLongitude = coordinates.longitude;
				log.debug("Station Latitude: " + ws.stationLatitude);
				log.debug("Station Longitude: " + ws.stationLongitude);
			}
			
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Alt()).getLength() > 0){
				ws.stationAltitude = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_Alt()).item(0).getTextContent();
				log.debug("Station Altitude: " + ws.stationAltitude);
			}
			
			//Fill Sensor information
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Sensor_Data()).getLength() > 0){
				
				NodeList xmlSensorList = xmlDoc.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Sensor_Data());
				int sensorListLength = xmlSensorList.getLength();
			
				Sensor[] sensorList = new Sensor[sensorListLength];
				
				for (int sensorIterator = 0; 
						sensorIterator < sensorListLength; 
						sensorIterator++){
					
					Node xmlSensorNode = xmlSensorList.item(sensorIterator);
					Element xmlSensorElement = (Element) xmlSensorNode;
					
					sensorList[sensorIterator] = new Sensor();
					
					if (!xmlSensorElement.getAttribute(EuskalmetWeatherStation.getEuskalmetStation_Sensor_Id()).isEmpty()){
						sensorList[sensorIterator].sensorId  = 
								xmlSensorElement.getAttribute(EuskalmetWeatherStation.getEuskalmetStation_Sensor_Id());
						log.debug("Sensor ID: " + sensorList[sensorIterator].sensorId);
						
						sensorList[sensorIterator].sensorURI = ws.stationURI + "_" + sensorList[sensorIterator].sensorId;
						log.debug("Sensor URI: " + sensorList[sensorIterator].sensorURI);
						
						if (xmlSensorElement.getElementsByTagName(
								EuskalmetWeatherStation.getEuskalmetStation_Sensor_Name()).getLength() > 0){
							sensorList[sensorIterator].sensorName = 
									xmlSensorElement.getElementsByTagName(
											EuskalmetWeatherStation.getEuskalmetStation_Sensor_Name()).item(0).getTextContent();
							log.debug("Sensor Name: " + sensorList[sensorIterator].sensorName);
						}
						
						if (xmlSensorElement.getElementsByTagName(
								EuskalmetWeatherStation.getEuskalmetStation_Sensor_Desc()).getLength() > 0){
							sensorList[sensorIterator].sensorDesc = 
									xmlSensorElement.getElementsByTagName(
											EuskalmetWeatherStation.getEuskalmetStation_Sensor_Desc()).item(0).getTextContent();
							log.debug("Sensor Desc: " + sensorList[sensorIterator].sensorDesc);
						}
						
						if (xmlSensorElement.getElementsByTagName(
								EuskalmetWeatherStation.getEuskalmetStation_Sensor_Alt()).getLength() > 0){
							sensorList[sensorIterator].sensorAlt = 
									xmlSensorElement.getElementsByTagName(
											EuskalmetWeatherStation.getEuskalmetStation_Sensor_Alt()).item(0).getTextContent();
							log.debug("Sensor Altitude: " + sensorList[sensorIterator].sensorAlt);
						}
						
							
						
					}
					else{
						log.error("ERROR in createEuskalmetWeatherStationFromXML");
						log.error("ERROR DESC: Sensor ID cannot be extracted from XML document");
					}
					
				}
				ws.stationSensorList = sensorList;
				log.debug("Sensor list added to the Weather Station");
			}
			
		}
		else{
			log.error("ERROR in createEuskalmetWeatherStationFromXML");
			log.error("ERROR DESC: Weather Station ID cannot be extracted from XML document");
		}
		
		log.debug("createEuskalmetWeatherStationFromXML END");
		return ws;
	}
	
	private static String getProvinceURI(String provinceString) {
		/*
		 * This function returns a province's corresponding URI from DBpedia
		 */
		log.debug("getProvinceURI: START");
		
		String provinceURI = new String();
		
		if (provinceString.toUpperCase().equals("ARABA/ÁLAVA") || provinceString.toUpperCase().equals("ARABA/ALAVA")) 
			provinceURI = "http://dbpedia.org/page/Álava";
		else if (provinceString.toUpperCase().equals("BIZKAIA"))
			provinceURI = "http://dbpedia.org/page/Biscay";
		else if (provinceString.toUpperCase().equals("GIPUZKOA"))
			provinceURI = "http://dbpedia.org/page/Gipuzkoa";
		else
			provinceURI = baseURI + "UnknownProvince_" + provinceString.trim();
		
		log.debug("getProvinceURI: END");
		return provinceURI;
	}
	
	public static Coordinates convertUTMToWGS84Coordinates (String northing, String easting){
		/*
		 * This function converts UTM to WGS84 coordinates
		 */
		log.debug("convertUTMToWGS84Coordinates: START");
		
		Coordinates coord = new Coordinates();
		
        int Zone= 30; //This zone belongs to the Basque Country territory
        char Letter= 'T'; //This letter belongs to the Basque Country territory
        double Northing=Double.parseDouble(northing);   
        double Easting=Double.parseDouble(easting);
        double Hem;
        if (Letter>'M')
            Hem='N';
        else
            Hem='S';            
        double north;
        if (Hem == 'S')
            north = Northing - 10000000;
        else
            north = Northing;
        double lat = (north/6366197.724/0.9996+(1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)-0.006739496742*Math.sin(north/6366197.724/0.9996)*Math.cos(north/6366197.724/0.9996)*(Math.atan(Math.cos(Math.atan(( Math.exp((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*( 1 -  0.006739496742*Math.pow((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996 )/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996 - 0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996 )*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996)*3/2)*(Math.atan(Math.cos(Math.atan((Math.exp((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996))*180/Math.PI;
        lat=Math.round(lat*10000000);
        coord.latitude=String.valueOf(lat/10000000);
        double lon =Math.atan((Math.exp((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*( north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2* north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3)) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))*180/Math.PI+Zone*6-183;
        lon=Math.round(lon*10000000);
        coord.longitude=String.valueOf(lon/10000000);       
		        
        log.debug("convertUTMToWGS84Coordinates: END");
		return coord;
	}
	
	
	
}

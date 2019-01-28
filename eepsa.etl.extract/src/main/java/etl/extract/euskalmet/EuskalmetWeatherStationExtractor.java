package etl.extract.euskalmet;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
import etl.semanticResources.Namespaces;

public class EuskalmetWeatherStationExtractor {

	private static Log log = LogFactory.getLog(EuskalmetWeatherStationExtractor.class);
	
	//Constants
	public static String baseURI = "http://www.tekniker.es/euskalmetWeatherStations#";
	public static String euskalmetWeatherStationBase = "weatherStation_Euskalmet_";
	public static String euskalmetWeatherStationOwnerURI = "http://es.dbpedia.org/page/Euskalmet";
	public static String euskalmetWeatherStationListXML = "http://opendata.euskadi.eus/contenidos/ds_meteorologicos/estaciones_meteorologicas/opendata/estaciones.xml";
	
	public static WeatherStation createEuskalmetWeatherStationFromXMLURL(String urlString){
		log.debug("createEuskalmetWeatherStationFromXMLURL START");
		
		if(urlString.isEmpty()){
			log.error("ERROR: in createEuskalmetWeatherStationFromXMLURL");
			log.error("ERROR DESC: createEuskalmetWeatherStationFromXMLURL " + urlString + " IS EMPTY");
		}
		else
			log.debug("createEuskalmetWeatherStationFromXMLURL urlString IS NOT EMPTY");
			
		WeatherStation ws = new WeatherStation();
		
		//Create a Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			
			//Create a Document from a file
			try {
				Document doc = builder.parse(new URL(urlString).openStream());
				ws = createEuskalmetWeatherStationFromXMLDoc(doc);
				
			} catch (SAXException e) {
				log.error("ERROR: in readXMLURL");
				log.error("ERROR DESC: SAXException. " + e);
			} catch (IOException e) {
				log.error("ERROR: in readXMLURL");
				log.error("ERROR DESC: IOException. " + e);
			}
		}
		 catch (ParserConfigurationException e1) {
				log.error("ERROR: in readXMLFile");
				log.error("ERROR DESC: Error ", e1);
				return ws;
		 }
		
		log.debug("createEuskalmetWeatherStationFromXMLURL END");
		return ws;
	}
	
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
			
			ws.stationURI = baseURI + euskalmetWeatherStationBase + ws.stationID;
			log.debug("Station URI: " + ws.stationURI);
			
			if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Name()).getLength() > 0){
				ws.stationName = root.getElementsByTagName(
						EuskalmetWeatherStation.getEuskalmetStation_Name()).item(0).getTextContent();
				log.debug("Station Name: " + ws.stationName);
			}
			
			ws.stationOwnerURI = euskalmetWeatherStationOwnerURI;
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
						
						String obsQuality = getObservedQualityURIFromEuskalmetSensorId(sensorList[sensorIterator].sensorId);
						
						sensorList[sensorIterator].observedQuality.qualityName = obsQuality.substring(obsQuality.lastIndexOf("#")+1);
						sensorList[sensorIterator].observedQuality.qualityURI = obsQuality;
						
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
							float sensorAlt = 
									Float.parseFloat(xmlSensorElement.getElementsByTagName(
											EuskalmetWeatherStation.getEuskalmetStation_Sensor_Alt()).item(0).getTextContent());
							
							sensorList[sensorIterator].sensorAlt = Float.toString(sensorAlt/100); //In meters
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

	public static String[] getEuskalmetWeatherStationURLList(String stationListURLXML){
		log.debug("getEuskalmetWeatherStationURLList START");
		
		if(stationListURLXML.isEmpty()){
			log.error("ERROR: in getEuskalmetWeatherStationURLList");
			log.error("ERROR DESC: getEuskalmetWeatherStationURLList " + stationListURLXML + " IS EMPTY");
		}
		
		String[] URLList = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		Document doc = null;
		try {
			doc = factory.newDocumentBuilder().parse(new URL(stationListURLXML).openStream());
			
			int wsURLListLength = 0;
			
			//get <row>
			NodeList wsURLList = doc.getElementsByTagName("row");
			wsURLListLength = wsURLList.getLength();
			
			URLList = new String[wsURLListLength];
			
			for (int wsURLIterator=0; wsURLIterator<wsURLListLength; wsURLIterator++) 
		    {
				Element element = (Element)wsURLList.item(wsURLIterator);	       
				String attrDataXML = new String();
				
				if (element.getElementsByTagName("dataxml").getLength() > 0)
					attrDataXML = element.getElementsByTagName("dataxml").item(0).getTextContent();
				
		        if (!attrDataXML.isEmpty())
		        	URLList[wsURLIterator] = attrDataXML;  
		    }
		} catch (MalformedURLException e) {
			log.error("ERROR: in getEuskalmetWeatherStationURLList");
			log.error("ERROR DESC: MalformedURLException", e);
		} catch (SAXException e) {
			log.error("ERROR: in getEuskalmetWeatherStationURLList");
			log.error("ERROR DESC: SAXException", e);
		} catch (IOException e) {
			log.error("ERROR: in getEuskalmetWeatherStationURLList");
			log.error("ERROR DESC: IOException", e);
		} catch (ParserConfigurationException e) {
			log.error("ERROR: in getEuskalmetWeatherStationURLList");
			log.error("ERROR DESC: ParserConfigurationException", e);
		}
		
		log.debug("getEuskalmetWeatherStationURLList END");
		return URLList;
		
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
	
	private static String getObservedQualityURIFromEuskalmetSensorId(String sensorId) {
		/*
		 * This function gets the URI of the quality observed by a Euskalmet Sensor 
		 */
		log.debug("getObservedQualityURIFromEuskalmetSensorId: START");
		
		String observedQualityURI = Namespaces.getNs_Q4EEPSA() + "UnknownQuality";
		
		if (sensorId.equals("11"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WindSpeed";
			
		else if (sensorId.equals("12"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WindDirection";
			
		else if (sensorId.equals("14"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "MaxWindSpeed"; //
			
		else if (sensorId.equals("16"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WindSpeedSigma"; //Sigma dirección del viento
			
		else if (sensorId.equals("17"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SigmaTheta"; //Sigma velocidad del viento
			
		else if (sensorId.equals("18"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WindPower"; //Velocidad del viento cúbica media  
			
		else if (sensorId.equals("21"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "OutdoorTemperature";

		else if (sensorId.equals("22"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "GroundLevelTemperature";
		
		else if (sensorId.equals("31"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "OutdoorHumidity";
			
		else if (sensorId.equals("40"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "PrecipitationLevel";
			
		else if (sensorId.equals("50"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "AtmosphericPressure";
						
		else if (sensorId.equals("60"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "WaterLevel";
						
		else if (sensorId.equals("61"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "WaterLevel"; 
						
		else if (sensorId.equals("62"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "WaterLevel"; 
						
		else if (sensorId.equals("65"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaterFlow"; 
						
		else if (sensorId.equals("70"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SolarRadiation";
		
		else if (sensorId.equals("74"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "NetRadiation";
						
		else if (sensorId.equals("90"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "WaterTemperature";
						
		else if (sensorId.equals("91"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "DissolvedOxygen";
						
		else if (sensorId.equals("92"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "PH";
						
		else if (sensorId.equals("93"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaterConductivity";
						
		else if (sensorId.equals("94"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "AmmoniaInWater";
						
		else if (sensorId.equals("95"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaterTurbidity";
						
		else if (sensorId.equals("97"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "OrganicCompoundsInWater";
						
		else if (sensorId.equals("98"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "NitrateInWater";
		
		else if (sensorId.equals("B0"))
			observedQualityURI = Namespaces.getNs_M3LITE() + "Visibility";
		
		else if (sensorId.equals("B1"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SeaWaterLevel";
		
		else if (sensorId.equals("B2"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SeaWaterLevel";

		else if (sensorId.equals("B4"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "MaxWaveHeight";
		
		else if (sensorId.equals("B5"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaveHeight";

		else if (sensorId.equals("B6"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SwellPeriod";
		
		else if (sensorId.equals("B7"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "HydrostaticPressureMSP";
		
		else if (sensorId.equals("B8"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "HydrostaticPressureLSP";
		
		else if (sensorId.equals("B9"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaterFlowMagnitude";
		
		else if (sensorId.equals("BA"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaterFlowDirection";
		
		else if (sensorId.equals("BB"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SeaWaterTemperature";
		
		else if (sensorId.equals("BC"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "TermistorTemperature";
		
		else if (sensorId.equals("BD"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaveHeight";

		else if (sensorId.equals("BE"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "WaveFlowVerticalSpeed";
		
		else if (sensorId.equals("BF"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "TideLevel";

		else if (sensorId.equals("BG"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "PeakPeriod";
		
		else if (sensorId.equals("BH"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "PeakPeriodDirection";
		
		else if (sensorId.equals("BI"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SwellAverageDirection";
		
		else if (sensorId.equals("FT"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "Phosphorus";
		
		else if (sensorId.equals("OR"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "OrtoPhosphate";

		else if (sensorId.equals("SS"))
			observedQualityURI = Namespaces.getNs_Q4EEPSA() + "SuspendedSolids";

		log.debug("Observed Quality: " + observedQualityURI);
		log.debug("getObservedQualityURIFromEuskalmetSensorId: END");
		return observedQualityURI;
	}
	
}
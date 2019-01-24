package etl.extract.files;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import etl.entity.WeatherStation;
import etl.extract.euskalmet.EuskalmetWeatherStation;
import etl.extract.euskalmet.WeatherStationExtractor;


public class XMLManager {

	private static Log log = LogFactory.getLog(XMLManager.class);
	
	public static WeatherStation getEuskalmetWeatherStationXMLElements(Document doc){
		log.debug("getWeatherStationXMLElements START");
		
		WeatherStation ws = new WeatherStation();
		int sensorListLength = 0;
		
		Element root = doc.getDocumentElement();
		
		if (!root.getAttribute(EuskalmetWeatherStation.getEuskalmetStation_Id()).isEmpty()){
			ws.stationID = root.getAttribute(EuskalmetWeatherStation.getEuskalmetStation_Id());
			ws.stationURI = xx + ws.stationID; //xx=namespace
		}
		
		if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Name()).getLength() > 0)
			ws.stationName = root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Name()).item(0).getTextContent();
		
		
		
		
		if (root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Alt()).getLength() > 0)
			ws.stationAltitude= root.getElementsByTagName(EuskalmetWeatherStation.getEuskalmetStation_Alt()).item(0).getTextContent();
		
		
		//Recogemos todos los sensores/devices en una lista
		if (root.getElementsByTagName(SENSOR_DATA).getLength() > 0){
			NodeList nList = doc.getElementsByTagName(SENSOR_DATA);
			sensorListLength = nList.getLength();
		
			//Inicializamos un Array en el que guardaremos toda la info de sensores y devices
			WeatherStationDevices[] wsArray = new WeatherStationDevices[sensorListLength];
			
			for (int sensorIterator = 0; 
					sensorIterator < sensorListLength; 
					sensorIterator++){
				
				Node nNode = nList.item(sensorIterator);
				Element eElement = (Element) nNode;
				
				wsArray[sensorIterator] = new WeatherStationDevices();
				
				if (eElement.getElementsByTagName(DEVICE).getLength() > 0)
					wsArray[sensorIterator].deviceName =
					eElement.getElementsByTagName(DEVICE).item(0).getTextContent();
				
				if (!eElement.getAttribute(SENSOR_ID).isEmpty())
					wsArray[sensorIterator].sensorId  = 
					eElement.getAttribute(SENSOR_ID);
				
				if (eElement.getElementsByTagName(SENSOR_ALT).getLength() > 0)
					wsArray[sensorIterator].sensorAlt =
					eElement.getElementsByTagName(SENSOR_ALT).item(0).getTextContent();
				
				if (eElement.getElementsByTagName(SENSOR_DESC).getLength() > 0)
					wsArray[sensorIterator].sensorDesc = 
						eElement.getElementsByTagName(SENSOR_DESC).item(0).getTextContent();
				else
					wsArray[sensorIterator].sensorDesc = new String(); //Esto no debería ser así. Habría que controlarlo mejor, pero por ahora para no perder demasiado tiempo puede valer.
				
			}
		
			//Copiamos el Array creado en el array "devicesAndSensors" de nuestro objeto
			ws.deviceAndSensor = wsArray;
		}
		
		if (ws.UTMLatitude.isEmpty()){
			WGS84Coordinates coordinates = WeatherStationConverter.convertUTMToWGS84Coordinates(String.valueOf(ws.latitude), String.valueOf(ws.longitude));
			ws.latitude = Float.parseFloat(coordinates.latitude);
			ws.longitude = Float.parseFloat(coordinates.longitude);
		}
		
		log.debug("getWeatherStationXMLElements END");
		return ws;
	}
	
}

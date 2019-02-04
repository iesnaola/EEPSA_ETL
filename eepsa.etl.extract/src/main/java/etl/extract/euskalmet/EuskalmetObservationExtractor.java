package etl.extract.euskalmet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

import etl.entity.Observation;

public class EuskalmetObservationExtractor {


	private static Log log = LogFactory.getLog(EuskalmetObservationExtractor.class);
	
	private static String EUSKALMET_OBS_BASE_URL = 
			"http://opendata.euskadi.eus/contenidos/ds_meteorologicos/met_stations_ds_";
	private static String Euskalmet_WeatherStation_Base_URI = "http://www.tekniker.es/euskalmetWeatherStations#weatherStation_Euskalmet_";
	
	public static List<Observation> createEuskalmetObservation(String stationID, String month, String year){
		log.debug("createEuskalmetObservation START");
		
		if(stationID.isEmpty() || month.isEmpty() || year.isEmpty()){
			log.error("ERROR: in createEuskalmetObservation");
			log.error("ERROR DESC: createEuskalmetObservation some parameter IS EMPTY");
		}
		else
			log.debug("createEuskalmetObservation parameters ARE NOT EMPTY");
			
		List<Observation> obsList = new ArrayList<Observation>();
		String xmlURLString = createEuskalmetObservationFileURL(stationID, month, year, ".xml");
		String xsdURLString = createEuskalmetObservationFileURL(stationID, month, year, ".xsd");
		
		List<String> xsdElementList = readXSD(xsdURLString);
		
		//Create a Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		
		try {
			builder = factory.newDocumentBuilder();
			
			//Create a Document from a file
			try {
				Document doc = builder.parse(new URL(xmlURLString).openStream());
				obsList = createEuskalmetObservationListFromXMLDoc(doc, xsdElementList);
				
			} catch (SAXException e) {
				log.error("ERROR: in createEuskalmetObservation");
				log.error("ERROR DESC: SAXException. " + e);
			} catch (IOException e) {
				log.error("ERROR: in createEuskalmetObservation");
				log.error("ERROR DESC: IOException. " + e);
			}
		}
		 catch (ParserConfigurationException e1) {
				log.error("ERROR: in createEuskalmetObservation");
				log.error("ERROR DESC: Error ", e1);
				return obsList;
		 }
		
		log.debug("createEuskalmetObservationFromXMLURL END");
		return obsList;
	}
	
	private static List<String> readXSD(String inputURLString) {
		log.debug("readXSD: START");
				
				
		if(inputURLString.isEmpty()){
			log.error("ERROR: in readXSD");
			log.error("ERROR DESC: readXSD " + inputURLString + " IS EMPTY");
			System.out.println("Input File URL is Empty");
		}
			
		List<String> xsdElementList = new ArrayList<String>();
		System.out.println(inputURLString);
		
		//Create a Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
		
			factory.setNamespaceAware(true);
			
			Document doc = null;
			try {
				doc = factory.newDocumentBuilder().parse(new URL(inputURLString).openStream());
				//get weather Station Elements Elements
				xsdElementList = getEuskalmetWeatherStationObservationsXMLElements(doc);
				
			} catch (SAXException e) {
				log.error("ERROR: in readXSD");
				log.error("ERROR DESC: SAXException", e);
				return xsdElementList;
			} catch (IOException e) {
				log.error("ERROR: in readXSD");
				log.error("ERROR DESC: IOException", e);
				return xsdElementList;
			}
		} catch (ParserConfigurationException e1) {
			log.error("ERROR: in readXSD");
			log.error("ERROR DESC: ParserConfigurationException", e1);
			return xsdElementList;
			}
	
	
		log.debug("readXSD: END");
		return xsdElementList;
			
			
		
	}

	private static List<String> getEuskalmetWeatherStationObservationsXMLElements(Document doc) {
		/*
		 * This function retrieves the XML elements of the Euskalmet Observations file
		 */
		log.debug("getEuskalmetWeatherStationObservationsXMLElements START");
		
		//Create EuskalmetXSDElement object
		List<String> xmlAttributeNameList = new ArrayList<String>();
		int nListLength = 0;
		
		//get <xs:element> elements
		NodeList nList = doc.getElementsByTagName("xs:element");
		nListLength = nList.getLength();
		
		for (int xsElementIterator=0; xsElementIterator<nListLength; xsElementIterator++) 
	    {
	        // Get element
	        Element element = (Element)nList.item(xsElementIterator);	        
	        
	        String attrName = element.getAttribute("name");

	        if (!attrName.isEmpty()){
	        	//Only the sensor list is retrieved (the rest of the information is not necessary)
	        	if (!attrName.equals("mes") && !attrName.equals("dia")
	        			&& !attrName.equals("hora") && !attrName.equals("Meteoros"))
	        		xmlAttributeNameList.add(attrName);
	        }
	    }
		
		log.debug("getEuskalmetWeatherStationObservationsXMLElements END");
		return xmlAttributeNameList;
	}

	private static List<Observation> createEuskalmetObservationListFromXMLDoc(Document doc, List<String> xsdElements) {
		log.debug("createEuskalmetObservationListFromXMLDoc START");
		
		//Variable declaration
		int dayListLength, hourListLength, xsdElementsLength = 0;
		String stationID = "";
		List<Observation> obsList = 
				new ArrayList<Observation>();
		Observation obs = new Observation();
		xsdElementsLength = xsdElements.size();
		
		
		Node monthNode = doc.getElementsByTagName(EuskalmetObservation.getEuskalmetObs_Month_Attr()).item(0);
		Element monthElement = (Element) monthNode;
		
		if (!monthElement.getAttribute(EuskalmetObservation.getEuskalmetObs_StationID_Attr()).isEmpty())
			stationID = monthElement.getAttribute(EuskalmetObservation.getEuskalmetObs_StationID_Attr()).substring(0, 4);

		
		NodeList dayList = doc.getElementsByTagName(EuskalmetObservation.getEuskalmetObs_Day_Tag());
		dayListLength = dayList.getLength();
		
		//Iterate over days
		for (int dayIterator = 0; 
				dayIterator < dayListLength; 
				dayIterator++){
			
			Node dayNode = dayList.item(dayIterator);
			Element dayElement = (Element) dayNode;
			
			NodeList hourList = dayElement.getElementsByTagName(EuskalmetObservation.getEuskalmetObs_Hour_Tag());
			hourListLength = hourList.getLength();
			
			//Iterate over hours
			for (int hourIterator = 0; 
					hourIterator < hourListLength; 
					hourIterator++){
				
				Node hourNode = hourList.item(hourIterator);
				Element hourElement = (Element) hourNode;

				if (!dayElement.getAttribute(EuskalmetObservation.getEuskalmetObs_Day_Attr()).isEmpty() 
						&& !hourElement.getAttribute(EuskalmetObservation.getEuskalmetObs_Hour_Attr()).isEmpty())
				{
					if (hourElement.getAttribute(EuskalmetObservation.getEuskalmetObs_Hour_Attr()).endsWith(":00")){
						
						obs = new Observation();
						obs.obsExecutorWeatherStationURI = generateWeatherStationURI(stationID);
						
						String dateTimeString = generateDateTimeString(dayElement.getAttribute(EuskalmetObservation.getEuskalmetObs_Day_Attr()), 
								hourElement.getAttribute(EuskalmetObservation.getEuskalmetObs_Hour_Attr()));
						obs.obsTime = dateTimeString;
						
						Node meteoroNode = hourElement.getElementsByTagName(EuskalmetObservation.getEuskalmetObs_Sensors()).item(0);
						Element meteoroElement = (Element) meteoroNode;
						
						for (int xsdElementIterator = 0; 
								xsdElementIterator < xsdElementsLength; 
								xsdElementIterator++){
							
								String attrName = xsdElements.get(xsdElementIterator);
								
								if (meteoroElement.getElementsByTagName(attrName).getLength()>0){
									
									obs.obsExecutorURI = obs.obsExecutorWeatherStationURI + "_" +
											meteoroElement.getElementsByTagName(attrName)
											.item(0).getNodeName();
									
									obs.obsValue = meteoroElement.
											getElementsByTagName(attrName).item(0).getTextContent();
									
								}
							
								obsList.add(obs);	
						}

						
					}
				}
					
			}
				
		}	
		log.debug("createEuskalmetObservationListFromXMLDoc END");
		return obsList;			
			
	}
	
	private static String createEuskalmetObservationFileURL(String stationID, String month, String year, String extension) {
		log.debug("createEuskalmetObservationFileURL: START");
		
		String fileURL = new String();
		
		if(Integer.parseInt(month)<1 || Integer.parseInt(month)>12){
			log.error("ERROR in createEuskalmetObservationFileURL");
			log.error("ERROR DESC: Given month must be between 1 and 12");
		}
		else{
			String monthProcessed = Integer.valueOf(month).toString(); //As it only works with "1", not "01"
			
			fileURL = new StringBuilder().append(EUSKALMET_OBS_BASE_URL).append(year).append("/opendata/")
						.append(year).append("/").append(stationID).append("/")
						.append(stationID).append("_").append(year).append("_")
						.append(monthProcessed).append(extension).toString();
		}
		
		log.debug("createEuskalmetObservationFileURL: END");
		return fileURL;
	}
	
	private static String generateWeatherStationURI(String stationID) {
		String weatherStationURI = Euskalmet_WeatherStation_Base_URI + stationID;
		
		return weatherStationURI;
	}
	
	private static String generateDateTimeString(String date, String time) {
		/*
		 * This function returns a datetimestamp format date time.
		 */
		String[] dateParts = date.split("\\-");
		
		String month = dateParts[1];
		if (Integer.valueOf(dateParts[1])<10)
			month = "0" + dateParts[1];

		String dateTime = dateParts[0] + "-" + month + "-" + dateParts[2] + "T" + time + ":00Z";
		
		return dateTime;
	}
	
}

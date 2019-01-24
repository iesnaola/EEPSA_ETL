package etl.extract.euskalmet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import etl.entity.WeatherStation;


public class WeatherStationExtractor {

	
	private static Log log = LogFactory.getLog(WeatherStationExtractor.class);
	
	
	public static WeatherStation extractEuskalmetWeatherStation(String inputFileString){
		log.debug("extractEuskalmetWeatherStation START");
		
		if(inputFileString.isEmpty()){
			log.error("ERROR: in extractEuskalmetWeatherStation");
			log.error("ERROR DESC: extractEuskalmetWeatherStation " + inputFileString + " IS EMPTY");
		}
		else
			log.debug("extractEuskalmetWeatherStation inputFileString IS NOT EMPTY");
			
		File inputFile = new File(inputFileString);
		WeatherStation ws = new WeatherStation();
		
		System.out.println(inputFileString);
		
		//Create a Document Builder
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			//Create a Document from a file
			try {
				Document doc = builder.parse(inputFile);
				
				//get weather Station Elements Elements
				ws = ManageXMLFiles.getWeatherStationXMLElements(doc);
	
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
		
		log.debug("readXMLFile end");
		return ws;
	}
	
}

package etl.transform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;

import etl.entity.Quality;
import etl.entity.WeatherStation;
import etl.semanticResources.ClassesURI;
import etl.semanticResources.Namespaces;
import etl.semanticResources.PropertiesURI;

public class AnnotateWeatherStation {

	private static Log log = LogFactory.getLog(AnnotateWeatherStation.class);
	
	public static Model annotateWeatherStation(WeatherStation ws) {
		log.debug("annotateWeatherStation: START");

		//Create model
		Model model = ModelFactory.createDefaultModel();
		
		if (ws == null){
			log.error("ERROR in annotateWeatherStation");
			log.error("ERROR DESC: weatherStation is NULL");
			return model; //Return an empty model
		}
		else
			log.debug("annotateWeatherStation weatherStation is NOT NULL");

		int tripleGeneration = 0;

		model = declareWeatherStationNamespaces(model);

		tripleGeneration = generateWeatherStationTriples(model, ws);
		if (tripleGeneration == 0){
			log.error("ERROR in annotateWeatherStation");
			log.error("ERROR DESC: Triples not generated");
		}
		log.debug("annotateWeatherStation: END");
		return model;
		
	}
	
	private static int generateWeatherStationTriples(Model model, WeatherStation ws){
		log.debug("generateWeatherStationTriples: START");
		
		int triplesGeneratedCorrectly = 0; 
		
		//Create Properties	
		Property prop_bot_containsElement = model.createProperty(PropertiesURI.getPropString_bot_containsElement());
		Property prop_dbo_owner = model.createProperty(PropertiesURI.getPropString_dbo_owner());
		Property prop_dbo_province = model.createProperty(PropertiesURI.getPropString_dbo_province());
		Property prop_dc_identifier = model.createProperty(PropertiesURI.getPropString_dc_identifier());
		Property prop_eep_forQuality = model.createProperty(PropertiesURI.getPropString_eep_forQuality());
		Property prop_foaf_name = model.createProperty(PropertiesURI.getPropString_foaf_name());
		Property prop_geo_alt = model.createProperty(PropertiesURI.getPropString_geo_alt());
		Property prop_geo_lat = model.createProperty(PropertiesURI.getPropString_geo_lat());
		Property prop_geo_location = model.createProperty(PropertiesURI.getPropString_geo_location());
		Property prop_geo_long = model.createProperty(PropertiesURI.getPropString_geo_long());
		
		try{
			//Create weather station's basic information
			model.createResource(ws.stationURI)
				.addProperty(RDF.type, 
						model.createResource(ClassesURI.getClass_WeatherStation()))
				.addProperty(prop_dbo_owner, 
						model.createResource(ws.stationOwnerURI))
				.addProperty(prop_dbo_province, 
						model.createResource(ws.stationProvinceURI))
				.addProperty(prop_dc_identifier, ws.stationID)
				.addProperty(prop_foaf_name, ws.stationName)
				.addProperty(prop_geo_lat,
						model.createTypedLiteral(ws.stationLatitude, XSDDatatype.XSDfloat))
				.addProperty(prop_geo_long, 
						model.createTypedLiteral(ws.stationLongitude, XSDDatatype.XSDfloat))
				.addProperty(prop_geo_alt,
						model.createTypedLiteral(ws.stationAltitude, XSDDatatype.XSDfloat))
				.addProperty(prop_geo_location, ws.stationLocation)
			;
			
			//Add weather station's sensors
			for (int sensorIterator = 0;
					sensorIterator < ws.stationSensorList.length;
					sensorIterator++){
			
				model.getResource(ws.stationURI)
					.addProperty(prop_bot_containsElement,
							model.createResource(ws.stationSensorList[sensorIterator].sensorURI)
								.addProperty(RDF.type, 
										model.createResource(ClassesURI.getClass_Sensor()))
								.addProperty(prop_dc_identifier, ws.stationSensorList[sensorIterator].sensorId)
								.addProperty(prop_geo_alt, 
										model.createTypedLiteral(ws.stationSensorList[sensorIterator].sensorAlt,  XSDDatatype.XSDfloat))
								.addProperty(prop_foaf_name, ws.stationSensorList[sensorIterator].sensorName)
					)
				;
				
				//Add sensor's observed qualities
				for (int qualityIterator = 0;
						qualityIterator < ws.stationSensorList[sensorIterator].observedQualityList.length;
						qualityIterator++){
					
					Quality quality = ws.stationSensorList[sensorIterator].observedQualityList[qualityIterator];
					
					model.getResource(ws.stationSensorList[sensorIterator].sensorURI)
						.addProperty(prop_eep_forQuality, 
								model.createResource(ws.stationURI + "_" + quality.qualityName)
									.addProperty(RDF.type, 
											model.createResource(quality.qualityURI)
									)
						)
					;
				}
			}
		}
		catch(Exception e){
			log.error("ERROR in generateWeatherStationTriples");
			log.error("ERROR DESC: ", e); //Define Exception
			return triplesGeneratedCorrectly;
		}

		triplesGeneratedCorrectly = 1;
		log.debug("generateWeatherStationTriples: END");
		return triplesGeneratedCorrectly;

	}
	
	private static Model declareWeatherStationNamespaces(Model model){
		log.debug("declareWeatherStationNamespaces: START");

		model.setNsPrefix("eep", Namespaces.getNs_EEP_ODP());
		model.setNsPrefix("q4eepsa", Namespaces.getNs_Q4EEPSA());
		model.setNsPrefix("exr4eepsa", Namespaces.getNs_EXR4EEPSA());
		model.setNsPrefix("aemet", Namespaces.getNs_AEMET());
		model.setNsPrefix("bot", Namespaces.getNs_BOT());
		model.setNsPrefix("dbo", Namespaces.getNs_DBO());
		model.setNsPrefix("dc", Namespaces.getNs_DC());
		model.setNsPrefix("foaf", Namespaces.getNs_FOAF());
		model.setNsPrefix("geo", Namespaces.getNs_GEO());
		model.setNsPrefix("xsd", Namespaces.getNs_XSD());
		
		log.debug("declareWeatherStationNamespaces: END");
		return model;
	}

}

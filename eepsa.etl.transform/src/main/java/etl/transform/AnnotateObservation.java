package etl.transform;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;

import etl.entity.Observation;
import etl.semanticResources.ClassesURI;
import etl.semanticResources.Namespaces;
import etl.semanticResources.PropertiesURI;

public class AnnotateObservation {

	private static Log log = LogFactory.getLog(AnnotateObservation.class);
	
	private static Model annotateObservation(Observation obs) {
		log.debug("annotateObservation: START");

		//Create model
		Model model = ModelFactory.createDefaultModel();
		
		if (obs == null){
			log.error("ERROR in annotateObservation");
			log.error("ERROR DESC: obs is NULL");
			return model; //Return an empty model
		}
		else
			log.debug("annotateObservation weatherStation is NOT NULL");

		int tripleGeneration = 0;

		model = declareObservationsNamespaces(model);

		tripleGeneration = generateObservationTriples(model, obs);
		if (tripleGeneration == 0){
			log.error("ERROR in annotateObservation");
			log.error("ERROR DESC: Triples not generated");
		}
		log.debug("annotateObservation: END");
		return model;
		
	}
	
	private static int generateObservationTriples(Model model, Observation obs){
		log.debug("generateObservationTriples: START");
		int triplesGeneratedCorrectly = 0; 
		
		//Create Properties
		Property prop_eep_madeBy = model.createProperty(PropertiesURI.getPropString_eep_madeBy());
		Property prop_eep_onQuality = model.createProperty(PropertiesURI.getPropString_eep_onQuality());
		Property prop_qudt_numValue = model.createProperty(PropertiesURI.getPropString_qudt_numValue());
		Property prop_qudt_unit = model.createProperty(PropertiesURI.getPropString_qudt_unit());
		Property prop_rc_hasResult =  model.createProperty(PropertiesURI.getPropString_rc_hasResult());
		Property prop_rc_hasTempCont = model.createProperty(PropertiesURI.getPropString_rc_tempCont());
		Property prop_time_dateTime = model.createProperty(PropertiesURI.getPropString_rc_inXSDDT());
		
		try{
			//Create observation basic information
			model.createResource(obs.obsURI)
				.addProperty(RDF.type, ClassesURI.getClass_Observation())
				.addProperty(prop_eep_onQuality, 
						model.createResource(obs.obsQualityURI))
				.addProperty(prop_eep_madeBy, 
						model.createResource(obs.obsExecutorURI))
				.addProperty(prop_rc_hasTempCont,
						model.createResource(obs.obsURI + "_" + obs.obsTime)
							.addProperty(prop_time_dateTime, 
									model.createTypedLiteral(obs.obsTime, XSDDatatype.XSDdateTimeStamp))
				)
				.addProperty(prop_rc_hasResult, 
						model.createResource()
							.addProperty(prop_qudt_numValue, 
									model.createTypedLiteral(obs.obsValue, XSDDatatype.XSDfloat))
							.addProperty(prop_qudt_unit, 
									model.createResource(obs.obsUnit))
				)
			;
		
		}
		catch(Exception e){
			log.error("ERROR in generateObservationTriples");
			log.error("ERROR DESC: ", e); //Define Exception
			return triplesGeneratedCorrectly;
		}

	
		triplesGeneratedCorrectly = 1;
		log.debug("generateObservationTriples: END");
		return triplesGeneratedCorrectly;

	}
	
	private static Model declareObservationsNamespaces(Model model){
		log.debug("declareObservationsNamespaces: START");

		model.setNsPrefix("eep", Namespaces.getNs_EEP_ODP());
		model.setNsPrefix("qudt", Namespaces.getNs_QUDT());
		model.setNsPrefix("rc", Namespaces.getNs_RC_ODP());
		model.setNsPrefix("time", Namespaces.getNs_TIME());
		model.setNsPrefix("xsd", Namespaces.getNs_XSD());
		
		log.debug("declareObservationsNamespaces: END");
		return model;
	}
	
}

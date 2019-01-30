package etl.load.virtuoso;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.update.UpdateAction;

public class VirtuosoLoader {

	
	private static Log log = LogFactory.getLog(VirtuosoLoader.class);
	
	public static Model updateModel(String rdfFilename, String sparqlQuery){
		log.debug("updateModel: START");
		Model model = ModelFactory.createDefaultModel() ;
		model.read(rdfFilename) ;
		UpdateAction.parseExecute(sparqlQuery, model);
		
		log.debug("updateModel: END");
		return model;
	}
	
	public static int loadModelToVirtuoso (Model model, String sparqlEndpointURL, String targetGraph, String sparqlUpdateQuery){
		log.debug("loadModelToVirtuoso: START");
		int correctlyLoaded = 0;
		
		try (RDFConnection conn = RDFConnectionFactory.connect(sparqlEndpointURL)) {
		        conn.load(targetGraph, model);
		        correctlyLoaded = 1;
		}
		log.debug("loadModelToVirtuoso: END");
		return correctlyLoaded;
	}
	
}

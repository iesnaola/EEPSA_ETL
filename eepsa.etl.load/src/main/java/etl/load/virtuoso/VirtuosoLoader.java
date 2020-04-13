package etl.load.virtuoso;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import virtuoso.jena.driver.VirtModel;


public class VirtuosoLoader {

	private static Log log = LogFactory.getLog(VirtuosoLoader.class);
	
	
	public static int loadModelToVirtuoso(Model model, String endpoint, String GRAPH_NAME, String USERNAME, String PASSWORD){
	/**
	 * This function loads a Model to Virtuoso	
	 */
		log.debug("loadModelToVirtuoso START");
		try {
			//Connect to Virtuoso
			VirtModel virtualModel = VirtModel.openDatabaseModel(GRAPH_NAME, endpoint, USERNAME, PASSWORD);
			long modelInitialSize = virtualModel.size();
			
			//Add model
			virtualModel.add(model);
			long modelNewSize = virtualModel.size();
			
			virtualModel.close();
			
			//Assuming that Virtuoso was empty (or at least it does not have this info yet)
			if (modelInitialSize < modelNewSize){
				log.debug("RDF content added.");
				log.debug("loadModelToVirtuoso END");
				return 1;
			}
				
			else{
				log.debug("RDF content NOT added.");
				log.debug("loadModelToVirtuoso END");
				return 0;
			}
		}
		catch (Exception e){
			log.error("ERROR: " + e);
			log.debug("loadModelToVirtuoso END");
			return 0;
		}
	}

	
	public static Model readRDFFile(String file){
	/**
	 * This function loads reads an RDF file and creates a Model	
	 */
		log.debug("readRDFFile START");
		//Create an empty model
		Model model = ModelFactory.createDefaultModel();
		
		try {
			//Read RDF and add store it in a Model
			RDFDataMgr.read(model, file) ;
			log.debug("readRDFFile END");
			return model;
		}
		catch(Exception e){
			log.debug("readRDFFile END");
			return model;
		}
		
	}


}

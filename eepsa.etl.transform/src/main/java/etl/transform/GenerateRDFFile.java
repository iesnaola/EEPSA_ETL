package etl.transform;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.rdf.model.Model;

public class GenerateRDFFile {

	private static Log log = LogFactory.getLog(GenerateRDFFile.class);
	
	public static int writeModelInFile(Model model, String filenameRDF, String outputNotation) {
		log.debug("writeModelInFile: START");
		
		if (model == null){
			log.error("ERROR in writeModelInFile");
			log.error("ERROR DESC: writeModelInFile model is NULL");
			return 0;
		}
		else
			log.debug("writeModelInFile model IS NOT NULL");
		if (filenameRDF.isEmpty()){
			log.error("ERROR in writeModelInFile");
			log.error("ERROR DESC: writeModelInFile filenameRDF is EMPTY");
			return 0;
		}
		else
			log.debug("writeModelInFile filenameRDF IS NOT EMPTY");
		
		try {
			//Create a File
			FileOutputStream outputFile = new FileOutputStream(filenameRDF);
			
			//Write the model in an outputFile, with a specific notation
			model.write(outputFile, outputNotation);
			
			log.debug("writeModelInFile: END");
			return 1;
			
		} catch (FileNotFoundException e) {
			log.error("ERROR in writeModelInFile");
			log.error("ERROR DESC: Filename " + filenameRDF + "not found", e);
			return 0;
		}
		catch (Exception e){
			log.error("ERROR in writeModelInFile");
			log.error("ERROR DESC:", e);
			return 0;
		}
	}
	
}

package gov.epa.exp_data_gathering.parse;

import java.util.Arrays;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.Arnot2006.ParseArnot2006;
import gov.epa.exp_data_gathering.parse.Burkhard.ParseBurkhard2;
import gov.epa.exp_data_gathering.parse.ECOTOX.ParseEcotox;
import gov.epa.exp_data_gathering.parse.ITRC.ParseITRC;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.ParseQSAR_ToolBox;

/**
* @author TMARTI02
*/
public class BCFUtilities {

	/**
	 * Creates files for all BCF sources all at once:
	 */
	public void ParseBCFSources() {
		
		List<String> propertyNames = Arrays.asList(ExperimentalConstants.strBCF, ExperimentalConstants.strBAF);
		boolean generateOriginalRecords=false;
		
		for (String propertyName:propertyNames) {
			System.out.println("\n**********************\n"+propertyName);
//			createArnotFiles(generateOriginalRecords, propertyName);
//			createBurkhardFiles(generateOriginalRecords, propertyName);
//			createEcotoxFiles(generateOriginalRecords, propertyName);
//			createITRC_Files(propertyName);
			createQSAR_ToolboxFiles(generateOriginalRecords);
		}
	}


	private void createQSAR_ToolboxFiles(boolean generateOriginalRecords) {
		String [] filenames= {ParseQSAR_ToolBox.fileNameBCF_ECHA_REACH, ParseQSAR_ToolBox.fileNameBCF_Canada, 
				ParseQSAR_ToolBox.fileNameBCF_CEFIC, ParseQSAR_ToolBox.fileNameBCF_NITE};

		for (String filename:filenames) {
			ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(null, filename);
			p.generateOriginalJSONRecords=generateOriginalRecords;//*** set to true on first run
			p.removeDuplicates=false;
			p.writeJsonExperimentalRecordsFile=true;
			p.writeExcelExperimentalRecordsFile=true;
			p.writeExcelFileByProperty=true;		
			p.writeCheckingExcelFile=false;//creates random sample spreadsheet
			p.createFiles();
			System.out.println("********************************************\n");
		}
	}


	private void createITRC_Files(String propertyName) {
		ParseITRC p = new ParseITRC(propertyName);
		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();
	}


	private void createArnotFiles(boolean generateOriginalRecords, String propertyName) {
		Parse p = new ParseArnot2006(propertyName);
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.writeCheckingExcelFile=false;
		p.removeDuplicates=false;
		p.createFiles();
	}
	
	private void createBurkhardFiles (boolean generateOriginalRecords, String propertyName) {
		ParseBurkhard2 p = new ParseBurkhard2(propertyName);
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}
	
	private void createEcotoxFiles (boolean generateOriginalRecords, String propertyName) {
		ParseEcotox p = new ParseEcotox();
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.removeDuplicates=false;//cant delete duplicates because experimental params might be different but still have same number value
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.init(propertyName);//in parent Parse class
		p.propertyName=propertyName;
		p.createFiles();		
	}

	
	public static void main(String[] args) {
		BCFUtilities b=new BCFUtilities();
		b.ParseBCFSources();
	}

}

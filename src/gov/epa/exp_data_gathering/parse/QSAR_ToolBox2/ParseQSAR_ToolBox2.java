package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.poi.util.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BCFUtilities;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.ParseQSAR_ToolBox;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox2.RecordQSAR_ToolBox2.Species;

public class ParseQSAR_ToolBox2 extends Parse {

	public static String fileNameAcuteToxicityDB = "acute oral toxicity db.xlsx";
	public static String fileNameAcuteToxicityEchaReach = "echa reach acute toxicity by test material.xlsx";
	public static String fileNameSensitizationEchaReach = "echa reach sensitization by test material.xlsx";
	public static String fileNameSensitization = "skin sensitization.xlsx";

	public static String fileNameBCF_Canada = "bioaccumulation canada v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_CEFIC = "bioaccumulation fish CEFIC LRI v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_NITE = "Bioconcentration and logKow NITE v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_ECHA_REACH = "bcfbaf echa reach v.4.8.2 2026-06-02.xlsx";

	public static String fileNameBCF_CanadaNew = "new files" + File.separator
			+ "bioaccumulation canada v.4.9 2026-08-04.xlsx";
	public static String fileNameBCF_CEFICNew = "new files" + File.separator
			+ "bioaccumulation fish CEFIC LRI v.4.9 2026-08-04.xlsx";
	public static String fileNameBCF_NITENew = "new files" + File.separator
			+ "Bioconcentration and LogKow NITE 4.9 2026-11-04.xlsx";
	public static String fileNameBCF_ECHA_REACHNew = "new files" + File.separator
			+ "bcfbaf echa reach v.4.9 2026-11-20.xlsx";
	public static String fileNameBCF_ECHA_REACHNew2 = "new files" + File.separator
			+ "REACH Bioaccumulation database (normalized) v.4.9 2026-08-04.xlsx";

	public static String fileName96hrAcuteAquatic = "96 hour aquatic toxicity.xlsx";
	public static String fileNamePhyschem = "echa reach physchem properties.xlsx";
	public static String fileNameKoc = "echa reach koc with name UTF-8.tsv";
	public static String fileNameBiodegWaterScreeningECHA_REACHNew = "new files" + File.separator
			+ "biodegradation in water screening tests v.4.9 2026-08-04.xlsx";
	public static String fileNameBiodegNITENew = "new files" + File.separator
			+ "biodegradation NITE 4.9 2026-11-06.xlsx";

	String original_source_name;
	List<String> selectedEndpoints;

	String propertyName;
	String outputTestGuideline;
	String outputProperty;
	String sourceNameWithDate;
	String fileName;
	String fileDate;
	private String charset;

	public ParseQSAR_ToolBox2(String fileName) {
		sourceName = RecordQSAR_ToolBox2.sourceName;
		this.fileName = fileName;
		this.init();

		if (fileName.equals(fileNameAcuteToxicityEchaReach)) {
			removeDuplicates = true;
			original_source_name = "ECHA Reach";
			selectedEndpoints = Arrays.asList("Dermal rabbit LD50", "Dermal rat LD50", "Inhalation mouse LC50",
					"Inhalation rat LC50", "Oral mouse LD50", "Oral rat LD50");
			init("Acute toxicity ECHA Reach");

		} else if (fileName.equals(fileNameAcuteToxicityDB)) {
			removeDuplicates = true;
			original_source_name = "Acute oral toxicity db";
			selectedEndpoints = Arrays.asList("Dermal rabbit LD50", "Dermal rat LD50", "Inhalation mouse LC50",
					"Inhalation rat LC50", "Oral mouse LD50", "Oral rat LD50");
			init("Acute toxicity oral toxicity db");

		} else if (fileName.equals(fileNamePhyschem)) {
			removeDuplicates = true;
			original_source_name = "ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strLogKOW,
					ExperimentalConstants.strWaterSolubility);
			init("Physchem ECHA Reach");

		} else if (fileName.equals(fileNameSensitizationEchaReach)) {
			removeDuplicates = false;
			original_source_name = "ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strSkinSensitizationLLNA);
			init("Sensitization ECHA Reach");

		} else if (fileName.equals(fileNameKoc)) {
			removeDuplicates = false;
			original_source_name = "ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strKOC);
			init("Koc ECHA Reach");

		} else if (fileName.equals(fileNameSensitization)) {
			removeDuplicates = false;
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strSkinSensitizationLLNA);
			init("Sensitization");

		} else if (fileName.equals(fileNameBCF_Canada)) {
			init("bioaccumulation canada v.4.8.2");

		} else if (fileName.equals(fileNameBCF_NITE)) {
			init("Bioconcentration and logKow NITE v.4.8.2");

		} else if (fileName.equals(fileNameBCF_CEFIC)) {
			init("bioaccumulation fish CEFIC LRI v.4.8.2");

		} else if (fileName.equals(fileNameBCF_ECHA_REACH)) {
			init("BCFBAF ECHA REACH v.4.8.2");

		} else if (fileName.equals(fileNameBCF_CEFICNew)) {
			init("bioaccumulation fish CEFIC LRI v.4.9");

		} else if (fileName.equals(fileNameBCF_CanadaNew)) {
			init("bioaccumulation canada v.4.9");

		} else if (fileName.equals(fileNameBCF_NITENew)) {
			init("Bioconcentration and logKow NITE v.4.9");

		} else if (fileName.equals(fileNameBCF_ECHA_REACHNew)) {
			init("BCFBAF ECHA REACH v.4.9");

		} else if (fileName.equals(fileNameBCF_ECHA_REACHNew2)) {
			init("REACH Bioaccumulation database (normalized) v.4.9");
		}
	}

	public ParseQSAR_ToolBox2(String propertyName, String outputGuideline, String fileName, String fileDate) {
		this.sourceName = RecordQSAR_ToolBox2.sourceName;
		this.propertyName = propertyName;
		this.fileName = fileName;

		if (fileName.equals(fileNameBiodegWaterScreeningECHA_REACHNew) || fileName.equals(fileNameBiodegNITENew)) {
			this.propertyName = propertyName;
			this.outputTestGuideline = outputGuideline;
			this.outputProperty = propertyName;
			this.fileName = fileName;
			this.fileDate = fileDate;

			String src = null;
			if (fileName.equals(fileNameBiodegWaterScreeningECHA_REACHNew)) {
				src = "ECHA REACH";
			} else if (fileName.equals(fileNameBiodegNITENew)) {
				src = "NITE";
			}

			if (ExperimentalConstants.strPercentageBiodegradation.equalsIgnoreCase(propertyName)) {
				init("Percent Biodegradation " + outputGuideline + " " + src + " " + fileDate);
			} else if (ExperimentalConstants.strRBIODEG.equalsIgnoreCase(propertyName)) {
				init("RBiodeg " + outputGuideline + " " + src + " " + fileDate);
			} else if (ExperimentalConstants.strRBIODEG_multiclass.equalsIgnoreCase(propertyName)) {
				init("RBiodeg multiclass " + outputGuideline + " " + src + " " + fileDate);
			} else if (ExperimentalConstants.strRBIODEG_10_day.equalsIgnoreCase(propertyName)) {
				init("RBiodeg 10 day " + outputGuideline + " " + src + " " + fileDate);
			}

		} else if (fileName.equals(fileName96hrAcuteAquatic)) {
			removeDuplicates = true;
			original_source_name = "ECHA REACH";
			selectedEndpoints = Arrays.asList(propertyName);
			mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName + File.separator
					+ "Fish tox ECHA";
			mainFolder += File.separator + propertyName;
			jsonFolder = mainFolder;
			new File(mainFolder).mkdirs();
		}
	}

	@Override
	protected void createOriginalRecords() {
		if (generateOriginalJSONRecords) {
			if (fileName.contains(".xlsx")) {
				Vector<JsonObject> records = RecordQSAR_ToolBox2.parseQSAR_ToolBoxRecordsFromExcel(fileName,
						sourceName);
				writeOriginalRecordsToFile(records);
			} else if (fileName.contains(".tsv")) {
				List<JsonObject> records = RecordQSAR_ToolBox2.parseQSAR_ToolBoxRecordsFromTextFile(fileName,
						sourceName);
				writeOriginalRecordsToFile(records);
			}
		}
	}
	
	private boolean isBcfFile() {
		return fileName.equals(fileNameBCF_CanadaNew) || fileName.equals(fileNameBCF_Canada)
				|| fileName.equals(fileNameBCF_CEFICNew) || fileName.equals(fileNameBCF_CEFIC)
				|| fileName.equals(fileNameBCF_NITENew) || fileName.equals(fileNameBCF_NITE)
				|| fileName.equals(fileNameBCF_ECHA_REACHNew) || fileName.equals(fileNameBCF_ECHA_REACHNew2)
				|| fileName.equals(fileNameBCF_ECHA_REACH);
	}

	private String requiredDatabaseForFile() {
		if (fileName.equals(fileNameBCF_CanadaNew) || fileName.equals(fileNameBCF_Canada)) {
			return "Bioaccumulation Canada";
		} else if (fileName.equals(fileNameBCF_CEFICNew) || fileName.equals(fileNameBCF_CEFIC)) {
			return "Bioaccumulation fish CEFIC LRI";
		} else if (fileName.equals(fileNameBCF_NITENew) || fileName.equals(fileNameBCF_NITE)) {
			return "Bioconcentration and logKow NITE";
		} else if (fileName.equals(fileNameBCF_ECHA_REACHNew) || fileName.equals(fileNameBCF_ECHA_REACHNew2)
				|| fileName.equals(fileNameBCF_ECHA_REACH)) {
			return "ECHA REACH";
		}
		return null;
	}
	
	
	private boolean shouldKeepByEndpoint(ExperimentalRecord er) {
		if (selectedEndpoints == null) {
			return true;
		}
		return er != null && er.property_name != null && selectedEndpoints.contains(er.property_name);
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental = new ExperimentalRecords();

		try {
			Type type = new TypeToken<Hashtable<String, List<RecordQSAR_ToolBox2.Species>>>() {
			}.getType();
			Hashtable<String, List<Species>> htSpeciesByCommonName = JsonUtilities.gsonPretty
					.fromJson(new FileReader("data\\experimental\\Arnot 2006\\htSuperCategory.json"), type);

			Hashtable<String, Species> htSpeciesByBinomalName = getHashtableSpeciesByBinomialName(
					htSpeciesByCommonName);

			File folder = new File(jsonFolder);
			if (folder.listFiles() == null) {
				System.out.println("No files in json folder:" + jsonFolder);
				return null;
			}

			List<RecordQSAR_ToolBox2> tempRecords = getOriginalRecordsFromJsonFiles(jsonFolder,
					RecordQSAR_ToolBox2[].class, this.charset);


			String requiredDatabase = requiredDatabaseForFile();
			boolean filterByDatabase = requiredDatabase != null;
			boolean filterByEndpoint = selectedEndpoints != null;

			for (RecordQSAR_ToolBox2 record : tempRecords) {

				if (record.Database == null) {
					continue;
				}

				if (filterByDatabase && !requiredDatabase.equals(record.Database)) {
					continue;
				}

				ExperimentalRecord er = record.toExperimentalRecord(this, htSpeciesByBinomalName, htSpeciesByCommonName);
				if (er == null) {
					continue;
				}

				if (!filterByEndpoint || shouldKeepByEndpoint(er)) {
					recordsExperimental.add(er);
				}
			}
			

			CompareExperimentalRecords cer = new CompareExperimentalRecords();
			if (!recordsExperimental.isEmpty()) {
				String propertyName = recordsExperimental.get(0).property_name;
				TreeMap<String, ExperimentalRecords> tm = cer.rm.getTreeMapByCAS(propertyName,
						ExperimentalConstants.str_binary, recordsExperimental);
				System.out.println("Number of unique casrns:" + tm.size());
			}

		} catch (

		Exception ex) {
			ex.printStackTrace();
		}

		if (recordsExperimental.size() == 0) {
			return recordsExperimental;
		}

		ExperimentalRecord er = recordsExperimental.get(0);
		if (er.property_name != null && er.property_name.equals(ExperimentalConstants.strBCF)) {
			String units = ExperimentalConstants.str_L_KG;
			Hashtable<String, ExperimentalRecords> htER = recordsExperimental
					.createExpRecordHashtableByCAS(er.property_name, units, true);
			boolean convertToLog = true;
			boolean omitSingleton = true;
			ExperimentalRecords.calculateAvgStdDevOverAllChemicals(htER, convertToLog, omitSingleton);
		}

		return recordsExperimental;
	}

	private Hashtable<String, Species> getHashtableSpeciesByBinomialName(
			Hashtable<String, List<Species>> htSpeciesByCommonName) {
		Hashtable<String, Species> htSpeciesByBinomalName = new Hashtable<>();
		for (String speciesCommon : htSpeciesByCommonName.keySet()) {
			List<Species> speciesList = htSpeciesByCommonName.get(speciesCommon);
			for (Species species : speciesList) {
				if (species.species_scientific == null)
					continue;
				htSpeciesByBinomalName.put(species.species_scientific, species);
			}
		}
		return htSpeciesByBinomalName;
	}


	static void run96hrAcuteFishTox() {
			
		ParseQSAR_ToolBox2 p = new ParseQSAR_ToolBox2(ExperimentalConstants.strAcuteAquaticToxicity,null, fileName96hrAcuteAquatic, null);
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=true;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}
	
	
	public static void runBCF() {
		
	// findMissingFieldsInRecordClass();
		
		// String [] filenames= {fileNameBCF_ECHA_REACH, fileNameBCF_Canada, fileNameBCF_CEFIC, fileNameBCF_NITE};
		// String [] filenames= {fileNameBCF_ECHA_REACH};
		// String [] filenames= {fileNameBCF_Canada};
		// String [] filenames= {fileNameBCF_CEFIC};
		// 
//		String [] filenames = {fileNameBCF_ECHA_REACHNew, fileNameBCF_CanadaNew, fileNameBCF_CEFICNew, fileNameBCF_NITENew, fileNameBCF_ECHA_REACHNew2};
//		
//		String [] filenames = {fileNameBCF_NITENew};//TODO get from "NITE BCF 000064113.xlsx" instead? compare CAS numbers
//		TODO assign flow-through for NITE records with guideline not set to flow-through since most of records in that spreadsheet are flow-through and Japanese are consistent in the method they use
		
		
//		String[] filenames = { fileNameBCF_ECHA_REACHNew };
//		String [] filenames= {fileNameBCF_NITENew, fileNameBCF_ECHA_REACHNew};
		String [] filenames= {fileNameBCF_NITENew};
		RecordQSAR_ToolBox2.omitBasedOnGLPCompliance = true;
		
		for (String filename : filenames) {
			ParseQSAR_ToolBox2 p = new ParseQSAR_ToolBox2(filename);
			p.charset = "UTF-8";
			p.generateOriginalJSONRecords = true;
			p.removeDuplicates = false;
			p.writeJsonExperimentalRecordsFile = true;
			p.writeExcelExperimentalRecordsFile = true;
			p.writeExcelFileByProperty = true;
			p.writeCheckingExcelFile = false;
			p.createFiles();
			
			//Print here so that output isnt cluttered:
			System.out.println("********************************************\n");
			System.out.println("Bad response sites:"
					+ JsonUtilities.gsonPretty.toJson(BCFUtilities.ResponseSiteFormatter.badSites));
			System.out.println("Unhandled response sites:"
					+ JsonUtilities.gsonPretty.toJson(BCFUtilities.ResponseSiteFormatter.unhandledSites));
		}
	}
	

	static void runKoc() {
		
		IOUtils.setByteArrayMaxOverride(200000000);
		ParseQSAR_ToolBox p=new ParseQSAR_ToolBox(fileNameKoc);
		
//		ExcelSourceReader.encoding="ISO-8859-1";//gets degrees but not <=
//		ExcelSourceReader.encoding="UTF-16";//no
//		ExcelSourceReader.encoding="UTF-8";
//		ExcelSourceReader.encoding="ASCII";//no
//		System.out.println("5 ≤ 10");
//		JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("qualifier", "≤");
//        System.out.println(RecordQSAR_ToolBox.gson.toJson(jsonObject));
		
		p.maxExcelRows=100000;
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=false;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
		
		
		//TODO when creating data set use data for Media=soil and measurement_method=Batch Equilibrium Method or HPLC Estimation Method
		
	}
	
	
	static void runPhyschem() {
		
		IOUtils.setByteArrayMaxOverride(200000000);
		ParseQSAR_ToolBox p=new ParseQSAR_ToolBox(fileNamePhyschem);
		
//		ExcelSourceReader.encoding="ISO-8859-1";//gets degrees but not <=
//		ExcelSourceReader.encoding="UTF-16";//no
//		ExcelSourceReader.encoding="UTF-8";
//		ExcelSourceReader.encoding="ASCII";//no
		
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=true;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}

	private static void runParse(String endpoint, String guideline, String fileName, String date) {
	    ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(endpoint, guideline, fileName, date);
	    p.maxExcelRows = 100000;
	    p.generateOriginalJSONRecords = true;
	    p.removeDuplicates = false;
	    p.writeJsonExperimentalRecordsFile = true;
	    p.writeExcelExperimentalRecordsFile = true;
	    p.writeExcelFileByProperty = false;
	    p.writeCheckingExcelFile = false;
	    p.createFiles();
	}
	
	
	static void runBiodegWaterScreening() {
		
//		ExcelSourceReader.encoding="ISO-8859-1";//gets degrees but not <=
//		ExcelSourceReader.encoding="UTF-16";//no
//		ExcelSourceReader.encoding="UTF-8";
//		ExcelSourceReader.encoding="ASCII";//no

		IOUtils.setByteArrayMaxOverride(200000000);
		
		RecordQSAR_ToolBox.omitBasedOnGLPCompliance=true;

		runParse(ExperimentalConstants.strRBIODEG,
		         ExperimentalConstants.str301F,
		         fileNameBiodegWaterScreeningECHA_REACHNew,
		         "2026-08-04");

//		runParse(ExperimentalConstants.strRBIODEG_10_day,
//		         ExperimentalConstants.str301F,
//		         fileNameBiodegWaterScreeningECHA_REACHNew,
//		         "2026-08-04");
//		
//		runParse(ExperimentalConstants.strRBIODEG_multiclass,
//        ExperimentalConstants.str301F,
//        fileNameBiodegWaterScreeningECHA_REACHNew,
//        "2026-08-04");
//
//		runParse(ExperimentalConstants.strRBIODEG,
//		         ExperimentalConstants.str301C,
//		         fileNameBiodegWaterScreeningECHA_REACHNew,
//		         "2026-08-04");
//		
//		runParse(ExperimentalConstants.strRBIODEG,
//		         ExperimentalConstants.str301C,
//		         fileNameBiodegNITENew,
//		         "2026-11-06");

	}
	public static void main(String[] args) {
		runBCF();
//		runBiodegWaterScreening();
//		runKoc();
//		run96hrAcuteFishTox();
//		runPhyschem();
		
//		
//		

	}
}
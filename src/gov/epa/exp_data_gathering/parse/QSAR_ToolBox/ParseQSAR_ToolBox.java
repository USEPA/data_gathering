package gov.epa.exp_data_gathering.parse.QSAR_ToolBox;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.poi.util.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords.ExperimentalRecordManipulator;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox.Species;

public class ParseQSAR_ToolBox extends Parse {
	
	String propertyName;

	public static String fileNameAcuteToxicityDB="acute oral toxicity db.xlsx";
	public static String fileNameAcuteToxicityEchaReach="echa reach acute toxicity by test material.xlsx";
	public static String fileNameSensitizationEchaReach="echa reach sensitization by test material.xlsx";
	public static String fileNameSensitization="skin sensitization.xlsx";
	public static String fileNameBCFCanada="Bioaccumulation Canada.xlsx";
	public static String fileNameBCFCEFIC="Bioaccumulation Fish CEFIC LRI.xlsx";
	public static String fileNameBCFNITE="Bioconcentration and LogKow NITE v2.xlsx";
	public static String fileName96hrAcuteAquatic="96 hour aquatic toxicity.xlsx";
	public static String fileNamePhyschem="echa reach physchem properties.xlsx";
	
	public static String fileNameKoc="echa reach koc with name UTF-8.tsv";
	
//	static String fileName=fileNameAcuteToxicityEchaReach;
//	static String fileName=fileNameAcuteToxicityDB;
//	static String fileName=fileNameSensitizationEchaReach;
//	static public String fileName=fileNameSensitization;
//	static String fileName=fileNameBCFCEFIC;
//	static String fileName=fileNameBCFCEFIC;
//	static String fileName=fileNameBCFCanada;
//	static String fileName=fileNameBCFNITE;
//	static String fileName=fileName96hrAcuteAquatic;
	static String fileName=fileNameKoc;
	
	
	String original_source_name;
	List<String>selectedEndpoints;
	
	public ParseQSAR_ToolBox(String propertyName) {
		this.propertyName=propertyName;
		sourceName = RecordQSAR_ToolBox.sourceName; // TODO Consider creating ExperimentalConstants.strSourceQSAR_ToolBox instead.
		this.init();
//		mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
//		jsonFolder= mainFolder;
//		new File(mainFolder).mkdirs();
		
		System.out.println("ParseQSAR_ToolBox():\t"+fileName+"\t"+propertyName);
		
		if(fileName.equals(fileNameAcuteToxicityEchaReach)) {
			removeDuplicates=true;
			
			original_source_name="ECHA Reach";
			selectedEndpoints = Arrays.asList("Dermal rabbit LD50", "Dermal rat LD50", "Inhalation mouse LC50",
					"Inhalation rat LC50", "Oral mouse LD50", "Oral rat LD50");
			init("Acute toxicity ECHA Reach");
			

		} else if (fileName.equals(fileNameAcuteToxicityDB)) {
			removeDuplicates=true;
			original_source_name="Acute oral toxicity db";
			selectedEndpoints = Arrays.asList("Dermal rabbit LD50", "Dermal rat LD50", "Inhalation mouse LC50",
					"Inhalation rat LC50", "Oral mouse LD50", "Oral rat LD50");
			init("Acute toxicity oral toxicity db");

		} else if (fileName.equals(fileNamePhyschem)) {
			removeDuplicates=true;
			original_source_name="ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strLogKOW,ExperimentalConstants.strWaterSolubility);
			init("Physchem ECHA Reach");
			
		} else if (fileName.equals(fileNameSensitizationEchaReach)) {
			removeDuplicates=false;
			original_source_name="ECHA Reach";
			
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strSkinSensitizationLLNA);
			
//			selectedEndpoints = Arrays.asList(ExperimentalConstants.strSkinSensitizationLLNA_EC3,
//					ExperimentalConstants.strSkinSensitizationLLNA_SI);
			
			
			init("Sensitization ECHA Reach");
			
			
		} else if (fileName.equals(fileNameKoc)) {
			removeDuplicates=false;
			original_source_name="ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strKOC);
			init("Koc ECHA Reach");
			
		} else if (fileName.equals(fileNameSensitization)) {
			removeDuplicates=false;
//			original_source_name="ECHA Reach";
			selectedEndpoints = Arrays.asList(ExperimentalConstants.strSkinSensitizationLLNA);
			init("Sensitization");
		} else if (fileName.equals(fileNameBCFCanada)) {
			removeDuplicates=true;
			original_source_name="Canada";
			selectedEndpoints = Arrays.asList(propertyName);
			mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName + File.separator+"BCF Canada";
			mainFolder+=File.separator+propertyName;//output json/excel in subfolder
			jsonFolder= mainFolder;
			new File(mainFolder).mkdirs();
		} else if (fileName.equals(fileNameBCFNITE)) {
			removeDuplicates=true;
			original_source_name="NITE";
			selectedEndpoints = Arrays.asList(propertyName);
			mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName + File.separator+"BCF NITE";
			mainFolder+=File.separator+propertyName;//output json/excel in subfolder;
			jsonFolder= mainFolder;
			new File(mainFolder).mkdirs();
		} else if (fileName.equals(fileNameBCFCEFIC)) {
			removeDuplicates=true;
			original_source_name="CEFIC";
			selectedEndpoints = Arrays.asList(propertyName);
			mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName + File.separator+"BCF CEFIC";
			mainFolder+=File.separator+propertyName;//output json/excel in subfolder
			jsonFolder= mainFolder;
			new File(mainFolder).mkdirs();
		} else if (fileName.equals(fileName96hrAcuteAquatic)) {
			removeDuplicates=true;
			original_source_name="ECHA REACH";
			selectedEndpoints = Arrays.asList(propertyName);
			mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName + File.separator+"Fish tox ECHA";
			mainFolder+=File.separator+propertyName;//output json/excel in subfolder
			jsonFolder= mainFolder;
			new File(mainFolder).mkdirs();
		}
		
	}

	@Override
	protected void createRecords() {
		if(generateOriginalJSONRecords) {
			
			if(fileName.contains(".xlsx")) {
				Vector<JsonObject> records = RecordQSAR_ToolBox.parseQSAR_ToolBoxRecordsFromExcel(fileName,sourceName);
				writeOriginalRecordsToFile(records);
			} else if (fileName.contains(".tsv")) {
				List<JsonObject> records = RecordQSAR_ToolBox.parseQSAR_ToolBoxRecordsFromTextFile(fileName,sourceName);
				writeOriginalRecordsToFile(records);
			}
			
		}
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		try {
			
			Type type = new TypeToken<Hashtable<String, List<RecordQSAR_ToolBox.Species>>>(){}.getType();
			Hashtable<String, List<Species>>htSpecies=JsonUtilities.gsonPretty.fromJson(new FileReader("data\\experimental\\Arnot 2006\\htSuperCategory.json"), type);

			File Folder=new File(jsonFolder);
			
			if(Folder.listFiles()==null) {
				System.out.println("No files in json folder:"+jsonFolder);
				return null;
			}
			
			List<RecordQSAR_ToolBox>tempRecords=getOriginalRecordsFromJsonFiles(jsonFolder, RecordQSAR_ToolBox[].class);
			
			for (RecordQSAR_ToolBox recordQSAR_ToolBox:tempRecords) {
				
				//Can only filter by whole body if filename is CEFIC
				if(fileName.equals(fileNameBCFCEFIC)) {
					
					ExperimentalRecord erKinetic=recordQSAR_ToolBox.toExperimentalRecordBCF_NITE_Kinetic(propertyName, htSpecies);
					if(erKinetic!=null)	recordsExperimental.add(erKinetic);
	
					ExperimentalRecord erSS=recordQSAR_ToolBox.toExperimentalRecordBCF_NITE_SS(propertyName, htSpecies);
					if(erSS!=null)	recordsExperimental.add(erSS);
					
				} else if(fileName.equals(fileNameBCFCanada)) {
					ExperimentalRecord erCanada=recordQSAR_ToolBox.toExperimentalRecordBCFCanada(propertyName);
					if(erCanada!=null)	recordsExperimental.add(erCanada);
				} else if(fileName.equals(fileNameBCFNITE)) {
					ExperimentalRecord erNITE=recordQSAR_ToolBox.toExperimentalRecordBCFNITE(propertyName, htSpecies);
					if(erNITE!=null)	recordsExperimental.add(erNITE);
				
				} else if(fileName.equals(fileName96hrAcuteAquatic)) {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecordFishTox(propertyName, htSpecies);
					if(er!=null)	recordsExperimental.add(er);

				} else if(fileName.equals(fileNameKoc)) {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecord(original_source_name);

					er.experimental_parameters.put("Chemical_Number", recordQSAR_ToolBox.Chemical_Number);
					
					
					if(er!=null)	{
						recordsExperimental.add(er);
						
						if(er.keep && er.property_name.equals(ExperimentalConstants.strKd)) {
							Double OC=recordQSAR_ToolBox.getMeanOrganicCarbonValue();

							if(OC!=null && OC>0) {					
								ExperimentalRecord erKoc=createKocRecordFromKd(er, OC);
//								System.out.println(gson.toJson(erKoc));
								recordsExperimental.add(erKoc);
							}
						}
						
//						String json=gson.toJson(recordQSAR_ToolBox).toLowerCase();
//						if(er.keep && (json.contains("estimat") || json.contains("calculat") || 
//								json.contains("model") || json.contains("qsar") || json.contains("equation")
//								|| json.contains("caculation"))) {
//							System.out.println(json+"\n\n");
//						}
						
					
					}

					
				} else {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecord(original_source_name);
					if(selectedEndpoints.contains(er.property_name))		
						recordsExperimental.add(er);
				}
			}//loop over records
			
//			addMissingDensities(true);//uses API to add extra entries to data/density.txt


		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByCAS(ExperimentalConstants.strKOC, ExperimentalConstants.str_L_KG,true);
//		Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByCAS(ExperimentalConstants.str_g_L,true);
		boolean convertToLog=true;
		boolean omitSingleton=false;
		ExperimentalRecords.calculateAvgStdDevOverAllChemicals(htER, convertToLog,omitSingleton);
		
		
		compareKoc(recordsExperimental);
		
		
		
//		System.out.println(gson.toJson(tm.get("soil")));
		
		
		
		
		
		return recordsExperimental;
	}

	private void compareKoc(ExperimentalRecords recordsExperimental) {
		TreeMap<String,TreeMap<String,ExperimentalRecords>>tm=new TreeMap<>();
		
		for(ExperimentalRecord er:recordsExperimental) {
			
			if(!er.keep) continue;
			if(!er.property_name.equals(ExperimentalConstants.strKOC)) continue;
			
			String media=er.experimental_parameters.get("Media")+"";
			String chemicalNumber=er.experimental_parameters.get("Chemical_Number")+"";
			
			if(tm.containsKey(media)) {
				Map<String,ExperimentalRecords>mapByChemNumber=tm.get(media);
				
				if(mapByChemNumber.containsKey(chemicalNumber)) {
					ExperimentalRecords recs=mapByChemNumber.get(chemicalNumber);
					recs.add(er);
				} else {
					ExperimentalRecords recs=new ExperimentalRecords();
					recs.add(er);
					mapByChemNumber.put(chemicalNumber, recs);
				}
			} else {
				TreeMap<String,ExperimentalRecords>mapByChemNumber=new TreeMap<>();
				tm.put(media, mapByChemNumber);
				ExperimentalRecords recs=new ExperimentalRecords();
				recs.add(er);
				mapByChemNumber.put(chemicalNumber, recs);
			}
			
			
//			System.out.println(chemicalNumber+media+"\t"+er.property_value_point_estimate_final);
			
			
		}
		
		CompareExperimentalRecords cer=new CompareExperimentalRecords();

		
		String units=ExperimentalConstants.str_L_KG;
		
		
		List<String>medias=new ArrayList<>();
		for(String media:tm.keySet()) {
			TreeMap<String,ExperimentalRecords>mapByChemNumber=tm.get(media);
			
			ExperimentalRecordManipulator.setMedianValues(mapByChemNumber, units);
			medias.add(media);
		}

		String media1="soil";
		TreeMap<String,ExperimentalRecords>map1=tm.get(media1);


		for (int j=0;j<medias.size();j++) {
			String media2=medias.get(j);	
			if(media2.equals(media1)) continue;
			TreeMap<String,ExperimentalRecords>map2=tm.get(media2);
			
			JsonObject jo=new JsonObject();
			
			cer.cm.compareChemicalsInCommon(media1,media2,map1, map2, units,jo);
		}
		
		
//		String media1="soil";
//		String media2="soil/sewage sludge";
//		TreeMap<String,ExperimentalRecords>map1=tm.get(media1);
//		TreeMap<String,ExperimentalRecords>map2=tm.get(media2);
//		cer.cm.compareChemicalsInCommon(media1,media2,map1, map2, units);
	}

	private ExperimentalRecord createKocRecordFromKd(ExperimentalRecord er, Double OC) {

		
//		System.out.println("\nEnter createKocRecordFromKd for "+er.casrn+"\t"+er.chemical_name);
		
		ExperimentalRecord erKoc=er.clone();
		
		erKoc.property_name=ExperimentalConstants.strKOC;
		erKoc.property_value_string="Kd = "+er.property_value_string+"; %OC = "+OC;
		
		
		if(erKoc.property_value_min_original!=null) {
			erKoc.property_value_min_original=erKoc.property_value_min_original*100/OC;
		}
		
		if(erKoc.property_value_max_original!=null) {
			erKoc.property_value_max_original=erKoc.property_value_max_original*100/OC;
		}
		
		if(erKoc.property_value_point_estimate_original!=null) {
			erKoc.property_value_point_estimate_original=erKoc.property_value_point_estimate_original*100/OC;
		}

		erKoc.property_value_min_final=null;
		erKoc.property_value_max_final=null;
		erKoc.property_value_point_estimate_final=null;

		RecordQSAR_ToolBox.unitConverter.convertBCF(erKoc);
		
		erKoc.updateNote("Koc calculated from Kd and %OC");
		
//		System.out.println("point_estimate_original="+erKoc.property_value_point_estimate_original);
//		System.out.println("point_estimate_final="+erKoc.property_value_point_estimate_final);
		
		
		return erKoc;
	}
	
	static void runBCF() {
		
		fileName=fileNameBCFNITE;

		List<String>properties=new ArrayList<>();
		
		properties.add(ExperimentalConstants.strBCF);
		properties.add(ExperimentalConstants.strFishBCF);		
		if(!fileName.equals(fileNameBCFCanada))properties.add(ExperimentalConstants.strFishBCFWholeBody);
		
		for (String propertyName:properties) {
			System.out.println(propertyName);
			ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(propertyName);
			p.generateOriginalJSONRecords=false;
			p.removeDuplicates=true;
			p.writeJsonExperimentalRecordsFile=true;
			p.writeExcelExperimentalRecordsFile=true;
			p.writeExcelFileByProperty=true;		
			p.writeCheckingExcelFile=false;//creates random sample spreadsheet
			p.createFiles();
			System.out.println("********************************************\n");
		}
		
	}
	
	static void runPhyschem() {
		
		IOUtils.setByteArrayMaxOverride(200000000);
		
		fileName=fileNamePhyschem;

		ParseQSAR_ToolBox p=new ParseQSAR_ToolBox(null);
		
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
	

	static void runKoc() {
		
		IOUtils.setByteArrayMaxOverride(200000000);
		
		fileName=fileNameKoc;

		ParseQSAR_ToolBox p=new ParseQSAR_ToolBox(null);
		
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
	
	static void run96hrAcuteFishTox() {
		
		String propertyName=ExperimentalConstants.strAcuteAquaticToxicity;
		
		fileName=fileName96hrAcuteAquatic;
		
		ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(propertyName);
		
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=true;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
		
	}
	
	
	public static void main(String[] args) {

//		UnitConverter.printMissingDensityCas=true;
		
//		runBCF();
//		run96hrAcuteFishTox();
//		runPhyschem();
		runKoc();

//******************************************************************************
//		fileName=fileNameAcuteToxicityDB;
//		ParseQSAR_ToolBox p=new ParseQSAR_ToolBox(null);
//		p.generateOriginalJSONRecords=true;
//		p.removeDuplicates=true;
//		p.writeJsonExperimentalRecordsFile=true;
//		p.writeExcelExperimentalRecordsFile=true;
//		p.writeExcelFileByProperty=true;		
//		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
//		p.createFiles();

		
	}
}
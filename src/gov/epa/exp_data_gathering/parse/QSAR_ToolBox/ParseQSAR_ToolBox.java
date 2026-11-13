package gov.epa.exp_data_gathering.parse.QSAR_ToolBox;

import java.io.File;
import java.io.FileReader;
//import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.poi.util.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BiodegradationPropertyValues;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords.ExperimentalRecordManipulator;
import gov.epa.exp_data_gathering.parse.EChemPortal.RecordEChemPortal;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.JsonFieldChecker;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.TsvToExcel;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox.Species;

public class ParseQSAR_ToolBox extends Parse {
	

	public static String fileNameAcuteToxicityDB="acute oral toxicity db.xlsx";
	public static String fileNameAcuteToxicityEchaReach="echa reach acute toxicity by test material.xlsx";
	public static String fileNameSensitizationEchaReach="echa reach sensitization by test material.xlsx";
	public static String fileNameSensitization="skin sensitization.xlsx";
	
//	public static String fileNameBCFCanada="Bioaccumulation Canada.xlsx";
//	public static String fileNameBCFCEFIC="Bioaccumulation Fish CEFIC LRI.xlsx";
//	public static String fileNameBCFNITE="Bioconcentration and LogKow NITE v2.xlsx";

	
	public static String fileNameBCF_Canada="bioaccumulation canada v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_CEFIC="bioaccumulation fish CEFIC LRI v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_NITE="Bioconcentration and logKow NITE v.4.8.2 2026-06-02.xlsx";
	public static String fileNameBCF_ECHA_REACH="bcfbaf echa reach v.4.8.2 2026-06-02.xlsx";//TODO

	// New files as of 2026-08-04 TODO
	public static String fileNameBCF_CanadaNew = "new files" + File.separator + "bioaccumulation canada v.4.9 2026-08-04.xlsx";
	public static String fileNameBCF_CEFICNew = "new files" + File.separator + "bioaccumulation fish CEFIC LRI v.4.9 2026-08-04.xlsx";
	public static String fileNameBCF_NITENew = "new files" + File.separator + "Bioconcentration and LogKow NITE 4.9 2026-11-04.xlsx";
	public static String fileNameBCF_ECHA_REACHNew = "new files" + File.separator + "bcfbaf echa reach v.4.9 2026-08-04.xlsx";
	public static String fileNameBCF_ECHA_REACHNew2 = "new files" + File.separator + "REACH Bioaccumulation database (normalized) v.4.9 2026-08-04.xlsx";
	
	public static String fileName96hrAcuteAquatic="96 hour aquatic toxicity.xlsx";
	public static String fileNamePhyschem="echa reach physchem properties.xlsx";
	
	public static String fileNameKoc="echa reach koc with name UTF-8.tsv";
//	public static String fileNameBiodegWaterScreening="biodegradation in water screening tests 2026-04-013.tsv";
	public static String fileNameBiodegWaterScreeningECHA_REACHNew="new files" + File.separator+"biodegradation in water screening tests v.4.9 2026-08-04.xlsx";	
	public static String fileNameBiodegNITENew="new files" + File.separator+"biodegradation NITE 4.9 2026-11-06.xlsx";	
	
	
//	static String fileName=fileNameAcuteToxicityEchaReach;
//	static String fileName=fileNameAcuteToxicityDB;
//	static String fileName=fileNameSensitizationEchaReach;
//	static public String fileName=fileNameSensitization;
//	static String fileName=fileNameBCFCEFIC;
//	static String fileName=fileNameBCFCanada;
//	static String fileName=fileNameBCFNITE;
//	static String fileName=fileName96hrAcuteAquatic;
//	static String fileName=fileNameKoc;
	
	
	String original_source_name;
	List<String>selectedEndpoints;
	
	String propertyName;
	String outputTestGuideline;
	String outputProperty;
	String sourceNameWithDate;
	String fileName;
	String fileDate;
	
	
	public ParseQSAR_ToolBox(String fileName) {
		sourceName = RecordQSAR_ToolBox.sourceName; // TODO Consider creating ExperimentalConstants.strSourceQSAR_ToolBox instead.
		this.fileName =fileName;
		
		this.init();//TODO is this redundant?
		
//		mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
//		jsonFolder= mainFolder;
//		new File(mainFolder).mkdirs();
		
//		System.out.println("ParseQSAR_ToolBox():\t"+fileName+"\t"+propertyName);
		
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
//		} else if (fileName.equals(fileNameBiodegWaterScreeningECHA_REACH)) {
//			removeDuplicates=false;
//			original_source_name="ECHA Reach";
//			selectedEndpoints = Arrays.asList(ExperimentalConstants.strRBIODEG);
//			init("RBiodeg 301F ECHA Reach");
			
		} else if (fileName.equals(fileNameSensitization)) {
			removeDuplicates=false;
//			original_source_name="ECHA Reach";
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
			
	public ParseQSAR_ToolBox(String propertyName, String outputGuideline, String fileName,String fileDate) {

		this.sourceName=RecordQSAR_ToolBox.sourceName;
		this.propertyName=propertyName;
		this.fileName=fileName;
		
		if (fileName.equals(fileNameBiodegWaterScreeningECHA_REACHNew) ||
				fileName.equals(fileNameBiodegNITENew)) {
						
			 this.propertyName = propertyName;
			 this.outputTestGuideline = outputGuideline;
			 this.outputProperty = propertyName;
			 this.fileName = fileName;
			 this.fileDate=fileDate;
			
			String src=null;
			
			if(fileName.equals(fileNameBiodegWaterScreeningECHA_REACHNew)) {
				src="ECHA REACH";
			} else if(fileName.equals(fileNameBiodegNITENew)) {
				src="NITE";
			}
			
			if (ExperimentalConstants.strPercentageBiodegradation.equalsIgnoreCase(propertyName)) {
				init("Percent Biodegradation "+outputGuideline+" "+src+" "+fileDate);
			} else if (ExperimentalConstants.strRBIODEG.equalsIgnoreCase(propertyName)) {
				init("RBiodeg "+outputGuideline+" "+src+" "+fileDate);
			} else if (ExperimentalConstants.strRBIODEG_multiclass.equalsIgnoreCase(propertyName)) {
				init("RBiodeg multiclass "+outputGuideline+" "+src+" "+fileDate);
			} else if (ExperimentalConstants.strRBIODEG_10_day.equalsIgnoreCase(propertyName)) {
				init("RBiodeg 10 day "+outputGuideline+" "+src+" "+fileDate);
			}
			
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
	protected void createOriginalRecords() {
		if(generateOriginalJSONRecords) {
			
//			System.out.println(fileName);
			
			if(fileName.contains(".xlsx")) {
				Vector<JsonObject> records = RecordQSAR_ToolBox.parseQSAR_ToolBoxRecordsFromExcel(fileName,sourceName);
//				System.out.println(JsonUtilities.gsonPretty.toJson(records.get(0)));
				
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
			Hashtable<String, List<Species>> htSpeciesByCommonName=JsonUtilities.gsonPretty.fromJson(new FileReader("data\\experimental\\Arnot 2006\\htSuperCategory.json"), type);
			
			Hashtable<String, Species> htSpeciesByBinomalName = getHashtableSpeciesByBinomialName(
					htSpeciesByCommonName);
			
			File Folder=new File(jsonFolder);
			
			if(Folder.listFiles()==null) {
				System.out.println("No files in json folder:"+jsonFolder);
				return null;
			}
			
			List<RecordQSAR_ToolBox>tempRecords=getOriginalRecordsFromJsonFiles(jsonFolder, RecordQSAR_ToolBox[].class);
			
			System.out.println("RecordQSAR_ToolBox.outputProperty: "+this.outputProperty);
			System.out.println("jsonFolder:"+jsonFolder);
			
			for (RecordQSAR_ToolBox recordQSAR_ToolBox:tempRecords) {
				
				if(recordQSAR_ToolBox.Database==null) {//just skip, no usable data
					continue;
				}
				
				if (fileName.equals(fileNameBCF_CanadaNew)) {
					if (!recordQSAR_ToolBox.Database.equals("Bioaccumulation Canada")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;					
					ExperimentalRecord erCanada=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erCanada!=null)	recordsExperimental.add(erCanada);
					
					//Canada doesnt have BAF data
				} else if (fileName.equals(fileNameBCF_CEFICNew)) {
					//Can only filter by whole body if filename is CEFIC
					if (!recordQSAR_ToolBox.Database.equals("Bioaccumulation fish CEFIC LRI")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;					
					ExperimentalRecord erCEFIC=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erCEFIC!=null)	recordsExperimental.add(erCEFIC);

					//CEFIC doesnt have BAF data
				} else if (fileName.equals(fileNameBCF_NITENew)) {
					if (!recordQSAR_ToolBox.Database.equals("Bioconcentration and logKow NITE")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;					
					
					ExperimentalRecord erBCF=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erBCF!=null)	recordsExperimental.add(erBCF);
				} else if (fileName.equals(fileNameBCF_ECHA_REACHNew)) {
					if (!recordQSAR_ToolBox.Database.equals("ECHA REACH")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(er!=null)	recordsExperimental.add(er);
				} else if (fileName.equals(fileNameBCF_ECHA_REACHNew2)) {
					if (!recordQSAR_ToolBox.Database.equals("ECHA REACH")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(er!=null)	recordsExperimental.add(er);
				} else if(fileName.equals(fileNameBCF_CEFIC)) {
					//Can only filter by whole body if filename is CEFIC
					if (!recordQSAR_ToolBox.Database.equals("Bioaccumulation fish CEFIC LRI")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;
					ExperimentalRecord erCEFIC=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erCEFIC!=null)	recordsExperimental.add(erCEFIC);

					//CEFIC doesnt have BAF data
					
				} else if(fileName.equals(fileNameBCF_Canada)) {
					if (!recordQSAR_ToolBox.Database.equals("Bioaccumulation Canada")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;					
					ExperimentalRecord erCanada=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erCanada!=null)	recordsExperimental.add(erCanada);
					
					//Canada doesnt have BAF data
					
				} else if(fileName.equals(fileNameBCF_NITE)) {
					if (!recordQSAR_ToolBox.Database.equals("Bioconcentration and logKow NITE")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;					
					
					ExperimentalRecord erBCF=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(erBCF!=null)	recordsExperimental.add(erBCF);
					
					//TODO- only data for 6 salts- not worth messing with:
//					ExperimentalRecord erBAF=recordQSAR_ToolBox.toExperimentalRecordBAFNITE(propertyName, htSpecies);
//					if(erBAF!=null)	recordsExperimental.add(erBCF);
					
				} else if(fileName.equals(fileNameBCF_ECHA_REACH)) {
					if (!recordQSAR_ToolBox.Database.equals("ECHA REACH")) //QSAR toolbox exported ECHA reach records as well for the NITE chemicals
						continue;
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecordBCF(htSpeciesByBinomalName);
					if(er!=null)	recordsExperimental.add(er);
				} else if(fileName.equals(fileName96hrAcuteAquatic)) {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecordFishTox(propertyName, htSpeciesByCommonName);
					if(er!=null)	recordsExperimental.add(er);
				
				} else if(fileName.equals(fileNameBiodegWaterScreeningECHA_REACHNew) ||
						fileName.equals(fileNameBiodegNITENew)) {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecord(this);					
					if(er!=null) recordsExperimental.add(er);
				} else if(fileName.equals(fileNameKoc)) {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecord(this);

					if(er!=null)	{
						
						recordsExperimental.add(er);
						
						if(er.property_name!=null) {
//							System.out.println(JsonUtilities.gsonPretty.toJson(er));
						}
						
						
//						if(er.keep && er.property_name.equals(ExperimentalConstants.strKd)) {
//							Double OC=recordQSAR_ToolBox.getMeanOrganicCarbonValue();
//							if(OC!=null && OC>0) {					
//								ExperimentalRecord erKoc=createKocRecordFromKd(er, OC);
////								System.out.println(gson.toJson(erKoc));
//								recordsExperimental.add(erKoc);
//							}
//						}
						
//						String json=gson.toJson(recordQSAR_ToolBox).toLowerCase();
//						if(er.keep && (json.contains("estimat") || json.contains("calculat") || 
//								json.contains("model") || json.contains("qsar") || json.contains("equation")
//								|| json.contains("caculation"))) {
//							System.out.println(json+"\n\n");
//						}
						
					
					}

					
				} else {
					ExperimentalRecord er=recordQSAR_ToolBox.toExperimentalRecord(this);
					if(selectedEndpoints.contains(er.property_name))		
						recordsExperimental.add(er);
				}
			}//loop over records
			
			CompareExperimentalRecords cer=new CompareExperimentalRecords();
			
//			System.out.println(JsonUtilities.gsonPretty.toJson(recordsExperimental));
			
			TreeMap<String, ExperimentalRecords> tm = cer.rm.getTreeMapByCAS(propertyName, ExperimentalConstants.str_binary, recordsExperimental);			
			System.out.println("Number of unique casrns:"+tm.size());
			
			
//			addMissingDensities(true);//uses API to add extra entries to data/density.txt

//			for (String method:BiodegradationPropertyValues.methods) {
//				System.out.println(method);
//				if(RecordQSAR_ToolBox.outputTestGuideline.equals(ExperimentalConstants.str301C) ) {
//					System.out.println(method+"\t"+BiodegradationPropertyValues.OECD301Classifier.isValidFor301C(method));
//				} else 	if(RecordQSAR_ToolBox.outputTestGuideline.equals(ExperimentalConstants.str301F) ) {
//					System.out.println(method+"\t"+BiodegradationPropertyValues.OECD301Classifier.isValidFor301C(method));
//				}
//			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if(recordsExperimental.size()==0) {
			return recordsExperimental;
		}
		
		ExperimentalRecord er=recordsExperimental.get(0);
		if(er.property_name.equals(ExperimentalConstants.strBCF)) {
			String units=ExperimentalConstants.str_L_KG;
			Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByCAS(er.property_name, units,true);
			boolean convertToLog=true;
			boolean omitSingleton=true;
			ExperimentalRecords.calculateAvgStdDevOverAllChemicals(htER, convertToLog,omitSingleton);
		}
		
		
//		compareKoc(recordsExperimental);
//		System.out.println(gson.toJson(tm.get("soil")));
		
		
		return recordsExperimental;
	}

	private Hashtable<String, Species> getHashtableSpeciesByBinomialName(
			Hashtable<String, List<Species>> htSpeciesByCommonName) {
		Hashtable<String, Species> htSpeciesByBinomalName=new Hashtable<>();
		for (String speciesCommon:htSpeciesByCommonName.keySet()) {
			List<Species>speciesList=htSpeciesByCommonName.get(speciesCommon);
			for (Species species:speciesList) {
				if (species.species_scientific==null)
					continue;
				htSpeciesByBinomalName.put(species.species_scientific, species);
			}
		}
		return htSpeciesByBinomalName;
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
		
		// findMissingFieldsInRecordClass();
		
		// String [] filenames= {fileNameBCF_ECHA_REACH, fileNameBCF_Canada, fileNameBCF_CEFIC, fileNameBCF_NITE};
		// String [] filenames= {fileNameBCF_ECHA_REACH};
		// String [] filenames= {fileNameBCF_Canada};
		// String [] filenames= {fileNameBCF_CEFIC};
		// String [] filenames= {fileNameBCF_NITE};
//		String [] filenames = {fileNameBCF_ECHA_REACHNew, fileNameBCF_CanadaNew, fileNameBCF_CEFICNew, fileNameBCF_NITENew, fileNameBCF_ECHA_REACHNew2};
		String [] filenames = {fileNameBCF_NITENew};
		
		for (String filename:filenames) {
			ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(filename);
			p.generateOriginalJSONRecords=true;//*** set to true on first run
			p.removeDuplicates=false;
			p.writeJsonExperimentalRecordsFile=true;
			p.writeExcelExperimentalRecordsFile=true;
			p.writeExcelFileByProperty=true;		
			p.writeCheckingExcelFile=false;//creates random sample spreadsheet
			p.createFiles();
			System.out.println("********************************************\n");
		}
		
	}

	private static void findMissingFieldsInRecordClass() {
		String  [] folders={"bioaccumulation canada v.4.8.2","bioaccumulation fish CEFIC LRI v.4.8.2","Bioconcentration and logKow NITE v.4.8.2"};
		Set<String> missingAll=new TreeSet<>();
		for (String folder:folders) {
			String filepath="data\\experimental\\QSAR_Toolbox\\"+folder+"\\QSAR_Toolbox Original Records.json";
			Set<String> missing=JsonFieldChecker.findUnknownFields(filepath, RecordQSAR_ToolBox.class);
//			System.out.println(folder+"\t"+JsonUtilities.gsonPretty.toJson(missing)+"\n");
			missingAll.addAll(missing);
		}
		System.out.println("All\t"+JsonUtilities.gsonPretty.toJson(missingAll));
		for (String missing:missingAll) {
			System.out.println("public String "+missing+";");
		}
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
	
	
	private static void runParse(String endpoint, String guideline, String fileName, String date) {
	    ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(endpoint, guideline, fileName, date);
	    p.maxExcelRows = 100000;
	    p.generateOriginalJSONRecords = false;
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
		         ExperimentalConstants.str301C,
		         fileNameBiodegWaterScreeningECHA_REACHNew,
		         "2026-08-04");
		
		runParse(ExperimentalConstants.strRBIODEG,
		         ExperimentalConstants.str301C,
		         fileNameBiodegNITENew,
		         "2026-11-06");


		runParse(ExperimentalConstants.strRBIODEG,
		         ExperimentalConstants.str301F,
		         fileNameBiodegWaterScreeningECHA_REACHNew,
		         "2026-08-04");

		runParse(ExperimentalConstants.strRBIODEG_10_day,
		         ExperimentalConstants.str301F,
		         fileNameBiodegWaterScreeningECHA_REACHNew,
		         "2026-08-04");
		
	}
	
	static void run96hrAcuteFishTox() {
		
		
		ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(ExperimentalConstants.strAcuteAquaticToxicity,null, fileName96hrAcuteAquatic, null);
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=true;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}
	
	static void convertTsvFilesToExcel() {
		
		
		String folder="data\\experimental\\QSAR_Toolbox\\text files\\";
		String folderExcel="data\\experimental\\QSAR_Toolbox\\excel files\\";
		
//		String[] filenames = { "bioaccumulation canada v.4.8.2 2026-06-02.tsv",
//				"bioaccumulation fish CEFIC LRI v.4.8.2 2026-06-02.tsv",
//				"Bioconcentration and logKow NITE v.4.8.2 2026-06-02.tsv",
//				"bcfbaf echa reach v.4.8.2 2026-06-02.tsv"};
		
//		String[] filenames = { 
//				"REACH Bioaccumulation database (normalized) v.4.9 2026-08-04.tsv",
//				"bioaccumulation canada v.4.9 2026-08-04.tsv",
//				"bioaccumulation fish CEFIC LRI v.4.9 2026-08-04.tsv",
//				"Bioconcentration and logKow NITE v.4.9 2026-08-04.tsv",
//				"bcfbaf echa reach v.4.9 2026-08-04.tsv"};
		
//		String [] filenames= {"biodegradation in water screening tests v.4.9 2026-08-04.tsv"};
//		String []filenames = {"Bioconcentration and LogKow NITE 4.9 2026-11-04.csv"};
		String []filenames = {"biodegradation NITE 4.9 2026-11-06.csv"};
		boolean stopAtFirstBlankRow=true;
		boolean detectNumerics=false;//keep as strings in excel
		
		for (String filename:filenames) {
			String tsvPath = folder+filename;
			
			System.out.println(folder+filename+"\t"+new File(folder+filename).exists());
			String xlsxPath = folderExcel+filename.replace(".tsv", ".xlsx");
			TsvToExcel.tsvToExcel(tsvPath, xlsxPath, stopAtFirstBlankRow, detectNumerics);
		}
	}
	
	
	
	public static void main(String[] args) {

//		UnitConverter.printMissingDensityCas=true;
		
//		 convertTsvFilesToExcel();
		
//		runBCF();
//		run96hrAcuteFishTox();
//		runPhyschem();
//		runKoc();
		runBiodegWaterScreening();

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
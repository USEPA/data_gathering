package gov.epa.exp_data_gathering.parse.EChemPortal;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.util.IOUtils;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.PressureCondition;
import gov.epa.exp_data_gathering.parse.TemperatureCondition;
import gov.epa.exp_data_gathering.parse.TextUtilities;
import gov.epa.exp_data_gathering.parse.RIFM_2026_01.RecordRIFM_2026_01;

/**
 * Parses data from echemportal.org
 * 
 * @author GSINCL01
 * @author tmarti02
 * 
 *
 */
public class ParseEChemPortal extends Parse {

	/**
	 * Output mode for the parser: "BINARY" or "CONTINUOUS"
	 * BINARY: Converts oxygen consumption % to 0.0 (not biodegradable) / 1.0 (biodegradable) if >60%
	 * CONTINUOUS: Preserves actual oxygen consumption percentage values
	 */
	private String outputMode = "BINARY";
	
	/**
	 * Base folder for output (before mode-specific subfolder is appended)
	 * Used to switch between mode-specific output folders
	 */
	private String baseFolderPath = "data" + java.io.File.separator + "experimental" + java.io.File.separator + "RIFM_2026_01";
	
	String fileName="todo";
	static final String filename301F = "biodegradation in water screening tests 2026-05-12.xlsx";
	static final String filenameKoc = "Koc 2026-04-16.xls";


	public ParseEChemPortal(String fileName) {
		sourceName = ExperimentalConstants.strSourceEChemPortal;
		this.fileName = fileName;
		
		if(fileName.equals(filename301F)) {
			init("RBiodeg 301 F ECHA Reach");
		} else if(fileName.equals(filenameKoc)) {
			init("Koc ECHA Reach");
		}
		
	}
	
//	@Override
//	protected void createRecords() {
////		Vector<RecordEChemPortal> records = RecordEChemPortal.parseEChemPortalQueriesFromExcel();
//
//		Vector<RecordEChemPortal> records = createBiodegradationRecords();
//        
//		writeOriginalRecordsToFile(records);
//	}
	
	
	@Override
	protected void createRecords() {
		if(generateOriginalJSONRecords) {
			
			Vector<RecordEChemPortal> records=null;
			
			if (fileName.equals(filename301F)) {
				records = createBiodegradationRecords();
			} else if (fileName.equals(filenameKoc)) {
				records = createKocRecords();
			}
			
			if(records!=null)			
				writeOriginalRecordsToFile(records);
			
		}
	}
	
	Hashtable<String, Integer> flattenBiodegRecords(List<RecordEChemPortal> recs) {
		
		Hashtable<String,List<RecordEChemPortal>>htRecsByCAS=new Hashtable<>();
		
		
		for (RecordEChemPortal rec:recs) {
			
			if(rec.derivedbinaryBiodegradation==null)continue;
			
			if(htRecsByCAS.containsKey(rec.number)) {
				List<RecordEChemPortal>recs2=htRecsByCAS.get(rec.number);
				recs2.add(rec);
			} else {
				List<RecordEChemPortal>recs2=new ArrayList<>();
				recs2.add(rec);
				htRecsByCAS.put(rec.number, recs2);
			}
		}
		
		Hashtable<String,Integer>htScoresByCAS=new Hashtable<>();
		
		for(String CAS:htRecsByCAS.keySet()) {
			List<RecordEChemPortal>recs2=htRecsByCAS.get(CAS);

			List<Integer>vals=new ArrayList<>();
			for (RecordEChemPortal rec2:recs2) {
				vals.add(rec2.derivedbinaryBiodegradation);
			}
			
			double avg=0;
			for (Integer val:vals) {
				avg+=val;
			}
			avg/=vals.size();
			
//			System.out.println(CAS+"\t"+vals+"\t"+avg);
			
			if(avg<=0.2) {
				htScoresByCAS.put(CAS, 0);
			} else if(avg>=0.8) {
				htScoresByCAS.put(CAS, 1);
			}
			
		}
		
		System.out.println("htRecsByCAS.size()="+htRecsByCAS.size());
		System.out.println("htScoresByCAS.size()="+htScoresByCAS.size());
		return htScoresByCAS;
	}
	

	/**
	 * The OECD 301F (Manometric Respirometry) 10% rule dictates that the 10-day
	 * window for "ready biodegradability" (60% ThOD or ) begins immediately once
	 * 10% biodegradation is reached. The compound must hit 60% degradation within
	 * 10 days after crossing the 10% threshold, with the entire 28-day test period
	 * in mind.
	 * 
	 * @return
	 */
	private Vector<RecordEChemPortal> createBiodegradationRecords() {
		
		Vector<RecordEChemPortal> records = RecordEChemPortal.parseEChemPortalExcelFile301F(fileName);

//		Hashtable<String,Integer>htScoresByCAS=flattenBiodegRecords(records);
//		// Start from default columns, then remove any you don’t want:
//        List<EChemPortalExcelExporter.ColumnSpec> cols = EChemPortalExcelExporter.defaultColumns();
//
//        // Drop pH and Values, for example:
//        cols.removeIf(c ->  c.header.equals("Values") 
//        		|| c.header.equals("Participant") || c.header.equals("Section")
//        		|| c.header.equals("Method") || c.header.equals("Pressure")
//        		|| c.header.equals("Temperature") || c.header.equals("pH"));
//                
//        try {
//        	EChemPortalExcelExporter.writeExcel(records, Path.of(jsonFolder+File.separator+"eChemPortal Original Records.xlsx"), cols,htScoresByCAS);
//        } catch (Exception ex) {
//        	ex.printStackTrace();
//        }
		
		
		return records;
	}
	

	private Vector<RecordEChemPortal> createKocRecords() {
		
		Vector<RecordEChemPortal> records = RecordEChemPortal.parseEChemPortalExcelFileKoc(fileName);

//		Hashtable<String,Integer>htScoresByCAS=flattenBiodegRecords(records);
//
//		
//		// Start from default columns, then remove any you don’t want:
//        List<EChemPortalExcelExporter.ColumnSpec> cols = EChemPortalExcelExporter.defaultColumns();
//
//        // Drop pH and Values, for example:
//        cols.removeIf(c ->  c.header.equals("Values") 
//        		|| c.header.equals("Participant") || c.header.equals("Section")
//        		|| c.header.equals("Method") || c.header.equals("Pressure")
//        		|| c.header.equals("Temperature") || c.header.equals("pH"));
//                
//        try {
//        	EChemPortalExcelExporter.writeExcel(records, Path.of(jsonFolder+File.separator+"eChemPortal Original Records.xlsx"), cols,htScoresByCAS);
//        } catch (Exception ex) {
//        	ex.printStackTrace();
//        }
		return records;
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			String jsonFileName = jsonFolder + File.separator + fileNameJSON_Records;
			File jsonFile = new File(jsonFileName);
			
			List<RecordEChemPortal> recordsEChemPortal = new ArrayList<RecordEChemPortal>();
			RecordEChemPortal[] tempRecords = null;
			if (howManyOriginalRecordsFiles==1) {
				tempRecords = gson.fromJson(new FileReader(jsonFile), RecordEChemPortal[].class);
				for (int i = 0; i < tempRecords.length; i++) {
					recordsEChemPortal.add(tempRecords[i]);
				}
			} else {
				for (int batch = 1; batch <= howManyOriginalRecordsFiles; batch++) {
					String batchFileName = jsonFileName.substring(0,jsonFileName.indexOf(".")) + " " + batch + ".json";
					File batchFile = new File(batchFileName);
					tempRecords = gson.fromJson(new FileReader(batchFile), RecordEChemPortal[].class);
					for (int i = 0; i < tempRecords.length; i++) {
						recordsEChemPortal.add(tempRecords[i]);
					}
				}
			}
			
			for (RecordEChemPortal r:recordsEChemPortal) {
				
				if (fileName.equals(filename301F)) {
					ExperimentalRecord er=r.toExperimentalRecordWaterBiodegration();
					recordsExperimental.add(er);
				} else if (fileName.equals(filenameKoc)) {
					List<ExperimentalRecord>ers=r.toExperimentalRecordsKoc();
					recordsExperimental.addAll(ers);
				} else {
//					System.out.println("Not biodeg record");
					addExperimentalRecords(r,recordsExperimental);
				}
				
//				break;
			}
			
//			System.out.println("Other endpoints="+JsonUtilities.gsonPretty.toJson(RecordEChemPortal.endpoints));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return recordsExperimental;
	}
	
	private void addExperimentalRecords(RecordEChemPortal ecpr,ExperimentalRecords records) {
		
		if (!ecpr.values.isEmpty()) {
			String cas = "";
			String einecs = "";
			
			if (ecpr.numberType.equals("CAS Number")) { 
				cas = ecpr.number;
			} else if (ecpr.numberType.equals("EC Number")) {
				einecs = ecpr.number; 
			}
			
			for (int i = 0; i < ecpr.values.size(); i++) {
				ExperimentalRecord er = new ExperimentalRecord();
				er.date_accessed = RecordEChemPortal.lastUpdated;
				er.source_name = ExperimentalConstants.strSourceEChemPortal;
				er.original_source_name = ecpr.participant;
				if (cas.length()!=0 && !cas.equals("unknown")) { er.casrn = cas;
				} else if (einecs.length()!=0 && !einecs.equals("unknown")) { er.einecs = einecs; }
				if (ecpr.substanceName!=null && !ecpr.substanceName.equals("-") && !ecpr.substanceName.contains("unnamed")) {
					er.chemical_name = StringEscapeUtils.escapeHtml4(ecpr.substanceName);
				}
				er.url = ecpr.url;
				if (ecpr.method!=null && !ecpr.method.isBlank()) {
					er.measurement_method = ecpr.method;
				}
				
				er.property_value_string = ecpr.values.get(i).replaceAll("â", "-");
				
				String propertyValue = er.property_value_string;
				if (!ecpr.temperature.isEmpty() && ecpr.temperature.get(i)!=null) { 
					String temp = ecpr.temperature.get(i).replaceAll("â", "-");
					TemperatureCondition.getTemperatureCondition(er,temp);
					er.property_value_string = er.property_value_string + ";" + temp;
				}
				if (!ecpr.pressure.isEmpty() && ecpr.pressure.get(i)!=null) {
					String pressure = ecpr.pressure.get(i).replaceAll("â", "-");
					PressureCondition.getPressureCondition(er,pressure,sourceName);
					er.property_value_string = er.property_value_string + ";" + pressure;
				}
				if (!ecpr.pH.isEmpty() && ecpr.pH.get(i)!=null) { 
					String pHStr = ecpr.pH.get(i).replaceAll("â", "-");
					er.property_value_string = er.property_value_string + ";" + pHStr;
					boolean foundpH = false;
					try {
						double[] range = TextUtilities.extractFirstDoubleRangeFromString(pHStr,pHStr.length());
						er.pH = range[0]+"-"+range[1];
						foundpH = true;
					} catch (Exception ex) { }
					if (!foundpH) {
						try {
							double[] range = TextUtilities.extractAltFormatRangeFromString(pHStr,pHStr.length());
							er.pH = range[0]+"-"+range[1];
							foundpH = true;
						} catch (Exception ex) { }
					}
					if (!foundpH) {
						try {
							Matcher caMatcher = Pattern.compile(".*?(ca. )?([-]?[ ]?[0-9]*\\.?[0-9]+)( ca. )([-]?[ ]?[0-9]*\\.?[0-9]+)").matcher(pHStr);
							if (caMatcher.find()) {
								String numQual = caMatcher.group(1).isBlank() ? "" : "~";
								er.pH = numQual+Double.parseDouble(caMatcher.group(2))+"~"+Double.parseDouble(caMatcher.group(4));
								foundpH = true;
							}
						} catch (Exception ex) { }
					}
					if (!foundpH && pHStr.contains(",") && !pHStr.endsWith(",")) {
						er.pH = pHStr;
						foundpH = true;
					}
					if (!foundpH) {
						try {
							double pHDouble = TextUtilities.extractClosestDoubleFromString(pHStr,pHStr.length(),"pH");
							String pHDoubleStr = Double.toString(pHDouble);
							String numQual = "";
							if (pHDouble >= 0 && pHDouble < 1) {
								numQual = TextUtilities.getNumericQualifier(pHStr,pHStr.indexOf("0"));
							} else {
								numQual = TextUtilities.getNumericQualifier(pHStr,pHStr.indexOf(pHDoubleStr.charAt(0)));
							}
							er.pH = numQual+pHDoubleStr;
							foundpH = true;
						} catch (Exception ex) { }
					}
				}
				if (ecpr.section.equals("Density")) {
					er.property_name = ExperimentalConstants.strDensity;
					ParseUtilities.getDensity(er,propertyValue);
				} else if (ecpr.section.equals("Melting / freezing point")) {
					er.property_name = ExperimentalConstants.strMeltingPoint;
					ParseUtilities.getTemperatureProperty(er,propertyValue);
				} else if (ecpr.section.equals("Boiling point")) {
					er.property_name = ExperimentalConstants.strBoilingPoint;
					ParseUtilities.getTemperatureProperty(er,propertyValue);
				} else if (ecpr.section.equals("Flash point")) {
					er.property_name = ExperimentalConstants.strFlashPoint;
					ParseUtilities.getTemperatureProperty(er,propertyValue);
				} else if (ecpr.section.equals("Water solubility")) {
					er.property_name = ExperimentalConstants.strWaterSolubility;
					ParseUtilities.getWaterSolubility(er,propertyValue,sourceName);
				} else if (ecpr.section.equals("Vapour pressure")) {
					er.property_name = ExperimentalConstants.strVaporPressure;
					ParseUtilities.getVaporPressure(er,propertyValue);
				} else if (ecpr.section.equals("Partition coefficient")) {
					er.property_name = ExperimentalConstants.strLogKOW;
					ParseUtilities.getLogProperty(er,propertyValue);
				} else if (ecpr.section.equals("Dissociation constant")) {
					er.property_name = ExperimentalConstants.str_pKA;
					ParseUtilities.getLogProperty(er,propertyValue);
				} else if (ecpr.section.equals("Henry's Law constant")) {
					er.property_name = ExperimentalConstants.strHenrysLawConstant;
					ParseUtilities.getHenrysLawConstant(er,propertyValue);
				}

				uc.convertRecord(er);

				if (!ParseUtilities.hasIdentifiers(er)) {
					er.keep = false;
					er.reason = "No identifiers";
				}

				er.reliability = ecpr.reliability;
				records.add(er);
			}
		}
	}
	

	static void runBiodegWaterScreening() {
		ParseEChemPortal p=new ParseEChemPortal(filename301F);
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=false;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet

		// Set output mode - options are "BINARY" or "CONTINUOUS"
		// BINARY: classifies as biodegradable (1.0) if oxygen consumption > 60%, else not biodegradable (0.0)
		// CONTINUOUS: preserves actual oxygen consumption percentages from the source data
		p.setOutputMode("BINARY");
		p.createFiles();
	}
	

	static void runKoc() {
		ParseEChemPortal p=new ParseEChemPortal(filenameKoc);
		p.generateOriginalJSONRecords=true;		
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=false;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}

	/**
	 * Sets the output mode for property value transformation and updates output folder accordingly.
	 * BINARY mode outputs to: data/experimental/RIFM_2026_01/RBiodeg 301F RIFM
	 * CONTINUOUS mode outputs to: data/experimental/RIFM_2026_01/Percent Biodegradation 301F RIFM
	 * 
	 * @param mode "BINARY" for binary biodegradable classification, "CONTINUOUS" for percentage values
	 */
	public void setOutputMode(String mode) {
		this.outputMode = mode;
		RecordEChemPortal.setMode(mode);
		
		// Update mainFolder and jsonFolder based on mode
		String subfolder;
		if ("CONTINUOUS".equalsIgnoreCase(mode)) {
			subfolder = "Percent Biodegradation 301 F ECHA Reach";
		} else if ("BINARY".equalsIgnoreCase(mode)) {
			subfolder = "RBiodeg 301 F ECHA Reach";
		} else {
			throw new IllegalArgumentException("Invalid output mode. Use 'BINARY' or 'CONTINUOUS'.");
		}
		
		this.mainFolder = baseFolderPath + java.io.File.separator + subfolder;
		this.jsonFolder = this.mainFolder;
		
		// Ensure the folder exists
		new java.io.File(this.mainFolder).mkdirs();
	}

	/**
	 * Gets the current output mode.
	 * 
	 * @return the current output mode ("BINARY" or "CONTINUOUS")
	 */
	public String getOutputMode() {
		return this.outputMode;
	}
	
	public static void main(String[] args) {
		
		IOUtils.setByteArrayMaxOverride(200000000);

		
		runBiodegWaterScreening();
//		runKoc();
	}
}

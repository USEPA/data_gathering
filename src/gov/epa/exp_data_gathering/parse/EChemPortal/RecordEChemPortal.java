package gov.epa.exp_data_gathering.parse.EChemPortal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ChemicalNameFixer;
import gov.epa.exp_data_gathering.parse.DownloadWebpageUtilities;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.EChemPortal.EstimateParser.Estimate;
import gov.epa.exp_data_gathering.parse.EChemPortalAPI.Query.Unit;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox.ResultBinaryScore;

/**
 * Stores data from echemportal.org
 * 
 * @author GSINCL01
 *
 */
public class RecordEChemPortal {
	
	String substanceName;
	String nameType;
	String number;
	String numberType;
	Boolean memberOfCategory;
	String participant;
	String section;
	String url;
	String reliability;
	String method;
	Vector<String> values;
	Vector<String> pressure;
	Vector<String> temperature;
	Vector<String> pH;
	String source;

	String typeOfInformation;
	String endpoint;
	String testGuidelineQualifier;
	String testGuideline;
	String GLP_compliance;
	String oxygenConditions;
	String media;

	List<RecordDegradation> recordsDegradation;
	List<RecordKoc> recordsKoc;
	
	String interpretationOfResults;
	Integer derivedbinaryBiodegradation;
	private String decisionDegradationRecord;
	Double percentDegradation28days;
	
	String dateAccessed;
	
	static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");

	
	static class RecordKoc {
		String type;
		String value;
	}

	static class RecordDegradation {
		String parameter;
		String degradationValue;
		String samplingTime;
		Double samplingTimeDays;
		
		public String toString() {
			return degradationValue+" "+parameter+" in "+samplingTimeDays+" days";
		}
		
	}
	
	String convertRecordsDegradationToString() {
		String str="";
		for (RecordDegradation rec:recordsDegradation) {
			str+=rec.toString()+"\n";
		}
		str=str.trim();
		return str;
	}
	

//	50-78-2	Reliability	1 (reliable without restriction)
//	50-78-2	Test guideline, Qualifier	according to guideline
//	50-78-2	Test guideline, Guideline	OECD Guideline 301 F (Ready Biodegradability
//	50-78-2	GLP compliance	yes (incl. QA statement)
//	50-78-2	Oxygen conditions	aerobic
//	50-78-2	% Degradation, Parameter	% degradation (O2 consumption)
//	50-78-2	% Degradation, Value	83.3
//	50-78-2	% Degradation, Sampling time	29 d
//	50-78-2	% Degradation, Parameter	% degradation (O2 consumption)
//	50-78-2	% Degradation, Value	69.6
//	50-78-2	% Degradation, Sampling time	10 d
//	50-78-2	Interpretation of results	readily biodegradable

	static final String lastUpdated = null;
	public static final String sourceName = ExperimentalConstants.strSourceEChemPortal;

	private RecordEChemPortal() {
		values = new Vector<String>();
		pressure = new Vector<String>();
		temperature = new Vector<String>();
		pH = new Vector<String>();
	}

	public static Vector<RecordEChemPortal> parseEChemPortalExcelFile301F(String filename) {

		Vector<RecordEChemPortal> records = new Vector<RecordEChemPortal>();

		String folderPath = "data\\experimental\\eChemPortal\\excel files\\";

		try {
//			String date = DownloadWebpageUtilities.getStringCreationDate(folderPath+filename);

			String filepath = folderPath + filename;
			
			String createdAt = getFileCreatedAt(filepath);

			FileInputStream fis = new FileInputStream(new File(filepath));

			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(0);

			Row row0 = sheet.getRow(0);

			Hashtable<String, Integer> htCols = new Hashtable<>();

			for (int i = 0; i < row0.getLastCellNum(); i++) {
				String header = row0.getCell(i).getStringCellValue().trim();
//				System.out.println(header+"\t"+i);
				htCols.put(header, i);
			}

//			Substance Name	Name type	Number	Number type	Member of Category	Source	Section	Values

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				RecordEChemPortal rec = new RecordEChemPortal();

				records.add(rec);

				// For biodeg we dont have following:
				rec.pressure = null;
				rec.temperature = null;
				rec.pH = null;

				rec.substanceName = row.getCell(htCols.get("Substance Name")).getStringCellValue().trim();
				
				if (rec.substanceName.equals("-")) rec.substanceName=null;
				
				rec.nameType = row.getCell(htCols.get("Name type")).getStringCellValue().trim();
				rec.number = row.getCell(htCols.get("Number")).getStringCellValue().trim();

				if (row.getCell(htCols.get("Number type")) != null)
					rec.numberType = row.getCell(htCols.get("Number type")).getStringCellValue().trim();

				rec.memberOfCategory = row.getCell(htCols.get("Member of Category")).getBooleanCellValue();
				rec.source = row.getCell(htCols.get("Source")).getStringCellValue().trim();

				rec.url = row.getCell(htCols.get("Section")).getHyperlink().getAddress();
				
				rec.dateAccessed=createdAt;

				String strValues = row.getCell(htCols.get("Values")).getStringCellValue();

				String[] values = strValues.split("\n");

//				50-78-2	Test guideline, Guideline	OECD Guideline 301 F (Ready Biodegradability
//				50-78-2	GLP compliance	yes (incl. QA statement)
//				50-78-2	Oxygen conditions	aerobic
//				50-78-2	% Degradation, Parameter	% degradation (O2 consumption)
//				50-78-2	% Degradation, Value	83.3
//				50-78-2	% Degradation, Sampling time	29 d
//				50-78-2	% Degradation, Parameter	% degradation (O2 consumption)
//				50-78-2	% Degradation, Value	69.6
//				50-78-2	% Degradation, Sampling time	10 d
//				50-78-2	Interpretation of results	readily biodegradable

				rec.recordsDegradation = new ArrayList<>();

				RecordDegradation recordDegradation = null;

				for (String value : values) {
					if (value.isBlank())
						continue;
					String[] vals = value.split(":");
					String parameterName = vals[0].trim();
					String parameterValue = vals[1].trim();

					if (parameterName.equals("Type of information")) {
						rec.typeOfInformation = parameterValue;
					} else if (parameterName.equals("Reliability")) {
						rec.reliability = parameterValue;
					} else if (parameterName.equals("Endpoint")) {
						rec.endpoint = parameterValue;
					} else if (parameterName.equals("Test guideline, Qualifier")) {
						rec.testGuidelineQualifier = parameterValue;
					} else if (parameterName.equals("Test guideline, Guideline")) {
						rec.testGuideline = parameterValue;
					} else if (parameterName.equals("GLP compliance")) {
						rec.GLP_compliance = parameterValue;
					} else if (parameterName.equals("Oxygen conditions")) {
						rec.oxygenConditions = parameterValue;
					} else if (parameterName.equals("% Degradation, Parameter")) {
						recordDegradation = new RecordDegradation();
						recordDegradation.parameter = parameterValue.replace('\u00A0',' ');;
						rec.recordsDegradation.add(recordDegradation);
					} else if (parameterName.equals("% Degradation, Value")) {
						recordDegradation.degradationValue = parameterValue;
					} else if (parameterName.equals("% Degradation, Sampling time")) {
						recordDegradation.samplingTime = parameterValue;
					} else if (parameterName.equals("Interpretation of results")) {

						if (vals.length == 3) {
							rec.interpretationOfResults = vals[2].trim();
						} else {
							rec.interpretationOfResults = parameterValue;
						}

					} else {
						System.out.println(rec.number + "\t" + parameterName + "\t" + parameterValue);
					}

				}
				
//				determineBinaryBiodegradation(rec);
//				determinePercentBiodegradation28days(rec);

			}
			wb.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}
	
	public static Vector<RecordEChemPortal> parseEChemPortalExcelFileKoc(String filename) {

		Vector<RecordEChemPortal> records = new Vector<RecordEChemPortal>();

		String folderPath = "data\\experimental\\eChemPortal\\excel files\\";

		try {
//			String date = DownloadWebpageUtilities.getStringCreationDate(folderPath+filename);

			String filepath = folderPath + filename;
			
			
			String createdAt = getFileCreatedAt(filepath);
	        
			FileInputStream fis = new FileInputStream(new File(filepath));

			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheet = wb.getSheetAt(0);

			Row row0 = sheet.getRow(0);

			Hashtable<String, Integer> htCols = new Hashtable<>();

			for (int i = 0; i < row0.getLastCellNum(); i++) {
				String header = row0.getCell(i).getStringCellValue().trim();
//				System.out.println(header+"\t"+i);
				htCols.put(header, i);
			}

//			Substance Name	Name type	Number	Number type	Member of Category	Source	Section	Values

			for (int i = 1; i < sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				RecordEChemPortal rec = new RecordEChemPortal();

				records.add(rec);

				// For biodeg we dont have following:
				rec.pressure = null;
				rec.temperature = null;
				rec.pH = null;

				rec.substanceName = row.getCell(htCols.get("Substance Name")).getStringCellValue().trim();
				
				if (rec.substanceName.equals("-")) rec.substanceName=null;
				
				rec.nameType = row.getCell(htCols.get("Name type")).getStringCellValue().trim();
				rec.number = row.getCell(htCols.get("Number")).getStringCellValue().trim();

				if (row.getCell(htCols.get("Number type")) != null)
					rec.numberType = row.getCell(htCols.get("Number type")).getStringCellValue().trim();

				rec.memberOfCategory = row.getCell(htCols.get("Member of Category")).getBooleanCellValue();
				rec.source = row.getCell(htCols.get("Source")).getStringCellValue().trim();

				rec.url = row.getCell(htCols.get("Section")).getHyperlink().getAddress();
				
				rec.dateAccessed = createdAt;

				String strValues = row.getCell(htCols.get("Values")).getStringCellValue();

				String[] values = strValues.split("\n");

				
				rec.recordsKoc = new ArrayList<>();

				RecordKoc recordKoc = null;


				for (String value : values) {
					if (value.isBlank())
						continue;
					String[] vals = value.split(":");
					String parameterName = vals[0].trim();
					String parameterValue = vals[1].trim();

					if (parameterName.equals("Type of information")) {
						rec.typeOfInformation = parameterValue;
					} else if (parameterName.equals("Reliability")) {
						rec.reliability = parameterValue;
					} else if (parameterName.equals("Endpoint")) {
						rec.endpoint = parameterValue;
					} else if (parameterName.equals("Test guideline, Qualifier")) {
						rec.testGuidelineQualifier = parameterValue;
					} else if (parameterName.equals("Test guideline, Guideline")) {
						rec.testGuideline = parameterValue;
					} else if (parameterName.equals("GLP compliance")) {
						rec.GLP_compliance = parameterValue;
					} else if (parameterName.equals("Media")) {
						rec.media = parameterValue;
					} else if (parameterName.equals("Type of method")) {
						rec.method = parameterValue;
					} else if (parameterName.equals("Adsorption coefficient, Type")) {
						recordKoc = new RecordKoc();
						recordKoc.type=parameterValue;
						rec.recordsKoc.add(recordKoc);
					} else if (parameterName.equals("Adsorption coefficient, Value")) {
						recordKoc.value=parameterValue;
					} else {
						System.out.println(rec.number + "\t" + parameterName + "\t" + parameterValue);
					}

				}
				
//				determineBinaryBiodegradation(rec);

			}
			wb.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}

	private static String getFileCreatedAt(String filepath) throws IOException {
		Path path = Paths.get(filepath);

		// Follow symlinks by default; add LinkOption.NOFOLLOW_LINKS if needed
		BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
		
		LocalDate createdDate = attrs.creationTime()
		        .toInstant()
		        .atZone(ZoneId.systemDefault())
		        .toLocalDate();
		
		String createdAt = createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		return createdAt;
	}

	public static HashSet<String>endpoints=new HashSet<>();
	
	/**
	 * Gets record for O2 consumption closest to 28 days duration
	 * 
	 * @param rec
	 */
	RecordDegradation getRecordDegradation28Day() {
				
		if(recordsDegradation==null)
			return null;
		
		double minDurationDiff=9999;
		RecordDegradation bestRec=null;
		
		TreeMap<Double,List<RecordDegradation>>htRecs=new TreeMap<>();
		
		
		for (RecordDegradation recBio:this.recordsDegradation) {
			
			if (!recBio.parameter.contains("% degradation (O2 consumption)")) {
//			    System.out.println(JsonUtilities.gsonPretty.toJson(recBio));
				endpoints.add(recBio.parameter);
			    continue;
			}
			if(recBio.samplingTimeDays==null)continue;
			
			Double key=Math.abs(recBio.samplingTimeDays-28);

			if(htRecs.containsKey(key)) {
				List<RecordDegradation>recs=htRecs.get(key);
				recs.add(recBio);				
			} else {
				List<RecordDegradation>recs=new ArrayList<>();
				recs.add(recBio);
				htRecs.put(key, recs);
			}
		}
		
		if(htRecs.size()==0)
			return null;

		
		List<RecordDegradation>recs=htRecs.get(htRecs.firstKey());
		
		boolean hasMin=false;
		boolean hasMax=false;
		for(RecordDegradation rec:recs) {
			Estimate estimate=EstimateParser.parse(rec.degradationValue);
			if(estimate.min!=null) hasMin=true;
			if(estimate.max!=null) hasMax=true;
		}

		if (recs.size()==1) 
			return recs.get(0);
		
		if(hasMin && hasMax) {

//			System.out.println("\nhas min and max");
			
			double avgPoint=0;
			int count=0;
			
			for(RecordDegradation rec:recs) {
				Estimate estimate=EstimateParser.parse(rec.degradationValue);
				count++;
//				System.out.println(count+"\t"+estimate.min+"\t"+estimate.max+"\t"+estimate.point);
				
				if(estimate.point!=null) {
					avgPoint+=estimate.point;
				} else {
					avgPoint+=(estimate.max+estimate.min)/2;					
				}
			}
			
			avgPoint/=count;
			Gson gson=new Gson();
			RecordDegradation recNew=gson.fromJson(gson.toJson(recs.get(0)),RecordDegradation.class);
			recNew.degradationValue=avgPoint+"";
//			System.out.println(this.number+"\t"+ JsonUtilities.gsonPretty.toJson(recs)+"\n"+JsonUtilities.gsonPretty.toJson(recNew)+"\n\n");
			return recNew;

			
		} else if(hasMin) {
//			System.out.println("has min");
			
			double avgMin=0;
			int count=0;
			for(RecordDegradation rec:recs) {
				Estimate estimate=EstimateParser.parse(rec.degradationValue);
				if(estimate.min==null) {
					System.out.println("No min: "+JsonUtilities.gsonPretty.toJson(rec));
					continue;
				}
				count++;
				avgMin+=estimate.min;
			}

			avgMin/=count;
			Gson gson=new Gson();
			RecordDegradation recNew=gson.fromJson(gson.toJson(recs.get(0)),RecordDegradation.class);
			recNew.degradationValue=">= "+avgMin+"";

//			System.out.println(this.number+"\t"+ JsonUtilities.gsonPretty.toJson(recs)+"\n"+JsonUtilities.gsonPretty.toJson(recNew)+"\n\n");
			return recNew;


		} else if(hasMax) {	
			System.out.println("has max");//doesnt happen

			//				System.out.println(this.number+"\t"+ JsonUtilities.gsonPretty.toJson(recs)+"\n");	
		} else {

			double avgPoint=0;
			int count=0;
			for(RecordDegradation rec:recs) {
				Estimate estimate=EstimateParser.parse(rec.degradationValue);
				if(estimate.point==null) {
					System.out.println("No point: "+JsonUtilities.gsonPretty.toJson(rec));
					continue;
				}
				count++;
				avgPoint+=estimate.point;
			}

			avgPoint/=count;
			Gson gson=new Gson();
			RecordDegradation recNew=gson.fromJson(gson.toJson(recs.get(0)),RecordDegradation.class);
			recNew.degradationValue=avgPoint+"";

//			System.out.println(this.number+"\t"+ JsonUtilities.gsonPretty.toJson(recs)+"\n"+JsonUtilities.gsonPretty.toJson(recNew)+"\n\n");
			return recNew;
		}
			
		return null;
	}
	
	

	static void determinePercentBiodegradation28days(RecordEChemPortal rec) {
		
		double minDurationDiff=9999;
		List<RecordDegradation> bestRecs=new ArrayList<>();
		
		for (RecordDegradation recBio:rec.recordsDegradation) {
			
			if(recBio.samplingTimeDays==null) continue;
			
			if (!recBio.parameter.contains("% degradation (O2 consumption)")) {
//			    System.out.println(JsonUtilities.gsonPretty.toJson(recBio));
			    continue;
			}
			
			double diff=Math.abs(recBio.samplingTimeDays-28);
			
			if(diff<0.01) {
				bestRecs.add(recBio);
			}
			
		}
		
		if(bestRecs.size()==0)return;
		
//		if(bestRecs.size()>1)		
//			System.out.println(JsonUtilities.gsonPretty.toJson(bestRecs));
		
		int count=0;
		Double avgDeg=0.0;
		for (RecordDegradation recBio:bestRecs) {
			
			Estimate estimate=EstimateParser.parse(recBio.degradationValue);
			
			if(estimate.point==null) {
				continue;
			} else {
				count++;
				avgDeg+=estimate.point;
			}
		}
		
		if(count>0) {
			avgDeg/=(double)count;
			rec.percentDegradation28days=avgDeg;
		}
		
		
//		//TODO there might be multiple 28 day records
//		
//		Estimate estimate=EstimateParser.parse(bestRec.degradationValue);
//		
//			
//		if(rec.derivedbinaryBiodegradation!=null) {
//			rec.decisionDegradationRecord = bestRec.toString();
//		}
		
	
	}
	
	
	public static Vector<RecordEChemPortal> parseEChemPortalQueriesFromExcel() {
		Vector<RecordEChemPortal> records = new Vector<RecordEChemPortal>();
		String folderNameExcel = "excel files";
		String mainFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
		String excelFilePath = mainFolder + File.separator + folderNameExcel;
		File folder = new File(excelFilePath);
		String[] filenames = folder.list();
		Vector<String> filenamesSorted = new Vector<String>();
		for (String filename : filenames) {
			if (filename.contains("1Condition")) {
				filenamesSorted.add(filename);
			}
		}
		for (String filename : filenames) {
			if (filename.contains("2Conditions")) {
				filenamesSorted.add(0, filename);
			} else if (filename.contains("All")) {
				filenamesSorted.add(filename);
			}
		}
		HashSet<String> urlCheck = new HashSet<String>();
		int countDuplicates = 0;
		for (String filename : filenamesSorted) {
			if (filename.endsWith(".xls")) {
				try {
					String filepath = excelFilePath + File.separator + filename;
					String date = DownloadWebpageUtilities.getStringCreationDate(filepath);
					if (!date.equals(lastUpdated)) {
						System.out.println(sourceName
								+ " warning: Last updated date does not match creation date of file " + filename);
					}
					FileInputStream fis = new FileInputStream(new File(filepath));
					Workbook wb = new HSSFWorkbook(fis);
					Sheet sheet = wb.getSheetAt(0);
					int rows = sheet.getLastRowNum();
					for (int i = 1; i < rows; i++) {
						Row row = sheet.getRow(i);
						String url = row.getCell(6).getHyperlink().getAddress();
						if (urlCheck.add(url)) {
							RecordEChemPortal ecpr = new RecordEChemPortal();
							ecpr.url = url;
							ecpr.substanceName = row.getCell(0).getStringCellValue().trim();
							ecpr.nameType = row.getCell(1).getStringCellValue().trim();
							ecpr.number = row.getCell(2).getStringCellValue().trim();
							ecpr.numberType = row.getCell(3).getStringCellValue().trim();
							ecpr.memberOfCategory = row.getCell(4).getBooleanCellValue();
							ecpr.participant = row.getCell(5).getStringCellValue().trim();
							ecpr.section = row.getCell(6).getStringCellValue().trim();
							if (ecpr.section.equals("Melting point / freezing point")) {
								ecpr.section = "Melting / freezing point";
							}
							ecpr.getValues(row.getCell(7).getStringCellValue().trim());
							records.add(ecpr);
						} else {
							countDuplicates++;
						}
					}
					wb.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		System.out.println("Eliminated " + countDuplicates + " duplicate records by URL");
		return records;
	}

	private void getValues(String cellValues) {
		String[] entryArray = cellValues.split("\n");
		for (String entry : entryArray) {
			if (entry != null && entry.contains(":")) {
				entry = entry.trim();
				String data = entry.substring(entry.indexOf(":") + 1).trim().replaceAll("Ã", "")
						.replaceAll("Ã¢ÂÂ", "-").replaceAll("â", "-");
				if (entry.startsWith("Reliability")) {
					reliability = data;
				} else if (entry.startsWith("Type of method")) {
					method = data;
				} else if (entry.startsWith(section + ", " + section.split(" ")[0])
						|| entry.startsWith(section + ", pKa") || entry.startsWith(section + " H, H")
						|| entry.startsWith("Effect levels, Effect level")) {
					values.add(data);
				} else if (entry.startsWith(section + ", Atm. press.")) {
					pressure.add(data);
				} else if (entry.startsWith(section + ", Temp.")) {
					temperature.add(data);
				} else if (entry.startsWith(section + ", pH")) {
					pH.add(data);
				}
			}
		}
	}
	
	
	private void setPropertyValues(RecordDegradation recBio, ExperimentalRecord er) {

		Estimate estimate=EstimateParser.parse(recBio.degradationValue);
		
//		System.out.println(recBio.degradationValue+"\n"+JsonUtilities.gsonPretty.toJson(estimate)+"\n");

		if(estimate.min!=null && estimate.max!=null) {
			er.property_value_min_original=estimate.min;
			er.property_value_max_original=estimate.max;
			
			er.property_value_string=Parse.formatValue(er.property_value_min_original)+" - "+Parse.formatValue(er.property_value_max_original);

			//			System.out.println(er.property_value_min_original+"\t"+er.property_value_max_original);

		} else if(estimate.min!=null) {
			er.property_value_min_original=estimate.min;
			er.property_value_string="> "+Parse.formatValue(er.property_value_min_original);
			//			System.out.println(er.property_value_min_original);

		} else if(estimate.max!=null) {
			er.property_value_max_original=estimate.max;
			//			System.out.println(er.property_value_max_original);
			er.property_value_string="< "+Parse.formatValue(er.property_value_max_original);

		} else if(estimate.point!=null) {
			er.property_value_point_estimate_original=estimate.point;

			String qualifier=null;
			if(recBio.degradationValue.contains("ca.")) {
				qualifier="~";
			}
			
			if(qualifier==null) {
				er.property_value_string=Parse.formatValue(er.property_value_point_estimate_original);
			} else {
				er.property_value_string=qualifier+" "+Parse.formatValue(er.property_value_point_estimate_original);
			}
			
		} 

		if(er.property_value_string!=null) {
			DecimalFormat df=new DecimalFormat("0.#");
			er.property_value_string+=" "+recBio.parameter+" in "+df.format(recBio.samplingTimeDays)+" days";
//			System.out.println(er.property_value_string);
			er.property_value_units_original=ExperimentalConstants.str_dimensionless;
		}

//		if(er.property_value_string==null) {
//			er.keep=false;
//			er.reason="No property_value_string";
//		}

	}
	
	ExperimentalRecord toExperimentalRecordWaterBiodegration() {

		ExperimentalRecord er = createExperimentalRecord(ExperimentalConstants.strRBIODEG);
		
		setBiodegradationParameters(er);
		
		RecordDegradation recBio=this.getRecordDegradation28Day();
		
		if(recBio==null) {
//			System.out.println(JsonUtilities.gsonPretty.toJson(this.recordsDegradation));
			boolean haveO2=false;
			for(RecordDegradation rec:this.recordsDegradation) {
				if (rec.parameter.contains("% degradation (O2 consumption)")) {
					haveO2=true;
				}
			}
			if(!haveO2) {
				er.keep=false;
				er.updateReason("Degradation not O2 consumption or ThOD");
				er.experimental_parameters.put("Measurement method",this.recordsDegradation.get(0).parameter);
//				System.out.println(er.experimental_parameters.get("Measurement method"));
			} else {
				System.out.println("Have null rec but have O2");//doesnt happen
			}
			return er;
		}

		er.experimental_parameters.put("Measurement method",recBio.parameter);
		
		
		Double duration=recBio.samplingTimeDays;

		DecimalFormat df=new DecimalFormat("0");
		
		setPropertyValues(recBio, er);
		
		if (er.experimental_parameters.containsKey("Test guideline")) {
			String strGuidelines = (String) er.experimental_parameters.get("Test guideline");
			if (!strGuidelines.contains("301 F")) {
				er.keep = false;
				er.updateReason("wrong guideline");//doesnt happen?
			}
		} else {
			er.keep = false;
			er.updateReason("guideline unavailable");//doesnt happen?
		}
		
		if (er.keep) {
			Estimate estimate=EstimateParser.parse(recBio.degradationValue);				
			ResultBinaryScore rbs=RecordQSAR_ToolBox.determineBinaryBiodegScore(estimate, duration);
						
			if(rbs.score!=null) {
				er.property_value_point_estimate_final=(double)rbs.score;
				er.property_value_units_final=ExperimentalConstants.str_binary;
//				System.out.println(er.casrn+","+er.property_value_point_estimate_final+","+er.property_value_string+","+er.experimental_parameters.get("Interpretation of results"));
			} else {
//				System.out.println(er.casrn+"\t"+er.reason);
				er.updateReason(rbs.reason);
				
//				if(rbs.reason.contains("Can't assign score based on min and max degradation values")) {
//					System.out.println(er.casrn+","+er.property_value_point_estimate_final+","+er.property_value_string+","+er.experimental_parameters.get("Interpretation of results"));
//				}
				
//				if(rbs.reason.contains("Can't assign score based on max degradation value")) {
//					System.out.println(er.casrn+","+er.property_value_point_estimate_final+","+er.property_value_string+","+er.experimental_parameters.get("Interpretation of results"));
//				}
				
//				if(rbs.reason.contains("Can't assign score based on min degradation value")) {
//					System.out.println(er.casrn+","+er.property_value_point_estimate_final+","+er.property_value_string+","+er.experimental_parameters.get("Interpretation of results"));
//				}
				
				er.keep=false;
			}
		}		
		
//		System.out.println(er.toJSON());
		return er;
	}

	private void setBiodegradationParameters(ExperimentalRecord er) {
		er.experimental_parameters=new Hashtable<>();
		if (this.oxygenConditions!=null) {
			er.experimental_parameters.put("Oxygen conditions", this.oxygenConditions);
		}
		
		for (RecordDegradation recBio:this.recordsDegradation) {
			Double duration=null;
			if(recBio.samplingTime.contains("d")) {
				duration = Double.parseDouble(recBio.samplingTime.replace(" d", ""));
			} else if(recBio.samplingTime.contains("h")) {
				duration = Double.parseDouble(recBio.samplingTime.replace(" h", ""));
				duration/=24.0;
			} else if(recBio.samplingTime.contains("wk")) {
				duration = Double.parseDouble(recBio.samplingTime.replace(" wk", ""));
				duration*=7.0;
			} else {
				System.out.println("Bad duration units:"+recBio.samplingTime);
			}
			recBio.samplingTimeDays=duration;
		}
		
		er.experimental_parameters.put("Biodegradation records", this.convertRecordsDegradationToString());
		er.experimental_parameters.put("Reliability",this.reliability);
		er.experimental_parameters.put("Test guideline",this.testGuideline);
		er.experimental_parameters.put("Test guideline compliance",this.GLP_compliance);
		er.experimental_parameters.put("Interpretation of results",this.interpretationOfResults);
	}

	private ExperimentalRecord createExperimentalRecord(String propertyName) {
		ExperimentalRecord er=new ExperimentalRecord();
		
		er.date_accessed = dateAccessed;
		er.source_name = ExperimentalConstants.strSourceEChemPortal;
		er.original_source_name = this.source;
		er.property_name=propertyName;
		
		if(numberType!=null) {
			if (numberType.equals("CAS Number")) { 
				er.casrn = number;
			} else if (numberType.equals("EC Number")) {
				er.einecs = number; 
			}
		}
		
		if (substanceName!=null && !substanceName.equals("-") && !substanceName.contains("unnamed")) {
//			er.chemical_name = StringEscapeUtils.escapeHtml4(substanceName);
			er.chemical_name = substanceName;
			String nameFixed=ChemicalNameFixer.fixName(er.chemical_name);
//			if(!nameFixed.equals(er.chemical_name)) {
//				System.out.println(nameFixed);
//			}
			er.chemical_name=nameFixed;
		}
		er.url = url;
		return er;
	}

	public List<ExperimentalRecord> toExperimentalRecordsKoc() {
		
		List<ExperimentalRecord>ers=new ArrayList<>();
		double cutoff=8;

		
		for (RecordKoc recordKoc:this.recordsKoc) {
			ExperimentalRecord er=createExperimentalRecord(ExperimentalConstants.strKOC);
			
			er.experimental_parameters=new Hashtable<>();
			er.experimental_parameters.put("Measurement method",this.method);
			er.experimental_parameters.put("Reliability",this.reliability);
			er.experimental_parameters.put("Test guideline",this.testGuideline);
			er.experimental_parameters.put("Test guideline compliance",this.GLP_compliance);
			
			er.experimental_parameters.put("Media",this.media);
			
			
			recordKoc.value=recordKoc.value.replace("without unit", "dimensionless").replace("> 427000 .","> 427000 L/kg");
			
			String [] vals = recordKoc.value.split(" ");
			String units=vals[vals.length-1];
			
			String strValue="";
			
			for (int i=0;i<vals.length-1;i++) {
				strValue+=vals[i]+"";
			}
			strValue=strValue.trim();
			
			Estimate estimate=EstimateParser.parse(strValue);
//			System.out.println(JsonUtilities.gsonPretty.toJson(this));
//			System.out.println(JsonUtilities.gsonPretty.toJson(estimate)+"\n");

			er.property_value_point_estimate_original = estimate.point;
			er.property_value_min_original = estimate.min;
			er.property_value_max_original = estimate.max;
			
			
			Set<String> L_PER_KG_EQUIVS = Set.of("L/kg", "mL/g", "dm3/kg", "(mL/g)", "cm3/g", "ml/g", "cm^3/g",
					"Cm3g-1", "cm3.g-1","cm-3/g","cm3 g-1");			
			
			if (units.equals("dimensionless")) {
				er.property_value_units_original = ExperimentalConstants.str_dimensionless;
			
			}else if (L_PER_KG_EQUIVS.contains(units)) {
				er.property_value_units_original = ExperimentalConstants.str_L_KG;
			} else if (units.equals("mL/kg")) {
				er.property_value_units_original = ExperimentalConstants.str_mL_kg;
			} else {
//				System.out.println("bad units: "+recordKoc.value+"==>"+units);
				er.property_value_units_original=units;
			}
			
			String correctedUnits=null;
			
			if (units.equals("dimensionless")) {
				correctedUnits = ExperimentalConstants.str_dimensionless;
			}else if (L_PER_KG_EQUIVS.contains(units)) {
				correctedUnits = ExperimentalConstants.str_L_KG;
			} else if (units.equals("mL/kg")) {
				correctedUnits = ExperimentalConstants.str_mL_kg;
			} else {
//				System.out.println("handle "+recordKoc.value);
				correctedUnits=units;
			}
			
			er.property_value_units_original=correctedUnits;

			if(recordKoc.type.contains("log"))  {
				
				if(correctedUnits.equals(ExperimentalConstants.str_dimensionless) 
						|| correctedUnits.equals(ExperimentalConstants.str_L_KG)) {
					
					er.property_value_units_original=ExperimentalConstants.str_LOG_L_KG;
					
					boolean exceedsCutoff = exceedsCutoff(er, cutoff);
					
					if(exceedsCutoff) {
						er.keep=false;
						er.updateNote("type=log Koc but value>"+cutoff);
						er.reason="type=log Koc and but value>"+cutoff;
						
//						System.out.println("***"+recordKoc.type+"\t"+recordKoc.value);
					}
					
//					System.out.println(er.casrn+"\t"+er.property_value_units_original+"\t"+recordKoc.value);
				} else {
					er.keep = false;
					er.reason="invalid units";
					er.updateNote("type=log Koc but units="+correctedUnits);
					
//					System.out.println("***"+recordKoc.type+"\t"+recordKoc.value);
					
				}
			} else  {//not log value

				if(correctedUnits.equals(ExperimentalConstants.str_dimensionless)) {
					
					
					boolean exceedsCutoff = exceedsCutoff(er, cutoff);
					
					if(exceedsCutoff) {
						er.property_value_units_original=ExperimentalConstants.str_L_KG;
						er.updateNote("Units changed from dimensionless to L/kg (value > "+cutoff+")");
					} else {
						er.keep = false;
						er.updateNote("type=Koc but units="+correctedUnits+" (value < "+cutoff+" so can't tell if log value or not)");
						er.reason="invalid units";
					}

				}
			}
			
//			System.out.println(recordKoc.type+"\t"+strValue+"\t"+ er.property_value_units_original);
			unitConverter.convertRecord(er);
//			System.out.println(this.number+"\t"+recordKoc.type+"\t"+units);
			
			if(this.method==null || this.method.equals("other")) {
				er.keep=false;
				er.reason="Invalid experimental method";
			}
			

			ers.add(er);
		}
		
		return ers;
		
	}

	private boolean exceedsCutoff(ExperimentalRecord er, double cutoff) {
		boolean valueOk=false;
		if(er.property_value_point_estimate_original!=null && er.property_value_point_estimate_original>cutoff) {
			valueOk=true;
		}
		if(er.property_value_min_original!=null && er.property_value_min_original>cutoff) {
			valueOk=true;
		}
		if(er.property_value_max_original!=null && er.property_value_max_original>cutoff) {
			valueOk=true;
		}
		return valueOk;
	}

}

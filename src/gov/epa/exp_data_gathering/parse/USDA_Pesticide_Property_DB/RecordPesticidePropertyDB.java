package gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB;

import java.beans.Transient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.ParameterValue.ExpPropUnit;
import gov.epa.exp_data_gathering.parse.ParameterValue.Parameter;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;


/**
* @author TMARTI02
*/
public class RecordPesticidePropertyDB {
	
	String casrn;
	String chemicalName;
	String note;
	String soilType;
    String temperature;
    String kd;
    String koc;
	String percentageOrganicMatter;
    String pH;
    List<String> references=new ArrayList<>();
    String source;
	
	public static final String sourceName="USDA Pesticide Properties Database";
	
	static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");
	
	
//	static String clean(String str) {
//		return str.replaceAll("[^a-zA-Z0-9]", "");
//	}
	
	public static List<RecordPesticidePropertyDB> parseChemicalTextFile() {
		
		List<RecordPesticidePropertyDB>recs=new ArrayList<>();
		
		String folder="data\\experimental\\"+sourceName+"\\text files\\";
		String filepath=folder+"USDA pesticide prop db chemicals.txt";

		HashSet<String>names=new HashSet<>();
		
		
		Map<String,String>htRefs=parseReferencesTextFile();
		
		HashSet<String>headers=new HashSet<>();
		
		 String ansiEncoding = "windows-1252";
		
		try (BufferedReader br=new BufferedReader(new FileReader(filepath,Charset.forName(ansiEncoding)))){
			
			String casrn="";
			String chemicalName="";
			
			while (true) {
				String Line=br.readLine();
				
				if(Line==null)break;
				
				Line=Line.replace("\u00A0", " ");
				
//				Line=Line.trim();
				
				if(Line.toLowerCase().contains("name:") || Line.toLowerCase().contains("name :")) {
					chemicalName=Line.substring(Line.indexOf(":")+1,Line.indexOf("CAS")).trim();
					casrn=Line.substring(Line.indexOf("CASRN:")+6,Line.length()).trim();
				}
				
			
//				if(Line.equals("soiltype         temp.    Kd     Koc    %om    pH   reference")) {
//					getDataType1(recs, names, br, casrn, chemicalName);
//				} else if (Line.equals("soiltype      temp.   Kd     Koc     %om   pH  source reference")) {
//					getDataType2(recs, names, br, casrn, chemicalName);
//					
//				} else if(Line.contains("Koc") && Line.contains("soiltype")){
////					System.out.println(Line);
//					headers.add(Line);
//				}

				
//				if(casrn.equals("54593-83-8"))
//					System.out.println("\n"+casrn+"\t"+Line);

				
				Line=Line.replace("Soil Type", "soiltype");
				
				if(Line.contains("Koc") && Line.contains("soiltype")){
					
					List<String>kocLines=new ArrayList();
					
					while (true) {
						
						String line=br.readLine();
						if(line.contains("---")) line=br.readLine();
						if(line==null) break;
						if(line.isBlank()) continue;
						if(line.toLowerCase().contains("field dissipation half")) break;
//									System.out.println(r.casrn+"\t"+dataLine);
						line=line.replace("\u00A0", " ");
						kocLines.add(line);
					}
					handleKocLines(recs, names, casrn, chemicalName, Line, htRefs,kocLines);
				}
			}
			
//			for(RecordPesticidePropertyDB r:recs) {
//				if(r.koc.contains("-") || r.koc.contains(",")) {
//					System.out.println(gson.toJson(r));
//				}
//				
//			}
			
			
			
			for (String header:headers) {
				System.out.println("here..."+header);
			}
			
			
//			System.out.println(gson.toJson(recs));
//			System.out.println(names.size());
			
			createExcelFile(recs, "data\\experimental\\"+sourceName+"\\excel files\\USDA pesticide prop db chemicals.xlsx");
			
		} catch (Exception  ex) {
			ex.printStackTrace();
		}
		
		
		return recs;
		
		
	}


	
	public static boolean hasNoNumbers(String input) {
        if (input == null || input.isEmpty()) {
            return true; // Consider empty or null strings as having no numbers
        }

        for (char ch : input.toCharArray()) {
            if (Character.isDigit(ch)) {
                return false; // Found a number, return false
            }
        }
        return true; // No numbers found, return true
    }

	private static void handleKocLines(List<RecordPesticidePropertyDB> recs, HashSet<String> names, String casrn,
			String chemicalName, String header, Map<String, String> htRefs,List<String>kocLines) throws IOException{
		
//		Iterator<String>itLines=kocLines.iterator();
		
		for(int i=0;i<kocLines.size();i++) {
			
			RecordPesticidePropertyDB r=new RecordPesticidePropertyDB();	
			
			String line=kocLines.get(i);
			String nextLine=null;
			
			if(i<kocLines.size()-1) {
				nextLine=kocLines.get(i+1);
//				System.out.println(nextLine);
			}
						
			r.casrn=casrn;
			r.chemicalName=chemicalName;
			
			names.add(r.chemicalName);
			
			getTemperature(header.toLowerCase(), line, r);
			getSoilType(casrn, header.toLowerCase(), line, r);
			
			
			r.kd=getValue(header, line, "Kd",1);


			getReference(header.toLowerCase(), line, r,htRefs);

			int result=getKocValue(chemicalName, header, line, nextLine, r,htRefs);

			if(result==1) {
//				System.out.println(chemicalName+"\thas dashed Koc");
				i++;//skip line
			}
			
			
			getpHValue(header, line, r);
			getSource(header.toLowerCase(), line, r);
			getPOMValue(header, line, r);
			
			
			if(r.source!=null && r.source.equals("Wauchope")) r.references.add("Wauchope");
			
			
//			if(casrn.contains("54593-83-8")) {
//			System.out.println(header+"\n"+line+"\n");
//			
//			for (int i = 0; i < r.soilType.length(); i++) {
//	            char ch = r.soilType.charAt(i);
//	            // Print the character and its Unicode code point in hexadecimal format
//	            System.out.printf("Character: '%c' | Unicode: \\u%04X%n", ch, (int) ch);
//	        }
//		}

			if(r.references.size()==0 && r.source!=null) r.references.add(r.source);
			recs.add(r);
			
		}
		
		
		
		
	}



	private static void getSoilType(String casrn, String header, String line, RecordPesticidePropertyDB r) {
		


//		if(header.contains("temp") && line.length()>=header.indexOf("temp")) {
//			
//			r.soilType=line.substring(0,header.indexOf("temp")).trim();
//			
//			if(r.soilType.isBlank()) r.soilType=null;
////			else System.out.println(r.casrn+"\t"+r.soilType);
//			
//		} else {
////			System.out.println(casrn+"\n"+header+"\n"+line+"\n");
//		}
		
		
			
		if(line.indexOf("  ")==-1)  {
//			System.out.println(line);
		}else {
			
			if(header.contains("temp") && line.length()>=header.indexOf("temp")) {
				
				if(line.indexOf("  ")>header.indexOf("temp")+2) {
					r.soilType=line.substring(0,header.indexOf("temp")).trim();
//					System.out.println("SoilType1="+r.soilType);
				} else {
					r.soilType=line.substring(0,line.indexOf("  ")).trim();
//					System.out.println("SoilType2="+r.soilType);
				}
				if(r.soilType.isBlank()) r.soilType=null;
			}
			
			
//			System.out.println(r.soilType);
		}
		
			

	}



	private static void getTemperature(String header, String line, RecordPesticidePropertyDB r) {
		
		r.temperature=getValue(header, line, "temp",1);
		if(r.temperature!=null) r.temperature=r.temperature.replace("C", "").replace("M", "");
		if(hasNoNumbers(r.temperature))
			r.temperature=null;
		
//		if(r.temperature!=null) 
//			System.out.println(r.casrn+"  temp="+r.temperature+"\n"+header+"\n"+line+"\n");
	}



	private static int getKocValue(String chemicalName, String headerLine, String line, String nextLine, RecordPesticidePropertyDB r,Map<String, String> htRefs) throws IOException{
		
//		String[] headers = headerLine.trim().split("\\s+");
//		List<String>headerList=new ArrayList<>(Arrays.asList(headers));
//		String nextHeader=headerList.get(headerList.indexOf("Koc")+1);
//		System.out.println(nextHeader);

		int result=0;
		
		String field="Koc";
		r.koc = getValue(headerLine, line, field,2);
		
		
		if(r.koc!=null && r.koc.contains("*") && !r.koc.contains("**")) {
			return result;
		}
		
		
		if(endsWithHyphen(r.koc)) {
			result=1;
			r.koc+=getValue(headerLine,nextLine,field,2);
			getReference(headerLine, nextLine, r, htRefs);
				
//				if(r.reference!=null) {
//					System.out.println(r.casrn+"  "+r.koc+"\t"+r.reference);
//				}
				
//			System.out.println(r.casrn+"   "+value);

		} 
		
		if(nextLine!=null){
			String kocNext = getValue(headerLine, nextLine, field,2);
			
			if(kocNext==null) {//just have another reference?
//				System.out.println(chemicalName+"\tKocNext==null:\t"+nextLine);
				String reference=getReference(headerLine, nextLine, r, htRefs);
				
				if(reference!=null) {
					result =1;
//					System.out.println(chemicalName+"\t"+reference);
				}
			}
		}
		
		return result;
		
			
	}



	private static String getValue(String headerLine, String line, String field,int extraChars) {
		
		
		if(!headerLine.contains(field)) return null;
		
		if(line.length()>headerLine.indexOf(field)) {

			String value=line.substring(headerLine.indexOf(field)-extraChars,line.length());
			
			int countRemoved=0;
			while (value.substring(0, 1).equals(" ")) {
				value=value.substring(1);//trim off preceeding spaces
				countRemoved++;
				
				if(value.length()==0) return null;
			}
			
			if(countRemoved>3)return null;
			
//			System.out.println(field+"\t"+countRemoved);
			
			if(value.indexOf(" ")!=-1) {
				value=value.substring(0,value.indexOf(" "));
			}

//			value=clean(value);
			
			return value;
		}
		
		return null;
			
	}
	
	private static void getpHValue(String header, String line, RecordPesticidePropertyDB r) {
		
		if(!header.contains("pH")) return;
		
		
		r.pH=getValue(header, line, "pH", 1);
		
		if(r.pH!=null && (r.pH.equals("ND") || r.pH.equals("M"))) {
			r.pH=null;
		}

//		if(r.pH!=null)
//			System.out.println(r.casrn+"\tpH="+r.pH+"\n"+header+"\n"+line+"\n");
	}
	
	
	private static void getPOMValue(String header, String line, RecordPesticidePropertyDB r) {
		if(!header.contains("%om")) return;
		r.percentageOrganicMatter=getValue(header, line, "%om", 1);

		if(r.percentageOrganicMatter!=null) {
			if( r.percentageOrganicMatter.equals("N/A") || r.percentageOrganicMatter.equals("EESADV")) r.percentageOrganicMatter=null;
		}
		
	}

	public static String removeAfterCommaOrSpace(String str) {
        if (str == null || str.isEmpty()) {
            return str; // Return the original string if it's null or empty
        }

        // Find the index of the first comma or space
        int commaIndex = str.indexOf(',');
        int spaceIndex = str.indexOf(' ');

        int endIndex;
        if (commaIndex == -1 && spaceIndex == -1) {
            // No comma or space found
            return str;
        } else if (commaIndex == -1) {
            // Only space found
            endIndex = spaceIndex;
        
        } else if (spaceIndex == -1) {
            // Only comma found
            endIndex = commaIndex;
        } else {
            // Both comma and space found, take the minimum index
            endIndex = Math.min(commaIndex, spaceIndex);
        }

        // Return the substring from the start to the determined end index
        return str.substring(0, endIndex);
    }
	
	private static String getReference(String header, String line, RecordPesticidePropertyDB r, Map<String, String> htRefs) {
		
//		r.reference=getValue(header, line, "reference",2);
		
		String field="reference";
		
		String reference=null;
				
		if(!header.contains(field)) return null;
		
		if(line.length()>header.indexOf(field)) {
			reference=line.substring(header.indexOf(field)-1,line.length()).trim();
			if(reference.isBlank())reference=null;
		} 
		
		if(reference==null)return null;

		String abbrev=removeAfterCommaOrSpace(reference);
		
//		System.out.println(abbrev);
		
		if(abbrev==null) {
			System.out.println(r.casrn+"\tabbrev=null\n"+header+"\n"+line+"\n");
			return null ;
		}
		
		
		
		
//		System.out.println(abbrev);
			
		if(abbrev.equals("W")) reference="Wauchope";
		else if(htRefs.containsKey(abbrev)) {
			reference=reference.replace(abbrev,htRefs.get(abbrev));
		} else {
//			System.out.println(r.casrn+"\t"+abbrev+"\tNo match");
		}

		if(reference!=null)r.references.add(reference);
		
		return reference;
	}
	
	
	private static void getSource(String header, String line, RecordPesticidePropertyDB r) {
		
		r.source=getValue(header,line,"source",2);
		
		if(r.source==null)return;
		
		if(r.source.equals("M")) r.source="Manufacturer";
		else if(r.source.equals("R")) r.source="Review";
		else if(r.source.equals("H")) r.source="Handbook";
		else if(r.source.equals("E")) r.source="Experiment";
		else if(r.source.equals("C")) r.source="Calculated";
		else if(r.source.equals("U")) r.source="Unknown";
		else if(r.source.equals("P")) r.source="EPA data";
		else if(r.source.equals("W")) r.source="Wauchope";
		else {
//			System.out.println(r.source+"\tunknown");
		}
		
		
//		System.out.println(r.source);

	}

	public static Map<String,String> parseReferencesTextFile() {
		
		Map<String, String>htRefs=new TreeMap<>();
		
		String folder="data\\experimental\\"+sourceName+"\\text files\\";
		String filepath=folder+"USDA pesticide prop db references.txt";

		try (BufferedReader br=new BufferedReader(new FileReader(filepath))){
			
			String reference=null;
			String refNum=null;
			
			while (true) {
				
				String Line=br.readLine();
				
				if(Line==null) break;
				
				Line=Line.replace("\u00A0", " ");
				
				if(Line.length()<3 || Line.isBlank()) continue;
				
				
				if(!Line.substring(0,3).isBlank()) {
					
					if(refNum!=null) {
//						System.out.println(refNum+"\t"+reference);
						htRefs.put(refNum,reference);
					}
					
					refNum=Line.substring(0,9).trim();
					reference=Line.substring(11).trim();
				} else {
					reference+=" "+Line.trim();
				}
				
			}
			
			
			htRefs.put(refNum,reference);
			
//			System.out.println(gson.toJson(htRefs));
//			System.out.println(names.size());
			
		} catch (Exception  ex) {
			ex.printStackTrace();
		}
		
		
		return htRefs;
		
		
	}


	
	



//	private static void fixHyphenatedKoc(BufferedReader br, RecordPesticidePropertyDB r) throws IOException {
//		
//
//		String line2=br.readLine().replace("\u00A0", " ");
//		
//		
//		if (line2.length() > 53) {
//			String koc2=line2.substring(33,52).trim();
//			r.koc+=koc2;
//			r.reference = line2.substring(52).trim();
//			if(r.reference.isBlank()) r.reference=null;
//		} else {
//			String koc2=line2.substring(33).trim();
//			r.koc+=koc2;
//		}
//		
////		System.out.println(r.casrn+"\t"+r.koc+"\t"+r.reference);
//	}
	
	
	public static boolean endsWithHyphen(String str) {
        // Check if the string is not null and not empty, and if the last character is a hyphen
        return str != null && !str.isEmpty() && str.charAt(str.length() - 1) == '-';
    }
	
	
	public static void createExcelFile(List<RecordPesticidePropertyDB> records, String fileName) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Pesticide Properties");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"CASRN", "Chemical Name", "Note", "Soil Type", "Temperature", "Kd", "Koc", "%OM", "pH", "Reference","Source"};
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        // Fill data rows
        int rowNum = 1;
        for (RecordPesticidePropertyDB record : records) {
            Row row = sheet.createRow(rowNum++);
            
            int col=0;
            
            row.createCell(col++).setCellValue(record.casrn);
            row.createCell(col++).setCellValue(record.chemicalName);
            row.createCell(col++).setCellValue(record.note);
            row.createCell(col++).setCellValue(record.soilType!=null ? record.soilType :"");
            row.createCell(col++).setCellValue(record.temperature != null ? record.temperature: "");
            row.createCell(col++).setCellValue(record.kd != null ? record.kd : "");
            row.createCell(col++).setCellValue(record.koc != null ? record.koc: "");
            row.createCell(col++).setCellValue(record.percentageOrganicMatter != null ? record.percentageOrganicMatter : "");
            row.createCell(col++).setCellValue(record.pH != null ? record.pH: "");
            row.createCell(col++).setCellValue(record.references.toString());
            row.createCell(col++).setCellValue(record.source);
        }

        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            workbook.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                workbook.close();
            } catch (Exception ignore) {
            }
        }
    }
	
	ExperimentalRecord toExperimentalRecord() {

		ExperimentalRecord er = new ExperimentalRecord();
		er.date_accessed = "10/3/2025";
		er.keep = true;
		er.casrn = casrn;
		er.chemical_name = chemicalName;

		if(er.casrn.equals("3424-82-6(o,p')")) er.casrn="3424-82-6";
		if(er.casrn.equals("50-29-2")) er.casrn="50-29-3";
		if(er.casrn.equals("251168-15-4")) er.casrn="25168-15-4";
		if(er.casrn.equals("109-93-4")) er.casrn="106-93-4";
		if(er.casrn.equals("23103-98-3")) er.casrn="23103-98-2";
		if(er.casrn.equals("1114-72-2")) er.casrn="1114-71-2";
		if(er.casrn.equals("58727-55-8")) er.casrn="38727-55-8";
		
		if(!ParseUtilities.isValidCAS(er.casrn)) {
			System.out.println(er.casrn+"\t"+er.chemical_name+"\tinvalid cas");
		}
		

		PublicSource ps=new PublicSource();
		ps.url="https://www.ars.usda.gov/northeast-area/beltsville-md-barc/beltsville-agricultural-research-center/adaptive-cropping-systems-laboratory/docs/ppd/pesticide-properties-database/";
		ps.name="The ARS Pesticide Properties Database";
		er.publicSource=ps;


		er.property_value_string = koc;
		er.property_name = ExperimentalConstants.strKOC;
		
		handleKoc(er);
		
		er.pH=pH;
			
		er.experimental_parameters=new TreeMap<>();
		if(soilType!=null && !soilType.isBlank()) {
			er.experimental_parameters.put("Soil_Type", soilType);
		}
		
		if(percentageOrganicMatter!=null && !percentageOrganicMatter.isBlank()) {
			try {
				double POM=Double.parseDouble(percentageOrganicMatter);
				
				if(POM>0)				
					er.experimental_parameters.put("Percentage_Organic_Matter",POM);
				
				
			} catch (Exception ex) {
				System.out.println(er.chemical_name+"\t"+percentageOrganicMatter+"\tparseError");
			}
		}
		
		if(er.experimental_parameters.size()==0) er.experimental_parameters=null;
		
		if(temperature!=null) {
			try {
				
				if(temperature.contains("-")) {
					
					String []temps=temperature.split("-");

//					er.parameter_values=new ArrayList<>();
//					ParameterValue pv=new ParameterValue();
//					pv.parameter=pv.new Parameter();
//					pv.parameter.name="Temperature";
//					pv.unit=pv.new ExpPropUnit();
//					pv.unit.name="DEG_C";
//					pv.unit.abbreviation="C";
					
					double valueMin=Double.parseDouble(temps[0]);
					double valueMax=Double.parseDouble(temps[1]);
					
					double valuePointEstimate=(valueMin+valueMax)/2.0;
					
					if(valuePointEstimate<20 || valuePointEstimate>30) {
						er.keep=false;
						er.reason="Invalid temperature";
					}
					er.temperature_C=valuePointEstimate;		
//					er.parameter_values.add(pv);
					
				} else {
					try {
						er.temperature_C=Double.parseDouble(temperature);			
					} catch (Exception ex) {
						System.out.println("Error parsing temp="+temperature);
					}
				}
					
				
			} catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("Couldnt parse temperature="+temperature);
			}
		}
		
		
		
		uc.convertRecord(er);
		

		//		er.temperature_C=rs.temperature;

		er.source_name = RecordPesticidePropertyDB.sourceName;
		// er.original_source_name = rs.referenceAbbreviated.get(i);

		addLiteratureSource(er);
		
		if(source!=null && source.equals("Calculated")) {
			er.keep=false;
			er.reason="calculated";
		}
		
		


		if(er.keep&& er.property_value_point_estimate_final==null &&  er.property_value_min_final==null && er.property_value_max_final==null) {
			er.keep=false;
			er.reason="No property value";
		}


		return er;
	}



	private void handleKoc(ExperimentalRecord er) {

		 
		if(koc==null) {
			//do nothing

		} else if(koc.contains("SALT")) {
			er.keep=false;
			er.reason="Unspecified salt";
		
		} else if(koc.contains("*") && !koc.contains("**")) {
			er.keep=false;
			er.reason="Selected value";
		
		} else if(koc.contains("est")) {
			er.keep=false;
			er.reason="Estimated";
		} else if(koc.contains("(")) {
			
			String val=koc.substring(0,koc.indexOf("("));
			
//			if(val.contains("000")) System.out.println("here99,"+koc+"\t"+val);
			
			val=val.replace("41,000","41000");
			
			
			er.property_value_point_estimate_original=Double.parseDouble(val);


		} else if(koc.contains("-")) {
			
			String [] vals=koc.split("-");
			
			double val1=Double.parseDouble(vals[0]);
			double val2=Double.parseDouble(vals[1]);
			
			if(val1<val2) {
				er.property_value_min_original=val1;
				er.property_value_max_original=val2;
			} else {
				er.property_value_min_original=val2;
				er.property_value_max_original=val1;
			}

		
		} else if(koc.contains("CIS")) {
			
			if(er.chemical_name.equals("1,3 DICHLOROPROPENE")) {
				er.chemical_name="cis-1,3 DICHLOROPROPENE";
				er.casrn="10061-01-5";
				er.property_value_point_estimate_original=Double.parseDouble(koc.replace("CIS", ""));
			}
			
		} else if(koc.contains("TRAN")) {
			
			if(er.chemical_name.equals("1,3 DICHLOROPROPENE")) {
				er.chemical_name="trans-1,3 DICHLOROPROPENE";
				er.casrn="10061-02-6";
				er.property_value_point_estimate_original=Double.parseDouble(koc.replace("TRAN", ""));
			}
		
		} else if(koc.contains("CA.")) {

			er.property_value_point_estimate_original=Double.parseDouble(koc.replace("CA.", ""));
			er.property_value_numeric_qualifier="~";
		} else if(koc.contains("CA")) {
			
			er.property_value_point_estimate_original=Double.parseDouble(koc.replace("CA", ""));
			er.property_value_numeric_qualifier="~";
		} else if(koc.contains("ca")) {

			er.property_value_point_estimate_original=Double.parseDouble(koc.replace("ca", ""));
			er.property_value_numeric_qualifier="~";
		
		
		} else if(koc.contains("PARENT")) {
			er.property_value_point_estimate_original=Double.parseDouble(koc.replace("PARENT", ""));
			er.keep=false;
			er.reason="Value is for parent compound";

		} else if(koc.contains("EST")) {
			er.keep=false;
			er.reason="Estimated";
		
		} else if(koc.contains(">=")) {
			er.property_value_point_estimate_original=Double.parseDouble(koc.replace(">=", ""));
			er.property_value_numeric_qualifier=">=";

		} else if(koc.contains("<")) {
			er.property_value_point_estimate_original=Double.parseDouble(koc.replace("<", ""));
			er.property_value_numeric_qualifier="<";

		} else if(koc.contains(">")) {
			er.property_value_point_estimate_original=Double.parseDouble(koc.replace(">", ""));
			er.property_value_numeric_qualifier=">";
		} else if(koc.contains(",")) {
			
			String [] vals=koc.split(",");
			

			if(vals[1].contentEquals("000")) {
				koc=vals[0]+vals[1];
				
				er.property_value_point_estimate_original=Double.parseDouble(koc);

//				System.out.println("koc="+koc);
//				System.out.println(vals[0]+"\t"+vals[1]+"\t"+koc);
				
//				System.out.println("koc fixed="+koc);
			} else {
				//TODO technically these are probably two values and not a range- so would need to split into separate records but there is only a handful of them
//				System.out.println(vals[0]+"\t"+vals[1]);
				
				double val1=Double.parseDouble(vals[0]);
				double val2=Double.parseDouble(vals[1]);
				
				if(val1<val2) {
					er.property_value_min_original=val1;
					er.property_value_max_original=val2;
				} else {
					er.property_value_min_original=val2;
					er.property_value_max_original=val1;
				}
//				System.out.println(val1+"\t"+val2);
			}
			
		} else if(koc.contains("**")) {
			
			String value=koc.replace("*", "");
			try {
				er.property_value_point_estimate_original=Double.parseDouble(value);					
			} catch (Exception ex) {
				System.out.println("Error parsing Koc="+koc);
//				ex.printStackTrace();
			}


				
		} else {

			try {
				er.property_value_point_estimate_original=Double.parseDouble(koc);
				er.property_value_point_estimate_final=er.property_value_point_estimate_original;
				
			} catch (Exception ex) {
				System.out.println("Couldnt parse:"+koc);
			}

		}
		
		if(er.property_value_string!=null && er.property_value_string.equals("222,500")) {
			er.keep=false;
			er.reason="Value is ambiguous";
		}
		
		
		er.property_value_units_original = ExperimentalConstants.str_L_KG;
		
		if(er.property_value_point_estimate_original!=null && er.property_value_point_estimate_original==0.0) {
			er.keep=false;
			er.reason="Koc=0";
		}
		
		
		if(references.size()==0 && er.keep) {
			er.keep=false;
			er.reason="No reference";//does this happen?
		} else {
//			System.out.println(er.casrn+"\t"+koc+"\t"+reference);				
		}

		
		
	}
	
	private void addLiteratureSource(ExperimentalRecord er) {

		er.reference= String.join("; ", references);
		
		if(er.reference!=null) {
			if(er.reference.equals("ENT") || er.reference.equals("RENT")) {
				er.keep=false;
				er.reason="Parent compound";
			} else if(er.reference.equals("Wauchope")) {
				er.keep=false;
				er.reason="Duplicative of Wauchope 1992?";
			} 
				
		} else if (er.keep && references.size()==0) {
			er.keep=false;
			er.reason="Missing reference";
		}
		
	}



	public static void main(String[] args) {
		parseChemicalTextFile();
//		parseReferencesTextFile();

	}

}

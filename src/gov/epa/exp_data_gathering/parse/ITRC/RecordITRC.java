package gov.epa.exp_data_gathering.parse.ITRC;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.Montgomery.RecordMontgomery;

/**
* @author TMARTI02
*/
public class RecordITRC {

	String PFAS_Name;
	String Acronym;
	String Isomer;
	String LogKocWithStdDev;
	String Type;
	String Applicable_Matrices;
	String Testing_Conditions;
	String Reference_Label;
	String Reference_Citation;
	int Reference_Number;
	
	public static final String sourceName="ITRC July 2023";
	static String filename="PhysChemProp_Table_July2023-FINAL.xlsx";
	
	transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");
	

	Hashtable<Integer, String> getCitations(Sheet sheetReferences) {
		
		Hashtable<Integer,String>ht=new Hashtable<>();
		
		for (int rowNum=6;rowNum<=102;rowNum++) {
			Row row=sheetReferences.getRow(rowNum);
			Integer citationNumber=(int) row.getCell(0).getNumericCellValue();
			String richCitation=row.getCell(3).getRichStringCellValue().getString();

			ht.put(citationNumber, richCitation);
			
//			if(citationTag.equals("Munoz, Budzinski, and Labadie, 2017")) System.out.println(richCitation);

			
//			System.out.println(citationTag);
		}
		return ht;
	}
	public List<RecordITRC> parseExcelFile() {
		
		List<RecordITRC>recs=new ArrayList<>();
		
		try {

			String filePath="data\\experimental\\"+sourceName+"\\excel files\\"+filename;
			
			System.out.println(filePath);
			
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

			HashSet<String>chems=new HashSet();
			
//			List<String>allCitations=getAllCitations();
			 
			Sheet sheetKoc=wb.getSheet("Log Koc");
			Sheet sheetReferences=wb.getSheet("References");
			
			Hashtable<Integer,String>htCitations=getCitations(sheetReferences);
			
			Hashtable<String,String>htNameToCAS=new Hashtable<>();
			
			String name=null;
			String acronym=null;
			
			for (int rowNum=7;rowNum<=268;rowNum++) {
				
				Row row=sheetKoc.getRow(rowNum);
				
				
				Cell cellLogKoc=row.getCell(3);
				
				if(cellLogKoc==null) continue;
				if(cellLogKoc.getStringCellValue().isBlank()) continue;
				
				RecordITRC rec=new RecordITRC();
				recs.add(rec);
				
				Cell cellName=row.getCell(0);
				Cell cellAcronym=row.getCell(1);
				
				if(!cellName.getStringCellValue().isBlank()) {
					name=cellName.getStringCellValue();
				}
				
				if(!cellAcronym.getStringCellValue().isBlank()) {
					acronym=cellAcronym.getStringCellValue();
				}
				
				rec.PFAS_Name=name;
				rec.Acronym=acronym;
				rec.LogKocWithStdDev=cellLogKoc.getStringCellValue();

				rec.Isomer=row.getCell(2).getStringCellValue();
				rec.Type=row.getCell(4).getStringCellValue();
				rec.Applicable_Matrices=row.getCell(5).getStringCellValue();
				rec.Testing_Conditions=row.getCell(6).getStringCellValue();
				rec.Reference_Label=row.getCell(7).getRichStringCellValue().getString();
				
				CellValue cellValue = evaluator.evaluate(row.getCell(8)); 
				
				rec.Reference_Number=(int)cellValue.getNumberValue();
				
				rec.Reference_Citation=htCitations.get(rec.Reference_Number);

				
//				System.out.println(rowNum+"\t"+name+"\t"+cellLogKoc.getStringCellValue());
			
			}
			
//			System.out.println(gson.toJson(recs));	
			System.out.println("Number of chemicals="+chems.size()+"\nNumber of records="+recs.size());


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recs;
		
	}
	

	public static void main(String[] args) {
		RecordITRC rm=new RecordITRC();
		rm.parseExcelFile();
//		rm.getCitations();
		
	}
	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
			
		er.chemical_name=PFAS_Name.trim();
		er.synonyms=Acronym;
		
		er.property_name=ExperimentalConstants.strKOC;
		
		er.property_value_units_original=ExperimentalConstants.str_LOG_L_KG;

//		er.property_value_point_estimate_original=Koc;
		
		er.property_value_string = this.LogKocWithStdDev;
		
//		LogKocWithStdDev=LogKocWithStdDev.replace("2.02 (±0.01) to 2.1 4 (±0.02)","2.02 (±0.01) to 2.14 (±0.02)");
		LogKocWithStdDev=LogKocWithStdDev.replace("2.1 4 (","2.14 (");
		
		LogKocWithStdDev=LogKocWithStdDev.replace("1.1-2.1","1.1 to 2.1");
		LogKocWithStdDev=LogKocWithStdDev.replace("2.4-2.6","2.4 to 2.6");
		LogKocWithStdDev=LogKocWithStdDev.replace("4.3-6.0","4.3 to 6.0");
		LogKocWithStdDev=LogKocWithStdDev.replace("2.34-2.83","2.34 to 2.83");
//		LogKocWithStdDev=LogKocWithStdDev.replace("Â±", "±");
		
//		for (int i = 0; i < LogKocWithStdDev.length(); i++) {
//            char ch = LogKocWithStdDev.charAt(i);
//            if (!Character.isLetterOrDigit(ch)) {
//                int charCode = (int) ch; // Get the character code
//                System.out.println(LogKocWithStdDev+", Special character: '" + ch + "' - Code: " + charCode);
//            }
//        }
		
				
		if(LogKocWithStdDev.contains("to")) {
			
			String [] vals=LogKocWithStdDev.split(" to ");
			
			if(vals.length==2) {
				
				String val1=vals[0];
				
				if(val1.contains("(")) {
					val1=val1.substring(0,val1.indexOf("(")).trim();
				}
				
				String val2=vals[1];
				
				if(val2.contains("(")) {
					val2=val2.substring(0,val2.indexOf("(")).trim();
				}

//				System.out.println(val1+"\t"+val2);
				
				er.property_value_min_original=Double.parseDouble(val1);
				er.property_value_max_original=Double.parseDouble(val2);

				
			} else {
				System.out.println("Only 1 value with to:"+LogKocWithStdDev);//Doesnt happen
			}
		} else {
			String val=LogKocWithStdDev;
			if(val.contains("(")) {
				val=val.substring(0,val.indexOf("(")).trim();
			}
			er.property_value_point_estimate_original=Double.parseDouble(val);
//			System.out.println(LogKocWithStdDev+"\t"+er.property_value_point_estimate_original);
		}
		
		
		if(!this.Isomer.equals("Not available")) {		
			er.updateNote("Isomer="+this.Isomer);
			
//			if(Isomer.contains("branched")) {
//				er.keep=false;
//				er.reason="Branched structure";
//			}
		}
		
		
		
		er.experimental_parameters=new TreeMap<>();

		if(!this.Testing_Conditions.equals("NA") && !this.Testing_Conditions.equals("NR")) {		
			er.experimental_parameters.put("Testing_Conditions",Testing_Conditions);
		}
		
//		if(Testing_Conditions.contains("Mixture")) {
//			er.keep=false;
//			er.reason=Testing_Conditions;
//		}
		
		
		
		if(!Applicable_Matrices.equals("--") && !Applicable_Matrices.equals("NR")) {
			er.experimental_parameters.put("Media",Applicable_Matrices);
		}
		
		
//		System.out.println(er.property_value_string);
		
		LiteratureSource ls=new LiteratureSource();
		ls.citation=Reference_Citation;
		ls.name=Reference_Label;
		er.literatureSource=ls;
		
		
		if(ls.name.equals("3M company, 2021")) {
			System.out.println(gson.toJson(this));
			
		}
		
		PublicSource ps=new PublicSource();
		ps.name=sourceName;
		ps.url="https://pfas-1.itrcweb.org/external-data-tables/";
		er.publicSource=ps;
		
		er.source_name=sourceName;
		
		if(this.Type.contains("F")) {
			er.keep=false;
			er.reason="Field measurement";
		}

		if(Type.contains("M")) {
			er.keep=false;
			er.reason="Modeled";
		}
		
//		er.experimental_parameters.put("% organic carbon", this.foc);
		uc.convertRecord(er);
		
		return er;
	}

}

package gov.epa.exp_data_gathering.parse.Montgomery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;


/**
* @author TMARTI02
*/
public class RecordMontgomery {

	String chemicalName;
	String casrn;

	String soil;
	Double Kd;
	Double foc;
	Double Koc;
	Double pH;
	String source;
	
	String reference;
	String citation;
	
	public static final String sourceName="Montgomery 1993";
	static String filename="Montgomery.xlsx";
	
	transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");

	Double getNumericValue(Row row, int col) {

		if (row.getCell(col) == null)
			return null;

		try {
			return row.getCell(col).getNumericCellValue();
		} catch (Exception ex) {
//			System.out.println("Error parsing "+chemicalName+"\tfoc="+row.getCell(col).getStringCellValue());
		}

		return null;

	}	
	
	
	 List<String> getAllCitations () {
		
		String filePath="data\\experimental\\"+sourceName+"\\excel\\montgomery references.txt";

		
		String citations="";

		try (BufferedReader br=new BufferedReader(new FileReader(filePath, Charset.forName("UTF-8"))))  {
			while (true) {
				String Line=br.readLine();
				if (Line==null)break;
				citations+=Line+" \n";
			}
		} catch (Exception ex )  {
			ex.printStackTrace();
		}
		
		
        // Split the citations based on the pattern ".\n" which marks the end of each citation
        String[] citationArray = citations.split("\\.\\s*\n");

        // Create an ArrayList to hold the citations
        List<String> citationList = new ArrayList<>();

        // Add each citation to the list
        for (String citation : citationArray) {
            citation = citation.trim(); // Trim any leading/trailing whitespace
            citation=citation.replace("\n" ,"");
            
            if (!citation.isEmpty()) {
                citationList.add(citation + "."); // Add the period back to the end of each citation
            }
        }

        // Print the citations
        
        
        int counter=0;
        for (int i=0;i<citationList.size();i++) {
        	
        	String citation=citationList.get(i);

//        	if(i>0) {
//            	String prevCitation=citationList.get(i-1);
//            	
//            	if(!citation.substring(0,1).equals(prevCitation.substring(0,1))) {
//            		System.out.println(counter+"\t"+citation);
//            	}
//            	
//        		
//        	}
        	counter++;
        	
//        	if(counter>=100 && counter<200)
//        	System.out.println(counter+"\t"+citation);
        }
        
        
        return citationList;
	}
	
	
	public List<RecordMontgomery> parseExcelFile() {
		
		List<RecordMontgomery>recs=new ArrayList<>();
		
		try {

			String filePath="data\\experimental\\"+sourceName+"\\excel\\"+filename;
			
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

			HashSet<String>chems=new HashSet();
			
		
			List<String>allCitations=getAllCitations();
			 
			
			int rowNum=1;
			Hashtable<String,String>htNameToCAS=new Hashtable<>();
			while (true) {
				Row row=wb.getSheetAt(0).getRow(rowNum);
				if(row.getCell(0)==null)break;
				String name=row.getCell(0).getStringCellValue();
				String cas=row.getCell(1).getStringCellValue();
				htNameToCAS.put(name, cas);
//				System.out.println(name+"\t"+cas);
				rowNum++;
			}
			
			
			for (int i=1;i<wb.getNumberOfSheets();i++) {
				
				Sheet sheet=wb.getSheetAt(i);
				chems.add(sheet.getSheetName());
				
				
				String reference = getReference(sheet);
				List<String>citations=getCitations(allCitations, sheet, reference);
				
				String citation="";
				for(String cit:citations) {
					if(citation.equals(""))citation=cit;
					else citation+="; "+cit;
				}
				
//				System.out.println("\n"+sheet.getSheetName()+"\t"+citation);
//				System.out.println("\n"+sheet.getSheetName());
//				for(String fullCitation:fullCitations) {
//					System.out.println("\t"+fullCitation);
//				}
				
				
//				System.out.println(sheet.getSheetName()+"\t"+reference);
				
				rowNum=1;
				
				while (true) {
					
					Row row=sheet.getRow(rowNum);
					
					if(row.getCell(2)!=null) {
						Cell cell=row.getCell(2);
						if(cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Median log Koc")) {
							break;
						}
					}

					
					RecordMontgomery rec=new RecordMontgomery();
					rec.chemicalName=sheet.getSheetName();
					rec.casrn=htNameToCAS.get(rec.chemicalName);
					
					if(rec.casrn==null) {
						System.out.println("Null cas for "+rec.chemicalName);
					}
					
					
					//TODO get CAS from main tab
					
					if(row.getCell(0)!=null)
						rec.soil=row.getCell(0).getStringCellValue();
					
					rec.Kd=getNumericValue(row, 1);
					rec.foc=getNumericValue(row, 2);
					rec.Koc=getNumericValue(row, 3);
					
					
					
					rec.pH=getNumericValue(row, 4);
					if(rec.pH!=null && rec.pH==0) rec.pH=null;
					
					rec.reference=reference;
					rec.citation=citation;
					
//					if(rec.chemicalName.equals("prometryn")) {
						
//					}

					
					if(rec.Koc!=null && rec.Koc>0.0) {
						recs.add(rec);	
					}

					
					
					rowNum++;
					
				}
//				String formula="INDEX(INDIRECT(\"'"+sheet.getSheetName()+"'!D:D\"), MATCH(\"Reference\", INDIRECT(\"'\" & A13 & \"'!C:C\"), 0))";
//				System.out.println(formula);
				
				
			}
						
//			System.out.println(gson.toJson(recs));	
//			System.out.println("Number of chemicals="+chems.size()+"\nNumber of records="+recs.size());
						
//			XSSFWorkbook wb = new XSSFWorkbook(filePath); 


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recs;
		
	}


	private List<String> getCitations(List<String> citations, Sheet sheet, String reference) {
		
		List<String>refCitations=new ArrayList<>();

		
		if(reference!=null) {
			
			String [] references=reference.split(";");
			
			for (String ref:references) {
				ref=ref.trim();
				
				if(ref.equals("Bromilow et al., 1980")) {
					refCitations.add("Bromilow, R.H., R.J. Baker, M.A.H. Freeman, and K. Gorog. \"The Degradation of Aldicarb and Oxamyl in Soil,\" Pestic. Sci., 11(4):371-378 (1980).");
				} else if (ref.equals("Karickhoff et al., 1979")) {
					refCitations.add("Karickhoff, S.W., D.S. Brown, and T.A. Scott. \"Sorption of Hydrophobic Pollutants on Natural Sediments,\" Water Res., 13(3):241-248 (1979).");
				} else if (ref.equals("Kay and Elrick, 1967")) {
					refCitations.add("Kay, B.D. and D.E. Elrick. \"Adsorption and Movement of Lindane in Soils,\" Soil Sci., 104(5):314-322 (1967).");
				} else if (ref.equals("Reinert and Rodgers, 1984")) {
					refCitations.add("Reinert, K.H. and J.H. Rodgers. \"Fate and Persistence of Aquatic Herbicides,\" Rev. Environ. Contam. Toxicol., 98:61-98 (1987).");
				} else {
					String citation=getCitation(citations, sheet, ref);
					if(citation==null) {
						refCitations.add(ref);
					} else {
						refCitations.add(citation);
					}
//							System.out.println(sheet.getSheetName()+"\t"+ref+"\t"+author+"\t"+year);
				}
				
			}
				
			
		} else {
			System.out.println("Null reference for "+sheet.getSheetName());
		}
		
		return refCitations;
	}


	private String getCitation(List<String> citations, Sheet sheet, String ref) {

		List<String>selectedCitations=new ArrayList<>();
		String [] vals=ref.split(",");
		String author=vals[0].trim();
		String year=vals[1].trim();

		if (author.contains("and")) {

			String author1=author.substring(0,author.indexOf(" ")).trim();
			String author2=author.substring(author.indexOf(" and ")+5,author.length()).trim();
			//									System.out.println(sheet.getSheetName()+"\t"+ref+"\t"+author1+"\t"+author2+"\t"+year);

			int matchCount=0;

			for(String citation:citations) {

				String authors=citation.substring(0,citation.indexOf("."));

				if(citation.indexOf(author1)!=0) continue;
				if(!citation.contains(author2)) continue;

				if(!citation.contains(year)) 
					continue;
				else {
					String nextChar=citation.substring(citation.indexOf(year)+4,citation.indexOf(year)+5);

					if(nextChar.equals(")")) {//OK
					} else if(nextChar.equals("a") && !year.contains("a")) {
						continue;
					} else {
						System.out.println("Next char="+nextChar+"\t"+year+"\t"+citation);
					}
				}
				selectedCitations.add(citation);

				matchCount++;
				//										System.out.println(matchCount+"\t"+ref+"\t"+citation);
			}

			if(matchCount!=1) {
				System.out.println(matchCount+"\t"+ref+"\t"+author1+"\t"+author2+"\t"+year);
			}

			if(selectedCitations.size()>0) {
				return selectedCitations.get(0);	
			} else {
				return null;
			}

		} else  {

			if(author.contains("et al")) 
				author=author.substring(0,author.indexOf("et al")).trim();

			int matchCount=0;

			for(String citation:citations) {

				if(citation.indexOf(author)!=0) {
					continue;
				}

				if(!citation.contains(year)) { 
					continue;
				} else {

					String nextChar=citation.substring(citation.indexOf(year)+4,citation.indexOf(year)+5);

					if(nextChar.equals(")")) {//OK
					} else if(nextChar.equals("a") && !year.contains("a")) {
						continue;
					} else {
						System.out.println("Next char="+nextChar+"\t"+year+"\t"+citation);
					}
				}
				//										if(ref.equals("Colbert et al., 1975")) {
				//											System.out.println(citation);
				//										}
				selectedCitations.add(citation);
				matchCount++;
				//										System.out.println(matchCount+"\t"+ref+"\t"+citation);
			}

			if(matchCount!=1) {
				System.out.println(matchCount+"\t"+ref+"\t"+author+"\t"+year);
			}

			if(selectedCitations.size()>0) {
				return selectedCitations.get(0);	
			} else {
				return null;
			}

		} 


	}


	private String getReference(Sheet sheet) {
		int rowNum;
		rowNum=1;
		while (true) {
			Row row=sheet.getRow(rowNum);
			
			if(row==null) {
				System.out.println("Null row for "+sheet.getSheetName());
				break;
			}
			
			if(row.getCell(2)!=null) {
				Cell cell=row.getCell(2);
				if(cell.getCellType().equals(CellType.STRING) && cell.getStringCellValue().equals("Reference")) {
					cell=row.getCell(3);
					
					String reference=cell.getStringCellValue().trim();
					if(reference.substring(reference.length()-1,reference.length()).equals(".")) {
						reference=reference.substring(0,reference.length()-1);
					}
					return reference;
				}
			}
			rowNum++;
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		RecordMontgomery rm=new RecordMontgomery();
		rm.parseExcelFile();
//		rm.getCitations();
		
	}

	public ExperimentalRecord toExperimentalRecord() {
		
		ExperimentalRecord er=new ExperimentalRecord();
		er.casrn=casrn;
		er.chemical_name=chemicalName;
		er.property_name=ExperimentalConstants.strKOC;
		er.property_value_units_original=ExperimentalConstants.str_L_KG;
		er.property_value_point_estimate_original=Koc;
//		er.property_value_string = Koc+" "+ExperimentalConstants.str_L_KG;
		er.property_value_string = Koc+"";
		
		LiteratureSource ls=new LiteratureSource();
		ls.citation=citation;
		ls.name=reference;
		er.literatureSource=ls;
		
		PublicSource ps=new PublicSource();
		ps.name=this.sourceName;
		ps.description="J.H. Montgomery, \"Agrochemical Desk Reference: Environmental Data,\" Lewis Publishers, Chelsea, MI, 625 pp., 1993.";
		er.publicSource=ps;
		
		if(this.pH!=null)
			er.pH=this.pH+"";
		
		er.source_name=sourceName;
		
//		er.reference=reference;
//		er.document_name=citation;
		
		
		er.experimental_parameters=new TreeMap<>();
		
		if(this.foc!=null && this.foc>0)		
			er.experimental_parameters.put("Percentage_Organic_Carbon", this.foc);
		
		
		if(soil!=null && !soil.isBlank()) {
			er.experimental_parameters.put("Soil_Type", soil);
		}
				
		uc.convertRecord(er);
		
		
//		System.out.println(gson.toJson(er));
		
		
		return er;
	}
}

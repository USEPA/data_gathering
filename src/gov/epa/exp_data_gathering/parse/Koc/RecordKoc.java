package gov.epa.exp_data_gathering.parse.Koc;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.QSAR.utilities.Inchi;
import gov.epa.QSAR.utilities.IndigoUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import hazard.StructureImageUtil;

/**
* @author TMARTI02
*/
public class RecordKoc {
	
	public static final String sourceName="Koc List of Publications";
	static String filename="Koc List of Publications 2026_01_09.xlsx";
	
	transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");

	String sourceChemicalName;
	String sourceChemicalAbbreviation;
	String sourceDtxsid;
	String sourceSmiles;
	String sourceCASRN;
	String sourceFormula;
	
	Double logKoc;
	Boolean omit;//duplicate records from the same primary source have omit=true
	
	LiteratureSource secondarySource;
	LiteratureSource primarySource;
	
	String soilType;
	String soilPercentOrganicCarbon;
	String pH;
	
	String note;
	String noteIdentifier;
	
	Hashtable<String, LiteratureSource>htCitations=null;

	Hashtable<String, LiteratureSource> getCitations(Workbook wb, FormulaEvaluator evaluator) {
		
		
		Hashtable<String,LiteratureSource>htLit=new Hashtable<>();
		
		
		Sheet sheet=wb.getSheet("Sources EndNote");
		int headerRowNum=0;
		List<String>headers=ExcelSourceReader.getHeaders2(sheet, headerRowNum);
		
		Row rowHeader=sheet.getRow(headerRowNum);
		
		for(int rowNum=1;rowNum<=98;rowNum++) {
			
			Row row=sheet.getRow(rowNum);
			
			LiteratureSource ls=new LiteratureSource();
			ls.name=(String)getCellValue(headers, row, "Label",evaluator);
			ls.citation=(String)getCellValue(headers, row, "Citation",evaluator);
			
			ls.doi=(String)getCellValue(headers, row, "DOI",evaluator);
			if(ls.doi!=null && !ls.doi.contains("http")) {
				ls.doi="https://doi.org/"+ls.doi;
			}
			htLit.put(ls.name,ls);
		}
		
		return htLit;
		
		
		
	}
	
	public List<RecordKoc> parseExcelFile() {
		
		List<RecordKoc>recs=new ArrayList<>();
		
		try {

			String filePath="data\\experimental\\"+sourceName+"\\excel files\\"+filename;
			
			System.out.println(filePath);
			
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			evaluator.setIgnoreMissingWorkbooks(true);

			
//			List<String>allCitations=getAllCitations();
			
			String startSheetName="Bahnick 1988";
			String endSheetName="Yaws 1999";
			
			htCitations=getCitations(wb,evaluator);
			
			
//			System.out.println(htCR_Name.size()+"\t"+htCR_CAS.size());
			
			for(int sheetIndex=wb.getSheetIndex(startSheetName);sheetIndex<=wb.getSheetIndex(endSheetName);sheetIndex++) {
				
				Sheet sheet=wb.getSheetAt(sheetIndex);
				
//				System.out.println(sheet.getSheetName());
//				if(!sheet.getSheetName().equals("Bahnick 1988")) continue;
//				if(!sheet.getSheetName().equals("Wang 2015")) continue;//has smiles
				
				int headerRowNum=getHeaderRow(sheet);
				List<String>headers=ExcelSourceReader.getHeaders2(sheet, headerRowNum);
				
//				System.out.println(headers);
				
				
				int colName=headers.indexOf("Source Chemical Name");
				int colLogKoc=headers.indexOf("log Koc");
				
				if(colLogKoc<colName) {
					System.out.println("log Koc column is less than name column:"+sheet.getSheetName());
				}
				
//				Source Chemical Name, Source CASRN, log Koc
//				System.out.println(sheet.getSheetName()+"\t"+headers);
				
				int rowNum=headerRowNum+1;
				
				while (true) {
					

					Row row=sheet.getRow(rowNum++);
					if(row==null)break;
					
					if (ExcelSourceReader.isRowBlank(row)) break;

					
					RecordKoc rec=new RecordKoc();

					Double omit=(Double)getCellValue(headers, row, "Omit",evaluator);

					if(omit!=null) {
						if(omit==1)rec.omit=true;
						else rec.omit=false;
					} else {
						rec.omit=false;
					}
					
					rec.sourceChemicalName=(String)getCellValue(headers, row, "Source Chemical Name",evaluator);
					rec.sourceChemicalAbbreviation=(String)getCellValue(headers, row, "Source Chemical Abbreviation",evaluator);
					rec.sourceSmiles=(String)getCellValue(headers, row, "Source Smiles",evaluator);
					rec.sourceCASRN=(String)getCellValue(headers, row, "Source CASRN",evaluator);
					rec.sourceFormula=(String)getCellValue(headers, row, "Source Formula",evaluator);
					
					
//					System.out.println(gson.toJson(rec));
					
					
					String secondarySourceName=(String)getCellValue(headers, row, "Secondary Source",evaluator);
					if(secondarySourceName!=null) {
						if(htCitations.containsKey(secondarySourceName)) {
							rec.secondarySource=htCitations.get(secondarySourceName);	
						} else if(!rec.omit) {
							System.out.println("Missing secondary source entry for "+secondarySourceName);
						}
						
					}
					
					String primarySourceName=(String)getCellValue(headers, row, "Primary source",evaluator);
					
					if(primarySourceName!=null) {
						if(htCitations.containsKey(primarySourceName)) {
							rec.primarySource=htCitations.get(primarySourceName);	
						} else if(!rec.omit) {
							System.out.println("Missing primary source entry for "+primarySourceName+"\n"+gson.toJson(rec));
						}
					}
					
//					getCuratedIdentifiers(rec,sheet.getSheetName());
					
					rec.note=(String)getCellValue(headers, row, "Note",evaluator);;
					
					Object soilType=getCellValue(headers, row, "exp_param_Soil_Type",evaluator);					
					if(soilType!=null) rec.soilType=soilType+"";
					
					Object soilPercentOrganicCarbon=getCellValue(headers, row, "exp_param_% organic carbon",evaluator);
					if(soilPercentOrganicCarbon!=null) rec.soilPercentOrganicCarbon=soilPercentOrganicCarbon+"";
					
					Object pH=getCellValue(headers, row, "pH",evaluator);
					if(pH!=null)rec.pH=pH+"";
					
					
//					if(rec.omit)continue;
					
					Object logKoc=getCellValue(headers, row, "log Koc",evaluator);
					
					if(logKoc!=null) {
						String typeKoc=logKoc.getClass().getName();

						if(typeKoc.equals("java.lang.Double")) {
							rec.logKoc=(Double)logKoc;
						} else {
							System.out.println(logKoc+"\t"+typeKoc);
						}
					} else {
						System.out.println("Missing LogKoc:"+sheet.getSheetName()+"\t"+gson.toJson(rec));
					}
					
					recs.add(rec);
//					System.out.println(gson.toJson(rec));	
					
					
//					System.out.println(rowNum);
					
					
				}
				
				
//				if(headers.contains("Source Chemical Name")) {
//					
//				}
				
				
			}
			
			 
//			System.out.println(gson.toJson(recs));	
			System.out.println("\nNumber of records="+recs.size());


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recs;
		
	}






	private String getAllIds(RecordKoc rec,String delimiter) {
		List<String>ids=new ArrayList<>();
		if(rec.sourceSmiles!=null)ids.add(rec.sourceSmiles);
		else ids.add("");
		if(rec.sourceFormula!=null)ids.add(rec.sourceFormula);
		else ids.add("");
		if(rec.sourceChemicalAbbreviation!=null)ids.add(rec.sourceChemicalAbbreviation);
		else ids.add("");
		if(rec.sourceChemicalName!=null)ids.add(rec.sourceChemicalName);
		else ids.add("");
		if(rec.sourceCASRN!=null)ids.add(rec.sourceCASRN);
		else ids.add("");

		String allIds = String.join(delimiter, ids);
		return allIds;
	}


	private Object getCellValue(List<String> headers, Row row, String colName, FormulaEvaluator evaluator) {

		if(!headers.contains(colName)) return null;
		Cell cell=row.getCell(headers.indexOf(colName));
		if(cell==null) return null;


		//		CellReference cellRef = new CellReference(cell); // You can also use new CellReference(row.getRowNum(), cell.getColumnIndex());
		//        String address = cellRef.formatAsString();
		//        System.out.println(address);


		if(cell.getCellType()==CellType.STRING) {

			if(cell.getStringCellValue().isBlank()) return null;
			else
				return cell.getStringCellValue();

		} else if(cell.getCellType()==CellType.FORMULA){

			try {
				CellType cachedResultType = cell.getCachedFormulaResultType();
				switch (cachedResultType) {
				case NUMERIC:
					return cell.getNumericCellValue();
				case STRING:
					if(cell.getStringCellValue().isBlank()) return null;					
					else return cell.getStringCellValue();
				case BOOLEAN:
					return cell.getBooleanCellValue();
				case ERROR:
					return cell.getErrorCellValue();

				default:
					break;
				}
			} catch (IllegalStateException e) {
				// This can happen if the cached value is not available or invalid
				// Proceed to evaluate the formula
			}


			// Option 2: Evaluate the formula
			CellValue cellValue = evaluator.evaluate(cell);
			switch (cellValue.getCellType()) {
			case NUMERIC:
				return cellValue.getNumberValue();
			case STRING:
				if(cellValue.getStringValue().isBlank()) return null;					
				else return cellValue.getStringValue();
			case BOOLEAN:
				return cellValue.getBooleanValue();
			case ERROR:
				return cellValue.getErrorValue();
			default:
				return null; // Handle other types if necessary
			}



		} else if(cell.getCellType()==CellType.NUMERIC) {
			return cell.getNumericCellValue();
		
		} else if(cell.getCellType()==CellType.BLANK) {
			return null;
			
		} else {
			System.out.println("Handle cell.getCellType()="+cell.getCellType());
		}

		return null;

	}


	
	
	
	int getHeaderRow(Sheet sheet) {
		
		for (int rowNum=0;rowNum<sheet.getPhysicalNumberOfRows();rowNum++) {
			
			Row row=sheet.getRow(rowNum);
			
			if(row==null)continue;
			
			for (int colNum=0;colNum<row.getLastCellNum();colNum++) {
				Cell cell=row.getCell(colNum);
				if(cell==null)continue;
				
				if(cell.getCellType()==CellType.STRING) {
					if(cell.getStringCellValue().equals("Source Chemical Name")) {
						return rowNum;
					}
				}
				
				
			}
		}
		return -1;
	}
	

	public static void main(String[] args) {
		RecordKoc rm=new RecordKoc();
		rm.parseExcelFile();

	}


	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
		
		er.property_name=ExperimentalConstants.strKOC;
		
		er.chemical_name=this.sourceChemicalName;
		er.synonyms=this.sourceChemicalAbbreviation;
		er.casrn=this.sourceCASRN;
		er.dsstox_substance_id=this.sourceDtxsid;
		er.formula=this.sourceFormula;
		er.smiles=this.sourceSmiles;
		
//		er.source_name=RecordKoc.sourceName;
				
		er.experimental_parameters=new Hashtable<>();
		
		
		er.parameter_values=new ArrayList<>();
		
		if(soilType!=null) {			
			ParameterValue pv=new ParameterValue();
			pv.parameter.name="Soil_Type";
			pv.value_text=soilType;
			pv.unit.name="TEXT";
			pv.unit.abbreviation="Text";//not needed already in database			
			er.parameter_values.add(pv);
		}
		

		if(soilPercentOrganicCarbon!=null) {
			ParameterValue pv=new ParameterValue();
			pv.parameter.name="Percentage_Organic_Carbon";
			pv.unit.name="DIMENSIONLESS";
			pv.unit.abbreviation="Dimensionless";//not needed already in database

			try {
				
				if (soilPercentOrganicCarbon.contains("-")) {
					String []vals=soilPercentOrganicCarbon.split("-");
					pv.value_min=Double.parseDouble(vals[0]);
					pv.value_max=Double.parseDouble(vals[1]);
					
				} else {
					pv.value_point_estimate=Double.parseDouble(soilPercentOrganicCarbon);	
				}
				
				er.parameter_values.add(pv);
				
			} catch (Exception ex) {
				System.out.println("Error parsing OC%:"+soilPercentOrganicCarbon);
			}
			
			
		}
		
		if(pH!=null) {
			ParameterValue pv=new ParameterValue();
			pv.parameter.name="pH";
			pv.unit.name="LOG_UNITS";
			pv.unit.abbreviation="Log units";//not needed already in database

			try {
				
				if (pH.contains("-")) {
					String []vals=pH.split("-");
					pv.value_min=Double.parseDouble(vals[0]);
					pv.value_max=Double.parseDouble(vals[1]);
					
				} else {
					pv.value_point_estimate=Double.parseDouble(pH);	
				}
				
				er.parameter_values.add(pv);
				
			} catch (Exception ex) {
				System.out.println("Error parsing pH%:"+pH);
			}
		}

		
		
//		if(soilType!=null)
//			System.out.println("soilType: "+soilType);
					
		
		if(this.secondarySource!=null) {
			PublicSource ps=new PublicSource();
			er.publicSource=ps;
			ps.name=secondarySource.name;
			ps.description=secondarySource.citation;
			ps.url=secondarySource.doi;
		}
		
		if(this.primarySource!=null) {
			er.literatureSource=primarySource;
		}
		
		er.property_value_units_original=ExperimentalConstants.str_LOG_L_KG;
		er.property_value_point_estimate_original=this.logKoc;
		er.property_value_string=this.logKoc+"";
		uc.convertRecord(er);
		
		if(this.omit) {
			er.keep=false;
			er.reason="Duplicate record from same primary source";
		}
		
//		System.out.println(gson.toJson(er));
		
		return er;
		
	}

}

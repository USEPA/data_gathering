package gov.epa.exp_data_gathering.parse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import org.apache.poi.ss.usermodel.*;

//import gov.epa.exp_data_gathering.parse.Koc.RecordKoc;

/**
 * @author TMARTI02
 */
public class DsstoxMapperFromChemRegExcelExport {

	class RecordChemReg {
		public String Query;
		public String Found_By;
		public String DSSTox_Substance_Id;
		public String DSSTox_Structure_Id;
		public String DSSTox_QC_Level;
		public String Substance_Name;
		public String Substance_CASRN;
		public String Substance_Type;
		public String Substance_Note;
		public String Structure_SMILES;
		public String Structure_InChI;
		public String Structure_InChIKey;
		public String Structure_Formula;
		public Double Structure_MolWt;
		public String Structure_SMILES_2D_QSAR;
		public String DateModified;
		
		
		
	}

	class RecordChemReg2 {
		public String Source_Smiles;
		public String Source_Formula;
		public String Source_Chemical_Abbreviation;
		public String Source_Chemical_Name;
		public String Source_CASRN;
		public String Source_Name_Fixed;
		public String Source_All_identifiers;
		public String Note;
		public String DTXSID_Curators;
		public String Note_Curators;
		
//		boolean used=false;
	}

	Hashtable<String, RecordChemReg> htCR_Name = null;
	Hashtable<String, RecordChemReg> htCR_CAS = null;
	Hashtable<String, RecordChemReg2> htCR_Curated = null;

	HashSet<String> missingChemregNames = new HashSet();
	HashSet<String> missingChemregCASRNs = new HashSet();
	
	List<String> validNameMappings = Arrays.asList("Preferred Name", "Unique Synonym", "Mapped Identifier",
			"Valid Synonym","Name2Structure");


	public DsstoxMapperFromChemRegExcelExport(String filepathExcelChemReg) {

		try {

			FileInputStream fis = new FileInputStream(new File(filepathExcelChemReg));
			Workbook wb = WorkbookFactory.create(fis);

			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
			evaluator.setIgnoreMissingWorkbooks(true);

			htCR_Curated = getChemregUniqueIdentifierHashtable(wb.getSheet("Unique identifiers Curated"), evaluator);
			
			htCR_Name = getChemregHashtable(wb.getSheet("ChemReg by Name"), evaluator);
			htCR_CAS = getChemregHashtable(wb.getSheet("ChemReg by CAS"), evaluator);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getAllIds(ExperimentalRecord rec, String delimiter) {
		
		List<String> ids = new ArrayList<>();
		
		if (rec.smiles != null)	ids.add(rec.smiles);
		else ids.add("");

		if (rec.formula != null)	ids.add(rec.formula);
		else ids.add("");// formula

		if (rec.synonyms != null) ids.add(rec.synonyms);
		else ids.add("");

		if (rec.chemical_name != null)	ids.add(rec.chemical_name);
		else ids.add("");

		if (rec.casrn != null) ids.add(rec.casrn);
		else ids.add("");

		String allIds = String.join(delimiter, ids);
		return allIds;
	}
	
	
	public void saveMissingChemregToTextFiles(String sourceName) {
		
		String folder="data\\experimental\\"+sourceName+"\\";
		
		try (FileWriter fw=new FileWriter(folder+"names missing in spreadsheet.txt",Charset.forName("UTF-8"))){
//			System.out.println("\nMissing chemReg names:");
			for(String name:missingChemregNames) {
//				System.out.println(name);
				fw.write(name+"\r\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		try (FileWriter fw=new FileWriter(folder+"casrns missing in spreadsheet.txt",Charset.forName("UTF-8"))){
//			System.out.println("\nMissing chemReg names:");
			for(String casrn:missingChemregCASRNs) {
//				System.out.println(name);
				fw.write(casrn+"\r\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	

	public void printMissingChemreg() {
		
		System.out.println("\nMissing chemReg names:");
		for(String name:missingChemregNames) {
			System.out.println(name);
		}
		
		System.out.println("\nMissing chemReg names:");
		for(String casrn:missingChemregCASRNs) {
			System.out.println(casrn);
		} 
	}

	
	private boolean hasBadName(ExperimentalRecord er) {
				
		
		List<String> badNames = Arrays.asList("C16", "C12", "poly", "salt", "polymer", "nitrates", "alcohols",
				"compounds", "similar", "terpenes", "reaction", "alkenes", "product", "esters", "generated", "branched",
				"concentration", "copper", "available", "plastic", "ions", "acids", "potassium", "sodium", "donor",
				"unnamed", "not applicable", "not reported", "sample");
		
		
		String nameLC = er.chemical_name.toLowerCase();
		for (String badName : badNames) {

			if (nameLC.contains(badName)) {
//					System.out.println("Has bad name: "+nameLC);
				return true;
			}
		}
		
		return false;


	}
	
	
	private RecordChemReg getChemRegRecordForName(ExperimentalRecord er) {

		if (er.chemical_name == null) return null;
		if (hasBadName(er)) return null;;
		
		if (!htCR_Name.containsKey(er.chemical_name.toLowerCase())) {
			if(er.keep) {
				missingChemregNames.add(er.chemical_name.toLowerCase());	
			}
			return null;
		}		
		
		RecordChemReg recCR = htCR_Name.get(er.chemical_name.toLowerCase());
		if(recCR.Found_By.equals("Not Found")) return null;
		
		return recCR;
	
	}
	
	
	private RecordChemReg getChemRegRecordForCAS(ExperimentalRecord er) {
		
		if(er.casrn==null) return null;
		
		if (!htCR_CAS.containsKey(er.casrn)) {
			if(er.keep) {
				missingChemregCASRNs.add(er.casrn);	
			}
			return null;
		}		

		RecordChemReg recCR = htCR_CAS.get(er.casrn);
		if(recCR.Found_By.equals("Not Found")) return null;
		else return recCR;

	}
	

	public void getDtxsid(ExperimentalRecord er) {
		
		if (er.dsstox_substance_id!=null) {
//			System.out.println("Dtxsid already set:"+er.dsstox_substance_id);
			return;//already set
		}
		
		RecordChemReg recCR_name=getChemRegRecordForName(er);
		RecordChemReg recCR_casrn=getChemRegRecordForCAS(er);

		
		
		if (recCR_name != null && recCR_casrn != null) {
			if(er.casrn.equals("67-72-1")) System.out.println("Found 67-72-1 in name+cas");
			handleCAS_and_name_match(er, recCR_name, recCR_casrn);
		} else if (recCR_casrn != null) {
			if(er.casrn.equals("67-72-1")) System.out.println("Found 67-72-1 in cas");
			er.dsstox_substance_id=recCR_casrn.DSSTox_Substance_Id;
//			System.out.println("CAS match "+er.casrn+" by "+recCR_casrn.Found_By);			
		} else if (recCR_name != null) {
			handleNameMatch(er, recCR_name);
		} else {
			if (er.keep) {
//				System.out.println("no dtxsid match:" + er.chemical_name+"\t"+er.casrn);
			}
		}

	}

	private void handleNameMatch(ExperimentalRecord er, RecordChemReg recCR_name) {
		boolean valid=false;
		for (String validMapping : this.validNameMappings) {
			if (recCR_name.Found_By.contains(validMapping)) {
				
//				if(!recCR_name.Found_By.contains("Preferred Name") && !recCR_name.Found_By.contains("Valid Synonym") && !recCR_name.Found_By.contains("Unique Synonym"))
//					System.out.println(er.chemical_name+"\t"+recCR_name.Found_By);
				
				valid=true;
			}
		}
		
		if(!valid) {
			System.out.println("Invalid name match:"+recCR_name.Found_By+":"+er.chemical_name);
		} else {
			er.dsstox_substance_id=recCR_name.DSSTox_Substance_Id;
//				System.out.println("Good name match "+er.chemical_name+" by "+recCR_name.Found_By);
		}
	}

	private void handleCAS_and_name_match(ExperimentalRecord er, RecordChemReg recCR_name, RecordChemReg recCR_casrn) {
		String dtxsidName=recCR_name.DSSTox_Substance_Id;
		String dtxsidCAS=recCR_casrn.DSSTox_Substance_Id;
		
//		System.out.println(dtxsidName+"\t"+dtxsidCAS);
		
		boolean printMismatch=false;
		
		if (!dtxsidName.equals(dtxsidCAS)) {
			
			if (recCR_name.Structure_InChIKey == null || recCR_casrn.Structure_InChIKey == null) {
					
				if(printMismatch && er.keep)
					System.out.println("Has a null inchi:" + er.chemical_name + "\t" + er.casrn + "\t" + dtxsidName
								+ "\t" + dtxsidCAS);
				return;
			}
			
			String inchiKeyName=recCR_name.Structure_InChIKey.substring(0,recCR_name.Structure_InChIKey.indexOf("-"));
			String inchiKeyCAS=recCR_casrn.Structure_InChIKey.substring(0,recCR_casrn.Structure_InChIKey.indexOf("-"));

			if (inchiKeyCAS != inchiKeyName) {
				if(printMismatch  && er.keep)
					System.out.println("Mismatch:" + er.chemical_name + "\t" + er.casrn + "\t" + dtxsidName
							+ "\t" + dtxsidCAS+"\t"+inchiKeyCAS+"\t"+inchiKeyName);
				
//				if(er.casrn.equals("67-72-1")) {
//					System.out.println("Mismatch:" + er.chemical_name + "\t" + er.casrn + "\t" + dtxsidName
//							+ "\t" + dtxsidCAS+"\t"+inchiKeyCAS+"\t"+inchiKeyName);
//				}

			} else {
				if(printMismatch && er.keep)
					System.out.println("Different SID but same 2d inchiKey:" + er.chemical_name + "\t" + er.casrn + "\t"
						+ dtxsidName + "\t" + dtxsidCAS);
				if(er.casrn.equals("3424-82-6")) System.out.println("here 4 Found 3424-82-6 in cas+name");

			}
			

		} else {
			er.dsstox_substance_id=dtxsidCAS;
		}
	}

	public void getCuratedIdentifiers(ExperimentalRecord er) {

		String allIds = getAllIds(er, "|");

//			System.out.println(allIds);

		if (htCR_Curated.containsKey(allIds.toLowerCase())) {
			RecordChemReg2 recCR2 = htCR_Curated.get(allIds.toLowerCase());
						
			if (recCR2.DTXSID_Curators != null) {
//				System.out.println("*Curated dtxsid:"+recCR2.DTXSID_Curators+"\tfrom: "+allIds);
				er.dsstox_substance_id = recCR2.DTXSID_Curators;
				er.updateNote("Manually dtxsid");
			}

			if (recCR2.Source_Name_Fixed != null) {
//					System.out.println("*Name changed from "+recordKoc.sourceChemicalName+" to "+recCR2.Source_Name_Fixed+"\t"+sheetName);
				er.updateNote("Name changed from " + er.chemical_name);
				er.chemical_name = recCR2.Source_Name_Fixed;
			}
		} else {
			
			if (er.keep) {
//				System.out.println("Didnt find in curated tab:"+allIds);	
			}
			
			
		}
	}

	private Hashtable<String, RecordChemReg2> getChemregUniqueIdentifierHashtable(Sheet sheet, FormulaEvaluator evaluator) {

		Hashtable<String, RecordChemReg2> ht = new Hashtable<>();
		int headerRowNum = 2;
		List<String> headers = ExcelSourceReader.getHeaders2(sheet, headerRowNum);

		int rowNum = headerRowNum + 1;

		while (true) {

			Row row = sheet.getRow(rowNum++);
			if (row == null)
				break;
			if (ExcelSourceReader.isRowBlank(row))
				break;

			RecordChemReg2 rec = new RecordChemReg2();

			for (String header : headers) {

				Object val = getCellValue(headers, row, header, evaluator);

				try {
					Field field = rec.getClass().getField(header.replace(" ", "_"));

					if (val != null) {
						if (val.getClass().getName().contains("Double")) {
							field.set(rec, val);
						} else if (val.getClass().getName().contains("String")) {
							field.set(rec, val);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (rec.Source_All_identifiers == null)
				break;
			ht.put(rec.Source_All_identifiers.toLowerCase(), rec);
		}
//		System.out.println(gson.toJson(ht));
		return ht;
	}

	private Object getCellValue(List<String> headers, Row row, String colName, FormulaEvaluator evaluator) {

		if (!headers.contains(colName))
			return null;
		Cell cell = row.getCell(headers.indexOf(colName));
		if (cell == null)
			return null;

		// CellReference cellRef = new CellReference(cell); // You can also use new
		// CellReference(row.getRowNum(), cell.getColumnIndex());
		// String address = cellRef.formatAsString();
		// System.out.println(address);

		if (cell.getCellType() == CellType.STRING) {

			if (cell.getStringCellValue().isBlank())
				return null;
			else
				return cell.getStringCellValue();

		} else if (cell.getCellType() == CellType.FORMULA) {

			try {
				CellType cachedResultType = cell.getCachedFormulaResultType();
				switch (cachedResultType) {
				case NUMERIC:
					return cell.getNumericCellValue();
				case STRING:
					if (cell.getStringCellValue().isBlank())
						return null;
					else
						return cell.getStringCellValue();
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
				if (cellValue.getStringValue().isBlank())
					return null;
				else
					return cellValue.getStringValue();
			case BOOLEAN:
				return cellValue.getBooleanValue();
			case ERROR:
				return cellValue.getErrorValue();
			default:
				return null; // Handle other types if necessary
			}

		} else if (cell.getCellType() == CellType.NUMERIC) {
			return cell.getNumericCellValue();

		} else if (cell.getCellType() == CellType.BLANK) {
			return null;

		} else {
			System.out.println("Handle cell.getCellType()=" + cell.getCellType());
		}

		return null;

	}

	private Hashtable<String, RecordChemReg> getChemregHashtable(Sheet sheet, FormulaEvaluator evaluator) {

		Hashtable<String, RecordChemReg> ht = new Hashtable<>();
		int headerRowNum = 0;
		List<String> headers = ExcelSourceReader.getHeaders2(sheet, headerRowNum);

		int rowNum = headerRowNum + 1;

		while (true) {

			Row row = sheet.getRow(rowNum++);
			if (row == null)
				break;
			if (ExcelSourceReader.isRowBlank(row))
				break;

			RecordChemReg rec = new RecordChemReg();

			for (String header : headers) {

				Object val = getCellValue(headers, row, header, evaluator);

				try {
					Field field = rec.getClass().getField(header.replace("-", "_"));

					if (val != null) {
						if (val.getClass().getName().contains("Double")) {
							field.set(rec, val);
						} else if (val.getClass().getName().contains("String")) {
							
							if(val!=null && header.equals("Found_By")) {
								String strVal= ((String)val).replace("<b>null</b>","").trim();
								field.set(rec,strVal);
							} else {
								field.set(rec, val);	
							}
							
							
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

//				System.out.println(header+"\t"+val);

			}

			ht.put(rec.Query.toLowerCase(), rec);
		}
//		System.out.println(gson.toJson(ht));
		return ht;
	}

}

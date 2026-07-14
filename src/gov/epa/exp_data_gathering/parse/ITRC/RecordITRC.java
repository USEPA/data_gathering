package gov.epa.exp_data_gathering.parse.ITRC;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
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
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.ITRC.RecordITRC.RecordBio.Species;

/**
* @author TMARTI02
*/
public class RecordITRC {

	String PFAS_Name;
	String Acronym;
	String Isomer;
	// String LogKocWithStdDev;
	String Type;
	String Applicable_Matrices;
	String Testing_Conditions;
	String Reference_Label;
	String Reference_Citation;
	int Reference_Number;
	String CAS;
	
	// Which of the above fields should be placed within the RecordsKOC/Bio objects?
	// Information such as the reference, and study conditions info can vary across records
	// for the same chemical, so might be better placed in the sub-records objects?
	// Seems like I need to also add info such as the reference label/citation/number to the
	// sub-records, as individual chemicals may have data from many sources

	List<RecordKOC> RecordsKOC;
	List<RecordBio> RecordsBio;
	
	public static final String sourceName="ITRC July 2023";
	static String filename="PhysChemProp_Table_July2023-FINAL.xlsx";
	// static String filename="ITRC_PFAS_-BCF-BAF_compilation_Table5-1_Oct2021.xlsx"
	
	transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");
	
	static class RecordKOC {
		String LogKocWithStdDev;
	}

	static class RecordBio {
		String BCF_Min;
		String BCF_Max;
		String BCF;
		String BAF_Min;
		String BAF_Max;
		String BAF;
		String Organism_Common_Name;
		String Organism_Scientific_Name;
		String Tissue_Type;
		String Wet_Dry_Lipid_Basis;
		String Lab_Field_Model_Study;
		String Location;
		String Freshwater_Marine_Estuary;
		String Waterbody_Description;
		String Reviewer_Notes;
		String Reference_Label;
		String Reference_Citation;
		String Reference_Url;
		// String Reference_Doi;
		String Reference_Author;
		String Reference_Journal;
		String Reference_Title;
		String Reference_Year;

		static class Species {
			Integer id;
			String species_common;
			String species_scientific;
			String species_supercategory;
			String habitat;
		}

		private String getSpeciesSupercategory(Hashtable<String, List<Species>> htSpecies) {
			Hashtable<String, Species> htSpeciesScientific = getSpeciesHashtableScientific(htSpecies);

			// Catch edge cases where multiple species were used, but all belong to the same class of organism
			if (Organism_Common_Name != null && Organism_Common_Name.equalsIgnoreCase("Hemigrapsus sanguineus, Sesarma pictum, Hemigrapsus penicillatus, Helice tridens tridens, and Philyra pisum")) {
				return "Fish";
			} else if (Organism_Common_Name != null && Organism_Common_Name.equalsIgnoreCase("Largemouth  and smallmouth bass")) {
				return "Fish";
			} else if (Organism_Common_Name != null && Organism_Common_Name.equalsIgnoreCase("Several species")) {
				return "Fish";
			} else if (Organism_Common_Name != null && Organism_Common_Name.equalsIgnoreCase("Turtles")) {
				return "Reptiles";
			}

			if(Organism_Common_Name!=null && htSpecies.containsKey(Organism_Common_Name.toLowerCase())) {
				
					List<Species>speciesList=htSpecies.get(Organism_Common_Name.toLowerCase());

					for(Species species:speciesList) {
						if(species.species_supercategory.contains("fish")) {
							return "Fish";
						} else if(species.species_supercategory.contains("algae")) {
							return "Algae";
						} else if(species.species_supercategory.contains("crustaceans")) {
							return "Crustaceans";
						} else if(species.species_supercategory.contains("insects/spiders")) {
							return "Insects/spiders";
						} else if(species.species_supercategory.contains("molluscs")) {
							return "Molluscs";
						} else if(species.species_supercategory.contains("worms")) {
							return "Worms";
						} else if(species.species_supercategory.contains("invertebrates")) {
							return "Invertebrates";
						} else if(species.species_supercategory.contains("flowers, trees, shrubs, ferns")) {
							return "Flowers, trees, shrubs, ferns";
						} else if(species.species_supercategory.contains("microorganisms")) {
							return "Microorganisms";
						} else if(species.species_supercategory.contains("amphibians")) {
							return "Amphibians";
						} else if(species.species_supercategory.equals("omit")) {
							return "Omit";
						}
					}
				} else if(htSpecies.containsKey(Organism_Scientific_Name.toLowerCase())) {

					List<Species>speciesList=htSpecies.get(Organism_Scientific_Name.toLowerCase());

					for(Species species:speciesList) {
						if(species.species_supercategory.contains("fish")) {
							return "Fish";
						} else if(species.species_supercategory.contains("algae")) {
							return "Algae";
						} else if(species.species_supercategory.contains("crustaceans")) {
							return "Crustaceans";
						} else if(species.species_supercategory.contains("insects/spiders")) {
							return "Insects/spiders";
						} else if(species.species_supercategory.contains("molluscs")) {
							return "Molluscs";
						} else if(species.species_supercategory.contains("worms")) {
							return "Worms";
						} else if(species.species_supercategory.contains("invertebrates")) {
							return "Invertebrates";
						} else if(species.species_supercategory.contains("flowers, trees, shrubs, ferns")) {
							return "Flowers, trees, shrubs, ferns";
						} else if(species.species_supercategory.contains("microorganisms")) {
							return "Microorganisms";
						} else if(species.species_supercategory.contains("amphibians")) {
							return "Amphibians";
						} else if(species.species_supercategory.equals("reptiles")) {
							return "Reptiles";
						} else if(species.species_supercategory.equals("omit")) {
							return "Omit";
						} else {
							System.out.println("Handle\t"+Organism_Scientific_Name+"\t"+species.species_supercategory);	
						}
					}
				} else if (Organism_Common_Name!=null && htSpeciesScientific.containsKey(Organism_Common_Name.toLowerCase())) {
					Species species = htSpeciesScientific.get(Organism_Common_Name.toLowerCase());
					if(species.species_supercategory.contains("fish")) {
						return "Fish";
					} else if(species.species_supercategory.contains("algae")) {
						return "Algae";
					} else if(species.species_supercategory.contains("crustaceans")) {
						return "Crustaceans";
					} else if(species.species_supercategory.contains("insects/spiders")) {
						return "Insects/spiders";
					} else if(species.species_supercategory.contains("molluscs")) {
						return "Molluscs";
					} else if(species.species_supercategory.contains("worms")) {
						return "Worms";
					} else if(species.species_supercategory.contains("invertebrates")) {
						return "Invertebrates";
					} else if(species.species_supercategory.contains("flowers, trees, shrubs, ferns")) {
						return "Flowers, trees, shrubs, ferns";
					} else if(species.species_supercategory.contains("microorganisms")) {
						return "Microorganisms";
					} else if(species.species_supercategory.contains("amphibians")) {
						return "Amphibians";
					} else if(species.species_supercategory.equals("reptiles")) {
						return "Reptiles";
					} else if(species.species_supercategory.equals("omit")) {
						return "Omit";
					} else {
						System.out.println("Handle\t"+Organism_Scientific_Name+"\t"+species.species_supercategory);	
					}
				} else if (htSpeciesScientific.containsKey(Organism_Scientific_Name.toLowerCase())) {
					Species species = htSpeciesScientific.get(Organism_Scientific_Name.toLowerCase());
					if(species.species_supercategory.contains("fish")) {
						return "Fish";
					} else if(species.species_supercategory.contains("algae")) {
						return "Algae";
					} else if(species.species_supercategory.contains("crustaceans")) {
						return "Crustaceans";
					} else if(species.species_supercategory.contains("insects/spiders")) {
						return "Insects/spiders";
					} else if(species.species_supercategory.contains("molluscs")) {
						return "Molluscs";
					} else if(species.species_supercategory.contains("worms")) {
						return "Worms";
					} else if(species.species_supercategory.contains("invertebrates")) {
						return "Invertebrates";
					} else if(species.species_supercategory.contains("flowers, trees, shrubs, ferns")) {
						return "Flowers, trees, shrubs, ferns";
					} else if(species.species_supercategory.contains("microorganisms")) {
						return "Microorganisms";
					} else if(species.species_supercategory.contains("amphibians")) {
						return "Amphibians";
					} else if(species.species_supercategory.equals("reptiles")) {
						return "Reptiles";
					} else if(species.species_supercategory.equals("omit")) {
						return "Omit";
					} else {
						System.out.println("Handle\t"+Organism_Scientific_Name+"\t"+species.species_supercategory);	
					}
				} else {
					System.out.println("missing in hashtable:\t"+"*"+Organism_Scientific_Name.toLowerCase()+"*");
				}
		return null;
		}

		/**
		 * this works for prod_dsstox- not v93 version since species table is different
		 * 
		 * @param tvq
		 * @return
		 */
		public static Hashtable<String, List<Species>> createSupercategoryHashtable(Connection conn) {
			Hashtable<String,List<Species>>htSpecies=new Hashtable<>();

			String sql="select species_id, species_common, species_scientific, species_supercategory, habitat from species";

			try {
				Statement st = conn.createStatement();			
				ResultSet rs = st.executeQuery(sql);

				while (rs.next()) {

					Species species=new Species();

					species.id=rs.getInt(1);
					species.species_common=rs.getString(2);
					species.species_scientific=rs.getString(3);
					species.species_supercategory=rs.getString(4);
					species.habitat=rs.getString(5);

					if(htSpecies.get(species.species_common)==null) {
						List<Species>speciesList=new ArrayList<>();
						speciesList.add(species);
						htSpecies.put(species.species_common, speciesList);
					} else {
						List<Species>speciesList=htSpecies.get(species.species_common);
						speciesList.add(species);
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return htSpecies;
		}

		// TODO: Implement properly to handle other species supercategories
		private Hashtable<String, Species> getSpeciesHashtableScientific(Hashtable<String, List<Species>> htSpeciesByCommonName) {
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
	}

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

	Hashtable<String, Integer> getCitationsBio(Sheet sheetReferences) {
		
		Hashtable<String,Integer>ht=new Hashtable<>();
		
		for (int rowNum=6;rowNum<=170;rowNum++) {
			Row row=sheetReferences.getRow(rowNum);
			// Integer citationNumber=(int) row.getCell(0).getNumericCellValue();
			String citationName = row.getCell(0).getStringCellValue();

			ht.put(citationName, rowNum);
			
//			if(citationTag.equals("Munoz, Budzinski, and Labadie, 2017")) System.out.println(richCitation);

			
//			System.out.println(citationTag);
		}
		return ht;
	}

	public List<RecordITRC> parseExcelFile(String filename) {
		
		List<RecordITRC>recs=new ArrayList<>();

		String folderPath = "data\\experimental\\"+sourceName+"\\excel files\\";
		
		try {

			String filePath=folderPath+filename;
			
			System.out.println(filePath);
			
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			
			FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

			HashSet<String>chems=new HashSet();
			
//			List<String>allCitations=getAllCitations();
			 
			Sheet sheetKoc=wb.getSheet("Log Koc");
			Sheet sheetReferences=wb.getSheet("References");

			if (sheetKoc == null) {
				System.out.println("Warning: sheet 'Log Koc' not found in " + filePath + " - skipping KOC parsing");
				wb.close();
				fis.close();
				return recs;
			}
            
			Hashtable<Integer,String>htCitations=new Hashtable<>();
			if (sheetReferences != null) {
				htCitations=getCitations(sheetReferences);
			} else {
				System.out.println("Warning: sheet 'References' not found in " + filePath + " - citation lookups disabled");
			}
			
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
				
				rec.Isomer=row.getCell(2).getStringCellValue();
				rec.Type=row.getCell(4).getStringCellValue();
				rec.Applicable_Matrices=row.getCell(5).getStringCellValue();
				rec.Testing_Conditions=row.getCell(6).getStringCellValue();
				rec.Reference_Label=row.getCell(7).getRichStringCellValue().getString();
				
				CellValue cellValue = evaluator.evaluate(row.getCell(8)); 
				
				rec.Reference_Number=(int)cellValue.getNumberValue();
				
				rec.Reference_Citation=htCitations.get(rec.Reference_Number);
				
				rec.RecordsKOC = new ArrayList<>();

				RecordKOC recordKOC = new RecordKOC();
				recordKOC.LogKocWithStdDev = cellLogKoc.getStringCellValue();
				rec.RecordsKOC.add(recordKOC);
				
//				System.out.println(rowNum+"\t"+name+"\t"+cellLogKoc.getStringCellValue());
			
			}
			
//			System.out.println(gson.toJson(recs));	
			System.out.println("Number of chemicals="+chems.size()+"\nNumber of records="+recs.size());


		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recs;
		
	}

	public List<RecordITRC> parseExcelFileBio(String filename) {
		
		List<RecordITRC> recs = new ArrayList<>();
		String folderPath = "data\\experimental\\" + sourceName + "\\excel files\\";
		
		try {
			String filePath = folderPath + filename;
			System.out.println(filePath);
			
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			Sheet sheetBAF = wb.getSheet("BCF-BAF Database");
			if (sheetBAF == null) {
				System.out.println("Warning: sheet 'BCF-BAF Database' not found in " + filePath + " - skipping bio parsing");
				wb.close();
				fis.close();
				return recs;
			}

			// Build hashtable to map column headers
			Row headerRow = sheetBAF.getRow(6);
			if (headerRow == null) {
				System.out.println("Warning: header row missing in 'BCF-BAF Database' sheet - skipping bio parsing");
				wb.close();
				fis.close();
				return recs;
			}
			Hashtable<String, Integer> htCols = new Hashtable<>();
			for (int i = 0; i < headerRow.getLastCellNum(); i++) {
				String colName = headerRow.getCell(i).getStringCellValue();
				htCols.put(colName, i);
			}

			// Handle reference information
			Sheet sheetRef = wb.getSheet("BCF-BAF Log");
			if (sheetRef == null) {
				System.out.println("Warning: sheet 'BCF-BAF Log' not found in " + filePath + " - reference lookups disabled");
			}

			// Build hashtable to map column headers for references
			Row headerRowRefs = null;
			if (sheetRef != null) {
				headerRowRefs = sheetRef.getRow(5);
				if (headerRowRefs == null) {
					System.out.println("Warning: header row missing in 'BCF-BAF Log' sheet - reference lookups disabled");
					sheetRef = null;
				}
			}
			Hashtable<String, Integer> htColsRefs = new Hashtable<>();
			for (int i = 0; i < headerRowRefs.getLastCellNum(); i++) {
				String colName = headerRowRefs.getCell(i).getStringCellValue();
				htColsRefs.put(colName, i);
			}

			// TODO: Finish implementation of reference information, need to use ref number column
			Hashtable<String,Integer>htRefsRows=new Hashtable<>();
			if (sheetRef != null) {
				htRefsRows=getCitationsBio(sheetRef);
			}
			
			// Use HashMap to group RecordBio by unique PFAS_Name (chemical identifier)
			java.util.HashMap<String, RecordITRC> chemicalMap = new java.util.HashMap<>();
			
			// Parse data rows (rows 8 through 1411 as you specified, adjust to 0-based indexing = 7-1410)
			for (int rowNum = 7; rowNum <= 1410; rowNum++) {
				Row row = sheetBAF.getRow(rowNum);
				if (row == null) continue;
				
				// Extract chemical identifying information from row
				String pfasName = getCellStringValue(row, htCols.get("PFAS Name"));
				String acronym = getCellStringValue(row, htCols.get("Acronym"));
				
				// Skip rows without PFAS Name
				if (pfasName == null || pfasName.isBlank()) continue;
				
				// Get or create RecordITRC for this unique chemical
				RecordITRC rec = chemicalMap.get(pfasName);
				if (rec == null) {
					rec = new RecordITRC();
					rec.PFAS_Name = pfasName;
					rec.Acronym = acronym;
					String temp = getCellStringValue(row, htCols.get("CAS No."));
					rec.CAS = (!temp.equals("0")) ? temp : null;
					// Populate other chemical-level fields that don't vary by row
					// rec.Isomer = getCellStringValue(row, htCols.get("Isomer"));
					// rec.Type = getCellStringValue(row, htCols.get("Type"));
					// rec.Applicable_Matrices = getCellStringValue(row, htCols.get("Applicable Matrices"));
					// rec.Testing_Conditions = getCellStringValue(row, htCols.get("Testing Conditions"));
					rec.Reference_Label = getCellStringValue(row, htCols.get("Reference"));
					// TODO: Implement method for gathering reference info from BCF-BAF Log sheet
					// rec.Reference_Citation = getCellStringValue(row, htCols.get("Reference Citation"));
					rec.RecordsBio = new ArrayList<>();
					chemicalMap.put(pfasName, rec);
				}
				
				// Handle BCF/BAF Values
				RecordBio recordBio = new RecordBio();
				recordBio.BCF = getCellStringValue(row, htCols.get("BCF"));
				recordBio.BCF_Min = getCellStringValue(row, htCols.get("BCF Min"));
				recordBio.BCF_Max = getCellStringValue(row, htCols.get("BCF Max"));
				recordBio.BAF = getCellStringValue(row, htCols.get("BAF"));
				recordBio.BAF_Min = getCellStringValue(row, htCols.get("BAF Min"));
				recordBio.BAF_Max = getCellStringValue(row, htCols.get("BAF Max"));

				// Handle metadata
				if (rec.Reference_Label != null && !rec.Reference_Label.isEmpty()) {
					Row rowRef = sheetRef.getRow(htRefsRows.get(rec.Reference_Label));
					populateBioMetadata(recordBio, row, htCols, rowRef, htColsRefs);
				} else {
					populateBioMetadata(recordBio, row, htCols);
				}
				
				rec.RecordsBio.add(recordBio);
				
				// Create RecordBio for BCF if value exists
				// if (bcfVal != null && !bcfVal.isBlank()) {
				// 	RecordBio recordBio = new RecordBio();
				// 	recordBio.BCF_Min = getCellStringValue(row, htCols.get("BCF Min"));
				// 	recordBio.BCF_Max = getCellStringValue(row, htCols.get("BCF Max"));
				// 	recordBio.BCF = bcfVal;
				// 	recordBio.BAF_Min = null;
				// 	recordBio.BAF_Max = null;
				// 	recordBio.BAF = null;
				// 	populateBioMetadata(recordBio, row, htCols);
				// 	rec.RecordsBio.add(recordBio);
				// }
			
				// Create RecordBio for BAF if value exists (separate record)
				// if (bafVal != null && !bafVal.isBlank()) {
				// 	RecordBio recordBio = new RecordBio();
				// 	recordBio.BCF_Min = null;
				// 	recordBio.BCF_Max = null;
				// 	recordBio.BCF = null;
				// 	recordBio.BAF_Min = getCellStringValue(row, htCols.get("BAF Min"));
				// 	recordBio.BAF_Max = getCellStringValue(row, htCols.get("BAF Max"));
				// 	recordBio.BAF = bafVal;
				// 	populateBioMetadata(recordBio, row, htCols);
				// 	rec.RecordsBio.add(recordBio);
				// }
			}
			
			// Convert HashMap values to list
			recs.addAll(chemicalMap.values());
			
			System.out.println("Number of unique chemicals=" + recs.size());
			wb.close();
			fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return recs;
	}

	private String getCellStringValue(Row row, Integer colIndex) {
		if (colIndex == null || row == null) return null;
		Cell cell = row.getCell(colIndex);
		if (cell == null) return null;
		
		try {
			switch (cell.getCellType()) {
				case STRING:
					return cell.getStringCellValue();
				case NUMERIC:
					// Handle numeric cells by converting to string
					double numVal = cell.getNumericCellValue();
					// Check if it's a whole number
					if (numVal == (long) numVal) {
						return String.format("%d", (long) numVal);
					} else {
						return String.valueOf(numVal);
					}
				case BLANK:
					return null;
				default:
					return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	private void populateBioMetadata(RecordBio bioRecord, Row row, Hashtable<String, Integer> htCols) {
		bioRecord.Organism_Common_Name = getCellStringValue(row, htCols.get("Organism Common Name"));
		bioRecord.Organism_Scientific_Name = getCellStringValue(row, htCols.get("Scientific Name"));
		bioRecord.Tissue_Type = getCellStringValue(row, htCols.get("Tissue Type (valid values)"));
		bioRecord.Wet_Dry_Lipid_Basis = getCellStringValue(row, htCols.get("Wet/Dry/Lipid Basis"));
		bioRecord.Lab_Field_Model_Study = getCellStringValue(row, htCols.get("Lab or Field or Model Study"));
		bioRecord.Location = getCellStringValue(row, htCols.get("Location"));
		bioRecord.Freshwater_Marine_Estuary = getCellStringValue(row, htCols.get("Freshwater/marine/estuarine"));
		bioRecord.Waterbody_Description = getCellStringValue(row, htCols.get("Waterbody Description"));
		bioRecord.Reviewer_Notes = getCellStringValue(row, htCols.get("Reviewer Notes"));
	}

	private void populateBioMetadata(RecordBio bioRecord, Row row, Hashtable<String, Integer> htCols, Row rowRef, Hashtable<String, Integer> htColsRefs) {
		// Handle metadata from main sheet
		bioRecord.Organism_Common_Name = getCellStringValue(row, htCols.get("Organism Common Name"));
		bioRecord.Organism_Scientific_Name = getCellStringValue(row, htCols.get("Scientific Name"));
		bioRecord.Tissue_Type = getCellStringValue(row, htCols.get("Tissue Type (valid values)"));
		bioRecord.Wet_Dry_Lipid_Basis = getCellStringValue(row, htCols.get("Wet/Dry/Lipid Basis"));
		bioRecord.Lab_Field_Model_Study = getCellStringValue(row, htCols.get("Lab or Field or Model Study"));
		bioRecord.Location = getCellStringValue(row, htCols.get("Location"));
		bioRecord.Freshwater_Marine_Estuary = getCellStringValue(row, htCols.get("Freshwater/marine/estuarine"));
		bioRecord.Waterbody_Description = getCellStringValue(row, htCols.get("Waterbody Description"));
		bioRecord.Reviewer_Notes = getCellStringValue(row, htCols.get("Reviewer Notes"));

		// Handle citation metadata from log sheet
		bioRecord.Reference_Label = getCellStringValue(rowRef, htColsRefs.get("Reference")).trim();
		bioRecord.Reference_Title = getCellStringValue(rowRef, htColsRefs.get("Title")).trim();
		bioRecord.Reference_Journal = getCellStringValue(rowRef, htColsRefs.get("Journal")).trim();
		bioRecord.Reference_Url = getCellStringValue(rowRef, htColsRefs.get("URL")).trim();

		// Pattern doiPattern = Pattern.compile("\\b10\\.\\d{4,9}/[-._;()/:A-Z0-9]+", Pattern.CASE_INSENSITIVE);
		// Matcher doiMatcher = doiPattern.matcher(bioRecord.Reference_Url);
		// if (doiMatcher.find()) {
		// 	bioRecord.Reference_Doi = doiMatcher.group(); // Regex match for DOI within the Reference URL
		// }

		Pattern yearPattern = Pattern.compile("\\d{4}\\w?");
		Matcher yearMatcher = yearPattern.matcher(bioRecord.Reference_Label);
		if (yearMatcher.find()) {
			bioRecord.Reference_Year = yearMatcher.group();
			bioRecord.Reference_Author = bioRecord.Reference_Label.substring(0, bioRecord.Reference_Label.indexOf(yearMatcher.group())).trim();
		}

		bioRecord.Reference_Citation = bioRecord.Reference_Author + ", " + bioRecord.Reference_Year + ". " + bioRecord.Reference_Title + ". " + bioRecord.Reference_Journal + ". " + bioRecord.Reference_Url;
	}
	

	public static void main(String[] args) {
		RecordITRC rm=new RecordITRC();
		rm.parseExcelFile(filename);
//		rm.getCitations();
		
	}
	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
			
		er.chemical_name=PFAS_Name.trim();
		er.synonyms=Acronym;
		
		er.property_name=ExperimentalConstants.strKOC;
		
		er.property_value_units_original=ExperimentalConstants.str_LOG_L_KG;

//		er.property_value_point_estimate_original=Koc;
		
		// er.property_value_string = this.LogKocWithStdDev;
		
//		LogKocWithStdDev=LogKocWithStdDev.replace("2.02 (Ã¯Â¯ÂÂ¿Â½0.01) to 2.1 Ã¯Â Â(Ã¯Â¿Â½0.02)","Ã¯Â.Â02 (Ã¯Â¿Â½0.01) Ã¯ÂoÂ 2.14 (Ã¯Â¿Â½0.02)");
		// LogKocWithStdDev=LogKocWithStdDev.replace("2.1 4 (","2.14 (");
		
		// LogKocWithStdDev=LogKocWithStdDev.replace("1.1-2.1","1.1 to 2.1");
		// LogKocWithStdDev=LogKocWithStdDev.replace("2.4-2.6","2.4 to 2.6");
		// LogKocWithStdDev=LogKocWithStdDev.replace("4.3-6.0","4.3 to 6.0");
		// LogKocWithStdDev=LogKocWithStdDev.replace("2.34-2.83","2.34 to 2.83");
//		LogKocWithStdDev=LogKocWithStdDev.replace("ÃÃÂ±",Ã¯Â"ÂÃ¯Â¿Â½");
		
//		for (int i = 0; i < LogKocWithStdDev.length(); i++) {
//            char ch = LogKocWithStdDev.charAt(i);
//            if (!Character.isLetterOrDigit(ch)) {
//                int charCode = (int) ch; // Get the character code
//                System.out.println(LogKocWithStdDev+", Special character: '" + ch + "' - Code: " + charCode);
//            }
//        }
		
				
		// if(LogKocWithStdDev.contains("to")) {
			
		// 	String [] vals=LogKocWithStdDev.split(" to ");
			
		// 	if(vals.length==2) {
				
		// 		String val1=vals[0];
				
		// 		if(val1.contains("(")) {
		// 			val1=val1.substring(0,val1.indexOf("(")).trim();
		// 		}
				
		// 		String val2=vals[1];
				
		// 		if(val2.contains("(")) {
		// 			val2=val2.substring(0,val2.indexOf("(")).trim();
		// 		}

//				System.out.println(val1+"\t"+val2);
				
				// er.property_value_min_original=Double.parseDouble(val1);
				// er.property_value_max_original=Double.parseDouble(val2);

				
			// } else {
			// 	System.out.println("Only 1 value with to:"+LogKocWithStdDev);//Doesnt happen
			// }
		// } else {
		// 	String val=LogKocWithStdDev;
		// 	if(val.contains("(")) {
		// 		val=val.substring(0,val.indexOf("(")).trim();
		// 	}
		// 	er.property_value_point_estimate_original=Double.parseDouble(val);
//			System.out.println(LogKocWithStdDev+"\t"+er.property_value_point_estimate_original);
		// }
		
		
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
		// uc.convertRecord(er);
		
		return er;
	}

	public List<ExperimentalRecord> toExperimentalRecordsKoc() {
		List<ExperimentalRecord> records = new ArrayList<>();
		
		for (RecordKOC recordKOC : RecordsKOC) {
			String logKocValue = recordKOC.LogKocWithStdDev;
			
			ExperimentalRecord er = new ExperimentalRecord();
			
			er.chemical_name = PFAS_Name.trim();
			er.synonyms = Acronym;
			
			er.property_name = ExperimentalConstants.strKOC;
			er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
			er.property_value_string = logKocValue;
			
			// Apply string replacements
			logKocValue = logKocValue.replace("2.1 4 (", "2.14 (");
			logKocValue = logKocValue.replace("1.1-2.1", "1.1 to 2.1");
			logKocValue = logKocValue.replace("2.4-2.6", "2.4 to 2.6");
			logKocValue = logKocValue.replace("4.3-6.0", "4.3 to 6.0");
			logKocValue = logKocValue.replace("2.34-2.83", "2.34 to 2.83");
			
			// Parse range or point estimate
			if (logKocValue.contains("to")) {
				String[] vals = logKocValue.split(" to ");
				if (vals.length == 2) {
					String val1 = vals[0];
					if (val1.contains("(")) {
						val1 = val1.substring(0, val1.indexOf("(")).trim();
					}
					String val2 = vals[1];
					if (val2.contains("(")) {
						val2 = val2.substring(0, val2.indexOf("(")).trim();
					}
					er.property_value_min_original = Double.parseDouble(val1);
					er.property_value_max_original = Double.parseDouble(val2);
				}
			} else {
				String val = logKocValue;
				if (val.contains("(")) {
					val = val.substring(0, val.indexOf("(")).trim();
				}
				er.property_value_point_estimate_original = Double.parseDouble(val);
			}
			
			// Add experimental parameters and metadata
			if (Isomer != null && !Isomer.equals("Not available")) {
				er.updateNote("Isomer=" + Isomer);
			}
			
			er.experimental_parameters = new TreeMap<>();
			if (Testing_Conditions != null && !Testing_Conditions.equals("NA") && !Testing_Conditions.equals("NR")) {
				er.experimental_parameters.put("Testing_Conditions", Testing_Conditions);
			}
			if (Applicable_Matrices != null && !Applicable_Matrices.equals("--") && !Applicable_Matrices.equals("NR")) {
				er.experimental_parameters.put("Media", Applicable_Matrices);
			}
			
			LiteratureSource ls = new LiteratureSource();
			ls.citation = Reference_Citation;
			ls.name = Reference_Label;
			er.literatureSource = ls;
			
			PublicSource ps = new PublicSource();
			ps.name = sourceName;
			ps.url = "https://pfas-1.itrcweb.org/external-data-tables/";
			er.publicSource = ps;
			
			er.source_name = sourceName;
			
			if (Type != null && Type.contains("F")) {
				er.keep = false;
				er.reason = "Field measurement";
			}
			if (Type != null && Type.contains("M")) {
				er.keep = false;
				er.reason = "Modeled";
			}
			
			uc.convertRecord(er);
			records.add(er);
		}
		
		return records;
	}

	public List<ExperimentalRecord> toExperimentalRecordsBio(String propertyName, Hashtable<String, List<Species>> htSpecies) {
		List<ExperimentalRecord> records = new ArrayList<>();
		
		if (RecordsBio == null) return records;
		
		for (RecordBio recordBio : RecordsBio) {
			// Create record for the specified property type if value exists
			if (propertyName.equals(ExperimentalConstants.strBCF) && recordBio.BCF != null && !recordBio.BCF.isBlank()) {
				ExperimentalRecord er = createExperimentalRecordFromBio(this, recordBio, "BCF", htSpecies);
				records.add(er);
			}
			
			// Create record for BAF if value exists
			if (propertyName.equals(ExperimentalConstants.strBAF) && recordBio.BAF != null && !recordBio.BAF.isBlank()) {
				ExperimentalRecord er = createExperimentalRecordFromBio(this, recordBio, "BAF", htSpecies);
				records.add(er);
			}
		}
		
		return records;
	}

	private ExperimentalRecord createExperimentalRecordFromBio(RecordITRC parentRecord, RecordBio recordBio, String propertyType, Hashtable<String, List<Species>> htSpecies) {
		ExperimentalRecord er = new ExperimentalRecord();
		
		// Use chemical identifying information from parent RecordITRC
		er.chemical_name = parentRecord.PFAS_Name;
		er.synonyms = parentRecord.Acronym;
		er.casrn = parentRecord.CAS;
		if (parentRecord.CAS == null) {
			er.updateNote("CASRN set to null since original value was 0");
		}
		
		// Set property based on type (BCF or BAF)
		if ("BCF".equals(propertyType)) {
			er.property_name = ExperimentalConstants.strBCF;
			
			// Parse BCF value
			parsePropertyValue(er, recordBio.BCF, recordBio.BCF_Min, recordBio.BCF_Max);
			
		} else if ("BAF".equals(propertyType)) {
			er.property_name = ExperimentalConstants.strBAF;
			
			// Parse BAF value
			parsePropertyValue(er, recordBio.BAF, recordBio.BAF_Min, recordBio.BAF_Max);
		}
		
		// Set experimental parameters from RecordBio
		er.experimental_parameters = new TreeMap<>();
		if (recordBio.Lab_Field_Model_Study != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, recordBio.Lab_Field_Model_Study);
		}

		if (recordBio.Location != null) {
			// er.experimental_parameters.put("Location", recordBio.Location);
			er.updateNote("Reported geographic location: " + recordBio.Location);
		}

		if (recordBio.Freshwater_Marine_Estuary != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, recordBio.Freshwater_Marine_Estuary.toLowerCase().trim());
		}

		if (recordBio.Organism_Common_Name != null || recordBio.Organism_Scientific_Name != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, recordBio.getSpeciesSupercategory(htSpecies));
			if (recordBio.Organism_Common_Name != null) {
				er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesCommon, recordBio.Organism_Common_Name);
			}
			if (recordBio.Organism_Scientific_Name != null) {
				er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, recordBio.Organism_Scientific_Name);
			}
		}

		if (recordBio.Tissue_Type != null) {
			String tissueType = recordBio.Tissue_Type;
			if (tissueType.toLowerCase().contains("whole")) {
				tissueType = "whole body";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamTissueType, tissueType.toLowerCase().trim());
		}

		if (recordBio.Wet_Dry_Lipid_Basis != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, recordBio.Wet_Dry_Lipid_Basis);
		}

		if (recordBio.Reviewer_Notes != null) {
			// er.experimental_parameters.put("Reviewer Notes", recordBio.Reviewer_Notes);
			er.updateNote(recordBio.Reviewer_Notes);
		}

		// if (recordBio.Waterbody_Description != null) {
		// 	if (recordBio.Waterbody_Description.toLowerCase().contains("lab")) {
		// 		er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, "Lab");
		// 	} else {
		// 		er.experimental_parameters.put("Waterbody Description", "Field");
		// 	}
		// }
		
		// Set source information from parent RecordITRC
		LiteratureSource ls = new LiteratureSource();
		ls.citation = recordBio.Reference_Citation;
		ls.name = recordBio.Reference_Label;
		ls.url = recordBio.Reference_Url;
		// ls.doi = recordBio.Reference_Doi;
		ls.author = recordBio.Reference_Author;
		ls.journal = recordBio.Reference_Journal;
		ls.title = recordBio.Reference_Title;
		ls.year = recordBio.Reference_Year;
		er.literatureSource = ls;
		
		PublicSource ps = new PublicSource();
		ps.name = sourceName;
		ps.url = "https://pfas-1.itrcweb.org/external-data-tables/";
		ps.description = "Bioconcentration and bioaccumulation factor data from ITRC data last updated in October 2021";
		er.publicSource = ps;
		
		er.source_name = sourceName;
		
		// Filter out records with data from undesired types of sources
		if (recordBio.Lab_Field_Model_Study != null && recordBio.Lab_Field_Model_Study.contains("Model")) {
			er.keep = false;
			er.updateReason("Modeled data");
		}
		// if (recordBio.Wet_Dry_Lipid_Basis != null && !recordBio.Wet_Dry_Lipid_Basis.equals("Wet")) {
		// 	er.keep = false;
		// 	er.updateReason("Non-wet basis");
		// }

		
		return er;
	}

	private void parsePropertyValue(ExperimentalRecord er, String mainValue, String minValue, String maxValue) {
		try {
			er.property_value_string = "";
			
			Boolean hasMin = minValue != null && !minValue.isBlank();
			Boolean hasMain = mainValue != null && !mainValue.isBlank();
			Boolean hasMax = maxValue != null && !maxValue.isBlank();

			// If min and max exist, use range (possibly dropping main)
			if (hasMin && hasMax) {
				double minVal = Double.parseDouble(minValue.trim());
				double maxVal = Double.parseDouble(maxValue.trim());
				er.property_value_string = minVal + " - " + maxVal;
				er.property_value_min_original = minVal;
				er.property_value_min_final = minVal;
				er.property_value_max_original = maxVal;
				er.property_value_max_final = maxVal;
				if (hasMain) {
					er.updateNote("Dropped point estimate value because min and max were provided");
				}
			} else if (hasMain) {
				double mainVal = Double.parseDouble(mainValue.trim());
				er.property_value_string = (String.valueOf(mainVal));
				er.property_value_point_estimate_original = mainVal;
				er.property_value_point_estimate_final = mainVal;
				if (hasMax) {
					er.updateNote("Dropped max value because point estimate was provided and no minimum was provided (and max is the log-value of the point estimate)");
				}
			} else {
				er.keep = false;
				er.updateReason("Invalid combination of data values");
			}

			er.property_value_units_original = ExperimentalConstants.str_L_KG;
			er.property_value_units_final = ExperimentalConstants.str_L_KG;


		} catch (NumberFormatException e) {
			// If numeric parsing fails, keep string representation
			System.out.println("Could not parse numeric value from: " + mainValue + ", " + minValue + ", " + maxValue);
		}
	}

}

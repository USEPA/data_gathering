package gov.epa.exp_data_gathering.parse.PubChem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.database.SQLite_CreateTable;
import gov.epa.database.SQLite_GetRecords;
import gov.epa.database.SQLite_Utilities;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.PressureCondition;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.RecordDashboard;
import gov.epa.exp_data_gathering.parse.TemperatureCondition;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.pHCondition;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Data;

import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.IdentifierData;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Information;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Markup;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Property;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Reference;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.Section;
import gov.epa.exp_data_gathering.parse.PubChem.JSONsForPubChem.StringWithMarkup;

import gov.epa.ghs_data_gathering.Utilities.FileUtilities;

/**
 * Contact at PubChem: Evan (NIH/NLM/NCBI) Bolton: bolton@ncbi.nlm.nih.gov
 * 
 */
public class RecordPubChem {
	Long ANID;
	Long cid;
	String iupacNameCid;//from pubchem- based on cid 
	String canonSmilesCid;////from pubchem - based on cid
	String synonyms;
	
	String casReference;//cas number from original source from reference number
	String chemicalNameReference;// name from original source from reference number

	
//	transient Hashtable<String,String> htCAS;//lookup cas based on reference number
//	transient Hashtable<String,String> htChemicalName;//lookup chemical name based on reference number
	
	
	
//	Vector<String> physicalDescription;
//	Vector<String> density;
//	Vector<String> meltingPoint;
//	Vector<String> boilingPoint;
//	Vector<String> flashPoint;
//	Vector<String> solubility;
//	Vector<String> vaporPressure;
//	Vector<String> henrysLawConstant;
//	Vector<String> logP;
//	Vector<String> pKa;
//	Hashtable<Integer, String> physicalDescriptionHT = new Hashtable<>();
//	Hashtable<Integer, String> densityHT = new Hashtable<>();
//	Hashtable<Integer, String> meltingPointHT = new Hashtable<>();
//	Hashtable<Integer, String> boilingPointHT = new Hashtable<>();
//	Hashtable<Integer, String> solubilityHT = new Hashtable<>();
//	Hashtable<Integer, String> flashPointHT = new Hashtable<>();
//	Hashtable<Integer, String> vaporPressureHT = new Hashtable<>();
//	Hashtable<Integer, String> hlcHT = new Hashtable<>();
//	Hashtable<Integer, String> logPHT = new Hashtable<>();
//	Hashtable<Integer, String> pKaHT = new Hashtable<>();

	String propertyName;
	String propertyValue;
	
	List<MarkupChemical> markupChemicals;
	
	
	public class MarkupChemical {
		String name;
		String cid;
	}


	String reference;
	String date_accessed;
	LiteratureSource literatureSource;
	PublicSource publicSourceOriginal;

	String pageUrl;

	String notes;

//	static final String sourceName = ExperimentalConstants.strSourcePubChem + "_2024_03_20";
	static final String sourceName = ExperimentalConstants.strSourcePubChem + "_2024_11_27";

	static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	private static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");

	public RecordPubChem() {
//		cas = new Vector<String>();
//		htCAS=new Hashtable<String,String>();
//		htChemicalName=new Hashtable<String, String>();
//		physicalDescription = new Vector<String>();
//		density = new Vector<String>();
//		meltingPoint = new Vector<String>();
//		boilingPoint = new Vector<String>();
//		flashPoint = new Vector<String>();
//		solubility = new Vector<String>();
//		vaporPressure = new Vector<String>();
//		henrysLawConstant = new Vector<String>();
//		logP = new Vector<String>();
//		pKa = new Vector<String>();
	}

	/**
	 * Extracts DTXSIDs from CompTox dashboard records and translates them to
	 * PubChem CIDs
	 * 
	 * @param records A vector of RecordDashboard objects
	 * @param start   The index in the vector to start converting
	 * @param end     The index in the vector to stop converting
	 * @return A vector of PubChem CIDs as strings
	 */
	private static Vector<String> getCIDsFromDashboardRecords(Vector<RecordDashboard> records, String dictFilePath,
			int start, int end) {
		Vector<String> cids = new Vector<String>();
		LinkedHashMap<String, String> dict = new LinkedHashMap<String, String>();

		try {
			File file = new File(dictFilePath);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] cells = line.split(",");
				dict.put(cells[0], cells[1]);
			}
			br.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int counter = 0;
		for (int i = start; i < end; i++) {
			String dtxsid = records.get(i).DTXSID;
			String cid = dict.get(dtxsid);
			if (cid != null) {
				cids.add(cid);
				counter++;
			} else {
				boolean foundCID = false;
				try {
					String inchikey = records.get(i).INCHIKEY;
					String url = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/inchikey/" + inchikey
							+ "/cids/TXT";
					String cidsTxt = FileUtilities.getText_UTF8(url);
					if (cidsTxt != null) {
						cids.add(cidsTxt.split("\r\n")[0]);
						foundCID = true;
						counter++;
					}
					Thread.sleep(200);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (!foundCID) {
					try {
						String smiles = records.get(i).SMILES;
						String url = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/smiles/" + smiles
								+ "/cids/TXT";
						String cidsTxt = FileUtilities.getText_UTF8(url);
						if (cidsTxt != null) {
							cids.add(cidsTxt.split("\r\n")[0]);
							foundCID = true;
							counter++;
						}
						Thread.sleep(200);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			if (counter % 100 == 0) {
				System.out.println("Found " + counter + " CIDs");
			}
		}
		System.out.println("Found " + counter + " CIDs");
		return cids;
	}

	public static HashSet<String> getCidsInDatabase(String sourceName) {
		String databaseName = sourceName + "_raw_json.db";
		String tableName = sourceName;
		String databaseFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
		String databasePath = databaseFolder + File.separator + databaseName;

		java.sql.Connection conn = SQLite_Utilities.getConnection(databasePath);

		HashSet<String> cidsAlreadyQueried = new HashSet<String>();
		ResultSet rs = SQLite_GetRecords.getAllRecords(SQLite_Utilities.getStatement(conn), tableName);
		try {
			long start = System.currentTimeMillis();
			while (rs.next()) {
				cidsAlreadyQueried.add(rs.getString("cid"));
			}
			long end = System.currentTimeMillis();

			System.out.println(cidsAlreadyQueried.size() + " CIDs in " + databasePath);

			return cidsAlreadyQueried;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	public static HashSet<Long> getCidsInDatabase2(String sourceName) {
		String databaseName = sourceName + "_raw_json.db";
		String tableName = sourceName;
		String databaseFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
		String databasePath = databaseFolder + File.separator + databaseName;

		java.sql.Connection conn = SQLite_Utilities.getConnection(databasePath);

		HashSet<Long> cidsAlreadyQueried = new HashSet<>();
		
		ResultSet rs = SQLite_GetRecords.getRecords(SQLite_Utilities.getStatement(conn), "select cid from "+tableName);
		try {
			long start = System.currentTimeMillis();
			while (rs.next()) {
				cidsAlreadyQueried.add(rs.getLong(1));
			}
			long end = System.currentTimeMillis();

			System.out.println(cidsAlreadyQueried.size() + " CIDs in " + databasePath);

			return cidsAlreadyQueried;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

	}

	private static void downloadJSONsToDatabase(HashSet<Long> cids, boolean startFresh) {
		ParsePubChem p = new ParsePubChem();
		String databaseName = p.sourceName + "_raw_json.db";
		String tableName = p.sourceName;
		String databasePath = p.databaseFolder + File.separator + databaseName;
		File db = new File(databasePath);
		if (!db.getParentFile().exists()) {
			db.getParentFile().mkdirs();
		}
		java.sql.Connection conn = SQLite_CreateTable.create_table(databasePath, tableName,
				RawDataRecordPubChem.fieldNames, startFresh);

		HashSet<Long> cidsAlreadyQueried=getCidsInDatabase2(p.sourceName);
		
		long sleep=200;
		
		
		try {
			int counterSuccess = 0;
			int counterTotal = 0;
			int counterMissingExpData = 0;
			long start = System.currentTimeMillis();
			for (Long cid : cids) {
				String experimentalURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/" + cid
						+ "/JSON?heading=Experimental+Properties";
				String idURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/property/IUPACName,CanonicalSMILES/JSON?cid="
						+ cid;
				String casURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/" + cid
						+ "/JSON?heading=CAS";
				String synonymURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/cid/" + cid + "/synonyms/TXT";

				SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				Date date = new Date();
				String strDate = formatter.format(date);

				RawDataRecordPubChem rec = new RawDataRecordPubChem(strDate, cid, "", "", "", "");
				if (cidsAlreadyQueried.add(cid) || startFresh) {
					counterTotal++;
					boolean keepLooking = true;
					try {
						rec.experimental = FileUtilities.getText_UTF8(experimentalURL);
						rec.experimental = rec.experimental.replaceAll("'", "''").replaceAll(";", "\\;");
					} catch (Exception ex) {
						counterMissingExpData++;
						keepLooking = false;
					}
					Thread.sleep(sleep);
					if (keepLooking) {
						try {
//							rec.cas=FileUtilities.getText_UTF8(casURL).replaceAll("'", "\'").replaceAll(";", "\\;");
							rec.cas = FileUtilities.getText_UTF8(casURL);
							rec.cas = rec.cas.replaceAll("'", "''").replaceAll(";", "\\;");
						} catch (Exception ex) {
						}
						Thread.sleep(sleep);
						try {
//							rec.identifiers=FileUtilities.getText_UTF8(idURL).replaceAll("'", "\'").replaceAll(";", "\\;");
							rec.identifiers = FileUtilities.getText_UTF8(idURL);
							rec.identifiers = rec.identifiers.replaceAll("'", "''").replaceAll(";", "\\;");
						} catch (Exception ex) {
						}
						Thread.sleep(sleep);
						try {
//							rec.synonyms=FileUtilities.getText_UTF8(synonymURL).replaceAll("'", "\'").replaceAll(";", "\\;");
							rec.synonyms = StringEscapeUtils.escapeHtml4(FileUtilities.getText_UTF8(synonymURL));
							rec.synonyms = rec.synonyms.replaceAll("'", "''").replaceAll(";", "\\;");
						} catch (Exception ex) {
						}
						Thread.sleep(sleep);
					}
					if (rec.experimental != null && !rec.experimental.isBlank()) {
						rec.addRecordToDatabase(tableName, conn);
						counterSuccess++;
					}
					if (counterTotal % 100 == 0) {
						long batchEnd = System.currentTimeMillis();
						System.out.println("Attempted: " + counterTotal + " (" + cidsAlreadyQueried.size() + " total)");
						System.out.println("Succeeded: " + counterSuccess);
						System.out.println("Failed - no experimental properties: " + counterMissingExpData);
						System.out.println("---------- (~" + (batchEnd - start) / 60000 + " min)");
						start = batchEnd;
					}
				}
			}
			System.out.println("Attempted: " + counterTotal + " (" + cidsAlreadyQueried.size() + " total)");
			System.out.println("Succeeded: " + counterSuccess);
			System.out.println("Failed - no experimental properties: " + counterMissingExpData);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected static Vector<RecordPubChem> parseJSONsInDatabase() {
		String databaseFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
//		String databasePath = databaseFolder + File.separator + sourceName + "_raw_json - Copy.db";
		String databasePath = databaseFolder + File.separator + sourceName + "_raw_json.db";
		Vector<RecordPubChem> records = new Vector<>();

		try {
			Statement stat = SQLite_Utilities.getStatement(databasePath);
			ResultSet rs = SQLite_GetRecords.getAllRecords(stat, sourceName);

			int counter = 0;

			System.out.println("Going through records in " + databasePath);

			while (rs.next()) {

				counter++;

				if (counter % 1000 == 0) {
					System.out.println(counter);
				}

				String date = rs.getString("date");
				String experimental = rs.getString("experimental");
				Data experimentalData = gson.fromJson(experimental, Data.class);

				Hashtable<Integer, Reference> htReferences = getReferenceHashtable(experimentalData);
//				System.out.println(gson.toJson(experimentalData.record.reference));

				List<Section> experimentalProperties = experimentalData.record.section.get(0).section.get(0).section;

				for (Section section : experimentalProperties) {

//					System.out.println(gson.toJson(section));

					getRecords(records, rs, date, experimentalData, htReferences, section);
					
//					if (section.tocHeading.trim().equals("Dissociation Constants")) {
//						//TODO Other Experimental Properties has mix of things with no property name explicitly listed
//						getRecordsWithEmbeddedPropertyNames(records, rs, date, experimentalData, htReferences, section);
//					} else {
//						getRecords(records, rs, date, experimentalData, htReferences, section);
//					}

				}

//				if(true) break;

			} // end loop over records
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}
	
	
	protected static Vector<RecordPubChem> parseJSONInDatabase(String cid) {
		String databaseFolder = "Data" + File.separator + "Experimental" + File.separator + sourceName;
//		String databasePath = databaseFolder + File.separator + sourceName + "_raw_json - Copy.db";
		String databasePath = databaseFolder + File.separator + sourceName + "_raw_json.db";
		Vector<RecordPubChem> records = new Vector<>();

		try {
			Statement stat = SQLite_Utilities.getStatement(databasePath);
			
			String sql="select * from "+sourceName+" where cid="+cid+";";
			
			ResultSet rs = SQLite_GetRecords.getRecords(stat, sql);

			int counter = 0;

			System.out.println("Going through records in " + databasePath);

			while (rs.next()) {

				counter++;

				if (counter % 1000 == 0) {
					System.out.println(counter);
				}

				String date = rs.getString("date");
				String experimental = rs.getString("experimental");
				Data experimentalData = gson.fromJson(experimental, Data.class);

				Hashtable<Integer, Reference> htReferences = getReferenceHashtable(experimentalData);
//				System.out.println(gson.toJson(experimentalData.record.reference));

				List<Section> experimentalProperties = experimentalData.record.section.get(0).section.get(0).section;

				for (Section section : experimentalProperties) {

//					System.out.println(gson.toJson(section));

					getRecords(records, rs, date, experimentalData, htReferences, section);
					
//					if (section.tocHeading.trim().equals("Dissociation Constants")) {
//						//TODO Other Experimental Properties has mix of things with no property name explicitly listed
//						getRecordsWithEmbeddedPropertyNames(records, rs, date, experimentalData, htReferences, section);
//					} else {
//						getRecords(records, rs, date, experimentalData, htReferences, section);
//					}

				}

//				if(true) break;

			} // end loop over records
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}

	private static void getRecords(Vector<RecordPubChem> records, ResultSet rs, String date, Data experimentalData,
			Hashtable<Integer, Reference> htReferences, Section section) throws SQLException {
		
		Hashtable<String,String> htCAS=new Hashtable<String,String>();//lookup cas based on reference number
		Hashtable<String,String> htChemicalName=new Hashtable<String,String>();//lookup chemical name based on reference number

		String cas = rs.getString("cas");
		Data casData = gson.fromJson(cas, Data.class);
		if (casData != null) {
			List<Information> casInfo = casData.record.section.get(0).section.get(0).section.get(0).information;
			for (Information c : casInfo) {
				String newCAS = c.value.stringWithMarkup.get(0).string;
				htCAS.put(c.referenceNumber, newCAS);
			}
			
			if(casData.record!=null && casData.record.reference!=null) {
				List<Reference>reference=casData.record.reference;
				for (Reference ref:reference) {
					htChemicalName.put(ref.referenceNumber,ref.name);
				}
//				System.out.println(gson.toJson(pcr.htChemicalName));
			}
		}
		
		for (Information information : section.information) {
//						System.out.println(gson.toJson(information));

			List<StringWithMarkup> valueStrings = information.value.stringWithMarkup;
			if (valueStrings == null) {
//							System.out.println(gson.toJson(information));
				continue;
			}

			// Loop over property values
			for (StringWithMarkup valueString : valueStrings) {

				if (valueString.string == null)
					continue;
				RecordPubChem pcr = new RecordPubChem();
				pcr.date_accessed = date.substring(0, date.indexOf(" "));
				pcr.cid = Long.parseLong(experimentalData.record.recordNumber);
				
//				pcr.propertyName = section.tocHeading.trim();
				
				if (information.name != null) {//happens with dissociation constants and other experimental properties
					pcr.propertyName = information.name.trim();
					// will have to parse out property name from the property value later
				} else {
					pcr.propertyName=section.tocHeading.trim();//temporary
				}
				
				pcr.propertyValue = valueString.string;				
				if(pcr.propertyValue!=null) pcr.propertyValue=pcr.propertyValue.trim();
				
				String s=pcr.propertyValue;
				
//				if(s.substring(s.length()-1,s.length()).contentEquals("/") && !s.contains("Estimated")) {//ends with /
//					System.out.println(pcr.propertyValue+"\n");
//				}
				
//				if(valueString.Markup==null) {
				
//				if(!pcr.propertyName.contentEquals("Physical Description") && !pcr.propertyName.contains("Other") && !pcr.propertyName.contains("Odor")) {
//				
//					System.out.println(pcr.propertyName+"\n"+gson.toJson(valueString));
				
//				}
				
				if(valueString.Markup!=null) {
					pcr.markupChemicals=new ArrayList<MarkupChemical>();
					for (Markup m:valueString.Markup) {
						MarkupChemical mc=pcr.new MarkupChemical();
						
						if (m.Extra!=null && m.Extra.indexOf("CID-")==0) {
							mc.cid=m.Extra.substring(4,m.Extra.length());	
						}
						
						if(m.URL!=null && m.URL.contains("compound")) 
							mc.name=m.URL.replace("https://pubchem.ncbi.nlm.nih.gov/compound/", "");	
						else if (m.URL!=null && m.URL.contains("element"))
							mc.name=m.URL.replace("https://pubchem.ncbi.nlm.nih.gov/element/", "");
					
						pcr.markupChemicals.add(mc);
					}
					
//					System.out.println(gson.toJson(pcr));
				}
				
				
				addIdentifiers(rs, pcr);
				addSourceMetadata(htReferences, information, pcr, htCAS,htChemicalName);
				records.add(pcr);
			}

		}
	}

//	private static void getRecordsWithEmbeddedPropertyNames(Vector<RecordPubChem> records, ResultSet rs, String date,
//			Data experimentalData, Hashtable<Integer, Reference> htReferences, Section section) throws SQLException {
//
//		for (Information information : section.information) {
//
//			List<StringWithMarkup> valueStrings = information.value.stringWithMarkup;
//			if (valueStrings == null) {
////							System.out.println(gson.toJson(information));
//				continue;
//			}
//
//			// Loop over property values
//			for (StringWithMarkup valueString : valueStrings) {
//
//				if (valueString.string == null)
//					continue;
//
//				RecordPubChem pcr = new RecordPubChem();
//				pcr.date_accessed = date.substring(0, date.indexOf(" "));
//				pcr.cid = experimentalData.record.recordNumber;
//
//				if (information.name != null) {
//					pcr.propertyName = information.name.trim();
//					// will have to extra later
//				} else {
//					pcr.propertyName=section.tocHeading.trim();//temporary
//				}
//
//				pcr.propertyValue = valueString.string;
//				addSourceMetadata(htReferences, information, pcr);
//				addIdentifiers(rs, pcr);
//				records.add(pcr);
////					System.out.println("here pcr="+gson.toJson(pcr));
//			}
//
//		}
//	}

	private static void addSourceMetadata(Hashtable<Integer, Reference> htReferences, Information information,
			RecordPubChem pcr, Hashtable<String, String> htCAS, Hashtable<String, String> htChemicalName) {

		if (information.reference != null) {
			pcr.literatureSource = new LiteratureSource();

			String citation1 = null;
			String citation2 = null;

			for (String reference : information.reference) {

				if (reference.contains("PMID:")) {

					if (reference.indexOf("PMID:") == 0) {
						String pmid = reference.substring(reference.indexOf(":") + 1, reference.length());
						pcr.literatureSource.url = "https://pubmed.ncbi.nlm.nih.gov/" + pmid + "/";
//						System.out.println(pcr.literatureSource.doi);
					} else if (reference.indexOf("DOI") > -1) {

						if (reference.indexOf("PMID") > -1) {
							String doi2 = reference.substring(reference.indexOf("DOI:") + 4, reference.length());
							doi2 = doi2.substring(0, doi2.indexOf(" ") - 1).trim();
							doi2 = "https://doi.org/" + doi2;
							pcr.literatureSource.doi = doi2;

						} else {
							System.out.println("Here2\treference=" + reference);
						}

						citation1 = reference.substring(0, reference.indexOf("DOI"));
						pcr.literatureSource.citation = citation1;

						if (reference.indexOf("PMID:") > 0) {
//							System.out.println(reference);
							String pmid = reference.substring(reference.indexOf("PMID:") + 5, reference.length());
							pcr.literatureSource.url = "https://pubmed.ncbi.nlm.nih.gov/" + pmid + "/";
//							System.out.println(pcr.literatureSource.url);
						}
					} else {
//						System.out.println("Here3\treference="+reference);
						pcr.literatureSource.citation = reference;
					}

				} else if (reference.contains("Tested as SID")) {
					pcr.notes = reference;
//					System.out.println(pcr.notes);
				} else {
					citation2 = reference;
					pcr.literatureSource.citation = citation2;
//					System.out.println(citation2);
				}
			}

//			if (citation1!=null && citation2!=null) {
//				System.out.println("citation1="+citation1);
//				System.out.println("citation2="+citation2+"\n");
//			}
//			System.out.println("pcr.notes="+pcr.notes+"\n");
//			if (information.reference.size() > 1) {
//				System.out.println(gson.toJson(pcr.literatureSource));
//			}
		}

		if (information.referenceNumber != null) {
			int refNum = Integer.parseInt(information.referenceNumber);

			Reference reference = htReferences.get(refNum);
			pcr.publicSourceOriginal = new PublicSource();
			pcr.publicSourceOriginal.name = reference.sourceName;
			pcr.publicSourceOriginal.description = reference.description;
			pcr.publicSourceOriginal.url = reference.url;// TODO fix these to remove specific page
			
			if(htCAS.containsKey(information.referenceNumber)) {
				pcr.casReference=htCAS.get(information.referenceNumber);
			} else {
//				System.out.println("cant get cas from ref num:"+information.referenceNumber+"\t"+pcr.cid);
			}

			if(htChemicalName.containsKey(information.referenceNumber)) {
				pcr.chemicalNameReference=htChemicalName.get(information.referenceNumber);	
//				System.out.println(pcr.chemical_name);
			} else {
//				System.out.println("cant get name from ref num:"+pcr.iupacName);
//				pcr.chemical_name=pcr.iupacName;//do we want to use this? doesnt come from original source
			}
			
					
			
//			System.out.println(gson.toJson(reference));
		}
	}

	/**
	 * Creates a new ExperimentalRecord object and sets all the fields that do not
	 * require advanced parsing
	 * 
	 * @return ExperimentalRecord
	 */
	protected ExperimentalRecord toExperimentalRecord(String propertyValueNonSplit) {

	
		ExperimentalRecord er = new ExperimentalRecord();

		er.experimental_parameters = new Hashtable<>();
		er.experimental_parameters.put("PubChem CID", cid);

		er.date_accessed = date_accessed;
		
		if(casReference==null && chemicalNameReference==null) {//use the values mapped to the cid if dont have anything from the reference
			er.smiles=canonSmilesCid;//use pubchem mapped smiles
			er.chemical_name=iupacNameCid;//use pubchem mapped name
//			System.out.println("Using main pubchem identifiers="+er.chemical_name+", smiles="+er.smiles);
//			System.out.println(er.publicSourceOriginal.name);
		} else {
			//use the identifiers from the property value's original reference (if available):
			er.casrn = casReference;				
			er.chemical_name = chemicalNameReference;
			
			//What if have name but no CAS? Are we losing records when creating dataset?
			
//			System.out.println("Using reference identifiers="+er.chemical_name+", cas="+er.casrn);
//			System.out.println("From main pubchem identifiers="+iupacNameCid+", smiles="+canonSmilesCid+"\n");
		}

		// TODO the propertyValue sometimes has a different chemical name inside it but
		// it's too hard to consistently parse it out due to the free formatting
		
		// TODO should we override chemical identifiers by setting DTXSID from dsstox compounds table using pubchem cid???

		if (synonyms != null) {
			er.synonyms = synonyms;
		}
		
		if (propertyName == null)
			return null;
		

		//TODO no way of knowing which pKa is acidic or basic unless they tag it as acidic or amino group		
//		if(propertyName.equals("Dissociation Constants")) {
//			String pv2=propertyValue.replace(" ", "").toLowerCase();
//
//			if(pv2.contains("pkb")) {
//				er.property_name=ExperimentalConstants.str_pKAb;
//			} else if(pv2.contains("kb")) {
//				er.property_name=ExperimentalConstants.str_KAb;
//			} else if (pv2.contains("pk")) {
//				er.property_name=ExperimentalConstants.str_pKA;//dont know whether acidic or basic
//			} else { 
//				System.out.println(er.casrn+"\t"+er.chemical_name+"\t"+ propertyValue);
//				return er;
//			}
//		} else {
//			er.property_name = standardizePropertyName(propertyName);	
//		}

		er.property_name = standardizePropertyName(propertyName);
		
		er.property_value_string = propertyValue;
		er.source_name = RecordPubChem.sourceName;

		boolean foundNumeric = false;
		
		fixPropertyValues();
		
		
		//		if(propertyValue.contains("pH") && !propertyName.equals(ExperimentalConstants.strWaterSolubility) && !propertyName.equals(ExperimentalConstants.strLogKOW) &&  !propertyName.equals(ExperimentalConstants.strAppearance)) {
//			System.out.println(propertyName+"\t"+propertyValue);
//		}
		

		if (er.property_name.equals(ExperimentalConstants.strDensity)
				|| er.property_name.equals(ExperimentalConstants.strVaporDensity)) {

			
			foundNumeric = ParseUtilities.getDensity(er, propertyValue);
			PressureCondition.getPressureCondition(er, propertyValue, sourceName);
			TemperatureCondition.getTemperatureCondition(er, propertyValue);

						
//			if(!foundNumeric)
//				System.out.println("Density\t"+foundNumeric+"\t"+propertyValue);	

			
		} else if (er.property_name == ExperimentalConstants.strMeltingPoint
				|| er.property_name == ExperimentalConstants.strBoilingPoint
				|| er.property_name == ExperimentalConstants.strAutoIgnitionTemperature
				|| er.property_name == ExperimentalConstants.strFlashPoint) {
			foundNumeric = ParseUtilities.getTemperatureProperty(er, propertyValue);
			PressureCondition.getPressureCondition(er, propertyValue, sourceName);
			
//			if(propertyValue.toLowerCase().contains("decomp")) {
//				er.keep=false;
//				er.reason="Decomposes";
////				er.property_value_qualitative="Decomposes";
////				System.out.println("Decomposes: "+propertyValue);
//			}

			if(er.property_name.equals(ExperimentalConstants.strFlashPoint)) {
				String PVLC=propertyValue.toLowerCase();
				
				if((PVLC.contains("close") && PVLC.contains("cup")) || PVLC.contains("c.c.") || PVLC.contains("closed") || PVLC.contains(" cc") || PVLC.contains("(cc)")) {
					er.measurement_method="Closed cup";
				} else if((PVLC.contains("open") && PVLC.contains("cup")) || PVLC.contains("o.c.") || PVLC.contains("OC.") || PVLC.contains(", open") || PVLC.contains(" oc") || PVLC.contains("(oc)")) {
					er.measurement_method="Open cup";
				} else {
//					System.out.println(propertyValue);
				}
			}
			
			
			if(propertyValue.toLowerCase().contains("not flammable")) {
				er.keep=false;
				er.property_value_qualitative="Not flammable";
//				System.out.println("Decomposes: "+propertyValue);
			} else if(propertyValue.toLowerCase().contains("flammable gas")) {
				er.keep=false;
				er.property_value_qualitative="Flammable gas";
			}

			
			if(propertyValue.toLowerCase().contains("sublim")) {
				er.property_value_qualitative="sublimates";
//				System.out.println("Decomposes: "+propertyValue);
			}


		} else if (er.property_name.equals(ExperimentalConstants.strWaterSolubility)) {
			
//			System.out.println("Here1 sol");
			foundNumeric = ParseUtilities.getWaterSolubility(er, propertyValue, sourceName);

			if (er.temperature_C == null && foundNumeric) {
				TemperatureCondition.getTemperatureCondition(er, propertyValue);
			}
			
			pHCondition.get_pH_Condition(er, propertyValue);
			// TODO get pH- difficult because pH can be in difference places, especially
			// when have different solvents in same string

			ParseUtilities.getQualitativeSolubility(er, propertyValue, sourceName);

//			if(er.property_value_qualitative!=null && propertyValue.contains("ethanol") && !foundNumeric)
//				System.out.println(er.property_value_qualitative+"\t"+propertyValue);
			
			
//			System.out.println("Here2 sol");

//			if(er.property_value_point_estimate_original!=null && er.property_value_point_estimate_original<0) {
//				System.out.println("Negative value:"+gson.toJson(er));
//			}

			// TODO note- that ones with qualitative solubility will have keep=false due to
			// missing units
		} else if (er.property_name.equals(ExperimentalConstants.strVaporPressure)) {
			foundNumeric = ParseUtilities.getVaporPressure(er, propertyValue);
			TemperatureCondition.getTemperatureCondition(er, propertyValue);
		} else if (er.property_name == ExperimentalConstants.strHenrysLawConstant) {
			foundNumeric = ParseUtilities.getHenrysLawConstant(er, propertyValue);
			
			TemperatureCondition.getTemperatureCondition(er, propertyValue);
			
//			if(!foundNumeric)
//				System.out.println("HLC\t"+foundNumeric+"\t"+propertyValue);	
			
			
		} else if (er.property_name == ExperimentalConstants.strLogKOW
				|| er.property_name == ExperimentalConstants.str_pKA
				|| er.property_name == ExperimentalConstants.str_pKAa
				|| er.property_name == ExperimentalConstants.str_pKAb) {

			// TMM TODO fix cases with pH since it retrieves the pH instead of the property
			// value:
//			log Kow = -2.82 @ pH 7   ==> 7
//			log Kow: -0.89 (pH 4); -1.85 (pH 7); -1.89 (pH 9)  ==> 9

			// Following one works, but doesnt get the value at pH7:
//			log Kow = 0.74 at pH 5 and -1.34 at pH 7  ==> 0.74 

			foundNumeric = ParseUtilities.getLogProperty(er, propertyValue);
			er.property_value_units_original = ExperimentalConstants.str_LOG_UNITS;
			TemperatureCondition.getTemperatureCondition(er, propertyValue);
			pHCondition.get_pH_Condition(er, propertyValue);
			
		} else if (er.property_name == ExperimentalConstants.strRefractiveIndex) {
			System.out.println("***TODO " + ExperimentalConstants.strRefractiveIndex + "\t" + propertyValue);
		
		} else if (er.property_name == ExperimentalConstants.strViscosity) {
			
//			System.out.println("***TODO " + ExperimentalConstants.strViscosity + "\t" + propertyValue);
			
			foundNumeric = ParseUtilities.getViscosity(er, propertyValue,propertyValueNonSplit);
//			ParseUtilities.getPressureCondition(er, propertyValue, sourceName);
			TemperatureCondition.getTemperatureCondition(er, propertyValue);
			
			String pvLC=propertyValue.toLowerCase();
			
			if(pvLC.contains("@ boiling point") || pvLC.contains("at boiling point")) {
				er.reason="Value @ boiling point";
				er.keep=false;
			}
			
			if(pvLC.contains("@ melting point") || pvLC.contains("at melting point")) {
				er.reason="Value @ melting point";
				er.keep=false;
			}

			
			
			if(pvLC.contains("gas") || pvLC.contains("vapor")) {
				er.reason="Gas viscosity";
				er.keep=false;
			}
			
			if(pvLC.contains("%")|| pvLC.contains("soln") || pvLC.contains("solution")) {
				er.keep=false;
				er.reason="Solution";
			}

			
			
		} else if (er.property_name == ExperimentalConstants.strSurfaceTension) {
//			System.out.println("***TODO " + ExperimentalConstants.strSurfaceTension + "\t" + propertyValue);			
			foundNumeric = ParseUtilities.getSurfaceTension(er, propertyValue);
//			ParseUtilities.getPressureCondition(er, propertyValue, sourceName);
			TemperatureCondition.getTemperatureCondition(er, propertyValue);

			
		} else if (er.property_name == ExperimentalConstants.strAppearance
				|| er.property_name == ExperimentalConstants.strOdor) {
			er.property_value_string = propertyValue;
			
//			er.property_value_qualitative = propertyValue.toLowerCase().replaceAll("colour", "color")
			
			er.property_value_qualitative = propertyValue.replace("colour", "color")
					.replace("odour", "odor").replace("vapour", "vapor");
			
			er.property_value_units_original = ExperimentalConstants.strTEXT;
			er.property_value_units_final = ExperimentalConstants.strTEXT;

		} else {
			System.out.println("Need to handle propertyValue for " + er.property_name);
		}

//		if (!er.property_name.equals(ExperimentalConstants.strWaterSolubility)
//				&& propertyValue.toLowerCase().contains("decomposes")) {
//			er.updateNote(ExperimentalConstants.str_dec);
//		}
		
//		if (propertyValue.toLowerCase().contains("est") && !propertyValue.toLowerCase().contains("ester")
//				&& !propertyValue.toLowerCase().contains("test")) {
//			// TODO is above if statement bulletproof?
//			er.updateNote(ExperimentalConstants.str_est);
//			er.keep = false;
//			er.reason = "Estimated";
//			System.out.println("Estimated:"+propertyValueNonSplit);
//		}
		
		
		if ((propertyValue.toLowerCase().contains("from experimentally derived coefficients"))) {
//			er.updateNote("Estimated from experimentally fit equation");
			er.updateNote(ExperimentalConstants.str_ext);
//			System.out.println("extrapolated from exp eqn:"+propertyValueNonSplit);
		} else if (propertyValueNonSplit.toLowerCase().contains("est vp/ws") 
				|| propertyValueNonSplit.toLowerCase().contains("est from vp/wsol")
				|| propertyValueNonSplit.toLowerCase().contains("estimated, vp/wsol")) {
			
			//OK
		} else if (propertyValueNonSplit.toLowerCase().contains("calcul")
				|| propertyValueNonSplit.toLowerCase().contains("estimat")
				|| propertyValueNonSplit.toLowerCase().contains("(est")
				|| propertyValueNonSplit.toLowerCase().contains("/est")
				|| propertyValueNonSplit.toLowerCase().contains("(calc")) {
			// TODO is above if statement bulletproof?
//			er.updateNote(ExperimentalConstants.str_est);
			er.keep = false;
			er.reason = "Estimated";
//			System.out.println("calculated:"+propertyValueNonSplit);
		
		} else if (propertyValueNonSplit.toLowerCase().contains("est") && !propertyValue.toLowerCase().contains("ester")) {			
//			System.out.println(propertyValueNonSplit);
		} else if (propertyValueNonSplit.toLowerCase().contains("calc")) {			
//			System.out.println(propertyValueNonSplit);			
		} else if ((propertyValueNonSplit.toLowerCase().contains("extrap"))) {
			er.updateNote(ExperimentalConstants.str_ext);
//			System.out.println("extrapolated:"+propertyValueNonSplit);
		}
		
		
		

		// Warns if there may be a problem with an entry
		if (propertyValue.contains("?")) {
			er.flag = true;
//			er.keep=false;
			er.updateNote("Question mark");
		}

		if(er.keep) {
			if ((foundNumeric || er.property_value_qualitative != null || er.note != null)) {

				if(er.reason!=null) {
					System.out.println("reason: "+er.reason+"\tflag: "+er.flag+"\tpV:"+er.property_value_string+"\tNote: "+er.note);
				}
				
//				er.reason = null;
				
			} else {

				er.keep = false;
				
				if(er.reason!=null) {
					System.out.println(er.reason);
				}
				
//				if(er.reason==null) System.out.println("Prev reason:"+er.reason);
				er.reason = "Bad data or units";

//				if(er.reason!=null) {
//					System.out.println(er.reason);
//				}

			}
		}
		
		
		if (publicSourceOriginal != null) {

			er.publicSourceOriginal = publicSourceOriginal;
			er.original_source_name=publicSourceOriginal.name;
			er.url = publicSourceOriginal.url;//store direct link in url instead

			if (publicSourceOriginal.name.equals("EPA DSSTox")) {
				er.keep = false;
				er.reason = "EPIsuite duplicate";
			} else if (publicSourceOriginal.name.equals("Sanford-Burnham Center for Chemical Genomics")) {
				er.keep = false;
				er.reason = "source data not retrievable";
			}
		}

		if (literatureSource != null) {
			
//			if (literatureSource.doi != null)
//				System.out.println(gson.toJson(literatureSource));
			
			er.literatureSource = literatureSource;
			er.reference = literatureSource.citation;
		}


		if(propertyValue.toLowerCase().contains("decomp")) {
			er.property_value_qualitative="decomposes";
			er.property_value_point_estimate_final=null;
			er.property_value_units_final=ExperimentalConstants.strTEXT;
		
		} else if(er.property_value_units_final==null && er.property_value_qualitative!=null) {
			//TODO what happens if have both quantitative and qualitative?
			er.property_value_units_final=ExperimentalConstants.strTEXT;
		} else {
			unitConverter.convertRecord(er);	
		}

				
		if(er.property_value_qualitative!=null) {//TODO is this bullet proof?
			er.keep=true;
			er.reason=null;
		}
		
		if(er.keep && er.property_value_qualitative==null && er.property_value_point_estimate_final==null 
				&& er.property_value_max_final==null && er.property_value_min_final==null ) {

			if(er.reason!=null && er.reason.equals("Incorrect property")) {
				// do nothing
			} else {
				er.keep=false;
				er.reason="No values";
//				er.updateNote("parsed propertyValue: "+er.property_value_string);
			}
			
		}

//		if(propertyValue.contains("pH") && (propertyValue.contains("@") || propertyValue.contains("log Kow"))) {
//		if(er.reference!=null && er.reference.equals("MacBean C, ed; The e-Pesticide Manual, 15th ed., Version 5.0.1. Surrey UK, British Crop Protection Council. Spirodiclofen (148477-71-8) (2010)")) {
//			System.out.println(propertyValue+"\tpoint_estimate="+er.property_value_point_estimate_original);
////			System.out.println(er.reference+"\n");
//		}
		


		return er;
	}

	/**
	 * Fixing the property value strings that would be difficult to reliably fix via regex
	 * 
	 */
	private void fixPropertyValues() {
		propertyValue = propertyValue.replaceAll("(?i)greater than", ">");
		propertyValue = propertyValue.replaceAll("(?i)less than", "<");
		propertyValue = propertyValue.replaceAll("(?i) or equal to ", "=");
		propertyValue = propertyValue.replaceAll("(?i)about ", "~");
		
		propertyValue = propertyValue.replace("70-80 �C at 2-5X10-5 mm Hg","70-80 �C at 3.5e-5 mm Hg");
		propertyValue = propertyValue.replace("175-177 �C/0.85 mm Hg","175-177 �C @ 0.85 mm Hg");
		propertyValue = propertyValue.replace("boiling point: 61-63 �c (51 mm hg)","61-63 �c @ 51 mm hg");
		propertyValue = propertyValue.replace("Distills above 360 �C with partial anhydride formation. BP: 286.5 �C at 100 mm Hg","BP: 286.5 �C at 100 mm Hg");
		//Need programatic way of identifying and removing commas
		propertyValue = propertyValue.replace("2,927 �C","2927 �C");
		propertyValue = propertyValue.replace("3,600 �C","3600 �C");
		propertyValue = propertyValue.replace("10,701 �F","10701 �F");
		propertyValue = propertyValue.replace("2,861 �C","2861 �C");
		propertyValue = propertyValue.replace("1,184 �C","1184 �C");
		propertyValue = propertyValue.replace("2,550 �C","2550 �C");
		propertyValue = propertyValue.replace("2,075 �C","2075 �C");
		propertyValue = propertyValue.replace("/Melting point is/ 197 �C (metastable phase). High melting form sublimes at 190-200 �C (0.2 mm pressure at 2 mm distance).","/melting point is/ 197 �c");
//		List <String>CommaNumbers=Arrays.asList("2,927 �C", "3,600 �C", "10,701 �F", "2,861 �C", "1,184 �C", "2,550 �C", "2,075 �C");
		propertyValue = propertyValue.replace("0.799 at 140 �F (70% sol), 0.933 at 20 �C", "0.933 at 20 �C");
		propertyValue = propertyValue.replace("151,5-154,0 �C","151.5-154.0 �C");
		propertyValue = propertyValue.replace("-15..2 �C, 258 K, 5 �F"," -15.2�C, 258 K, 5 �F");
		propertyValue = propertyValue.replace("185,0-190,0 �C","185.0-190.0 �C");
		propertyValue = propertyValue.replace("1,184 �C","1184 �C");
		propertyValue = propertyValue.replace("-42,5 �C","-42.5 �C");
		propertyValue = propertyValue.replace("strong at 3-10 ppm. [ACGIH] 10 �F","10 �F");
		propertyValue = propertyValue.replace("... by the capillary rise method, range from 36.75 dynes/cm (10% soln) to 22.08 dynes/cm (0.1% soln).","... by the capillary rise method, range from (10% soln) 36.75 to 22.08 dynes/cm (0.1% soln).");
		propertyValue = propertyValue.replace("1.1416 at 20 �C g/cu cm" , "1.1416 at 20 �C");
		propertyValue = propertyValue.replace("Sp Gr: 1.63 at 61/4 �C", "Sp Gr: 1.63 at 4 �C");
		propertyValue = propertyValue.replace("0.916@76 �F", "0.916 @ 76 �F");
		propertyValue = propertyValue.replace("Density  (at 0-4 �C): 0.6 g/cm^3", "0.6 g/cm^3 at 0-4 �C");
		propertyValue = propertyValue.replace("0.8789(20°)", "0.8789 (20�C)");
		propertyValue = propertyValue.replace("0.870(15.5°)", "0.870 (15.5�C)");
		propertyValue = propertyValue.replace("0.87505(15°)", "0.87505 (15�C)");
		propertyValue = propertyValue.replace("log Kow > 5 (pH 4-5, 20-25 �C)", "log Kow > 5 (20-25 �C, pH 4-5)");
		propertyValue = propertyValue.replace("log Kow = 3.8-4.1 (pH 6-7, 20-25 �C)", "log Kow = 3.8-4.1 (20-25 �C, pH 6-7)");
		propertyValue = propertyValue.replace("log Kow = 2.5-3.2 (pH 9-10, 20-25 �C)", "log Kow = 2.5-3.2 (20-25 �C, pH 9-10)");
		propertyValue = propertyValue.replace("24X10-5 to 30X10-5 (5.8X10-6 to 7.3X10-6 atm-cu m/mol) at 37 �C", "6.55X10-6 atm-cu m/mol at 37 �C");
		propertyValue = propertyValue.replace("8.5X10-12 to 4.1X10-8 Pa-cu m/mol (Aminoglycosides) (etc)", "2.05X10-8 Pa-cu m/mol (Aminoglycosides) (etc)");
		propertyValue = propertyValue.replace("The relative density will be between the bulk density and its density in molten form: 600 < D < 960 kg/cu m", "960 kg/cu m");
		//Vapor Pressure
		propertyValue = propertyValue.replace("3.0X01-2 mm Hg at 25 �C (extrapolated)", "3.0X10-2 mm Hg at 25 �C (extrapolated)");
		propertyValue = propertyValue.replace("Vapor pressure: 74-76 deg/40 mm 85% technical grade mixture", "Vapor pressure: 40 mm at 74-76 deg/ 85% technical grade mixture");
		propertyValue = propertyValue.replace("1.7X10+6 Pa at 21 �C (12.8 mm Hg at 21.1 �C)", "1.7X10+6 Pa at 21 �C");
		propertyValue = propertyValue.replace("% in saturated air at 25 �C: 0.0026. 1 ppm = 7.29 mg/cu m; 1 mg/L = 137.2 ppm at 25 �C 760 mm Hg. VP: less than 0.01 mm Hg at 25 �C", "VP: less than 0.01 mm Hg at 25 �C");
		propertyValue = propertyValue.replace("Yellow to tan crystalline solid with characteristic vegetable odor. MP 189-191 �C. Insoluble in water, slightly soluble in alcohols, and soluble in acetone, chorobenzene, and 1,2-dichloroethane. VP: 2X10-8 mm Hg at 25 �C. /Technical/", "VP: 2X10-8 mm Hg at 25 �C");
		propertyValue = propertyValue.replace("1.6X10-9 mm Hg at 25 (extrapolated)", "VP = 1.6X10-9 mm Hg at 25 (extrapolated)");
		//Water Solubility
		propertyValue = propertyValue.replace("White crystals, mp 262-263 �C. Solubility in water: 20 g/100mL. Freely soluble in methanol", "Solubility in water: 20 g/100mL. Freely soluble in methanol");
		propertyValue = propertyValue.replace("White to off-white powder. MP 120-122 �C. Solubility in water: 792 mg/mL /Lisdexamfetamine dimethanesulfonate/", "Solubility in water: 792 mg/mL /Lisdexamfetamine dimethanesulfonate/");
		propertyValue = propertyValue.replace("Crystals. Six-sided plates, monoclinic or triclinic, mp 153-156 �C. One gram dissolves in 1 ml water and in 30 ml alcohol. Slightly sol in chloroform. Almost insoluble in ether. The pH of a 0.1 M aqueous solution is 6.0. /Hydrochloride/", "One gram dissolves in 1 ml water");
		propertyValue = propertyValue.replace("mp 78-81 �C. Solubility (room temperature): 918 mg/l water", "Solubility (room temperature): 918 mg/l water");
		propertyValue = propertyValue.replace("Exists as a dihydrate at room temperature, crystals, mp 156-163 �C. Anhydrous form mp approximately 190 �C. Slightly bitter taste. Freely soluble in water (~1 g/1 ml water). Solubility in 95% ethanol: 0.42 g/ 100 ml. Sparingly soluble in benzene, chloroform. Practically insoluble in ether. The pH of a 2-5% aqueous solution may vary from 4.5 to 3.0. /Chloride/", "Exists as a dihydrate at room temperature, crystals. Slightly bitter taste. Freely soluble in water (~1 g/1 ml water). Solubility in 95% ethanol: 0.42 g/ 100 ml. Sparingly soluble in benzene, chloroform. Practically insoluble in ether. The pH of a 2-5% aqueous solution may vary from 4.5 to 3.0. /Chloride/");
		propertyValue = propertyValue.replace("Amorphous, hygroscopic, white powder. Mp 169.0-171.2 �C. Solubility in water: approx 500 mg/mL. Similarly soluble in methanol, ethanol,; sparingly soluble in chloroform. /21-Sodium succinate/", "Amorphous, hygroscopic, white powder. Solubility in water: approx 500 mg/mL. Similarly soluble in methanol, ethanol,; sparingly soluble in chloroform. /21-Sodium succinate/");
		propertyValue = propertyValue.replace("Soluble in ethanol and oils, insoluble in water", "insoluble in water");
		propertyValue = propertyValue.replace("Miscible with many lacquer solvents, diluents, oils, slightly soluble in water", "slightly soluble in water");
		propertyValue = propertyValue.replace("Freely soluble in glacial acetic acid, slightly soluble in methanol, very slightly soluble in water, and practically insoluble in ethanol.", "very slightly soluble in water");
		propertyValue = propertyValue.replace("FREELY SOL IN WATER, LESS SOL IN ALCOHOL, SPARINGLY SOL IN ACETONE /HYDROCHLORIDE/", "FREELY SOL IN WATER");
		propertyValue = propertyValue.replace("Mol wt 296.84. Crystals, dec 300-303 �C. Freely sol in water, alcohol. Practically insol in ether, chloroform, benzene /Hydrochloride/", "Freely sol in water");
		propertyValue = propertyValue.replace("It is freely soluble in ethanol, soluble in hydrochloric acid, slightly soluble in water, and very slightly soluble in sodium hydroxide.", "slightly soluble in water");
		propertyValue = propertyValue.replace("Practically insoluble in water. One gram dissolves in about 16 ml 95% ethanol", "Practically insoluble in water");
		propertyValue = propertyValue.replace("In water, 4.2X10+5 to 5.95X10+5 mg/L at 20 �C", "In water, 5.08X10+5 mg/L at 20 �C");
		propertyValue = propertyValue.replace("Crystals. Bitter taste. mp 194-198 �C. Sol in water. Sparingly sol in methanol. Practically insol in benzene, ether. pH (2% aq soln): 4.5. /Phosphate/","Crystals. Bitter taste. Sol in water. Sparingly sol in methanol. Practically insol in benzene, ether. pH (2% aq soln): 4.5. /Phosphate/");
		propertyValue = propertyValue.replace("Mol wt 301.82. Bitter crystals producing temporary numbness of the tongue. mp 237-241 �C. UV max: 242 nm (E 1% 1cm = 495 to 515); min 222 nm. One gram dissolves in 40 ml water, in 25 ml alc. Practically insol in ether, benzene, chloroform. pH (1% aq soln): 6.3. /Hydrochloride/","Mol wt 301.82. Bitter crystals producing temporary numbness of the tongue. UV max: 242 nm (E 1% 1cm = 495 to 515); min 222 nm. One gram dissolves in 40 ml water, in 25 ml alc. Practically insol in ether, benzene, chloroform. pH (1% aq soln): 6.3. /Hydrochloride/");
		propertyValue = propertyValue.replace("Soluble in ethanol and most fixed oils, insoluble in glycerol, propylene glycol and water","insoluble in glycerol, propylene glycol and water");
		propertyValue = propertyValue.replace("Soluble in ethanol and fixed oils, insoluble in glycerol, propylene glycol and water","insoluble in glycerol, propylene glycol and water");
		propertyValue = propertyValue.replace("Soluble in ether, insoluble in water","insoluble in water");
		propertyValue = propertyValue.replace("Slightly soluble in propylene glycol, insoluble in glycerol and water","insoluble in glycerol and water");
		propertyValue = propertyValue.replace("Dark bluish-green powder. Solubility in water, 30 mg/mL at 25 �C; solubility in ethylene glycol monomethyl ether 30 mg/mL; in ethanol 7 mg/mL. UV max 635 nm in water /Acid Blue 1/","Dark bluish-green powder. Solubility in water, 30 mg/mL at 25 �C;");
		propertyValue = propertyValue.replace("In aqueous media with a pH 1.1-7.8, axitinib has a solubility of over 0.2 ?g/mL.","In aqueous media with a pH between 1.1 to 7.8, axitinib has a solubility of over 0.2 g/mL.");
		propertyValue = propertyValue.replace("Toluene - 4.0X10+3. Water - 2.5 ppm at 22 �C","Water - 2.5 ppm at 22 �C");
		propertyValue = propertyValue.replace("Water pH 9 2-3 (mg/mL)","Water 2-3 (mg/mL) at pH 9");

		
		propertyValue = propertyValue.replace("PPM @ 25 �C: ACETONE 16,800, BENZENE 500, XYLENE 400, DIMETHYLFORMAMIDE 50,300, WATER 230,800, ISOPROPANOL 20,900, METHYLETHYL KETONE 5,900","230800 ppm @ 25 �C");
		propertyValue = propertyValue.replace("Solubility (mg/ml): propylene glycol 75, methanol 44, ethanol 29, 2-propanol 6.7, dimethylsulfoxide 6.5, water 2.2, chloroform 0.5, acetone <0.5, ethyl acetate <0.5, diethyl ether <0.5, benzene <0.5, acetonitrile <0.5.","2.2 mg/mL");
		propertyValue = propertyValue.replace("Solubility (mg/mL): water 83, ethanol (200 proof) 26, propylene glycol 93, ethanol (190 proof) >100, methanol >100, 2-propanol 4.6, ethyl acetate 1.0, DMF >100, methylene chloride >100, hexane 0.001; Freely soluble in chloroform, practically insoluble in ether /Verapamil hydrochloride/", "83 mg/mL");
		
		propertyValue = propertyValue.replace("In an experiment performed under inert gas atmosphere, the solubility /in water/ of freshly precipitated chromium(III) hydroxide was determined from under-saturation and from over-saturation at different pH. As an amphoteric hydroxide, the solubility curve of chromium(III) hydroxide is parabolic, depending on pH. The solubility is as follows: >pH 4: moderately soluble; pH 6.8 - 11.5: insoluble; > pH 11.5 - 14 slightly soluble ... at pH 4, the solubility was about 520 mg/L; at pH 6.8 to 11.8, the solubility was about 0.005 mg/L","The solubility is as follows: >pH 4: moderately soluble; insoluble at pH 6.8 - 11.5; slightly soluble at pH 11.5 - 14; at pH 4, the solubility was about 520 mg/L; the solubility was about 0.005 mg/L at pH 6.8 to 11.8");
	
		
		propertyValue = propertyValue.replace("log Kow > 5 (pH 4-5, 20-25 �C)","log Kow > 5 @ pH 4.5 and 22.5�C");
		propertyValue = propertyValue.replace("log Kow = 3.8-4.1 (pH 6-7, 20-25 �C)","log Kow = 3.8-4.1 @ pH 6.5 and 22.5�C)");
		propertyValue = propertyValue.replace("log Kow = 2.5-3.2 (pH 9-10, 20-25 �C)","");

	
	
	}

	/**
	 * Convert pubchem names to our db name
	 * 
	 */
	private String standardizePropertyName(String propertyName) {
		if (propertyName.equals("Physical Description") || propertyName.equals("Color/Form")) {
			return ExperimentalConstants.strAppearance;
		} else if (propertyName.equals("Odor")) {
			return ExperimentalConstants.strOdor;
		} else if (propertyName.equals("Boiling Point")) {
			return ExperimentalConstants.strBoilingPoint;
		} else if (propertyName.equals("Autoignition Temperature")) {
			return ExperimentalConstants.strAutoIgnitionTemperature;
		} else if (propertyName.equals("Refractive Index")) {
			return ExperimentalConstants.strRefractiveIndex;
		} else if (propertyName.equals("Flash Point")) {
			return ExperimentalConstants.strFlashPoint;
		} else if (propertyName.equals("Vapor Pressure")) {
			return ExperimentalConstants.strVaporPressure;
		} else if (propertyName.equals("Melting Point")) {
			return ExperimentalConstants.strMeltingPoint;
		} else if (propertyName.equals("Solubility")) {
			return ExperimentalConstants.strWaterSolubility;// may be any solvent though!
		} else if (propertyName.equals("Henry's Law Constant")) {
			return ExperimentalConstants.strHenrysLawConstant;
		} else if (propertyName.equals("Density")) {
			return ExperimentalConstants.strDensity;
		} else if (propertyName.equals("Vapor Density")) {
			return ExperimentalConstants.strVaporDensity;
		} else if (propertyName.equals("Viscosity")) {
			return ExperimentalConstants.strViscosity;
		} else if (propertyName.equals("LogP")) {
			return ExperimentalConstants.strLogKOW;
		} else if (propertyName.equals(ExperimentalConstants.str_pKA)) {
			return ExperimentalConstants.str_pKA;
		} else if (propertyName.equals(ExperimentalConstants.str_pKAa)) {
			return ExperimentalConstants.str_pKAa;
		} else if (propertyName.equals(ExperimentalConstants.str_pKAb)) {
			return ExperimentalConstants.str_pKAb;
		} else if (propertyName.equals("Surface Tension")) {
			return ExperimentalConstants.strSurfaceTension;
		} else {
			System.out.println("In standardizePropertyName() need to handle\t" + propertyName);
			return null;
		}
	}

	/***
	 * This info is from pubchem cid and not the original source
	 * 
	 * @param rs
	 * @param pcr
	 * @throws SQLException
	 */
	private static void addIdentifiers(ResultSet rs, RecordPubChem pcr) throws SQLException {
		String identifiers = rs.getString("identifiers");
		IdentifierData identifierData = gson.fromJson(identifiers, IdentifierData.class);

		if (identifierData != null) {
			Property identifierProperty = identifierData.propertyTable.properties.get(0);
			pcr.iupacNameCid = identifierProperty.iupacName;
			pcr.canonSmilesCid = identifierProperty.canonicalSMILES;
		}

		if (rs.getString("synonyms") != null)
			pcr.synonyms = rs.getString("synonyms").replaceAll("\r\n", "|");
		
	}

	private static Hashtable<Integer, Reference> getReferenceHashtable(Data experimentalData) {
		Hashtable<Integer, Reference> htReferences = null;
		if (experimentalData.record.reference != null) {
			htReferences = new Hashtable<>();
			for (Reference reference : experimentalData.record.reference) {
				int refNum = Integer.parseInt(reference.referenceNumber);
				htReferences.put(refNum, reference);
			}
		}
		return htReferences;
	}

	static void getCidsWithPropertyData() {

		Hashtable<String, String> htCIDs = ParsePubChem.getCID_HT();

		try {
			String folder = "C:\\Users\\TMARTI02\\OneDrive - Environmental Protection Agency (EPA)\\0 java\\0 model_management\\ghs-data-gathering\\data\\experimental\\PubChem_2024_03_20\\";
			FileWriter fw = new FileWriter(folder + "pubchem cids with data.txt");

			int counter = 0;

			for (String pubchemCID : htCIDs.keySet()) {

				counter++;

				String experimentalURL = "https://pubchem.ncbi.nlm.nih.gov/rest/pug_view/data/compound/" + pubchemCID
						+ "/JSON?heading=Experimental+Properties";

				try {
//					String experimental = FileUtilities.getText_UTF8_Line(experimentalURL);

					URL url = new URL(experimentalURL);
					HttpURLConnection huc = (HttpURLConnection) url.openConnection();

					int responseCode = huc.getResponseCode();

//					System.out.println(pubchemCID+"\t"+responseCode);

					if (responseCode == 200)
						fw.write(pubchemCID + "\t1\r\n");
					else
						fw.write(pubchemCID + "\t0\r\n");

//					System.out.println(experimental);

				} catch (Exception ex) {
					fw.write(pubchemCID + "\t0\r\n");
				}
//				Thread.sleep(100);			
				fw.flush();

				if (counter % 10 == 0)
					System.out.println(counter);
			}

			fw.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
	
	
	
	
	
	
	
	

	



	
	
	
	
	
	public static void main(String[] args) {
//		Vector<RecordDashboard> drs = DownloadWebpageUtilities.getDashboardRecordsFromExcel("Data" + "/PFASSTRUCT.xls");
//		Vector<String> cids = getCIDsFromDashboardRecords(drs,"Data"+"/CIDDICT.csv",1,8164);

//		List<String> cidsList = gov.epa.QSAR.utilities.FileUtilities.readFile("Data\\Experimental\\PubChem\\solubilitycids.txt");
//		List<String> cidsList = gov.epa.QSAR.utilities.FileUtilities
//				.readFile("Data\\Experimental\\PubChem\\solubilitycids-test.txt");

		// TMM get data using cids from gabriels sqlite
		RecordPubChem r = new RecordPubChem();
		
		
//		r.loadIdentifiers(folder, conn);
		

		//old way get from prev db:
//		HashSet<String> cids = r.getCidsInDatabase("Pubchem");// old ones from 2020

		//New way get from annotation jsons:
//		GetCIDsFromProperty g=new GetCIDsFromProperty();
//		String folder="data\\experimental\\"+r.sourceName+"\\json\\";
//		HashSet<Long>cids=g.getCidsFromFolder(folder);

//		downloadJSONsToDatabase(cids, true);
//		downloadJSONsToDatabase(cids, false);

//		Vector<RecordPubChem>recs=parseJSONInDatabase("62695");//trans-2-butene
//		Vector<RecordPubChem>recs=parseJSONInDatabase("643835");//cis-2-hexene
//		Vector<RecordPubChem>recs=parseJSONInDatabase("241");//trans-2-butene
		
//		printPhyschemRecords(recs);

		
		
//		getCidsWithPropertyData();
	}

	private static void printPhyschemRecords(Vector<RecordPubChem> recs) {
		for (RecordPubChem rec:recs) {
			
			if(!rec.propertyName.equals("Solubility") &&
					!rec.propertyName.equals("Boiling Point") && 
					!rec.propertyName.equals("Melting Point") &&
					!rec.propertyName.equals("LogP") &&
					!rec.propertyName.equals("Henry's Law Constant") &&
					!rec.propertyName.equals("Vapor Pressure")) continue; 
			
			ExperimentalRecord er=rec.toExperimentalRecord(rec.propertyValue);
			
			if(rec.markupChemicals==null) continue;
			
			System.out.println(gson.toJson(rec)+"\n");
			System.out.println(gson.toJson(er)+"\n\n**********************\n");
			
		}
	}

	
	public void printObject(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(gson.toJson(object));
	}
}

package gov.epa.exp_data_gathering.parse.EPISUITE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.math3.analysis.function.Exp;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.io.iterator.IteratingSDFReader;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.AtomicProperties;

//import gov.epa.TEST.Descriptors.DescriptorUtilities.AtomicProperties;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.UnitConverter;

public class RecordEpisuiteISIS {

	String CAS;
	String Name;
	String Smiles;	
	
//	Double WS_LogMolar;	
//	Double WS_mg_L;
//	Double HL = null;
//	Double VP;
//	Double MP;
//	Double BP;
//	Double KOW;
//	Double BCF;
//	Double KOA;
//	Double LogKoc;
//	Double Km;
//	Double BioHC;
//	Double RBIODEG;
	String Dataset;
	
	
	String propertyName;
	Double propertyValue;
	String propertyUnits;
	Double propertyValueMin;
	Double propertyValueMax;
	
	
	Double Temperature;
	String DataSet;
	String Reference;
	Double pH;
	
	Double WS_LogMolarCalc;
	
	String dataType;
	String note;
	
	static final String sourceName=ExperimentalConstants.strSourceEpisuiteISIS;

	
	static final UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");
	
	
	ExperimentalRecord toExperimentalRecord() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");  
		Date date = new Date();  
		String strDate=formatter.format(date);
		String dayOnly = strDate.substring(0,strDate.indexOf(" "));

		ExperimentalRecord er = new ExperimentalRecord();

		
		er.date_accessed = dayOnly;
		er.temperature_C = Temperature;
		er.pH=pH+"";
		er.casrn=ParseUtilities.fixCASLeadingZero(CAS);		
		er.chemical_name = Name;
		er.smiles=Smiles;

		
//		if (r.KOW != null) {
//			keep=true;
//			er.keep=true;
//			er.property_name = ExperimentalConstants.strLogKOW;
//			er.property_value_string = String.valueOf(r.KOW);
//			ParseUtilities.getLogProperty(er, er.property_value_string);
//
//			// references having pH values included
//			if (r.Reference.toLowerCase().contains("ph")) {
//				String regex = "(.*)(\\;)?pH:(\\s)*(^\\d*\\.\\d+|\\d+\\.\\d*$)";
//				String string = r.Reference;
//				Pattern pattern = Pattern.compile(regex);
//				Matcher matcher = pattern.matcher(string);
//				if (matcher.matches()) {
//					String pHGroup = matcher.group(4);
//					er.pH = pHGroup;
//					String refGroup = matcher.group(1);
//					er.reference = refGroup;
//				}
//
//			}			
//			uc.convertRecord(er);
//		}
//
//
//		if (r.Km != null) {
//			keep=true;
//			er.keep=true;
//			er.property_name = ExperimentalConstants.strLogKmHL;
//			er.property_value_string = String.valueOf(r.Km);
//			ParseUtilities.getLogProperty(er, er.property_value_string);
//			uc.convertRecord(er);
//
//		}
//
//
//		if (r.BioHC != null) {
//			keep = true;
//			er.keep=true;
//			er.property_name = ExperimentalConstants.strLogHalfLifeBiodegradation;
//			er.property_value_string = String.valueOf(r.BioHC);
//			ParseUtilities.getLogProperty(er, er.property_value_string);
//			uc.convertRecord(er);
//
//		}
//
//		if (r.RBIODEG != null) {
//			keep = true;
//			er.keep=true;
//			er.property_name = ExperimentalConstants.strRBIODEG;
//			
//			er.property_value_string = String.valueOf(r.RBIODEG)+" "+ExperimentalConstants.str_binary;
//			
//			er.property_value_units_original=ExperimentalConstants.str_binary;
//			er.property_value_units_final=ExperimentalConstants.str_binary;
//			
//			er.property_value_point_estimate_original=r.RBIODEG;
//			er.property_value_point_estimate_final=r.RBIODEG;
//			
//			er.dataset=r.Dataset;
//			
////			ParseUtilities.getLogProperty(er, er.property_value_string);
//			uc.convertRecord(er);
//
//		}


//		if (r.WS_LogMolar != null || r.WS_LogMolarCalc != null) {
//			er.property_name=ExperimentalConstants.strWaterSolubility;
//			keep = true;
//			er.keep=true;
//			if (r.WS_LogMolar==null) {
//				er.flag=true;
//				er.note="logMolar value is null";
//				System.out.println(er.casrn+"\t"+er.note);
//			} else if (r.WS_LogMolarCalc==null) {
//				er.flag=true;
//				er.note="logMolarCalc value is null";
//				System.out.println(er.casrn+"\t"+er.note);
//			} else {
//				//			System.out.println(er.casrn+"\t"+r.WS_LogMolar+"\t"+r.WS_LogMolarCalc+"\t"+Math.abs(r.WS_LogMolar-r.WS_LogMolarCalc));
//
//				if (Math.abs(r.WS_LogMolar-r.WS_LogMolarCalc)>0.5) {
//					er.keep=false;
//					er.reason="logM value doesnt match value calculated from mg/L value";
//					System.out.println(er.casrn+"\t"+r.WS_LogMolar+"\t"+r.WS_LogMolarCalc);
//				} else if (Math.abs(r.WS_LogMolar-r.WS_LogMolarCalc)>0.1) {
//					er.flag=true;				
//					er.note="logM value ("+r.WS_LogMolar+") doesnt match value calculated from mg/L value ("+r.WS_LogMolarCalc+")";
//				}
//			}
		
		er.property_name=propertyName;

		
		if(propertyValue==null) {
			
			if(propertyValueMin!=null && propertyValueMax!=null) {
				er.property_value_min_original=propertyValueMin;
				er.property_value_max_original=propertyValueMax;
//				System.out.println(er.casrn+"\t"+er.property_value_min_original+" - "+er.property_value_max_original);
				
			} else {
				er.keep=false;
				
				if(note!=null && note.contains("Decomposes")) {
					er.reason=note;
				} else {
					er.reason="property value is null";
				}
				
				System.out.println(er.casrn+"\t missing value\t"+note);
				return er;
			}
			
		} else {
			er.property_value_point_estimate_original=propertyValue;
		}
		
		if(dataType!=null && !dataType.equals("EXP")) {
			er.keep=false;
			er.reason="Data type="+dataType;
			
			if(!dataType.equals("EST")) {
				System.out.println(er.casrn+"\tdataType="+dataType);
			}
			
		}
			
		

		er.property_value_units_original=propertyUnits;

		uc.convertRecord(er);

		er.source_name = ExperimentalConstants.strSourceEpisuiteISIS;
		er.reference=Reference;
		er.url="http://esc.syrres.com/interkow/EpiSuiteData_ISIS_SDF.htm";

//		er.keep = false;
//		er.reason = "Episuite duplicate";
		
		return er;
		
	}

	private static Vector<RecordEpisuiteISIS> getRecords(String filepath,String abbrev){
		Vector<RecordEpisuiteISIS> records = new Vector<>();
		
		File file=new File(filepath);
		
		
		try {
			
			IteratingSDFReader mr = new IteratingSDFReader(new FileInputStream(filepath),DefaultChemObjectBuilder.getInstance());								

			int counter=0;

//			DescriptorFactory df=new DescriptorFactory(false);
			
			while (mr.hasNext()) {
				
				IAtomContainer m=null;

				m = mr.next();

				RecordEpisuiteISIS r=new RecordEpisuiteISIS();
				
				counter++;
				
				r.CAS=m.getProperty("CAS");		
				
				if(r.CAS!=null)r.CAS=r.CAS.trim();
				
				
				if (m.getProperty("NAME")!=null) {
					r.Name=m.getProperty("NAME");
					r.Name=r.Name.trim();
				}
				
				
				r.Smiles=generateSmiles(m);
				
//				String desc=DescriptorsFromSmiles.goDescriptors(r.Smiles);
				

				if(abbrev.equals("WS")) {
					r.propertyName=ExperimentalConstants.strWaterSolubility;
					r.propertyValue=Double.parseDouble(m.getProperty("LogMolar"));
					r.propertyUnits=ExperimentalConstants.str_log_M;
//					System.out.println(counter+"\t"+r.CAS+"\t"+r.propertyValue);
				} else if(abbrev.equals("LKoc")) {
					r.propertyName=ExperimentalConstants.strKOC;
					if(m.getProperty(abbrev)!=null) {
						r.propertyValue=Double.parseDouble(m.getProperty(abbrev));	
					}
					if(m.getProperty("KocRef")!=null) {
						r.Reference=m.getProperty("KocRef");	
					}
					r.propertyUnits=ExperimentalConstants.str_LOG_L_KG;
				} else if (abbrev.contentEquals("HL")) {
					r.propertyName=ExperimentalConstants.strHenrysLawConstant;
					r.propertyUnits=ExperimentalConstants.str_atm_m3_mol;
				} else if (abbrev.contentEquals("VP")) {
					r.propertyName=ExperimentalConstants.strVaporPressure;
					r.propertyUnits=ExperimentalConstants.str_mmHg;
				} else if (abbrev.contentEquals("MP")) {
					r.propertyName=ExperimentalConstants.strMeltingPoint;
					r.propertyUnits=ExperimentalConstants.str_C;
				} else if (abbrev.contentEquals("Kow")) {
					r.propertyName=ExperimentalConstants.strLogKOW;
					r.propertyUnits=ExperimentalConstants.str_LOG_UNITS;
				} else if (abbrev.contentEquals("LogBCF")) {
					r.propertyName=ExperimentalConstants.strBCF;
					r.propertyUnits=ExperimentalConstants.str_LOG_UNITS;
				} else if (abbrev.contentEquals("LogKOA")) {
					r.propertyName=ExperimentalConstants.strLogKOA;
					r.propertyUnits=ExperimentalConstants.str_LOG_UNITS;
				} else if (abbrev.contentEquals("LogKmHL")) {
					r.propertyName=ExperimentalConstants.strKmHL;
				} else if (abbrev.contentEquals("LogHalfLife")) {
					r.propertyName=ExperimentalConstants.strLogHalfLifeBiodegradation;
				} else if (abbrev.contentEquals("BP")) {
					
					r.propertyName=ExperimentalConstants.strBoilingPoint;
					r.propertyUnits=ExperimentalConstants.str_C;
					
					
					if (m.getProperty(abbrev)==null) {
					
					} else if (m.getProperty(abbrev).toString().contains("dec")) {
						r.note="Decomposes";
					} else if (!m.getProperty(abbrev).toString().contains("-")) {
						r.propertyValue = Double.parseDouble(m.getProperty(abbrev));
					
					} else if (m.getProperty(abbrev)!=null && !(m.getProperty(abbrev).toString().contains("dec")) && (m.getProperty(abbrev).toString().contains("-"))) {
						String str = m.getProperty(abbrev);
						int dashIndex = str.indexOf("-");
						
						if (dashIndex != 0) {
							String temp1 = str.substring(0,dashIndex);
							String temp2 = str.substring(dashIndex + 1,str.length());
							double temp1double = Double.parseDouble(temp1);
							double temp2double = Double.parseDouble(temp2);
							
							r.propertyValueMin=temp1double;
							r.propertyValueMax=temp2double;
							// possible else
						} else {
							r.propertyValue = Double.parseDouble(m.getProperty(abbrev));
						}
					}
				} else if(abbrev.contentEquals("RBIODEG")) {
					if (m.getProperty("Biowin56_Obs")!=null) {
						r.propertyValue=Double.parseDouble(m.getProperty("Biowin56_Obs"));
						r.propertyUnits=ExperimentalConstants.str_binary;
					} else {
						continue;
					}
					
					if (m.getProperty("Biowin56_DataSet")!=null) {
						r.Dataset=m.getProperty("Biowin56_DataSet");
					}
					
					
				}
				
				
				List<String>abbrevs=Arrays.asList("HL","VP","MP","Kow","LogBCF","LogKOA","LogKmHL","LogHalfLife");
				
				if(abbrevs.contains(abbrev)) {
					if (m.getProperty(abbrev)!=null) {
						r.propertyValue = Double.parseDouble(m.getProperty(abbrev));
					}
				}
				
				
				r.DataSet=m.getProperty("DataSet");
				
				if(m.getProperty(abbrev+" Reference")!=null) {
					r.Reference=m.getProperty(abbrev+" Reference");	
					if(r.Reference.contains("; pH ")) {
						r.pH=Double.parseDouble(r.Reference.substring(r.Reference.indexOf("; pH")+4,r.Reference.length()));
						r.Reference=r.Reference.substring(0,r.Reference.indexOf("; pH"));
//						System.out.println(r.Reference+"\t"+r.pH);
					}
				}
				
				if (m.getProperty(abbrev+" Temperature")!=null) r.Temperature=Double.parseDouble(m.getProperty(abbrev +" Temperature"));
				
				
				if (m.getProperty(abbrev+" Data Type")!=null) {
					r.dataType=m.getProperty(abbrev+" Data Type");
				}
				
				records.add(r);
				
//				System.out.println(records.size());
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}

	
	private static double Calculate_mw(IAtomContainer m) {
		// tried to use CDK built in methods but they suck
		// alternative method would be to use m2 which includes the hydrogens
		
		try {
			AtomicProperties ap=AtomicProperties.getInstance();
		
			double MW=0;
			
			for (int i=0;i<=m.getAtomCount()-1;i++) {			
				IAtom a=m.getAtom(i);
				
				if (a.getSymbol().contentEquals("Na")) MW+=22.98977; 
				else if (a.getSymbol().contentEquals("K")) MW+=39.0983;
				else if (a.getSymbol().contentEquals("Ca")) MW+=40.08;
				else if (a.getSymbol().contentEquals("Ba")) MW+=137.33;		
				else if (a.getSymbol().contentEquals("U")) MW+=238.029;
				else if (a.getSymbol().contentEquals("Sr")) MW+=87.62;
				else MW+=ap.getMass(a.getSymbol());
								
				MW+=a.getImplicitHydrogenCount()*ap.getMass("H");
				
			}
		
			return MW;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return -9999;
			
	}
	
	public static String generateSmiles(IAtomContainer ac) {
		return generateSmiles(ac, SmiFlavor.Unique);
	}

	
	public static String generateSmiles(IAtomContainer ac, int flavor) {
		try {
			SmilesGenerator sg = new SmilesGenerator(flavor);
			String smiles = sg.create(ac);
			return smiles;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	//TODO add rest of properties from SDFs
	
	
	public static Vector<RecordEpisuiteISIS> getRecordsFromSDFs() {
		Vector<RecordEpisuiteISIS> records = new Vector<>();

		
		String strFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName+File.separator+"EPI_SDF_Data"+File.separator;
		
//		getWaterSolubilityRecords(records, strFolder);
		
		records.addAll(getRecords(strFolder+"EPI_PCKOC_Data_SDF.sdf","LKoc"));
		
//		System.out.println(ReflectionToStringBuilder.toString(records1.get(0)));
//		System.out.println(ReflectionToStringBuilder.toString(records2.get(0)));

//		records.addAll(getRecords(strFolder+"EPI_Henry_Data_SDF.sdf","HL"));
//		records.addAll(getRecords(strFolder+"EPI_VP_Data_SDF.sdf","VP"));
//		records.addAll(getRecords(strFolder+"EPI_Melt_Pt_Data_SDF.sdf","MP"));
//		records.addAll(getRecords(strFolder+"EPI_Boil_Pt_Data_SDF.sdf","BP"));
//		records.addAll(getRecords(strFolder+"EPI_Kowwin_Data_SDF.sdf","Kow"));
//		records.addAll(getRecords(strFolder+"EPI_BCF_Data_SDF.sdf","LogBCF"));
//		records.addAll(getRecords(strFolder+"EPI_KOA_Data_SDF.sdf","LogKOA"));
//		records.addAll(getRecords(strFolder+"EPI_KM_Data_SDF.sdf","LogKmHL"));
//		records.addAll(getRecords(strFolder+"EPI_BioHC_Data_SDF.sdf","LogHalfLife"));
//		records.addAll(getRecords(strFolder+"EPI_Biowin_Data_SDF.sdf","RBIODEG"));
//		

		

		return(records);
	}

	private static void getWaterSolubilityRecords(Vector<RecordEpisuiteISIS> records, String strFolder) {
		Vector<RecordEpisuiteISIS> records1 = getRecords(strFolder+"EPI_WaterFrag_Data_SDF.sdf","WS");
		records.addAll(records1);
		
		Vector<RecordEpisuiteISIS> records2 = getRecords(strFolder+"EPI_Wskowwin_Data_SDF.sdf","WS");

//		// Only add new records from second file:
		for (RecordEpisuiteISIS rec2:records2) {
			boolean haveRec=false;
			for (RecordEpisuiteISIS rec1:records1) {
				if (rec1.CAS.contentEquals(rec2.CAS)) {
//					System.out.println(rec2.CAS+"\t"+rec1.CAS);
					haveRec=true;
					break;
				}
			}
			if (!haveRec) {
//				System.out.println(rec2.CAS);
				records.add(rec2);
			}
		}
	}
	
	
	void getKocData() {
		try {
			
			String strFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName+File.separator+"EPI_SDF_Data"+File.separator;
			String filename="EPI_PCKOC_Data_SDF.sdf";
			String sdfFilePath=strFolder+filename;
			
			try (InputStream sdfInputStream = new FileInputStream(sdfFilePath);
		             Workbook workbook = new XSSFWorkbook()) {

		            // Create a sheet in the workbook
		            Sheet sheet = workbook.createSheet("Chemicals");

		            // Create header row
		            Row headerRow = sheet.createRow(0);
		            
		            int col=0;
		            
		            headerRow.createCell(col++).setCellValue("CAS");
		            headerRow.createCell(col++).setCellValue("Name");
		            headerRow.createCell(col++).setCellValue("Smiles");
		            headerRow.createCell(col++).setCellValue("LogKoc");
		            headerRow.createCell(col++).setCellValue("LogKocRef");
		            
		            // Set up CDK SDF reader
		            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		            IteratingSDFReader sdfReader = new IteratingSDFReader(sdfInputStream, builder);

		            int rowIndex = 1;
		            while (sdfReader.hasNext()) {
		                IAtomContainer molecule = sdfReader.next();
		                
						String cas=molecule.getProperty("CAS");
						while (cas.substring(0,1).equals("0")) cas=cas.substring(1,cas.length());


		                // Create a new row in the sheet
		                Row row = sheet.createRow(rowIndex++);
		                
		                col=0;
		                
		                
		                String Name=molecule.getProperty("NAME");
		                if(Name==null) Name="";
		                
		                String Smiles=molecule.getProperty("Smiles");
		                if(Smiles==null) Smiles="";
		                
		                
		                if (molecule.getProperty("LKoc")==null) continue;
		                
		                Double LogKoc=Double.parseDouble(molecule.getProperty("LKoc"));
		                
		                String LogKocRef=molecule.getProperty("LogKocRef");


		                System.out.println(cas+"\t"+LogKoc);

		                row.createCell(col++).setCellValue(cas != null ? cas : "N/A");
		                row.createCell(col++).setCellValue(Name);
		                row.createCell(col++).setCellValue(Smiles);
		                row.createCell(col++).setCellValue(LogKoc);
		                row.createCell(col++).setCellValue(LogKocRef);
		                
		                
//		                if(acdLogP != null)row.createCell(1).setCellValue(Double.parseDouble(acdLogP));
		            }

		            String excelFilePath=strFolder+"EPI_PCKOC_Data_SDF.xlsx";
		            
		            // Write the workbook to the file
		            try (OutputStream fileOut = new FileOutputStream(excelFilePath)) {
		                workbook.write(fileOut);
		            }

		            System.out.println("Excel file created successfully: " + excelFilePath);

		        } catch (Exception  e) {
		            e.printStackTrace();
		        }
		
		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	void getBCFData() {
		try {
			
			String strFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName+File.separator+"EPI_SDF_Data"+File.separator;
			String filename="EPI_BCF_Data_SDF.sdf";
			String sdfFilePath=strFolder+filename;
			
			try (InputStream sdfInputStream = new FileInputStream(sdfFilePath);
		             Workbook workbook = new XSSFWorkbook()) {

		            // Create a sheet in the workbook
		            Sheet sheet = workbook.createSheet("Chemicals");

		            // Create header row
		            Row headerRow = sheet.createRow(0);
		            
		            int col=0;
		            
		            headerRow.createCell(col++).setCellValue("CAS");
		            headerRow.createCell(col++).setCellValue("Name");
		            headerRow.createCell(col++).setCellValue("Smiles");
		            headerRow.createCell(col++).setCellValue("LogBCF");
		            
		            
		            // Set up CDK SDF reader
		            IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
		            IteratingSDFReader sdfReader = new IteratingSDFReader(sdfInputStream, builder);

		            int rowIndex = 1;
		            while (sdfReader.hasNext()) {
		                IAtomContainer molecule = sdfReader.next();
		                
						String cas=molecule.getProperty("CAS");
						while (cas.substring(0,1).equals("0")) cas=cas.substring(1,cas.length());


		                // Create a new row in the sheet
		                Row row = sheet.createRow(rowIndex++);
		                
		                col=0;
		                
		                
		                String Name=molecule.getProperty("NAME");
		                if(Name==null) Name="";
		                
		                String Smiles=molecule.getProperty("Smiles");
		                if(Smiles==null) Smiles="";
		                
		                
		                if (molecule.getProperty("LogBCF")==null) continue;
		                
		                Double LogBCF=Double.parseDouble(molecule.getProperty("LogBCF"));
		                
		                row.createCell(col++).setCellValue(cas != null ? cas : "N/A");
		                row.createCell(col++).setCellValue(Name);
		                row.createCell(col++).setCellValue(Smiles);
		                row.createCell(col++).setCellValue(LogBCF);
		                
		                
		                
//		                if(acdLogP != null)row.createCell(1).setCellValue(Double.parseDouble(acdLogP));
		            }

		            String excelFilePath=strFolder+filename.replace(".sdf",".xlsx");
		            
		            // Write the workbook to the file
		            try (OutputStream fileOut = new FileOutputStream(excelFilePath)) {
		                workbook.write(fileOut);
		            }

		            System.out.println("Excel file created successfully: " + excelFilePath);

		        } catch (Exception  e) {
		            e.printStackTrace();
		        }
		
		
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main (String[] args) {
		
		RecordEpisuiteISIS recordEpisuiteISIS=new RecordEpisuiteISIS();
//		recordEpisuiteISIS.getKocData();
//		recordEpisuiteISIS.getBCFData();
		
		if(true)return;
		
//		Vector<RecordEpisuiteISIS> records = recordWaterFragmentData();

		String strFolder = "Data"+File.separator+"Experimental"+ File.separator + sourceName+File.separator+"EPI_SDF_Data"+File.separator;
		Vector<RecordEpisuiteISIS> records = getRecords(strFolder+"EPI_Biowin_Data_SDF.sdf","RBIODEG");
//		System.out.println(records.size());
		for (RecordEpisuiteISIS r:records) {
			while (r.CAS.substring(0,1).equals("0")) r.CAS=r.CAS.substring(1,r.CAS.length());
			System.out.println(r.CAS+"\t"+r.propertyValue);
		}
		
		
	}
}


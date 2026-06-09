package gov.epa.exp_data_gathering.parse;

import java.awt.FlowLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import com.google.gson.JsonObject;

import gov.epa.QSAR.utilities.MatlabChart;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.EChemPortal.RecordEChemPortal;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox;
import gov.epa.exp_data_gathering.parse.Parse;

/**
 * @author TMARTI02
 */
public class CompareExperimentalRecords {

	public static class Source {
		public String sourceName;
		public String subfolder;
		public String experimentalRecordsPath;
		
		public Source(String sourceName,String subfolder) {
			this.sourceName=sourceName;
			this.subfolder=subfolder;
			
		}
		
	}

	public CompareMethods cm=new CompareMethods();
	public Comparisons c=new Comparisons();
	public ExperimentalRecordManipulator rm=new ExperimentalRecordManipulator();

	public class ExperimentalRecordManipulator {

		public ExperimentalRecords getAllExperimentalRecords(List<Source> sources) {
			ExperimentalRecords recsAll=new ExperimentalRecords();
			for(Source source:sources) {
				ExperimentalRecords recs=ExperimentalRecords.getExperimentalRecords(source.sourceName, source.subfolder);
				recsAll.addAll(recs);
			}
			return recsAll;
		}
		
		public ExperimentalRecords getAllExperimentalRecords(List<Source> sources,String propertyName) {
			ExperimentalRecords recsAll=new ExperimentalRecords();
			for(Source source:sources) {
				ExperimentalRecords recs=ExperimentalRecords.getExperimentalRecords(source.sourceName, source.subfolder);
				
				for (ExperimentalRecord er:recs) {
					if(!er.property_name.contentEquals(propertyName)) continue;
					recsAll.add(er);	
				}
			}
			return recsAll;
		}

		private TreeMap<String, ExperimentalRecords> getTreeMapByCAS(String propertyName, String units,
				ExperimentalRecords experimentalRecords) {
			TreeMap<String,ExperimentalRecords>ht=new TreeMap<>();
			for(ExperimentalRecord er:experimentalRecords) {
				if(er.casrn==null) continue;
				if(!er.property_name.contentEquals(propertyName)) continue;
				if(ht.containsKey(er.casrn)) {
					ExperimentalRecords recsCAS=ht.get(er.casrn);
					recsCAS.add(er);
				} else {
					ExperimentalRecords recsCAS=new ExperimentalRecords();
					recsCAS.add(er);
					ht.put(er.casrn, recsCAS);
				}
			}
			setMedianValues(ht,units);
			return ht;
		}
		

		private TreeMap<String, ExperimentalRecords> getTreeMapByDTXSID(String propertyName, String units,
				ExperimentalRecords experimentalRecords) {
			TreeMap<String,ExperimentalRecords>ht=new TreeMap<>();
			for(ExperimentalRecord er:experimentalRecords) {
				if(er.dsstox_substance_id==null) continue;
				if(!er.property_name.contentEquals(propertyName)) continue;
				if(ht.containsKey(er.dsstox_substance_id)) {
					ExperimentalRecords recsCAS=ht.get(er.dsstox_substance_id);
					recsCAS.add(er);
				} else {
					ExperimentalRecords recsCAS=new ExperimentalRecords();
					recsCAS.add(er);
					ht.put(er.dsstox_substance_id, recsCAS);
				}
			}
			setMedianValues(ht,units);
			return ht;
		}

		private HashSet<String> updateCountBySourceHashtable(Hashtable<String, Integer> htCountBySource, ExperimentalRecords recs) {


			HashSet<String>sources=new HashSet<>();

			for(ExperimentalRecord er:recs) {
				String sourceName=null;
				if(er.getOriginalSourceName()==null) sourceName="Unknown";
				else sourceName=er.getOriginalSourceName();
				sources.add(sourceName);
			}


			for (String sourceName:sources) {
				if(htCountBySource.containsKey(sourceName)) {
					int oldVal=htCountBySource.get(sourceName);
					htCountBySource.put(sourceName,oldVal+1);
				} else {
					htCountBySource.put(sourceName,1);
				}
			}

			return sources;

		}

		private TreeMap<String,ExperimentalRecords> getHashtable(String sourceName,String subfolder, String propertyName,String units) {


			ExperimentalRecords experimentalRecords = ExperimentalRecords.getExperimentalRecords(sourceName, subfolder);

			int totalCount=experimentalRecords.size();


			TreeMap<String, ExperimentalRecords> ht = rm.getTreeMapByCAS(propertyName, units, experimentalRecords);

			System.out.println(sourceName+"\t"+ht.size()+"\t"+totalCount);

			return ht;
		}

		private ExperimentalRecords getAllExperimentalRecords(List<Source> sources,boolean isbad) {
			ExperimentalRecords recsAll=new ExperimentalRecords();
			for(Source source:sources) {
				if(isbad) {
					ExperimentalRecords recs=ExperimentalRecords.getExperimentalRecordsBad(source.sourceName, source.subfolder);
					recsAll.addAll(recs);
				} else {
					ExperimentalRecords recs=ExperimentalRecords.getExperimentalRecords(source.sourceName, source.subfolder);
					recsAll.addAll(recs);
				}
			}
			return recsAll;
		}

		private static void setMedianValue(ExperimentalRecords recs, List<Double> vals) {
		
			//		System.out.println(recs.get(0).casrn+"\t"+vals.size());
		
			if(vals.size()%2==0) {// even
		
				int middleVal2=vals.size()/2;
				int middleVal1=middleVal2-1;
				recs.medianValue=(vals.get(middleVal1)+vals.get(middleVal2))/2.0;
				
			} else {//odd
				int middleVal=vals.size()/2;
				recs.medianValue=vals.get(middleVal);
			}
		
			//		int counter=0;
			//		for (Double val:vals) {
			//			System.out.println(val+"\t"+counter++);
			//		}
			//		System.out.println(recs.get(0).casrn+"\t"+vals.size()+"\t"+recs.medianValue+"\n");
		
		
		}

		private static void setBinaryScore(ExperimentalRecords recs, List<Double> vals) {
		
			if(vals.size()==0) return;
		
			double avg=0;
			for(Double val:vals) avg+=val;
			avg/=vals.size();
		
			if(avg<=0.2) recs.medianValue=0.0;
			else if (avg>=0.8) recs.medianValue=1.0;
			else {
//				System.out.println(recs.get(0).casrn+"\t"+JsonUtilities.gsonPretty.toJson(recs)+"\n");
				return;
			}
		
			//		System.out.println(recs.get(0).casrn+"\t"+avg);
		
		}

		/**
		 * TODO This method assumes that property has log units as modelable units
		 *  
		 * @param recs
		 * @param units
		 */
		private static void setMedianValue(ExperimentalRecords recs,String units) {
		
			List<Double>vals=new ArrayList<>();
			
//			System.out.println("recs.size()="+recs.size());
		
			for (ExperimentalRecord er:recs) {
				
				if(er.property_value_units_final==null) continue;
				if(!er.property_value_units_final.equals(units)) continue;
				Double val=null;
				
				if(units.equals(ExperimentalConstants.str_binary)) {
					val=er.property_value_point_estimate_final;
				} else {
					if(er.property_value_numeric_qualifier!=null) {
//						if (er.property_value_numeric_qualifier.contains("<") || er.property_value_numeric_qualifier.contains(">")) continue;
						if (!er.property_value_numeric_qualifier.equals("~")) continue;
					}
					if(er.property_value_max_final!=null && er.property_value_min_final!=null) {
						if(Math.abs(Math.log10(er.property_value_min_final/er.property_value_max_final))<1) {
							val=Math.sqrt(er.property_value_max_final*er.property_value_min_final);						
						} else {
							continue;
						}
					} else if(er.property_value_point_estimate_final!=null) {
						val=er.property_value_point_estimate_final;
					} else continue;

					//TODO Following needs to work for any units we give it
					if(!units.toLowerCase().contains("log") && !units.equals(ExperimentalConstants.str_C)) {
						if(val==0.0) continue;
						val=Math.log10(val);
					}					
				}
		
//				System.out.println(er.property_value_units_final+"\t"+units);
				vals.add(val);
//				System.out.println(er.casrn+"\t"+val);
				//			System.out.println(er.property_value_string+"\t"+val);
			}
			
//			System.out.println("vals.size()="+vals.size());

			if (vals.size()>0) {
				Collections.sort(vals);
				
				if(units.equals(ExperimentalConstants.str_binary)) {
					setBinaryScore(recs,vals);
				} else {
					setMedianValue(recs,vals);	
				}
		
			}
		}

		public static void setMedianValues(TreeMap<String,ExperimentalRecords> tm, String units) {
			int count=0;
		
			for (String key:tm.keySet()) {
				ExperimentalRecords recs=tm.get(key);
				setMedianValue(recs,units);
								
//				System.out.println(key+"\t"+recs.medianValue);
				count+=recs.size();
			}
		}

		private void removeByParameter(String parameterName, String parameterValue, ExperimentalRecords recs1) {
			for (int i=0;i<recs1.size();i++) {
				ExperimentalRecord rec=recs1.get(i);
				
				if(rec.experimental_parameters.get(parameterName)==null || 
						!rec.experimental_parameters.get(parameterName).equals(parameterValue)) {
					recs1.remove(i--);
				}
			}
		}

	}

	class Comparisons {

		private void compareREACH_Sources() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("QSAR_Toolbox","Acute toxicity ECHA Reach"));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("eChemPortalAPI","AcuteToxicityOral"));

			cm.compare(sources1, sources2, ExperimentalConstants.strORAL_RAT_LD50,ExperimentalConstants.str_mg_kg,"cas");
		}
		
		
		private void compareWS() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("QSAR_Toolbox","Physchem ECHA Reach"));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("eChemPortal",null));

			cm.compare(sources1, sources2, ExperimentalConstants.strWaterSolubility,ExperimentalConstants.str_g_L,"cas");
		}
		
		private void compareWS2() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("OChem",null));
			sources1.add(new Source("PubChem",null));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("OChem_2024_04_03",null));
			sources2.add(new Source("PubChem_2024_11_27",null));

			printChemicalsInCommon=false;
			
			cm.compare(sources1, sources2, ExperimentalConstants.strWaterSolubility,ExperimentalConstants.str_g_L,"cas");
		}


		private void compareSensitization() {
			List<Source>sources1=new ArrayList<>();
			List<Source>sources2=new ArrayList<>();

			//Compare 2 NIEHS data sources:
			//		sources1.add(new Source("NICEATM",null));
			//		sources2.add(new Source("NIEHS_ICE_2024_08","skin sensitization"));

			//				countWithMedian1=547
			//				countWithMedian2=385
			//				countIn1Not2=229
			//				countIn2Not1=67
			//				Count in common=318
			//				Concordance=1.00
			//				Conclusion: keep NICEATM version


			//		sources1.add(new Source("NICEATM",null));
			//		sources2.add(new Source("QSAR_Toolbox","Sensitization"));

			//				countWithMedian1=547
			//				countWithMedian2=530
			//				countIn1Not2=184
			//				countIn2Not1=167
			//				Count in common=363
			//				Concordance=0.99
			//				Conclusion: keep both


			sources1.add(new Source("NICEATM",null));
			sources2.add(new Source("eChemPortalAPI","SkinSensitisation"));

			//				countWithMedian1=547
			//				countWithMedian2=3984
			//				countIn1Not2=444
			//				countIn2Not1=3881
			//				Count in common=103
			//				Concordance=0.94
			//				Conclusion: keep both

			System.out.println("Source1="+sources1.get(0).sourceName);
			System.out.println("Source2="+sources2.get(0).sourceName+"\n");
			cm.compareChemicalsInCommonConcordance(sources1, sources2,ExperimentalConstants.strSkinSensitizationLLNA,ExperimentalConstants.str_binary);



		}

		private void compareToChemidplusToEcha() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("ChemIDplus_2024_12_04",null));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("QSAR_Toolbox","Acute toxicity ECHA Reach"));
			//		sources2.add(new Source("eChemPortalAPI","AcuteToxicityOral"));

			cm.compare(sources1, sources2, ExperimentalConstants.strORAL_RAT_LD50,ExperimentalConstants.str_mg_kg,"cas");
		}

		void compareQSAR_Toolbox_sources() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("QSAR_Toolbox","Acute toxicity ECHA Reach"));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("QSAR_Toolbox","Acute toxicity oral toxicity db"));

			cm.compare(sources1, sources2, ExperimentalConstants.strORAL_RAT_LD50,ExperimentalConstants.str_mg_kg,"cas");
		}

		private void compareToNIEHS_OralRatLD50() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("ChemIDplus_2024_12_04",null));
			//		sources1.add(new Source("eChemPortalAPI","AcuteToxicityOral"));
			sources1.add(new Source("QSAR_Toolbox","Acute toxicity ECHA Reach"));

			List<Source>sources2=new ArrayList<>();
			sources2.add(new Source("NIEHS_ICE_2024_08","Acute oral"));

			cm.compare(sources1, sources2, ExperimentalConstants.strORAL_RAT_LD50,ExperimentalConstants.str_mg_kg,"cas");
		}

		void lookAtEchemportalLD50_Guidelines() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("eChemportalAPI","AcuteToxicityOral"));


			ExperimentalRecords recs=rm.getAllExperimentalRecords(sources1);

			//		System.out.println(recs.size());

			ExperimentalRecords recs401=new ExperimentalRecords();
			ExperimentalRecords recsNot401=new ExperimentalRecords();

			for (ExperimentalRecord er:recs) {

				if(er.experimental_parameters.containsKey("Guidelines")) {
					String guidelines=er.experimental_parameters.get("Guidelines")+"";

					if(guidelines.contains("401")) recs401.add(er);
					else recsNot401.add(er);
				}

			}

			String propertyName=ExperimentalConstants.strORAL_RAT_LD50;
			String units=ExperimentalConstants.str_mg_kg;

			TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs401);
			TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recsNot401);

			//		System.out.println(tm1.size());
			//		System.out.println(tm2.size());

			System.out.println("countWithMedian1="+cm.getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+cm.getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+cm.getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+cm.getNewChemicalCount(tm2, tm1,false));

			cm.compareChemicalsInCommon(tm1, tm2, units);

		}

		void lookAtEchemportalLD50_Guidelines2() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("eChemportalAPI","AcuteToxicityOral"));


			ExperimentalRecords recs=rm.getAllExperimentalRecords(sources1);

			//		System.out.println(recs.size());

			ExperimentalRecords recs423=new ExperimentalRecords();
			ExperimentalRecords recs420=new ExperimentalRecords();

			for (ExperimentalRecord er:recs) {

				if(er.experimental_parameters.containsKey("Guidelines")) {
					String guidelines=er.experimental_parameters.get("Guidelines")+"";

					if(guidelines.contains("423")) recs423.add(er);
					else if(guidelines.contains("420")) recs420.add(er);

				}

			}

			String propertyName=ExperimentalConstants.strORAL_RAT_LD50;
			String units=ExperimentalConstants.str_mg_kg;

			TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs423);
			TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recs420);


			System.out.println("countWithMedian1="+cm.getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+cm.getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+cm.getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+cm.getNewChemicalCount(tm2, tm1,false));


			cm.compareChemicalsInCommon(tm1, tm2, units);

			System.out.println(tm1.size());
			System.out.println(tm2.size());
		}

		void lookAtEchemportalLD50_Guidelines3() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("eChemportalAPI","AcuteToxicityOral"));

			ExperimentalRecords recs=rm.getAllExperimentalRecords(sources1);
			//		System.out.println(recs.size());

			ExperimentalRecords recs423=new ExperimentalRecords();
			ExperimentalRecords recs425=new ExperimentalRecords();

			for (ExperimentalRecord er:recs) {
				if(er.experimental_parameters.containsKey("Guidelines")) {
					String guidelines=er.experimental_parameters.get("Guidelines")+"";

					if(guidelines.contains("423")) recs423.add(er);
					else if(guidelines.contains("425")) recs425.add(er);

				}

			}

			String propertyName=ExperimentalConstants.strORAL_RAT_LD50;
			String units=ExperimentalConstants.str_mg_kg;

			TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs423);
			TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recs425);


			System.out.println("countWithMedian1="+cm.getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+cm.getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+cm.getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+cm.getNewChemicalCount(tm2, tm1,false));


			cm.compareChemicalsInCommon(tm1, tm2, units);

			System.out.println(tm1.size());
			System.out.println(tm2.size());
		}

		void lookAtLLNA_MixtureVsNonMixtureNIEHS_ICE() {
			List<Source>sources1=new ArrayList<>();
			sources1.add(new Source("NIEHS_ICE_2024_08","skin sensitization"));

			ExperimentalRecords recs=rm.getAllExperimentalRecords(sources1);
			ExperimentalRecords recsBad=rm.getAllExperimentalRecords(sources1,true);

			//		System.out.println(recs.size());
			//		System.out.println(recsBad.size());

			//		if(true)return;

			String propertyName=ExperimentalConstants.strSkinSensitizationLLNA;
			String units=ExperimentalConstants.str_binary;

			TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs);
			System.out.println("good\t"+recs.size()+"\t"+tm1.size());


			TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recsBad);
			System.out.println("bad\t"+recsBad.size()+"\t"+tm2.size());

			//		System.out.println(tm1.size());
			//		System.out.println(tm2.size());

			System.out.println("countWithMedian1="+cm.getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+cm.getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+cm.getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+cm.getNewChemicalCount(tm2, tm1,false));

			cm.compareConcordance(tm1, tm2);

		}

		
		/**
		 * TEST Sources
		 * 
		 * "73. Dimitrov, S., et al., Base-line model for identifying the bioaccumulation potential of chemicals. SAR and QSAR in Environmental Research, 2005. 16: p. 531-554
			76. Zhao, C.B., E.; Chana, A.; Roncaglioni, A.; Benfenati, E., A new hybrid system of QSAR models for predicting bioconcentration factors (BCF). Chemosphere, 2008. 73: p. 1701-1707."

				Zhao uses Dimitrov which uses Japan data.
				Dimitrov uses  Japan NITE/MITI data. The data in "000064113.xlsx" matches fairly well but chemical count is different.
				Metadata is limited unless can track down data in one of Japan's databases
			
			74. Arnot, J.A. and F.A.P.C. Gobas, A review of bioconcentration factor (BCF) and bioaccumulation factor (BAF) assessments for organic chemicals in aquatic organisms. Environ. Rev., 2006. 14: p. 257-297.
			
				ToxVal bcfbaf table has same number of records as Arnot's spreadsheet!
				Richard Judson confirmed this.
				
			75. EURAS. EURAS bioconcentration factor (BCF) Gold Standard Database. 3/30/18]; Available from: http://ambit.sourceforge.net/euras/.
			
				No longer available?
				
			Conclusion: use ToxVal and ECOTOX

		 */
		private void compareBCF() {

			printChemicalsInCommon=true;
			
			List<Source>sources1=new ArrayList<>();
			List<Source>sources2=new ArrayList<>();

			String propertyName=ExperimentalConstants.strBCF;
//			String propertyName=ExperimentalConstants.strFishBCF;
//			String propertyName=ExperimentalConstants.strFishBCFWholeBody;

//			sources1.add(new Source("Burkhard",propertyName));
//			sources1.add(new Source("ECOTOX_2023_12_14",propertyName));
//			sources2.add(new Source("ToxVal_prod",propertyName));
//			sources2.add(new Source("Arnot 2006",null));
//			sources2.add(new Source("Arnot 2006",propertyName));
			
//			sources2.add(new Source("ECOTOX_2023_12_14",propertyName));

			sources1.add(new Source("Arnot 2006",propertyName));
//			sources1.add(new Source("QSAR_Toolbox","BCF NITE//"+propertyName)); 

//			sources2.add(new Source("Arnot 2006",propertyName));
//			sources2.add(new Source("ECOTOX_2024_12_12",propertyName));
//			sources2.add(new Source("QSAR_Toolbox","BCF NITE//"+propertyName)); 
//			sources2.add(new Source("Burkhard",propertyName));//only 37 new
//			sources2.add(new Source("OPERA2.8",null));//only has CAS not SID

//			sources2.add(new Source("QSAR_Toolbox","BCF CEFIC//"+propertyName));
			

//			sources1.add(new Source("Arnot 2006",propertyName));
			
//			sources2.add(new Source("QSAR_Toolbox","Bioconcentration and logKow NITE v.4.8.2"));
//			sources2.add(new Source("QSAR_Toolbox","bioaccumulation canada v.4.8.2"));
			
			
			sources2.add(new Source("QSAR_Toolbox","Bioconcentration and logKow NITE v.4.8.2"));
//			sources2.add(new Source("QSAR_Toolbox","bioaccumulation fish CEFIC LRI v.4.8.2"));
//			sources2.add(new Source("QSAR_Toolbox","BCFBAF ECHA REACH v.4.8.2"));

			// sources1.add(new Source("Arnot 2006","Bioconcentration factor"));
			
//			sources2.add(new Source("QSAR_Toolbox","Bioconcentration and logKow NITE v.4.8.2"));
//			sources2.add(new Source("QSAR_Toolbox","bioaccumulation canada v.4.8.2"));
//			sources2.add(new Source("QSAR_Toolbox","bioaccumulation fish CEFIC LRI v.4.8.2"));
			// sources2.add(new Source("QSAR_Toolbox","BCFBAF ECHA REACH v.4.8.2"));

			
//			sources1.add(new Source("Burkhard", "Bioconcentration factor"));
//			sources2.add(new Source("ITRC July 2023", "BCF ITRC"));

			
			
//			sources2.add(new Source("ECOTOX_2024_12_12",propertyName));
			
			String units="L/kg";
			cm.compare(sources1, sources2, propertyName, units,"cas");
//			cm.compare(sources1, sources2, propertyName, units,"sid");
//			cm.compare(sources1, sources2, propertyName, units,"cas","Species supercategory","Fish");
			
			
			//We get more records if we use both even though they overlap a bit
			

		}
		
		private void compareRBiodegFull() {
            String propertyName = ExperimentalConstants.strRBIODEG;
            String units = ExperimentalConstants.str_binary;
            
            List<Source> sources1 = new ArrayList<>();
            List<Source> sources2 = new ArrayList<>();
            
            sources1.add(new Source(RecordQSAR_ToolBox.sourceName, "RBiodeg 301F ECHA Reach"));
            sources2.add(new Source(RecordEChemPortal.sourceName, "RBiodeg 301 F ECHA Reach"));
            
            ExperimentalRecords recs1 = rm.getAllExperimentalRecords(sources1);
            ExperimentalRecords recs2 = rm.getAllExperimentalRecords(sources2);
            
            TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
            TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
            
            // NEW: Debug the median count difference
            cm.debugMedianCountDifference(tm1, tm2, propertyName, units, 
                RecordQSAR_ToolBox.sourceName, RecordEChemPortal.sourceName);
            
            System.out.println("\n");
            compareRBiodeg();
            System.out.println();
            compareRBiodeg_DebugOriginal();
            System.out.println();
            analyzeRBiodeg_BadRecords();
            System.out.println();
            analyzeRBiodeg_KeepFlag();
			System.out.println();
			analyzeRBiodeg_301F();
        }


		private void compareRBiodeg() {
			
			String propertyName=ExperimentalConstants.strRBIODEG;
			
			List<Source>sources1=new ArrayList<>();
			List<Source>sources2=new ArrayList<>();

			sources1.add(new Source(RecordQSAR_ToolBox.sourceName,"RBiodeg 301F ECHA Reach"));
			sources2.add(new Source(RecordEChemPortal.sourceName,"RBiodeg 301 F ECHA Reach"));

//			sources1.add(new Source("RIFM_2026_01",null));//fragrance data
//			sources2.add(new Source(RecordEChemPortal.sourceName,"RBiodeg 301 F ECHA Reach"));

			cm.compareChemicalsInCommonConcordance(sources1, sources2, propertyName,
					ExperimentalConstants.str_binary);

		}

		private void compareRBiodeg_DebugOriginal() {
			String propertyName = ExperimentalConstants.strRBIODEG;
			String qsarPath = "data\\experimental\\QSAR_Toolbox\\RBiodeg 301F ECHA Reach\\";
			String eChemPath = "data\\experimental\\eChemPortal\\RBiodeg 301 F ECHA Reach\\";
			
			// Load the original JSON records (before ExperimentalRecord conversion)
			// ExperimentalRecords origQSAR = ExperimentalRecords.loadFromJSON(
			// 	"data\\experimental\\QSAR_Toolbox\\RBiodeg 301F ECHA Reach\\QSAR_Toolbox Original Records.json"
			// );

			List<RecordQSAR_ToolBox> origQSAR = Parse.getOriginalRecordsFromJsonFiles(qsarPath, RecordQSAR_ToolBox[].class);

			HashSet<String> origQsarCasSet = new HashSet<>();
			for (RecordQSAR_ToolBox record : origQSAR) {
				origQsarCasSet.add(record.CAS_Number);
			}

			// ExperimentalRecords origEChem = ExperimentalRecords.loadFromJSON(
			// 	"data\\experimental\\eChemPortal\\RBiodeg 301 F ECHA Reach\\eChemPortal Original Records.json"
			// );

			List<RecordEChemPortal> origEChem = Parse.getOriginalRecordsFromJsonFiles(eChemPath, RecordEChemPortal[].class);

			HashSet<String> origEChemCasSet = new HashSet<>();
			for (RecordEChemPortal record : origEChem) {
				origEChemCasSet.add(record.number);
			}
			
			System.out.println("=== ORIGINAL RECORDS (Before Conversion) ===");
			System.out.println("QSAR_Toolbox Original Records: " + origQSAR.size() + " (" + origQsarCasSet.size() + " unique CASRN)");
			System.out.println("eChemPortal Original Records: " + origEChem.size() + " (" + origEChemCasSet.size() + " unique CASRN)");
			
			// Now compare final records
			List<Source> sources1 = new ArrayList<>();
			sources1.add(new Source(RecordQSAR_ToolBox.sourceName, "RBiodeg 301F ECHA Reach"));
			
			List<Source> sources2 = new ArrayList<>();
			sources2.add(new Source(RecordEChemPortal.sourceName, "RBiodeg 301 F ECHA Reach"));
			
			ExperimentalRecords finalQSAR = rm.getAllExperimentalRecords(sources1);
			ExperimentalRecords finalEChem = rm.getAllExperimentalRecords(sources2);
			
			System.out.println("\n=== FINAL EXPERIMENTAL RECORDS (After Conversion) ===");
			System.out.println("QSAR_Toolbox Final Records: " + finalQSAR.size());
			System.out.println("eChemPortal Final Records: " + finalEChem.size());
			System.out.println("QSAR Loss: " + (origQSAR.size() - finalQSAR.size()) + 
				" (" + (100.0 * (origQSAR.size() - finalQSAR.size()) / origQSAR.size()) + "%)");
			System.out.println("eChemPortal Loss: " + (origEChem.size() - finalEChem.size()) + 
				" (" + (100.0 * (origEChem.size() - finalEChem.size()) / origEChem.size()) + "%)");
		}

		private void analyzeRBiodeg_BadRecords() {
			ExperimentalRecords badQSAR = ExperimentalRecords.loadFromJSON(
				"data\\experimental\\QSAR_Toolbox\\RBiodeg 301F ECHA Reach\\QSAR_Toolbox Experimental Records-Bad.json"
			);
			
			ExperimentalRecords badEChem = ExperimentalRecords.loadFromJSON(
				"data\\experimental\\eChemPortal\\RBiodeg 301 F ECHA Reach\\eChemPortal Experimental Records-Bad.json"
			);
			
			System.out.println("=== BAD RECORDS ANALYSIS ===");
			System.out.println("QSAR_Toolbox Bad Records: " + badQSAR.size());
			System.out.println("eChemPortal Bad Records: " + badEChem.size());
			
			// Analyze reasons for rejection
			Hashtable<String, Integer> reasonsQSAR = new Hashtable<>();
			Hashtable<String, Integer> reasonsEChem = new Hashtable<>();
			
			for (ExperimentalRecord er : badQSAR) {
				String reason = (er.reason != null) ? er.reason : "No reason given";
				reasonsQSAR.put(reason, reasonsQSAR.getOrDefault(reason, 0) + 1);
			}
			
			for (ExperimentalRecord er : badEChem) {
				String reason = (er.reason != null) ? er.reason : "No reason given";
				reasonsEChem.put(reason, reasonsEChem.getOrDefault(reason, 0) + 1);
			}
			
			System.out.println("\nQSAR_Toolbox rejection reasons:");
			reasonsQSAR.forEach((reason, count) -> System.out.println("  " + reason + ": " + count));
			
			System.out.println("\neChemPortal rejection reasons:");
			reasonsEChem.forEach((reason, count) -> System.out.println("  " + reason + ": " + count));
		}
		
		private void analyzeRBiodeg_KeepFlag() {
			List<Source> sources1 = new ArrayList<>();
			sources1.add(new Source(RecordQSAR_ToolBox.sourceName, "RBiodeg 301F ECHA Reach"));
			ExperimentalRecords finalQSAR = rm.getAllExperimentalRecords(sources1);
			
			List<Source> sources2 = new ArrayList<>();
			sources2.add(new Source(RecordEChemPortal.sourceName, "RBiodeg 301 F ECHA Reach"));
			ExperimentalRecords finalEChem = rm.getAllExperimentalRecords(sources2);
			
			int flaggedQSAR = 0, flaggedEChem = 0;
			for (ExperimentalRecord er : finalQSAR) {
				if (er.flag) flaggedQSAR++;
			}
			for (ExperimentalRecord er : finalEChem) {
				if (er.flag) flaggedEChem++;
			}
			
			System.out.println("=== KEEP/FLAG ANALYSIS ===");
			System.out.println("QSAR_Toolbox flagged records: " + flaggedQSAR);
			System.out.println("eChemPortal flagged records: " + flaggedEChem);
		}

		private void analyzeRBiodeg_301F() {
			String propertyName = ExperimentalConstants.strRBIODEG;
			String qsarPath = "data\\experimental\\QSAR_Toolbox\\RBiodeg 301F ECHA Reach\\";
			String eChemPath = "data\\experimental\\eChemPortal\\RBiodeg 301 F ECHA Reach\\";

			List<RecordQSAR_ToolBox> origQSAR = Parse.getOriginalRecordsFromJsonFiles(qsarPath, RecordQSAR_ToolBox[].class);

			HashSet<String> origQsarCasSet = new HashSet<>();
			HashSet<String> origQsarCasSet301F = new HashSet<>();
			for (RecordQSAR_ToolBox record : origQSAR) {
				origQsarCasSet.add(record.CAS_Number);
				if (record.Test_guideline != null && record.Test_guideline.equals("OECD Guideline 301 F (Ready Biodegradability: Manometric Respirometry Test)")) {
					origQsarCasSet301F.add(record.CAS_Number);
				}
			}

			List<RecordEChemPortal> origEChem = Parse.getOriginalRecordsFromJsonFiles(eChemPath, RecordEChemPortal[].class);

			HashSet<String> origEChemCasSet = new HashSet<>();
			HashSet<String> origEChemCasSet301F = new HashSet<>();
			for (RecordEChemPortal record : origEChem) {
				origEChemCasSet.add(record.number);
				if (record.testGuideline != null && record.testGuideline.equals("OECD Guideline 301 F (Ready Biodegradability: Manometric Respirometry Test)")) {
					origEChemCasSet301F.add(record.number);
				}
			}

			ExperimentalRecords expQSAR = ExperimentalRecords.loadFromJSON(qsarPath + "QSAR_Toolbox Experimental Records.json");

			HashSet<String> expQsarCasSet = new HashSet<>();
			HashSet<String> expQsarCasSet301F = new HashSet<>();
			for (ExperimentalRecord record : expQSAR) {
				expQsarCasSet.add(record.casrn);
				String testGuideline = (String) record.getExperimentalParameter("Test guideline");
				if (testGuideline != null && testGuideline.equals("OECD Guideline 301 F (Ready Biodegradability: Manometric Respirometry Test)")) {
					expQsarCasSet301F.add(record.casrn);
				}
			}

			ExperimentalRecords expEChem = ExperimentalRecords.loadFromJSON(eChemPath + "eChemPortal Experimental Records.json");
			
			HashSet<String> expEChemCasSet = new HashSet<>();
			HashSet<String> expEChemCasSet301F = new HashSet<>();
			for (ExperimentalRecord record : expEChem) {
				expEChemCasSet.add(record.casrn);
				String testGuideline = (String) record.getExperimentalParameter("Test guideline");
				if (testGuideline != null && testGuideline.equals("OECD Guideline 301 F (Ready Biodegradability: Manometric Respirometry Test)")) {
					expEChemCasSet301F.add(record.casrn);
				}
			}

			System.out.println("=== 301 F RECORDS ===");
			System.out.println("Records Set\t\t\t\tSize\tUnique CASRN\t301F CASRNs");
			System.out.println("QSAR_Toolbox Original Records:\t\t" + origQSAR.size() + "\t" + origQsarCasSet.size() + "\t" + origQsarCasSet301F.size());
			System.out.println("eChemPortal Original Records:\t\t" + origEChem.size() + "\t" + origEChemCasSet.size() + "\t" + origEChemCasSet301F.size());
			System.out.println("QSAR_Toolbox Experimental Records:\t" + expQSAR.size() + "\t" + expQsarCasSet.size() + "\t" + expQsarCasSet301F.size());
			System.out.println("eChemPortal Experimental Records:\t" + expEChem.size() + "\t" + expEChemCasSet.size() + "\t" + expEChemCasSet301F.size());
		}

		private void compareKoc() {

			printChemicalsInCommon=true;

			String propertyName=ExperimentalConstants.strKOC;

			List<Source>sources1=new ArrayList<>();
			List<Source>sources2=new ArrayList<>();

//			sources1.add(new Source(RecordKoc.sourceName,null));
//			sources2.add(new Source("OPERA2.8",null));//only has CAS not SID
			
//			sources1.add(new Source(RecordKoc.sourceName,null));
//			sources2.add(new Source(RecordQSAR_ToolBox.sourceName,"Koc ECHA Reach"));//only has CAS not SID
			
			sources1.add(new Source(RecordQSAR_ToolBox.sourceName,"Koc ECHA Reach"));//only has CAS not SID
			sources2.add(new Source(RecordEChemPortal.sourceName,"Koc ECHA Reach"));//only has CAS not SID

			
			String units="L/kg";
//			cm.compare(sources1, sources2, propertyName, units,"sid");
			cm.compare(sources1, sources2, propertyName, units,"cas");

		}
		
		private void compareAquaticTox() {

			printChemicalsInCommon=true;
			
			List<Source>sources1=new ArrayList<>();
			List<Source>sources2=new ArrayList<>();

			String propertyName=ExperimentalConstants.strAcuteAquaticToxicity;

//			
			sources1.add(new Source("ECOTOX_2024_12_12",propertyName));
			sources2.add(new Source("QSAR_Toolbox","Fish tox ECHA//"+propertyName)); 

			String units="g/L";
			cm.compare(sources1, sources2, propertyName, units,"cas");
//			cm.compare(sources1, sources2, propertyName, units,"sid");
//			cm.compare(sources1, sources2, propertyName, units,"cas","Species supercategory","Fish");
			

		}

		void compareOralRat() {

			c.compareToNIEHS_OralRatLD50();
			//		c.compareToChemidplusToEcha();
			//		c.compare("ChemIDplus_2024_12_04", "ChemIDplus", ExperimentalConstants.strORAL_RAT_LD50,ExperimentalConstants.str_mg_kg);

			//		c.compareREACH_Sources();
			//		c.compareQSAR_Toolbox_sources();

			//		c.lookAtEchemportalLD50_Guidelines();
			//		c.lookAtEchemportalLD50_Guidelines2();
			//		c.lookAtEchemportalLD50_Guidelines3();

		}


	}

	public static boolean printChemicalsInCommon=false;
	public static boolean printSourceChemical=false;


	
	public class CompareMethods {

		void compare(List<Source>sources1, List<Source>sources2, String propertyName,String units,String idType) {

			ExperimentalRecords recs1=rm.getAllExperimentalRecords(sources1,propertyName);
			ExperimentalRecords recs2=rm.getAllExperimentalRecords(sources2,propertyName);


			if(idType.equals("sid")) {
				recs1.addDtxsids();
				recs2.addDtxsids();
			}
			
			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}

			System.out.println("sources1:"+ParseUtilities.gson.toJson(sources1));
			System.out.println("sources2:"+ParseUtilities.gson.toJson(sources2));

			System.out.println("countWithMedian1="+getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+getNewChemicalCount(tm2, tm1,false));
			System.out.println("countInEither="+getCountInEither(tm2, tm1,false));
			
			
			
			compareChemicalsInCommon(tm1, tm2, units);

		}
		
		
		public void compare(ExperimentalRecords allRecords, String sourceName1, String sourceName2, String propertyName,String units,String idType) {

			ExperimentalRecords recs1=new ExperimentalRecords();
			ExperimentalRecords recs2=new ExperimentalRecords();

			
			for (ExperimentalRecord er:allRecords) {
				if(!er.property_name.equals(propertyName)) continue;
				
				if(er.source_name.equals(sourceName1))recs1.add(er);
				if(er.source_name.equals(sourceName2))recs2.add(er);
				
			}
			
			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}
			
			JsonObject jo=new JsonObject();
			jo.addProperty("source1", sourceName1);
			jo.addProperty("source2", sourceName2);
			jo.addProperty("countWithMedian1",getCountWithMedian(tm1));
			jo.addProperty("countWithMedian2",getCountWithMedian(tm2));
			jo.addProperty("countIn1Not2",getNewChemicalCount(tm1, tm2,false));
			jo.addProperty("countIn2Not1",getNewChemicalCount(tm2, tm1,false));
			jo.addProperty("countInEither",getCountInEither(tm2, tm1,false));
			
			compareChemicalsInCommon(sourceName1,sourceName2,tm1, tm2, units, jo);
			
			System.out.println(ParseUtilities.gson.toJson(jo));


		}
		
		public void compare(Hashtable<String,ExperimentalRecords>htAllRecords, String sourceName1, String sourceName2, String propertyName,String units,String idType) {

			ExperimentalRecords recs1=htAllRecords.get(sourceName1);
			ExperimentalRecords recs2=htAllRecords.get(sourceName2);

			
			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}
			
			JsonObject jo=new JsonObject();
			jo.addProperty("source1", sourceName1);
			jo.addProperty("source2", sourceName2);
			jo.addProperty("countWithMedian1",getCountWithMedian(tm1));
			jo.addProperty("countWithMedian2",getCountWithMedian(tm2));
			jo.addProperty("countIn1Not2",getNewChemicalCount(tm1, tm2,false));
			jo.addProperty("countIn2Not1",getNewChemicalCount(tm2, tm1,false));
			jo.addProperty("countInEither",getCountInEither(tm2, tm1,false));
			
			double MAE= compareChemicalsInCommon(sourceName1,sourceName2,tm1, tm2, units, jo);
			
			System.out.println(ParseUtilities.gson.toJson(jo));


		}
		
		
		public void compareToOtherSources(Hashtable<String,ExperimentalRecords>htAllRecords, String sourceName, String propertyName,String units,String idType) {

			String source1="All but "+sourceName;
			String source2=sourceName;

			ExperimentalRecords recs1=new ExperimentalRecords();
			for (String key:htAllRecords.keySet()) {
				if(!key.equals(sourceName)) {
					recs1.addAll(htAllRecords.get(key));
				}
			}

			ExperimentalRecords recs2=htAllRecords.get(sourceName);
			
			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}

			
			JsonObject jo=new JsonObject();
			jo.addProperty("source1", source1);
			jo.addProperty("source2", source2);
			jo.addProperty("countWithMedian1",getCountWithMedian(tm1));
			jo.addProperty("countWithMedian2",getCountWithMedian(tm2));
			jo.addProperty("countIn1Not2",getNewChemicalCount(tm1, tm2,false));
			jo.addProperty("countIn2Not1",getNewChemicalCount(tm2, tm1,false));
			jo.addProperty("countInEither",getCountInEither(tm2, tm1,false));
			
			double MAE= compareChemicalsInCommon(source1, source2, tm1, tm2, units, jo);
			System.out.println(ParseUtilities.gson.toJson(jo));

		}

		
		public void compare(ExperimentalRecords recs1,ExperimentalRecords recs2, String sourceName1, String sourceName2, String propertyName,String units,String idType) {

			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}
			
			JsonObject jo=new JsonObject();
			jo.addProperty("source1", sourceName1);
			jo.addProperty("source2", sourceName2);
			jo.addProperty("countWithMedian1",getCountWithMedian(tm1));
			jo.addProperty("countWithMedian2",getCountWithMedian(tm2));
			jo.addProperty("countIn1Not2",getNewChemicalCount(tm1, tm2,false));
			jo.addProperty("countIn2Not1",getNewChemicalCount(tm2, tm1,false));
			jo.addProperty("countInEither",getCountInEither(tm2, tm1,false));
			
			compareChemicalsInCommon(sourceName1,sourceName2,tm1, tm2, units, jo);
			
			System.out.println(ParseUtilities.gson.toJson(jo));


		}
		
		
		void compare(List<Source>sources1, List<Source>sources2, String propertyName,String units,String idType,String parameterName,String parameterValue) {

			ExperimentalRecords recs1=rm.getAllExperimentalRecords(sources1);
			ExperimentalRecords recs2=rm.getAllExperimentalRecords(sources2);
			
			rm.removeByParameter(parameterName, parameterValue, recs1);
			rm.removeByParameter(parameterName, parameterValue, recs2);


			TreeMap<String, ExperimentalRecords> tm1=null;
			TreeMap<String, ExperimentalRecords> tm2=null;

			if(idType.equals("cas")) {
				tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
				tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);
			} else if(idType.equals("sid")) {
				tm1 = rm.getTreeMapByDTXSID(propertyName, units, recs1);
				tm2 = rm.getTreeMapByDTXSID(propertyName, units, recs2);
			}

			System.out.println("countWithMedian1="+getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+getNewChemicalCount(tm2, tm1,false));
			System.out.println("countInEither="+getCountInEither(tm2, tm1,false));

			compareChemicalsInCommon(tm1, tm2, units);

		}

		void compare(String sourceName1,String sourceName2,String propertyName,String units) {
			TreeMap<String,ExperimentalRecords> tm1 = rm.getHashtable(sourceName1,null, propertyName,units);
			TreeMap<String,ExperimentalRecords> tm2 = rm.getHashtable(sourceName2,null, propertyName,units);

			System.out.println("countWithMedian1="+getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+getNewChemicalCount(tm2, tm1,true));
			compareChemicalsInCommon(tm1, tm2, units);

		}

		void compare(String sourceName1,String sourceName2,String subfolder1,String subfolder2, String propertyName,String units) {
			TreeMap<String,ExperimentalRecords> tm1 = rm.getHashtable(sourceName1,subfolder1,propertyName,units);
			TreeMap<String,ExperimentalRecords> tm2 = rm.getHashtable(sourceName2,subfolder2, propertyName,units);

			System.out.println("countWithMedian1="+getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+getNewChemicalCount(tm2, tm1,false));
			compareChemicalsInCommon(tm1, tm2,units);

		}

		public double compareChemicalsInCommon(TreeMap<String,ExperimentalRecords>tm1,TreeMap<String,ExperimentalRecords>tm2, String units) {

//			if(!units.toLowerCase().contains("log")) {
//				System.out.println("Need to handle units="+units);
//			}

			int countInCommon=0;
			double MAE=0;

			DecimalFormat df=new DecimalFormat("0.00");

//			boolean printValues=false;

//			if(printChemicalsInCommon) System.out.println("\nLogType\tkey\tLog10median_1\tLog10median_2\tdiff");
			if(printChemicalsInCommon) System.out.println("key\tLog10median_1\tLog10median_2\tdiff");

			List<Double>vals1=new ArrayList<>();
			List<Double>vals2=new ArrayList<>();


			for (String key:tm1.keySet()) {
				ExperimentalRecords recs1=tm1.get(key);

				//			System.out.println(key);

				if(!tm2.containsKey(key))continue;

				ExperimentalRecords recs2=tm2.get(key);

				if(recs1.medianValue!=null && recs2.medianValue!=null) {
					
					Double error=Math.abs(recs1.medianValue-recs2.medianValue);
					vals1.add(recs1.medianValue);
					vals2.add(recs2.medianValue);

					if(printChemicalsInCommon) {
//						System.out.println("took log\t"+key+"\t"+df.format(recs1.medianValue)+"\t"+df.format(recs2.medianValue)+"\t"+df.format(error));					
						System.out.println(key+"\t"+df.format(recs1.medianValue)+"\t"+df.format(recs2.medianValue)+"\t"+df.format(error));					
					}

					
					//				System.out.println(casrn+"\t"+recs1.medianValue+"\t"+recs2.medianValue);	
//					Double error=null;
//					if(units.toLowerCase().contains("log")) {
//						error=Math.abs(recs1.medianValue-recs2.medianValue);
//						if(printValues) {
//
//							System.out.println("already log\t"+key+"\t"+df.format(recs1.medianValue)+"\t"+df.format(recs2.medianValue)+"\t"+df.format(error));					
//						}
//					} else {
//						error=Math.abs(recs1.medianValue-recs2.medianValue);
//						vals1.add(recs1.medianValue);
//						vals2.add(recs2.medianValue);
//						if(printValues) {
//							System.out.println("took log\t"+key+"\t"+df.format(recs1.medianValue)+"\t"+df.format(recs2.medianValue)+"\t"+df.format(error));					
//						}
//					}
					
					//				if(error>0) {
					//					System.out.println(casrn+"\t"+df.format(Math.log10(recs1.medianValue))+"\t"+df.format(Math.log10(recs2.medianValue))+"\t"+df.format(error));
					//				}

					MAE+=error;
					countInCommon++;

				} 
			}
			
			
			if(!units.contains("log"))units="log10("+units+")";
			

			createPlot(units, vals1, vals2);

			MAE/=countInCommon;
			System.out.println("Count in common="+countInCommon);
			System.out.println("MAE="+MAE);
			return MAE;

		}
		
		class ExperimentalPair implements Comparable<ExperimentalPair> {

		    ExperimentalRecords recs1;
		    ExperimentalRecords recs2;

		    ExperimentalPair(ExperimentalRecords recs1, ExperimentalRecords recs2) {
		        this.recs1 = recs1;
		        this.recs2 = recs2;
		    }

		    Double getAbsDiff() {
		        return Math.abs(recs1.medianValue - recs2.medianValue);
		    }

		    static String getHeader() {
		        if (printSourceChemical) {
		            return "id1\tid2\tmedian1\tmedian2\tabsDiff";
		        } else {
		            return "id\tabsDiff";
		        }
		    }

		    String getId() {
		        ExperimentalRecord rec1 = recs1.get(0);
		        ExperimentalRecord rec2 = recs2.get(0);

		        if (printSourceChemical) {
		            return (rec1.casrn + "; " + rec1.chemical_name + "\t" + rec2.casrn + "; " + rec2.chemical_name);
		        } else {
		            return (rec1.dsstox_substance_id);
		        }
		    }

		    String getLine() {
		    	
		    	DecimalFormat df=new DecimalFormat("0.00");
		    	
		        return getId() + "\t" + df.format(recs1.medianValue) + "\t" + df.format(recs2.medianValue) 
		        + "\t" + df.format(getAbsDiff());
		    }

		    // Natural ordering: absDiff descending
		    @Override
		    public int compareTo(ExperimentalPair other) {
		        return Double.compare(other.getAbsDiff(), this.getAbsDiff());
		    }

		}
		
		
		
		
		public double compareChemicalsInCommon(String sourceName1,String sourceName2, TreeMap<String,ExperimentalRecords>tm1,TreeMap<String,ExperimentalRecords>tm2, String units,JsonObject jo) {

//			if(!units.toLowerCase().contains("log")) {
//				System.out.println("Need to handle units="+units);
//			}

			

			DecimalFormat df=new DecimalFormat("0.00");

//			boolean printValues=false;

			if(printChemicalsInCommon) System.out.println("\n"+ExperimentalPair.getHeader());

			List<Double>vals1=new ArrayList<>();
			List<Double>vals2=new ArrayList<>();
			

			List<ExperimentalPair>pairs=new ArrayList<>();
			
			for (String key:tm1.keySet()) {

				ExperimentalRecords recs1=tm1.get(key);
				if(!tm2.containsKey(key))continue;
				ExperimentalRecords recs2=tm2.get(key);
				
				if(recs1.medianValue!=null && recs2.medianValue!=null) {
					ExperimentalPair pair=new ExperimentalPair(recs1,recs2);
					pairs.add(pair);
					vals1.add(recs1.medianValue);
					vals2.add(recs2.medianValue);
				} 
			}
			
			Collections.sort(pairs);
			
			
			double MAE=0;
			for (ExperimentalPair pair:pairs) {
				MAE+=pair.getAbsDiff();
				if(printChemicalsInCommon) {
					System.out.println(pair.getLine());
				}
			}
			MAE/=pairs.size();
//			
			
			if(!units.contains("log"))units="log10("+units+")";
			

			createPlot(units, vals1, vals2,sourceName1,sourceName2);

			jo.addProperty("countInCommon",pairs.size());
			jo.addProperty("MAE",MAE);

			return MAE;

		}
		
		

		void compareChemicalsInCommonConcordance(List<Source>sources1, List<Source>sources2, String propertyName,String units) {

			ExperimentalRecords recs1=rm.getAllExperimentalRecords(sources1);
			ExperimentalRecords recs2=rm.getAllExperimentalRecords(sources2);

			TreeMap<String, ExperimentalRecords> tm1 = rm.getTreeMapByCAS(propertyName, units, recs1);
			TreeMap<String, ExperimentalRecords> tm2 = rm.getTreeMapByCAS(propertyName, units, recs2);

			System.out.println("countWithMedian1="+getCountWithMedian(tm1));
			System.out.println("countWithMedian2="+getCountWithMedian(tm2));
			System.out.println("countIn1Not2="+getNewChemicalCount(tm1, tm2,false));
			System.out.println("countIn2Not1="+getNewChemicalCount(tm2, tm1,false));

			compareConcordance(tm1, tm2);

		}

		private double compareConcordance(TreeMap<String, ExperimentalRecords> tm1,
				TreeMap<String, ExperimentalRecords> tm2) {
			int countInCommon=0;
			double Concordance=0;

			DecimalFormat df=new DecimalFormat("0.00");

			//		boolean printValues=true;
			boolean printValues=false;

			if(printValues) System.out.println("casrn\tBinary_1\tBinary_2");

			for (String casrn:tm1.keySet()) {
				ExperimentalRecords recs1=tm1.get(casrn);

				if(!tm2.containsKey(casrn))continue;

				ExperimentalRecords recs2=tm2.get(casrn);

				if(recs1.medianValue!=null && recs2.medianValue!=null) {
					//				System.out.println(casrn+"\t"+recs1.medianValue+"\t"+recs2.medianValue);	

					double diff=Math.abs(recs1.medianValue-recs2.medianValue);

					if(diff<0.0001) {
						Concordance++;
					}

					countInCommon++;

					if(printValues) {
						System.out.println(casrn+"\t"+recs1.medianValue+"\t"+recs2.medianValue);					
					}
				} 
			}

			Concordance/=countInCommon;

			System.out.println("Count in common="+countInCommon);
			System.out.println("Concordance="+df.format(Concordance));

			return Concordance;
		}

		int getCountInEither(TreeMap<String,ExperimentalRecords>tm1,TreeMap<String,ExperimentalRecords>tm2,boolean printValues) {

			HashSet<String>keys=new HashSet<>();

			for (String key:tm1.keySet()) {
				ExperimentalRecords recs=tm1.get(key);
				if(recs.medianValue!=null)
					keys.add(key);
			}
			for (String key:tm2.keySet()) {
				ExperimentalRecords recs=tm2.get(key);
				if(recs.medianValue!=null)
					keys.add(key);
			}

			return keys.size();

		}

		int getCountWithMedian(TreeMap<String,ExperimentalRecords>tm) {

			int countWithMedian=0;
			for (String key:tm.keySet()) {
				ExperimentalRecords recs1=tm.get(key);

				if(recs1.medianValue!=null) countWithMedian++;
			}
			return countWithMedian;

		}

		/**
		 * Get counts of chemicals special to the first source
		 * 
		 * @param tm1
		 * @param tm2
		 * @return
		 */
		int getNewChemicalCount(TreeMap<String,ExperimentalRecords>tm1,TreeMap<String,ExperimentalRecords>tm2,boolean printValues) {

			int countIn1Not2=0;

			Hashtable<String,Integer>htCountBySource=new Hashtable<>();

			for (String casrn:tm1.keySet()) {
				ExperimentalRecords recs1=tm1.get(casrn);

				if(!tm2.containsKey(casrn) && recs1.medianValue!=null) {
					countIn1Not2++;

					HashSet<String>sources=rm.updateCountBySourceHashtable(htCountBySource, recs1);

					if(printValues) System.out.println(casrn+"\t"+(recs1.medianValue)+"\t"+sources);

					//				if(printValues && sources.contains("Unknown")) {
					//					System.out.println(casrn+"\tHas Unknown"+"\t"+recs1.medianValue);
					//				}

					continue;
				}

				ExperimentalRecords recs2=tm2.get(casrn);

				if(recs1.medianValue!=null && recs2.medianValue==null) {
					//				if(printValues) System.out.println(casrn+"\t"+(recs1.medianValue));
					countIn1Not2++;
					HashSet<String>sources=rm.updateCountBySourceHashtable(htCountBySource, recs1);

					if(printValues) System.out.println(casrn+"\t"+(recs1.medianValue)+"\t"+sources);

					//				if(printValues && sources.contains("Unknown")) {
					//					System.out.println(casrn+"\tHas Unknown"+"\t"+recs1.medianValue);
					//				}

				}
			}

			if(printValues) {
				System.out.println("\nCounts by original source that arent in other set");
				for(String source:htCountBySource.keySet()) {
					System.out.println(source+"\t"+htCountBySource.get(source));
				}
				System.out.println("");
			}

			return countIn1Not2;

		}

		public void createPlot(String units, List<Double> vals1, List<Double> vals2) {
			double[]x = makeArray(vals1);
			double[]y = makeArray(vals2);
		
			MatlabChart fig = new MatlabChart(); // figure('Position',[100 100 640 480]);
			fig.plot(x, y, "-r", 2.0f, "data"); // plot(x,y1,'-r','LineWidth',2);
			fig.plot(y, y, "-b", 2.0f, "Y=X"); // plot(x,y1,'-r','LineWidth',2);

			//        fig.plot(x, y2, ":k", 3.0f, "BAC");  // plot(x,y2,':k','LineWidth',3);
		
			fig.RenderPlot();                    // First render plot before modifying
			fig.title("Source1 vs. Source 2");    // title('Stock 1 vs. Stock 2');
			//      fig.xlim(10, 100);                   // xlim([10 100]);
			//      fig.ylim(200, 300);                  // ylim([200 300]);
		
		
			//TODO for some properties it wont be logged units in labels
		
			fig.xlabel("exp source 1 "+units);                  // xlabel('Days');
			fig.ylabel("exp source 2 "+units);                 // ylabel('Price');
			fig.grid("on","on");                 // grid on;
			fig.legend("southeast");             // legend('AAPL','BAC','Location','northeast')
			fig.font("Helvetica",15);            // .. 'FontName','Helvetica','FontSize',15
			//      fig.saveas("MyPlot.jpeg",640,480);   // saveas(gcf,'MyPlot','jpeg');
		
			XYLineAndShapeRenderer xy=(XYLineAndShapeRenderer) fig.chart.getXYPlot().getRenderer();

			xy.setSeriesShapesVisible(0, true);
			xy.setSeriesLinesVisible(0, false);

			xy.setSeriesShapesVisible(1, false);
			xy.setSeriesLinesVisible(1, true);

			
		
		
			ChartPanel cp=new ChartPanel(fig.chart);
		
		
			JFrame jframe=new JFrame();
			jframe.add(cp);
			cp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
			jframe.setSize(500,500);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setLocationRelativeTo(null);
			jframe.setVisible(true);
		}
		
		public void createPlot(String units, List<Double> vals1, List<Double> vals2,String source1,String source2) {
			double[]x = makeArray(vals1);
			double[]y = makeArray(vals2);
		
			MatlabChart fig = new MatlabChart(); // figure('Position',[100 100 640 480]);
			fig.plot(x, y, "-r", 2.0f, "data"); // plot(x,y1,'-r','LineWidth',2);
			fig.plot(y, y, "-b", 2.0f, "Y=X"); // plot(x,y1,'-r','LineWidth',2);

			//        fig.plot(x, y2, ":k", 3.0f, "BAC");  // plot(x,y2,':k','LineWidth',3);
		
			fig.RenderPlot();                    // First render plot before modifying
			fig.title(source1+" vs. "+source2);    // title('Stock 1 vs. Stock 2');
			//      fig.xlim(10, 100);                   // xlim([10 100]);
			//      fig.ylim(200, 300);                  // ylim([200 300]);
		
		
			//TODO for some properties it wont be logged units in labels
		
			fig.xlabel("exp "+source1+" "+units);                  // xlabel('Days');
			fig.ylabel("exp "+source2+" "+units);                 // ylabel('Price');
			fig.grid("on","on");                 // grid on;
			fig.legend("southeast");             // legend('AAPL','BAC','Location','northeast')
			fig.font("Helvetica",15);            // .. 'FontName','Helvetica','FontSize',15
			//      fig.saveas("MyPlot.jpeg",640,480);   // saveas(gcf,'MyPlot','jpeg');
		
			XYLineAndShapeRenderer xy=(XYLineAndShapeRenderer) fig.chart.getXYPlot().getRenderer();

			xy.setSeriesShapesVisible(0, true);
			xy.setSeriesLinesVisible(0, false);

			xy.setSeriesShapesVisible(1, false);
			xy.setSeriesLinesVisible(1, true);

			ChartPanel cp=new ChartPanel(fig.chart);
			JFrame jframe=new JFrame();
			jframe.add(cp);
			cp.setLayout(new FlowLayout(FlowLayout.LEFT));
		
			jframe.setSize(500,500);
			jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jframe.setLocationRelativeTo(null);
			jframe.setVisible(true);
		}
		
		

		private double[] makeArray(List<Double> vals1) {
			double[]x=new double[vals1.size()];
			int i=0;
			for (Double val:vals1) {
				x[i++]=val;
			}
			return x;
			//		Double[] array = new Double[vals1.size()];
			//		return vals1.toArray(array); 
		
		}

		/**
		 * Print example records WITH medians from TreeMap (records counted by getCountWithMedian)
		 * @param tm TreeMap to inspect
		 * @param sourceName Label for output
		 * @param maxExamples Number of examples to print
		 */
		void printRecordsWithMedian(TreeMap<String, ExperimentalRecords> tm, String sourceName, int maxExamples) {
			System.out.println("\n=== RECORDS WITH MEDIAN (" + sourceName + ") ===");
			System.out.println("CAS\tChemical Name\tMedian Value\tRecord Count\tUnits");
			System.out.println("---");
			
			int count = 0;
			for (String casrn : tm.keySet()) {
				if (count >= maxExamples) break;
				
				ExperimentalRecords recs = tm.get(casrn);
				if (recs.medianValue != null) {
					ExperimentalRecord example = recs.get(0);
					System.out.println(casrn + "\t" + example.chemical_name + "\t" + 
						recs.medianValue + "\t" + recs.size() + "\t" + example.property_value_units_final);
					count++;
				}
			}
			System.out.println("(Showing " + count + " of " + getCountWithMedian(tm) + " total with median)");
		}

		/**
		 * Print example records WITHOUT medians from TreeMap (records excluded from getCountWithMedian)
		 * Helps identify why certain chemicals are not contributing to comparisons
		 * @param tm TreeMap to inspect
		 * @param propertyName Property being analyzed (for filtering)
		 * @param units Expected units (for filtering)
		 * @param sourceName Label for output
		 * @param maxExamples Number of examples to print
		 */
		void printRecordsWithoutMedian(TreeMap<String, ExperimentalRecords> tm, String propertyName, 
				String units, String sourceName, int maxExamples) {
			System.out.println("\n=== RECORDS WITHOUT MEDIAN (" + sourceName + ") ===");
			System.out.println("CAS\tChemical Name\tRecord Count\tReason(s) for No Median");
			System.out.println("---");
			
			int count = 0;
			for (String casrn : tm.keySet()) {
				if (count >= maxExamples) break;
				
				ExperimentalRecords recs = tm.get(casrn);
				if (recs.medianValue == null) {
					ExperimentalRecord example = recs.get(0);
					String reason = analyzeMedianFailure(recs, units);
					System.out.println(casrn + "\t" + example.chemical_name + "\t" + 
						recs.size() + "\t" + reason);
					count++;
				}
			}
			
			int totalWithoutMedian = tm.size() - getCountWithMedian(tm);
			System.out.println("(Showing " + count + " of " + totalWithoutMedian + " total without median)");
		}

				/**
         * Analyze why a specific chemical's records failed to produce a median
         * Returns human-readable string explaining the issue
         */
        private String analyzeMedianFailure(ExperimentalRecords recs, String units) {
            
            int validCount = 0;
            int noUnitsMatch = 0;
            int hasQualifiers = 0;
            int rangeTooBroad = 0;
            int noPointEstimate = 0;
            int binaryOutOfRange = 0;
            
            List<Double> vals = new ArrayList<>();
            
            for (ExperimentalRecord er : recs) {
                // Check 1: Units mismatch
                if (er.property_value_units_final == null || !er.property_value_units_final.equals(units)) {
                    noUnitsMatch++;
                    continue;
                }
                
                // Check 2: Qualifier check (skip if not "~" qualifier, except for binary/ranges)
                if (er.property_value_numeric_qualifier != null && 
                    !er.property_value_numeric_qualifier.equals("~")) {
                    hasQualifiers++;
                    continue;
                }
                
                // Check 3: Try to get value for median calculation
                Double val = null;
                
                if (units.equals(ExperimentalConstants.str_binary)) {
                    val = er.property_value_point_estimate_final;
                    if (val != null) {
                        vals.add(val);
                        validCount++;
                    }
                } else {
                    // Range check
                    if (er.property_value_max_final != null && er.property_value_min_final != null) {
                        double logDiff = Math.abs(Math.log10(er.property_value_min_final / er.property_value_max_final));
                        if (logDiff >= 1) {
                            rangeTooBroad++;
                            continue;
                        }
                        val = Math.sqrt(er.property_value_max_final * er.property_value_min_final);
                    } else if (er.property_value_point_estimate_final != null) {
                        val = er.property_value_point_estimate_final;
                    } else {
                        noPointEstimate++;
                        continue;
                    }
                    
                    // Log conversion if needed
                    if (!units.toLowerCase().contains("log") && !units.equals(ExperimentalConstants.str_C)) {
                        if (val == 0.0) {
                            noPointEstimate++;
                            continue;
                        }
                        val = Math.log10(val);
                    }
                    
                    vals.add(val);
                    validCount++;
                }
            }
            
            // Build reason string - check why median wasn't set despite having valid values
            StringBuilder sb = new StringBuilder();
            
            if (noUnitsMatch > 0) sb.append("No units match (").append(noUnitsMatch).append(") ");
            if (hasQualifiers > 0) sb.append("Has qualifiers like <,> (").append(hasQualifiers).append(") ");
            if (rangeTooBroad > 0) sb.append("Range too broad (").append(rangeTooBroad).append(") ");
            if (noPointEstimate > 0) sb.append("No point estimate (").append(noPointEstimate).append(") ");
            
            // Special case: binary with values but outside [0.2, 0.8] range for setBinaryScore
            if (units.equals(ExperimentalConstants.str_binary) && validCount > 0) {
                double avg = 0;
                for (Double v : vals) avg += v;
                avg /= vals.size();
                if (avg > 0.2 && avg < 0.8) {
                    sb.append("Binary avg outside range [0.2, 0.8]: ").append(String.format("%.2f", avg)).append(" ");
                }
            }
            
            return sb.toString().trim().isEmpty() ? "Unknown reason (" + recs.size() + " records)" : sb.toString().trim();
		}

		/**
         * Detailed comparison showing why getCountWithMedian differs between two sources
         */
        void debugMedianCountDifference(TreeMap<String, ExperimentalRecords> tm1, 
                TreeMap<String, ExperimentalRecords> tm2, String propertyName, String units,
                String source1Name, String source2Name) {
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DEBUGGING MEDIAN COUNT DIFFERENCE");
            System.out.println("=".repeat(80));
            
            int count1 = getCountWithMedian(tm1);
            int count2 = getCountWithMedian(tm2);
            int total1 = tm1.size();
            int total2 = tm2.size();
            
            System.out.println("\nSUMMARY:");
            System.out.println(source1Name + ": " + count1 + " with median / " + total1 + " total chemicals (" + 
                (100.0 * count1 / total1) + "%)");
            System.out.println(source2Name + ": " + count2 + " with median / " + total2 + " total chemicals (" + 
                (100.0 * count2 / total2) + "%)");
            System.out.println("Difference: " + Math.abs(count1 - count2) + " chemicals");
            
            // Show examples of each
            System.out.println("\n" + "-".repeat(80));
            printRecordsWithMedian(tm1, source1Name, 5);
            
            System.out.println("\n" + "-".repeat(80));
            printRecordsWithMedian(tm2, source2Name, 5);
            
            System.out.println("\n" + "-".repeat(80));
            printRecordsWithoutMedian(tm1, propertyName, units, source1Name + " (excluded)", 5);
            
            System.out.println("\n" + "-".repeat(80));
            printRecordsWithoutMedian(tm2, propertyName, units, source2Name + " (excluded)", 5);
            
					System.out.println("\n" + "=".repeat(80));
				}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CompareExperimentalRecords c=new CompareExperimentalRecords();

		c.c.compareBCF();

//		c.c.compareOralRat();

//		c.c.lookAtLLNA_MixtureVsNonMixtureNIEHS_ICE();//only 8?
//		c.c.compareSensitization();

		// c.c.compareRBiodeg();
		// c.c.compareRBiodeg_DebugOriginal();
		// c.c.analyzeRBiodeg_BadRecords();
		// c.c.analyzeRBiodeg_KeepFlag();
//		c.c.compareRBiodegFull();
		
//		c.c.compareKoc();
//		c.c.compareAquaticTox();
//		c.c.compareWS();
//		c.c.compareWS2();

	}

}

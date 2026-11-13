package gov.epa.exp_data_gathering.parse;

import java.text.DecimalFormat;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.EstimateParser.Estimate;
import gov.epa.exp_data_gathering.parse.EChemPortal.RecordEChemPortal;
import gov.epa.exp_data_gathering.parse.EChemPortal.RecordEChemPortal.RecordDegradation;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author TMARTI02
 */
public class BiodegradationPropertyValues {

//	public class OECD301Classifier {
//
//		private static final Map<String, Boolean> METHOD_301F = new HashMap<>();
//		private static final Map<String, Boolean> METHOD_301C = new HashMap<>();
//		private static final Map<String, Boolean> READY_BIODEG_MAP = new HashMap<>();
//				   
//				   
//		static {
//			// =========================
//			// 301F - TRUE: Valid 28-day BOD / ThOD / O2 Consumption Endpoints for QSAR
//			// =========================
//			put301F("BOD/ThOD", true);
//			put301F("degradation percentage was obtained from BOD/ThOD as per guidelines described in OECD 301 F",
//					true);
//			put301F("ThOD NH4", true);
//			put301F("% biodegradation: BOD / ThOD NH3 × 100", true);
//			put301F("% biodegradation based on ThOD NHO3", true); // IUCLID typo for NO3
//			put301F("BOD: ThOD ratio", true);
//			put301F("oxygen consumption", true); // Ensure parser filters for unit == "%"
//			put301F("% biodegradation = 100 × BOD / ThODNO3", true);
//			put301F("% biodegradation based on ThOD NH4", true);
//			put301F("% biodegradation (ThODNO3)", true);
//			put301F("ThOD (NH4)", true);
//			put301F("ThOD-NO3", true);
//			put301F("Theoretical Oxygen Demand (ThOD)NH4", true);
//			put301F("% biodegradation: BOD / ThOD NH4 × 100", true);
//			put301F("BOD & ThOD", true);
//			put301F("Percentage of Theoretical Oxygen Demand", true);
//			put301F("on the basis of values of BOD and ThOD", true);
//			put301F("% biodegradation: BOD / ThOD NO3 × 100", true);
//			put301F("ThODN03", true); // IUCLID typo (zero instead of O)
//			put301F("BOD of ThOD", true);
//			put301F("% biodegradation: 100 × BOD / ThODNO3", true);
//			put301F("% degradation (based on ThOD_NH4 and ThOD_NO3)", true);
//			put301F("Biochemical oxygen demand", true);
//			put301F("O2 uptake in the test solutions", true); // Verify unit == "%"
//			put301F("%BOD/ThOD", true);
//			put301F("BOD and ThOD", true);
//			put301F("Theoretical oxygen demand without nitrification (ThODN3) in %", true); // IUCLID typo for ThODNH3
//			put301F("% biodegradation (ThODNH4)", true);
//			put301F("% degradation (ThOD)", true);
//			put301F("THODNO3", true);
//			put301F("O2 consumption at 50 mg/L", true); // Ensure parser filters for unit == "%"
//			put301F("ThODNH4", true);
//			put301F("% degrdadability BOD and ThOD", true); // IUCLID typo
//			put301F("ThOD(NH4)", true);
//			put301F("blank-corrected BOD", true); // Ensure parser filters for unit == "%"
//			put301F("% degradation (O2 consumption)", true);
//			put301F("ThOD-NH4", true);
//			put301F("ThOD NO3", true);
//			put301F("BOD:ThOD ratio", true);
//			put301F("Theoretical oxygen demand", true); // Verify unit == "%"
//			put301F("% ThOD NO3 and ThOD NH4", true);
//			put301F("O2 consumption, TOC removal", true); // Ensure parser extracts O2 consumption (%)
//			put301F("% biodegradation: 100 × BOD / ThODNH4", true);
//			put301F("% biodegradation = 100 × BOD / ThODNH4", true);
//			put301F("ThODNO3", true);
//			put301F("ThOD (NO3)", true);
//			put301F("Percentage of biodegradation (Based on ThOD NH4)", true);
//			put301F("cumulative % ThOD, mean", true);
//			put301F("Theoretical oxygen demand with nitrification (ThODNO3) in %", true);
//
//			// eChemPortal 301F TRUE entries (also matches "other: ..." variants
//			// automatically)
//			put301F("ThOD", true);
//			put301F("ThOD Mean", true);
//			put301F("% ThOD", true);
//			put301F("%ThOD", true);
//			put301F("THOD (%)", true);
//			put301F("% biodegradation based on ThOD", true);
//			put301F("% degradation (O2 consumption) and ThOD", true);
//			put301F("BOD", true); // Ensure parser filters for unit == "%"
//			put301F("% biodegradation ThOD (NH4)", true);
//			put301F("% Biodegradation ThOD (NO3)", true);
//			put301F("% degradation based on ThODNH4", true);
//			put301F("% degradation based on BOD", true);
//			put301F("Theoretical Oxygen Demand (ThOD) [%]", true);
//			put301F("ThOD [%]", true);
//			put301F("Biochemical Oxygen Demand (BOD)", true);
//			put301F("% biodegradation = 100 × BOD / ThODNH4 Without nitrification", true);
//			put301F("% biodegradation = 100 × BOD / ThODNO3 With nitrification", true);
//			put301F("% degradation (BOD/ThODNH4)", true);
//			put301F("BOD (Biochemical Oxygen Demand)", true);
//			put301F("% degradation by BOD", true);
//			put301F("O2 consumption, DOC removal, CO2 formation", true); // Ensure parser extracts O2 consumption (%)
//
//			// =========================
//			// 301F - FALSE: Controls, 10-day window, BOD5, COD, Non-301F, and Unspecified
//			// =========================
//			put301F("% degradation (O2 consumption) Toxicity control", false); // Inoculum toxicity control, NOT test
//																				// item
//			put301F("% degradation (O2 consumption) 10-day window", false); // 10-day window metric, not 28-day endpoint
//			put301F("% degradation (O2 consumption) CBT", false); // Closed Bottle Test (OECD 301D)
//			put301F("% degradation (CO2 evolution) classic manometric method", false); // CO2 evolution (301B)
//			put301F("% degradation (test mat. analysis) THOD NH4", false); // Primary parent analysis
//			put301F("% degradation (test mat. analysis) ThOD NO3", false); // Primary parent analysis
//			put301F("BOD5*100/ThOD", false); // 5-day window, not 28-day
//			put301F("% degradation BOD/COD", false); // COD used for mixtures/UVCBs
//			put301F("BOD/COD", false);
//			put301F("BOD28/COD", false);
//			put301F("% of COD/ThOD NH4", false); // Chemical oxidation ratio, not biodegradation
//			put301F("BOD5*100/COD", false);
//			put301F("BSB5/CSB", false); // German for BOD5/COD
//			put301F("CSB", false); // German for COD
//			put301F("COD", false);
//			put301F("O2/C", false);
//			put301F("dissolved oxygen %", false);
//			put301F("BOD & residual amount", false);
//			put301F("residue analysis", false);
//			put301F("% degradation (test mat. analysis)", false);
//			put301F("% degradation (DOC removal)", false);
//			put301F("DOC removal and CO2 evolution", false);
//			put301F("% degradation (TOC removal)", false);
//			put301F("% degradation (CO2 evolution)", false);
//			put301F("% degradation (DNPOC)", false);
//			put301F("abiotic degradation", false);
//			put301F("manometric respiration test", false);
//			put301F("not specified", false);
//			put301F("% degradation", false);
//			put301F("biodegradation", false);
//			put301F("% biodegradation", false);
//			put301F("% Biodegradation", false);
//
//			// =========================
//			// 301C - TRUE: Valid Ultimate Biodegradation (BOD / ThOD / O2 Consumption) for
//			// QSAR
//			// =========================
//			put301C("BOD/ThOD", true);
//			put301C("BOD and GC", true); // Ensure parser extracts BOD value, not GC
//			put301C("Degree of degradation by BOD", true);
//			put301C("BOD versus ThOD", true);
//			put301C("BOD (NO2)", true);
//			put301C("Th BOD", true);
//			put301C("Biodegradation (corrected by the Inoculum control) expressed as the % of BOD versus ThOD", true);
//			put301C("BOD14", true); // 14-day MITI window vs standard 28-day 301C
//			put301C("Biochemical oxygen demand", true);
//			put301C("calculation based on BOD", true);
//			put301C("BOD and ThOD", true);
//			put301C("% degradation (ThOD)", true);
//			put301C("Indirect BOD", true);
//			put301C("BOD(NO2)", true);
//			put301C("BOD (NH3)", true);
//			put301C("degradability based on the BOD measurements", true);
//			put301C("% degradation (O2 consumption)", true);
//			put301C("BOD and UV-Vis", true); // Ensure parser extracts BOD value, not UV-Vis
//			put301C("BOD related to THOD", true);
//			put301C("BOD(NH3)", true);
//
//			// =========================
//			// 301C - FALSE: Primary Degradation Only (GC, HPLC, UV-Vis, Residual Parent)
//			// =========================
//			put301C("GC analysis", false);
//			put301C("UV-Vis", false);
//			put301C("GC Chromatography", false);
//			put301C("UV-VIS", false);
//			put301C("Percentage biodegradation by GC", false);
//			put301C("Gas chromatography", false);
//			put301C("% degradation (GC)", false);
//			put301C("percentage biodegradation of test item(GC)", false);
//			put301C("% biodegradation of the test item by GC", false);
//			put301C("GC", false);
//			put301C("Gas chromatography analysis", false);
//			put301C("% biodegradation by GC", false);
//			put301C("Percentage biodegration by GC", false);
//			put301C("HPLC Estimation Method", false);
//			put301C("% degradation (hplc)", false);
//			put301C("HPCL (%)", false);
//			put301C("percentage biodegradation of anionic component of test material (LC-MS)", false);
//			put301C("test mat. analysis by GC", false);
//			put301C("degradability based on the residual test substance concentration", false);
//			put301C("residual test substance", false);
//
//			// =========================
//			// 301C - FALSE: Non-301C Endpoints (DOC, TOC, CO2) & Unspecified/Invalid
//			// Strings
//			// =========================
//			put301C("% degradation (DOC removal)", false);
//			put301C("% degradation (TOC removal)", false);
//			put301C("Percentage degradation by DOC", false);
//			put301C("Total organic carbon", false);
//			put301C("% degradation (CO2 evolution)", false);
//			put301C("ThCO2", false);
//			put301C("% degradation (inorg. C analysis)", false);
//			put301C("% degradation (test mat. analysis)", false);
//			put301C("Detection rate of 5,5'-bi-1H-tetrazole", false);
//			put301C("Detection rate of ammonia", false);
//			put301C("% degradation based on AA measurement of lithium", false);
//			put301C("Detection rate of copper", false);
//			put301C("half-life in days (QSAR/QSPR)", false);
//			put301C("degradation %", false);
//			put301C("analytical determinations", false);
//			put301C("%", false);
//			put301C("Biodegradation", false);
//			put301C("not specified", false);
//			put301C("null", false);
//			put301C("Other", false);
//			
//			
//			// =========================
//			// 301C - Additional eChemPortal / MITI TRUE Entries
//			// =========================
//			put301C("BOD", true); // Ensure parser filters for unit == "%"
//			put301C("Biological oxygen demand", true);
//			put301C("BOD (%)", true);
//			put301C("BOD, TOC removal and analysis of test substance", true); // Ensure parser extracts BOD value
//			put301C("Degree of degradation by BOD       Degradability (%)= ((BOD - B)/ TOD) x 100 BOD: Biochemical oxygen demand of the system (sludge + test substance) (mg) B: Biochemical oxygen demand of sludge blank system (mg)TOD: Theoretical oxygen demand (calculated value assumuning 100% purity) (mg)", true);
//			put301C("O2 consumption (BOD)", true);
//			put301C("% degration (BOD)", true); // IUCLID typo
//			put301C("% degradation (BOD)", true);
//			put301C("% degradation (TOC removal) and BOD", true); // Ensure parser extracts BOD value
//			put301C("% biodegradation by BOD", true);
//			put301C("from BOD", true);
//			put301C("Observed by BOD measurements", true);
//			put301C("Percentage biodegradation by BOD", true);
//
//			// =========================
//			// 301C - Additional eChemPortal / MITI FALSE Entries
//			// =========================
//			put301C("% based on BOD Closed Bottle test", false); // OECD 301D, not 301C
//			put301C("ammonium-nitrogen analysis by HPLC", false);
//			put301C("HPLC", false);
//			put301C("HPLC method", false);
//			put301C("Degree of decomposition by HPLC Degradability (%) = ((Sw- Ss)/ Sw) x 100 // Ss  : Residual amount of test substance in the system (sludge + test substance) (measured value) (mg) // Sw : Residual amount of test substance in (water + test substance) system", false);
//			put301C("HPLC analysis", false);
//			put301C("HPLC results", false);
//			put301C("Test material analysis, HPLC", false);
//			put301C("Result by GC", false);
//			put301C("TOC", false);
//			put301C("Biodegradation in %", false);
//			
//			
//			putInterpretation("readily biodegradable", true);
//	        putInterpretation("extensively and rapidly biodegraded, reaching >60% biodegradation mark", true);
//
//	        // FALSE: Explicitly fails 10-day window, exceeds 28 days, or is only inherent/partial/not ready
//	        putInterpretation("readily biodegradable, but failing 10-day window", false);
//	        putInterpretation("not readily biodegradable", false);
//	        putInterpretation("under test conditions no biodegradation observed", false);
//	        putInterpretation("not readily biodegradable by this test, but complete mineralisation within 60 days", false);
//	        putInterpretation("inherently biodegradable", false);
//	        putInterpretation("the substance is potentially biodegradable", false);
//	        putInterpretation("Rapidly biodegradable and potentially readily biodegradable.", false);
//	        putInterpretation("Ultimate biodegradation under enhanced OECD 301 F conditions exceeded 60% on day 60.", false);
//	        putInterpretation("Not Readily biodegradable but inherently and Ultimately Biodegradable.", false);
//	        putInterpretation("inherently biodegradable, not fulfilling specific criteria", false);
//	        putInterpretation("inherently biodegradable, fulfilling specific criteria", false);
//	        putInterpretation("Not readily biodegradable but Inherently biodegradable", false);
//	        putInterpretation("biodegradable", false);
//	        putInterpretation("inherently and ultimately biodegradable", false);
//	        putInterpretation("The criterion for the OECD guidelines was not reached for the test article which means that it cannot be regarded as readily biodegradable.", false);
//	        putInterpretation("The criterion for the OECD guidelines was not reached for the test article Jeffcat ZF-20 (BAE) which means that it cannot be regarded as readily biodegradable.", false);
//	        putInterpretation("The substance is ultimately biodegradable and hence not persistent in the environment: Ultimate biodegradation under enhanced OECD 301 F conditions exceeded the trigger value of 60% on day 60. Hence the substance is not persistent in the environment.", false);
//	        putInterpretation("Not readily biodegradable but Inherently and ultimately biodegradable", false);
//	        putInterpretation("Moderately biodegradable", false);
//	        putInterpretation("Not readily biodegradable under the conditions of the OECD Guideline (59% after 28 days).", false);
//	        putInterpretation("partially biodegradable", false);
//	        putInterpretation("biodegradable, the pass level for ready biodegradability was not reached", false);
//	        putInterpretation("not inherently biodegradable", false);
//	        putInterpretation("Not readily biodegradable.", false);
//	        putInterpretation("Not readily biodegradable under the conditions of this test", false);
//	        putInterpretation("see considerations in the attached document", false);
//	        putInterpretation("Does not meet the criteria for ready biodegradability", false);
//	        putInterpretation("rapidly biodegradable", false);
//	        putInterpretation("not ready biodegradable", false);
//	        putInterpretation("not readily biodegradable under the respective test conditions, but overall/potential biodegradability has been indicated.", false);
//	        putInterpretation("test item is not ready biodegradable", false);
//	        putInterpretation("biodegradation seen but not meet the criteria of ready biodegradable", false);
//	        putInterpretation("Maximum biodegradation level of 42 % in 58 days", false);
//	        putInterpretation("Other", false);
//	        putInterpretation("The test substance is not ready biodegradable.", false);
//	        putInterpretation("not readily biodegradable, but moderate biodegradation was observed", false);
//	        putInterpretation("the tested substance is not readily biodegradable (60% biodegradation level not achieved after adaption time (10% degradation) of 11 days)", false);
//	        putInterpretation("Nor readily biodegradable according OECD criteria", false);
//	        putInterpretation("Time duration not sufficient in order to conclude.", false);
//	        putInterpretation("The substance is partly biodegradable (25%) under test conditions", false);
//	        putInterpretation("No conclusion is given as to whether the test substance can be considered as readily biodegradable.", false);
//	        putInterpretation("Not readily biodegradable but complete primary degradation", false);
//	        putInterpretation("The test item is considered to be Not Readily Biodegradable.", false);
//	        putInterpretation("not readily biodegradable under the chosen test conditions", false);
//	        putInterpretation("no readily biodegradable", false);
//	        putInterpretation("Hostanox SE 10 P has a potential for degradation", false);
//	        putInterpretation("not readily biodegradable, but biodegradation observed", false);
//	        putInterpretation("partial biodegradation observed", false);
//	        putInterpretation("under test conditions the test item is classified as not readily biodegradable", false);
//	        putInterpretation("observed biodegradation not sufficient for labeling as readily biodegradable", false);
//	        putInterpretation("poorly biodegradable", false);
//	        putInterpretation("Only limited biodegradation observed (8.6%/28 d)", false);
//	        putInterpretation("not readily biodegradable under test conditions", false);
//	        putInterpretation("Not readily biodegradable under the conditions in this test.", false);
//	        putInterpretation("Not readily biodegradable under the conditions in this test", false);
//	        putInterpretation("The substance is not readily biodegradable.", false);
//	        putInterpretation("readily / not readily / inherently biodegradable", false);
//	        putInterpretation("Whilst the the test material cannot be considered to be readily biodegradable under the strict terms and conditions of OECD Guideline 301F, the evidence for aerobic degradability (52.4 % after 28 days and 62.2 % after 56 days) could be shown.", false);
//	        putInterpretation("No biodegradation based on oxygen consumption, but specific analysis confirms degradation of the formate ester to the corresponding alcohol product.", false);
//	        putInterpretation("not readily biodegradable according to OECD criteria. However, the product is biodegradable after extended adaptation.", false);
//	        putInterpretation("biodegradable after adaption and in a prolonged test", false);
//	        putInterpretation("not readily bioderadable (according to OECD criteria), however, partly or moderately biodegradable.", false);
//	        putInterpretation("The test substance is not readily biodegradable under the test conditions.", false);
//	        putInterpretation("partly or moderately biodegradable and not readily biodegradable according to OECD criteria", false);
//	        putInterpretation("Not readily biodegradable; poorly biodegradable", false);
//	        putInterpretation("Not readily biodegradable according to OECD criteria.", false);
//	        putInterpretation("The test substance is poorly biodegradable and not readily biodegradable according to OECD criteria.", false);
//	        putInterpretation("not considered to be ready biodegradable", false);
//	        putInterpretation("Poorly biodegradable.", false);
//	        putInterpretation("The test substance can therefore not considered to be ready biodegradable.", false);
//	        putInterpretation("partly/moderate biodegradable", false);
//	        putInterpretation("BOD of ThOD; poorly biodegradable", false);
//	        putInterpretation("Biodegradable (not readily biodegradable according to OECD criteria)", false);
//	        putInterpretation("biodegradable, not readily biodegradable according to OECD criteria.", false);
//	        putInterpretation("moderately", false);
//	        putInterpretation("not readily biodegrdable (according to OECD criteria), partly or moderately biodegradable", false);
//	        putInterpretation("not readily biodegradable according to OECD criteria", false);
//	        putInterpretation("under test conditions some components are biodegradable, some are not", false);
//	        putInterpretation("slowly biodegraded", false);
//		}
//
//		public static boolean isValidFor301F(String method) {
//			return lookup(method, METHOD_301F, "301F");
//		}
//
//		public static boolean isValidFor301C(String method) {
//			return lookup(method, METHOD_301C, "301C");
//		}
//
//		/* Returns true ONLY if the IUCLID interpretation string indicates the substance
//	     * meets full OECD ready biodegradability criteria (>=60% within 28 days AND 10-day window).
//	     */
//	    public static boolean isReadilyBiodegradable(String interpretation) {
//	        if (interpretation == null || interpretation.isBlank()) {
//	            return false;
//	        }
//	        return READY_BIODEG_MAP.getOrDefault(interpretation.trim().toLowerCase(), false);
//	    }
//
//		private static boolean lookup(String method, Map<String, Boolean> dict, String label) {
//			if (method == null) {
//				return false;
//			}
//
//			String key = normalize(method);
//			if (key.isEmpty()) {
//				System.out.println("[" + label + "] method is empty after normalization: \"" + method + "\"");
//				return false;
//			}
//
//			// 1. Exact normalized dictionary lookup (handles explicit true & false overrides)
//			Boolean v = dict.get(key);
//			if (v != null) {
//				return v;
//			}
//
//			// 2. Smart fallback for IUCLID "<standard header> <free-text remarks>"
//			if ("301F".equals(label) || "301C".equals(label)) {
//				if (key.startsWith("% degradation o2 consumption ")) {
//					if (key.contains("toxicity control")
//							|| key.contains("reference")
//							|| key.contains("10 day window")
//							|| key.contains("cbt")
//							|| key.contains("closed bottle")) {
//						return false;
//					}
//					return true;
//				}
//
//				// Automatically reject free-text remarks appended to non-301F / non-301C endpoints
//				// (Note: exact overrides like "% degradation (TOC removal) and BOD" are matched in step 1 above!)
//				if (key.startsWith("% degradation test mat analysis ")
//						|| key.startsWith("% degradation doc removal ")
//						|| key.startsWith("% degradation toc removal ")
//						|| key.startsWith("% degradation co2 evolution ")
//						|| key.startsWith("% degradation dnpoc ")) {
//					return false;
//				}
//			}
//
//			System.out.println(
//					"[" + label + "] method not found in map: \"" + method + "\" | normalized=\"" + key + "\"");
//			return false;
//		}
//
//		
//		private static String normalize(String s) {
//			if (s == null) {
//				return "";
//			}
//			String norm = s.toLowerCase(Locale.ROOT)
//					.replace("×", "x")
//					.replaceAll("[\\p{Z}\\p{Cf}\\s\\-_()/.,:;]+", " ")
//					.trim();
//
//			// Strip IUCLID / eChemPortal "other:" prefix so "other: ThOD-NH4" matches "thod nh4"
//			if (norm.startsWith("other ")) {
//				norm = norm.substring(6).trim();
//			}
//			return norm;
//		}
//		
//		private static void put301F(String k, boolean v) {
//			METHOD_301F.put(normalize(k), v);
//		}
//
//		private static void put301C(String k, boolean v) {
//			METHOD_301C.put(normalize(k), v);
//		}
//		
//		private static void putInterpretation(String key, boolean isReadyBiodegradable) {
//	        if (key != null) {
//	            READY_BIODEG_MAP.put(key.trim().toLowerCase(), isReadyBiodegradable);
//	        }
//	    }
//	}

	public class OECD301Classifier {

		private static final Map<String, Boolean> METHOD_301F = new HashMap<>();
		private static final Map<String, Boolean> METHOD_301C = new HashMap<>();
		private static final Map<String, Boolean> READY_BIODEG_MAP = new HashMap<>();

		static {
			// =========================
			// 301F - TRUE: Valid 28-day BOD / ThOD / O2 Consumption Endpoints for QSAR
			// =========================
			put301F("BOD/ThOD", true);
			put301F("degradation percentage was obtained from BOD/ThOD as per guidelines described in OECD 301 F",
					true);
			put301F("ThOD NH4", true);
			put301F("% biodegradation: BOD / ThOD NH3 × 100", true);
			put301F("% biodegradation based on ThOD NHO3", true); // IUCLID typo for NO3
			put301F("BOD: ThOD ratio", true);
			put301F("oxygen consumption", true); // Ensure parser filters for unit == "%"
			put301F("% biodegradation = 100 × BOD / ThODNO3", true);
			put301F("% biodegradation based on ThOD NH4", true);
			put301F("% biodegradation (ThODNO3)", true);
			put301F("ThOD (NH4)", true);
			put301F("ThOD-NO3", true);
			put301F("Theoretical Oxygen Demand (ThOD)NH4", true);
			put301F("% biodegradation: BOD / ThOD NH4 × 100", true);
			put301F("BOD & ThOD", true);
			put301F("Percentage of Theoretical Oxygen Demand", true);
			put301F("on the basis of values of BOD and ThOD", true);
			put301F("% biodegradation: BOD / ThOD NO3 × 100", true);
			put301F("ThODN03", true); // IUCLID typo (zero instead of O)
			put301F("BOD of ThOD", true);
			put301F("% biodegradation: 100 × BOD / ThODNO3", true);
			put301F("% degradation (based on ThOD_NH4 and ThOD_NO3)", true);
			put301F("Biochemical oxygen demand", true);
			put301F("O2 uptake in the test solutions", true); // Verify unit == "%"
			put301F("%BOD/ThOD", true);
			put301F("BOD and ThOD", true);
			put301F("Theoretical oxygen demand without nitrification (ThODN3) in %", true); // IUCLID typo for ThODNH3
			put301F("% biodegradation (ThODNH4)", true);
			put301F("% degradation (ThOD)", true);
			put301F("THODNO3", true);
			put301F("O2 consumption at 50 mg/L", true); // Ensure parser filters for unit == "%"
			put301F("ThODNH4", true);
			put301F("% degrdadability BOD and ThOD", true); // IUCLID typo
			put301F("ThOD(NH4)", true);
			put301F("blank-corrected BOD", true); // Ensure parser filters for unit == "%"
			put301F("% degradation (O2 consumption)", true);
			put301F("ThOD-NH4", true);
			put301F("ThOD NO3", true);
			put301F("BOD:ThOD ratio", true);
			put301F("Theoretical oxygen demand", true); // Verify unit == "%"
			put301F("% ThOD NO3 and ThOD NH4", true);
			put301F("O2 consumption, TOC removal", true); // Ensure parser extracts O2 consumption (%)
			put301F("% biodegradation: 100 × BOD / ThODNH4", true);
			put301F("% biodegradation = 100 × BOD / ThODNH4", true);
			put301F("ThODNO3", true);
			put301F("ThOD (NO3)", true);
			put301F("Percentage of biodegradation (Based on ThOD NH4)", true);
			put301F("cumulative % ThOD, mean", true);
			put301F("Theoretical oxygen demand with nitrification (ThODNO3) in %", true);

			// eChemPortal 301F TRUE entries (also matches "other: ..." variants
			// automatically)
			put301F("ThOD", true);
			put301F("ThOD Mean", true);
			put301F("% ThOD", true);
			put301F("%ThOD", true);
			put301F("THOD (%)", true);
			put301F("% biodegradation based on ThOD", true);
			put301F("% degradation (O2 consumption) and ThOD", true);
			put301F("BOD", true); // Ensure parser filters for unit == "%"
			put301F("% biodegradation ThOD (NH4)", true);
			put301F("% Biodegradation ThOD (NO3)", true);
			put301F("% degradation based on ThODNH4", true);
			put301F("% degradation based on BOD", true);
			put301F("Theoretical Oxygen Demand (ThOD) [%]", true);
			put301F("ThOD [%]", true);
			put301F("Biochemical Oxygen Demand (BOD)", true);
			put301F("% biodegradation = 100 × BOD / ThODNH4 Without nitrification", true);
			put301F("% biodegradation = 100 × BOD / ThODNO3 With nitrification", true);
			put301F("% degradation (BOD/ThODNH4)", true);
			put301F("BOD (Biochemical Oxygen Demand)", true);
			put301F("% degradation by BOD", true);
			put301F("O2 consumption, DOC removal, CO2 formation", true); // Ensure parser extracts O2 consumption (%)

			// =========================
			// 301F - FALSE: Controls, 10-day window, BOD5, COD, Non-301F, and Unspecified
			// =========================
			put301F("% degradation (O2 consumption) Toxicity control", false); // Inoculum toxicity control, NOT test
																				// item
			put301F("% degradation (O2 consumption) 10-day window", false); // 10-day window metric, not 28-day endpoint
			put301F("% degradation (O2 consumption) CBT", false); // Closed Bottle Test (OECD 301D)
			put301F("% degradation (CO2 evolution) classic manometric method", false); // CO2 evolution (301B)
			put301F("% degradation (test mat. analysis) THOD NH4", false); // Primary parent analysis
			put301F("% degradation (test mat. analysis) ThOD NO3", false); // Primary parent analysis
			put301F("BOD5*100/ThOD", false); // 5-day window, not 28-day
			put301F("% degradation BOD/COD", false); // COD used for mixtures/UVCBs
			put301F("BOD/COD", false);
			put301F("BOD28/COD", false);
			put301F("% of COD/ThOD NH4", false); // Chemical oxidation ratio, not biodegradation
			put301F("BOD5*100/COD", false);
			put301F("BSB5/CSB", false); // German for BOD5/COD
			put301F("CSB", false); // German for COD
			put301F("COD", false);
			put301F("O2/C", false);
			put301F("dissolved oxygen %", false);
			put301F("BOD & residual amount", false);
			put301F("residue analysis", false);
			put301F("% degradation (test mat. analysis)", false);
			put301F("% degradation (DOC removal)", false);
			put301F("DOC removal and CO2 evolution", false);
			put301F("% degradation (TOC removal)", false);
			put301F("% degradation (CO2 evolution)", false);
			put301F("% degradation (DNPOC)", false);
			put301F("abiotic degradation", false);
			put301F("manometric respiration test", false);
			put301F("not specified", false);
			put301F("% degradation", false);
			put301F("biodegradation", false);
			put301F("% biodegradation", false);
			put301F("% Biodegradation", false);

			// =========================
			// 301C - TRUE: Valid Ultimate Biodegradation (BOD / ThOD / O2 Consumption) for
			// QSAR
			// =========================
			put301C("BOD/ThOD", true);
			put301C("BOD and GC", true); // Ensure parser extracts BOD value, not GC
			put301C("Degree of degradation by BOD", true);
			put301C("BOD versus ThOD", true);
			put301C("BOD (NO2)", true);
			put301C("Th BOD", true);
			put301C("Biodegradation (corrected by the Inoculum control) expressed as the % of BOD versus ThOD", true);
			put301C("BOD14", true); // 14-day MITI window vs standard 28-day 301C
			put301C("Biochemical oxygen demand", true);
			put301C("calculation based on BOD", true);
			put301C("BOD and ThOD", true);
			put301C("% degradation (ThOD)", true);
			put301C("Indirect BOD", true);
			put301C("BOD(NO2)", true);
			put301C("BOD (NH3)", true);
			put301C("degradability based on the BOD measurements", true);
			put301C("% degradation (O2 consumption)", true);
			put301C("BOD and UV-Vis", true); // Ensure parser extracts BOD value, not UV-Vis
			put301C("BOD related to THOD", true);
			put301C("BOD(NH3)", true);

			// eChemPortal / MITI 301C TRUE entries
			put301C("BOD", true); // Ensure parser filters for unit == "%"
			put301C("Biological oxygen demand", true);
			put301C("BOD (%)", true);
			put301C("BOD, TOC removal and analysis of test substance", true); // Ensure parser extracts BOD value
			put301C("Degree of degradation by BOD       Degradability (%)= ((BOD - B)/ TOD) x 100 BOD: Biochemical oxygen demand of the system (sludge + test substance) (mg) B: Biochemical oxygen demand of sludge blank system (mg)TOD: Theoretical oxygen demand (calculated value assumuning 100% purity) (mg)",
					true);
			put301C("O2 consumption (BOD)", true);
			put301C("% degration (BOD)", true); // IUCLID typo
			put301C("% degradation (BOD)", true);
			put301C("% degradation (TOC removal) and BOD", true); // Ensure parser extracts BOD value
			put301C("% biodegradation by BOD", true);
			put301C("from BOD", true);
			put301C("Observed by BOD measurements", true);
			put301C("Percentage biodegradation by BOD", true);
			put301C("% degradation (based on BOD)", true);
			put301C("BOD and HPLC", true); // Ensure parser extracts BOD value, not HPLC
			put301C("% degradation by ThOD", true);
			put301C("biochemical oxygen demand (BOD)", true); // Also covers "biochemical oxygen demand - BOD"
			put301C("Biological oxygen demand (BOD) measurements", true);
			put301C("Biodegradation by biochemical oxygen demand (BOD)", true);
			put301C("BOD measurements", true);
			put301C("% degradation by BOD", true);
			put301C("Biodegradability based on BOD measurement results", true);

			// =========================
			// 301C - FALSE: Primary Degradation Only (GC, HPLC, UV-Vis, Residual Parent)
			// =========================
			put301C("GC analysis", false);
			put301C("UV-Vis", false);
			put301C("GC Chromatography", false);
			put301C("UV-VIS", false);
			put301C("Percentage biodegradation by GC", false);
			put301C("Gas chromatography", false);
			put301C("% degradation (GC)", false);
			put301C("percentage biodegradation of test item(GC)", false);
			put301C("% biodegradation of the test item by GC", false);
			put301C("GC", false);
			put301C("Gas chromatography analysis", false);
			put301C("% biodegradation by GC", false);
			put301C("Percentage biodegration by GC", false);
			put301C("HPLC Estimation Method", false);
			put301C("% degradation (hplc)", false);
			put301C("HPCL (%)", false);
			put301C("percentage biodegradation of anionic component of test material (LC-MS)", false);
			put301C("test mat. analysis by GC", false);
			put301C("degradability based on the residual test substance concentration", false);
			put301C("residual test substance", false);
			put301C("ammonium-nitrogen analysis by HPLC", false);
			put301C("HPLC", false);
			put301C("HPLC method", false);
			put301C("Degree of decomposition by HPLC Degradability (%) = ((Sw- Ss)/ Sw) x 100 // Ss  : Residual amount of test substance in the system (sludge + test substance) (measured value) (mg) // Sw : Residual amount of test substance in (water + test substance) system",
					false);
			put301C("HPLC analysis", false);
			put301C("HPLC results", false);
			put301C("Test material analysis, HPLC", false);
			put301C("Result by GC", false);
			put301C("Percentage biodegradation of test item (HPLC)", false);
			put301C("residual ammoun of test substance (HPLC analysis)", false); // IUCLID typo
			put301C("% degradation by HPLC", false);
			// =========================
			// 301C - FALSE: Non-301C Endpoints (DOC, TOC, CO2) & Unspecified/Invalid
			// Strings
			// =========================
			put301C("% based on BOD Closed Bottle test", false); // OECD 301D, not 301C
			put301C("% degradation (DOC removal)", false);
			put301C("% degradation (TOC removal)", false);
			put301C("Percentage degradation by DOC", false);
			put301C("Percentage biodegradation by DOC", false);
			put301C("dissolved organic carbon (DOC)", false);
			put301C("residual amount of DOC", false);
			put301C("Total organic carbon", false);
			put301C("TOC", false);
			put301C("% degradation (CO2 evolution)", false);
			put301C("ThCO2", false);
			put301C("% degradation (inorg. C analysis)", false);
			put301C("% degradation (test mat. analysis)", false);
			put301C("Detection rate of 5,5'-bi-1H-tetrazole", false);
			put301C("Detection rate of ammonia", false);
			put301C("% degradation based on AA measurement of lithium", false);
			put301C("Detection rate of copper", false);
			put301C("half-life in days (QSAR/QSPR)", false);
			put301C("degradation %", false);
			put301C("analytical determinations", false);
			put301C("%", false);
			put301C("Biodegradation", false);
			put301C("Biodegradation in %", false);
			put301C("not specified", false);
			put301C("null", false);
			put301C("Other", false);
			
			//only have a couple that indicate pass the 10 day window:
			putInterpretation("readily biodegradable", true);
	        putInterpretation("extensively and rapidly biodegraded, reaching >60% biodegradation mark", true);

	        // FALSE: Explicitly fails 10-day window, exceeds 28 days, or is only inherent/partial/not ready
	        putInterpretation("readily biodegradable, but failing 10-day window", false);
	        putInterpretation("not readily biodegradable", false);
	        putInterpretation("under test conditions no biodegradation observed", false);
	        putInterpretation("not readily biodegradable by this test, but complete mineralisation within 60 days", false);
	        putInterpretation("inherently biodegradable", false);
	        putInterpretation("the substance is potentially biodegradable", false);
	        putInterpretation("Rapidly biodegradable and potentially readily biodegradable.", false);
	        putInterpretation("Ultimate biodegradation under enhanced OECD 301 F conditions exceeded 60% on day 60.", false);
	        putInterpretation("Not Readily biodegradable but inherently and Ultimately Biodegradable.", false);
	        putInterpretation("inherently biodegradable, not fulfilling specific criteria", false);
	        putInterpretation("inherently biodegradable, fulfilling specific criteria", false);
	        putInterpretation("Not readily biodegradable but Inherently biodegradable", false);
	        putInterpretation("biodegradable", false);
	        putInterpretation("inherently and ultimately biodegradable", false);
	        putInterpretation("The criterion for the OECD guidelines was not reached for the test article which means that it cannot be regarded as readily biodegradable.", false);
	        putInterpretation("The criterion for the OECD guidelines was not reached for the test article Jeffcat ZF-20 (BAE) which means that it cannot be regarded as readily biodegradable.", false);
	        putInterpretation("The substance is ultimately biodegradable and hence not persistent in the environment: Ultimate biodegradation under enhanced OECD 301 F conditions exceeded the trigger value of 60% on day 60. Hence the substance is not persistent in the environment.", false);
	        putInterpretation("Not readily biodegradable but Inherently and ultimately biodegradable", false);
	        putInterpretation("Moderately biodegradable", false);
	        putInterpretation("Not readily biodegradable under the conditions of the OECD Guideline (59% after 28 days).", false);
	        putInterpretation("partially biodegradable", false);
	        putInterpretation("biodegradable, the pass level for ready biodegradability was not reached", false);
	        putInterpretation("not inherently biodegradable", false);
	        putInterpretation("Not readily biodegradable.", false);
	        putInterpretation("Not readily biodegradable under the conditions of this test", false);
	        putInterpretation("see considerations in the attached document", false);
	        putInterpretation("Does not meet the criteria for ready biodegradability", false);
	        putInterpretation("rapidly biodegradable", false);
	        putInterpretation("not ready biodegradable", false);
	        putInterpretation("not readily biodegradable under the respective test conditions, but overall/potential biodegradability has been indicated.", false);
	        putInterpretation("test item is not ready biodegradable", false);
	        putInterpretation("biodegradation seen but not meet the criteria of ready biodegradable", false);
	        putInterpretation("Maximum biodegradation level of 42 % in 58 days", false);
	        putInterpretation("Other", false);
	        putInterpretation("The test substance is not ready biodegradable.", false);
	        putInterpretation("not readily biodegradable, but moderate biodegradation was observed", false);
	        putInterpretation("the tested substance is not readily biodegradable (60% biodegradation level not achieved after adaption time (10% degradation) of 11 days)", false);
	        putInterpretation("Nor readily biodegradable according OECD criteria", false);
	        putInterpretation("Time duration not sufficient in order to conclude.", false);
	        putInterpretation("The substance is partly biodegradable (25%) under test conditions", false);
	        putInterpretation("No conclusion is given as to whether the test substance can be considered as readily biodegradable.", false);
	        putInterpretation("Not readily biodegradable but complete primary degradation", false);
	        putInterpretation("The test item is considered to be Not Readily Biodegradable.", false);
	        putInterpretation("not readily biodegradable under the chosen test conditions", false);
	        putInterpretation("no readily biodegradable", false);
	        putInterpretation("Hostanox SE 10 P has a potential for degradation", false);
	        putInterpretation("not readily biodegradable, but biodegradation observed", false);
	        putInterpretation("partial biodegradation observed", false);
	        putInterpretation("under test conditions the test item is classified as not readily biodegradable", false);
	        putInterpretation("observed biodegradation not sufficient for labeling as readily biodegradable", false);
	        putInterpretation("poorly biodegradable", false);
	        putInterpretation("Only limited biodegradation observed (8.6%/28 d)", false);
	        putInterpretation("not readily biodegradable under test conditions", false);
	        putInterpretation("Not readily biodegradable under the conditions in this test.", false);
	        putInterpretation("Not readily biodegradable under the conditions in this test", false);
	        putInterpretation("The substance is not readily biodegradable.", false);
	        putInterpretation("readily / not readily / inherently biodegradable", false);
	        putInterpretation("Whilst the the test material cannot be considered to be readily biodegradable under the strict terms and conditions of OECD Guideline 301F, the evidence for aerobic degradability (52.4 % after 28 days and 62.2 % after 56 days) could be shown.", false);
	        putInterpretation("No biodegradation based on oxygen consumption, but specific analysis confirms degradation of the formate ester to the corresponding alcohol product.", false);
	        putInterpretation("not readily biodegradable according to OECD criteria. However, the product is biodegradable after extended adaptation.", false);
	        putInterpretation("biodegradable after adaption and in a prolonged test", false);
	        putInterpretation("not readily bioderadable (according to OECD criteria), however, partly or moderately biodegradable.", false);
	        putInterpretation("The test substance is not readily biodegradable under the test conditions.", false);
	        putInterpretation("partly or moderately biodegradable and not readily biodegradable according to OECD criteria", false);
	        putInterpretation("Not readily biodegradable; poorly biodegradable", false);
	        putInterpretation("Not readily biodegradable according to OECD criteria.", false);
	        putInterpretation("The test substance is poorly biodegradable and not readily biodegradable according to OECD criteria.", false);
	        putInterpretation("not considered to be ready biodegradable", false);
	        putInterpretation("Poorly biodegradable.", false);
	        putInterpretation("The test substance can therefore not considered to be ready biodegradable.", false);
	        putInterpretation("partly/moderate biodegradable", false);
	        putInterpretation("BOD of ThOD; poorly biodegradable", false);
	        putInterpretation("Biodegradable (not readily biodegradable according to OECD criteria)", false);
	        putInterpretation("biodegradable, not readily biodegradable according to OECD criteria.", false);
	        putInterpretation("moderately", false);
	        putInterpretation("not readily biodegrdable (according to OECD criteria), partly or moderately biodegradable", false);
	        putInterpretation("not readily biodegradable according to OECD criteria", false);
	        putInterpretation("under test conditions some components are biodegradable, some are not", false);
	        putInterpretation("slowly biodegraded", false);
		}

		public static boolean isValidFor301F(String method) {
			return lookup(method, METHOD_301F, "301F");
		}

		public static boolean isValidFor301C(String method) {
			return lookup(method, METHOD_301C, "301C");
		}

		private static boolean lookup(String method, Map<String, Boolean> dict, String label) {
			if (method == null) {
				return false;
			}

			String key = normalize(method);
			if (key.isEmpty()) {
				System.out.println("[" + label + "] method is empty after normalization: \"" + method + "\"");
				return false;
			}

			// 1. Exact normalized dictionary lookup (handles explicit true & false overrides)
			Boolean v = dict.get(key);
			if (v != null) {
				return v;
			}

			// 2. Smart fallback for IUCLID "<standard header> <free-text remarks>"
			if ("301F".equals(label) || "301C".equals(label)) {
				if (key.startsWith("% degradation o2 consumption ")) {
					if (key.contains("toxicity control")
							|| key.contains("reference")
							|| key.contains("10 day window")
							|| key.contains("cbt")
							|| key.contains("closed bottle")) {
						return false;
					}
					return true;
				}

				// Automatically reject free-text remarks appended to non-301F / non-301C endpoints
				if (key.startsWith("% degradation test mat analysis ")
						|| key.startsWith("% degradation doc removal ")
						|| key.startsWith("% degradation toc removal ")
						|| key.startsWith("% degradation co2 evolution ")
						|| key.startsWith("% degradation dnpoc ")) {
					return false;
				}
			}

			System.out.println(
					"[" + label + "] method not found in map: \"" + method + "\" | normalized=\"" + key + "\"");
			return false;
		}


		private static String normalize(String s) {
			if (s == null) {
				return "";
			}
			String norm = s.toLowerCase(Locale.ROOT)
					.replace("×", "x")
					.replaceAll("[\\p{Z}\\p{Cf}\\s\\-_()/.,:;]+", " ")
					.trim();

			// Strip IUCLID / eChemPortal "other:" prefix so "other: ThOD-NH4" matches "thod nh4"
			if (norm.startsWith("other ")) {
				norm = norm.substring(6).trim();
			}
			return norm;
		}


		private static void put301F(String k, boolean v) {
			METHOD_301F.put(normalize(k), v);
		}

		private static void put301C(String k, boolean v) {
			METHOD_301C.put(normalize(k), v);
		}
		
		private static void putInterpretation(String key, boolean isReadyBiodegradable) {
	        if (key != null) {
	            READY_BIODEG_MAP.put(key.trim().toLowerCase(), isReadyBiodegradable);
	        }
		}
	    
		
		public static boolean isReadilyBiodegradable(String interpretation) {
			if (interpretation == null || interpretation.isBlank()) {
				return false;
			}
			return READY_BIODEG_MAP.getOrDefault(interpretation.trim().toLowerCase(), false);
		}

	}

	public static class ResultBinaryScore {
		public ResultBinaryScore(Integer score, String reason) {
			this.score = score;
			this.reason = reason;
		}

		public ResultBinaryScore() {
		}

		public Integer score;
		public String reason;
	}

	private static void convertToContinuous(ExperimentalRecord er, Estimate estimate, Double duration) {
		// Constants
		final double DAYS = 28.0;
		final double EPS = 0.01; // tolerance for ~28 days
		final double LOW = 5.0; // 5%
		final double HIGH = 95.0; // 95%

		// Setup
		er.property_name = ExperimentalConstants.strPercentageBiodegradation;
		er.property_value_units_final = ExperimentalConstants.str_dimensionless;

		er.property_value_point_estimate_original = estimate.point;
		er.property_value_min_original = estimate.min;
		er.property_value_max_original = estimate.max;

		boolean hasPoint = estimate.point != null;
		boolean hasMin = estimate.min != null;
		boolean hasMax = estimate.max != null;

		setPropertyValueString(er, estimate, duration);

		// ~28 days
		if (Math.abs(duration - DAYS) < EPS) {
			if (hasPoint) {
				setPointWithClamp(er, estimate.point);
				return;
			}
			if (hasMin && hasMax) {
				er.property_value_min_final = estimate.min;
				er.property_value_max_final = estimate.max;
				return; // dataset creation step will decide usability
			}
			if (hasMax) {
				if (estimate.max <= LOW) {
					er.property_value_point_estimate_final = 0.0;
					er.updateNote("degradation < 5%, rounded to zero");
				} else {
					er.property_value_max_final = estimate.max;
					er.keep = false;
					er.reason = "have 28 day duration with a max value > 5%";
				}
				return;
			}
			if (hasMin) {
				if (estimate.min >= HIGH) {
					er.property_value_point_estimate_final = 100.0;
					er.updateNote("degradation > 95%, rounded to 100%");
				} else {
					er.property_value_min_final = estimate.min;
					er.keep = false;
					er.reason = "have 28 day duration with a min value < 95%";
				}
				return;
			}
			return; // nothing else to do
		}

		// >28 days
		if (duration > DAYS) {
			if (hasPoint) {
				if (estimate.point <= LOW) {
					er.property_value_point_estimate_final = 0.0;
					er.updateNote("degradation (" + estimate.point + "%) < 5%, set to 0% for duration = " + duration
							+ " days");
				} else {
					er.keep = false;
					er.updateReason("Have > 28 day duration with point estimate > 5%");
				}
			} else {
				er.keep = false;
				er.updateReason("Dont have point estimate for duration > 28 days");
			}
			return;
		}

		// <28 days
		if (hasPoint) {
			if (estimate.point >= HIGH) {
				er.property_value_point_estimate_final = 100.0;
				er.updateNote("degradation (" + estimate.point + "%) >= 95%, set to 100% for duration = " + duration
						+ " days");
			} else {
				er.keep = false;
				er.updateReason("Have < 28 duration with point estimate < 95%");
			}
		} else {
			er.keep = false;
			er.updateReason("Dont have point estimate for duration < 28 days");
		}
	}

	private static void setPointWithClamp(ExperimentalRecord er, double point) {
		if (point > 100.0) {
			er.property_value_point_estimate_final = 100.0;
			er.updateNote("Degradation exceeds 100%, setting to 100%");
		} else if (point < 0.0) {
			er.property_value_point_estimate_final = 0.0;
			er.updateNote("Degradation < 0%, setting to 0%");
		} else {
			er.property_value_point_estimate_final = point;
		}
	}

	public static void setPropertyValues(ExperimentalRecord er, Estimate estimate, Double duration,
			String interpretation) {
		er.property_value_units_original = ExperimentalConstants.str_dimensionless;

		if (er.property_name.equals(ExperimentalConstants.strRBIODEG)) {
			convertToBinary(er, estimate, duration);

		} else if (er.property_name.equals(ExperimentalConstants.strRBIODEG_10_day)) {
			convertToBinarywith10day(er, estimate, duration, interpretation);

		} else if (er.property_name.equalsIgnoreCase(ExperimentalConstants.strPercentageBiodegradation)) {
			convertToContinuous(er, estimate, duration);
		}
	}

//	public class OECD301Classifier {
//
//		private static final Map<String, Boolean> METHOD_301F = new HashMap<>();
//		private static final Map<String, Boolean> METHOD_301C = new HashMap<>();
//
//		static {
//			// =========================
//			// 301F
//			// =========================
//			// TRUE: Valid 28-day BOD / ThOD / O2 Consumption Endpoints for QSAR
//			put301F("BOD/ThOD", true); // Added missing entry
//			put301F("degradation percentage was obtained from BOD/ThOD as per guidelines described in OECD 301 F",
//					true); // Added missing entry
//			put301F("ThOD NH4", true);
//			put301F("% biodegradation: BOD / ThOD NH3 × 100", true);
//			put301F("% biodegradation based on ThOD NHO3", true); // IUCLID typo for NO3
//			put301F("BOD: ThOD ratio", true);
//			put301F("oxygen consumption", true); // Ensure parser filters for unit == "%"
//			put301F("% biodegradation = 100 × BOD / ThODNO3", true);
//			put301F("% biodegradation based on ThOD NH4", true);
//			put301F("% biodegradation (ThODNO3)", true);
//			put301F("ThOD (NH4)", true);
//			put301F("ThOD-NO3", true);
//			put301F("Theoretical Oxygen Demand (ThOD)NH4", true);
//			put301F("% biodegradation: BOD / ThOD NH4 × 100", true);
//			put301F("BOD & ThOD", true);
//			put301F("Percentage of Theoretical Oxygen Demand", true);
//			put301F("on the basis of values of BOD and ThOD", true);
//			put301F("% biodegradation: BOD / ThOD NO3 × 100", true);
//			put301F("ThODN03", true); // IUCLID typo (zero instead of O)
//			put301F("BOD of ThOD", true);
//			put301F("% biodegradation: 100 × BOD / ThODNO3", true);
//			put301F("% degradation (based on ThOD_NH4 and ThOD_NO3)", true);
//			put301F("Biochemical oxygen demand", true);
//			put301F("O2 uptake in the test solutions", true); // Changed from false -> true (verify unit == "%")
//			put301F("%BOD/ThOD", true);
//			put301F("BOD and ThOD", true);
//			put301F("Theoretical oxygen demand without nitrification (ThODN3) in %", true); // IUCLID typo for ThODNH3
//			put301F("% biodegradation (ThODNH4)", true);
//			put301F("% degradation (ThOD)", true);
//			put301F("THODNO3", true);
//			put301F("O2 consumption at 50 mg/L", true); // Ensure parser filters for unit == "%"
//			put301F("ThODNH4", true);
//			put301F("% degrdadability BOD and ThOD", true); // IUCLID typo
//			put301F("ThOD(NH4)", true);
//			put301F("blank-corrected BOD", true); // Ensure parser filters for unit == "%"
//			put301F("% degradation (O2 consumption)", true);
//			put301F("ThOD-NH4", true);
//			put301F("ThOD NO3", true);
//			put301F("BOD:ThOD ratio", true);
//			put301F("Theoretical oxygen demand", true); // Changed from false -> true (verify unit == "%")
//			put301F("% ThOD NO3 and ThOD NH4", true);
//			put301F("O2 consumption, TOC removal", true); // Ensure parser extracts O2 consumption (%)
//			put301F("% biodegradation: 100 × BOD / ThODNH4", true);
//			put301F("% biodegradation = 100 × BOD / ThODNH4", true);
//			put301F("ThODNO3", true);
//			put301F("ThOD (NO3)", true);
//			put301F("Percentage of biodegradation (Based on ThOD NH4)", true);
//			put301F("cumulative % ThOD, mean", true);
//			put301F("Theoretical oxygen demand with nitrification (ThODNO3) in %", true);
//
//			// FALSE: BOD5, COD-Normalized, Non-301F Endpoints, and Unspecified Strings
//			put301F("BOD5*100/ThOD", false); // Changed from true -> false (5-day window, not 28-day)
//			put301F("% degradation BOD/COD", false); // Changed from true -> false (COD used for mixtures/UVCBs)
//			put301F("BOD/COD", false); // Changed from true -> false
//			put301F("BOD28/COD", false); // Changed from true -> false
//			put301F("% of COD/ThOD NH4", false); // Changed from true -> false (chemical oxidation ratio, not
//													// biodegradation)
//			put301F("BOD5*100/COD", false);
//			put301F("BSB5/CSB", false); // German for BOD5/COD
//			put301F("CSB", false); // German for COD
//			put301F("COD", false);
//			put301F("O2/C", false);
//			put301F("dissolved oxygen %", false);
//			put301F("BOD & residual amount", false);
//			put301F("residue analysis", false);
//			put301F("% degradation (test mat. analysis)", false);
//			put301F("% degradation (DOC removal)", false);
//			put301F("DOC removal and CO2 evolution", false);
//			put301F("% degradation (TOC removal)", false);
//			put301F("% degradation (CO2 evolution)", false);
//			put301F("% degradation (DNPOC)", false);
//			put301F("abiotic degradation", false);
//			put301F("manometric respiration test", false);
//			put301F("not specified", false);
//			put301F("% degradation", false);
//			put301F("biodegradation", false);
//			put301F("% biodegradation", false);
//			put301F("% Biodegradation", false);
//
//			// TRUE: Valid Ultimate Biodegradation (BOD / ThOD / O2 Consumption) for QSAR
//			put301C("BOD/ThOD", true);
//			put301C("BOD and GC", true); // Ensure parser extracts BOD value, not GC
//			put301C("Degree of degradation by BOD", true);
//			put301C("BOD versus ThOD", true);
//			put301C("BOD (NO2)", true);
//			put301C("Th BOD", true);
//			put301C("Biodegradation (corrected by the Inoculum control) expressed as the % of BOD versus ThOD", true);
//			put301C("BOD14", true); // Note: 14-day MITI window vs standard 28-day 301C
//			put301C("Biochemical oxygen demand", true);
//			put301C("calculation based on BOD", true);
//			put301C("BOD and ThOD", true);
//			put301C("% degradation (ThOD)", true);
//			put301C("Indirect BOD", true);
//			put301C("BOD(NO2)", true);
//			put301C("BOD (NH3)", true);
//			put301C("degradability based on the BOD measurements", true);
//			put301C("% degradation (O2 consumption)", true); // Changed from false -> true
//			put301C("BOD and UV-Vis", true); // Ensure parser extracts BOD value, not UV-Vis
//			put301C("BOD related to THOD", true);
//			put301C("BOD(NH3)", true);
//
//			// FALSE: Primary Degradation Only (GC, HPLC, UV-Vis, Residual Parent) -
//			// Excludes from QSAR
//			put301C("GC analysis", false); // Changed from true -> false
//			put301C("UV-Vis", false); // Changed from true -> false
//			put301C("GC Chromatography", false); // Changed from true -> false
//			put301C("UV-VIS", false); // Changed from true -> false
//			put301C("Percentage biodegradation by GC", false); // Changed from true -> false
//			put301C("Gas chromatography", false); // Changed from true -> false
//			put301C("% degradation (GC)", false); // Changed from true -> false
//			put301C("percentage biodegradation of test item(GC)", false); // Changed from true -> false
//			put301C("% biodegradation of the test item by GC", false); // Changed from true -> false
//			put301C("GC", false); // Changed from true -> false
//			put301C("Gas chromatography analysis", false); // Changed from true -> false
//			put301C("% biodegradation by GC", false); // Changed from true -> false
//			put301C("Percentage biodegration by GC", false); // Changed from true -> false
//			put301C("HPLC Estimation Method", false);
//			put301C("% degradation (hplc)", false);
//			put301C("HPCL (%)", false);
//			put301C("percentage biodegradation of anionic component of test material (LC-MS)", false);
//			put301C("test mat. analysis by GC", false);
//			put301C("degradability based on the residual test substance concentration", false);
//			put301C("residual test substance", false);
//
//			// FALSE: Non-301C Endpoints (DOC, TOC, CO2) & Unspecified/Invalid Strings
//			put301C("% degradation (DOC removal)", false);
//			put301C("% degradation (TOC removal)", false);
//			put301C("Percentage degradation by DOC", false);
//			put301C("Total organic carbon", false);
//			put301C("% degradation (CO2 evolution)", false);
//			put301C("ThCO2", false);
//			put301C("% degradation (inorg. C analysis)", false);
//			put301C("% degradation (test mat. analysis)", false);
//			put301C("Detection rate of 5,5'-bi-1H-tetrazole", false);
//			put301C("Detection rate of ammonia", false);
//			put301C("% degradation based on AA measurement of lithium", false);
//			put301C("Detection rate of copper", false);
//			put301C("half-life in days (QSAR/QSPR)", false);
//			put301C("degradation %", false);
//			put301C("analytical determinations", false);
//			put301C("%", false);
//			put301C("Biodegradation", false);
//			put301C("not specified", false);
//			put301C("null", false);
//			put301C("Other", false);
//		}
//
//		public static boolean isValidFor301F(String method) {
//			return lookup(method, METHOD_301F, "301F");
//		}
//
//		public static boolean isValidFor301C(String method) {
//			return lookup(method, METHOD_301C, "301C");
//		}
//
//		private static boolean lookup(String method, Map<String, Boolean> dict, String label) {
//			if (method == null) {
//				return false;
//			}
//
//			String key = normalize(method);
//			if (key.isEmpty()) {
//				System.out.println("[" + label + "] method is empty after normalization: \"" + method + "\"");
//				return false;
//			}
//
//			Boolean v = dict.get(key);
//			if (v != null) {
//				return v;
//			}
//
//			System.out.println(
//					"[" + label + "] method not found in map: \"" + method + "\" | normalized=\"" + key + "\"");
//			return false;
//		}
//
//		private static String normalize(String s) {
//			if (s == null) {
//				return "";
//			}
//			return s.toLowerCase(Locale.ROOT).replace("×", "x")
//					// Handles Unicode whitespace (\p{Z}), invisible format chars (\p{Cf}), and
//					// punctuation
//					.replaceAll("[\\p{Z}\\p{Cf}\\s\\-_()/.,:;]+", " ").trim();
//		}
//
//		private static void put301F(String k, boolean v) {
//			METHOD_301F.put(normalize(k), v);
//		}
//
//		private static void put301C(String k, boolean v) {
//			METHOD_301C.put(normalize(k), v);
//		}
//	}

	public static Set<String> methods = new HashSet<>();

	public static boolean isValidGuidelineCompliance(String value) {
		if (value == null)
			return false;

		String s = value.trim().toLowerCase(Locale.ROOT);
		if (s.isEmpty())
			return false;

		// Explicit negatives / non-compliance / unknown
		if (containsAny(s, "not specified", "no ", "no,", "no certificate", "no certificate included",
				"no details available", "not glp", "not conducted under glp", "not performed under glp",
				"not performed according to international guideline", "not specified to be glp compliant",
				"not internationally recognized", "not required at time of test initiation", "screening", "pre-test",
				"preliminary study", "internal study", "academic setting without glp certification",
				"not glp accredited", "glp compliance claim ... rescinded", "study was initially glp compliant" // if
																												// rescinded
																												// later
		)) {
			return false;
		}

		// Explicit positive / acceptable compliance evidence
		if (containsAny(s, "yes", "glp", "good laboratory practice", "oecd glp", "glp statement",
				"glp compliance statement", "glp compliance inspected", "glp certified", "glp certified lab",
				"glp certificate", "certificate included", "certificate issued", "certificate of compliance",
				"qa statement", "quality assurance", "iso/iec 17025", "sn en 45001",
				"oe cd principles of good laboratory practice", "principles of good laboratory practice",
				"guidelines of chemical testing good laboratory practices", "china glp", "sepa glp", "da kk s",
				"dakk s", "accredited")) {
			return true;
		}

		// Special cases:
		// If it starts with "yes" or includes "yes (incl. QA statement)" etc.
		if (s.startsWith("yes")) {
			return true;
		}

		// If it mentions GLP compliance but not clearly negative, accept
		if (s.contains("glp") || s.contains("good laboratory practice")) {
			return true;
		}

		return false;
	}

	public static ResultBinaryScore determineRBiodegScore(RecordEChemPortal re, String outputTestGuideline) {

		BiodegEvidence evidence = hasAtLeast60PercentAt28Days(re, outputTestGuideline);

		switch (evidence) {
		case ABOVE_60:
			return new ResultBinaryScore(1, ">=60% degradation at 28 days");

		case BELOW_60:
			return new ResultBinaryScore(0, "<60% degradation at 28 days");

		case CONFLICT:
			return new ResultBinaryScore(null, "Conflicting degradation values at 28 days");

		case NONE:
		default:
			return new ResultBinaryScore(null, "No usable evidence of >=60% degradation at 28 days");

		}
	}

	public static ResultBinaryScore determineRBiodegScoreWith10dayWindow(RecordEChemPortal re,
			String outputTestGuideline) {
		String interp = re.interpretationOfResults == null ? ""
				: re.interpretationOfResults.trim().toLowerCase(Locale.ROOT);

		// 1) Check whether interpretation is clearly positive
//		boolean interpretationPositive = isPositiveReadyBiodegradableInterpretation(interp);

		boolean interpretationPositive = BiodegradationPropertyValues.OECD301Classifier.isReadilyBiodegradable(interp);

		if (!interpretationPositive) {
			return new ResultBinaryScore(0, "Interpretation does not indicate ready biodegradability");
		}

		// 2) Check whether any degradation record reaches >= 60% at 28 days
		BiodegEvidence evidence = hasAtLeast60PercentAt28Days(re, outputTestGuideline);

		switch (evidence) {

		case CONFLICT:
			return new ResultBinaryScore(null, "Conflicting degradation values at 28 days");
		case BELOW_60:
			return new ResultBinaryScore(0, "<60% degradation at 28 days");
		case ABOVE_60:
			return new ResultBinaryScore(1, "Ready biodegradable interpretation and >=60% degradation at 28 days");

		case NONE:
		default:
			return new ResultBinaryScore(null, "No usable evidence of >=60% degradation at 28 days");
		}

	}

	public static ResultBinaryScore determineRBiodegScoreMulticlass(RecordEChemPortal re, String outputTestGuideline) {
		String interp = re.interpretationOfResults == null ? ""
				: re.interpretationOfResults.trim().toLowerCase(Locale.ROOT);

//		boolean interpretationPositive = isPositiveReadyBiodegradableInterpretation(interp);
		boolean interpretationPositive = BiodegradationPropertyValues.OECD301Classifier.isReadilyBiodegradable(interp);

		BiodegEvidence evidence = hasAtLeast60PercentAt28Days(re, outputTestGuideline);

		switch (evidence) {
		case ABOVE_60:
			if (interpretationPositive) {
				return new ResultBinaryScore(2, "Ready biodegradable interpretation and >=60% degradation at 28 days");
			} else {
				return new ResultBinaryScore(1, ">=60% degradation at 28 days but interpretation not clearly positive");
			}

		case BELOW_60:
			return new ResultBinaryScore(0, "<60% degradation at 28 days");

		case CONFLICT:
			return new ResultBinaryScore(null, "Conflicting degradation values at 28 days");

		case NONE:
		default:
			return new ResultBinaryScore(null, "No usable evidence of >=60% degradation at 28 days");
		}
	}

	private static boolean containsAny(String s, String... patterns) {
		String lower = s.toLowerCase(Locale.ROOT);
		for (String p : patterns) {
			if (lower.contains(p.toLowerCase(Locale.ROOT))) {
				return true;
			}
		}
		return false;
	}

	private static Double parseDoubleSafe(String s) {
		if (s == null)
			return null;
		try {
			return Double.parseDouble(s.trim());
		} catch (Exception e) {
			return null;
		}
	}

	public enum BiodegEvidence {
		NONE, // no usable 28-day evidence
		BELOW_60, // evidence indicates <60% at 28 days
		ABOVE_60, // evidence indicates >=60% at 28 days
		CONFLICT // mixed evidence
	}

	private static BiodegEvidence hasAtLeast60PercentAt28Days(RecordEChemPortal re, String outputTestGuideline) {

		List<RecordDegradation> records = re.recordsDegradation;
		if (records == null || records.isEmpty())
			return BiodegEvidence.NONE;

		boolean sawBelow60 = false;
		boolean sawAbove60 = false;

		for (RecordDegradation r : records) {
			if (r == null)
				continue;

			methods.add(r.parameter);

			if (outputTestGuideline.equals(ExperimentalConstants.str301F)) {

				if (!OECD301Classifier.isValidFor301F(r.parameter)) {
					continue;
				}

			} else if (outputTestGuideline.equals(ExperimentalConstants.str301C)) {
				if (!OECD301Classifier.isValidFor301C(r.parameter)) {
					continue;
				}
			}

			Estimate estimate = EstimateParser.parse(r.degradationValue);
			ResultBinaryScore rbs = determineResultBinaryScore60PercentWithin28days(estimate, r.samplingTimeDays);

			if (rbs.score != null) {
				if (rbs.score == 1) {
					sawAbove60 = true;
				} else if (rbs.score == 0) {
					sawBelow60 = true;
				}
			}
		}

		if (sawAbove60 && sawBelow60) {
			return BiodegEvidence.CONFLICT;
		}
		if (sawAbove60) {
			return BiodegEvidence.ABOVE_60;
		}
		if (sawBelow60) {
			return BiodegEvidence.BELOW_60;
		}
		return BiodegEvidence.NONE;
	}

//	private static boolean isPositiveReadyBiodegradableInterpretation(String s) {
//		if (s == null || s.isEmpty())
//			return false;
//
//		if (containsAny(s, "no biodegradation observed", "under test conditions no biodegradation observed",
//				"not readily biodegradable", "not ready biodegradable", "not considered to be ready biodegradable",
//				"does not meet the criteria for ready biodegradability", "the substance is not readily biodegradable",
//				"the test substance is not ready biodegradable", "not readily biodegradability",
//				"no readily biodegradable", "poorly biodegradable", "negligible biodegradation",
//				"observed biodegradation not sufficient for labeling as readily biodegradable",
//				"not readily biodegradable under the conditions of this test",
//				"not readily biodegradable under the conditions in this test",
//				"not readily biodegradable under test conditions",
//				"not readily biodegradable according to oecd criteria",
//				"not readily biodegradable after 28 days of incubation",
//				"the test item is considered to be \\not readily biodegradable\\")) {
//			return false;
//		}
//
//		// Positive phrases
//		if (containsAny(s, "readily biodegradable", "rapidly biodegradable", "extensively and rapidly biodegraded",
//				"ready and completely biodegradable", "considered as readily biodegradable",
//				"considered to be readily biodegradable", "can therefore be considered as readily biodegradable",
//				"can be considered to be readily biodegradable",
//				"the test item can therefore be considered as readily biodegradable",
//				"the test substance can therefore be considered to be readily biodegradable",
//				"the test item was found to be ready and completely biodegradable",
//				"readily biodegradable according to oecd criteria", "readily biodegradable based on thodnh4",
//				"the test item was found to be ready and completely biodegradable under the conditions applied in a manometric respirometry test")) {
//			// If it explicitly says 10-day window failed and no exemption applies, do not
//			// count as ready
//			if (containsAny(s, "failing 10-day window", "10-day window criterion is not fulfilled",
//					"not fulfilling the 10-day window", "did not fulfill the 10-day window",
//					"did not meet the 10-day window")) {
//				// You said degradation records usually don't tell us enough about the 10-day
//				// window,
//				// so treat failing-window text as NOT ready unless the text explicitly says
//				// exemption applies.
//				if (containsAny(s, "does not apply", "criterion does not apply", "window does not apply", "uvcb",
//						"multiconstituent", "multi constituent", "multi-constituent")) {
//					return true;
//				}
//				return false;
//			}
//
//			return true;
//		}
//
//		return false;
//	}

	/**
	 * Implementation for converting record to binary
	 * 
	 * @param er
	 * @param estimate
	 * @param duration
	 */
	private static void convertToBinary(ExperimentalRecord er, Estimate estimate, Double duration) {

		ResultBinaryScore rbs = determineResultBinaryScore60PercentWithin28days(estimate, duration);

		er.property_value_point_estimate_original = estimate.point;
		er.property_value_min_original = estimate.min;
		er.property_value_max_original = estimate.max;

		er.property_value_units_original = ExperimentalConstants.str_dimensionless;
		er.property_value_units_final = ExperimentalConstants.str_binary;

		if (rbs.score != null) {
			er.property_value_point_estimate_final = (double) rbs.score;
			er.property_value_units_final = ExperimentalConstants.str_binary;
		} else {
//			System.out.println(er.casrn+"\t"+er.reason);
			er.updateReason(rbs.reason);
			er.keep = false;
		}

//		System.out.println(recBio.degradationValue+"\n"+JsonUtilities.gsonPretty.toJson(estimate)+"\n");

		setPropertyValueString(er, estimate, duration);

	}

	private static void convertToBinarywith10day(ExperimentalRecord er, Estimate estimate, Double duration,
			String interpretation) {

		ResultBinaryScore rbs = determineResultBinaryScore60PercentWithin28days(estimate, duration);

		er.property_value_point_estimate_original = estimate.point;
		er.property_value_min_original = estimate.min;
		er.property_value_max_original = estimate.max;

		er.property_value_units_original = ExperimentalConstants.str_dimensionless;

		if (interpretation == null) {
			er.keep = false;
			er.updateReason("Interpretation of results is missing");
			return;
		}

		er.property_value_units_final = ExperimentalConstants.str_binary;
//		boolean interpretationPositive = isPositiveReadyBiodegradableInterpretation(interpretation);
		boolean interpretationPositive = BiodegradationPropertyValues.OECD301Classifier
				.isReadilyBiodegradable(interpretation);

		if (!interpretationPositive) {
			er.property_value_string = "Interpretation (" + interpretation
					+ ") does not indicate ready biodegradability";
			er.property_value_point_estimate_final = 0.0;
		} else if (rbs.score != null) {
			setPropertyValueString(er, estimate, duration);
			er.property_value_point_estimate_final = (double) rbs.score;
			er.property_value_units_final = ExperimentalConstants.str_binary;
		} else {
//			System.out.println(er.casrn+"\t"+er.reason);
			er.updateReason(rbs.reason);
			er.keep = false;
		}

//		System.out.println(recBio.degradationValue+"\n"+JsonUtilities.gsonPretty.toJson(estimate)+"\n");

	}

	public static ResultBinaryScore determineResultBinaryScore60PercentWithin28days(Estimate estimate,
			Double duration) {

		ResultBinaryScore rbs = new ResultBinaryScore();
		int daysCutoff = 28;

		// 2. Prevent NullPointerException on auto-unboxing
		if (duration == null) {
			rbs.reason = "Duration is null";
			return rbs;
		}

		if (estimate.point != null) {
			if (estimate.point >= 60) {
				if (duration <= daysCutoff) {
					rbs.score = 1;
				} else {
					rbs.reason = "Point estimate > 60% but duration > 28 days";
				}
			} else {
				if (duration >= daysCutoff) {
					rbs.score = 0;
				} else { // can't tell if they waited long enough
					rbs.reason = "Point estimate < 60% but duration < 28 days";
				}
			}

		} else if (estimate.min != null && estimate.max != null) {

			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score = 1;
			} else if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score = 0;
			} else {
				rbs.reason = "Can't assign score based on min and max degradation values";
			}

		} else if (estimate.max != null) {
			if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score = 0;
			} else {
				rbs.reason = "Can't assign score based on max degradation value";
			}
		} else if (estimate.min != null) {
			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score = 1;
			} else {
				rbs.reason = "Can't assign score based on min degradation value";
			}
		} else {
			rbs.reason = "Missing or non-percentage degradation value";
		}
		return rbs;
	}

	public static void setPropertyValueString(ExperimentalRecord er, Estimate estimate, double duration) {

		// Following code seems redundant since er.property_value_min_original is set
		// from estimate.min:
//		if (er.property_value_min_original != null && estimate.min == null) estimate.min = er.property_value_min_original;
//		if (er.property_value_max_original != null && estimate.max == null) estimate.max = er.property_value_max_original;
//		if (er.property_value_point_estimate_original != null && estimate.point == null) estimate.point = er.property_value_point_estimate_original;

		if (estimate.min != null && estimate.max != null) {
			er.property_value_string = Parse.formatValue(er.property_value_min_original) + " - "
					+ Parse.formatValue(er.property_value_max_original);
		} else if (estimate.min != null) {
			er.property_value_string = "> " + Parse.formatValue(er.property_value_min_original);
//			er.property_value_numeric_qualifier = ">";
		} else if (estimate.max != null) {
			er.property_value_string = "< " + Parse.formatValue(er.property_value_max_original);
//			er.property_value_numeric_qualifier = "<";
		} else if (estimate.point != null) {
			er.property_value_string = Parse.formatValue(er.property_value_point_estimate_original);
		}

		if (er.property_value_string != null) {

//			String measurment_method = (String)er.experimental_parameters.get("measurement_method");

			DecimalFormat df = new DecimalFormat("0.#");

			er.property_value_string += " " + er.measurement_method + " in " + duration + " days";
			// System.out.println(er.property_value_string);
		}
	}

}

package gov.epa.api;

import java.util.Map;

public class ExperimentalConstants {

	
	public static final String strMethodKinetic="Kinetic";
	public static final String strMethodSteadyState="Steady State";
	public static final String strMethodOther="Other";
	
	//Add list of property names here:
	public static final String strWaterSolubility="Water solubility";
	public static final String strDMSOSolubility="DMSO solubility";

	public static final String strVaporPressure="Vapor pressure";
	public static final String strHenrysLawConstant="Henry's law constant";

//	public static final String strLogKow="Octanol water partition coefficient";//TODO add log() to it?
//	public static final String strLogKoa="Octanol air partition coefficient";//TODO add log() to it?

	public static final String strLogKOW="LogKow: Octanol-Water";//TODO add log() to it?
	public static final String strLogKOA="LogKoa: Octanol-Air";//TODO add log() to it?
	
	public static final String strDensity="Density";
	public static final String strVaporDensity="Vapor density";
	public static final String strLiquidDensity="Liquid density";

	
	public static final String strMeltingPoint="Melting point";
	public static final String strBoilingPoint="Boiling point";
	public static final String strFlashPoint="Flash point";
	public static final String strAppearance="Appearance";
	public static final String strOdor="Odor";

	public static final String str_pKA="pKa";
	public static final String str_pKAa="Acidic pKa";
	public static final String str_pKAb="Basic pKa";

	
	public static final String strLogOH="LogOH";
	public static final String strOH = "Atmospheric hydroxylation rate";//OPERA

	public static final String strLogBCF="LogBCF";
	public static final String strBCF = "Bioconcentration factor";
	public static final String strFishBCF = "Fish bioconcentration factor";
	public static final String strFishBCFWholeBody = "Whole body fish bioconcentration factor";
	public static final String strStandardFishBCF = "Standard test species fish bioconcentration factor";
	public static final String strStandardFishBCFWholeBody = "Whole body Standard test species fish bioconcentration factor";
//	public static final String strLogBCF_Fish_Whole_Body="LogBCF_Fish_Whole_Body";
	public static final String strBAF = "Bioaccumulation factor";
	public static final String strFishBAF = "Fish bioaccumulation factor";
	public static final String strFishBAFWholeBody = "Whole body fish bioaccumulation factor";
	public static final String strBMF = "Biomagnification factor";
	
	public static final String strLogKOC = "LogKOC";
	public static final String strLogKoc = "LogKoc";
	
	
	public static final String strKOC = "Soil Adsorption Coefficient (Koc)";
	public static final String strKd = "Soil Adsorption Coefficient (Kd)";
	
	public static final String strLogKmHL = "LogKmHL";
	public static final String strKmHL = "Fish biotransformation half-life (Km)";//OPERA


	public static final String strLogHalfLifeBiodegradation = "LogHalfLife";
	public static final String strBIODEG_HL_HC = "Biodegradation half-life for hydrocarbons";//OPERA


	
	public static final String strEyeIrritation="EyeIrritation";
	public static final String strEyeCorrosion="EyeCorrosion";
	public static final String strSkinIrritationPII="SkinIrritationPII";
	public static final String strSkinIrritation="SkinIrritation";
	public static final String strSkinCorrosion="SkinCorrosion";
	
	public static final String strCLINT = "Human hepatic intrinsic clearance";//OPERA
	public static final String strFUB = "Fraction unbound in human plasma";//OPERA
	public static final String strTTR_ANSA = "Binding to TTR (replacement of ANSA)";//OPERA
	public static final String strCACO2 = "Caco-2 permeability (Papp)";//OPERA
	public static final String strRBIODEG = "Ready biodegradability";//OPERA
	public static final String strOXYGENCONSUMPTION = "Percent O2 consumption";
	public static final String strPercentageBiodegradation = "Percentage biodegradation";
	

	
	//Add list of well defined property units here:
	public static final String str_mg_L="mg/L";
	public static final String str_mg_m3="mg/m^3";
	public static final String str_mg_dm3 = "mg/dm3";	
	
	public static final String str_g_m3="g/m^3";
	public static final String str_mL_m3="mL/m^3";
	public static final String str_uL_m3="uL/m^3";
	public static final String str_uL_L="uL/L";
	
	public static final String str_mL_L="mL/L";
	public static final String str_mg_mL="mg/mL";
	public static final String str_g_L="g/L";
	public static final String str_ug_L="ug/L";
	public static final String str_ng_L="ng/L";
	
	public static final String str_Bq_mL = "Bq/mL";
	public static final String str_mBq_mL = "mBq/mL";
	public static final String str_kBq_mL = "kBq/mL";

	
	public static final String str_lb_ft3="lb/ft^3";
	public static final String str_lb_gal="lb/gal";
	
	public static final String str_ug_mL="ug/mL";
	public static final String str_g_100mL="g/100mL";
	public static final String str_mg_100mL="mg/100mL";
	public static final String str_ng_ml="ng/mL";
	public static final String str_g_cm3="g/cm3";
	public static final String str_kg_m3="kg/m3";
	public static final String str_g_mL="g/mL";
	public static final String str_kg_L="kg/L";
	public static final String str_kg_dm3="kg/dm3";
	public static final String str_C="C";
	public static final String str_F="F";
	public static final String str_K="K";
	public static final String str_pctWt="%w";
	public static final String str_pctVol="%v";
	public static final String str_pct="%";
	
	public static final String str_pph="pph";
	public static final String str_ppm="ppm";
	public static final String str_ppb="ppb";
	public static final String str_ppt = "ppt";
	
	public static final String str_atm_m3_mol="atm-m3/mol";
	public static final String str_atm_cm3_mol="atm-cm3/mol";
	public static final String str_mol_m3_atm = "mol/m3-Pa";
	public static final String str_Pa_m3_mol="Pa-m3/mol";
	public static final String str_mmHg="mmHg";
	public static final String str_atm="atm";
	public static final String str_kpa="kPa";
	public static final String str_hpa="hPa";
	public static final String str_mpa="mPa";
	public static final String str_upa="uPa";
	public static final String str_npa="nPa";
	public static final String str_pa="Pa";
	public static final String str_mbar="mbar";
	public static final String str_bar="bar";
	public static final String str_torr="Torr";
	public static final String str_psi="psi";
	public static final String str_M="M";
	public static final String str_mM="mM";
	public static final String str_mmol_L = "mmol/L";
	public static final String str_mol_L = "mol/L";
	public static final String str_uM="uM";
	public static final String str_nM="nM";
	public static final String str_pM="pM";
	public static final String str_pmol_L = "pmol/L";
	public static final String str_log_M="log10(M)";
	public static final String str_neg_log_M = "-log10(M)";
	public static final String str_log_mg_L="log10(mg/L)";
	public static final String str_log_ppm="log10(ppm)";
	public static final String str_log_mmHg="log10(mmHg)";
	public static final String str_log_atm_m3_mol="log10(atm-m3/mol)";
	public static final String str_dimensionless_H="Dimensionless H";
	public static final String str_dimensionless_H_vol="Dimensionless H (volumetric)";
	public static final String str_dimensionless="Dimensionless";
	public static final String str_binary = "Binary";
	public static final String str_continuous = "Continuous";

	public static final String str_LOG_UNITS = "Log units";
	
	public static final String str_DAYS = "days";
	public static final String str_LOG_DAYS = "log10(days)";
	
	public static final String str_LOG_CM_SEC="log10(cm/sec)";
	public static final String str_CM_SEC="cm/sec";

	
	public static final String str_COUNT = "Count";
	public static final String str_POUNDS = "lbs";

	public static final String str_LOG_CM3_MOLECULE_SEC="log10(cm3/molecule-sec)";
	public static final String str_CM3_MOLECULE_SEC="cm3/molecule-sec";
	public static final String str_LOG_L_KG = "log10(L/kg)";
	public static final String str_LOG_UL_MIN_1MM_CELLS="log10(ul/min/10^6 cells)";//for clint
	public static final String str_UL_MIN_1MM_CELLS="ul/min/10^6 cells";//for clint

	public static final String str_ng_kg="ng/kg";
	public static final String str_ug_kg="ug/kg";
	public static final String str_mg_kg="mg/kg";
	public static final String str_g_kg="g/kg";
	
	public static final String str_mL_kg="mL/kg";
	public static final String str_uL_kg="uL/kg";
	public static final String str_iu_kg="iu/kg";
	public static final String str_L_KG = "L/kg";
	public static final String str_L_g = "L/g";
	public static final String str_L_mg = "L/mg";
//	public static final String str_mL_mg = "mL/mg";
//	public static final String str_ml_g="ml/g";

	public static final String str_units_kg="units/kg";
	public static final String str_mg="mg";
	public static final String str_mg_kg_H20="mg/kg H2O";
	public static final String str_g_Mg_H20="g/Mg H2O";
	public static final String str_g_100g="g/100g";
	public static final String str_mg_100g="mg/100g";
	public static final String str_mol_m3_H20="mol/m3 H2O";
	public static final String str_mol_kg_H20="mol/kg H2O";
	public static final String str_kg_kg_H20="kg/kg H2O";
	public static final String str_g_kg_H20="g/kg H2O";
	public static final String str_ug_g_H20 ="ug/g H2O";
	public static final String str_ug_100mL = "ug/100mL";
	public static final String str_mg_10mL = "mg/10mL";
	public static final String str_g_10mL = "g/10mL";
	public static final String str_oz_gal = "oz/gal";
	public static final String str_pii="PII";
	
	public static final String str_dyn_cm="dyn/cm";
	public static final String str_mN_m="mN/m";
	public static final String str_mN_cm="mN/cm";
	public static final String str_N_cm="N/cm";
	public static final String str_N_m="N/m";
	
	public static final String str_cP="cP";
	public static final String str_uP="uP";
	public static final String str_mP="mP";
	public static final String str_P="Poise";//use longer name to avoid errors with Pa-sec type units
	public static final String str_uPa_sec="uPa-sec";
	public static final String str_Pa_sec="Pa-sec";
	public static final String str_cSt="cSt";
	
	
	//Other:
	public static final String str_dec="decomposes";
	public static final String str_lit="literature";
	public static final String str_subl="sublimates";
	public static final String str_relative_density="relative density (water = 1)";
	public static final String str_relative_mixture_density="relative density of the vapor-air mixture (air = 1)";
	public static final String str_relative_gas_density="relative gas density (air = 1)";
	public static final String str_est="estimated";
	public static final String str_ext="extrapolated";
	public static final String str_negl="negligible";
	
	//Add list of source names here:
	public static final String strSourceLookChem="LookChem";
	public static final String strSourcePubChem="PubChem";
	public static final String strSourceEChemPortal="eChemPortal";
	public static final String strSourceEChemPortalAPI="eChemPortalAPI";
	public static final String strSourceOChem="OChem";
	public static final String strSourceOChem_2024_04_03="OChem_2024_04_03";
	
	public static final String strSourceOFMPub="OFMPub";
	public static final String strSourceSigmaAldrich="Sigma-Aldrich";
	public static final String strSourceChemicalBook="ChemicalBook";
	public static final String strSourceSander="Sander";
//	public static final String strSourceSander="Sander v4.0";
	public static final String strSourceQSARDB="QSARDB";
	public static final String strSourceBradley="Bradley";
	public static final String strSourceADDoPT="ADDoPT";
	public static final String strSourceAqSolDB="AqSolDB";
	public static final String strSourceChemBL="ChemBL";
	public static final String strSourceChemidplus="ChemIDplus";
	public static final String strSourceChemidplus2024_12_04="ChemIDplus_2024_12_04";
	public static final String strSourceOPERA="OPERA";
	public static final String strSourceOPERA28="OPERA2.8";
	public static final String strSourceOPERA29="OPERA2.9";
	public static final String strSourceEpisuiteOriginal="EpisuiteOriginal";
	public static final String strSourceEpisuiteISIS="EpisuiteISIS";
	public static final String strSourceICF="ICF";
	public static final String strSource3M="ThreeM";
	
	public static final String strSourceOECD_Toolbox="OECD Toolbox";
	public static final String strSourceOECD_Toolbox_SkinIrrit = "OECD Toolbox Skin Irritation";
	public static final String strSourceNICEATM="NICEATM";
	public static final String strSourceCFSAN="CFSAN";
	public static final String strSourceLebrun="Lebrun";
	public static final String strSourceDRD="DRD";
	public static final String strSourceTakahashi="Takahashi";
	public static final String strSourceBurkhard="Burkhard";
	public static final String strSourceHayashi="Hayashi";
	public static final String strSourceBagley="Bagley";
	public static final String strSourceKodithala="Kodithala";
	public static final String strSourceVerheyen="Verheyen";
	
	public static final String strSourceCERAPP_Exp="CERAPP_Exp";

	public static final String strInVitroToxicity = "in vitro toxicity";
	public static final String strNINETY_SIX_HOUR_FATHEAD_MINNOW_LC50 ="96 hour fathead minnow LC50";
	public static final String strNINETY_SIX_HOUR_SCUD_LC50 ="96 hour scud LC50";
	public static final String strNINETY_SIX_HOUR_BLUEGILL_LC50 = "96 hour bluegill LC50";

	public static final String strNINETY_SIX_HOUR_RAINBOW_TROUT_LC50= "96 hour rainbow trout LC50";
//	public static final String strFORTY_EIGHT_HOUR_WATER_FLEA_LC50= "48 hour water flea LC50";
	public static final String strFORTY_EIGHT_HR_DAPHNIA_MAGNA_LC50 ="48 hour Daphnia magna LC50";


	public static final String strSourceSampleSource="SampleSource";

	public static final String strAR = "Androgen receptor activity";
	public static final String strER = "Estrogen receptor activity";
	
	public static final String str_ANDROGEN_RECEPTOR_AGONIST = "Androgen receptor agonist";//OPERA
	public static final String str_ANDROGEN_RECEPTOR_ANTAGONIST = "Androgen receptor antagonist";//OPERA
	public static final String str_ANDROGEN_RECEPTOR_BINDING = "Androgen receptor binding";//OPERA

	public static final String str_ESTROGEN_RECEPTOR_AGONIST = "Estrogen receptor agonist";//OPERA
	public static final String str_ESTROGEN_RECEPTOR_ANTAGONIST = "Estrogen receptor antagonist";//OPERA
	public static final String str_ESTROGEN_RECEPTOR_BINDING = "Estrogen receptor binding";//OPERA

	
	public static final String strTEXT="Text";

//	public static final String strSourceEcotox="ECOTOX";
	public static final String strSourceEcotox_2023_12_14="ECOTOX_2023_12_14";
	public static final String strSourceEcotox_2024_12_12="ECOTOX_2024_12_12";
	public static final String strSourceEcotox_2026_03_12="ECOTOX_2026_03_12";
	
	public static final String sourceNITE_OPPT = "NITE_OPPT";

	public static final String strAutoIgnitionTemperature="Autoignition temperature";
	public static final String strRefractiveIndex="Refractive index";
	public static final String strViscosity="Viscosity";
	public static final String strSurfaceTension = "Surface tension";
	
	
	public static final String strDERMAL_RAT_LD50="Dermal rat LD50";
	public static final String strDERMAL_MOUSE_LD50="Dermal mouse LD50";
	public static final String strDERMAL_RABBIT_LD50="Dermal rabbit LD50";
	
	public static final String strINTRADERMAL_RABBIT_LD50="Intradermal rabbit LD50";
	
	
	public static final String strORAL_RAT_LD50="Oral rat LD50";
	public static final String strORAL_MOUSE_LD50="Oral mouse LD50";
	public static final String strORAL_RABBIT_LD50="Oral rabbit LD50";
	public static final String strORAL_GUINEA_PIG_LD50="Oral guinea pig";
	public static final String strRatOralLD50="rat_oral_LD50";
	
	public static final String strInhalationLC50="inhalation_LC50";	
	public static final String strInhalationRatLC50="Inhalation rat LC50";
	public static final String strInhalationMouseLC50="Inhalation mouse LC50";
	public static final String strInhalationRabbitLC50="Inhalation rabbit LC50";
	
	public static final String strFOUR_HOUR_INHALATION_RAT_LC50="4 hour Inhalation rat LC50";

	
	public static final String strAcuteAquaticToxicity = "Acute aquatic toxicity";
	public static final String strChronicAquaticToxicity = "Chronic aquatic toxicity";
	public static final String strAcuteOralToxicity = "Acute oral toxicity";
	public static final String strAcuteDermalToxicity = "Acute dermal toxicity";
	public static final String strAcuteInhalationToxicity = "Acute inhalation toxicity";
	
	public static final String strSkinSensitizationLLNA="SkinSensitizationLLNA";
	public static final String strSkinSensitizationLLNA_EC3="SkinSensitizationLLNA EC3";
	public static final String strSkinSensitizationLLNA_SI="SkinSensitizationLLNA SI";
	
	public static final String strParameterSoilType = "Soil_Type";
	
	// Experimental Parameter Names
	public static final String expParamMediaType = "Media type";
	public static final String expParamTestLocation = "Test location";
	public static final String expParamWetDry = "Test specificity";
	public static final String expParamWaterConcentration = "Water concentration";
	public static final String expParamConcentrationType = "concentration_type";
	public static final String expParamLipidPercent = "Lipid content percentage";
	public static final String expParamSpeciesLatin = "Species latin";
	public static final String expParamSpeciesCommon = "Species common";
	public static final String expParamSpeciesSupercategory = "Species supercategory";
	public static final String expParamMeasurementMethod = "Measurement method";
	public static final String expParamExposureDuration = "Exposure duration";
	public static final String expParamTissueType = "Response site";
	public static final String expParamResponseSite = expParamTissueType;
	public static final String expParamTemperature = "Temperature";
	public static final String expParamExposureType = "exposure_type";
	public static final String expParamPh = "pH";
	public static final String expParamGuideline = "Test guideline";
	public static final String expParamObservationDuration = "Observation duration";
	public static final String expParamReliability = "Reliability";
	public static final String expParamValueWholeBody = "whole body";

	// Data Source Names
	public static final String sourceEcha = "ECHA REACH";
	public static final String sourceNite = "Bioconcentration and logKow NITE";
	public static final String sourceCanada = "Bioaccumulation Canada";
	public static final String sourceCefic = "Bioaccumulation fish CEFIC LRI";

	// Guideline Names
	public static final String guidelineOecd305 = "OECD Guideline 305";
	public static final String guidelineOecd203 = "OECD Guideline 203";
	public static final String guidelineOecd210 = "OECD Guideline 210";
	public static final String guidelineOecd319a = "OECD Guideline 319A";
	public static final String guidelineOecd319b = "OECD Guideline 319B";

	public static final Map<String, String> guidelineHashMap = Map.ofEntries(
		Map.entry("OECD Guideline 305 (Bioaccumulation in Fish: Aqueous and Dietary Exposure) -I: Aqueous Exposure Bioconcentration Fish Test", "OECD Guideline 305-I"),
		Map.entry("OECD Guideline 305 (Bioaccumulation in Fish: Aqueous and Dietary Exposure) -II: Minimised Aqueous Exposure Fish Test", "OECD Guideline 305-II"),
		Map.entry("OECD Guideline 305 (Bioaccumulation in Fish: Aqueous and Dietary Exposure) -III: Dietary Exposure Bioaccumulation Fish Test", "OECD Guideline 305-III"),
		Map.entry("OECD Guideline 305 A (Bioaccumulation: Sequential Static Fish Test)", "OECD Guideline 305A"),
		Map.entry("OECD Guideline 305 B (Bioaccumulation: Semi-static Fish Test)", "OECD Guideline 305B"),
		Map.entry("OECD Guideline 305 C (Bioaccumulation: Test For The Degree of Bioconcentration in Fish)", "OECD Guideline 305C"),
		Map.entry("OECD Guideline 305 D (Bioaccumulation: Static Fish Test)", "OECD Guideline 305D"),
		Map.entry("OECD Guideline 305 E (Bioaccumulation: Flow-through Fish Test)", "OECD Guideline 305E"),
		Map.entry("\u2018\u2019OECD TG 305C “Degree of Bioconcentration in Fish\u2019\u2019", "OECD Guideline 305C"),
		Map.entry("â??â??OECD TG 305C â??Degree of Bioconcentration in Fishâ??â??", "OECD Guideline 305C"),
		// Map.entry("‘’OECD TG 305C “Degree of Bioconcentration in Fish’’", "OECD Guideline 305C"),
		Map.entry("draft document of the new OECD TG for the S9 assay.", "OECD Guideline Draft for the S9 Assay"),
		Map.entry(guidelineOecd305, guidelineOecd305),
		Map.entry("OECD 305", guidelineOecd305),
		Map.entry("EPAOECD - OECD TEST NO. 305 (2012),US EPA EPA 712-C-16-003 (2016)", guidelineOecd305),
		Map.entry("OECD - 2012 TEST 305", guidelineOecd305),
		Map.entry("OECD - BIOCONCENTRATION TEST 305, OECD, 1996", guidelineOecd305),
		Map.entry("OECD - OECD 305", guidelineOecd305),
		Map.entry("OECD - OECD 305, 2004", guidelineOecd305),
		Map.entry("OECD - OECD BIOCONCENTRATION TEST 305 (OECD, 1996)", guidelineOecd305),
		Map.entry("OECD - OECD BIOCONCENTRATION TEST 305 (OECD,1996)", guidelineOecd305),
		Map.entry("OECD - OECD GUIDELINE 305-II, 2012", guidelineOecd305),
		Map.entry("OECD - OECD NO. 305, 1996", guidelineOecd305),
		Map.entry("OECD - OECD NO. 305, 2012", guidelineOecd305),
		Map.entry("OECD - OECD TEST 305", guidelineOecd305),
		Map.entry("OECD - OECD TEST GUIDELINE NO. 305", guidelineOecd305),
		Map.entry("OECD - OECD TEST GUIDELINE NO. 305, 2012", guidelineOecd305),
		Map.entry("OECD Guideline 305 (Bioconcentration: Flow-through Fish Test)", guidelineOecd305),
		Map.entry("OECD Guideline No 305 Bioaccumulation in fish: aqueous and dietary exposure (2012)", guidelineOecd305),
		Map.entry("OECD Guidelines for Testing of Chemicals, Guideline 305: Bioaccumulation in Fish: Aqueous and Dietary Exposure.", guidelineOecd305),
		Map.entry("The test was performed according to the OECD Guidelines for Testing of Chemicals No 305 (adopted June 14, 1996): Bioconcentration: Flow-through Fish Test.", guidelineOecd305),
		Map.entry(guidelineOecd203, guidelineOecd203),
		Map.entry("OECD 203", guidelineOecd203),
		Map.entry("OECD - 2019 TEST 203", guidelineOecd203),
		Map.entry(guidelineOecd210, guidelineOecd210),
		Map.entry("OECD 210", guidelineOecd210),
		Map.entry("OECD - OECD GUIDELINES TEST NO. 210, 1992", guidelineOecd210),
		Map.entry(guidelineOecd319a, guidelineOecd319a),
		Map.entry("OECD 319A", guidelineOecd319a),
		Map.entry("OECD Guideline 319A:Determination of in vitro intrinsic clearance using cryopreserved rainbow trout hepatocytes (RT-HEP), OECD319 A", guidelineOecd319a),
		Map.entry(guidelineOecd319b, guidelineOecd319b),
		Map.entry("OECD 319B", guidelineOecd319b),
		Map.entry("OECD 319B - Determination of in vitro intrinsic clearance using Rainbow trout liver S9 subcellular fraction (RT-S9)", guidelineOecd319b),
		Map.entry("OECD 319B : Determination of in vitro intrinsic clearance using rainbow trout liver S9 subcellular fraction (RT-S9)", guidelineOecd319b),
		Map.entry("OECD Guideline 319B: Determination of in vitro intrinsic clearance using rainbow trout liver S9 sub-cellular fraction (RT-S9)", guidelineOecd319b)
	);

	public static final Map<String, String> ecotoxResponseSiteMap = Map.ofEntries(
		Map.entry("--",	"Unspecified"),
		Map.entry("AB",	"Aboveground portion"),
		Map.entry("ABD",	"Abdomen"),
		Map.entry("ABP",	"Abdominal process"),
		Map.entry("AD",	"Adipose tissue"),
		Map.entry("ADC",	"Auditory center"),
		Map.entry("AF",	"Amniotic fluid"),
		Map.entry("AG",	"Accessory gland"),
		Map.entry("AL",	"Albumen (egg white)"),
		Map.entry("ALG",	"Albumen gland"),
		Map.entry("AM",	"Adductor muscle"),
		Map.entry("AMG",	"Amygdala"),
		Map.entry("AMO",	"Amebocyte"),
		Map.entry("ANG",	"Antennal gland"),
		Map.entry("ANT",	"Antenna (antennae)"),
		Map.entry("AO",	"Anogenital"),
		Map.entry("AP",	"Appendage"),
		Map.entry("AR",	"Adrenal gland"),
		Map.entry("ARC",	"Aerenchyma "),
		Map.entry("ART",	"Artery"),
		Map.entry("ARX",	"Area X (Avian brain)"),
		Map.entry("AS",	"Air sac"),
		Map.entry("AT",	"Alimentary tract"),
		Map.entry("ATA",	"Aorta"),
		Map.entry("ATH",	"Abdomen and thorax"),
		Map.entry("ATM",	"Atrium"),
		Map.entry("ATR",	"Anther"),
		Map.entry("AX",	"Axons"),
		Map.entry("BA",	"Bark"),
		Map.entry("BB",	"Bulb"),
		Map.entry("BBL",	"Barbel"),
		Map.entry("BC",	"Buccal mass"),
		Map.entry("BCT",	"Bract"),
		Map.entry("BD",	"Bud"),
		Map.entry("BDT",	"Bile duct"),
		Map.entry("BDW",	"Body wall"),
		Map.entry("BF",	"Bursa of fabricius"),
		Map.entry("BG",	"Breeding (nuptial) gland"),
		Map.entry("BI",	"Bile"),
		Map.entry("BIL",	"Bill"),
		Map.entry("BIT",	"Biliary tract"),
		Map.entry("BK",	"Beak"),
		Map.entry("BL",	"Blood"),
		Map.entry("BLA",	"Bulbus arteriosus"),
		Map.entry("BLC",	"Blood cell"),
		Map.entry("BM",	"Bone marrow"),
		Map.entry("BMC",	"Bone marrow cells"),
		Map.entry("BMP",	"Bone marrow plasma"),
		Map.entry("BO",	"Bone"),
		Map.entry("BOL",	"Boll (cotton)"),
		Map.entry("BR",	"Brain"),
		Map.entry("BRN",	"Branches"),
		Map.entry("BRS",	"Brain stem"),
		Map.entry("BSG",	"Basal ganglia"),
		Map.entry("BT",	"Breast"),
		Map.entry("BU",	"Bursa"),
		Map.entry("BV",	"Blood vessel"),
		Map.entry("BW",	"Bees wax"),
		Map.entry("BY",	"Byssus"),
		Map.entry("CA",	"Cartilage"),
		Map.entry("CAE",	"Caecum"),
		Map.entry("CAN",	"Canopy"),
		Map.entry("CAP",	"Cap, mushroom"),
		Map.entry("CB",	"Cob"),
		Map.entry("CBC",	"Cerebral cortex"),
		Map.entry("CBH",	"Cerebral hemisphere"),
		Map.entry("CBM",	"Cerebrum"),
		Map.entry("CC",	"Cocoon"),
		Map.entry("CCM",	"Cecum"),
		Map.entry("CDB",	"Caudal bone"),
		Map.entry("CDP",	"Caudal peduncle"),
		Map.entry("CDV",	"Caudal vertebrae"),
		Map.entry("CE",	"Coelomic fluid"),
		Map.entry("CEL",	"Cell"),
		Map.entry("CG",	"Cloacal gland"),
		Map.entry("CGG",	"Coagulating gland"),
		Map.entry("CH",	"Spinal cord"),
		Map.entry("CHC",	"Chloragogen"),
		Map.entry("CHL",	"Cochlea"),
		Map.entry("CHN",	"Choana"),
		Map.entry("CHO",	"Chorion"),
		Map.entry("CHP",	"Choroid plexus"),
		Map.entry("CIL",	"Cilia"),
		Map.entry("CL",	"Claw"),
		Map.entry("CLC",	"Cloaca"),
		Map.entry("CLM",	"Coelomocytes"),
		Map.entry("CLN",	"Colon"),
		Map.entry("CLT",	"Clitoris, clitoral gland"),
		Map.entry("CLU",	"Clitellum"),
		Map.entry("CLV",	"Calvarium"),
		Map.entry("CM",	"Crown to rump"),
		Map.entry("CMB",	"Comb"),
		Map.entry("CMG",	"Cement gland"),
		Map.entry("CN",	"Cotyledon"),
		Map.entry("CNS",	"Central nervous system"),
		Map.entry("CO",	"Collagen"),
		Map.entry("COL",	"Coleoptile"),
		Map.entry("COR",	"Corm"),
		Map.entry("COS",	"Corpuscles of stannius"),
		Map.entry("CP",	"Capat"),
		Map.entry("CPA",	"Corpus allatum"),
		Map.entry("CPG",	"Cowper's gland"),
		Map.entry("CPR",	"Clasper"),
		Map.entry("CPS",	"Carpus"),
		Map.entry("CPT",	"Chloroplast"),
		Map.entry("CR",	"Crop"),
		Map.entry("CRB",	"Cerebellum"),
		Map.entry("CRG",	"Cerebral ganglion"),
		Map.entry("CRI",	"Cervical rib"),
		Map.entry("CRN",	"Cornea"),
		Map.entry("CRP",	"Carapace"),
		Map.entry("CRR",	"Cerebellar region"),
		Map.entry("CRX",	"Cortex "),
		Map.entry("CS",	"Chromosome"),
		Map.entry("CSF",	"Cerebrospinal fluid"),
		Map.entry("CST",	"Cisternae"),
		Map.entry("CT",	"Cephalothorax"),
		Map.entry("CTE",	"Ctenidium"),
		Map.entry("CU",	"Culture cells"),
		Map.entry("CUT",	"Cuticle"),
		Map.entry("CV",	"Caudal vertebra"),
		Map.entry("CVM",	"Calvarium"),
		Map.entry("CVV",	"Cervical vertebrae"),
		Map.entry("CVX",	"Cervix"),
		Map.entry("CX",	"Caudex"),
		Map.entry("CY",	"Cytosol"),
		Map.entry("CYT",	"Cytoplasm"),
		Map.entry("DG",	"Digestive gland"),
		Map.entry("DGT",	"Digit"),
		Map.entry("DI",	"Diaphragm"),
		Map.entry("DN",	"Diencephalon"),
		Map.entry("DO",	"Duodenum"),
		Map.entry("DT",	"Digestive tract"),
		Map.entry("EAL",	"Ear leaf"),
		Map.entry("EAR",	"Ear"),
		Map.entry("EBP",	"External body parts"),
		Map.entry("EC",	"Excreta"),
		Map.entry("ED",	"Endometrium"),
		Map.entry("EF",	"Efferent ducts"),
		Map.entry("EG",	"Egg"),
		Map.entry("EL",	"Elytrom"),
		Map.entry("ELE",	"Eleocyte"),
		Map.entry("EM",	"Embryo"),
		Map.entry("EMS",	"Embryonic shoot cells"),
		Map.entry("EN",	"Entrails"),
		Map.entry("ENG",	"Endocrine gland(s)"),
		Map.entry("EO",	"Endothelium"),
		Map.entry("EP",	"Endoplasmic reticulum"),
		Map.entry("EPD",	"Epididymis"),
		Map.entry("EPF",	"Extrapallial fluid"),
		Map.entry("ER",	"Erythrocyte"),
		Map.entry("ES",	"Esophagus"),
		Map.entry("ET",	"Edible tissue"),
		Map.entry("EU",	"Egg cuticle"),
		Map.entry("EV",	"Exuviae"),
		Map.entry("EX",	"Exoskeleton"),
		Map.entry("EXC",	"Extracellular"),
		Map.entry("EY",	"Eye"),
		Map.entry("EYS",	"Eyestalk"),
		Map.entry("EZ",	"Enzyme"),
		Map.entry("F1",	"F1 generation"),
		Map.entry("FAC",	"Face"),
		Map.entry("FB",	"Frontal bone"),
		Map.entry("FBR",	"Forebrain"),
		Map.entry("FC",	"Feces"),
		Map.entry("FD",	"Frond"),
		Map.entry("FE",	"Feathers"),
		Map.entry("FET",	"Fetus"),
		Map.entry("FG",	"Foregut"),
		Map.entry("FI",	"Fin"),
		Map.entry("FIB",	"Fibula"),
		Map.entry("FL",	"Fillet"),
		Map.entry("FLB",	"Flower bud"),
		Map.entry("FLW",	"Flower/inflorescence"),
		Map.entry("FM",	"Femur"),
		Map.entry("FMD",	"Femur diaphysis"),
		Map.entry("FME",	"Femur epiphysis"),
		Map.entry("FML",	"Left femur"),
		Map.entry("FMM",	"Femur  metaphysis"),
		Map.entry("FO",	"Foot"),
		Map.entry("FOD",	"Fodder"),
		Map.entry("FOL",	"Foliage"),
		Map.entry("FOR",	"Forage"),
		Map.entry("FP",	"Fatpad"),
		Map.entry("FR",	"Fruit"),
		Map.entry("FRL",	"Forelimb"),
		Map.entry("FX",	"Frontal cortex"),
		Map.entry("GA",	"Granum (plural grana)"),
		Map.entry("GB",	"Gall bladder"),
		Map.entry("GC",	"Gland complex"),
		Map.entry("GE",	"Germarium"),
		Map.entry("GF",	"Green forage"),
		Map.entry("GG",	"Green gland"),
		Map.entry("GI",	"Gill(s)"),
		Map.entry("GL",	"Ganglion"),
		Map.entry("GMT",	"Germ tube"),
		Map.entry("GNP",	"Genital papillae"),
		Map.entry("GNT",	"Gnathopod"),
		Map.entry("GO",	"Gonad(s)"),
		Map.entry("GOL",	"Golgi apparatus"),
		Map.entry("GP",	"Gills+palps"),
		Map.entry("GPD",	"Gonopodium"),
		Map.entry("GR",	"Grain"),
		Map.entry("GS",	"Germinated seed"),
		Map.entry("GT",	"Gastrointestinal tract"),
		Map.entry("GU",	"Gut"),
		Map.entry("GY",	"Gametophyte"),
		Map.entry("GZ",	"Gizzard"),
		Map.entry("HA",	"Hair"),
		Map.entry("HAP",	"Haptonema"),
		Map.entry("HAY",	"Hay"),
		Map.entry("HB",	"Hindbrain"),
		Map.entry("HC",	"Hypocotyl callus cell"),
		Map.entry("HD",	"Head"),
		Map.entry("HDG",	"Hindgut"),
		Map.entry("HDK",	"Head kidney (pronephros)"),
		Map.entry("HE",	"Heart"),
		Map.entry("HIP",	"Hippocampus"),
		Map.entry("HK",	"Heart and kidneys"),
		Map.entry("HKG",	"Husks and grain"),
		Map.entry("HL",	"Hemolymph"),
		Map.entry("HLA",	"Hyalinocyte"),
		Map.entry("HLB",	"Hindlimb"),
		Map.entry("HM",	"Humerus"),
		Map.entry("HMC",	"Hemocyte"),
		Map.entry("HMG",	"Hemoglobin"),
		Map.entry("HO",	"Honey"),
		Map.entry("HOD",	"Hyoid"),
		Map.entry("HP",	"Hepatopancreas"),
		Map.entry("HPG",	"Hypopharyngeal gland"),
		Map.entry("HS",	"Hematopoietic system"),
		Map.entry("HSC",	"Hypocotyl (or Stem) Cortex "),
		Map.entry("HSK",	"Husk"),
		Map.entry("HSS",	"Hypocotyl (or Stem) Stele "),
		Map.entry("HTC",	"Heterocyst"),
		Map.entry("HTG",	"Hatching gland"),
		Map.entry("HVC",	"HVC Region (Avian brain)"),
		Map.entry("HY",	"Hypothalamus"),
		Map.entry("HYA",	"Hypha"),
		Map.entry("HYD",	"Hypodermis"),
		Map.entry("HYP",	"Hypocotyl"),
		Map.entry("IB",	"Interparietal bone"),
		Map.entry("IBP",	"Internal body parts"),
		Map.entry("ICL",	"Inclusions"),
		Map.entry("IE",	"Ileum"),
		Map.entry("IL",	"Ilium"),
		Map.entry("IN",	"Intestinal tract"),
		Map.entry("IR",	"Interrenal gland"),
		Map.entry("IT",	"Internode"),
		Map.entry("JA",	"Jaw"),
		Map.entry("JE",	"Jejunum"),
		Map.entry("JV",	"Juvenile"),
		Map.entry("KI",	"Kidney"),
		Map.entry("KIL",	"Kidney, left"),
		Map.entry("KIR",	"Kidney, right"),
		Map.entry("KR",	"Kernal"),
		Map.entry("LAL",	"Lateral line"),
		Map.entry("LAM",	"Laminae"),
		Map.entry("LC",	"Leaf chloroplast"),
		Map.entry("LD",	"Lipid, fat"),
		Map.entry("LE",	"Leaf/needle"),
		Map.entry("LEI",	"Leaf index"),
		Map.entry("LEN",	"Lens"),
		Map.entry("LEO",	"Leaf, old"),
		Map.entry("LEU",	"Leukocytes"),
		Map.entry("LEY",	"Leaf, young"),
		Map.entry("LG",	"Leg"),
		Map.entry("LGT",	"Ligament"),
		Map.entry("LI",	"Liver"),
		Map.entry("LIG",	"Leibleins gland"),
		Map.entry("LIM",	"Liver microsomes"),
		Map.entry("LIN",	"Large intestine"),
		Map.entry("LIP",	"Lip"),
		Map.entry("LIT",	"Litters"),
		Map.entry("LM",	"Limb"),
		Map.entry("LMP",	"Lymphocyte"),
		Map.entry("LMV",	"Lumbar vertebrae"),
		Map.entry("LMW",	"Low molecular weight biomolecules (e.g., amino acids)"),
		Map.entry("LN",	"Lymph node"),
		Map.entry("LNX",	"Larynx"),
		Map.entry("LP",	"Labial palps"),
		Map.entry("LTB",	"Left tibia"),
		Map.entry("LU",	"Lung(s)"),
		Map.entry("LYM",	"Lymph"),
		Map.entry("LYS",	"Lysosome"),
		Map.entry("MA",	"Mantle"),
		Map.entry("MB",	"Muscle+bone"),
		Map.entry("MBR",	"Midbrain"),
		Map.entry("MC",	"Microsome"),
		Map.entry("MD",	"Mullerian duct"),
		Map.entry("MDP",	"Madreporite"),
		Map.entry("ME",	"Meristem"),
		Map.entry("MES",	"Mesentery"),
		Map.entry("MI",	"Midgut or midgut gland"),
		Map.entry("MIT",	"Mitochondria"),
		Map.entry("MK",	"Milk, lactating female"),
		Map.entry("ML",	"Melanophore"),
		Map.entry("MM",	"Mammary tissue"),
		Map.entry("MNS",	"Manus"),
		Map.entry("MO",	"Mucous"),
		Map.entry("MOB",	"Medulla oblongata"),
		Map.entry("MOM",	"Mother cells, pollen"),
		Map.entry("MP",	"Metanephridium"),
		Map.entry("MPG",	"Macrophage"),
		Map.entry("MPT",	"Malpighian tubule"),
		Map.entry("MR",	"Membrane"),
		Map.entry("MRC",	"Motor cortex"),
		Map.entry("MS",	"Mesenteric lymph node"),
		Map.entry("MSC",	"Mesencephalon"),
		Map.entry("MSI",	"Mucosa of the small intestine"),
		Map.entry("MSS",	"Microsomal supernatant"),
		Map.entry("MT",	"Multiple tissue/organ"),
		Map.entry("MTC",	"Metacarpus"),
		Map.entry("MTH",	"Mouth"),
		Map.entry("MTM",	"Mentum"),
		Map.entry("MU",	"Muscle"),
		Map.entry("MUL",	"Multiple entries"),
		Map.entry("MV",	"Microvilli"),
		Map.entry("MYC",	"Mycellium"),
		Map.entry("MYM",	"Myometrium"),
		Map.entry("NAC",	"Nucleus accumbens"),
		Map.entry("NAL",	"Nail"),
		Map.entry("NB",	"Nasal bone"),
		Map.entry("ND",	"Nodule, root"),
		Map.entry("NE",	"Nervous tissue"),
		Map.entry("NEM",	"Neuromasts"),
		Map.entry("NG",	"Nasal gland"),
		Map.entry("NI",	"Nipple"),
		Map.entry("NK",	"Neck"),
		Map.entry("NL",	"Needle"),
		Map.entry("NOC",	"Notochord"),
		Map.entry("NOD",	"Node"),
		Map.entry("NP",	"Nuptial pad"),
		Map.entry("NR",	"Not reported"),
		Map.entry("NRN",	"Neuron"),
		Map.entry("NSE",	"Nose"),
		Map.entry("NT",	"Neural tube"),
		Map.entry("NTR",	"Nectar"),
		Map.entry("NU",	"Nuclei"),
		Map.entry("NVC",	"Nerve cord"),
		Map.entry("NVL",	"Navel"),
		Map.entry("NY",	"Nymph"),
		Map.entry("OC",	"Oocyte"),
		Map.entry("OCL",	"Occipital lobe"),
		Map.entry("OD",	"Oviduct"),
		Map.entry("OF",	"Orifice"),
		Map.entry("OG",	"Organ"),
		Map.entry("OL",	"Olfactory"),
		Map.entry("OM",	"Omentum"),
		Map.entry("OPN",	"Optic nerve"),
		Map.entry("OPR",	"Operculum"),
		Map.entry("OR",	"Organelle"),
		Map.entry("OS",	"Osphradium"),
		Map.entry("OT",	"Opisthaptor"),
		Map.entry("OTO",	"Otoliths"),
		Map.entry("OTV",	"Otic vesicle"),
		Map.entry("OV",	"Ovaries"),
		Map.entry("OVF",	"Ovarian follicle"),
		Map.entry("OVP",	"Ovipositor"),
		Map.entry("OVT",	"Ovotestis"),
		Map.entry("PA",	"Palps"),
		Map.entry("PAN",	"Panicle"),
		Map.entry("PAT",	"Parathyroid gland"),
		Map.entry("PB",	"Pseudobranch"),
		Map.entry("PBD",	"Projectile body"),
		Map.entry("PC",	"Pyloric ceca"),
		Map.entry("PCL",	"Peduncle"),
		Map.entry("PD",	"Pod"),
		Map.entry("PDG",	"Pedal ganglion"),
		Map.entry("PE",	"Penis"),
		Map.entry("PEH",	"Penis sheath"),
		Map.entry("PEL",	"Peel"),
		Map.entry("PEP",	"Pecten epipharyngis"),
		Map.entry("PES",	"Petiole and stem"),
		Map.entry("PF",	"Pseudofeces"),
		Map.entry("PG",	"Prostate gland"),
		Map.entry("PGL",	"Preening gland"),
		Map.entry("PHG",	"Pheromone gland"),
		Map.entry("PHL",	"Phalanges"),
		Map.entry("PHO",	"Phloem"),
		Map.entry("PI",	"Pituitary gland"),
		Map.entry("PL",	"Plasma"),
		Map.entry("PLA",	"Platelet"),
		Map.entry("PLC",	"Placenta"),
		Map.entry("PLG",	"Phellogen "),
		Map.entry("PLL",	"Pellicle"),
		Map.entry("PLN",	"Popliteal node"),
		Map.entry("PLP",	"Pulp"),
		Map.entry("PLT",	"Palate"),
		Map.entry("PLU",	"Pleura"),
		Map.entry("PLV",	"Pelvis"),
		Map.entry("PLY",	"Polysaccharide"),
		Map.entry("PM",	"Pons + medulla"),
		Map.entry("PNG",	"Pineal gland"),
		Map.entry("PO",	"Pollen, pollen grains"),
		Map.entry("POS",	"Pod + seed"),
		Map.entry("PPD",	"Parapodium"),
		Map.entry("PPG",	"Preputial gland"),
		Map.entry("PR",	"Proventriculus"),
		Map.entry("PRC",	"Pericardium"),
		Map.entry("PRF",	"Particulate fraction"),
		Map.entry("PRG",	"Progeny"),
		Map.entry("PRO",	"Protein"),
		Map.entry("PRT",	"Peritoneum"),
		Map.entry("PS",	"Pancreas"),
		Map.entry("PSG",	"Plastglobuli"),
		Map.entry("PSL",	"Pistil"),
		Map.entry("PT",	"Petiole"),
		Map.entry("PTB",	"Parietal bone"),
		Map.entry("PTG",	"Parotid gland"),
		Map.entry("PTL",	"Petal"),
		Map.entry("PTU",	"Plant, unspecified"),
		Map.entry("PU",	"Pollen tube"),
		Map.entry("PV",	"Perivitelline space"),
		Map.entry("PX",	"Pharynx"),
		Map.entry("PXS",	"Peroxisome"),
		Map.entry("PYR",	"Pyrenoid"),
		Map.entry("RA",	"Radius"),
		Map.entry("RAC",	"Rachis"),
		Map.entry("RAD",	"Radius, distal"),
		Map.entry("RAN",	"RA (Avian brain)"),
		Map.entry("RB",	"Rib"),
		Map.entry("RC",	"Rectum"),
		Map.entry("RD",	"Radicle"),
		Map.entry("RDL",	"Radiole"),
		Map.entry("RE",	"Retina"),
		Map.entry("RFM",	"Right femur"),
		Map.entry("RG",	"Rectal gland"),
		Map.entry("RH",	"Rhizome"),
		Map.entry("RL",	"Root, lateral"),
		Map.entry("RLP",	"Root, primary lateral"),
		Map.entry("RLS",	"Root, second lateral"),
		Map.entry("RM",	"Retractor muscle"),
		Map.entry("RNC",	"Renal cortex"),
		Map.entry("RNM",	"Renal medulla"),
		Map.entry("RO",	"Root"),
		Map.entry("ROC",	"Root cortex"),
		Map.entry("ROE",	"Root epidermis"),
		Map.entry("ROI",	"Root, inner cortex"),
		Map.entry("ROO",	"Root , outer cortex"),
		Map.entry("ROS",	"Root, stele"),
		Map.entry("RP",	"Root, primary"),
		Map.entry("RPP",	"Renal papilla"),
		Map.entry("RR",	"Residual, remnant, carcass"),
		Map.entry("RS",	"Root + stem"),
		Map.entry("RST",	"Rostrum"),
		Map.entry("RT",	"Reproductive tissue"),
		Map.entry("RTB",	"Right tibia"),
		Map.entry("RTC",	"Root tip cells"),
		Map.entry("RTE",	"Rete testis"),
		Map.entry("RTP",	"Root tips"),
		Map.entry("RU",	"Radius-ulna"),
		Map.entry("RV",	"Right ventricle"),
		Map.entry("RZ",	"Root + rhizome"),
		Map.entry("SA",	"Salt gland"),
		Map.entry("SAC",	"Striatum-accumbens"),
		Map.entry("SAP",	"Sap"),
		Map.entry("SB",	"Shell, membrane"),
		Map.entry("SB2",	"Stem/stalk,lower half"),
		Map.entry("SC",	"Scale"),
		Map.entry("SCH",	"Starch"),
		Map.entry("SCM",	"Scrotum"),
		Map.entry("SCP",	"Scapula"),
		Map.entry("SCV",	"Sacral vertebrae"),
		Map.entry("SCY",	"Spermatocyte"),
		Map.entry("SD",	"Seed"),
		Map.entry("SDL",	"Seedling"),
		Map.entry("SDM",	"Subdermis"),
		Map.entry("SE",	"Sensory organs"),
		Map.entry("SEM",	"Semen"),
		Map.entry("SG",	"Shell gland"),
		Map.entry("SGM",	"Segment"),
		Map.entry("SH",	"Stomach"),
		Map.entry("SHF",	"Stomach or rumen fluid"),
		Map.entry("SI",	"Siphon"),
		Map.entry("SIN",	"Small intestine"),
		Map.entry("SINS",	"Small intestine serosa"),
		Map.entry("SK",	"Skin, epidermis"),
		Map.entry("SKL",	"Skull"),
		Map.entry("SKM",	"Skeletal muscle"),
		Map.entry("SLG",	"Silk gland"),
		Map.entry("SLK",	"Silk"),
		Map.entry("SLL",	"Shell"),
		Map.entry("SLV",	"Stem to leaves"),
		Map.entry("SM",	"Sperm"),
		Map.entry("SMM",	"Stomach mucosa"),
		Map.entry("SMT",	"Spermatheca"),
		Map.entry("SMW",	"Stomach wall"),
		Map.entry("SN",	"Skeleton"),
		Map.entry("SO",	"Shoot"),
		Map.entry("SOM",	"Somite"),
		Map.entry("SOT",	"Shoot tip"),
		Map.entry("SP",	"Spleen"),
		Map.entry("SPB",	"Sphenoid bone"),
		Map.entry("SPC",	"Superior colliculus"),
		Map.entry("SPI",	"Spine, backbone"),
		Map.entry("SPK",	"Spikelet"),
		Map.entry("SPL",	"Sepal"),
		Map.entry("SPR",	"Sporophyte"),
		Map.entry("SPS",	"Spines, protuberant structures"),
		Map.entry("SPT",	"Spermatid"),
		Map.entry("SR",	"Serum"),
		Map.entry("SRB",	"Strobilus"),
		Map.entry("SRC",	"Secretory cell"),
		Map.entry("SS",	"Stem"),
		Map.entry("SSC",	"Somatosensory center"),
		Map.entry("SSI",	"Serosa of the small intestines"),
		Map.entry("SSP",	"Stems plus petioles"),
		Map.entry("ST",	"Soft tissue"),
		Map.entry("STA",	"Setae"),
		Map.entry("STB",	"Semeniferous tubules"),
		Map.entry("STE",	"Sternum or sternebrae"),
		Map.entry("STG",	"Straw and grain"),
		Map.entry("STH",	"Straw and husk"),
		Map.entry("STL",	"Stolon"),
		Map.entry("STM",	"Striatum"),
		Map.entry("STN",	"Stamen"),
		Map.entry("STO",	"Stoma"),
		Map.entry("STR",	"Straw"),
		Map.entry("STV",	"Stover"),
		Map.entry("SU",	"Stalk/stem,upper half"),
		Map.entry("SV",	"Seminal vesicle"),
		Map.entry("SVA",	"Saliva"),
		Map.entry("SVG",	"Salivary gland"),
		Map.entry("SVN",	"Sinus venosus"),
		Map.entry("SWB",	"Swim bladder"),
		Map.entry("SX",	"Submaxillary gland"),
		Map.entry("TA",	"Tail"),
		Map.entry("TAK",	"Thylakoid"),
		Map.entry("TB",	"Tibia"),
		Map.entry("TBC",	"Tubercles"),
		Map.entry("TCH",	"Trachea"),
		Map.entry("TCV",	"Thoracic vertebrae"),
		Map.entry("TD",	"Transudate"),
		Map.entry("TE",	"Testes"),
		Map.entry("TEL",	"Testicle, left"),
		Map.entry("TER",	"Testicle, right"),
		Map.entry("TF",	"Tuber flesh"),
		Map.entry("TG",	"Thigh muscle"),
		Map.entry("TH",	"Thorax"),
		Map.entry("THA",	"Thorax and abdomen"),
		Map.entry("TI",	"Tissue"),
		Map.entry("TIL",	"Tillers"),
		Map.entry("TK",	"Trunk"),
		Map.entry("TKK",	"Trunk kidney"),
		Map.entry("TLE",	"Trifoliate leaves"),
		Map.entry("TLI",	"Thalli"),
		Map.entry("TLM",	"Thalamus"),
		Map.entry("TLN",	"Telencephalon"),
		Map.entry("TLS",	"Talus"),
		Map.entry("TM",	"Tarsus-metatarsus"),
		Map.entry("TMR",	"Tumor"),
		Map.entry("TN",	"Tentacles"),
		Map.entry("TO",	"Tongue"),
		Map.entry("TOP",	"Tops (plants)"),
		Map.entry("TOR",	"Torso"),
		Map.entry("TP",	"Tuber peeling"),
		Map.entry("TR",	"Tarsus"),
		Map.entry("TRD",	"Tear duct"),
		Map.entry("TS",	"Thymus"),
		Map.entry("TSC",	"Thymus cortex"),
		Map.entry("TSL",	"Tassel"),
		Map.entry("TSM",	"Thymus medulla"),
		Map.entry("TT",	"Tibiotarsus"),
		Map.entry("TTH",	"Tooth, teeth"),
		Map.entry("TU",	"Tuber"),
		Map.entry("TY",	"Thyroid"),
		Map.entry("UB",	"Urinary bladder"),
		Map.entry("UBG",	"Ultimobranchial gland"),
		Map.entry("UG",	"Uropygial gland"),
		Map.entry("UL",	"Ulna"),
		Map.entry("ULE",	"Unifoliate leaves"),
		Map.entry("UNT",	"Urinary tract"),
		Map.entry("UP",	"Urogenital papillae"),
		Map.entry("UR",	"Urine"),
		Map.entry("URT",	"Ureter"),
		Map.entry("UT",	"Uterus"),
		Map.entry("UTH",	"Urethra"),
		Map.entry("VA",	"Vagina"),
		Map.entry("VAS",	"Vasculature"),
		Map.entry("VC",	"Visual center"),
		Map.entry("VCL",	"Vacuole"),
		Map.entry("VD",	"Vas deferens"),
		Map.entry("VE",	"Vertebra"),
		Map.entry("VEN",	"Vein"),
		Map.entry("VG",	"Vegetative portion"),
		Map.entry("VGL",	"Visceral ganglion"),
		Map.entry("VI",	"Viscera"),
		Map.entry("VL",	"Villi"),
		Map.entry("VN",	"Vine"),
		Map.entry("VNT",	"Ventricle"),
		Map.entry("VNTL",	"Ventricle, left"),
		Map.entry("VSC",	"Vesicle"),
		Map.entry("VT",	"Vitellarium"),
		Map.entry("VV",	"Valve"),
		Map.entry("WD",	"Wolffian duct"),
		Map.entry("WI",	"Wings"),
		Map.entry("WL",	"Wall, body"),
		Map.entry("WM",	"White matter"),
		Map.entry("WO",	"Whole organism"),
		Map.entry("WR",	"Wrist"),
		Map.entry("WTL",	"Wattle"),
		Map.entry("XY",	"Xylem"),
		Map.entry("YO",	"Yolk"),
		Map.entry("YS",	"Yolk sac"),
		Map.entry("ZP",	"Zona pellucida"),
		Map.entry("ZY",	"Zymbal gland")
	);

	public static final Map<String, String> responseSiteMap = Map.ofEntries(
		Map.entry("whole body", "whole body"),
		Map.entry("esophagus", "esophagus"),
		Map.entry("section i", ""),
		Map.entry("section ii", ""),
		Map.entry("section iii", ""),
		Map.entry("gills", "gill"),
		Map.entry("skin", "skin"),
		Map.entry("backbone scale", "spine"),
		Map.entry("spinal cord", "spine"),
		Map.entry("liver", "liver"),
		Map.entry("muscle", "muscle"),
		Map.entry("blood", "blood"),
		Map.entry("contents of guts", "gut content"),
		Map.entry("brain", "brain"),
		Map.entry("kidney", "kidney"),
		Map.entry("gut", "gut"),
		Map.entry("red muscles", "red muscle"),
		Map.entry("white muscles", "white muscle"),
		Map.entry("scale", "scale"),
		Map.entry("backbone", "spine"),
		Map.entry("gall bladder", "gall bladder"),
		Map.entry("spleen", "spleen"),
		Map.entry("lipid, fat", "fat"),
		Map.entry("residual, remnant, carcass", "carcass"),
		Map.entry("carcass", "carcass"),
		Map.entry("edible tissue", "edible portion"),
		Map.entry("elim", ""),
		Map.entry("heart", "heart"),
		Map.entry("bile", "bile"),
		Map.entry("red muscle", "red muscle"),
		Map.entry("white muscle", "white muscle"),
		Map.entry("scales", "scale"),
		Map.entry("bone", "bone"),
		Map.entry("intestines", "intestine"),
		Map.entry("intestinal tract", "intestine"),
		Map.entry("intestine", "intestine"),
		Map.entry("skin, epidermis", "skin"),
		Map.entry("head", "head"),
		Map.entry("digestive tract", "digestive tract"),
		Map.entry("air sac", "air sac"),
		Map.entry("viscera", "viscera"),
		Map.entry("alimentary tract", "alimentary tract"),
		Map.entry("alimentary canal", "alimentary tract"),
		Map.entry("body remainder", ""),
		Map.entry("internal organs", "organs"),
		Map.entry("gi-tract", "digestive tract"),
		Map.entry("gonads", "gonad"),
		Map.entry("plasma", "plasma"),
		Map.entry("tissue", "tissue"),
		Map.entry("blood cells", "blood"),
		Map.entry("blood plasma", "plasma"),
		Map.entry("gill filaments", "gill"),
		Map.entry("cartilage", "cartilage"),
		Map.entry("hepatopancreas", "hepatopancreas"),
		Map.entry("pyloric caeca", "pyloric caeca"),
		Map.entry("post intestine", "intestine"),
		Map.entry("ant intestine", "intestine"),
		Map.entry("stomach", "stomach"),
		Map.entry("lens", "lens"),
		Map.entry("cerebellum", "cerebellum"),
		Map.entry("medulla", "medulla"),
		Map.entry("cerebrum", "cerebrum"),
		Map.entry("intestinal fat", "intestinal fat"),
		Map.entry("epidermal mucus", "mucus"),
		Map.entry("head kidney (pronephros)", "head kidney"),
		Map.entry("trunk kidney", "trunk kidney"),
		Map.entry("yolk sac", "yolk sac"),
		Map.entry("embryo", "embryo"),
		Map.entry("shoot", "shoot"),
		Map.entry("serum", "serum"),
		Map.entry("whole fish", "whole body"),
		Map.entry("whole carcass", "whole body"),
		Map.entry("soft tissue", "soft tissue"),
		Map.entry("soft body", "soft tissue"),
		Map.entry("shell", "shell"),
		Map.entry("skeleton", "skeleton"),
		Map.entry("fillet", "fillet"),
		Map.entry("fatpad", "fat"),
		Map.entry("hindgut", "hindgut"),
		Map.entry("organ", "organs"),
		Map.entry("amniotic fluid", "amniotic fluid"),
		Map.entry("multiple entries", ""),
		Map.entry("ovary", "ovary"),
		Map.entry("testis", "testes"),
		Map.entry("ovaries", "ovary"),
		Map.entry("testes", "testes"),
		Map.entry("egg", "egg"),
		Map.entry("zona radiata", "zona radiata"),
		Map.entry("embryo, yolk, and zona radiata", "egg"),
		Map.entry("embryo, yolk", "egg"),
		Map.entry("fins", "fin"),
		Map.entry("eye", "eye"),
		Map.entry("dead fish", ""),
		Map.entry("remaining carcass", ""),
		Map.entry("whole blood", "blood"),
		Map.entry("abdomen", "abdomen"),
		Map.entry("cephalothorax", "cephalothorax"),
		Map.entry("leaf", "leaf"),
		Map.entry("rhizome", "rhizome"),
		Map.entry("mantle", "mantle"),
		Map.entry("gonad", "gonad"),
		Map.entry("gill", "gill"),
		Map.entry("foot", "foot"),
		Map.entry("whole mussel", "whole body"),
		Map.entry("exuviae", "exuviae"),
		Map.entry("hemolymph", "hemolymph"),
		Map.entry("flesh", "flesh"),
		Map.entry("gi tract", "digestive tract"),
		Map.entry("mantle, gill", "mantle, gill"),
		Map.entry("mantle, rectum", "mantle, rectum"),
		Map.entry("byssus", "byssus"),
		Map.entry("siphon", "siphon"),
		Map.entry("tail", "tail"),
		Map.entry("haemolymph", "hemolymph"),
		Map.entry("ganglion", "ganglion"),
		Map.entry("coelomic fluid", "coelomic fluid"),
		Map.entry("seed", "seed"),
		Map.entry("frond", "frond"),
		Map.entry("stem to leaves", "stem to leaves"),
		Map.entry("plant", "plant"),
		Map.entry("cap", "cap"),
		Map.entry("lung", "lung"),
		Map.entry("oviduct", "oviduct"),
		Map.entry("fruit", "fruit"),
		Map.entry("petiole", "petiole"),
		Map.entry("flower", "flower"),
		Map.entry("root and stem", "root, stem"),
		Map.entry("tentacles", "tentacles"),
		Map.entry("aboveground portion", "above ground portion"),
		Map.entry("whole organism", "whole body"),
		Map.entry("not reported", ""),
		Map.entry("osphradium", "osphradium"),
		Map.entry("digestive gland", "digestive gland"),
		Map.entry("root", "root"),
		Map.entry("stem", "stem"),
		Map.entry("soft parts", "soft tissue"),
		Map.entry("body", "whole body"),
		Map.entry("dorsal carapace", "dorsal carapace"),
		Map.entry("soft site", "soft tissue"),
		Map.entry("whole bodies", "whole body"),
		Map.entry("ventral nerve cord", "ventral nerve cord"),
		Map.entry("leaves", "leaf"),
		Map.entry("pyloric ceca", "pyloric caeca"),
		Map.entry("green gland", "coxal gland"),
		Map.entry("nuclei crude i", "nucleus"),
		Map.entry("cells well washed", "cell"),
		Map.entry("nuclei crude ii", "nucleus"),
		Map.entry("nuclei purified", "nucleus"),
		Map.entry("nuclei with benzathrone intercalated ana-lyzed", "nucleus"),
		Map.entry("elytrom", "elytron"),
		Map.entry("wings", "wings"),
		Map.entry("thorax", "thorax"),
		Map.entry("carapace", "carapace"),
		Map.entry("labial palps", "labial palps"),
		Map.entry("digestive gland/", "digestive gland"),
		Map.entry("digestive glanded", "digestive gland"),
		Map.entry("soft party", "soft tissue"),
		Map.entry("as body fluid", "bodily fluid"),
		Map.entry("yolk", "yolk"),
		Map.entry("leaf/needle", "leaf"),
		Map.entry("adductor muscle", "adductor"),
		Map.entry("/ efct/total body , no sig elim", "whole body"),
		Map.entry("elimination described, kidney", "kidney"),
		Map.entry("dry wt tissue conc", "tissue"),
		Map.entry("branchial tissue", "branchial tissue"),
		Map.entry("gi minus tract", "digestive tract"),
		Map.entry("muscle and bone", "muscle, bone"),
		Map.entry("blood and muscle", "muscle, blood"),
		Map.entry("exoskeleton", "exoskeleton"),
		Map.entry("foregut", "foregut"),
		Map.entry("swim bladder", "swim bladder"),
		Map.entry("cytoplasm crude with theca", "theca cell cytoplasm"),
		Map.entry("nuclei with benzathrone intercalated ana minus lyzed", "nuclei"),
		Map.entry("fat", "fat"),
		Map.entry("blood cell", "blood"),
		Map.entry("tissue remainder", "tissue"),
		Map.entry("shell removed", "whole body minus shell"),
		Map.entry("whole fish without intestine", "whole body minus intestine"),
		Map.entry("edible parts, fillet,", "edible portion"),
		Map.entry("whole body rsd", "whole body"),
		Map.entry("gastrointestinal tract", "digestive tract"),
		Map.entry("exoskeleton tissue", "exoskeleton"),
		Map.entry("gill tissue", "gill"),
		Map.entry("hepatopancreas tissue", "hepatopancreas"),
		Map.entry("gonad tissue", "gonad"),
		Map.entry("muscle tissue", "muscle"),
		Map.entry("heart tissue", "heart"),
		Map.entry("head, foot, operculum", "head, foot, operculum"),
		Map.entry("columellar muscle, salivary gland, oesophageal gland, heart, body fluid, remaining tissue", "columellar muscle, salivary gland, oesophageal gland, heart, body fluid, tissue"),
		Map.entry("rest of organism", "whole body"),
		Map.entry("multiple tissue/organ", "tissue, organs"),
		Map.entry("midgut or midgut gland", "midgut"),
		Map.entry("adipose tissue", "adipose tissue"),
		Map.entry("leaf, young", "leaf"),
		Map.entry("leaf, old", "leaf"),
		Map.entry("abdominal muscle", "abdominal muscle"),
		Map.entry("anterior intestine", "anterior intestine"),
		Map.entry("cap, mushroom", "cap"),
		Map.entry("capat", ""),
		Map.entry("digestive gland, stomach", "digestive gland, stomach"),
		Map.entry("edible parts, fillet", "edible portion"),
		Map.entry("entire organism", "whole body"),
		Map.entry("epaxial muscle", "epaxial muscle"),
		Map.entry("erythrocytes", "blood"),
		Map.entry("flower/inflorescence", "flower"),
		Map.entry("food", "food"),
		Map.entry("gills, viscera", "gill, viscera"),
		Map.entry("kidneys", "kidney"),
		Map.entry("plant, unspecified", "whole body"),
		Map.entry("posterior intestine", "posterior intestine"),
		Map.entry("shell, membrane", "shell, membrane"),
		Map.entry("skin surface", "skin"),
		Map.entry("skind", "skin"),
		Map.entry("stomach and digestive gland", "digestive gland, stomach"),
		Map.entry("whole egg", "whole body"),
		Map.entry("whole mussels", "whole body"),
		Map.entry("blood and muscle ana minus lyzed", "muscle, blood"),
		Map.entry("gastro minus intestinal tract", "digestive tract"),
		Map.entry("gill ,residue 7 d post exp minus osure reported", "gill"),
		Map.entry("gills and viscera", "gill, viscera"),
		Map.entry("hepato minus pancreas", "hepatopancreas"),
		Map.entry("clearance half minus life descr", ""),
		Map.entry("considered contr for stable lead expt", ""),
		Map.entry("depuration descr", ""),
		Map.entry("depuration described", ""),
		Map.entry("elim descr", ""),
		Map.entry("elim descr on graphs", ""),
		Map.entry("elimination", ""),
		Map.entry("elimination described", ""),
		Map.entry("elimination reported on graph", ""),
		Map.entry("equilib", ""),
		Map.entry("exretion rptd, bcf at equilib = 120", ""),
		Map.entry("exretion rptd, bcf at equilib = 26", ""),
		Map.entry("exretion rptd, bcf at equilib = 63", ""),
		Map.entry("expressed as partition coefficients", ""),
		Map.entry("followedelim descr", ""),
		Map.entry("half minus life 207 h", ""),
		Map.entry("half minus life 71 h", ""),
		Map.entry("kinetic bcf", ""),
		Map.entry("kinetic bcf at equilib", ""),
		Map.entry("maximum, at equiblibrium", ""),
		Map.entry("steady state", ""),
		Map.entry("used kinetic model", ""),
		Map.entry("used plateau method", ""),
		Map.entry("(whole plant/portal)", "whole body"),
		Map.entry("above minus ground tissue", "above ground portion"),
		Map.entry("algal mass", "algal mass"),
		Map.entry("average of spinal cord, muscle, liver and tapeworm", "spine, muscle, liver, tapeworm"),
		Map.entry("bcfs based on plasma, liver, and muscle concentrations of bisphenol a and aqueous concentrations", "plasma, liver, muscle"),
		Map.entry("blood and tissues (gill, muscle, liver, kidney, intestine)", "blood, gill, muscle, liver, kidney, intestine"),
		Map.entry("bones", "bone"),
		Map.entry("brain tissue weight", "brain"),
		Map.entry("carcass and viscera", "carcass, viscera"),
		Map.entry("celery shoot concentration", "shoot"),
		Map.entry("cells", "cell"),
		Map.entry("cestodes (intestinal parasites)", "cestodes"),
		Map.entry("coelomic fluid sulfide (ww)", "coelomic fluid"),
		Map.entry("coelomic fluid ww", "coelomic fluid"),
		Map.entry("concentration in fish/concentration in water", "whole body"),
		Map.entry("concentration in soft tissue", "soft tissue"),
		Map.entry("concentration of test substance in fish, no further data available", "whole body"),
		Map.entry("concentration of test substance in fish, no further data available.", "whole body"),
		Map.entry("concentration of test substance in tissue that reduced by 50% the uptake rate of algae by mussels in comparison to the pre minus exposed control rate over a 105 minute exposure.", "tissue"),
		Map.entry("cplant / csoii where cplant and csoil represent the heavy metal concentration in extracts of plants and soils on a dry weight basis, respectively", "whole body"),
		Map.entry("deposition of metal in shell", "shell"),
		Map.entry("dried seaweed", "whole body"),
		Map.entry("edible fraction", "edible portion"),
		Map.entry("edible fraction, non minus edible fraction, whole fish", "whole body"),
		Map.entry("egg case", "egg"),
		Map.entry("eggs d.w.", "egg"),
		Map.entry("fat weight", "fat"),
		Map.entry("fern leaves", "leaf"),
		Map.entry("fern stem", "stem"),
		Map.entry("filet", "fillet"),
		Map.entry("fillet d.w.", "fillet"),
		Map.entry("fish fillet", "fillet"),
		Map.entry("fish flesh (wet weight)", "flesh"),
		Map.entry("fish muscle", "muscle"),
		Map.entry("fish sample (2 g weight)", "sample"),
		Map.entry("flounder kidney", "kidney"),
		Map.entry("flounder kidneys", "kidney"),
		Map.entry("flounder liver", "liver"),
		Map.entry("flounder muscle", "muscle"),
		Map.entry("for fish portions and whole minus fish", "whole body"),
		Map.entry("fruit tissue concentration", "fruit"),
		Map.entry("fruiting body", "above ground portion"),
		Map.entry("gill tissue weight", "gill"),
		Map.entry("gill ww", "gill"),
		Map.entry("gills, liver, kidney", "gill, liver, kidney"),
		Map.entry("grass", "grass"),
		Map.entry("hemolymph, midgut gland, muscle tissue, ww", "hemolymph, midgut, muscle"),
		Map.entry("herring liver", "liver"),
		Map.entry("herring muscle", "muscle"),
		Map.entry("jelly", "egg"),
		Map.entry("kernel concentration", "kernel"),
		Map.entry("kidney tissue weight", "kidney"),
		Map.entry("kidney, liver, gill, gonad, gastrointestinal tract", "kidney, liver, gill, gonad, digestive tract"),
		Map.entry("leaf tissue concentration", "leaf"),
		Map.entry("leaf tissue d.w.", "leaf"),
		Map.entry("leaf/water", "leaf"),
		Map.entry("leaves d.w.", "leaf"),
		Map.entry("lipid", "fat"),
		Map.entry("lipid and growht corrected kinetic", "fat"),
		Map.entry("lipid and growth corrcected", "fat"),
		Map.entry("lipid and growth corrected", "fat"),
		Map.entry("lipid and growth corrected kintic", "fat"),
		Map.entry("lipid content of ww", "fat"),
		Map.entry("lipid normalised, growth corrected kinetic bioconcentration factor", "fat"),
		Map.entry("lipid minus corrected steady state biomagnification factor.", "fat"),
		Map.entry("liver tissue", "liver"),
		Map.entry("liver tissue weight", "liver"),
		Map.entry("liver ww", "liver"),
		Map.entry("liver, muscle and spinal cord", "liver, muscle, spine"),
		Map.entry("loading level of test substance in water that reduced by 50% the uptake rate of algae by mussels in comparison to the pre minus exposed control rate over a 105 minute exposure.", "tissue"),
		Map.entry("macrophytes", "whole body"),
		Map.entry("mantle cavity fluid sulfide concentration (mg/kg ww)", "mantle fluid"),
		Map.entry("mean equilibrum 14c residue concentration in tissue by the mean measured cocnetration of the anthraquinone in the test solution during the same period", "tissue"),
		Map.entry("mean tissue concentration", "tissue"),
		Map.entry("metabolism", "metabolite"),
		Map.entry("metabolite", "metabolite"),
		Map.entry("metabolite cas 20170 minus 32 minus 5", "metabolite"),
		Map.entry("mg/kg fish", "whole body"),
		Map.entry("molusc shell", "shell"),
		Map.entry("molusc soft part", "soft tissue"),
		Map.entry("molusc plug", "plug"),
		Map.entry("molusc tissue weight", "tissue"),
		Map.entry("molusc tissues", "tissue"),
		Map.entry("muscles", "muscle"),
		Map.entry("musculature", "muscle"),
		Map.entry("non minus edible fraction", "non-edible portion"),
		Map.entry("non minus edible tissue", "non-edible portion"),
		Map.entry("organ d.w.", "organs"),
		Map.entry("organ w.w.", "organs"),
		Map.entry("ovary tissue weight", "ovary"),
		Map.entry("pea fruit concentration", "fruit"),
		Map.entry("plant dry weight", "whole body"),
		Map.entry("plant tissue", "tissue"),
		Map.entry("plasma tissue weight", "plasma"),
		Map.entry("pneumatophores", "pneumatophores"),
		Map.entry("radish root concentration", "root"),
		Map.entry("ratio of the concentration of the test substance in the fish and in water", "whole body"),
		Map.entry("remainder", "remainder"),
		Map.entry("root tissue", "root"),
		Map.entry("root tissue concentration", "root"),
		Map.entry("root tissue d.w.", "root"),
		Map.entry("root/water", "root"),
		Map.entry("root:shoot transfer factor", "root, shoot"),
		Map.entry("roots", "root"),
		Map.entry("seawater", "water"),
		Map.entry("sediment", "sediment"),
		Map.entry("seeds", "seed"),
		Map.entry("shells", "shell"),
		Map.entry("shoot and roots vs substrate after 64d", "root, shoot"),
		Map.entry("shoot dry weight", "shoot"),
		Map.entry("shoot tissue", "shoot"),
		Map.entry("shoots", "shoot"),
		Map.entry("soft parts and siphon", "soft tissue, siphon"),
		Map.entry("soft tissue dry weight", "soft tissue"),
		Map.entry("soft tissues", "soft tissue"),
		Map.entry("straw concentration", "straw"),
		Map.entry("sunflower leaves", "leaf"),
		Map.entry("syphon", "siphon"),
		Map.entry("tissue (dry weight)", "tissue"),
		Map.entry("tissue (wet weight)", "tissue"),
		Map.entry("tissue wet weight (carcass)", "carcass"),
		Map.entry("tissue wet weight (gonad)", "gonad"),
		Map.entry("total dw without gill", "whole body minus gill"),
		Map.entry("total lipid content", "fat"),
		Map.entry("tube feet", "tube feet"),
		Map.entry("vertebra", "spine"),
		Map.entry("viscera tissue", "viscera"),
		Map.entry("wet weight of bluegill tissue (carcass)", "carcass"),
		Map.entry("wet weight of bluegill tissue (gonad)", "gonad"),
		Map.entry("white skeletal muscle w.w.", "muscle"),
		Map.entry("whole body (dpm/g fish) / dpm/g water", "whole body"),
		Map.entry("whole body d.w.", "whole body"),
		Map.entry("whole body not stated wet or dry weight", "whole body"),
		Map.entry("whole body w.w, whole body d.w., lipid content", "whole body"),
		Map.entry("whole body w.w.", "whole body"),
		Map.entry("whole body weight", "whole body"),
		Map.entry("whole body weight and total fat", "whole body"),
		Map.entry("whole body ww", "whole body"),
		Map.entry("whole fish (not clearly specified if wet or dry weight)", "whole body"),
		Map.entry("whole fish tissue", "whole body"),
		Map.entry("whole fish, normalised to 5% lipid content", "whole body"),
		Map.entry("whole plant", "whole body"),
		Map.entry("whole test organism", "whole body"),
		Map.entry("2 minus 5 g tissue", "tissue"),
		Map.entry("muscle plug", "plug"),
		Map.entry("muscle tissue weight", "tissue"),
		Map.entry("muscle tissues", "tissue"),
		Map.entry("soft parts and syphon", "soft tissue, siphon"),
		Map.entry("whole body. ww of dw not specified", "whole body"),
		Map.entry("whole fish (dry or wet weight not specified)", "whole body"),
		Map.entry("whole fish, not specified", "whole body"),
		Map.entry("total body , no sig elim", "whole body"),
		Map.entry("not specified", ""),
		Map.entry("", "")
	);


}

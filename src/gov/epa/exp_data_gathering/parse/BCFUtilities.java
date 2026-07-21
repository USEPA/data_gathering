package gov.epa.exp_data_gathering.parse;

import java.util.Arrays;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.Arnot2006.ParseArnot2006;
import gov.epa.exp_data_gathering.parse.Burkhard.ParseBurkhard2;
import gov.epa.exp_data_gathering.parse.ECOTOX.ParseEcotox;
import gov.epa.exp_data_gathering.parse.ITRC.ParseITRC;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.ParseQSAR_ToolBox;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
* @author TMARTI02
*/
public class BCFUtilities {

	/**
	 * Creates files for all BCF sources all at once:
	 */
	public void ParseBCFSources() {
		
		List<String> propertyNames = Arrays.asList(ExperimentalConstants.strBCF, ExperimentalConstants.strBAF);
		boolean generateOriginalRecords=true;
		
		for (String propertyName:propertyNames) {
			System.out.println("\n**********************\n"+propertyName);
			createArnotFiles(generateOriginalRecords, propertyName);
			createBurkhardFiles(generateOriginalRecords, propertyName);
			createEcotoxFiles(generateOriginalRecords, propertyName);
			createITRC_Files(propertyName);
			createQSAR_ToolboxFiles(generateOriginalRecords);
		}
	}


	private void createQSAR_ToolboxFiles(boolean generateOriginalRecords) {
		String [] filenames= {ParseQSAR_ToolBox.fileNameBCF_ECHA_REACH, ParseQSAR_ToolBox.fileNameBCF_Canada, 
				ParseQSAR_ToolBox.fileNameBCF_CEFIC, ParseQSAR_ToolBox.fileNameBCF_NITE};

		for (String filename:filenames) {
			ParseQSAR_ToolBox p = new ParseQSAR_ToolBox(null, filename);
			p.generateOriginalJSONRecords=generateOriginalRecords;//*** set to true on first run
			p.removeDuplicates=false;
			p.writeJsonExperimentalRecordsFile=true;
			p.writeExcelExperimentalRecordsFile=true;
			p.writeExcelFileByProperty=true;		
			p.writeCheckingExcelFile=false;//creates random sample spreadsheet
			p.createFiles();
			System.out.println("********************************************\n");
		}
	}


	private void createITRC_Files(String propertyName) {
		ParseITRC p = new ParseITRC(propertyName);
		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();
	}


	private void createArnotFiles(boolean generateOriginalRecords, String propertyName) {
		Parse p = new ParseArnot2006(propertyName);
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.writeCheckingExcelFile=false;
		p.removeDuplicates=false;
		p.createFiles();
	}
	
	private void createBurkhardFiles (boolean generateOriginalRecords, String propertyName) {
		ParseBurkhard2 p = new ParseBurkhard2(propertyName);
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
	}
	
	private void createEcotoxFiles (boolean generateOriginalRecords, String propertyName) {
		ParseEcotox p = new ParseEcotox();
		p.generateOriginalJSONRecords=generateOriginalRecords;
		p.removeDuplicates=false;//cant delete duplicates because experimental params might be different but still have same number value
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.init(propertyName);//in parent Parse class
		p.propertyName=propertyName;
		p.createFiles();		
	}


	public static void setOecd305Parameters(ExperimentalRecord er) {
		// Reasonable assumptions if guideline OECD 305 was followed
		// Guideline is intended to use flow-through exposure (semi-static is permissible)
		if (er.experimental_parameters.get(ExperimentalConstants.expParamExposureType) == null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, "Flow-through");
			er.updateNote("Exposure type set to Flow-through based on OECD 305 guideline standards");
		}
		// Guideline is supposed to normalize to 5% lipid content
		if (er.parameter_values == null) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamLipidPercent;
			pv.unit.abbreviation = ExperimentalConstants.str_dimensionless;
			pv.value_point_estimate = 5.0;
			er.parameter_values.add(pv);
			er.updateNote("Lipid content set to 5% based on OECD 305 guideline standards");
		} else {
            Boolean hasLipidPercent = false;
            for (ParameterValue pv : er.parameter_values) {
                if (pv.parameter.name.equals(ExperimentalConstants.expParamLipidPercent)) {
                    hasLipidPercent = true;
                }
            }
            if (!hasLipidPercent) {
                ParameterValue pv = new ParameterValue();
                pv.parameter.name = ExperimentalConstants.expParamLipidPercent;
                pv.unit.abbreviation = ExperimentalConstants.str_dimensionless;
                pv.value_point_estimate = 5.0;
                er.parameter_values.add(pv);
                er.updateNote("Lipid content set to 5% based on OECD 305 guideline standards");
            }
        }
		// Guideline is supposed to normalize to wet-weight, might be set elsewhere
		if (er.experimental_parameters.get(ExperimentalConstants.expParamWetDry) == null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, "Wet");
			er.updateNote("Wet-weight assumed based on OECD 305 guideline standards");
		}
		// Guideline is supposed to used kinetic BCF values, might be set elsewhere
		if (er.experimental_parameters.get(ExperimentalConstants.expParamMeasurementMethod) == null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Kinetic");
			er.updateNote("Kinetic measurement method assumed based on OECD 305 guideline standards");
		}
	}

    public static Double parseTemperature(String tempStr, String unitStr) {
        if (tempStr == null || tempStr.trim().isEmpty()) return null;
        try {
            double temp = Double.parseDouble(tempStr);
            if (unitStr != null) {
                unitStr = unitStr.trim().toUpperCase(Locale.ROOT);
                if (unitStr.equals("K")) {
                    temp -= 273.15; // Convert Kelvin to Celsius
                } else if (unitStr.equals("F")) {
                    temp = (temp - 32) * 5 / 9; // Convert Fahrenheit to Celsius
                }
            }
            return temp;
        } catch (NumberFormatException e) {
            return null; // Invalid number format
        }
    }

public class TestGuidelineFormatter {

    // --- Public API ---
    public static String normalizeTestGuideline(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "";
		String s = cleanGuidelineOECD(raw);
        s = preClean(s);
        s = fixSpelling(s);

        // Placeholders / low-information
        String ph = placeholderLabel(s);
        if (ph != null) return ph;

        // Preserve exact OECD Guideline forms the way you want them
        String oecd = keepOecdGuidelineForms(s);
        if (oecd != null) return oecd;

        // Canonical known standards into a single clean string
        String std;

        // EU Method C.x
        std = matchEuMethod(s);
        if (std != null) return std;

        // US EPA OPPTS
        std = matchEpaOppts(s);
        if (std != null) return std;

        // US EPA OPP
        std = matchEpaOpp(s);
        if (std != null) return std;

        // US EPA OTS
        std = matchEpaOts(s);
        if (std != null) return std;

        // ASTM E-xxxx-yy and drafts
        std = matchAstm(s);
        if (std != null) return std;

        // Japan CSCL family (Kanpogyo/Yakuhatsu/Kikyoku) and related notices
        std = matchJapanCscl(s);
        if (std != null) return std;

        // Publications / citations (leave in single tidy string)
        if (looksLikePublication(s)) {
            s = tidyCapitalization(s);
			return s;
        } else if (s.toLowerCase().contains("as below")) {
			return null;
		} else if (s.toLowerCase().contains("as mentioned below")) {
			return null;
		} else if (s.toLowerCase().contains("as per mentioned below")) {
			return null;
		} else if (s.toLowerCase().contains("refer below")) {
			return null;
		} else if (s.toLowerCase().contains("version / remarks")) {
			return null;
		} else if (s.toLowerCase().contains("no data")) {
			return null;
		} else if (s.toLowerCase().contains("other")) {
			return null;
		} else if (s.toLowerCase().contains("nc")) {
			return null;
		} else if (s.toLowerCase().contains("not reported")) {
			return null;
		} else {
			// Fallback: cleaned string
			s = tidyCapitalization(s);
			return s;
		}
    }

    // --- Patterns ---

    private static final Pattern OECD_305 = Pattern.compile(
        "(?i)\\bOECD\\s*Guideline\\s*305\\s*([A-E]|-?I{1,3})?\\b"
    );

    private static final Pattern OECD_GENERIC = Pattern.compile(
        "(?i)\\bOECD\\s*Guideline\\s*(\\d{3}[A-E]?)\\b"
    );

    private static final Pattern EU_METHOD = Pattern.compile(
        "(?i)\\bEU\\s*Method\\s*C\\.?\\s*(\\d+)\\b(?:\\s*\\(([^)]+)\\))?"
    );

    private static final Pattern EPA_OPPTS = Pattern.compile(
        "(?i)\\b(?:US\\s*)?EPA\\s*OPPTS\\s*(\\d{3,4}\\.\\d{4})(?:\\s*\\(([^)]+)\\))?"
    );

    private static final Pattern EPA_OPP = Pattern.compile(
        "(?i)\\b(?:US\\s*)?EPA\\s*OPP\\s*(\\d{2,3}-\\d)\\b(?:\\s*\\(([^)]+)\\))?"
    );

    private static final Pattern EPA_OTS = Pattern.compile(
        "(?i)\\b(?:US\\s*)?EPA\\s*OTS\\s*(\\d{3}\\.\\d{4})(?:\\s*\\(([^)]+)\\))?"
    );

    private static final Pattern OPP_LONE_CODE = Pattern.compile(
        "(?i)^\\s*(165-4|72-6)\\b(?:\\s*\\(([^)]+)\\))?.*"
    );

    private static final Pattern ASTM_E = Pattern.compile(
        "(?i)\\bASTM\\s*(?:E[-\\s]*)?(\\d{3,4}-\\d{2})\\b"
    );

    private static final Pattern ASTM_DRAFT_NO = Pattern.compile(
        "(?i)\\bASTM\\s*(?:.*\\bDraft\\s*No\\.?\\s*(\\d+)\\b).*"
    );

    // Japan CSCL signals
    private static final Pattern JAPAN_KAN_YAK_KIK = Pattern.compile(
        "(?i)(Kanpogyo).{0,80}(Yakuhatsu).{0,80}(Kikyoku)"
    );

    private static final Pattern JAPAN_0331_7 = Pattern.compile(
        "(?i)(Yakushokuhatsu)\\s*0?331\\s*No\\.?\\s*7"
    );

    private static final Pattern JAPAN_LAW117 = Pattern.compile(
        "(?i)\\bLaw\\s*No\\.?\\s*117\\b"
    );

    private static final Pattern PLACEHOLDER =
        Pattern.compile("(?i)\\b(as (?:below|mentioned below|per mentioned below)|refer(?:\\s+.*below)?|see 'version / remarks'|no data|other|nc)\\b");

    private static final Pattern BARE_TOKEN =
        Pattern.compile("(?i)^(OPPTS|OECD|EPAOECD|EPAASTM|EPASTD)\\s*$");

    // --- Helpers ---

    private static String preClean(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        s = s.replace("\u2013", "-").replace("\u2014", "-");
        s = s.replaceAll("[<>«»]", "");       // drop angle/chevron quotes
        s = s.replaceAll("[“”\"‟\u2018\u2019]+", "");
        s = s.replaceAll("\\?+", "");         // remove stray ? used as quotes
        s = s.replaceAll("\\s+", " ").trim();
        s = s.replaceAll("\\s+([\\-:,;])", "$1");  // no space before punctuation
        s = s.replaceAll("([\\-:,;])\\s+", "$1 "); // single space after punctuation
        s = s.replaceAll("\\.+$", "");        // trim trailing dots
        return s;
    }

    private static String fixSpelling(String s) {
        // Ordered replacements (longer before shorter to avoid oscillations)
        Map<String, String> fixes = new LinkedHashMap<>();
        fixes.put("\\bKanapokihatsu\\b", "Kanpokihatsu");   // 031121002 misspelling
        fixes.put("\\bKanhogyo\\b", "Kanpogyo");
        fixes.put("\\bKanpogyou\\b", "Kanpogyo");
        fixes.put("\\bYokuhatsu\\b", "Yakuhatsu");
        fixes.put("\\bKiyoku\\b", "Kikyoku");
        fixes.put("\\bKiyo?ku\\b", "Kikyoku");              // safety
        fixes.put("\\bs\\s*tipulated\\b", "stipulated");
        fixes.put("\\bwohle\\b", "whole");
        fixes.put("\\bMollluscs\\b", "Molluscs");
        fixes.put("\\bREPDOCUTION\\b", "REPRODUCTION");
		fixes.put("Pestiefdes", "Pesticides");

        for (Map.Entry<String, String> e : fixes.entrySet()) {
            s = s.replaceAll(e.getKey(), e.getValue());
        }
        return s;
    }

    private static String placeholderLabel(String s) {
        if (s.equalsIgnoreCase("Not reported")) return null;
        if (PLACEHOLDER.matcher(s).find()) return null;

        Matcher bare = BARE_TOKEN.matcher(s);
        if (bare.find()) {
            String token = bare.group(1).toUpperCase(Locale.ROOT);
            if ("OPPTS".equals(token)) return "US EPA OPPTS (unspecified)";
            if ("OECD".equals(token)) return "OECD Guideline (unspecified)";
            // EPAOECD, EPAASTM, EPASTD (and similar mixed tokens) → collapse
            return null;
        }

        // “OECD - OECD, 2012” style
        if (s.matches("(?i)^OECD\\s*[-–]\\s*OECD,?\\s*\\d{4}.*")) {
            return "OECD Guideline (unspecified)";
        }
        return null;
    }

	public static String cleanGuidelineOECD(String guideline) {
		if (guideline == null) return null;
		if (ExperimentalConstants.guidelineHashMap.containsKey(guideline)) {
			return ExperimentalConstants.guidelineHashMap.get(guideline);
		} else if (guideline.toLowerCase().contains("oecd tg 305c")) {
			// Handles an edge case with unicode quotes that can't be replaced properly
			return "OECD Guideline 305C";
		}
		return guideline;
	}

    private static String keepOecdGuidelineForms(String s) {
        // Keep exactly “OECD Guideline 305”, “OECD Guideline 305-I”, “OECD Guideline 305A…E”, etc.
        Matcher m = OECD_305.matcher(s);
        if (m.find()) {
            String suffix = m.group(1);
            if (suffix == null) return "OECD Guideline 305";
            String cleaned = suffix.toUpperCase(Locale.ROOT).replaceAll("\\s+", "");
            return "OECD Guideline 305" + cleaned;
        }

        // Other OECD Guideline entries like 203, 210, 319A/B
        m = OECD_GENERIC.matcher(s);
        if (m.find()) {
            String code = m.group(1).toUpperCase(Locale.ROOT);
            return "OECD Guideline " + code;
        }

		return null;
    }

    private static String matchEuMethod(String s) {
        Matcher m = EU_METHOD.matcher(s);
        if (!m.find()) return null;
        String code = m.group(1);
        String title = m.group(2) != null ? (" (" + cleanTitle(m.group(2)) + ")") : "";
        return "EU Method C." + code + title;
    }

    private static String matchEpaOppts(String s) {
        Matcher m = EPA_OPPTS.matcher(s);
        if (!m.find()) return null;
        String code = m.group(1);
        String title = m.group(2) != null ? (" (" + cleanTitle(m.group(2)) + ")") : "";
        return "US EPA OPPTS " + code + title;
    }

    private static String matchEpaOpp(String s) {
        Matcher m = EPA_OPP.matcher(s);
        if (m.find()) {
            String code = m.group(1);
            String title = m.group(2) != null ? (" (" + cleanTitle(m.group(2)) + ")") : "";
            return "US EPA OPP " + code + title;
        }
        // Lone codes like “165-4 Bioaccumulation in Fish”
        m = OPP_LONE_CODE.matcher(s);
        if (m.find()) {
            String code = m.group(1);
            String title = m.group(2) != null ? (" (" + cleanTitle(m.group(2)) + ")") : "";
            return "US EPA OPP " + code + title;
        }
        return null;
    }

    private static String matchEpaOts(String s) {
        Matcher m = EPA_OTS.matcher(s);
        if (!m.find()) return null;
        String code = m.group(1);
        String title = m.group(2) != null ? (" (" + cleanTitle(m.group(2)) + ")") : "";
        return "US EPA OTS " + code + title;
    }

    private static String matchAstm(String s) {
        Matcher m = ASTM_E.matcher(s);
        if (m.find()) {
            String code = m.group(1).toUpperCase(Locale.ROOT).replaceAll("^-*", "");
            if (!code.startsWith("E")) code = "E" + code;
            return "ASTM " + code;
        }
        m = ASTM_DRAFT_NO.matcher(s);
        if (m.find()) {
            return "ASTM Draft No. " + m.group(1);
        }
        if (s.toUpperCase(Locale.ROOT).contains("ASTM")) {
            // generic ASTM reference
            return "ASTM (unspecified)";
        }
        return null;
    }

    private static String matchJapanCscl(String s) {
        if (JAPAN_KAN_YAK_KIK.matcher(s).find()) {
            return "Japan CSCL bioconcentration test (Kanpogyo No.5; Yakuhatsu No.615; 49 Kikyoku No.392)";
        }
        if (JAPAN_0331_7.matcher(s).find()) {
            return "Japan CSCL bioconcentration test (Yakushokuhatsu 0331 No.7)";
        }
        if (JAPAN_LAW117.matcher(s).find()
                || s.toLowerCase(Locale.ROOT).contains("law no. 117")
                || s.toLowerCase(Locale.ROOT).contains("law 117")) {
            return "Japan Law No. 117 bioconcentration test";
        }
        // MITI / Japanese guideline generic mentions → compact label
        if (s.toLowerCase(Locale.ROOT).contains("miti")
                || s.toLowerCase(Locale.ROOT).contains("japan")
                || s.toLowerCase(Locale.ROOT).contains("japanese guideline")) {
            return "Japan CSCL bioconcentration test";
        }
        return null;
    }

    private static String cleanTitle(String t) {
        String s = t.replaceAll("\\s+", " ").trim();
        if (s.isEmpty()) return s;
        // Capitalize first letter, keep acronyms
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    private static boolean looksLikePublication(String s) {
        boolean hasYear = Pattern.compile("\\b(19|20)\\d{2}\\b").matcher(s).find();
        boolean hasAuthors = Pattern.compile("(?i)[A-Z][a-z]+\\s*,\\s*[A-Z]\\.?").matcher(s).find()
                || s.contains("ET AL") || s.contains("et al");
        boolean hasJournalish = Pattern.compile("(?i)(Environ|Toxicol|Technol|Journal|Chem|Report)").matcher(s).find();
        return hasYear && (hasAuthors || hasJournalish);
    }

    private static String tidyCapitalization(String s) {
		if (looksLikePublication(s) && s.charAt(0) == s.toLowerCase(Locale.ROOT).charAt(0)) {
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}

        if (s.length() > 10 && s.equals(s.toUpperCase(Locale.ROOT))) {
            String lower = s.toLowerCase(Locale.ROOT);
            return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
        } else if (s.length() > 6 && s.equals(s.toLowerCase(Locale.ROOT))) {
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		} else if (s.charAt(0) == s.toLowerCase(Locale.ROOT).charAt(0)) {
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}

        return s;
    }

    // --- Quick demo ---
    public static void test(String[] args) {
        String[] samples = {
            // "OECD Guideline 305",
            // "OECD Guideline 305-I",
            // "OECD Guideline 210",
            // "EU Method C.13 (Bioconcentration: Flow-through Fish Test)",
            // "EPA OPPTS 850.1730 (Fish Bioconcentration Test)",
            // "EPA OPP 165-4 (Laboratory Studies of Pesticide Accumulation in Fish)",
            // "EPA OTS 797.1520 (Fish Bioconcentration Test-Rainbow Trout)",
            // "ASTM E1022-94",
            // "ASTM Proposed Standard Practice ... Draft No. 9",
            // "in vivo fish bioconcentration study guidelines referenced in Kanpogyo Notification No. 5, Yakuhatsu Notification No. 615 and 49 Kiyoku Notification No. 392",
            // "Study method concerning new chemical substances? provided in Kanpogyo No5, Yakuhatsu No.615 and 49 Kiyoku No.392, 1974",
            // "Test Method Relating to New Chemical Substances (Yakushokuhatsu 0331 No.7 ... 2011)",
            // "In accordance with the Law ... (Japan 1973. Law No. 117)",
            // "as per mentioned below",
            // "no data",
            // "OPPTS",
            // "EPAOECD",
            // "OECD - OECD, 2012",
            // "Oliver, B.G. and A.J. Niimi. 1983. ... Environ. Sci. Technol.",
			// "‘’OECD TG 305C “Degree of Bioconcentration in Fish’’",
			// "\u2018\u2019OECD TG 305C “Degree of Bioconcentration in Fish\u2019\u2019",
			"as below",
			"draft document of the new OECD TG for the S9 assay.",
			"no data",
			"proposed guidelines of the Environmental Protection Agency as published in the Federal Register July 10, 1978 and modified October 3, 1980; specifically parts 163.62-1l(d) and 163.165-3 entitled Fish Accumulation",
			"see 'Version / remarks'"
        };
        for (String s : samples) {
            System.out.printf("%-140s -> %s%n", s, normalizeTestGuideline(s));
        }
    }
}

	
	public static void main(String[] args) {
		BCFUtilities b=new BCFUtilities();
		b.ParseBCFSources();
		// TestGuidelineFormatter.test(args);
	}

}

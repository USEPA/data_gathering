package gov.epa.exp_data_gathering.parse.EChemPortal;

/**
* @author TMARTI02
*/
import java.util.*;
import java.util.regex.Pattern;

public class BiodegradabilityClassifier {

    public enum ClassLabel {
        READILY(1),
        NOT_READILY(2),
        FAIL_10D(3),
        CANT_ASSIGN(4);

        public final int code;
        ClassLabel(int c) { this.code = c; }
    }

    // Optional exact overrides (normalized key -> label)
    private final Map<String, ClassLabel> overrides = new HashMap<>();

    // Core patterns (operate on normalized, lowercase input)
    private static final Pattern P_NOT_READILY_EXPLICIT = Pattern.compile(
        "\\b(not\\s+readily\\s+biodegrad\\w*|no\\s+readily\\s+biodegrad\\w*|nor\\s+readily\\s+biodegrad\\w*|"
      + "cannot\\s+be\\s+considered\\s+to\\s+be\\s+readily\\s+biodegrad\\w*|"
      + "not\\s+considered\\s+to\\s+be\\s+readily\\s+biodegrad\\w*|"
      + "test\\s+item\\s+is\\s+not\\s+readily\\s+biodegrad\\w*|"
      + "the\\s+substance\\s+is\\s+not\\s+readily\\s+biodegrad\\w*)"
    );

    private static final Pattern P_NO_BIODEG = Pattern.compile(
        "under\\s+test\\s+conditions\\s+no\\s+biodegrad\\w*|\\bnegligible\\s+biodegrad\\w*"
    );

    // “failing 10-day window” variants
    private static final Pattern P_FAIL_10D = Pattern.compile(
        "\\bfail\\w*\\b[^\\n]*\\b10\\s*[- ]?d(?:ay)?\\s*window\\b|"
      + "\\b10\\s*[- ]?d(?:ay)?\\s*window\\b[^\\n]*\\bfail\\w*\\b"
    );

    // “10-day window does not apply / not applicable / waived”
    private static final Pattern P_10D_NOT_APPLY = Pattern.compile(
        "\\b10\\s*[- ]?d(?:ay)?\\s*window\\b[^\\n]*\\b(does\\s+not\\s+apply|not\\s+applicable|waiv\\w+)\\b"
    );

    // “considered/regarded as readily biodegradable”
    private static final Pattern P_CONSIDERED_READILY = Pattern.compile(
        "\\b(consider\\w*|regard\\w*)\\b[^\\n]{0,80}\\breadily\\s+biodegrad\\w*"
    );

    // Positive “readily biodegradable”
    private static final Pattern P_READILY = Pattern.compile("\\breadily\\s+biodegrad\\w*");

    // Ambiguous / no conclusion
    private static final Pattern P_AMBIG = Pattern.compile(
        "readily\\s*/\\s*not\\s*readily|study\\s+cannot\\s+be\\s+used|no\\s+conclusion|time\\s+duration\\s+not\\s+sufficient|^other$"
    );

    public BiodegradabilityClassifier() { }

    public ClassLabel classify(String raw) {
        if (raw == null) return ClassLabel.CANT_ASSIGN;
        String s = norm(raw);

        // 0) overrides
        ClassLabel ov = overrides.get(s);
        if (ov != null) return ov;

        // 1) Ambiguous/no conclusion first
        if (P_AMBIG.matcher(s).find()) return ClassLabel.CANT_ASSIGN;

        // 2) New rule: must contain the word "readily" to be classifiable
        if (!s.contains("readily")) return ClassLabel.CANT_ASSIGN;

        // 3) Strong negatives
        if (P_NOT_READILY_EXPLICIT.matcher(s).find()) return ClassLabel.NOT_READILY;
        if (P_NO_BIODEG.matcher(s).find() && s.contains("readily")) return ClassLabel.NOT_READILY;

        // 4) 10-day window logic
        boolean hasFail10d = P_FAIL_10D.matcher(s).find();
        boolean notApply10d = P_10D_NOT_APPLY.matcher(s).find();
        boolean consideredReadily = P_CONSIDERED_READILY.matcher(s).find();
        if (hasFail10d) {
            // If 10-day window waived/not applicable AND text concludes readily → consider readily
            if (notApply10d && (consideredReadily || P_READILY.matcher(s).find())) {
                return ClassLabel.READILY;
            }
            return ClassLabel.FAIL_10D;
        }

        // 5) Positive readily
        if (P_READILY.matcher(s).find()) return ClassLabel.READILY;

        // 6) Default (even with "readily" present but no decisive pattern)
        return ClassLabel.CANT_ASSIGN;
    }

    private static String norm(String x) {
        String t = x.trim().toLowerCase(Locale.ROOT);
        t = t.replace('–', '-').replace('—', '-');
        t = t.replaceAll("\\s+", " ");
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() > 1) {
            t = t.substring(1, t.length() - 1).trim();
        }
        return t;
    }

    // Allow adding specific phrase overrides if needed
    public void putOverride(String rawKey, ClassLabel label) {
        overrides.put(norm(rawKey), label);
    }

    // ---------- Demo: classify the full list ----------
    public static void main(String[] args) {
        BiodegradabilityClassifier clf = new BiodegradabilityClassifier();

        String[] inputs = new String[] {
            "readily biodegradable",
            "readily biodegradable, but failing 10-day window",
            "inherently biodegradable, not fulfilling specific criteria",
            "not readily biodegradable",
            "under test conditions no biodegradation observed",
            "not inherently biodegradable",
            "inherently biodegradable",
            "Rapidly biodegradable and potentially readily biodegradable.",
            "moderately",
            "not readily biodegradable under the respective test conditions, but overall/potential biodegradability has been indicated.",
            "poorly biodegradable",
            "not readily biodegradable After prolonged test duration and adaption biodegradable.",
            "biodegradable after adaption and in a prolonged test",
            "not readily biodegradable according to OECD criteria. However,  the product is biodegradable after extended adaptation.",
            "The substance is partly biodegradable (25%) under test conditions",
            "not readily biodegradable under the chosen test conditions",
            "readily biodegradable according to OECD criteria",
            "not readily bioderadable (according to OECD criteria), however,  partly or moderately biodegradable.",
            "Moderately biodegradable",
            "biodegradable",
            "test item is not ready biodegradable",
            "Does not meet the criteria for ready biodegradability",
            "Time duration not sufficient in order to conclude.",
            "other",
            "Not readily biodegradable but Inherently biodegradable",
            "Not readily biodegradable but Inherently and ultimately biodegradable",
            "readily biodegradable, but failing 10-day window In this study the 10% biodegradation of ThOD starts day 13 and the biodegradation reached the pass level 60% of ThOD day 27 (i.e. > 10 days, see green cells Table 3). Consequently, the test item 1,1'-isopropylidenebis(p-phenyleneoxy)dipropan-2-ol was not readily biodegradable under the conditions of the test.",
            "partially biodegradable",
            "inherently biodegradable, fulfilling specific criteria",
            "Not readily biodegradable under the conditions in this test.",
            "the tested substance is not readily biodegradable (60% biodegradation level not achieved after adaption time (10% degradation) of 11 days)",
            "under test conditions no biodegradation observed Test item might be inhibitory.",
            "not readily biodegradable A degradation of <10% indicate that the test substance is poorly biodegradable.",
            "not considered to be ready biodegradable",
            "readily biodegradable The mean biodegradation of at least 10% of Trimethylamine N-Oxide Anhydrate was reached at day 4 (ThODNO3). At the end of the 10-day window at day 14, the mean degradation of Trimethylamine N-Oxide Anhydrate was 86% (ThODNO3) and therefore the 10 day window criterion was passed. The mean biodegradation at test end after 28 days was 91% (ThODNO3).The test item can therefore be considered as readily biodegradable.",
            "not readily biodegradable not readily but biodegradable",
            "The test substance is in this test poorly biodegradable (not readily-biodegradable under the test conditions)",
            "not readily biodegradable One of 3 replicates reached the trigger of 60% for biodegratation (61%). The 10 day window is not applicable for a UVCB.",
            "not readily biodegradable by this test, but complete mineralisation within 60 days",
            "inherently biodegradable 60% of biodegradation at 60 days is observed",
            "partly or moderately biodegradable and not readily biodegradable according to OECD criteria",
            "The criterion for the OECD guidelines was not reached for the test article which means that it cannot be regarded as readily biodegradable.",
            "poorly biodegradable (and not readily according to OECD criteria)",
            "moderately/partly biodegradable, but not readily biodegradable according to OECD criteria",
            "inherently biodegradable, fulfilling specific criteria The test substancee should be regarded as inherent, ultimately biodegradable based on 63% biodegradation after 28 days (not fulfilling the 10-day-window).",
            "under test conditions the test item is classified as not readily biodegradable",
            "Not readily biodegradable; poorly biodegradable",
            "Not readily biodegradable under the conditions of this test",
            "Readily biodegradable; no information on degradation in 10 day window",
            "Not readily biodegradable under the conditions in this test",
            "readily / not readily / inherently biodegradable",
            "moderately biodegradable after adaption",
            "Not Readily biodegradable but inherently and Ultimately Biodegradable.",
            "not readily biodegradable Substance is not persistent (62% degradation after 61 days).",
            "not readily biodegrdable (according to OECD criteria), partly or moderately biodegradable",
            "biodegradable, the pass level for ready biodegradability was not reached",
            "inherently biodegradable, fulfilling specific criteria See detailed argumentation given in the endpoint summary.",
            "Maximum biodegradation level of 42 % in 58 days",
            "not ready biodegradable",
            "The test substance can therefore not considered to be ready biodegradable.",
            "Nor readily biodegradable according OECD criteria",
            "not readily biodegradable, but moderate biodegradation was observed",
            "Not readily biodegradable according to OECD criteria.",
            "Not readily biodegradable.",
            "No biodegradation based on oxygen consumption, but specific analysis confirms degradation of the formate ester to the corresponding alcohol product.",
            "Not readily biodegradable under test conditions",
            "BOD of ThOD; poorly biodegradable",
            "at least partially biodegradable",
            "rapidly biodegradable",
            "under test conditions some components are biodegradable, some are not",
            "Study cannot be used for classification",
            "no readily biodegradable",
            "inherently biodegradable, fulfilling specific criteria Cedarwood Oil Virginia is a mixture of chemicals (UVCB), the time window should therefore not be applied to this multi-constituent substance (OECD, 2006)",
            "not readily biodegradable Biodegradation occured, 59% at day 60, but not enough to reach the ready biodegradation treshold.",
            "readily biodegradable Based on ThODNH4",
            "readily biodegradable Amyris Oil is a mixture of chemicals (UVCB), the time window should therefore not be applied to this multi-constituent substance (OECD, 2006)",
            "readily biodegradable Since the test substance is a UVCB substance, the 10-day window criterion does not apply, and the test item can be considered as readily biodegradable.",
            "inherently and ultimately biodegradable",
            "extensively and rapidly biodegraded, reaching  >60% biodegradation mark",
            "Partial degradation, but not readily biodegradable",
            "partly/moderate biodegradable",
            "not readily biodegradable The pH 6.0-8.5 range criteria does not automatically invalidate the test, but is suggestive that repetition of testing be completed using lower concentrations of test item, and that a 'ready biodegradable' conclusion may be possible to be achieved.",
            "negligible biodegradation, not readily biodegradable",
            "not readily biodegradable but not persistent in the environment.",
            "not readily biodegradable primary biodegradation metabolite of registered substance",
            "Not readily biodegradable but complete primary degradation",
            "not readily biodegradable under the test conditions.",
            "partly or moderately biodegradable",
            "slowly biodegraded",
            "not readily biodegradable Test item may be inhibitory.",
            "biodegradable, not readily biodegradable according to OECD  criteria.",
            "Not readily biodegradation",
            "readily biodegradable, but failing 10-day window. For UVCB-substances the 10-day window condition does not apply (Commission Regulation (EU) No 286/2011).",
            "not readily biodegradable according to OECD criteria",
            "The test substance is not ready biodegradable.",
            "Biodegradable (not readily biodegradable according to OECD criteria)",
            "under test conditions, not readily biodegradable",
            "observed biodegradation not sufficient for labeling as readily biodegradable",
            "not readily biodegradable, but biodegradation observed",
            "The test item is considered to be \\Not Readily Biodegradable\\.",
            "The substance is not readily biodegradable.",
            "not readily biodegradability under the conditions of this test.",
            "not readily biodegradable after 28 days of incubation",
            "the substance is potentially biodegradable",
            "not readily biodegradable (according to OECD criteria)",
            "not considered to be readily biodegradable under the conditions of this test",
            "inherently biodegradable, fulfilling specific criteria Whilst the test material cannot be considered to be readily biodegradable under the strict terms and conditions of OECD Guideline 301F, the  evidence for aerobic degradability (52.4 % after 28 days and 62.2 % after 56 days) could be shown.",
            "see considerations in the attached document",
            "readily biodegradable, but failing 10-day window According to chapter R. 7b of the ‘Guidance on information requirements and chemical safety assessment - Endpoint specific guidance' (ECHA, 2017) the 10 d window does not apply for UVCB substances.",
            "biodegradation seen but not meet the criteria of ready biodegradable",
            "not readily biodegradable 60% BOD/ThOD after 28 days, but failing 10-day window criteria",
            "under test conditions no biodegradation observed The test item cannot be considred as readily biodegradable under OECD Guideline No. 301F",
            "readily biodegradable, but failing 10-day window Since the substance is an UVCB and it reach > 60% in 28 days, it is considered as readily bioderadable.",
            "Only limited biodegradation observed (8.6%/28 d)",
            "under test conditions no biodegradation observed not readily biodegradable",
            "readily biodegradable, but failing 10-day window As the test item was a UVCB, the 10-day window criteria does not apply and therefore the test item can be considered to be readily biodegradable under the strict terms and conditions of OECD Guideline No. 301F.",
            "No conclusion is given as to whether the test substance can be considered as readily biodegradable.",
            "not readily biodegradable The test item attained 2% biodegradation after 28 days, calculated from the oxygen consumption values, and therefore cannot be considered to be readily biodegradable under the strict terms and conditions of OECD Guideline No. 301F.",
            "not readily biodegradable under conditions of this study",
            "readily biodegradable REACH permits waiving the 10 window for multi constituent’s substances in cases where the components are defined as a homologous series of substances within a certain range of carbon chain length and/or degree of substitution. The test substance is considered to consist of components with different carbon chain lengths.",
            "not readily biodegradable test item has the potential for biodegradation, but it is not readily biodegradable.",
            "43.7 %",
            "readily biodegradable The test item was found to be ready and completely biodegradable under the conditions applied in a manometric respirometry test.",
            "readily biodegradable The 10d windows criterion is not fulfilled, but since the substance is a multiconstituent, this criterion does not apply."
        };

        Map<ClassLabel, Integer> counts = new EnumMap<>(ClassLabel.class);
        for (ClassLabel c : ClassLabel.values()) counts.put(c, 0);

        System.out.println("code\tlabel\tinput");
        for (String s : inputs) {
            ClassLabel lab = clf.classify(s);
            counts.put(lab, counts.get(lab) + 1);
            System.out.println(lab.code + "\t" + lab.name() + "\t" + s);
        }

        System.out.println("\nSummary:");
        for (ClassLabel c : ClassLabel.values()) {
            System.out.printf("%d (%s): %d%n", c.code, c.name(), counts.get(c));
        }
    }
}
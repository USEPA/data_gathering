package gov.epa.exp_data_gathering.parse;

/**
* @author TMARTI02
*/
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EstimateParser {

    public static class Estimate {
        public Double min;   // lower bound (null if not a range/inequality)
        public Double max;   // upper bound (null if not a range/inequality)
        public Double point; // single value (set only when min/max are both null)

        @Override
        public String toString() {
            return "Estimate{min=" + min + ", max=" + max + ", point=" + point + '}';
        }
    }

    // Patterns
    private static final Pattern OP_NUM     = Pattern.compile("(>=|>|<=|<)\\s*([+-]?\\d+(?:\\.\\d+)?)");
    private static final Pattern RANGE_DASH = Pattern.compile("([+-]?\\d+(?:\\.\\d+)?)\\s*[-–—]\\s*([+-]?\\d+(?:\\.\\d+)?)");
    private static final Pattern NUM_OP_NUM = Pattern.compile("([+-]?\\d+(?:\\.\\d+)?)\\s*(<=|<|>=|>)\\s*([+-]?\\d+(?:\\.\\d+)?)");
    private static final Pattern SINGLE_NUM = Pattern.compile("^\\s*([+-]?\\d+(?:\\.\\d+)?)\\s*$");

    public static Estimate parse(String raw) {
        if (raw == null) return null;
        String s = normalize(raw);

        Estimate est = new Estimate();

        // 1) Both-sided inequalities: ">= 76.1 <= 93.9", "> 3 < 4"
        Matcher mOps = OP_NUM.matcher(s);
        List<String> ops = new ArrayList<>();
        List<Double> nums = new ArrayList<>();
        while (mOps.find()) {
            ops.add(mOps.group(1));
            nums.add(Double.valueOf(mOps.group(2)));
        }
        if (ops.size() >= 2) {
            Double lower = null, upper = null;
            if (ops.get(0).startsWith(">")) lower = nums.get(0);
            if (ops.get(0).startsWith("<")) upper = nums.get(0);
            if (ops.get(1).startsWith(">")) lower = nums.get(1);
            if (ops.get(1).startsWith("<")) upper = nums.get(1);
            assignRange(est, lower, upper);
            // point stays null when min/max are set
            return est;
        }

        // 2) Dash range: "80—90", "30–40"
        Matcher mDash = RANGE_DASH.matcher(s);
        if (mDash.find()) {
            double a = Double.parseDouble(mDash.group(1));
            double b = Double.parseDouble(mDash.group(2));
            assignRange(est, Math.min(a, b), Math.max(a, b));
            return est;
        }

        // 3) Number-op-number: "0 <= 5"
        Matcher mNumOpNum = NUM_OP_NUM.matcher(s);
        if (mNumOpNum.find()) {
            double left  = Double.parseDouble(mNumOpNum.group(1));
            double right = Double.parseDouble(mNumOpNum.group(3));
            assignRange(est, Math.min(left, right), Math.max(left, right));
            return est;
        }

        // 4) Single-sided inequality: "<= 70.6", "> 25"
        if (!ops.isEmpty()) {
            String op = ops.get(0);
            double val = nums.get(0);
            if (op.startsWith("<")) {
                est.max = val;
            } else {
                est.min = val;
            }
            // point stays null when min/max are set
            return est;
        }

        // 5) Plain single number: set point only (min/max remain null)
        Matcher mSingle = SINGLE_NUM.matcher(s);
        if (mSingle.find()) {
            est.point = Double.parseDouble(mSingle.group(1));
            return est;
        }

        // Fallback: first number → point only
        Matcher anyNum = Pattern.compile("([+-]?\\d+(?:\\.\\d+)?)").matcher(s);
        if (anyNum.find()) {
            est.point = Double.parseDouble(anyNum.group(1));
            return est;
        }

        // No numeric content
        return est;
    }

    private static void assignRange(Estimate est, Double lower, Double upper) {
        est.min = lower;
        est.max = upper;
        if (est.min != null && est.max != null && est.min > est.max) {
            double t = est.min; est.min = est.max; est.max = t;
        }
        est.point = null; // enforce rule: if has min or max, don't set point
    }

    private static String normalize(String s) {
        String t = s.trim().toLowerCase(Locale.ROOT);
        // Normalize unicode dashes to ASCII hyphen
        t = t.replace('\u2013', '-').replace('\u2014', '-');
        // Remove benign prefixes like "ca.", "~", "approx"
        t = t.replace("ca.", " ").replace("approx.", " ").replace("~", " ");
        // Collapse spaces
        t = t.replaceAll("\\s+", " ");
        return t;
    }

    // Demo (optional)
    public static void main(String[] args) {
        String[] samples = {
            ">= 76.1 <= 93.9", // min=76.1, max=93.9, point=null
            "0",               // point=0, min=max=null
            "93",              // point=93
            "80—90",           // min=80, max=90, point=null
            "<= 70.6",         // max=70.6, point=null
            "> 25",            // min=25, point=null
            ">= 9 <= 10",      // min=9, max=10, point=null
            "0 <= 5",          // min=0, max=5, point=null
            "> 3 < 4",         // min=3, max=4, point=null
            "ca. 82"           // point=82
        };
        for (String s : samples) {
            Estimate e = parse(s);
            System.out.println(s + " -> " + e);
        }
    }
}
package gov.epa.exp_data_gathering.parse;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;
/**
 * @author TMARTI02
 */
public class ChemicalNameFixer {

	private static final Pattern NAMED_ENTITY = Pattern.compile("&([A-Za-z]+);?");
	private static final Pattern NUMERIC_ENTITY = Pattern.compile("&#(x?[0-9A-Fa-f]+);?");
	private static final Pattern QUOTE_LIKE = Pattern.compile("[\u2018\u2019\u02BC\u00B4\u2032]");

	private static String getReplacementForNamedEntity(String entity) {
		String lower = entity.toLowerCase(Locale.ROOT);
		if (lower.equals("nbsp"))
			return " ";
		if (lower.equals("micro"))
			return "mu";

		String name = switch (lower) {
		case "alpha" -> "alpha";
		case "beta" -> "beta";
		case "gamma" -> "gamma";
		case "delta" -> "delta";
		case "epsilon" -> "epsilon";
		case "zeta" -> "zeta";
		case "eta" -> "eta";
		case "theta", "thetasym", "vartheta" -> "theta";
		case "iota" -> "iota";
		case "kappa" -> "kappa";
		case "lambda" -> "lambda";
		case "mu" -> "mu";
		case "nu" -> "nu";
		case "xi" -> "xi";
		case "omicron" -> "omicron";
		case "pi" -> "pi";
		case "rho" -> "rho";
		case "sigma", "sigmaf" -> "sigma";
		case "tau" -> "tau";
		case "upsilon", "upsih" -> "upsilon";
		case "phi", "varphi" -> "phi";
		case "chi" -> "chi";
		case "psi" -> "psi";
		case "omega" -> "omega";
		default -> null;
		};

		if (name == null)
			return null;
		if (Character.isUpperCase(entity.charAt(0))) {
			return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
		}
		return name;
	}


	
	private static String applyDashes(String replacement, String original, int startIdx, int endIdx) {
	    // If it's just a space (from &nbsp;), don't add dashes
	    if (replacement.equals(" ")) return replacement;

	    // 1. Check for leading digit: e.g., (1&alpha; -> (1-alpha
	    boolean needsLeadingDash = startIdx > 0 && Character.isDigit(original.charAt(startIdx - 1));
	    
	    // 2. Check for existing trailing dash: e.g., &alpha;-thujone -> alpha-
	    boolean alreadyHasTrailingDash = endIdx < original.length() && original.charAt(endIdx) == '-';

	    String result = replacement;
	    if (needsLeadingDash) {
	        result = "-" + result;
	    }
	    if (!alreadyHasTrailingDash) {
	        result = result + "-";
	    }
	    
	    return result;
	}
	
	public static String greekEntitiesToNames(String input) {
	    if (input == null || input.isEmpty()) return input;

	    // Process Named Entities
	    Matcher m1 = NAMED_ENTITY.matcher(input);
	    StringBuilder sb = new StringBuilder();
	    while (m1.find()) {
	        String replacement = getReplacementForNamedEntity(m1.group(1));
	        if (replacement != null) {
	            // Pass start and end index to check surroundings
	            replacement = applyDashes(replacement, input, m1.start(), m1.end());
	            m1.appendReplacement(sb, Matcher.quoteReplacement(replacement));
	        } else {
	            m1.appendReplacement(sb, Matcher.quoteReplacement(m1.group()));
	        }
	    }
	    m1.appendTail(sb);
	    String result = sb.toString();

	    // Process Numeric Entities
	    Matcher m2 = NUMERIC_ENTITY.matcher(result);
	    sb = new StringBuilder();
	    while (m2.find()) {
	        try {
	            String body = m2.group(1);
	            int cp = (body.startsWith("x") || body.startsWith("X")) 
	                    ? Integer.parseInt(body.substring(1), 16) : Integer.parseInt(body, 10);

	            String name = getGreekNameForCodepoint(cp);
	            if (name != null) {
	                name = applyDashes(name, result, m2.start(), m2.end());
	                m2.appendReplacement(sb, Matcher.quoteReplacement(name));
	            } else {
	                m2.appendReplacement(sb, Matcher.quoteReplacement(new String(Character.toChars(cp))));
	            }
	        } catch (Exception e) {
	            m2.appendReplacement(sb, Matcher.quoteReplacement(m2.group()));
	        }
	    }
	    m2.appendTail(sb);
	    return sb.toString().replace('\u00A0', ' ');
	}

	private static String getGreekNameForCodepoint(int cp) {
		return switch (cp) {
		case 0x03B1 -> "alpha";
		case 0x03B2 -> "beta";
		case 0x03B3 -> "gamma";
		case 0x03B4 -> "delta";
		case 0x03B5 -> "epsilon";
		case 0x03B6 -> "zeta";
		case 0x03B7 -> "eta";
		case 0x03B8, 0x03D1 -> "theta";
		case 0x03B9 -> "iota";
		case 0x03BA -> "kappa";
		case 0x03BB -> "lambda";
		case 0x03BC, 0x00B5 -> "mu";
		case 0x03BD -> "nu";
		case 0x03BE -> "xi";
		case 0x03BF -> "omicron";
		case 0x03C0 -> "pi";
		case 0x03C1 -> "rho";
		case 0x03C2, 0x03C3 -> "sigma";
		case 0x03C4 -> "tau";
		case 0x03C5, 0x03D2 -> "upsilon";
		case 0x03C6, 0x03D5 -> "phi";
		case 0x03C7 -> "chi";
		case 0x03C8 -> "psi";
		case 0x03C9 -> "omega";
		default -> null;
		};
	}

//	public static String fixName(String name) {
//		if (name == null)
//			return null;
//		String fixed = QUOTE_LIKE.matcher(name).replaceAll("'");
//		return greekEntitiesToNames(fixed);
//	}
	
	
	public static String replaceCommonHtmlEntities(String s) {
	    if (s == null) return null;

	    return s
	        .replace("&rsquo;", "'")
	        .replace("&rsquo", "'")
	        .replace("&lsquo;", "'")
	        .replace("&lsquo", "'")
	        .replace("&quot;", "\"")
	        .replace("&quot", "\"")
	        .replace("&amp;", "&")
	        .replace("&amp", "&")
	        .replace("&nbsp;", " ")
	        .replace("&nbsp", " ")
	        .replace("&sup1;", "1")
	        .replace("&sup1", "1")
	        .replace("&sup2;", "2")
	        .replace("&sup2", "2")
	        .replace("&sup3;", "3")
	        .replace("&sup3", "3")
	        .replace("&lt;", "<")
	        .replace("&lt", "<")
	        .replace("&gt;", ">")
	        .replace("&gt", ">")
	        .replace("&plusmn;", "±")
	        .replace("&plusmn", "±")
	        .replace("&prime;", "'")
	        .replace("&prime", "'")
	        .replace("&ndash;", "-")
	        .replace("&ndash", "-")
	        .replace("&auml;", "ä")
	        .replace("&auml", "ä")
	        .replace("&szlig;", "ß")
	        .replace("&szlig", "ß")
	        .replace("&reg;", "®")
	        .replace("&reg", "®");
	}
	
	
	public static String fixName(String name) {
	    if (name == null)
	        return null;

	    String fixed = replaceCommonHtmlEntities(name);
	    fixed = QUOTE_LIKE.matcher(fixed).replaceAll("'");
	    return greekEntitiesToNames(fixed);
	}
	

	private static String appendDashIfMissing(String replacement, String original, int endIdx) {
		if (replacement.equals(" "))
			return replacement;

		// Check if the character immediately after the match is already a dash
		boolean alreadyHasDash = (endIdx < original.length() && original.charAt(endIdx) == '-');

		return alreadyHasDash ? replacement : replacement + "-";
	}

	// Quick demo
	public static void main(String[] args) {
		String[] samples = { "dihydro-&alpha;-terpineol", "&alpha;-thujone", 
				"ethyl&nbsp;trans-2,cis-4-decadienoate",
				"&delta;-octalactone", "&gamma;-heptalactone", 
				"menthyl acetate (1&alpha;,2&beta;,5&alpha;)" };
		for (String s : samples) {
			System.out.println(fixName(s));
		}
	}
}

package gov.epa.exp_data_gathering.parse;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
* @author TMARTI02
*/
public class ParameterValue {//Simplified version of hibernate class

	public Parameter parameter=new Parameter();
	public ExpPropUnit unit=new ExpPropUnit();

	public String value_qualifier;
	public Double value_point_estimate;
	public Double value_min;
	public Double value_max;
	public String value_text;
	public Double value_error;
	
	
	public class Parameter {
		public String name;
		public String description;//optional, can set later in database
	}
	
	public class ExpPropUnit {
		public String abbreviation;
		public String name;//should set in Hibernate project
	}
	
	
	public static String getFormattedValue(Double dvalue,int nsig) {

		if(dvalue==null) {
			return "N/A";
		}
		DecimalFormat dfSci=new DecimalFormat("0.00E00");
		DecimalFormat dfInt=new DecimalFormat("0");
		try {
			if(dvalue!=0 && (Math.abs(dvalue)<0.01 || Math.abs(dvalue)>1e3)) {
				return dfSci.format(dvalue);
			}
//			System.out.println(dvalue+"\t"+setSignificantDigits(dvalue, nsig));
			return setSignificantDigits(dvalue, nsig);
		} catch (Exception ex) {
			return null;
		}
	}
	
	public static String setSignificantDigits(double value, int significantDigits) {
	    if (significantDigits < 0) throw new IllegalArgumentException();

	    // this is more precise than simply doing "new BigDecimal(value);"
	    BigDecimal bd = new BigDecimal(value, MathContext.DECIMAL64);
	    bd = bd.round(new MathContext(significantDigits, RoundingMode.HALF_UP));
	    final int precision = bd.precision();
	    if (precision < significantDigits)
	    bd = bd.setScale(bd.scale() + (significantDigits-precision));
	    return bd.toPlainString();
	}    

	/**
	 * Method to display parameterValue in the ExperimentalRecord spreadsheets
	 */
	@Override
	public String toString() {
		
		int n=3;
		String pointEstimate=getFormattedValue(value_point_estimate,n);
		String strValMin=getFormattedValue(value_min,n);
		String strValMax=getFormattedValue(value_max,n);

		if(value_point_estimate!=null) {
			if(value_qualifier!=null) {
				return value_qualifier+" "+pointEstimate+" "+unit.abbreviation;
			} else {
				return pointEstimate+" "+unit.abbreviation;
			}
		} else if (value_min!=null && value_max!=null) {
			return strValMin+ " "+unit.abbreviation+" < value < " +strValMax+ " "+unit.abbreviation;
		} else if (value_min!=null) {
			return " > "+strValMin+" "+unit.abbreviation;
		} else if (value_max!=null) {
			return " < "+strValMax+" "+unit.abbreviation;	
		} else if (value_text!=null) {
			return value_text;
		} else {
			return null;
		}
	}

	
	public String toStringNoUnits() {
		
		int n=3;
		String pointEstimate=getFormattedValue(value_point_estimate,n);
		String strValMin=getFormattedValue(value_min,n);
		String strValMax=getFormattedValue(value_max,n);

		if(value_point_estimate!=null) {
			if(value_qualifier!=null) {
				return value_qualifier+" "+pointEstimate;
			} else {
				return pointEstimate;
			}
		} else if (value_min!=null && value_max!=null) {
			return strValMin+ " - " +strValMax;
		} else if (value_min!=null) {
			return " > "+strValMin;
		} else if (value_max!=null) {
			return " < "+strValMax;	
		} else {
			return null;
		}
	}

}


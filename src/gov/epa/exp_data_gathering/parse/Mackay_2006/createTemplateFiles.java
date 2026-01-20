package gov.epa.exp_data_gathering.parse.Mackay_2006;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;

/**
* @author TMARTI02
*/
public class createTemplateFiles {

	public static void main(String[] args) {
		
		String fileName="Mackay_2006.xlsx";
		String sourceName="Mackay_2006";
		
		ExcelSourceReader esr=new ExcelSourceReader(fileName, "data/experimental", sourceName, sourceName);
		esr.headerRowNum=0;
		
		esr.createClassTemplateFiles(null);

	}

}

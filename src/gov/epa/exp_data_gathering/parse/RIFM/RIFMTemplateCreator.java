package gov.epa.exp_data_gathering.parse.RIFM;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;

/**
* @author TMARTI02
*/
public class RIFMTemplateCreator {

	
	public static void main(String[] args) {

		int headerRowNum=2;
		String sourceName="RIFM";
		String folder="data/experimental/"+sourceName+"/excel files";
		File Folder=new File(folder);

		List<String>sheetNames=Arrays.asList("Jan2023-updated version");

		ExcelSourceReader esr=new ExcelSourceReader();
		esr.sourceName=sourceName;
		List<String>headers=esr.getAllHeadersFromExcelFilesInFolder(sheetNames,headerRowNum, Folder);
		esr.createClassTemplateFiles(headers);

	}
	
}

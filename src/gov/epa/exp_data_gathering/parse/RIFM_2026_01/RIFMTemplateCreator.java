package gov.epa.exp_data_gathering.parse.RIFM_2026_01;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;

/**
* @author TMARTI02
*/
public class RIFMTemplateCreator {

	
	public static void main(String[] args) {

		int headerRowNum=0;
		String sourceName="RIFM_2026_01";
		String folder="data/experimental/"+sourceName+"/excel files";
		File Folder=new File(folder);
		
		
		System.out.println(Folder.exists());

		List<String>sheetNames=Arrays.asList("Sheet1");

		ExcelSourceReader esr=new ExcelSourceReader();
		esr.sourceName=sourceName;
		List<String>headers=esr.getAllHeadersFromExcelFilesInFolder(sheetNames,headerRowNum, Folder);
		esr.createClassTemplateFiles(headers);

	}
	
}

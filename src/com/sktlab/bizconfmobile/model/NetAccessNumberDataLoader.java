package com.sktlab.bizconfmobile.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import android.os.Environment;

import com.sktlab.bizconfmobile.R;
import com.sktlab.bizconfmobile.activity.AppClass;
import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.parser.AccessNumNetParser;
import com.sktlab.bizconfmobile.util.FileUtil;
import com.sktlab.bizconfmobile.util.Util;

public class NetAccessNumberDataLoader implements DataLoader {
	
	private int bridgeId;
	private String confCode;
	
	public NetAccessNumberDataLoader(int bridgeInfo, String confCode) {
		
		this.bridgeId = bridgeInfo;
		this.confCode = confCode;
	}
	
	@Override
	public List getLoadedData() {
		
		List<AccessNumber> list = null;
		
		try {
			
			AccessNumNetParser parser = new AccessNumNetParser(confCode);
			
			list = parser.parse();
			
			String csvFileName = getCsvFileName(this.bridgeId);
			
			if (FileUtil.isExistSDcard()) {
				
				saveToCSV(list,csvFileName);
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
	}

	public static String getCsvFileName(int bridgeId) {
		
		String csvFileName = "test.csv";		
		String language = AccessNumNetParser.getLanguage();
		
		switch(bridgeId) {
		
		case Constant.BEI_JING_BRIDGE:
			
			csvFileName = Constant.BEI_JING_ACCESS_FILE_NAME + language + Constant.CSV_FILE_SUFFIX;
			break;
			
		case Constant.SHANG_HAI_BRIDGE:
			csvFileName = Constant.SHANG_HAI_ACCESS_FILE_NAME + language + Constant.CSV_FILE_SUFFIX;
			break;
			
		default:
			csvFileName = Constant.SHANG_HAI_ACCESS_FILE_NAME + language + Constant.CSV_FILE_SUFFIX;
			break;
		
		}
		
		return csvFileName;
	}
	
	@Override
	public void showMsg() {

		Util.shortToast(AppClass.getInstance(), R.string.toast_error_conf_code);
	}
	
	public void saveToCSV(List<AccessNumber> list, String fileName) {

		int rowCount = 0;
		FileWriter fw;
		BufferedWriter bfw;

		File saveFile = FileUtil.createFile(FileUtil.CSV_FILE_PATH, fileName, true);
		
		try {

			rowCount = list.size();						
			
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			
			if (rowCount > 0) {
				
				// write CSV file header
				bfw.write("Country" + ',');
				bfw.write("Number" + ',');
				bfw.write("Remark");
				
				//a new line after write the header
				bfw.newLine();
				
				//write data
				for (int i = 0; i < rowCount; i++) {
					
					writeRow(bfw, list.get(i));
					//new line
					bfw.newLine();
				}
			}
			//write buffer to file
			bfw.flush();
			bfw.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public BufferedWriter writeRow(BufferedWriter bfw, AccessNumber accessNumber) throws IOException{
		
		bfw.write(accessNumber.getCountry() + ',');
		bfw.write(accessNumber.getNumber() + ',');
		bfw.write(accessNumber.getNumberType());
		
		return bfw;
	}
}

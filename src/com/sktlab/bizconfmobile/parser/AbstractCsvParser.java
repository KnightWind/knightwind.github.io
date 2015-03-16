package com.sktlab.bizconfmobile.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.sktlab.bizconfmobile.util.CharSetUtil;
import com.sktlab.bizconfmobile.util.Util;

public abstract class AbstractCsvParser {

	public static final String TAG = "AbstractCsvParser";

	private BufferedReader bufferedreader = null;
	private List<String> list = new ArrayList<String>();

	public AbstractCsvParser(InputStream inputStream) throws IOException {

		bufferedreader = new BufferedReader(
				new InputStreamReader(inputStream, CharSetUtil.GBK));

		String stemp;

		while ((stemp = bufferedreader.readLine()) != null) {
			
			//Util.BIZ_CONF_DEBUG(TAG, "stemp: " + stemp);
			list.add(stemp);
		}
	}

	public AbstractCsvParser(String filename) throws IOException {

		bufferedreader = new BufferedReader(new FileReader(filename));

		String stemp;

		while ((stemp = bufferedreader.readLine()) != null) {

			list.add(stemp);
		}
	}

	private List getList(){

		return list;
	}

	protected int getRowNum() {

		return list.size();
	}

	protected int getColNum() {

		if (!list.toString().equals("[]")) {

			if (list.get(0).toString().contains(",")) {
				
				return list.get(0).toString().split(",").length;
			} else if (list.get(0).toString().trim().length() != 0) {
				
				return 1;
			} else {
				
				return 0;
			}
		} else {
			
			return 0;
		}
	}

	protected String getRow(int index) {

		if (this.list.size() != 0){
			
			return (String) list.get(index);
		}else {
			
			return null;
		}		
	}

	protected String getCol(int index) {

		if (this.getColNum() == 0) {
			
			return null;
		}

		StringBuffer scol = new StringBuffer();
		String temp = null;
		int colnum = this.getColNum();

		if (colnum > 1) {
			
			for (Iterator it = list.iterator(); it.hasNext();) {
				
				temp = it.next().toString();
				scol = scol.append(temp.split(",")[index] + ",");
			}
		} else {
			
			for (Iterator it = list.iterator(); it.hasNext();) {
				
				temp = it.next().toString();
				scol = scol.append(temp + ",");
			}
		}
		
		String str = new String(scol.toString());
		str = str.substring(0, str.length() - 1);
		
		return str;
	}

	protected String getString(int row, int col) {

		String temp = null;
		int colnum = this.getColNum();
		
		if (colnum > 1) {
			
			temp = list.get(row).toString().split(",")[col];
		} else if (colnum == 1) {
			
			temp = list.get(row).toString();
		} else {
			
			temp = null;
		}
		
		return temp.replace("\"", "");
	}

	private void closeIO(){
		
		try {
			
			this.bufferedreader.close();
		} catch (Exception e) {
			e.printStackTrace();
			Util.BIZ_CONF_DEBUG(TAG, "close io exception: " + e.getMessage());
		}
	}

	public abstract List parse();
	
	public List getParedObj() {
		
		List parsedObj = parse();
		
		closeIO();
		
		return parsedObj;
	}
}

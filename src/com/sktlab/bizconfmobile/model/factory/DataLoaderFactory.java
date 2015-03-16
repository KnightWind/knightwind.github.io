package com.sktlab.bizconfmobile.model.factory;

import com.sktlab.bizconfmobile.interfaces.DataLoader;
import com.sktlab.bizconfmobile.model.BeijingAccessDataLoader;
import com.sktlab.bizconfmobile.model.Constant;
import com.sktlab.bizconfmobile.model.ErrorConfCodeNoRecord;
import com.sktlab.bizconfmobile.model.ErrorRequestBridgeIdTimeOut;
import com.sktlab.bizconfmobile.model.NetAccessNumberDataLoader;
import com.sktlab.bizconfmobile.model.ShanghaiPlistAccessDataLoader;
import com.sktlab.bizconfmobile.model.ShanghaiCSVAccessDataLoader;

public class DataLoaderFactory {

	private static class factoryHolder {
		
		private static DataLoaderFactory instance
					= new DataLoaderFactory();
	}
	
	private DataLoaderFactory() {
		
	}
	
	public static DataLoaderFactory getInstance() {
		
		return factoryHolder.instance;
	}
	
	public DataLoader createLoaderFromNet(int bridgeInfo,String confCode) {
		
		DataLoader loader = new NetAccessNumberDataLoader(bridgeInfo, confCode);
		
		return loader;
	}
	
	public DataLoader createLoader(int bridgeInfo) {
		
		DataLoader loader = null;
		
		switch (bridgeInfo) {
		
		case Constant.BEI_JING_BRIDGE:
			
			loader = new BeijingAccessDataLoader();
			break;
		
		case Constant.SHANG_HAI_BRIDGE:
			
			//loader = new ShangHaiAccessDataLoader();
			loader = new ShanghaiCSVAccessDataLoader();
			break;
		
		case Constant.ERR_NO_CONF_CODE_RECORD:
			
			loader = new ErrorConfCodeNoRecord();
			break;
			
		case Constant.ERR_REQUEST_BRIDGE_ID_TIME_OUT:
			
			loader = new ErrorRequestBridgeIdTimeOut();
			break;
			
		default:
			
			loader = new ErrorRequestBridgeIdTimeOut();
			break;
		}
		
		return loader;
	}
}

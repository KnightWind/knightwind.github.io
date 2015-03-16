package com.sktlab.bizconfmobile.model.requesthandler;

import com.sktlab.bizconfmobile.model.ConfControl;
import com.sktlab.bizconfmobile.model.manager.CommunicationManager;
import com.sktlab.bizconfmobile.model.manager.ContactManager;

public abstract class RequestHandler {
	
	protected ConfControl confControl = ConfControl.getInstance();
	protected CommunicationManager commManager = CommunicationManager.getInstance();
	protected ContactManager contactManager = ContactManager.getInstance();
	
	protected RequestHandler successor;
	
	public void setSuccessor(RequestHandler handler) {
		
		successor = handler;
	}
	
	public abstract void handleRequest(String request);
}

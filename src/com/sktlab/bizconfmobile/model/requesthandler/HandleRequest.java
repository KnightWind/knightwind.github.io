package com.sktlab.bizconfmobile.model.requesthandler;

public class HandleRequest {
	
	private AccSessionCreatedHanlder accCreatedHandler;
	private AativeConfListHanlder aclHandler;
	private ActiveConfHandler acHandler;
	private AcvSaHanlder acvSaHandler;
	private AssocConfHandler assocConfHandler;
	private BvCreatedHandler bvCreatedHandler;
	private ConfMutedHandler cmHandler;
	private ConnectSuccessHandler csHandler;
	private ErrorRevHandler errorHandler;
	private LiveConfRemovedHanlder lcrHanlder;
	private ConfLockedHandler lockHandler;
	private MutedPartyHandler mutePartyHandler;
	private PartyAddedHandler pAddHandler;
	private PartyAttrRevHanlder pAHandler;
	private PartyAttrAlterHandler pAlterHandler;
	private PartyDeletedHandler pDelHandler;
	private PartyListInConfHandler pListHandler;
	private ConfRecordedHanlder recordHandler;
	private RollCallHanlder rollCallHandler;
	private AcvPartyTalkerHandler pTalkerHandler;
	private DisconnectedWebOperatorHandler disconnWebOpHandler;
	private WebOpChangedHandler webOpChangeHandler;
	private AcvAHanlder acvAHandler;
	private BvBApAddedHandler bvBApAddHandler;
	private BvBActivePasscodeConfListHandler bvBAplHandler;
	private AcvOperatorListHandler acvOlHandler;
	
	private static class Holder {
		
		private static HandleRequest handler = new HandleRequest();
	}
	
	public static HandleRequest getInstance() {
		
		return Holder.handler;
	}
	
	/**
	 * Base constructor work flow:
	 * create ACC ->
	 * create BV  ->
	 * create ACL ->
	 * Active conference ->
	 * Associate conference ->
	 * if out call enable, Add party to conference ->
	 * if out call party added, outcall them
	 * 
	 *	client send msg to server: 0~1~LS.CS~ACC
	 * 	client send msg to server: 0~2~LS.CS~BV
		client send msg to server: 46BE~1~BV.B.ACL
		client send msg to server: 46BD~1~ACC.C.ACTIVATE~1~5211100458~0~0
		client send msg to server: 46BD~2~ACC.C.ASSOC~46C0
		client send msg to server: 0~3~LS.CS~ACV~1~46C0
		client send msg to server: 46C1~1~ACV.SA.ALTER~TalkerUpdatesEnabled~1
		client send msg to server: 46BD~3~ACC.C.A.ALTER~46DF~ConfLevelPasscode~1234
		client send msg to server: 46C1~2~ACV.PL
		client send msg to server: 46BD~3~ACC.P.CREATE~46C0~18202932163~018202932163~1~~~~~~~
		client send msg to server: 46BD~4~ACC.O.AA~46C2
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	private HandleRequest() {

		accCreatedHandler = new AccSessionCreatedHanlder();
		aclHandler = new AativeConfListHanlder();
		acHandler = new ActiveConfHandler();
		acvSaHandler = new AcvSaHanlder();
		assocConfHandler = new AssocConfHandler();
		bvCreatedHandler = new BvCreatedHandler();
		cmHandler = new ConfMutedHandler();
		csHandler = new ConnectSuccessHandler();
		errorHandler = new ErrorRevHandler();
		lcrHanlder = new LiveConfRemovedHanlder();
		lockHandler = new ConfLockedHandler();
		mutePartyHandler = new MutedPartyHandler();
		pAddHandler = new PartyAddedHandler();
		pAHandler = new PartyAttrRevHanlder();
		pAlterHandler = new PartyAttrAlterHandler();
		pDelHandler = new PartyDeletedHandler();
		pListHandler = new PartyListInConfHandler();
		recordHandler = new ConfRecordedHanlder();
		rollCallHandler = new RollCallHanlder();
		pTalkerHandler = new AcvPartyTalkerHandler();
		disconnWebOpHandler = new DisconnectedWebOperatorHandler();
		webOpChangeHandler = new WebOpChangedHandler();
		acvAHandler = new AcvAHanlder();
		bvBApAddHandler = new BvBApAddedHandler();
		bvBAplHandler = new BvBActivePasscodeConfListHandler();
		acvOlHandler = new AcvOperatorListHandler();
		
		//create handle chain
		csHandler.setSuccessor(errorHandler);
		errorHandler.setSuccessor(accCreatedHandler);
		accCreatedHandler.setSuccessor(bvCreatedHandler);
		bvCreatedHandler.setSuccessor(aclHandler);
		aclHandler.setSuccessor(acHandler);
		acHandler.setSuccessor(bvBApAddHandler);
		bvBApAddHandler.setSuccessor(bvBAplHandler);
		bvBAplHandler.setSuccessor(assocConfHandler);
		assocConfHandler.setSuccessor(acvSaHandler);
		acvSaHandler.setSuccessor(disconnWebOpHandler);	
		disconnWebOpHandler.setSuccessor(webOpChangeHandler);
		webOpChangeHandler.setSuccessor(acvAHandler);	
		acvAHandler.setSuccessor(pListHandler);		
		pListHandler.setSuccessor(pTalkerHandler);
		pTalkerHandler.setSuccessor(pAddHandler);
		pAddHandler.setSuccessor(pAHandler);
		pAHandler.setSuccessor(pDelHandler);
		pDelHandler.setSuccessor(rollCallHandler);
		rollCallHandler.setSuccessor(recordHandler);
		recordHandler.setSuccessor(lockHandler);
		lockHandler.setSuccessor(cmHandler);
		cmHandler.setSuccessor(mutePartyHandler);
		mutePartyHandler.setSuccessor(pAlterHandler);
		pAlterHandler.setSuccessor(lcrHanlder);
		lcrHanlder.setSuccessor(acvOlHandler);
	}
	
	public void handle(String request) {
		
		csHandler.handleRequest(request);
	}
}

package com.sktlab.bizconfmobile.interfaces;

import com.sktlab.bizconfmobile.model.Participant;

import android.app.Activity;

public interface IConfControl {
	
	public void startConf(Activity activity, ILoadingDialogCallback callback);
	public void hfControl();
	public void selfMute();
	public void allMute(int muteState, int muteParticipant);
	public void muteParty(Participant party, int state, int playMessage);
	public void rollCall();	
	public void record(int state);
	public void addPartyToConf(Participant party, boolean isModerator);
	public void addPartyToConf(Participant party, boolean isModerator, String confId);
	public void lockConf(int state);
	public void otherFunc();
	public void disconnectParty(Participant party);
}

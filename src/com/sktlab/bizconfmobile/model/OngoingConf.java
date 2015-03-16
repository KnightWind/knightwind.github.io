package com.sktlab.bizconfmobile.model;

public class OngoingConf {
	private static final Object m_lockStateLock = new Object();
	private static final Object m_muteStateLock = new Object();
	private static final Object m_recordingStateLock = new Object();
	protected volatile boolean m_audioStatus = false;
	protected volatile boolean m_isBillingEnabled = false;
	protected volatile boolean m_isDialOutEnabled = false;
	protected volatile boolean m_isLockStateChanging = false;
	protected volatile boolean m_isLocked = false;
	private volatile boolean m_isMusicOnHoldEnabled = false;
	protected volatile boolean m_isMuteStateChanging = false;
	protected volatile boolean m_isMuted = false;
	protected volatile boolean m_isRecording = false;
	protected volatile boolean m_isRecordingEnabled = false;
	protected volatile boolean m_isRecordingStateChanging = false;
	protected volatile boolean m_isSecurityCodeEnabled = false;

	private ConferenceAttr mAttr;

	public OngoingConf() {

		setAttr(new ConferenceAttr());
	}

	public boolean audioStatus() {
		return this.m_audioStatus;
	}

	public boolean isBillingEnabled() {
		return this.m_isBillingEnabled;
	}

	public boolean isDialoutEnabled() {
		return this.m_isDialOutEnabled;
	}

	public boolean isLockStateChanging() {
		synchronized (m_lockStateLock) {
			boolean bool = this.m_isLockStateChanging;
			return bool;
		}
	}

	public boolean isLocked() {
		synchronized (m_lockStateLock) {
			boolean bool = this.m_isLocked;
			return bool;
		}
	}

	public boolean isMusicOnHoldEnabled() {
		return this.m_isMusicOnHoldEnabled;
	}

	public boolean isMuteStateChanging() {
		synchronized (m_muteStateLock) {
			boolean bool = this.m_isMuteStateChanging;
			return bool;
		}
	}

	public boolean isMuted() {
		synchronized (m_muteStateLock) {
			boolean bool = this.m_isMuted;
			return bool;
		}
	}

	public boolean isRecording() {
		synchronized (m_recordingStateLock) {
			boolean bool = this.m_isRecording;
			return bool;
		}
	}

	public boolean isRecordingEnabled() {
		return this.m_isRecordingEnabled;
	}

	public boolean isRecordingStateChanging() {
		synchronized (m_recordingStateLock) {
			boolean bool = this.m_isRecordingStateChanging;
			return bool;
		}
	}

	public boolean isSecurityCodeEnabled() {
		return this.m_isSecurityCodeEnabled;
	}

	public void setAudioStatus(boolean paramBoolean) {
		this.m_audioStatus = paramBoolean;
	}

	public void setIsBillingEnabled(boolean paramBoolean) {
		this.m_isBillingEnabled = paramBoolean;
	}

	public void setIsDialOutEnabled(boolean paramBoolean) {
		this.m_isDialOutEnabled = paramBoolean;
	}

	public void setIsMusicOnHoldEnabled(boolean paramBoolean) {
		this.m_isMusicOnHoldEnabled = paramBoolean;
	}

	public void setIsRecordingEnabled(boolean paramBoolean) {
		this.m_isRecordingEnabled = paramBoolean;
	}

	public void setIsSecurityCodeEnabled(boolean paramBoolean) {
		this.m_isSecurityCodeEnabled = paramBoolean;
	}

	public void setLockStateChanging(boolean paramBoolean) {
		synchronized (m_lockStateLock) {
			this.m_isLockStateChanging = paramBoolean;
			return;
		}
	}

	public void setLocked(boolean paramBoolean) {
		synchronized (m_lockStateLock) {
			setLockStateChanging(false);
			this.m_isLocked = paramBoolean;
			return;
		}
	}

	public void setMuteStateChanging(boolean paramBoolean) {
		synchronized (m_muteStateLock) {
			this.m_isMuteStateChanging = paramBoolean;
			return;
		}
	}

	public void setMuted(boolean paramBoolean) {
		synchronized (m_muteStateLock) {
			setMuteStateChanging(false);
			this.m_isMuted = paramBoolean;
			return;
		}
	}

	public void setRecording(boolean paramBoolean) {
		synchronized (m_recordingStateLock) {
			setRecordingStateChanging(false);
			this.m_isRecording = paramBoolean;
			return;
		}
	}

	public void setRecordingStateChanging(boolean paramBoolean) {
		synchronized (m_recordingStateLock) {
			this.m_isRecordingStateChanging = paramBoolean;
			return;
		}
	}

	public ConferenceAttr getAttr() {
		return mAttr;
	}

	public void setAttr(ConferenceAttr mAttr) {
		this.mAttr = mAttr;
	}
}

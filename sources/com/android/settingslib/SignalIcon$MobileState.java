package com.android.settingslib;

import java.util.Objects;

public class SignalIcon$MobileState extends SignalIcon$State {
    public boolean airplaneMode;
    public int callState;
    public boolean carrierNetworkChangeMode;
    public String cellBroadcastMessage = "";
    public boolean configChange;
    public boolean dataConnected;
    public int dataNetType = -1;
    public boolean dataRoamingEnabled;
    public boolean dataSim;
    public boolean defaultDataOff;
    public boolean isCellBroadcastEnabled = false;
    public boolean isDefault;
    public boolean isDualSignal;
    public boolean isEmergency;
    public boolean isFemtoCell;
    public int maxLevel = 4;
    public String networkName;
    public String networkNameData;
    public int nrDataIconType = -1;
    public int rejectCode = 0;
    public boolean roaming;
    public String shortFormLabel = "";
    public int slotId = -1;
    public int subId = -1;
    public int uiccCardState = 0;
    public boolean userSetup;
    public boolean wifiConnected;
    public boolean wifiEnabled;

    public void copyFrom(SignalIcon$State signalIcon$State) {
        super.copyFrom(signalIcon$State);
        SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) signalIcon$State;
        this.dataSim = signalIcon$MobileState.dataSim;
        this.networkName = signalIcon$MobileState.networkName;
        this.networkNameData = signalIcon$MobileState.networkNameData;
        this.dataConnected = signalIcon$MobileState.dataConnected;
        this.isDefault = signalIcon$MobileState.isDefault;
        this.isEmergency = signalIcon$MobileState.isEmergency;
        this.airplaneMode = signalIcon$MobileState.airplaneMode;
        this.carrierNetworkChangeMode = signalIcon$MobileState.carrierNetworkChangeMode;
        this.userSetup = signalIcon$MobileState.userSetup;
        this.roaming = signalIcon$MobileState.roaming;
        this.dataRoamingEnabled = signalIcon$MobileState.dataRoamingEnabled;
        this.defaultDataOff = signalIcon$MobileState.defaultDataOff;
        this.callState = signalIcon$MobileState.callState;
        this.isFemtoCell = signalIcon$MobileState.isFemtoCell;
        this.wifiEnabled = signalIcon$MobileState.wifiEnabled;
        this.wifiConnected = signalIcon$MobileState.wifiConnected;
        this.shortFormLabel = signalIcon$MobileState.shortFormLabel;
        this.isCellBroadcastEnabled = signalIcon$MobileState.isCellBroadcastEnabled;
        this.cellBroadcastMessage = signalIcon$MobileState.cellBroadcastMessage;
        this.nrDataIconType = signalIcon$MobileState.nrDataIconType;
        this.isDualSignal = signalIcon$MobileState.isDualSignal;
        this.maxLevel = signalIcon$MobileState.maxLevel;
        this.dataNetType = signalIcon$MobileState.dataNetType;
        this.uiccCardState = signalIcon$MobileState.uiccCardState;
        this.rejectCode = signalIcon$MobileState.rejectCode;
        this.slotId = signalIcon$MobileState.slotId;
        this.subId = signalIcon$MobileState.subId;
        this.configChange = signalIcon$MobileState.configChange;
    }

    /* access modifiers changed from: protected */
    public void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(',');
        sb.append("slotId=");
        sb.append(this.slotId);
        sb.append(',');
        sb.append("subId=");
        sb.append(this.subId);
        sb.append(',');
        sb.append("dataSim=");
        sb.append(this.dataSim);
        sb.append(',');
        sb.append("networkName=");
        sb.append(this.networkName);
        sb.append(',');
        sb.append("networkNameData=");
        sb.append(this.networkNameData);
        sb.append(',');
        sb.append("dataConnected=");
        sb.append(this.dataConnected);
        sb.append(',');
        sb.append("dataNetType=");
        sb.append(this.dataNetType);
        sb.append(',');
        sb.append("roaming=");
        sb.append(this.roaming);
        sb.append(',');
        sb.append("dataRoamingEnabled=");
        sb.append(this.dataRoamingEnabled);
        sb.append(',');
        sb.append("isDefault=");
        sb.append(this.isDefault);
        sb.append(',');
        sb.append("isEmergency=");
        sb.append(this.isEmergency);
        sb.append(',');
        sb.append("airplaneMode=");
        sb.append(this.airplaneMode);
        sb.append(',');
        sb.append("carrierNetworkChangeMode=");
        sb.append(this.carrierNetworkChangeMode);
        sb.append(',');
        sb.append("userSetup=");
        sb.append(this.userSetup);
        sb.append(',');
        sb.append("defaultDataOff=");
        sb.append(this.defaultDataOff);
        sb.append(',');
        sb.append("uiccCardState =");
        sb.append(this.uiccCardState);
        sb.append("callState=");
        sb.append(this.callState);
        sb.append(',');
        sb.append("isFemtoCell=");
        sb.append(this.isFemtoCell);
        sb.append(',');
        sb.append("wifiEnabled=");
        sb.append(this.wifiEnabled);
        sb.append(',');
        sb.append("wifiConnected=");
        sb.append(this.wifiConnected);
        sb.append(',');
        sb.append("isDualSignal=");
        sb.append(this.isDualSignal);
        sb.append(',');
        sb.append("maxLevel=");
        sb.append(this.maxLevel);
        sb.append(',');
        sb.append("isCellBroadcastEnabled=");
        sb.append(this.isCellBroadcastEnabled);
        sb.append(',');
        sb.append("cellBroadcastMessage=");
        sb.append(this.cellBroadcastMessage);
        sb.append("rejectCode =");
        sb.append(this.rejectCode);
        sb.append("configChange =");
        sb.append(this.configChange);
        sb.append(",nrDataIconType=");
        sb.append(this.nrDataIconType);
    }

    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            SignalIcon$MobileState signalIcon$MobileState = (SignalIcon$MobileState) obj;
            return Objects.equals(signalIcon$MobileState.networkName, this.networkName) && Objects.equals(signalIcon$MobileState.networkNameData, this.networkNameData) && signalIcon$MobileState.slotId == this.slotId && signalIcon$MobileState.subId == this.subId && signalIcon$MobileState.dataSim == this.dataSim && signalIcon$MobileState.dataConnected == this.dataConnected && signalIcon$MobileState.dataNetType == this.dataNetType && signalIcon$MobileState.isEmergency == this.isEmergency && signalIcon$MobileState.airplaneMode == this.airplaneMode && signalIcon$MobileState.carrierNetworkChangeMode == this.carrierNetworkChangeMode && signalIcon$MobileState.userSetup == this.userSetup && signalIcon$MobileState.isDefault == this.isDefault && signalIcon$MobileState.roaming == this.roaming && signalIcon$MobileState.dataRoamingEnabled == this.dataRoamingEnabled && signalIcon$MobileState.defaultDataOff == this.defaultDataOff && signalIcon$MobileState.uiccCardState == this.uiccCardState && signalIcon$MobileState.rejectCode == this.rejectCode && signalIcon$MobileState.isDualSignal == this.isDualSignal && signalIcon$MobileState.maxLevel == this.maxLevel && signalIcon$MobileState.callState == this.callState && signalIcon$MobileState.isFemtoCell == this.isFemtoCell && signalIcon$MobileState.wifiEnabled == this.wifiEnabled && signalIcon$MobileState.wifiConnected == this.wifiConnected && signalIcon$MobileState.nrDataIconType == this.nrDataIconType && Objects.equals(signalIcon$MobileState.shortFormLabel, this.shortFormLabel) && signalIcon$MobileState.isCellBroadcastEnabled == this.isCellBroadcastEnabled && Objects.equals(signalIcon$MobileState.cellBroadcastMessage, this.cellBroadcastMessage) && signalIcon$MobileState.configChange == this.configChange;
        }
    }
}

package com.ibamb.dnet.module.constants;

public class Control {

    //The command type is list below:
    public static final int GET_PARAMETERS = 0xC1;
    public static final int SET_PARAMETERS = 0xC3;
    public static final int AUTH_USER = 0xC5;
    public static final int REBOOT_SYSTEM = 0xC3;
    public static final int LOGOUT = 0xC9;
    public static final int UDPDATE_FIRMWARE = 0x8B;
    public static final int SEARCH_DEVICE = 0x81;

    //The response type is list below:
    public static final byte NO_DATA_REPLY = -1;
    public static final byte ACKNOWLEDGE = 0x00;
    public static final byte NO_PERMISSION = 0x02;
    public static final byte OPTION_NOT_SUPPORT = 0x04;
    public static final byte VALUE_INVALID = 0x06;
    public static final byte AUTH_FAIL = 0x08;

    public static final byte OTHER_ERR = -2;
}

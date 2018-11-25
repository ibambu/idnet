package com.ibamb.dnet.module.instruct;

import com.ibamb.dnet.module.beans.DeviceBaseInfo;
import com.ibamb.dnet.module.beans.DeviceParameter;
import com.ibamb.dnet.module.beans.ParameterItem;
import com.ibamb.dnet.module.core.SpecialParams;
import com.ibamb.dnet.module.instruct.beans.Parameter;

import java.util.ArrayList;
import java.util.List;

public class DeviceBaseInfoManager {

    public static final int MAX_SUPPORT_UART = 32;

    public static DeviceBaseInfo getDeviceBaseInfo(String mac) {
        DeviceParameter deviceParameter = new DeviceParameter(mac, null, null);
        List<ParameterItem> items = new ArrayList<>();
        Parameter parameter = SpecialParams.getInstance().getParameter(SpecialParams.TypeId.SYS_DEVICE_WORK_MODE.name());
        items.add(new ParameterItem(parameter.getId(), null));
        deviceParameter.setParamItems(items);
        IParamReader reader = new ParamReader();
        reader.readDeviceParam(deviceParameter);

        DeviceBaseInfo deviceBaseInfo = new DeviceBaseInfo();
        deviceBaseInfo.setComBaseInfoList(new ArrayList<>());

        ParameterItem parameterItem = deviceParameter.getParamItems().get(0);
        if (parameterItem != null && parameterItem.getByteValue() != null && parameterItem.getByteValue().length==parameter.getByteLength()) {
            byte[] byteData = parameterItem.getByteValue();

            boolean com1Eanbled = (byteData[0] & 0x01) == 0x01;
            boolean com1CanDNS = (byteData[2] & 0x04) == 0x04;
            DeviceBaseInfo.ComBaseInfo comBaseInfo1 = new DeviceBaseInfo.ComBaseInfo();
            comBaseInfo1.setComId(1);
            comBaseInfo1.setComEabled(com1Eanbled);
            comBaseInfo1.setCanDNS(com1CanDNS);
            deviceBaseInfo.getComBaseInfoList().add(comBaseInfo1);

            boolean com2Eabled = (byteData[0] & 0x10) == 0x10;
            boolean com2CanDNS = (byteData[2] & 0x08) == 0x08;

            DeviceBaseInfo.ComBaseInfo comBaseInfo2 = new DeviceBaseInfo.ComBaseInfo();
            comBaseInfo2.setComId(2);
            comBaseInfo2.setComEabled(com2Eabled);
            comBaseInfo2.setCanDNS(com2CanDNS);
            deviceBaseInfo.getComBaseInfoList().add(comBaseInfo2);

            boolean com3Eabled = (byteData[3] & 0x01) == 0x01;
            boolean com3CanDNS = (byteData[3] & 0x20) == 0x20;

            DeviceBaseInfo.ComBaseInfo comBaseInfo3 = new DeviceBaseInfo.ComBaseInfo();
            comBaseInfo3.setComId(3);
            comBaseInfo3.setComEabled(com3Eabled);
            comBaseInfo3.setCanDNS(com3CanDNS);
            deviceBaseInfo.getComBaseInfoList().add(comBaseInfo3);

            for (int i = 2; i < MAX_SUPPORT_UART; i++) {
                boolean comEnabled = (byteData[i + 2] & 0x01) == 0x01;
                boolean comCanDNS = (byteData[i + 2] & 0x20) == 0x20;
                DeviceBaseInfo.ComBaseInfo comBaseInfo = new DeviceBaseInfo.ComBaseInfo();
                comBaseInfo.setComId(i+2);
                comBaseInfo.setComEabled(comEnabled);
                comBaseInfo.setCanDNS(comCanDNS);
                deviceBaseInfo.getComBaseInfoList().add(comBaseInfo);
            }
        }

        return deviceBaseInfo;
    }
}

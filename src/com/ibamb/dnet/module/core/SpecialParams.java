package com.ibamb.dnet.module.core;

import com.ibamb.dnet.module.instruct.beans.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpecialParams {
    private static SpecialParams ourInstance;


    private static Map<String, Parameter> parameterMap;

    public enum TypeId {
        BASIC_AT_ENTER_METHOD, SYS_DEVICE_WORK_MODE, SYS_PRODUCTION_NAME, SYS_FIRMWARE_VER
    }

    public Parameter getParameter(String paramId) {
        return parameterMap.get(paramId);
    }

    public synchronized static SpecialParams getInstance() {
        if (ourInstance == null) {
            ourInstance = new SpecialParams();
        }
        return ourInstance;
    }

    private SpecialParams() {
        if (parameterMap == null) {
            parameterMap = new HashMap<>();
            //BASIC_AT_ENTER_METHOD
            Parameter basicAtEnterMethod = new Parameter();
            basicAtEnterMethod.setParamType(0);//参数类型
            basicAtEnterMethod.setChannelId(0);//通道ID
            basicAtEnterMethod.setId(TypeId.BASIC_AT_ENTER_METHOD.name());//字符参数ID
            basicAtEnterMethod.setViewTagId(TypeId.BASIC_AT_ENTER_METHOD.name());//显示参数的界面控件ID
            basicAtEnterMethod.setElementType(0);//界面控件类型
            basicAtEnterMethod.setDecId(193);//参数对应的十进制ID
            basicAtEnterMethod.setHexId(0xc1);//参数对应的十六进制ID
            basicAtEnterMethod.setCovertType(1);//参数转换类型
            basicAtEnterMethod.setByteLength(1);//参数所占长度
            basicAtEnterMethod.setValueMappings(new ArrayList<>());
            parameterMap.put(basicAtEnterMethod.getId(), basicAtEnterMethod);
            ParameterMapping.getInstance().addParameter(basicAtEnterMethod);

            //SYS_DEVICE_WORK_MODE
            Parameter deviceWorkMode = new Parameter();
            deviceWorkMode.setParamType(0);//参数类型
            deviceWorkMode.setChannelId(0);//通道ID
            deviceWorkMode.setId(TypeId.SYS_DEVICE_WORK_MODE.name());//字符参数ID
            deviceWorkMode.setViewTagId(TypeId.SYS_DEVICE_WORK_MODE.name());//显示参数的界面控件ID
            deviceWorkMode.setElementType(0);//界面控件类型
            deviceWorkMode.setDecId(353);//参数对应的十进制ID
            deviceWorkMode.setHexId(0x161);//参数对应的十六进制ID
            deviceWorkMode.setCovertType(4);//参数转换类型 字符型
            deviceWorkMode.setByteLength(34);//参数所占长度
            deviceWorkMode.setValueMappings(new ArrayList<>());
            parameterMap.put(deviceWorkMode.getId(), deviceWorkMode);
            ParameterMapping.getInstance().addParameter(deviceWorkMode);

            //SYS_PRODUCTION_NAME
            Parameter productName = new Parameter();
            productName.setParamType(0);//参数类型
            productName.setChannelId(0);//通道ID
            productName.setId(TypeId.SYS_PRODUCTION_NAME.name());//字符参数ID
            productName.setViewTagId(TypeId.SYS_PRODUCTION_NAME.name());//显示参数的界面控件ID
            productName.setElementType(0);//界面控件类型
            productName.setDecId(7);//参数对应的十进制ID
            productName.setHexId(0x07);//参数对应的十六进制ID
            productName.setCovertType(4);//参数转换类型 字符型
            productName.setByteLength(20);//参数所占长度
            productName.setValueMappings(new ArrayList<>());
            parameterMap.put(productName.getId(), productName);
            ParameterMapping.getInstance().addParameter(productName);

            //SYS_FIRMWARE_VER
            Parameter firmWareVersion = new Parameter();
            firmWareVersion.setParamType(0);//参数类型
            firmWareVersion.setChannelId(0);//通道ID
            firmWareVersion.setId(TypeId.SYS_FIRMWARE_VER.name());//字符参数ID
            firmWareVersion.setViewTagId(TypeId.SYS_FIRMWARE_VER.name());//显示参数的界面控件ID
            firmWareVersion.setElementType(0);//界面控件类型
            firmWareVersion.setDecId(0);//参数对应的十进制ID
            firmWareVersion.setHexId(0x00);//参数对应的十六进制ID
            firmWareVersion.setCovertType(4);//参数转换类型 字符型
            firmWareVersion.setByteLength(20);//参数所占长度
            firmWareVersion.setValueMappings(new ArrayList<>());
            parameterMap.put(firmWareVersion.getId(), firmWareVersion);
            ParameterMapping.getInstance().addParameter(firmWareVersion);
        }
    }
}

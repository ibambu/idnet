package com.ibamb.dnet.module.sync;

import com.ibamb.dnet.module.beans.DeviceParameter;
import com.ibamb.dnet.module.beans.ParameterItem;
import com.ibamb.dnet.module.instruct.IParamWriter;
import com.ibamb.dnet.module.instruct.ParamWriter;

import java.util.List;

public class DeviceParamSynchronize {
    /**
     * 同步设备参数值，返回目标设备所有参数最新值。
     * @param srcDeviceParameter
     * @param distDeviceParameter
     * @return
     */
    public void syncDeviceChannelParam(DeviceParameter srcDeviceParameter, DeviceParameter distDeviceParameter) {
        try {
            List<ParameterItem> srcParamItems = srcDeviceParameter.getParamItems();
            List<ParameterItem> distParamItems = distDeviceParameter.getParamItems();
            copyParamValue(srcParamItems,distParamItems);
            IParamWriter writer = new ParamWriter();
            writer.writeDeviceParam(distDeviceParameter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制参数值
     *
     * @param srcParamList
     * @param distParamList
     */
    private void copyParamValue(List<ParameterItem> srcParamList, List<ParameterItem> distParamList) {
        for (ParameterItem srcParamItem : srcParamList) {
            for (ParameterItem distParamItem : distParamList) {
                if (srcParamItem.getParamId().equals(distParamItem.getParamId())) {
                    distParamItem.setParamValue(srcParamItem.getParamValue());
                    break;
                }
            }
        }
    }
}

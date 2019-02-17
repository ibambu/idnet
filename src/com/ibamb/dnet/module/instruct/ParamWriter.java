package com.ibamb.dnet.module.instruct;

import com.ibamb.dnet.module.beans.DeviceParameter;
import com.ibamb.dnet.module.beans.ParameterItem;
import com.ibamb.dnet.module.constants.Constants;
import com.ibamb.dnet.module.constants.Control;
import com.ibamb.dnet.module.core.ContextData;
import com.ibamb.dnet.module.core.ParameterMapping;
import com.ibamb.dnet.module.instruct.beans.Information;
import com.ibamb.dnet.module.instruct.beans.InstructFrame;
import com.ibamb.dnet.module.instruct.beans.Parameter;
import com.ibamb.dnet.module.instruct.beans.ReplyFrame;
import com.ibamb.dnet.module.net.UDPMessageSender;

import java.util.ArrayList;
import java.util.List;



public class ParamWriter implements IParamWriter {
    @Override
    public DeviceParameter writeDeviceParam(DeviceParameter deviceParameter) {
        deviceParameter = sendStructure(deviceParameter);
        return deviceParameter;
    }

    /**
     * 发送读/写参数指令
     *
     * @param deviceParameter
     * @return
     */
    private DeviceParameter sendStructure(DeviceParameter deviceParameter) {
        try {
            //根据传入的读/写标志设置控制位。
            UDPMessageSender sender = new UDPMessageSender();
            List<ParameterItem> parameterItems = deviceParameter.getParamItems();
            //主帧固定结构长度
            int mainStructLen = Constants.UDM_CONTROL_LENGTH
                    + Constants.UDM_ID_LENGTH
                    + Constants.UDM_MAIN_FRAME_LENGTH
                    + Constants.UDM_IP_LENGTH
                    + Constants.UDM_MAC_LENGTH;
            //子帧固定结构长度
            int subStructLen = Constants.UDM_TYPE_LENGTH
                    + Constants.UDM_SUB_FRAME_LENGTH;
            List<Information> informationList = new ArrayList<>();//存放本次读/写的所有参数

            //返回帧固定结构长度
            int replyMainStructLen = Constants.UDM_CONTROL_LENGTH
                    + Constants.UDM_ID_LENGTH
                    + Constants.UDM_MAIN_FRAME_LENGTH;

            //先生成帧对象
            InstructFrame instructFrame = new InstructFrame(Control.SET_PARAMETERS, deviceParameter.getMac());
            instructFrame.setInfoList(informationList);
            instructFrame.setId(ContextData.getInstance().getCommunicationId());

            int sendFrameLength = mainStructLen;//所有子帧总长度
            int replyFrameLength = replyMainStructLen;//期望返回帧的总长度。


            //遍历通道参数，将要读/写参数存入帧对象中。
            for (ParameterItem parameterItem : parameterItems) {
                Parameter param = ParameterMapping.getInstance().getMapping(parameterItem.getParamId());
                String typeId = param.getId();
                Information dataField = new Information(typeId, parameterItem.getParamValue());
                //如果是读取参数值，则参数值长度设置为0. 如果是写参数，则参数值的长度设置约定长度。
                dataField.setLength(subStructLen + param.getByteLength());
                sendFrameLength += dataField.getLength();//增加发送的主帧长度
                replyFrameLength += subStructLen + param.getByteLength();//增加返回帧的长度
                informationList.add(dataField);
            }
            instructFrame.setLength(sendFrameLength);
            IEncoder encoder = new ParamWriteEncoder();
            //生成发送报文
            System.out.print("send:");
            byte[] sendData = encoder.encode(instructFrame, Control.SET_PARAMETERS);
            for(int i=0;i<sendData.length;i++){
                System.out.print(Integer.toHexString(sendData[i])+" ");
            }
            System.out.println();
            //发送报文
            System.out.print("replay:");
            byte[] replyData = sender.sendByUnicast(sendData, replyFrameLength, deviceParameter.getIp());
            for(int i=0;i<replyData.length;i++){
                System.out.print(Integer.toHexString(replyData[i])+" ");
            }
            System.out.println();
            //解析返回报文
            IParser parser = new ReplyFrameParser();
            ReplyFrame replyFrame = parser.parse(replyData);
            /**
             * 对于修改参数值值的时候，2,4字节长度高位和低位位置是反的，
             * 所以对于读取参数只判断返回码是否成功即可，后面再重新读取一次。
             */
            deviceParameter.setResultCode(replyFrame.getControl());
//            if(replyFrame.getId()==instructFrame.getId( ) &&  replyFrame.getControl() == Control.ACKNOWLEDGE ){
//                channelParameter.setSuccessful(true);
//            }else{
//                channelParameter.setSuccessful(false);
//            }
        } catch (Exception e) {
            deviceParameter.setResultCode(Control.OTHER_ERR);
        }
        return deviceParameter;
    }
}

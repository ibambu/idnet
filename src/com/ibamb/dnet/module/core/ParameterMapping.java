package com.ibamb.dnet.module.core;

import com.ibamb.dnet.module.instruct.beans.Parameter;
import com.ibamb.dnet.module.instruct.beans.ValueMapping;
import com.ibamb.dnet.module.security.AESUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class ParameterMapping {
    private static ParameterMapping parameterMapping;
    private static Map<String, Parameter> parameterMap;


    public static void setCustParameterMapping(Map<String, Parameter> cParameterMap){
          parameterMap = cParameterMap;
    }

    public static  synchronized ParameterMapping getInstance(){
        if(parameterMapping==null){
            parameterMapping = new ParameterMapping();
        }
        return parameterMapping;
    }

    private ParameterMapping() {
        parameterMap = loadParameterMapping();
    }

    private Map<String,Parameter> loadParameterMapping(){
        BufferedReader bufferedReader = null;
        Map<String, Parameter> mapping = new HashMap();
        try {
            InputStream in = ParameterMapping.class.getClassLoader().getResourceAsStream("typeid");
            bufferedReader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String readLine = null;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(readLine);
            }

            String content = AESUtil.aesDecrypt(stringBuilder.toString(),"1qaz2wsx3edc4rfv");

            String[] paramArrays = content.split("&&");
            for (String typeLine : paramArrays) {
                String[] dataArray = typeLine.split("#");
                int cellCount = 0;
                Parameter param = new Parameter();
                param.setParamType(Integer.parseInt(dataArray[cellCount++]));//参数类型
                param.setChannelId(Integer.parseInt(dataArray[cellCount++]));//通道ID
                param.setId(dataArray[cellCount++]);//字符参数ID
                param.setViewTagId(dataArray[cellCount++]);//现实参数的界面控件ID
                param.setElementType(Integer.parseInt(dataArray[cellCount++]));//界面控件类型
                param.setDecId(Integer.parseInt(dataArray[cellCount++]));//参数对应的十进制ID
                param.setHexId(Integer.parseInt(dataArray[cellCount++], 16));//参数对应的十六进制ID
                param.setCovertType(Integer.parseInt(dataArray[cellCount++]));//参数转换类型
                param.setByteLength(Integer.parseInt(dataArray[cellCount++]));//参数所占长度
                List<ValueMapping> vMappings = new ArrayList<>();
                param.setValueMappings(vMappings);
                //如果是枚举值，则将枚举值对应的显示值都放入VaueMapping对象中。
                String enumValues = dataArray[cellCount++];
                String displayEnumValues = dataArray[cellCount++];
                if (dataArray.length > 9) {
                    if (enumValues != null && displayEnumValues != null) {
                        String[] values = enumValues.split(",");
                        String[] names = displayEnumValues.split(",");
                        if (values.length == names.length) {
                            for (int i = 0; i < values.length; i++) {
                                if (values[i].equals("NULL")) {
                                    continue;
                                }
                                ValueMapping vmapping = new ValueMapping();
                                vmapping.setValue(values[i]);
                                vmapping.setDisplayValue(names[i]);
                                vMappings.add(vmapping);
                                vmapping.setParamId(param.getId());
                            }
                        }
                    }
                }
                String pubFlag = dataArray[cellCount++];
                boolean isPublic = "1".equals(pubFlag) ? true : false;
                param.setPublic(isPublic);
                mapping.put(param.getId(), param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {

                }
            }
        }
        return mapping;
    }

    public void setParameterMap(Map<String, Parameter> parameterMap) {
        ParameterMapping.parameterMap = parameterMap;
    }

    public List<Parameter> getChannelParamDef(int channelId) {
        List<Parameter> channelParamList = new ArrayList<>();
        for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
            String paramId = it.next();
            Parameter parameter = parameterMap.get(paramId);
            if (parameter.getChannelId() == channelId) {
                channelParamList.add(parameter);
            }
        }
        return channelParamList;
    }

    public List<Parameter> getChannelPublicParam(int channelId) {
        List<Parameter> channelParamList = new ArrayList<>();
        for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
            String paramId = it.next();
            Parameter parameter = parameterMap.get(paramId);
            if (parameter.getChannelId() == channelId && parameter.isPublic()) {
                channelParamList.add(parameter);
            }
        }
        return channelParamList;
    }

    /**
     * 读取支持的通道
     *
     * @return
     */
    public List<String> getSupportedChannels() {
        List<String> supportedChannels = new ArrayList<>();
        for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
            String paramId = it.next();
            Parameter parameter = parameterMap.get(paramId);
            String channelId = String.valueOf(parameter.getChannelId());
            if (!supportedChannels.contains(channelId)) {
                supportedChannels.add(channelId);
            }
        }
        return supportedChannels;
    }

    public Parameter getMapping(String paramId) {
        return parameterMap.get(paramId);
    }

    /**
     * 根据十进制参数ID获取某个参数定义信息。
     *
     * @param decId
     * @return
     */
    public Parameter getMappingByDecId(int decId) {
        Parameter parameter = null;
        for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
            String paramId = it.next();
            Parameter parameter1 = parameterMap.get(paramId);
            if (parameter1.getDecId() == decId) {
                parameter = parameter1;
                break;
            }
        }
        return parameter;
    }

    /**
     * 当通道ID为空时，建议用于获取参数对应的枚举选项值。因为之是根据TAG随机获取一个通道的某个参数枚举值。
     * 理论上各个通道相同类型的参数枚举选项值是一致的。
     *
     * @param tagId
     * @param channelId
     * @return
     */
    public Parameter getMappingByTagId(String tagId, String channelId) {
        Parameter parameter = null;
        for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
            String paramId = it.next();
            Parameter parameter1 = parameterMap.get(paramId);
            if (channelId == null) {
                if (parameter1.getViewTagId().equals(tagId)) {
                    parameter = parameter1;
                    break;
                }
            } else if (parameter1.getViewTagId().equals(tagId)
                    && String.valueOf(parameter1.getChannelId()).equals(channelId)) {
                parameter = parameter1;
                break;
            }
        }
        return parameter;
    }

    /**
     * 用户读写参数时获取某个通道的参数定义信息。
     *
     * @param tagIds
     * @param channelId
     * @return
     */
    public List<Parameter> getMappingByTags(String[] tagIds, String channelId) {
        List<Parameter> paramList = new ArrayList<>();

        for (String tagId : tagIds) {
            for (Iterator<String> it = parameterMap.keySet().iterator(); it.hasNext(); ) {
                String paramId = it.next();
                Parameter parameter1 = parameterMap.get(paramId);
                if (parameter1.getViewTagId().equals(tagId)
                        && String.valueOf(parameter1.getChannelId()).equals(channelId)) {
                    paramList.add(parameter1);
                    break;
                }
            }
        }
        return paramList;
    }
}

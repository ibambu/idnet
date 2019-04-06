package com.ibamb.dnet.module.file;

import com.ibamb.dnet.module.log.UdmLog;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    public static List<ProductVersion> loadProductVersionDescrible(String localfile) {
        BufferedReader bufferedReader = null;
        List<ProductVersion> productVersionList = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localfile), "gbk"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] versionInfos = line.split("\\|");
                if (versionInfos.length < 3) {
                    continue;
                }
                ProductVersion productVersion = new ProductVersion(versionInfos[0], versionInfos[1], versionInfos[2]);
                List<ProductVersion.HistoryVersion> historyVersions = new ArrayList<>();
                productVersion.setHistoryVersions(historyVersions);
                if (versionInfos.length > 3) {
                    for (int i = 3; i < versionInfos.length; i++) {
                        ProductVersion.HistoryVersion historyVersion = new ProductVersion.HistoryVersion();
                        historyVersion.setVersion(versionInfos[i]);
                        historyVersion.setFileName(versionInfos[i + 1]);
                        historyVersions.add(historyVersion);
                    }
                }
                productVersionList.add(productVersion);
            }
        } catch (Exception e) {
            UdmLog.getErrorTrace(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return productVersionList;
    }
}

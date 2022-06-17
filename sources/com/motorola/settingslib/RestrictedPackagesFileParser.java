package com.motorola.settingslib;

import android.text.TextUtils;
import android.util.Log;
import com.motorola.settingslib.RestrictedPackage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class RestrictedPackagesFileParser {
    private static RestrictedPackagesFileParser sInstance;

    RestrictedPackagesFileParser() {
    }

    public static RestrictedPackagesFileParser getInstance() {
        if (sInstance == null) {
            sInstance = new RestrictedPackagesFileParser();
        }
        return sInstance;
    }

    public List<RestrictedPackage> getPackagesFromFile(String str) {
        File[] listFiles;
        ArrayList arrayList = new ArrayList();
        File file = new File(str);
        if (file.exists() && file.isDirectory() && (listFiles = file.listFiles()) != null && listFiles.length != 0) {
            for (File file2 : listFiles) {
                if (!file2.isDirectory()) {
                    try {
                        parseFile(arrayList, new FileInputStream(file2));
                    } catch (FileNotFoundException unused) {
                        Log.e("RestrictedPackagesFileParser", "File not found for : " + file2);
                    }
                }
            }
        }
        return arrayList;
    }

    private void parseFile(List<RestrictedPackage> list, FileInputStream fileInputStream) {
        if (fileInputStream != null) {
            try {
                Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileInputStream).getDocumentElement();
                if (documentElement == null) {
                    return;
                }
                if (documentElement.hasChildNodes()) {
                    NodeList childNodes = documentElement.getChildNodes();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        Node item = childNodes.item(i);
                        if (item != null) {
                            if (item.getNodeType() == 1) {
                                if (item.getNodeName().equalsIgnoreCase("item")) {
                                    parseNode(list, item);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("RestrictedPackagesFileParser", "Error occurred while parsing InputStream", e);
            }
        } else {
            throw new IllegalArgumentException("Invalid InputStream");
        }
    }

    private void parseNode(List<RestrictedPackage> list, Node node) {
        Node namedItem;
        String textContent = node.getTextContent();
        if (!TextUtils.isEmpty(textContent) && !textContent.trim().isEmpty()) {
            RestrictedPackage.Builder builder = new RestrictedPackage.Builder(textContent);
            if (node.hasAttributes() && (namedItem = node.getAttributes().getNamedItem("channels")) != null) {
                String textContent2 = namedItem.getTextContent();
                if (!TextUtils.isEmpty(textContent2) && !textContent2.trim().isEmpty()) {
                    builder.setChannelIds(Arrays.asList(textContent2.split(";")));
                }
            }
            list.add(builder.build());
        }
    }
}

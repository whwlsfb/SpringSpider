package burp.scanner;

import burp.*;
import burp.scanner.sub.APIDoc;
import burp.scanner.sub.SpringActuator;
import burp.utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpringScanner implements IScannerCheck {

    private final String[] STATIC_FILE_EXT = new String[]{
            "png",
            "jpg",
            "jpeg",
            "gif",
            "pdf",
            "bmp",
            "js",
            "css",
            "ico",
            "woff",
            "woff2",
            "ttf",
            "otf",
            "ttc",
            "svg",
            "psd",
            "exe",
            "zip",
            "rar",
            "7z",
            "msi",
            "tar",
            "gz",
            "mp3",
            "mp4",
            "mkv",
            "swf",
            "xls",
            "xlsx",
            "doc",
            "docx",
            "ppt",
            "pptx",
            "iso",
            "map",
            "php",
            "aspx",
            "ashx",
            "asp"
    };

    List<String> scannedUrls = new ArrayList<>();
    List<ISubScanner> subScanners = new ArrayList<ISubScanner>() {{
        add(new APIDoc());
        add(new SpringActuator());
    }};

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        URL originUrl = cleanURL(Utils.Helpers.analyzeRequest(baseRequestResponse).getUrl());
        List<IScanIssue> result = new ArrayList<>();
        URL[] urls = Utils.splitUrls(originUrl);
        for (URL url : urls) {
            if (!isChecked(url.toString()))
                for (ISubScanner subScanner : subScanners) {
                    result.addAll(subScanner.check(url, baseRequestResponse));
                }
        }
        return result;
    }

    public URL cleanURL(URL originUrl) {
        String baseUrl = originUrl.getProtocol() + "://" + originUrl.getAuthority() + "/";
        String path = originUrl.getPath();
        if (isStaticFile(originUrl)) {
            path = path.substring(1, path.lastIndexOf("/"));
        } try {
            return new URL(baseUrl + path);
        } catch (Exception ex) {
            return originUrl;
        }
    }

    public boolean isChecked(String url) {
        String urlMd5 = Utils.MD5(url);
        synchronized (scannedUrls) {
            if (scannedUrls.contains(urlMd5)) {
                return true;
            } else {
                scannedUrls.add(urlMd5);
                return false;
            }
        }
    }

    private boolean isStaticFile(URL url) {
        return Arrays.stream(STATIC_FILE_EXT).anyMatch(s -> s.equalsIgnoreCase(Utils.getUrlFileExt(url.toString())));
    }

    @Override
    public List<IScanIssue> doActiveScan(IHttpRequestResponse baseRequestResponse, IScannerInsertionPoint insertionPoint) {
        return null;
    }

    @Override
    public int consolidateDuplicateIssues(IScanIssue existingIssue, IScanIssue newIssue) {
        return 0;
    }
}

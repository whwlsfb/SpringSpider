package burp.scanner.sub;

import burp.*;
import burp.scanner.IResponseChecker;
import burp.scanner.ISubScanner;
import burp.scanner.Payload;
import burp.utils.BypassPayloadUtils;
import burp.utils.ConfigUtils;
import burp.utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class APIDoc implements ISubScanner {
    private final List<Payload> payloads = new ArrayList<Payload>() {{
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"swagger-ui.html"});
        }}, (baseRequestResponse, checkRequest, newUrl) -> {
            IResponseKeywords founds = Utils.Helpers.analyzeResponseKeywords(new ArrayList<String>() {{
                add("Swagger UI");
                add("swagger-ui/lib");
            }}, checkRequest.getResponse());
            if (BypassPayloadUtils.hasFound(founds, 0) && Utils.Helpers.analyzeResponse(checkRequest.getResponse()).getStatusCode() == 200) {
                Utils.Callback.printOutput("found " + newUrl + ".\r\n");
                return new Issue(
                        baseRequestResponse.getHttpService(),
                        newUrl,
                        new IHttpRequestResponse[]{checkRequest},
                        "Swagger UI found.",
                        "URL: " + newUrl,
                        "Medium", true);
            } else {
                return null;
            }
        }));
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"api-docs"});
            add(new String[]{"v2", "api-docs"});
            add(new String[]{"v2", "api-docs-ext"});
            add(new String[]{"swagger", "v1", "swagger.json"});
        }}, (baseRequestResponse, checkRequest, newUrl) -> {
            IResponseKeywords founds = Utils.Helpers.analyzeResponseKeywords(new ArrayList<String>() {{
                add("\"swagger\":");
            }}, checkRequest.getResponse());
            if (BypassPayloadUtils.hasFound(founds, 0) && Utils.Helpers.analyzeResponse(checkRequest.getResponse()).getStatusCode() == 200) {
                Utils.Callback.printOutput("found " + newUrl + ".\r\n");
                return new Issue(
                        baseRequestResponse.getHttpService(), newUrl,
                        new IHttpRequestResponse[]{checkRequest},
                        "API-Docs found.",
                        "URL: " + newUrl,
                        "Medium", false);
            } else {
                return null;
            }
        }));
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"doc.html"});
        }}, (baseRequestResponse, checkRequest, newUrl) -> null));
    }};

    @Override
    public String getName() {
        return "API Doc";
    }

    @Override
    public List<Issue> check(URL url, IHttpRequestResponse originRequestResponse) {
        IRequestInfo originRequest = Utils.Helpers.analyzeRequest(originRequestResponse);
        List<String> originHeaders = originRequest.getHeaders();
        List<Issue> result = new ArrayList<>();
        for (Payload payload : payloads) {
            List<Issue> issues = new ArrayList<>();
            for (String[] resParts : payload.resources) {
                for (URL newUrl : BypassPayloadUtils.getBypassPayloads(url, resParts, ConfigUtils.getDict(ConfigUtils.DIR_BYPASS))) {
                    byte[] newRequest = BypassPayloadUtils.makeNewGETRequest(originHeaders, newUrl);
                    IHttpRequestResponse resp = Utils.Callback.makeHttpRequest(originRequestResponse.getHttpService(), newRequest);
                    Issue issue = payload.responseChecker.checkResponse(originRequestResponse, resp, newUrl);
                    if (issue != null) {
                        issues.add(issue);
                        break;
                    }
                }
                if (issues.size() > 0) {
                    break;
                }
            }
            result.addAll(issues);
        }
        return result;
    }
}

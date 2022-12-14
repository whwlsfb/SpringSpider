package burp.scanner.sub;

import burp.*;
import burp.scanner.IResponseChecker;
import burp.scanner.ISubScanner;
import burp.scanner.Payload;
import burp.scanner.SpringScanner;
import burp.utils.BypassPayloadUtils;
import burp.utils.ConfigUtils;
import burp.utils.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpringActuator implements ISubScanner {
    private SpringScanner scanner;

    public SpringActuator(SpringScanner scanner) {
        this.scanner = scanner;
    }

    private final List<Payload> payloads = new ArrayList<Payload>() {{
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"env"});
            add(new String[]{"actuator", "env"});
        }}, (baseRequestResponse, checkRequest, newUrl) -> {
            IResponseKeywords founds = Utils.Helpers.analyzeResponseKeywords(new ArrayList<String>() {{
                add("java.version");
                add("os.arch");
            }}, checkRequest.getResponse());
            if (BypassPayloadUtils.hasFound(founds, 0) && Utils.Helpers.analyzeResponse(checkRequest.getResponse()).getStatusCode() == 200) {
                Utils.Callback.printOutput("found " + newUrl + ".\r\n");
                return new Issue(
                        baseRequestResponse.getHttpService(),
                        newUrl,
                        new IHttpRequestResponse[]{checkRequest},
                        "Spring Actuator-Env found.",
                        "URL: " + newUrl,
                        "Medium", true);
            } else {
                return null;
            }
        }));
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"actuator"});
        }}, (baseRequestResponse, checkRequest, newUrl) -> {
            IResponseKeywords founds = Utils.Helpers.analyzeResponseKeywords(new ArrayList<String>() {{
                add("health");
                add("{\"self\":{");
                add("{\"_links\":{");
            }}, checkRequest.getResponse());
            if (BypassPayloadUtils.hasFound(founds, 0) && Utils.Helpers.analyzeResponse(checkRequest.getResponse()).getStatusCode() == 200) {
                Utils.Callback.printOutput("found " + newUrl + ".\r\n");
                return new Issue(
                        baseRequestResponse.getHttpService(),
                        newUrl,
                        new IHttpRequestResponse[]{checkRequest},
                        "Spring Actuator found.",
                        "URL: " + newUrl,
                        "Medium", false);
            } else {
                return null;
            }
        }));
    }};

    @Override
    public String getName() {
        return "Spring Actuator";
    }


    @Override
    public List<Issue> check(URL url, IHttpRequestResponse originRequestResponse) {
        IRequestInfo originRequest = Utils.Helpers.analyzeRequest(originRequestResponse);
        List<String> originHeaders = originRequest.getHeaders();
        List<Issue> result = new ArrayList<>();
        for (Payload payload : payloads) {
            List<Issue> issues = new ArrayList<>();
            for (String[] resParts : payload.resources) {
                if (Utils.urlAllowScan(resParts)) {
                    for (URL newUrl : BypassPayloadUtils.getBypassPayloads(url, resParts, ConfigUtils.getDict(ConfigUtils.DIR_BYPASS))) {
                        IHttpRequestResponse resp = scanner.doRequest(originHeaders, originRequestResponse, newUrl);
                        if (resp != null) {
                            Issue issue = payload.responseChecker.checkResponse(originRequestResponse, resp, newUrl);
                            if (issue != null) {
                                issues.add(issue);
                                break;
                            }
                        }
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

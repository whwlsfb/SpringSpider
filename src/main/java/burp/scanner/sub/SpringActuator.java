package burp.scanner.sub;

import burp.*;
import burp.scanner.IResponseChecker;
import burp.scanner.ISubScanner;
import burp.scanner.Payload;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SpringActuator implements ISubScanner {
    private final List<Payload> payloads = new ArrayList<Payload>() {{
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"env"});
            add(new String[]{"actuator", "env"});
        }}, new IResponseChecker() {
            @Override
            public Issue checkResponse(IHttpRequestResponse baseRequestResponse, IHttpRequestResponse checkRequest, URL newUrl) {
                return null;
            }
        }));
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"info"});
            add(new String[]{"actuator", "info"});
        }}, new IResponseChecker() {
            @Override
            public Issue checkResponse(IHttpRequestResponse baseRequestResponse, IHttpRequestResponse checkRequest, URL newUrl) {
                return null;
            }
        }));
        add(new Payload(new ArrayList<String[]>() {{
            add(new String[]{"doc.html"});
        }}, new IResponseChecker() {
            @Override
            public Issue checkResponse(IHttpRequestResponse baseRequestResponse, IHttpRequestResponse checkRequest, URL newUrl) {
                return null;
            }
        }));
    }};

    @Override
    public String getName() {
        return "Spring Actuator";
    }

    @Override
    public List<Issue> check(URL url, IHttpRequestResponse originRequest) {
        return null;
    }
}

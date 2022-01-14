package burp.scanner;

import java.util.List;

public class Payload {
    public List<String[]> resources = null;
    public IResponseChecker responseChecker = null;

    public Payload(List<String[]> resources, IResponseChecker responseChecker) {
        this.resources = resources;
        this.responseChecker = responseChecker;
    }
}

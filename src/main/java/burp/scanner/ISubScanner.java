package burp.scanner;

import burp.IHttpRequestResponse;
import burp.Issue;

import java.net.URL;
import java.util.List;

public interface ISubScanner {
    String getName();

    List<Issue> check(URL url, IHttpRequestResponse originRequestResponse);
}

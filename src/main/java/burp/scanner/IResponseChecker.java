package burp.scanner;

import burp.*;

import java.net.URL;

public interface IResponseChecker {
    Issue checkResponse(IHttpRequestResponse baseRequestResponse, IHttpRequestResponse checkRequest, URL newUrl);
}

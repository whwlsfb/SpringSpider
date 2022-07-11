package burp;

import burp.scanner.SpringScanner;
import burp.ui.UIHandler;
import burp.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

public class BurpExtender implements IBurpExtender, IExtensionStateListener {

    public IExtensionHelpers helpers;
    public IBurpExtenderCallbacks callbacks;
    public PrintWriter stdout;
    public PrintWriter stderr;
    public String version = "0.1";
    public UIHandler uiHandler;
    public SpringScanner scanner;

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        Utils.Callback = this.callbacks = callbacks;
        Utils.Helpers = this.helpers = callbacks.getHelpers();
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        callbacks.setExtensionName("SpringSpider");
        this.stdout.println("SpringSpider v" + version);
        this.uiHandler = new UIHandler(this);
        callbacks.addSuiteTab(this.uiHandler);
        scanner = new SpringScanner();
        callbacks.registerScannerCheck(scanner);
        callbacks.registerExtensionStateListener(this);
    }

    @Override
    public void extensionUnloaded() {
    }

}

package burp.ui;

import burp.BurpExtender;
import burp.ITab;
import burp.utils.ConfigUtils;
import burp.utils.UIUtil;
import burp.utils.Utils;
import jdk.nashorn.internal.runtime.regexp.joni.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIHandler implements ITab {
    public JPanel mainPanel;
    public BurpExtender parent;
    public JTextField dirScanDeeper;
    public static final List<String> DefaultBypass = new ArrayList<String>() {{
        add(".");
        add(";");
    }};
    public static final List<String> DefaultScanPoint = new ArrayList<String>() {{
        add("/env");
        add("/actuator");
        add("/actuator/env");
    }};

    public UIHandler(BurpExtender parent) {
        this.parent = parent;
        this.initUI();
    }

    private void applyDefaultBypassIfNotSet() {
        String val = ConfigUtils.get(ConfigUtils.DIR_BYPASS);
        if (val == null || val.isEmpty()) {
            for (String str : DefaultBypass) {
                ConfigUtils.setStrToDict(ConfigUtils.DIR_BYPASS, str, true);
            }
        }
    }

    private void applyDefaultScanPointIfNotSet() {
        String val = ConfigUtils.get(ConfigUtils.SCAN_POINT);
        if (val == null || val.isEmpty()) {
            for (String str : DefaultScanPoint) {
                ConfigUtils.setStrToDict(ConfigUtils.SCAN_POINT, str, true);
            }
        }
    }

    private void initUI() {
        applyDefaultBypassIfNotSet();
        applyDefaultScanPointIfNotSet();
        Utils.SplitDeep = ConfigUtils.getInt(ConfigUtils.DIR_SCAN_DEEPER, 3);
        this.mainPanel = new JPanel();
        mainPanel.setAlignmentX(0.0f);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(new BoxLayout(mainPanel, 1));
        EmptyBorder border = new EmptyBorder(5, 5, 6, 6);


        JPanel panel1 = UIUtil.GetXJPanel();
        dirScanDeeper = new JTextField(10);
        dirScanDeeper.setText(String.valueOf(ConfigUtils.getInt(ConfigUtils.DIR_SCAN_DEEPER, 3)));
        dirScanDeeper.setMaximumSize(dirScanDeeper.getPreferredSize());
        panel1.add(new JLabel("Dir Scan Deeper: "));
        panel1.add(dirScanDeeper);
        panel1.add(new JButton("Save") {{
            addActionListener(l -> {
                try {
                    int val = Integer.parseInt(dirScanDeeper.getText());
                    Utils.SplitDeep = val;
                    ConfigUtils.setInt(ConfigUtils.DIR_SCAN_DEEPER, val);
                    JOptionPane.showMessageDialog(mainPanel, "OK!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Must be number!");
                }
            });
        }});
        mainPanel.add(panel1);

        panel1 = UIUtil.GetXJPanel();
        panel1.add(new JLabel("Use Bypass: "));
        JLabel savedTip1 = new JLabel("Saved!");
        savedTip1.setVisible(false);
        savedTip1.setBorder(border);
        savedTip1.setForeground(Color.GREEN);
        for (String str : new String[]{
                ".",
                ";",
                "..;",
                ";%09..;",
                ";%09..",
                ";%2f..",
                "*",
                "%09",
                "%20",
                "%23",
                "%2e",
                "%2f"
        }) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setText(str);
            checkBox.addActionListener(a -> {
                ConfigUtils.setStrToDict(ConfigUtils.DIR_BYPASS, str, checkBox.isSelected());
                startTimerHide(savedTip1);
            });
            checkBox.setSelected(ConfigUtils.getStrInDict(ConfigUtils.DIR_BYPASS, str, false));
            panel1.add(checkBox);
        }
        panel1.add(savedTip1);
        mainPanel.add(panel1);

        panel1 = UIUtil.GetXJPanel();
        panel1.add(new JLabel("Scan Point: "));
        JLabel savedTip2 = new JLabel("Saved!");
        savedTip2.setVisible(false);
        savedTip2.setBorder(border);
        savedTip2.setForeground(Color.GREEN);
        for (String str : new String[]{
                "/env",
                "/actuator",
                "/actuator/env"
        }) {
            JCheckBox checkBox = new JCheckBox();
            checkBox.setText(str);
            checkBox.addActionListener(a -> {
                ConfigUtils.setStrToDict(ConfigUtils.SCAN_POINT, str, checkBox.isSelected());
                startTimerHide(savedTip2);
            });
            checkBox.setSelected(ConfigUtils.getStrInDict(ConfigUtils.SCAN_POINT, str, false));
            panel1.add(checkBox);
        }
        panel1.add(savedTip2);
        mainPanel.add(panel1);


//        BackendUIHandler bui = new BackendUIHandler(parent);
//        POCUIHandler pui = new POCUIHandler(parent);
//        FuzzUIHandler fui = new FuzzUIHandler(parent);
//        this.mainPanel.addTab("Backend", bui.getPanel());
//        this.mainPanel.addTab("POC", pui.getPanel());
//        this.mainPanel.addTab("Fuzz", fui.getPanel());
    }

    public void startTimerHide(JComponent component) {
        component.setVisible(true);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    component.setVisible(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public String getTabCaption() {
        return "SpringSpider";
    }

    @Override
    public Component getUiComponent() {
        return mainPanel;
    }
}

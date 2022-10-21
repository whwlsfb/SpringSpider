# SpringSpider

该工具为被动扫描Spring Actuator端点的BurpSuite插件，用于解决多层级目录下隐藏的Actuator端点、或端点需要Bypass才能访问的情况下的漏报问题。

## 安装方法

导航至BurpSuite的`Extender->Extensions`界面，点击`Add`按钮，在弹出的窗口中点击`Select file ...`按钮，在文件打开页面中找到插件的jar文件，安装即可。

## 使用

该插件安装完成后，将无需特殊设置，自动启用被动扫描，扫描发现的端点将会生成漏洞条目出现在BurpSuite首页的`Issue activity`中。另外，若要优化扫描过程中的参数，则需要根据需要，修改插件设置，插件设置位于BurpSuite的SpringSpider选项卡。

本插件具有如下设置项：

#### Enable

该复选框为修改该插件的启用状态，当该复选框选中时插件才会执行被动扫描。当取消选中时，插件将不会再接受新的扫描任务，在当前正在执行的扫描任务结束后将会停止扫描。

#### Dir Scan Deeper

该设置项为修改插件的目录扫描深度，设置范围为`1~∞`，默认建议值为`3`，假设当前目录扫描深度设置为`3`，在用户访问目标「`http://test.com/backend/api/admin/user/`」时，将会拆分为「`http://test.com/`」、「`http://test.com/backend/`」、「`http://test.com/backend/api/`」分别扫描，该参数请尽量控制在1~5以内，以避免产生过大的请求流量。

#### Use Bypass

该设置项为修改启用的Bypass字符列表，默认启用`;`、`.`，当正常请求无果后，将会尝试在路径中插入Bypass字符尝试进行绕过，例如在启用`;`字符后，对「`http://test.com/api/actuator/env`」的绕过URL则是「`http://test.com/api/;/actuator/;/env`」

#### Scan Point

该设置项为修改启动扫描的端点，为了避免请求频率过大，目前支持启用的端点有「/actuator/env」、「/actuator」、「/env」，建议全部启用。

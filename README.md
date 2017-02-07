Teamcity agent priority
=======================
Plugin for ordering build agents.

Description
-----------
This plugin gives you a possibility to prioritize your build agents 
by different criteria, so whenever the build starts, available agent 
with higher weight will be used.

:panda_face: The plugin requires Teamcity version **10+** for correct functionality, as it uses some recent API.

Installation
------------
To install plugin [download zip archive](https://github.com/grundic/teamcity-agent-priority/releases)
it and copy it to Teamcity \<data directory\>/plugins (it is $HOME/.BuildServer/plugins under Linux and C:\Users\<user_name>\.BuildServer\plugins under Windows).
For more information, take a look at [official documentation](https://confluence.jetbrains.com/display/TCD10/Installing+Additional+Plugins)

Configuration
-------------
Build agent prioritization is configured per project. Sub-projects would inherit their parents settings. So, for example,
adding configuration to the ROOT project would affect the whole Teamcity server installation.
To add priority to selected build, go to the project settings and select `Agent priority` on the left panel. Then click
on `Add Priority` button and select one of the available prioritizes:
  
  * By build status: this priority would sort agents depending on the build result, that was received during previous executions;
  * By configuration parameter: this priority would sort agents by the given parameter, provided by user. Parameter should be set in buildAgent.properties file;
  * By CPU benchmark index: this priority would sort agents by CPU benchmark index, calculated during agent start;
  * By name: this priority would sort agents by name;
  
Each priority could have some parameters, which are described on the corresponding configuration page.
Multiple priorities could be selected and reordered.
For your convenience, there is a possibility to check prioritization: select the build for which you would like to see
ordered agent and you would see agents in sorted order.

![demo](https://github.com/grundic/teamcity-agent-priority/blob/master/media/example.png?raw=true)


License
-------
[MIT](https://github.com/grundic/teamcity-agent-priority/blob/master/LICENSE)


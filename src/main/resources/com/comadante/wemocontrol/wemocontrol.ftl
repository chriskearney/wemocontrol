<#-- @ftlvariable name="" type="com.comadante.wemocontrol.WemoControlView" -->
<html>
    <body>
        <#list switches>
        <ul>
        <#items as switch>
            <li><a href="/wemo/${getUrlSafe(switch)}/toggle">${switch}</a></li>
        </#items>
       </ul>
       </#list>
     </body>
</html>
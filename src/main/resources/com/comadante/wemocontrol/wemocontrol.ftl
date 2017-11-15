<#-- @ftlvariable name="" type="com.comadante.wemocontrol.WemoControlView" -->
<html>
    <body>
        <#list switches>
        <ul>
        <#items as switch>
            <li>${switch}</li>
        </#items>
       </ul>
       </#list>
     </body>
</html>
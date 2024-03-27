<#assign active = "nav-item active"/>
<div class="sidebar" data-image="../static/assets/img/sidebar-5.jpg" data-color="azure">
    <div class="sidebar-wrapper">
        <div class="logo">
            <a href="" class="simple-text">
                Dataverse
            </a>
        </div>
        <ul class="nav">
            <li class="<#if (pageName!"") == 'CAMPAIGN'>${active}</#if>">
                <a class="nav-link" href="${springMacroRequestContext.contextPath}">
                    <i class="nc-icon nc-chart-pie-35"></i>
                    <p>XXXX</p>
                </a>
            </li>
            <li class="<#if (pageName!"") == 'PORT_DI'>${active}</#if>">
                <a class="nav-link" href="${springMacroRequestContext.contextPath}/di/">
                    <i class="nc-icon nc-app"></i>
                    <p>数据集成</p>
                </a>
            </li>
            <li class="<#if (pageName!"") == 'PORT_DD'>${active}</#if>">
                <a class="nav-link" href="${springMacroRequestContext.contextPath}/dd/">
                    <i class="nc-icon nc-globe-2"></i>
                    <p>数据开发</p>
                </a>
            </li>
        </ul>
    </div>
</div>
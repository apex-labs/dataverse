<!-- Navbar -->
<nav class="navbar navbar-expand-lg " color-on-scroll="500">
    <div class="container-fluid">
        <a class="navbar-brand" href="#pablo"> ${pageTitle} </a>
        <button href="" class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" aria-controls="navigation-index" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-bar burger-lines"></span>
            <span class="navbar-toggler-bar burger-lines"></span>
            <span class="navbar-toggler-bar burger-lines"></span>
        </button>
        <div class="collapse navbar-collapse justify-content-end" id="navigation">
            <ul class="nav navbar-nav mr-auto">
                <li class="nav-item">
                    <a href="#" class="nav-link" data-toggle="dropdown">
                        <i class="nc-icon nc-palette"></i>
                        <span class="d-lg-none">Dashboard</span>
                    </a>
                </li>
                <#--<li class="dropdown nav-item">-->
                    <#--<a href="#" class="dropdown-toggle nav-link" data-toggle="dropdown">-->
                        <#--<i class="nc-icon nc-planet"></i>-->
                        <#--<span class="notification">5</span>-->
                        <#--<span class="d-lg-none">Notification</span>-->
                    <#--</a>-->
                    <#--<ul class="dropdown-menu">-->
                        <#--<a class="dropdown-item" href="#">Notification 1</a>-->
                        <#--<a class="dropdown-item" href="#">Notification 2</a>-->
                        <#--<a class="dropdown-item" href="#">Notification 3</a>-->
                        <#--<a class="dropdown-item" href="#">Notification 4</a>-->
                        <#--<a class="dropdown-item" href="#">Another notification</a>-->
                    <#--</ul>-->
                <#--</li>-->
                <li class="nav-item">
                    <a href="#" class="nav-link">
                        <i class="nc-icon nc-zoom-split"></i>
                        <span class="d-lg-block">&nbsp;Search</span>
                    </a>
                </li>
            </ul>
            <ul class="navbar-nav ml-auto">
                <li class="nav-item">
                    <a class="nav-link" href="#pablo">
                        <span class="no-icon">Danny</span>
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="http://example.com" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <span class="no-icon">Dropdown</span>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/camp-trigger">活动管理</a>
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/user-event/">注册事件</a>
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/login-event/">登录事件</a>
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/order-event/">订单事件</a>
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/trigger-result/">触发结果</a>
                        <a class="dropdown-item" href="${springMacroRequestContext.contextPath}/kafka-view/">KFK视图</a>
                        <a class="dropdown-item" href="#">。。。</a>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#pablo">
                        <span class="no-icon">Log out</span>
                    </a>
                </li>
            </ul>
        </div>
    </div>
</nav>
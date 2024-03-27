<!--
=========================================================
 Light Bootstrap Dashboard - v2.0.1
=========================================================

 Product Page: https://www.creative-tim.com/product/light-bootstrap-dashboard
 Copyright 2019 Creative Tim (https://www.creative-tim.com)
 Licensed under MIT (https://github.com/creativetimofficial/light-bootstrap-dashboard/blob/master/LICENSE)

 Coded by Creative Tim

=========================================================

 The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.  -->
<!DOCTYPE html>
<html lang="en">

<head>
    <#include "../template/meta.ftl"/>
</head>

<body>
    <#assign pageName = "PORT_DI"/>
    <#assign pageTitle = "Kafka工具"/>
<div class="wrapper">
    <#include "../template/slider.ftl"/>
    <div class="main-panel">
        <#include "../template/navbar.ftl"/>
        <div class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-12">
                        <div class="card strpied-tabled-with-hover" style="padding-left: 20px;">
                            <div class="row">
                                <div class="col-md-12">
                                    <div class="card-header ">
                                        <h4 class="card-title">输入服务端Host</h4>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-6">
                                                <label>Jdbc URL</label>
                                                <input type="text" id="jdbcUrl" value="jdbc:mysql://10.25.19.1:3306/ecommerce?tinyInt1isBit=false&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL" placeholder="请输入JDBC URL" class="col-md-12 form-control">
                                            </div>
                                            <div class="col-md-3">
                                                <label>User</label>
                                                <input type="text" id="jdbcUser" value="dev" placeholder="请输入用户名" class="col-md-12 form-control">
                                            </div>
                                            <div class="col-md-3">
                                                <label>Password</label>
                                                <input type="textarea" id="jdbcPwd" value='123456' class="col-md-12 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <textarea id="query" class="col-md-12 form-control">select * from a_user</textarea>
                                            </div>
                                        </div>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <textarea id="storageName" class="col-md-12 form-control">/tmp/dataverse/a_user</textarea>
                                            </div>
                                        </div>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <input id="serverAddress" type="hidden" value="">
                                                <button id="connBtn" class="btn btn-info col-md-12" onclick="di()">抽数</button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div id="eventBody" class="col-md-12">
                        <div class="card strpied-tabled-with-hover" style="padding-left: 20px;">
                            <div class="card-body table-full-width table-responsive">
                                <table class="table table-hover table-striped">
                                    <thead>
                                    <th>结果</th>
                                    </thead>
                                    <tbody id="diResult" style="font-size: 10px;">
                                        <!-- 动态写入 -->
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <#include "../template/footer.ftl"/>
    </div>
</div>
    <#include "../template/plugin.ftl"/>

    <#include "../template/script.ftl"/>

    <script src="${springMacroRequestContext.contextPath}/static/js/port-di.js"></script>
</body>
</html>

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
<#assign pageName = "PORT_DD"/>
<#assign pageTitle = "Open Data Platform Connnective"/>
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
                                        <h4 class="card-title">输入数据源信息</h4>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <label>SQL:</label>
                                                <textarea id="sql" size="100" class="form-control"
                                                          value="select * from a_user limit 100">select * from a_user limit 100</textarea>
                                            </div>
                                        </div>
                                        <div class="row" style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <label>StorageInfo:</label>
                                                <textarea id="storageInfo" size="100" class="form-control"
                                                          value="">[{"tableName":"a_user","storePath":"/tmp/dataverse/a_user"}]</textarea>
                                            </div>
                                        </div>
                                        <div id="operatorDiv" class="row"
                                             style="padding-bottom: 20px;padding-top: 10px;">
                                            <div class="col-md-12">
                                                <label></label>
                                                <input type="button" class="btn btn-info col-md-12" onclick="query()" value="运行">
                                            </div>
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
                                <tbody id="odpcResultContent" style="font-size: 10px;">
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

<script src="${springMacroRequestContext.contextPath}/static/js/port-dd.js"></script>
</body>
</html>

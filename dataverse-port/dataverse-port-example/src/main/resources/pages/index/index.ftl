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
    <#assign pageName = "CAMPAIGN"/>
    <div class="wrapper">
        <#include "../template/slider.ftl"/>
        <div class="main-panel">
            <#include "../template/navbar.ftl"/>
            <div class="content">
                <div class="container-fluid">
                    <div class="row">
                        <div class="col-md-12">
                            <#if eventTrigger??>
                                <div class="card strpied-tabled-with-hover" style="padding-left: 20px;">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <div class="card-header ">
                                                <h4 class="card-title">活动发布结果</h4>
                                                <p class="card-category">${eventTrigger}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </#if>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <form id="myForm" method="post" action="${springMacroRequestContext.contextPath}/publish">
                                <div class="card strpied-tabled-with-hover" style="padding-left: 20px;">
                                    <div class="card-header ">
                                        <h4 class="card-title">活动信息</h4>
                                        <#--<p class="card-category">请填写活动信息</p>-->
                                    </div>
                                    <div class="card-body table-full-width table-responsive">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <label>活动ID</label>
                                                <input type="text" name="campaignId" class="form-control" placeholder="请输入活动ID">
                                            </div>
                                            <div class="col-md-3">
                                                <label>活动名称</label>
                                                <input type="text" name="campaignName" class="form-control" placeholder="请输入活动名称">
                                            </div>
                                            <#--<div class="col-md-3">-->
                                                <#--<label>开始时间</label>-->
                                                <#--<input type="text" name="startTime" class="form-control" placeholder="yyyy-MM-dd HH:ss:mm">-->
                                            <#--</div>-->
                                            <#--<div class="col-md-3">-->
                                                <#--<label>结束时间</label>-->
                                                <#--<input type="text" name="endTime" class="form-control" placeholder="yyyy-MM-dd HH:ss:mm">-->
                                            <#--</div>-->
                                        </div>
                                    </div>
                                </div>
                                <div class="card strpied-tabled-with-hover" style="padding-left: 20px;">
                                    <div class="card-header ">
                                        <h4 class="card-title">触发规则</h4>
                                        <#--<p class="card-category">请填写业务事件的触发规则信息</p>-->
                                    </div>
                                    <div class="card-body table-full-width table-responsive">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <select id="userEvent" name="event1.eventCode" class="col-md-10 form-control">
                                                    <option value="100001">用户注册事件</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select id="userEvent1" name="event1.rule1FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list userEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select id="userEvent1" name="event1.rule1Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event1.rule1Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select id="userEvent2" name="event1.rule2FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list userEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select id="userEvent2" name="event1.rule2Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event1.rule2Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event1.rule3FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list userEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event1.rule3Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event1.rule3Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card-body table-full-width table-responsive">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <select name="event2.eventCode" class="col-md-10 form-control">
                                                    <option value="100002">用户登录事件</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event2.rule1FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list loginEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event2.rule1Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event2.rule1Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event2.rule2FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list loginEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event2.rule2Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event2.rule2Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="card-body table-full-width table-responsive">
                                        <div class="row">
                                            <div class="col-md-6">
                                                <select name="event3.eventCode" class="col-md-10 form-control">
                                                    <option value="100003">用户下单位事件</option>
                                                </select>
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event3.rule1FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list orderEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event3.rule1Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event3.rule1Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event3.rule2FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list orderEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event3.rule2Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event2.rule2Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                        <div class="row" style="margin-top: 20px;">
                                            <div class="col-md-1"></div>
                                            <div class="col-md-3">
                                                <select name="event3.rule3FieldName" class="col-md-10 form-control">
                                                    <option value="">请选择事件属性</option>
                                                    <#list orderEvent as event>
                                                        <option value="${event.eventField!}">${event.eventName!}</option>
                                                    </#list>
                                                </select>
                                            </div>
                                            <div class="col-md-2">
                                                <select name="event3.rule3Operator" class="col-md-10 form-control">
                                                    <option value="1">等于</option>
                                                    <option value="2">不等于</option>
                                                    <option value="3">包含</option>
                                                    <option value="4">不包含</option>
                                                    <option value="5">有值</option>
                                                    <option value="6">无值</option>
                                                    <option value="7">空</option>
                                                    <option value="8">非空</option>
                                                </select>
                                            </div>
                                            <div class="col-md-4">
                                                <input type="text" name="event3.rule3Operand" value="" class="col-md-10 form-control">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card strpied-tabled-with-hover" style="padding-left: 20px;padding-right: 20px;">
                                    <div class="card-body table-full-width table-responsive">
                                        <div class="form-group">
                                            <button type="submit" class="btn btn-info col-md-12" onclick="">提交</button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>

                    </div>
                </div>
            </div>
            <#include "../template/footer.ftl"/>
        </div>
    </div>
    <#include "../template/plugin.ftl"/>

    <#include "../template/script.ftl"/>

    <script src="${springMacroRequestContext.contextPath}/static/js/xxx.js"></script>
</body>
</html>

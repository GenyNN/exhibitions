<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Dashboard - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/dashboard.css'/>"/>
</head>
<body>
<div class="container-fluid">
<#if secure == false>
    <div class="row">
        <div class="col">
            <div id="security-box-inner" class="box-inner">
            <span>Warning! The default staff account is enabled, which presents a security risk because it uses a
                username and password that are publicly available on GitHub. Please either delete the account or
                change its password before using Einvite in a production setting.</span>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div id="auth-box-outer" class="col">
            <div id="auth-box-inner" class="box-inner">
                <form action="<@spring.url '/logout'/>" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-danger">Sign Out</button>
                </form>
            </div>
        </div>
    </div>
<#if list\-participant??>
    <div class="row">
        <div id="participant-box-outer" class="col">
            <div id="participant-box-inner" class="box-inner">
                <p>Участники <button id="participant-button-create" class="btn btn-success" <#if create\-participant??><#else>disabled</#if>>Создать</button></p>
                <table id="participant-box-content" class="table">
                    <tr><th>id</th><th>Кличка</th><th>Заводчик/Питомник</th><th>Email</th><th>Действие</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if list\-asset??>
    <div class="row">
        <div id="asset-box-outer" class="col">
            <div id="asset-box-inner" class="box-inner">
                <p>Награды <button id="asset-button-create" class="btn btn-success" <#if create\-asset??><#else>disabled</#if>>Создать</button></p>
                <table id="asset-box-content" class="table">
                    <tr><th>Название</th><th>Выставка</th><th>Действие</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if list\-event??>
    <div class="row">
        <div id="event-box-outer" class="col">
            <div id="event-box-inner" class="box-inner">
                <p>Выставки <button id="event-button-create" class="btn btn-success" <#if create\-event??><#else>disabled</#if>>Создать</button></p>
                <table id="event-box-content" class="table">
                    <tr><th>Название</th><th>Дата начала</th><th>Дата окончания</th><th>Действие</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if list\-staff??>
    <div class="row">
        <div id="perm-box-outer" class="col">
            <div id="perm-box-inner" class="box-inner">
                <p> Права доступа для ${staff.username?capitalize}</p>
                <ul>
                    <#list staff.permissions as permission>
                        <li>${permission.description}</li>
                    </#list>
                </ul>
            </div>
        </div>

        <div id="staff-box-outer" class="col">
            <div id="staff-box-inner" class="box-inner">
                <p>Редактор персонала <button id="staff-button-create" class="btn btn-success" <#if create\-staff??><#else>disabled</#if>>Create</button></p>
                <table id="staff-box-content" class="table">
                    <tr><th>Кличка</th><th>Действие</th></tr>
                </table>
            </div>
        </div>
    </div>
</#if>
<#if read\-logs??>
    <div class="row">
        <div id="log-box-outer" class="col">
            <div id="log-box-inner" class="box-inner">
                <p>Audit Log</p>
                <div id="log-box-content"></div>
            </div>
        </div>
    </div>
</#if>
    <div class="row">
        <div class="col">
        <#include "copyright.inc.ftlh">
        </div>
    </div>
</div>

<div class="invisible">
<#list permissions as permission>
    <div id="perm-${permission}"></div>
</#list>
</div>

<#include "stdimports.inc.ftlh">
<script type="text/javascript" src="<@spring.url 'js/auditlog.js'/>"></script>
<script type="text/javascript" src="<@spring.url 'js/participantdashboard.js'/>"></script>
<script type="text/javascript" src="<@spring.url 'js/staffdashboard.js'/>"></script>
<script type="text/javascript" src="<@spring.url 'js/assetdashboard.js'/>"></script>
<script type="text/javascript" src="<@spring.url 'js/eventdashboard.js'/>"></script>
</body>
</html>
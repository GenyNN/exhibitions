<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Asset - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/assetcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="asset-create-form">
                <h1>Создать награду</h1>
                <form action="<@spring.url '/api/v1/asset'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Награда</label>
                        <input type="text" class="form-control" name="name" id="asset-name" placeholder="Награда">
                    </div>
                    <div class="form-group">
                        <label for="">Описание</label>
                        <input type="text" class="form-control" name="description" id="asset-description" placeholder="Описание">
                    </div>
                    <div class="form-group">
                        <label for="">Выставка</label>
                        <select name="eventId" id="asset-event">
                            <#list events as event>
                                <option value="${event.id}">${event.name}</option>
                            </#list>
                        </select>
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Отменить</button>
                    <button class="btn btn-primary">Подтвердить</button>
                </form>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col">
        <#include "copyright.inc.ftlh">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftlh">
<script src="<@spring.url '/js/assetcreate.js'/>"></script>
</body>
</html>
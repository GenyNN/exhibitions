<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Event - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/eventcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="event-create-form">
                <h1>Создать выставку</h1>
                <form action="<@spring.url '/api/v1/event'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Название</label>
                        <input type="text" class="form-control" name="name" id="event-name" placeholder="Название">
                    </div>
                    <div class="form-group">
                        <label for="">Описание</label>
                        <input type="text" class="form-control" name="description" id="event-description" placeholder="Описание">
                    </div>
                    <div class="form-group">
                        <label for="">Дата начала (GMT)</label>
                        <input type="datetime-local" class="form-control" name="startDate" id="event-start-date">
                    </div>
                    <div class="form-group">
                        <label for="">Дата окончания (GMT)</label>
                        <input type="datetime-local" class="form-control" name="endDate" id="event-end-date">
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
<script src="<@spring.url '/js/eventcreate.js'/>"></script>
</body>
</html>
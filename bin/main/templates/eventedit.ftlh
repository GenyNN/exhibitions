<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Event - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/eventedit.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="event-edit-form">
                <h1>Редактировать выставку</h1>
                <form action="<@spring.url '/api/v1/event/${event.id}'/>" method="post">
                    <div id="event-error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Название</label>
                        <input type="text" class="form-control" name="name" id="event-name" placeholder="Short Name" value="${event.name}">
                    </div>
                    <div class="form-group">
                        <label for="">Описание</label>
                        <input type="text" class="form-control" name="description" id="event-description" placeholder="Description" value="${event.description}">
                    </div>
                    <div class="form-group">
                        <label for="">Дата начала (GMT)</label>
                        <input type="datetime-local" class="form-control" name="startDate" id="event-start-date" value="${event.startDate?string["yyyy-MM-dd'T'HH:mm"]}">
                    </div>
                    <div class="form-group">
                        <label for="">Дата окончания (GMT)</label>
                        <input type="datetime-local" class="form-control" name="endDate" id="event-end-date" value="${event.endDate?string["yyyy-MM-dd'T'HH:mm"]}">
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Отменить</button>
                    <button class="btn btn-primary">Создать</button>
                </form>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col">
            <div id="event-participants-edit-form">
                <h1>Участники выставок</h1>
                <form action="<@spring.url '/api/v1/event/${event.id}/participant'/>" method="post">
                    <div id="event-participant-error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Участники</label>
                        <table id="event-participant-table">
                            <tr><th>Участник</th><th>Награда</th><th>Действие</th><th></th></tr>
                            <#list awards as award>
                            <tr>
                                <td>${award.awardIdentity.participant.lastName}, ${award.awardIdentity.participant.firstName} ()</td>
                                <td>
                                <select data-event-id="${award.awardIdentity.event.id}" data-participant-id="${award.awardIdentity.participant.id}">
                                    <#if award.asset??>
                                        <option value="">None</option>
                                    <#else>
                                        <option value="" selected>None</option>
                                    </#if>
                                    <#list assets as asset>
                                        <#if award.asset?? && award.asset.id == asset.id>
                                        <option value="${asset.id}" selected>${asset.name}</option>
                                        <#else>
                                        <option value="${asset.id}">${asset.name}</option>
                                        </#if>
                                    </#list>
                                </select>
                                </td>
                                <td><button data-csrf-param="${_csrf.parameterName}" data-csrf-token="${_csrf.token}" data-participant-id="${award.awardIdentity.participant.id}" type="submit" class="btn btn-danger">Удалить</button></td>
                                <td><p class="text-danger"></p></td>
                            </tr>
                            </#list>
                        </table>
                    </div>
                    <div class="form-group">
                        <label for="">Добавить участника</label>
                        <input type="text" class="form-control" name="email" id="event-participant" placeholder="Email участника">
                    </div>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-primary">Добавить</button>
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
<script src="<@spring.url '/js/eventedit.js'/>"></script>
</body>
</html>
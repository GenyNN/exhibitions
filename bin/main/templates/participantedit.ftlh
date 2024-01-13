<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Edit Participant - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/participantedit.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="participant-edit-form">
                <h1>Редактировать участника</h1>
                <form action="<@spring.url '/api/v1/participant/${participant.id}'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Кличка</label>
                        <input type="text" class="form-control" name="firstName" id="participant-first-name" placeholder="First Name" value="${participant.firstName}">
                    </div>
                    <div class="form-group">
                        <label for="">Заводчик/Питомник</label>
                        <input type="text" class="form-control" name="lastName" id="participant-last-name" placeholder="Last Name" value="${participant.lastName}">
                    </div>
                    <!--
                    <div class="form-group">
                        <label for="">Email</label>
                        <input type="text" class="form-control" name="email" id="participant-email" placeholder="Email" value="${participant.email}">
                    </div>
                    -->
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button class="btn btn-danger">Отменить</button>
                    <button class="btn btn-primary">Создать</button>
                </form>
            </div>
        </div>
    </div>


    <div class="row">
        <div class="col">
            <div id="participants-assets-edit-form">
                <h1>Награды за участие в выставках</h1>
                <form action="<@spring.url '/api/v1/participant/${participant.id}/addAsset'/>" method="post">
                    <div id="event-participant-error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <table id="participant-asset-table">
                            <#list awardEntities as awardEntity>
                            <tr>
                                <td>${awardEntities.participant.lastName}, ${awardEntities.participant.firstName} (${awardEntities.participant.email})</td>
                                <td>
                                </td>
                            </tr>
                            </#list>
                        </table>

                        <!--
                        <table id="participant-asset-table">
                            <#list awards as award>
                            <tr>
                                <td>${award.awardIdentity.participant.lastName}, ${award.awardIdentity.participant.firstName} (${award.awardIdentity.participant.email})</td>
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
                        -->

                        <!--
                    </div>
                    <div class="form-group">
                        <label for="">Добавить награду</label>
                        -->


                        <!--
                            <input type="text" class="form-control" name="id" id="id-asset" placeholder="Id награды">
                        -->

                        <!--
                        <select data-participant-id="${participant.id}">
                            <#list assets as asset>
                                <option value="${asset.id}">${asset.name}</option>
                            </#list>
                        </select>
                        -->
                    </div>
                    <div class="form-group">
                        <label for="">Добавить участника</label>
                        <input type="text" class="form-control" name="email" id="participant-awardEntity" placeholder="Email участника">
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
<script src="<@spring.url '/js/participantedit.js'/>"></script>
</body>
</html>
<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Create Participant - Einvite</title>

    <#include "stdmeta.inc.ftlh">
    <#include "stdlinks.inc.ftlh">
    <#include "internal.inc.ftlh">
    <link rel="stylesheet" href="<@spring.url '/css/participantcreate.css'/>"/>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col">
            <div id="participant-create-form">
                <h1>Создать участника</h1>
                <form action="<@spring.url '/api/v1/participant'/>" method="post">
                    <div id="error-box" class="form-group invisible">
                        <p class="text-danger"></p>
                    </div>
                    <div class="form-group">
                        <label for="">Кличка</label>
                        <input type="text" class="form-control" name="firstName" id="participant-first-name" placeholder="Кличка">
                    </div>
                    <div class="form-group">
                        <label for="">Заводчик/Питомник</label>
                        <input type="text" class="form-control" name="lastName" id="participant-last-name" placeholder="Заводчик/Питомник">
                    </div>
                    <!--
                    <div class="form-group">
                        <label for="">Email</label>
                        <input type="text" class="form-control" name="email" id="participant-email" placeholder="participant@somewhere.net">
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
        <#include "copyright.inc.ftlh">
        </div>
    </div>
</div>

<#include "stdimports.inc.ftlh">
<script src="<@spring.url '/js/participantcreate.js'/>"></script>
</body>
</html>
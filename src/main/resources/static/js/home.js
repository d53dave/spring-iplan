$(function() {
    'use strict';

    var calendar;

    $.post("calendar/get", function(data) {
        console.log("Got calendar id " + data['id']);
        calendar = data;
        renderCalendar(data);
    });

    $('#resetButton').click(function(e) {
        e.preventDefault();
        var del = confirm("Sind sie sicher?");
        if (del) {
            $.post("calendar/" + calendar['id'] + "/clear", function(data) {
                calendar = data;
                renderCalendar(data);
                optionsDialog.dialog("close");
                $.bootstrapGrowl("Kalender geleert!", {
                    type: 'warning'
                });
            });
        }
    });

    var timeRegex = /^(\d+)\D+(\d+)\D+$/i;

    function getSeconds(hoursAndMinutes) {
        if (timeRegex.test(hoursAndMinutes)) {
            var results = timeRegex.exec(hoursAndMinutes);
            return Number(results[1]) * 3600 + Number(results[2]) * 60;
        }
        return 0;
    }

    function renderCourse(elem) {
        var event = {
            "internaltype": 'course',
            "start": moment(elem['startTime']),
            "end": moment(elem['startTime']).add(elem['duration'], 'seconds'),
            "title": elem['name'],
            "text": elem['text'],
            "id": elem['id']
        };

        $("#calendar").fullCalendar('renderEvent', event, true);

        if (Number(elem['preparationTime']) > 0) {
            var event = {
                "start": moment(elem['startTime']).subtract(elem['preparationTime'], 'seconds'),
                "end": elem['startTime'],
                "title": "Vorbereitung\n" + elem['name'],
                "id": elem['id'],
                "color": "#E070E6"
            };
            $("#calendar").fullCalendar('renderEvent', event, true);
        }
        if (Number(elem['reworkTime']) > 0) {
            var event = {
                "start": moment(elem['startTime']).add(elem['duration'], 'seconds'),
                "end": moment(elem['startTime']).add(elem['duration'], 'seconds').add(elem['reworkTime'], 'seconds'),
                "title": "Nachbearbeitung\n" + elem['name'],
                "id": elem['id'],
                'color': '#B9E670'
            };
            $("#calendar").fullCalendar('renderEvent', event, true);
        }
    }

    function renderActivity(elem) {
        var event = {
            "internaltype": 'activity',
            "start": moment(elem['startTime']),
            "end": moment(elem['startTime']).add(elem['duration'], 'seconds'),
            "title": elem['name'],
            "text": elem['text'],
            "id": elem['id'],
            "color": 'grey'
        };

        $("#calendar").fullCalendar('renderEvent', event, true);
    }

    function renderCalendar(calData) {
        if (calData == undefined) return;

        $("a#download").attr("href", "/calendar/" + calData['id'] + "/export");

        $('#calendar').fullCalendar('removeEvents');

        console.log("Rendering Calendar " + JSON.stringify(calData));

        calData['courses'].forEach(renderCourse);
        calData['activities'].forEach(renderActivity);
        updateStatisticsFields(calData['statistics']);
        $("input[name='workload']").val(calData['options']['workload']);
        $("input[name='freedays']").val(calData['options']['freeDayCount']);
    }

    function updateStatisticsFields(data) {
        if (data == undefined) return;

        $('#stat-total').text(Math.ceil(Number(data['total']) / 3600) + " Stunden");
        $('#stat-course').text(Math.ceil(Number(data['courseWork']) / 3600) + " Stunden");
        $('#stat-prep').text(Math.ceil(Number(data['preparation']) / 3600) + " Stunden");
        $('#stat-rework').text(Math.ceil(Number(data['rework']) / 3600) + " Stunden");
        $('#stat-activity').text(Math.ceil(Number(data['freeTime']) / 3600) + " Stunden");
        $('#stat-rest').text(Math.ceil(Number(data['rest']) / 3600) + " Stunden");
    }

    function refreshStatistics() {
        $.get("calendar/" + calendar['id'] + "/statistics", function(data) {
            updateStatisticsFields(data);
        });
    }

    var addCourseDialog, addCourseForm, addActivityDialog, addActivityForm, optionsDialog, optionsForm;

    // Options Dialog

    optionsDialog = $('#dialog-form-options').dialog({
        autoOpen: false,
        height: 400,
        width: 400,
        modal: true,
        resizable: false,
        draggable: false,
        open: function() {
            $('.ui-widget-overlay').bind('click', function() {
                $('#dialog-form-options').dialog('close');
            })
        },
        buttons: {
            "Abbruch": function() {
                optionsDialog.dialog("close");
            },
            "Speichern": function() {
                console.log("Speichere Options");
                var $dial = $(this),
                    workload = $dial.find("input[name='workload']").val(),
                    freedays = $dial.find("input[name='freedays']").val();

                $.ajax({
                    type: "POST",
                    contentType: "application/json; charset=utf-8",
                    url: "calendar/" + calendar['id'] + "/options",
                    data: JSON.stringify({
                        "workload": workload,
                        "freeDayCount": freedays
                    }),
                    success: function(data, textStatus, jqXHR) {
                        optionsDialog.dialog("close");
                        $.bootstrapGrowl("Optionen gepeichert!", {
                            type: 'success'
                        });
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        optionsDialog.dialog("close");
                        $.bootstrapGrowl("Optionen konnten nicht gepeichert werden!", {
                            type: 'danger'
                        });
                    }
                });
            }
        },
        close: function() {

        }
    });

    $("#editoptions").on("click", function(e) {
        e.preventDefault();
        console.log("editoptions clicked!");
        optionsDialog.dialog("open");
    });


    // ADD COURSE

    addCourseDialog = $("#dialog-form-course")
        .dialog({
            autoOpen: false,
            height: 600,
            width: 400,
            modal: true,
            resizable: false,
            draggable: false,
            open: function() {
                $('.ui-widget-overlay').bind('click', function() {
                    $('#dialog-form-course').dialog('close');
                })
            },
            buttons: {
                "Abbruch": function() {
                    addCourseDialog.dialog("close");
                },
                "Tu es!": function() {
                    var $dial = $(this),
                        name = $dial.find("input[name='name']").val(),
                        text = $dial.find("textarea").val(),
                        start = $dial.find("input[name='start']").val(),
                        duration = getSeconds($dial.find("input[name='duration']").val()),
                        preparation = getSeconds($dial.find("input[name='preparation']").val()),
                        rework = getSeconds($dial.find("input[name='rework']").val()),
                        priority = Number($dial.find("input[name='priority']").val());

                    var course = {
                        "name": name,
                        "text": text,
                        //09.06.2015 14:15 -> 2015-06-09T14:15:00.000
                        "startTime": moment(start, "DD.MM.YYYY HH:mm").format("YYYY-MM-DDTHH:mm:ss.SSS"),
                        "duration": duration,
                        "preparationTime": preparation,
                        "reworkTime": rework,
                        "priority": priority
                    };

                    $.ajax({
                        type: "POST",
                        contentType: "application/json; charset=utf-8",
                        url: "calendar/" + calendar['id'] + "/course/new",
                        data: JSON.stringify(course),
                        success: function(data, textStatus, jqXHR) {
                            renderCourse(data);
                            refreshStatistics();
                            addCourseDialog.dialog("close");
                            $.bootstrapGrowl("Kurs gepeichert!", {
                                type: 'success'
                            });
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            if (errorThrown === "Forbidden") {
                                $.bootstrapGrowl("Dieser Kurs überschneidet sich mit einem anderen Eintrag!", {
                                    type: 'danger'
                                });
                            } else {
                                $.bootstrapGrowl("Fehler: " + errorThrown, {
                                    type: 'danger'
                                });
                            }
                        }
                    });
                }
            },
            close: function() {
                //addCourseForm[0].reset();
                //allFields.removeClass("ui-state-error");
            }
        });


    $("#addcourse").on("click", function(e) {
        e.preventDefault();
        console.log("addcourse clicked!");
        addCourseDialog.dialog("open");
    });

    ///Add ACTIVITY
    addActivityDialog = $("#dialog-form-activity")
        .dialog({
            autoOpen: false,
            height: 400,
            width: 400,
            modal: true,
            resizable: false,
            draggable: false,
            open: function() {
                $('.ui-widget-overlay').bind('click', function() {
                    $('#dialog-form-activity').dialog('close');
                })
            },
            buttons: {
                "Abbruch": function() {
                    addActivityDialog.dialog("close");
                },
                "Tu es!": function() {
                    var $dial = $(this),
                        name = $dial.find("input[name='name']").val(),
                        text = $dial.find("textarea").val(),
                        duration = getSeconds($dial.find("input[name='duration']").val());


                    var activ = {
                        "internaltype": 'activity',
                        "name": name,
                        "text": text,
                        "duration": duration,
                    };

                    $.ajax({
                        type: "POST",
                        contentType: "application/json; charset=utf-8",
                        url: "calendar/" + calendar['id'] + "/activity/new",
                        data: JSON.stringify(activ),
                        success: function(data, textStatus, jqXHR) {
                            renderActivity(data);
                            refreshStatistics();
                            $.bootstrapGrowl("Aktivität gespeichert", {
                                type: 'success'
                            });
                            addActivityDialog.dialog("close");
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            if (errorThrown === "Conflict") {
                                $.bootstrapGrowl("Diese Aktivität kann nicht gespeichert werden", {
                                    type: 'danger'
                                });
                            } else {
                                $.bootstrapGrowl("Fehler: " + errorThrown, {
                                    type: 'danger'
                                });
                            }
                        }
                    });
                }
            },
            close: function() {
                //addActivityForm[0].reset();
                //allFields.removeClass("ui-state-error");
            }
        });


    $("#addactivity").on("click", function(e) {
        e.preventDefault();
        console.log("addcactivity clicked!");
        addActivityDialog.dialog("open");
    });

    // REMOVE

    $('#calendar').fullCalendar({
        header: {
            left: '',
            center: '',
            right: ''
        },
        defaultView: 'agendaWeek',
        columnFormat: 'ddd',
        editable: false,
        allDaySlot: false,
        maxTime: '22:00',
        minTime: '07:00',
        eventColor: '#378006',
        height: 750,
        eventOverlap: false,
        slotEventOverlap: false,
        firstDay: 1,
        businessHours: {
            start: '07:00', // a start time (10am in this example)
            end: '20:00', // an end time (6pm in this example)
            dow: [1, 2, 3, 4, 5, 6]
        },
        eventLimit: true, // allow "more" link when too many events,
        eventClick: function(calEvent, jsEvent, view) {
            var del = confirm("Wollen Sie dieses Ereignis entfernen?");
            if (del) {
                $.post("calendar/" + calendar['id'] + "/" + calEvent.internaltype + "/" + calEvent._id + "/delete", function(data) {
                        $('#calendar').fullCalendar('removeEvents', calEvent._id);
                        calendar = data;
                        renderCalendar(data);
                    })
                    .fail(function() {
                        alert("Ereignis konnte nicht entfernt werden!");
                    });
            }
        }
    });
    $('#calendar').fullCalendar('today');

    //UPLOAD
    var url = 'calendar/upload';
    $('#fileupload').fileupload({
            url: url,
            dataType: 'json',
            done: function(e, data) {

                renderCalendar(data.result);
                $.bootstrapGrowl("Import erfolgreich!", {
                    type: 'success'
                });
            },
            progressall: function(e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress .progress-bar').css('width',
                    progress + '%');
            }
        }).prop('disabled', !$.support.fileInput).parent()
        .addClass($.support.fileInput ? undefined : 'disabled');
});

setTimeout(function() {
    $('.fc-today').removeClass('fc-today'); //remove current day hightlight from calendar
}, 1);
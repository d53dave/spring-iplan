$(function() {
    'use strict';

    // OPTION DIALOG
    var workloadSpinner = $("#workload").spinner({
        max: 23,
        min: -1
    });
    var freedaysSpinner = $("#freedays").spinner({
        max: 7,
        min: 0
    });
    
    
    $('#duration-act').timepicker({
        timeFormat: "H 'h' m 'min'",
        timeText: 'Dauer',
        timeOnlyTitle: 'Dauer wählen',
        hourMax: 8,
        stepMinute: 5,
        beforeShow: function(input) {
            setTimeout(function() {
                $(input).datepicker("widget").find(
                        ".ui-datepicker-current")
                    .hide();
            }, 1);
        }
    });

    var spinner = $("#priority").spinner();
    $('#startTime').datetimepicker({
        minDate: moment().startOf('week').toDate(),
        maxDate: moment().endOf('week').toDate(),
        hourMin: 6,
        stepMinute: 5
    });

    $('#endTime').timepicker({
        timeFormat: "H 'h' m 'min'",
        timeText: 'Dauer',
        timeOnlyTitle: 'Dauer wählen',
        hourMax: 8,
        stepMinute: 5,
        beforeShow: function(input) {
            setTimeout(function() {
                $(input).datepicker("widget").find(
                        ".ui-datepicker-current")
                    .hide();
            }, 1);
        }
    });

    $('#rework').timepicker({
        timeFormat: "H 'h' m 'min'",
        timeText: 'Dauer',
        timeOnlyTitle: 'Dauer wählen',
        hourMax: 8,
        stepMinute: 5,
        beforeShow: function(input) {
            setTimeout(function() {
                $(input).datepicker("widget").find(
                        ".ui-datepicker-current")
                    .hide();
            }, 1);
        }
    });

    $('#preparation').timepicker({
        timeFormat: "H 'h' m 'min'",
        timeText: 'Dauer',
        timeOnlyTitle: 'Dauer wählen',
        hourMax: 8,
        stepMinute: 5,
        beforeShow: function(input) {
            setTimeout(function() {
                $(input).datepicker("widget").find(
                        ".ui-datepicker-current")
                    .hide();
            }, 1);
        }
    });
});
$(function() {
	var calendar = $('#calendar');
	console.log(calendar);

	calendar.fullCalendar({
		header: {
			left:   '',
		    center: '',
		    right:  ''
		},
		defaultView : 'agendaWeek',
		columnFormat: 'ddd',
		editable : true,
		allDaySlot : false,
		maxTime : '22:00',
		minTime : '07:00',
		eventColor : '#378006',
		height : 750,
		eventOverlap : false,
		slotEventOverlap : false,
		firstDay : 1,
		businessHours : {
			start : '07:00', // a start time (10am in this example)
			end : '20:00', // an end time (6pm in this example)
			dow : [ 1, 2, 3, 4, 5, 6 ]
		},
		eventLimit : true, // allow "more" link when too many events
		events : [ {
			title : 'All Day Event',
			start : '2015-02-01'
		}, {
			title : 'Long Event',
			start : '2015-02-07',
			end : '2015-02-10'
		}, {
			id : 992,
			title : 'Repeating Event',
			start : '2015-02-09T16:00:00'
		}, {
			id : 991,
			title : 'Repeating Event',
			start : '2015-02-16T16:00:00'
		}, {
			id : 123,
			title : 'LOL',
			start : (new Date()).toString()
		} ]
	});
	calendar.fullCalendar('today');

});

$(function() {
var calendar = $('#calendar');
console.log(calendar);
	calendar.fullCalendar({
                defaultView: 'agendaWeek',
		defaultDate: '2015-02-12',
		editable: true,
allDaySlot: false,
maxTime: '22:00',
minTime: '07:00',
eventColor: '#378006',
 height: 750,
eventOverlap: false,
slotEventOverlap: false,
firstDay:1,
businessHours: {
    start: '07:00', // a start time (10am in this example)
    end: '20:00', // an end time (6pm in this example)
    dow: [1,2,3,4,5,6]
},
		eventLimit: true, // allow "more" link when too many events
		events: [
			{
				title: 'All Day Event',
				start: '2015-02-01'
			},
			{
				title: 'Long Event',
				start: '2015-02-07',
				end: '2015-02-10'
			},
			{
				id: 999,
				title: 'Repeating Event',
				start: '2015-02-09T16:00:00'
			},
			{
				id: 999,
				title: 'Repeating Event',
				start: '2015-02-16T16:00:00'
			},
			{
				title: 'Conference',
				start: '2015-02-11',
				end: '2015-02-13'
			},
			{
				title: 'Meeting',
				start: '2015-02-11T10:30:00',
				end: '2015-02-11T12:30:00'
			},
			{
				title: 'Lunch',
				start: '2015-02-12T12:00:00'
			},
			{
				title: 'Meeting',
				start: '2015-02-12T14:30:00'
			},
			{
				title: 'Happy Hour',
				start: '2015-02-12T17:30:00'
			},
			{
				title: 'Dinner',
				start: '2015-02-12T20:00:00'
			},
			{
				title: 'Birthday Party',
				start: '2015-02-13T07:00:00'
			},
			{
				title: 'Click for Google',
				url: 'http://google.com/',
				start: '2015-02-28'
			}
		]
	});
});

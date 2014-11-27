$(function() {
    $.timepicker.regional['de'] = {
        timeOnlyTitle: 'Bitte Uhrzeit wählen',
        timeText: 'Uhrzeit',
        hourText: 'Stunde',
        minuteText: 'Minute',
        secondText: 'Sekunde',
        currentText: 'Aktuell',
        closeText: 'Schließen',
        timeFormat: 'HH:mm:ss',
        dateFormat: 'yy.mm.dd',
        monthNames: ['Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
            'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'],
        dayNames: ['Sonntag','Montag','Dienstag','Mittwoch','Donnerstag','Freitag','Samstag'],
        dayNamesShort: ['So','Mo','Di','Mi','Do','Fr','Sa'],
        dayNamesMin: ['So','Mo','Di','Mi','Do','Fr','Sa'],
        firstDay: 1
    };
    $.timepicker.setDefaults($.timepicker.regional['de']);
});
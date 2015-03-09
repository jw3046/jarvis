
# TODO: oauth for accessing API

import json
# json is returned from the google calendar api

event = {
        'summary': 'Party',
        'start': {
            'dateTime': '2015-07-02T10:00:00.000-07:00'
            },
        'end': {
            'dateTime': '2015-07-02T11:00:00.000-07:00'
            }
        }

created_event = service.events().insert(calendarID='primary', body=event).execute()
print created_event['id']


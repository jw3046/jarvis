
import requests
import json

def getJSON(string):
    '''Returns the JSON SEMAFOR parse by making an api call to
    the official website
    '''
    # build 'restful' api request
    url = 'http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence='+string
    # make get request
    response = requests.get(url)
    return response.text

def getFirstPhrase(jsontext):
    myjson = json.loads(jsontext)
    frames = myjson['sentences'][0]['frames']
    mainframe = frames[0]
    mainidea = mainframe['target']['text']

    return mainidea


if __name__ == '__main__':
    jsontext = getJSON("Hello my friend.")
    mainidea = getFirstPhrase(jsontext)
    print mainidea


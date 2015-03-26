
import requests
import json

def getJSON(string):
    '''Returns the JSON SEMAFOR parse by making an api call to
    the official website. Note that both the frame-based parsing
    and the dependency parse are in the json
    '''
    # build 'restful' api request
    url = 'http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence='+string
    # make get request
    response = requests.get(url)
    return response.text

def getFirstPhrase(jsontext):
    '''Simply tries to return the word in the first frame, which
    is the root of the dependency parse.
    '''
    myjson = json.loads(jsontext)
    frames = myjson['sentences'][0]['frames']
    mainframe = frames[0]
    mainidea = mainframe['target']['text']

    return mainidea


if __name__ == '__main__':
    jsontext = getJSON("My roommate stole all of my pizza yesterday.")
    #mainidea = getFirstPhrase(jsontext)
    print jsontext



import requests
import json

'''
The format of the JSON from Semafor is:

{
    debug_info:
        frame_parser_elapsed_seconds: float
        dependency_parser_elapsed_seconds: float
    sentences:
        [
            {
                conll: string
                text: string
                relations:
                    [
                        [string, string, [[string,string],[string,string]]],
                        ...
                    ]
                tokens:
                    [
                        string,
                        ...
                    ]
                entities:
                    [
                        [string, string, [[int, int]]],
                        ...
                    ]
                frames:
                    [
                        {
                            target:
                                {
                                    start: int
                                    end: int
                                    name: string
                                    text: string
                                }
                            annotationSets:
                                [
                                    {
                                        frameElements:
                                            [
                                                {
                                                    start: int
                                                    end: int
                                                    name: string
                                                    text: string
                                                },
                                                ...
                                            ]
                                        score: float
                                        rank: int
                                    }
                                ]
                        },
                        ...
                    ]
            }
        ]
}
'''

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


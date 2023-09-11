from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from django.conf import settings
import json

def page_not_found(request, exception):
    response_data = {}
    response_data['result'] = 'error'
    response_data['message'] = 'Page not found'
    response_data['result_id'] = '404'
    return HttpResponse(json.dumps(response_data), content_type="application/json")
    
def bad_request(request, exception):
    response_data = {}
    response_data['result'] = 'error'
    response_data['message'] = 'Bad Request'
    response_data['result_id'] = '400'
    return HttpResponse(json.dumps(response_data), content_type="application/json")
    
def permission_denied(request, exception):
    response_data = {}
    response_data['result'] = 'error'
    response_data['message'] = 'Bad Request'
    response_data['result_id'] = '501'
    return HttpResponse(json.dumps(response_data), content_type="application/json")
    
def server_error(request):
    response_data = {}
    response_data['result'] = 'error'
    response_data['message'] = 'Bad Request'
    response_data['result_id'] = '500'
    return HttpResponse(json.dumps(response_data), content_type="application/json")
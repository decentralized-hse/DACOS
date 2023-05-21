import json

from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import Server


def add_server(request):
    if request.method != 'POST':
        return HttpResponseBadRequest('Wrong request type')
    content = request.body.decode('utf-8')
    content = json.loads(content)
    if content.get('url') is None:
        return HttpResponseBadRequest('URL is undefined')
    if content.get('public_key') is None:
        return HttpResponseBadRequest('public_key is undefined')
    if len(Server.objects.filter(url=content['url'])) != 0:
        return HttpResponseBadRequest('There is already such server')
    Server(url=content.get('url'), public_key=content['public_key']).save()
    return HttpResponse('OK')

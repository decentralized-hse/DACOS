import json

from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import PublicUser
from utils import valid_username, server_exists


def register_user_once(request):
    content = request.body.decode('utf-8')
    content = json.loads(content)
    if request.method == 'POST' and 'username' in content \
            and 'server' in content and 'public_key' in content:
        if not valid_username(content['username']):
            return HttpResponseBadRequest('Username is already in use.')
        if server_exists(content['server']):
            user = PublicUser(username=content['username'], public_key=content['public_key'],
                              register_server=content['server'])
            user.save()
        else:
            return HttpResponseBadRequest('No requested server.')

    else:
        return HttpResponseBadRequest('Bad request format.')
    return HttpResponse('OK')
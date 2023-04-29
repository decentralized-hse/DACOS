import json

from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import PublicUser, Server
from utils import valid_username, server_exists
import requests
import os


# Переделать регистрацию потому что сейчас рекурсивно запускается.
def register_user(request):
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
            # Registering user everywhere.
            for server in Server.objects.all().iterator():
                if server.url != os.getenv('THIS_SERVER'):
                    requests.post(server.url + '/users/register/once', {'username': user.username,
                                                                        'server': user.register_server,
                                                                        'public_key': user.public_key})
        else:
            return HttpResponseBadRequest('No requested server.')

    else:
        return HttpResponseBadRequest('Bad request format.')
    return HttpResponse('OK')

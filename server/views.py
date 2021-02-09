from django.shortcuts import render
from django.http import HttpResponse, HttpResponseBadRequest
from server.models import PrivateUser


def register_user(request):
    private_registration = can_register_private(request)
    public_registration = can_register_public(request)
    if private_registration.status_code != 200:
        return private_registration
    if public_registration.status_code != 200:
        return public_registration
    return HttpResponse('OK')


def can_register_private(request):
    if request.method == 'POST' and 'username' in request.POST and 'password' in request.POST:
        if request.POST['username'] > 50:
            return HttpResponseBadRequest('username is too long - 50 symbols is limit')
        if request.POST['username'] in PrivateUser.username:
            return HttpResponseBadRequest('username is already in use')
        HttpResponse('OK')
    else:
        HttpResponseBadRequest('bad request format')
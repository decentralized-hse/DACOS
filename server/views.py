from django.shortcuts import render
from django.http import HttpResponse, HttpResponseBadRequest
from server.models import PrivateUser


"""
register user for both PrivateUser and PublicUser tables
"""
def register_user(request):
    private_registration = can_register_private(request)
    print(private_registration)
    public_registration = can_register_public(request)
    if private_registration.status_code != 200:
        return private_registration
    if public_registration.status_code != 200:

        return public_registration
    return HttpResponse('OK')


def can_register_private(request):
    if request.method == 'POST' and 'username' in request.POST and 'password' in request.POST:
        if len(request.POST['username']) > 50:
            return HttpResponseBadRequest('username is too long - 50 symbols is limit')
        if check_username(request.POST['username'], PrivateUser):
            return HttpResponseBadRequest('username is already in use')
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('bad request format')


"""
usertype is PublicUser or PrivateUser
returns True if table contains username
"""
def check_username(username, usertype):
    users = usertype.objects.values('username')
    for user in users:
        if username == user['username']:
            return True
    return False

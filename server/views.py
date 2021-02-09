from django.shortcuts import render
from django.http import HttpResponse,HttpResponseNotAllowed,HttpResponseBadRequest
from .models import PublicUser

# Create your views here.
def index(request):
    check_username('')
    return HttpResponse('Hello World!')


def can_register_public(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Invalid request type')
    if 'username' in request.POST  and 'public_rsa_n' in request.POST and'public_rsa_e' in request.POST and 'g_in_big_power' in request.POST:
        if len(request.POST.get('username')) > 50:
            return HttpResponseBadRequest('username is too long - 50 symbols is limit')
        if check_username(request.POST.get('username')):
            return HttpResponseBadRequest('username is already in use')
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('Not enough data')

def check_username(username):
    users = PublicUser.objects.values('username')
    for user in users:
        if username == user['username']:
            return True
    return False
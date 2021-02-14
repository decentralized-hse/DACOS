from django.shortcuts import render
from django.http import HttpResponse,HttpResponseNotAllowed,HttpResponseBadRequest, JsonResponse
from .models import PublicUser,Ticket
from django.core import serializers
import json as simplejson


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

def add_ticket(username,password):
    new_ticket = Ticket(usernameRSA = username,usernameAES = password)
    new_ticket.save()

def register_ticket(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Invalid request type')
    if 'usernameRSA' in request.POST  and 'usernameAES' in request.POST:
        username = request.POST.get('usernameRSA')
        password = request.POST.get('usernameAES')
        if(5 <= len(username) <= 300 and 5 <= len(password) <= 300):
            add_ticket(username,password)
            return HttpResponse('OK')
        else:
            return HttpResponseBadRequest('username or password is invalid length - 5 to 300 is optimal')
    else:
        return HttpResponseBadRequest('Not enough data')

def get_all_tickets(request):
    if(request.method == 'GET'):
        tickets = Ticket.objects.all()
        json_answer = simplejson.dumps([{'usernameRSA': ticket.usernameRSA} for ticket in tickets])
        return JsonResponse(json_answer, safe = False)
    return HttpResponseNotAllowed('Invalid request type')

def delete_ticket(request):
    if(request.method == 'POST'):
        try:
            Ticket.objects.filter(usernameAES = request.POST.get('usernameAES')).delete()
            return HttpResponse('OK')
        except:
            return HttpResponseBadRequest('there is no such user')
    return HttpResponseNotAllowed('Invalid request type')


    
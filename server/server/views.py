from argon2 import PasswordHasher
from django.http import HttpResponse, HttpResponseNotAllowed,HttpResponseBadRequest, JsonResponse
from .models import PublicUser, Ticket, PrivateUser, Block
import json as simplejson


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
    PrivateUser(username=request.POST['username'], password_hash=PasswordHasher().hash(request.POST['password'])).save()
    PublicUser(username=request.POST['username'], cell_id=1, public_rsa_n=request.POST['public_rsa_n'],
               public_rsa_e=request.POST['public_rsa_e'], g_in_big_power=request.POST['g_in_big_power']).save()
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


def can_register_public(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Invalid request type')
    if 'username' in request.POST and 'public_rsa_n' in request.POST and 'public_rsa_e' in request.POST and 'g_in_big_power' in request.POST:
        if len(request.POST.get('username')) > 50:
            return HttpResponseBadRequest('username is too long - 50 symbols is limit')
        if check_username(request.POST.get('username'), PublicUser):
            return HttpResponseBadRequest('username is already in use')
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('Not enough data')


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


def add_ticket(username, password):
    new_ticket = Ticket(usernameRSA=username, usernameAES=password)
    new_ticket.save()


def register_ticket(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Invalid request type')
    if 'usernameRSA' in request.POST and 'usernameAES' in request.POST:
        username = request.POST.get('usernameRSA')
        password = request.POST.get('usernameAES')
        if 5 <= len(username) <= 300 and 5 <= len(password) <= 300:
            add_ticket(username, password)
            return HttpResponse('OK')
        else:
            return HttpResponseBadRequest('username or password is invalid length - 5 to 300 is optimal')
    else:
        return HttpResponseBadRequest('Not enough data')


def get_all_tickets(request):
    if request.method == 'GET':
        tickets = Ticket.objects.all()
        json_answer = simplejson.dumps([{'usernameRSA': ticket.usernameRSA} for ticket in tickets])
        return JsonResponse(json_answer, safe=False)
    return HttpResponseNotAllowed('Invalid request type')


def delete_ticket(request):
    if request.method == 'POST':
        try:
            Ticket.objects.filter(usernameAES=request.POST.get('usernameAES')).delete()
            return HttpResponse('OK')
        except:
            return HttpResponseBadRequest('there is no such user')
    return HttpResponseNotAllowed('Invalid request type')


def write_log(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Invalid request type')
    if 'message' in request.POST:
        message = request.POST.get('message')
        log = Block.objects.latest('block').block
        id = Block.objects.latest('block').id
        if len(log) == 32:
            Block(block=[message,]).save()
        else:
            log.append(message)
            Block.objects.filter(id=id).update(block=log)
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('Not enough data')


def read_message(request):
    if request.method == 'GET' and 'block_number' in request.GET and request.GET['block_number'].isdigit():
        blocks = list(Block.objects.all().values_list('block', flat=True))
        print(blocks)
        block_number = int(request.GET['block_number'])
        if block_number >= len(blocks):
            return HttpResponseBadRequest('Wrong data')
        return JsonResponse(blocks[block_number:], safe=False)
    else:
        return HttpResponseBadRequest('Not enough data')


from argon2 import PasswordHasher
from django.http import HttpResponse, HttpResponseNotAllowed,HttpResponseBadRequest, JsonResponse
from .models import PublicUser, Ticket, PrivateUser, Block
import json as simplejson
from ast import literal_eval
from server.settings import *


def register_user(request):
    """
    register user for both PrivateUser and PublicUser tables
    """
    if request.method == 'POST' and 'username' in request.POST\
            and 'password' in request.POST and 'publicKey' in request.POST:
        public_key = simplejson.loads(request.POST['publicKey'])
        if len(request.POST['username']) > 50:
            return HttpResponseBadRequest('Username is too long - 50 symbols is limit.')
        if check_username(request.POST['username'], PrivateUser) or\
                check_username(request.POST['username'], PublicUser):
            return HttpResponseBadRequest('Username is already in use.')
        print(len(public_key))
        if not isinstance(public_key, list) or len(public_key) != 32:
            return HttpResponseBadRequest('Format of public key is bad.')
    else:
        return HttpResponseBadRequest('Bad request format.')

    PrivateUser(username=request.POST['username'], password_hash=PasswordHasher().hash(request.POST['password'])).save()
    # TODO: replace cell_id=1 with actual cell ids.
    PublicUser(username=request.POST['username'], cell_id=1, public_key=public_key).save()
    return HttpResponse('OK')


def check_username(username, usertype):
    """
    usertype is PublicUser or PrivateUser
    returns True if table contains username
    """
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
        if len(log) == global_settings('BLOCK_SIZE'):
            Block(block=[message]).save()
        else:
            log.append(message)
            Block.objects.filter(id=id).update(block=log)
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('Not enough data')


def read_message(request):
    if request.method == 'GET' and 'block_number' in request.GET and request.GET['block_number'].isdigit():
        blocks = list(Block.objects.all().values_list('block', flat=True))
        if len(blocks[-1]) < global_settings('BLOCK_SIZE'):
            blocks = blocks[:-1]
        block_number = int(request.GET['block_number'])
        if block_number > len(blocks):
            return JsonResponse([], safe=False)
        return JsonResponse(blocks[block_number:], safe=False)
    else:
        return HttpResponseBadRequest('Not enough data')


from argon2 import PasswordHasher
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from .models import PublicUser, PrivateUser, Block
import json as simplejson
from ast import literal_eval
from server.settings import *


# TODO: add get all users, to give all info for creating Chat in android.


def register_user(request):
    """
    register user for both PrivateUser and PublicUser tables
    """
    if request.method == 'POST' and 'username' in request.POST \
            and 'password' in request.POST and 'publicKey' in request.POST:
        public_key = simplejson.loads(request.POST['publicKey'])
        if len(request.POST['username']) > 50:
            return HttpResponseBadRequest('Username is too long - 50 symbols is limit.')
        if check_username(request.POST['username'], PrivateUser) or \
                check_username(request.POST['username'], PublicUser):
            return HttpResponseBadRequest('Username is already in use.')
        print(len(public_key))
        if not isinstance(public_key, list) or len(public_key) != 32:
            return HttpResponseBadRequest('Format of public key is bad.')
    else:
        return HttpResponseBadRequest('Bad request format.')

    PrivateUser(username=request.POST['username'], password_hash=PasswordHasher().hash(request.POST['password'])).save()
    PublicUser(username=request.POST['username'], public_key=public_key).save()
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


def get_users(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Not enough data')
    # all_users is list of tuples.
    all_users = list(PublicUser.objects.all().values_list('username', 'public_key'))
    return HttpResponse(simplejson.
                        dumps([{'username': user[0], 'public_key': user[1]} for user in all_users]))

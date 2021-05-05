from argon2 import PasswordHasher
from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from .models import PublicUser, PrivateUser, Block, Server
import json as simplejson
from ast import literal_eval
from server.settings import *


# TODO: Service crashes if there is no blocks at all and someone tries to read them.


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


def write_msg(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Wrong request type')
    if 'message' in request.POST:
        message = request.POST.get('message')
        last_block = Block.objects.latest('block').block
        id = Block.objects.latest('block').id
        if len(last_block) >= global_settings('BLOCK_SIZE'):
            Block(block=[message]).save()
        else:
            last_block.append(message)
            Block.objects.filter(id=id).update(block=last_block)
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
        return HttpResponseBadRequest('Wrong request type')
    # all_users is list of tuples.
    all_users = list(PublicUser.objects.all().values_list('username', 'public_key'))
    return HttpResponse(simplejson.
                        dumps([{'username': user[0], 'public_key': user[1]} for user in all_users]))


def add_blocks(request):
    # TODO: untested and currently unused! So should be tested on adding client functionality
    if request.method != 'POST':
        return HttpResponseBadRequest('Wrong request type')
    new_blocks = request.POST.get('new_blocks')
    if new_blocks is None or not isinstance(new_blocks, list) or len(new_blocks) == 0:
        return HttpResponseBadRequest('Not enough data')
    for block in new_blocks:
        if not isinstance(block, list):
            return HttpResponseBadRequest('Not enough data')
        for item in block:
            if not isinstance(item, int):
                return HttpResponseBadRequest('Not enough data')
        if len(block) != global_settings('BLOCK_SIZE'):
            return HttpResponseBadRequest('Not enough data')
    last_block = Block.objects.latest('block')
    if len(last_block.block) >= global_settings('BLOCK_SIZE'):
        for block in new_blocks:
            Block(block=block).save()
    else:
        last_data = last_block.block
        Block.objects.filter(id=last_block.id).update(block=new_blocks[0])
        for block in new_blocks[1:]:
            Block(block=block).save()
        Block(block=last_data).save()
    return HttpResponse('OK')


def get_servers(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Wrong request type')
    return JsonResponse({ 'servers': list(Server.objects.all().values_list('url', flat=True))}, safe=False)

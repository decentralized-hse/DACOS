from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from .models import PublicUser, Server, ServerKeys, EncodedMessage
import json as simplejson
import requests
from dotenv import load_dotenv
import rsa
import os
from ast import literal_eval


# Generate RSA if there is none.
keys = list(ServerKeys.objects.all())
key = None
if len(keys) == 0:
    (pub_key, private_key) = rsa.newkeys(2048)
    pub_key = pub_key._save_pkcs1_pem().decode("utf-8")
    private_key = private_key._save_pkcs1_pem().decode("utf-8")
    key = ServerKeys(public_key=pub_key, private_key=private_key)
    key.save()
else:
    key = keys[0]
print(key.private_key)
SERVER_PRIVATE_KEY = rsa.PrivateKey._load_pkcs1_pem(key.private_key)
# Update servers list.
if os.getenv('THIS_SERVER') == os.getenv('ADMIN_SERVER'):
    try:
        Server.objects.get(url=os.getenv('THIS_SERVER'))
    except Server.DoesNotExist:
        serv = Server(url=os.getenv('THIS_SERVER'), public_key=key.public_key)
        serv.save()
else:
    requests.post(os.getenv('ADMIN_SERVER') + '/add_server', {'url': os.getenv('THIS_SERVER'),
                                                              'public_key': key.public_key})


def register_user(request):
    if request.method == 'POST' and 'username' in request.POST \
            and 'server' in request.POST and 'public_key' in request.POST:

        if not valid_username(request.POST['username']):
            return HttpResponseBadRequest('Username is already in use.')
        if server_exists(request.POST['server']):
            user = PublicUser(username=request.POST['username'], public_key=request.POST['public_key'],
                              register_server=request.POST['server'])
            user.save()
            # Registering user everywhere.
            for server in Server.objects.all().iterator():
                if server.url != os.getenv('THIS_SERVER'):
                    requests.post(server.url + '/register_user', {'username': user.username,
                                                                  'server': user.register_server,
                                                                  'public_key': user.public_key})
        else:
            return HttpResponseBadRequest('No requested server.')

    else:
        return HttpResponseBadRequest('Bad request format.')
    return HttpResponse('OK')


def valid_username(username):
    users = PublicUser.objects.values('username')
    for user in users:
        if username == user['username']:
            return False
    return True


def server_exists(url):
    servers = Server.objects.values('url')
    for server in servers:
        if url == server['url']:
            return True
    return False


def write_msg(request):
    if request.method != 'POST':
        return HttpResponseNotAllowed('Wrong request type')
    # if 'message' in request.POST:
    #     load_dotenv()
    #     message = request.POST.get('message')
    #     # Decrypting message:
    #     skey = PrivateKey(bytes.fromhex(os.getenv('PRIVATE_KEY')))
    #     unsealed_box = SealedBox(skey)
    #     message = unsealed_box.decrypt(bytes.fromhex(message)).decode('utf-8')
    #     # See if we should pass message to another server
    #     if "∫" == message[0]:
    #         return send_further(request, message)
    #     last_block = Block.objects.latest('block').block
    #     id = Block.objects.latest('block').id
    #     if len(last_block) >= global_settings('BLOCK_SIZE'):
    #         Block(block=[message]).save()
    #     else:
    #         last_block.append(message)
    #         Block.objects.filter(id=id).update(block=last_block)
    #     return HttpResponse('OK')
    # else:
    #     return HttpResponseBadRequest('Not enough data')


def send_further(request, message):
    for i in range(1, len(message)):
        if message[i] == "∫":
            end = i
            break
    server_id = int(message[1:end])
    server = Server.objects.filter(id=server_id)
    if len(server) != 1:
        return HttpResponseBadRequest(f'There is no such server id:{server_id}')
    requests.post(server.url + '/write_msg', {'message': message[end + 1:]})
    return HttpResponse('OK')


def read_message(request):
    if request.method == 'GET' and 'block_number' in request.GET and request.GET['block_number'].isdigit():
        # if len(blocks[-1]) < global_settings('BLOCK_SIZE'):
        #     blocks = blocks[:-1]
        # block_number = int(request.GET['block_number'])
        # if block_number > len(blocks):
        #     return JsonResponse([], safe=False)
        return JsonResponse('ew', safe=False)
    else:
        return HttpResponseBadRequest('Not enough data')


def get_users(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Wrong request type')
    # Get all users in a list of dicts.
    users_list = []
    for user in PublicUser.objects.all().iterator():
        users_list.append({'username': user.username, 'public_key': user.public_key,
                           'register_server': user.register_server})
    return JsonResponse({'users': users_list}, safe=False)


def add_blocks(request):
    # TODO: untested and currently unused! So should be tested on adding client functionality
    # if request.method != 'POST':
    #     return HttpResponseBadRequest('Wrong request type')
    # new_blocks = request.POST.get('new_blocks')
    # if new_blocks is None or not isinstance(new_blocks, list) or len(new_blocks) == 0:
    #     return HttpResponseBadRequest('Not enough data')
    # for block in new_blocks:
    #     if not isinstance(block, list):
    #         return HttpResponseBadRequest('Not enough data')
    #     for item in block:
    #         if not isinstance(item, int):
    #             return HttpResponseBadRequest('Not enough data')
    #     if len(block) != global_settings('BLOCK_SIZE'):
    #         return HttpResponseBadRequest('Not enough data')
    # last_block = Block.objects.latest('block')
    # if len(last_block.block) >= global_settings('BLOCK_SIZE'):
    #     for block in new_blocks:
    #         Block(block=block).save()
    # else:
    #     last_data = last_block.block
    #     Block.objects.filter(id=last_block.id).update(block=new_blocks[0])
    #     for block in new_blocks[1:]:
    #         Block(block=block).save()
    #     Block(block=last_data).save()
    return HttpResponse('OK')


def get_servers(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Wrong request type')
    server_list = []
    for serv in Server.objects.all().iterator():
        server_list.append({'url': serv.url, 'public_key': serv.public_key})
    return JsonResponse({'servers': server_list}, safe=False)


def add_server(request):
    if request.method != 'POST':
        return HttpResponseBadRequest('Wrong request type')
    if request.POST.get('url') is None:
        return HttpResponseBadRequest('URL is undefined')
    if request.POST.get('public_key') is None:
        return HttpResponseBadRequest('public_key is undefined')
    if len(Server.objects.filter(url=request.POST.get('url'))) != 0:
        return HttpResponseBadRequest('There is already such server')
    Server(url=request.POST.get('url'), public_key=request.POST.get('public_key')).save()
    return HttpResponse('OK')

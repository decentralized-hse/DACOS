import json

from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from .models import PublicUser, Server, ServerKeys, EncodedMessage
import requests
import rsa
import os


try:
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
    SERVER_PRIVATE_KEY = rsa.PrivateKey._load_pkcs1_pem(key.private_key)
    # Update servers list.
    if os.getenv('THIS_SERVER') == os.getenv('ADMIN_SERVER'):
        try:
            Server.objects.get(url=os.getenv('THIS_SERVER'))
        except Server.DoesNotExist:
            serv = Server(url=os.getenv('THIS_SERVER'), public_key=key.public_key)
            serv.save()
    else:
        requests.post(os.getenv('ADMIN_SERVER') + '/servers/add', {'url': os.getenv('THIS_SERVER'),
                                                                   'public_key': key.public_key})
except:
    # In case we are running migrations we would fall here.
    # So we are not doing anything.
    pass


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
    content = request.body.decode('utf-8')
    content = json.loads(content)
    print(content)
    if 'message' in content:
        enc_message = content['message']
        # Decrypting message:
        message = ''
        try:
            message = rsa.decrypt(enc_message.encode('utf-8'), SERVER_PRIVATE_KEY)
            message = message.decode('utf8')
            print('decoded: ', message)
        except rsa.DecryptionError:
            message = EncodedMessage(text=enc_message)
            message.save()
            return HttpResponse('OK')
        # chr(169) == ©.
        split = message.partition(chr(169))
        if split[1] == '':
            message = EncodedMessage(text=enc_message)
            message.save()
            return HttpResponse('OK')
        try:
            server = Server.objects.get(url=split[0])
            requests.post(server.url + '/messages/write', {'message': split[2]})
        except Server.DoesNotExist:
            message = EncodedMessage(text=enc_message)
            message.save()
            return HttpResponse('OK')
        return HttpResponse('OK')
    else:
        return HttpResponseBadRequest('Not enough data')


def read_message(request):
    if request.method == 'GET' and 'block_number' in request.GET and request.GET['block_number'].isdigit():
        block_number = int(request.GET['block_number'])
        # Test if this formulae works.
        messages = EncodedMessage.objects.all().values_list('text', flat=True).distinct()
        print(messages)
        messages = messages[block_number * int(os.getenv('BLOCK_SIZE')):
                            messages_size - messages_size % int(os.getenv('BLOCK_SIZE'))]
        return JsonResponse({'messages': messages, 'block_number': messages_size // int(os.getenv('BLOCK_SIZE'))},
                            safe=False)
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
    content = request.body.decode('utf-8')
    content = json.loads(content)
    if content.get('url') is None:
        return HttpResponseBadRequest('URL is undefined')
    if content.get('public_key') is None:
        return HttpResponseBadRequest('public_key is undefined')
    if len(Server.objects.filter(url=content['url'])) != 0:
        return HttpResponseBadRequest('There is already such server')
    Server(url=request.POST.get('url'), public_key=content['public_key']).save()
    return HttpResponse('OK')


def register_user_once(request):
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
        else:
            return HttpResponseBadRequest('No requested server.')

    else:
        return HttpResponseBadRequest('Bad request format.')
    return HttpResponse('OK')
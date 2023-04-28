import json

from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import Server, EncodedMessage
import requests
import rsa


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
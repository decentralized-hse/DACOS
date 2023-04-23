from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import Server


def get_servers(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Wrong request type')
    server_list = []
    for serv in Server.objects.all().iterator():
        server_list.append({'url': serv.url, 'public_key': serv.public_key})
    return JsonResponse({'servers': server_list}, safe=False)
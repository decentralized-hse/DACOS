from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import PublicUser


def get_users(request):
    if request.method != 'GET':
        return HttpResponseBadRequest('Wrong request type')
    # Get all users in a list of dicts.
    users_list = []
    for user in PublicUser.objects.all().iterator():
        users_list.append({'username': user.username, 'public_key': user.public_key,
                           'register_server': user.register_server})
    return JsonResponse({'users': users_list}, safe=False)
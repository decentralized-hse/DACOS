from django.http import HttpResponse, HttpResponseNotAllowed, HttpResponseBadRequest, JsonResponse
from ..models import EncodedMessage
import os


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
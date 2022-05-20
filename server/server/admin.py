from django.contrib import admin
from .models import PublicUser, EncodedMessage, Server, ServerKeys

admin.site.register(PublicUser)
admin.site.register(EncodedMessage)
admin.site.register(Server)
admin.site.register(ServerKeys)

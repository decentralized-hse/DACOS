from django.contrib import admin
from .models import PublicUser, PrivateUser, Block

admin.site.register(PublicUser)
admin.site.register(PrivateUser)
admin.site.register(Block)
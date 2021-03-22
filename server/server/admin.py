from django.contrib import admin
from .models import PublicUser, Ticket, PrivateUser, Block
# Register your models here.

admin.site.register(PublicUser)
admin.site.register(PrivateUser)

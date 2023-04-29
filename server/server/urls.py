from django.urls import path

from views import register_user
from views import register_user_once
from views import write_msg
from views import get_users
from views import read_message
from views import get_servers
from views import add_server

urlpatterns = [
    path('users/register', register_user.register_user),
    path('users/register/once', register_user_once.register_user_once),
    path('messages/write', write_msg.write_msg),
    path('users', get_users.get_users),
    path('messages/get', read_message.read_message),
    path('servers', get_servers.get_servers),
    path('servers/add', add_server.add_server)
]

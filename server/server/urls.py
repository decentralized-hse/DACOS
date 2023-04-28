from django.urls import path

from server.logic import register_user
from server.logic import register_user_once
from server.logic import write_msg
from server.logic import get_users
from server.logic import read_message
from server.logic import get_servers
from server.logic import add_server

urlpatterns = [
    path('users/register', register_user.register_user),
    path('users/register/once', register_user_once.register_user_once),
    path('messages/write', write_msg.write_msg),
    path('users', get_users.get_users),
    path('messages/get', read_message.read_message),
    path('servers', get_servers.get_servers),
    path('servers/add', add_server.add_server)
]

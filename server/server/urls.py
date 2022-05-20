from django.urls import path
from . import views

urlpatterns = [
    path('users/register', views.register_user),
    path('users/register/once', views.register_user_once),
    path('messages/write', views.write_msg),
    path('users', views.get_users),
    path('messages/get', views.read_message),
    path('servers', views.get_servers),
    path('servers/add', views.add_server)
]

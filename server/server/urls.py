from django.urls import path
from . import views

urlpatterns = [
    path('register', views.register_user),
    path('write_msg', views.write_msg),
    path('get_users', views.get_users),
    path('read_message', views.read_message)
]

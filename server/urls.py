from django.urls import path, include
from . import views
from django.conf.urls import url

urlpatterns = [
    path('register', views.register_user)
]
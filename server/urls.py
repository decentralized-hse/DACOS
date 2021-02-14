from django.urls import path, include
from . import views
from django.conf.urls import url

urlpatterns = [
    path('', views.index),
    path('delete_ticket', views.delete_ticket),
    path('add_ticket', views.register_ticket),
    path('get_tickets', views.get_all_tickets),
]
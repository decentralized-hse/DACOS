from django.urls import path
from . import views

urlpatterns = [
    path('register', views.register_user),
    path('delete_ticket', views.delete_ticket),
    path('add_ticket', views.register_ticket),
    path('get_tickets', views.get_all_tickets),
    path('write_msg', views.write_log),
]
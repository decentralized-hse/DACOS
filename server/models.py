from django.db import models


class PublicUser(models.Model):
    username = models.CharField(max_length = 50)
    cell_id = models.IntegerField()
    public_rsa_n = models.CharField(max_length = 300)
    public_rsa_e = models.CharField(max_length = 300)
    g_in_big_power = models.CharField(max_length = 300)


class PrivateUser(models.Model):
    username = models.CharField('Ник пользователя', max_length=50)
    password_hash = models.CharField('Хэш пароля пользователя', max_length=77)
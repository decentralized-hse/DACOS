from django.db import models
from django.contrib.postgres.fields import ArrayField


class PublicUser(models.Model):
    username = models.CharField(max_length=50)
    cell_id = models.IntegerField()
    public_rsa_n = models.CharField(max_length=300)
    public_rsa_e = models.CharField(max_length=300)
    g_in_big_power = models.CharField(max_length=300)


class PrivateUser(models.Model):
    username = models.CharField('Ник пользователя', max_length=50)
    password_hash = models.CharField('Хэш пароля пользователя', max_length=77)


class Ticket(models.Model):
    usernameRSA = models.CharField(max_length=300)
    usernameAES = models.CharField(max_length=300)


class Block(models.Model):
    block = ArrayField(models.CharField(max_length=500), size = 32)
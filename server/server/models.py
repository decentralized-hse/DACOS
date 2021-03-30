from django.db import models
from django.contrib.postgres.fields import ArrayField


class PublicUser(models.Model):
    username = models.CharField(max_length=50)
    # TODO: не нужно ли вообще убрать cell_id? Возможно это остаток от прошлых задумок сервера.
    cell_id = models.IntegerField()
    public_key = ArrayField(models.IntegerField())


class PrivateUser(models.Model):
    username = models.CharField('Ник пользователя', max_length=50)
    password_hash = models.CharField('Хэш пароля пользователя', max_length=77)


class Ticket(models.Model):
    usernameRSA = models.CharField(max_length=300)
    usernameAES = models.CharField(max_length=300)


class Block(models.Model):
    block = ArrayField(models.CharField(max_length=500), size=32)

from django.db import models
from django.contrib.postgres.fields import ArrayField


class PublicUser(models.Model):
    username = models.CharField(help_text='Ник пользователя, дублирован в PrivateUser', max_length=50)
    # TODO: Maybe should start containing public key as hex string, not as byte array.
    public_key = ArrayField(models.IntegerField(),
                            help_text='Публичный ключ пользователя, используется для шифрования сообщения конкретному'
                                      ' пользователю. Применяется криптография на эллиптических кривых. Хранится в виде'
                                      ' массива байт длинной 32.',)


class PrivateUser(models.Model):
    username = models.CharField(help_text='Ник пользователя, дублирован в PublicUser', max_length=50)
    password_hash = models.CharField(help_text='Хэш пароля пользователя для проверки авторизации', max_length=77)


class Block(models.Model):
    block = ArrayField(models.CharField(max_length=500), size=32,
                       help_text='Блоки зашифрованных сообщений, размер блока можно поменять в settings.py '
                                 '(максимальный размер блока - 32).')


class Server(models.Model):
    url = models.CharField(help_text="Url + port сервера, на котором также запущен сервер DACOS.", max_length=100)

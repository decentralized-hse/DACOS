from django.db import models
from django.contrib.postgres.fields import ArrayField


class PublicUser(models.Model):
    username = models.TextField(help_text='Ник пользователя, дублирован в PrivateUser')
    public_key = models.TextField(help_text='Публичный ключ пользователя, используется для шифрования'
                                            ' сообщения конкретному'
                                            ' пользователю. Применяется RSA 2048')
    register_server = models.TextField(help_text='Адрес сервера на котором пользователь зарегистрирован.'
                                                 ' Хранится в виде address:port')


class EncodedMessage(models.Model):
    models.TextField('')
    text = ArrayField(models.CharField(max_length=500), size=32,
                      help_text='Текст зашифрованного сообщения.')


class Server(models.Model):
    url = models.CharField(help_text="Url:port сервера, на котором также запущен сервер DACOS.", max_length=100)
    public_key = models.TextField(help_text='Публичный ключ сервера, используется для шифрования'
                                            ' сообщения для пересылки на сервер. Применяется RSA 2048')
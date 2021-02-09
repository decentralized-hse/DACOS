from django.db import models


class PrivateUser(models.Model):
    username = models.CharField('Ник пользователя', max_length=50)
    password_hash = models.CharField('Хэш пароля пользователя', max_length=77)
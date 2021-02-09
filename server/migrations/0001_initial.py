    # Generated by Django 3.1.6 on 2021-02-09 18:51

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='PrivateUser',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('username', models.CharField(max_length=50, verbose_name='Ник пользователя')),
                ('password_hash', models.CharField(max_length=77, verbose_name='Хэш пароля пользователя')),
            ],
        ),
    ]

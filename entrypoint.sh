#!/bin/bash

echo "Apply database migrations"
python manage.py makemigrations;
python manage.py migrate;


gunicorn --bind 0.0.0.0:$PORT DACOS.wsgi

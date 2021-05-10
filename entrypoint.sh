#!/bin/bash

echo "Apply database migrations"
python manage.py makemigrations;
python manage.py migrate;


gunicorn --bind 0.0.0.0:8080 DACOS.wsgi

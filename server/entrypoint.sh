#!/bin/bash

echo "Apply database migrations"
python3.9 manage.py makemigrations;
echo "Made migrations"
python3.9 manage.py migrate;
echo "Migrated"


gunicorn --bind 0.0.0.0:8000 DACOS.wsgi --workers=4
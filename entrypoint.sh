#!/bin/bash

docker build -t dacos -f Dockerfile .
docker run dacos
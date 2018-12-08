#!/bin/sh
docker image rm --force postgres-sample
docker build -t postgres-sample -f "Dockerfile-postgres-sample" .
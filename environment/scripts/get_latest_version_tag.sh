#!/usr/bin/env bash

aws ecr describe-images --repository-name offer-service --image-ids "imageTag=latest" | jq -r '.imageDetails[0].imageTags[] | select(. != "latest") | select(. | contains(".") | not)'

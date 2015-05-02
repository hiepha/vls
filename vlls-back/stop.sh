#!/bin/bash
vlls_back_id=$(ps aux | grep '[j]ava -jar build/libs/vlls-back' | awk '{print $2}')
if test -n "$vlls_back_id"; then
    curl -X POST localhost:8080/vlls-back/godown
    sleep 6

    vlls_back_id=$(ps aux | grep '[j]ava -jar build/libs/vlls-back' | awk '{print $2}')
    if test -n "$vlls_back_id"; then
        curl -X POST localhost:8080/vlls-back/godown
    fi;
fi;
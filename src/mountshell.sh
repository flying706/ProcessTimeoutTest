#!/bin/bash 
echo "local directory: "$1
echo "remote directory:"$2
echo "process now begin..."

echo "msg:mount -t nfs "$2" "$1"..."
mount -t nfs $2 $1

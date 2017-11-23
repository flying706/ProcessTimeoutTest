#!/bin/bash 
#mount remote directory
#demo:  ./mountshell.sh /data/data8 192.168.1.173:/testspeed 192.168.1.173:/data/data8/.
echo "local directory: "$1
echo "remote directory:"$2
#echo "scp source: "$3
echo "process now begin..."

echo "msg:umount "$1"..."
umount $1

echo "msg:mount -t nfs "$2" "$1"..."
mount -t nfs $2 $1

echo "msg:start edit /etc/fstab file..."
temp=`echo $1|sed 's/\//\\\\\//g'`
sed -i -r "s/(.*?$temp.*?)/#\1/" /etc/fstab
sed -i "\$a $2 $1 nfs default 1 0" /etc/fstab

#echo "msg:rm -rf $1/* .."
#rm -rf $1/*

#echo "msg:scp -r "$3" "$1
#scp -r $3 $1
echo "end"

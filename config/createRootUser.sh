#!/bin/bash
 
username=$1
pass=$2

sudo useradd $username
 
echo ${pass} | sudo passwd ${username} --stdin  &>/dev/null
 
echo "${username} ALL=(ALL:ALL) ALL" >> /etc/sudoers

echo "${username}  ALL=(ALL) NOPASSWD: ALL" >> /etc/sudoers

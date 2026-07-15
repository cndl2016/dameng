#!/bin/bash
# sh write_file.sh port 9002 /opt/file.ini
#sed -i '/^port/c port 9002' file.txt #替换 port 开头的 某一行
#sed -n '/^port/p' file #仅列出port开头的行?echo $1 $2 $3
# $1 key $2 value $3 文件地址
# 删除文件地址为$3，$1开头的行

sed -i /^"$1 "/d $3
#文件末尾添加内容

echo >> $3

echo $1 $2 >> $3

sed -i "s/\]/ /g" $3

sed -i "s/\[/ /g" $3

sed -i "s/\:space:/ /g" $3

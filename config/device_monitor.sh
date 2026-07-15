#!/bin/bash
# sh test_monitor.sh 10.14.1.127
#脚本功能描述：依据/proc/stat文件获取并计算CPU使用率
#echo "CPU 使用情况："
#top -n 1 | grep "Cpu(s)" | awk '{print $2+$4}'
#echo "内存使用情况："
#free -m | grep Mem | awk '{print $3/$2 * 100.0}'
#CPU时间计算公式：CPU_TIME=user+system+nice+idle+iowait+irq+softirq
#CPU使用率计算公式：cpu_usage=(idle2-idle1)/(cpu2-cpu1)*100
#默认时间间隔 s
TIME_INTERVAL=1
LAST_CPU_INFO=$(cat /proc/stat | grep -w cpu | awk '{print $2,$3,$4,$5,$6,$7,$8}')
LAST_SYS_IDLE=$(echo $LAST_CPU_INFO | awk '{print $4}')
LAST_TOTAL_CPU_T=$(echo $LAST_CPU_INFO | awk '{print $1+$2+$3+$4+$5+$6+$7}')

deviceIp=$1
interfaces=`/usr/sbin/ip l | awk -F":"  '/^[^ ]/{print $2}'| awk '{print $1}'`
#interfaces=`cat /proc/net/dev | sed -e "s/\(.*\)\:\(.*\)/\1/g"  | awk '{print $1 }' |tail -n +3`
for interface in ${interfaces[*]};do
     rs=`/usr/sbin/ip a show $interface| grep $deviceIp`
     if [ -n "$rs"   ];then
#         echo $interface
         break
     fi
done
ethn=$interface
export TERM=xterm
RX_pre=$(cat /proc/net/dev | grep -w $ethn | sed 's/:/ /g' | awk '{print $2}')
TX_pre=$(cat /proc/net/dev | grep -w $ethn | sed 's/:/ /g' | awk '{print $10}')
sleep ${TIME_INTERVAL}
NEXT_CPU_INFO=$(cat /proc/stat | grep -w cpu | awk '{print $2,$3,$4,$5,$6,$7,$8}')
NEXT_SYS_IDLE=$(echo $NEXT_CPU_INFO | awk '{print $4}')
NEXT_TOTAL_CPU_T=$(echo $NEXT_CPU_INFO | awk '{print $1+$2+$3+$4+$5+$6+$7}')

#系统空闲时间
SYSTEM_IDLE=`echo ${NEXT_SYS_IDLE} ${LAST_SYS_IDLE} | awk '{print $1-$2}'`
#CPU总时间
TOTAL_TIME=`echo ${NEXT_TOTAL_CPU_T} ${LAST_TOTAL_CPU_T} | awk '{print $1-$2}'`
CPU_USAGE=`echo ${SYSTEM_IDLE} ${TOTAL_TIME} | awk '{printf "%.2f", 100-$1/$2*100}'`
echo "CPU_USE:${CPU_USAGE}" #%
#echo "CPU_CORE:" `lscpu |grep '^CPU(s)' | head -n 1 | awk '{print $2 }'`
echo "CPU_CORE:" `nproc`
#获取内存使用率的脚本
memoryUsed=`free -m | sed -n '2p' | awk '{printf "%.2f\n",($3)/$2*100}'`
echo "MEM_USE:${memoryUsed}" # %
echo "MEM_TOTAL:"`free -m | sed -n '2p' | awk '{printf "%.2f\n",($2)/1024}'`
echo "MEM_USED:"`free -m | sed -n '2p' | awk '{printf "%.2f\n",($3)/1024}'`
# MEM_FREE= free+available
free -m | sed -n '2p' | awk '{printf "MEM_FREE:"  "%.2f\n",  ($4+$6)/1024}'
#获取网络IO的脚本
RX_next=$(cat /proc/net/dev | grep -w $ethn | sed 's/:/ /g' | awk '{print $2}')
TX_next=$(cat /proc/net/dev | grep -w $ethn | sed 's/:/ /g' | awk '{print $10}')
RX=$((${RX_next}-${RX_pre}))
TX=$((${TX_next}-${TX_pre}))
echo "TRAFFIC_IN:$RX" #KByte/s
echo "TRAFFIC_OUT:$TX" #KByte/s
#  if [[ $RX -lt 1024 ]];then
#    RX="${RX}B/s"
#  elif [[ $RX -gt 1048576 ]];then
#    RX=$(echo $RX | awk '{print $1/1048576 "MB/s"}')
#  else
#    RX=$(echo $RX | awk '{print $1/1024 "KB/s"}')
#  fi
#
#  if [[ $TX -lt 1024 ]];then
#    TX="${TX}B/s"
#  elif [[ $TX -gt 1048576 ]];then
#    TX=$(echo $TX | awk '{print $1/1048576 "MB/s"}')
#  else
#    TX=$(echo $TX | awk '{print $1/1024 "KB/s"}')
#  fi
#
#  echo -e "$ethn \t $RX   $TX "
#获取磁盘使用率的脚本
diskUsed=`df |awk '{a+=$3}END{print a}'`
diskTotal=`df |awk '{a+=$2}END{print a}'`
awk 'BEGIN{printf "DISK_USED:" "%.2f\n",('$diskUsed'/'$diskTotal')*100}' #%

get_host_disk_io() {
    if ! command -v iostat &> /dev/null; then
       echo "[]"
       return 1
    fi
    disks_io_str="$(iostat -dxk 3 2 | awk '/Device/{c++; if(c==2) {print; flag=1; next}} flag' | awk 'NR==1 {for (i=1; i<=NF; i++) {if ($i ~ /^Device/) device=i; if ($i=="r/s") rs=i; if ($i=="rkB/s") rkb=i; if ($i=="r_await") r_await=i; if ($i=="w/s") ws=i; if ($i=="wkB/s") wkb=i; if ($i=="w_await") w_await=i; if ($i=="%util") util=i}} NR>1 {print $device, $rs, $rkb, $r_await, $ws, $wkb, $w_await, $util}')"
    declare -A devices
    # 读取 disks_io_info
    while IFS= read -r line; do
       if [[ $line =~ ^[[:alnum:]] ]]; then
          device="$(echo $line | awk '{print $1}')"
          r_iops="$(echo "$line" | awk -F ' ' '{print $2}')"
          r_kb="$(echo "$line" | awk -F ' ' '{print $3}')"
          r_wait="$(echo "$line" | awk -F ' ' '{print $4}')"
          w_iops="$(echo "$line" | awk -F ' ' '{print $5}')"
          w_kb="$(echo "$line" | awk -F ' ' '{print $6}')"
          w_wait="$(echo "$line" | awk -F ' ' '{print $7}')"
          util="$(echo "$line" | awk -F ' ' '{print $8}')"
          devices[$device]=$(echo "{\"diskName\": \"$device\", \"readIops\": \"$r_iops\", \"readKb\": \"$r_kb\", \"readWait\": \"$r_wait\", \"writeIops\": \"$w_iops\", \"writeKb\": \"$w_kb\", \"writeWait\": \"$w_wait\", \"util\": \"$util\"}")
       fi
    done <<< "$(echo "$disks_io_str")"
    # 序列化成列表
    disk_io_list="["
    for disk_device in "${!devices[@]}"; do
       disk_io_list="$disk_io_list${devices[$disk_device]}, "
    done
    disk_io_list=${disk_io_list%, }"]"
    echo "$disk_io_list"
    return 0
}
results+=("$(get_host_disk_io &)")
echo $results

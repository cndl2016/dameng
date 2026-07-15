#!/bin/bash  
toml_file="$3"
key="$1"
value="$2"
type="$4"
key_value_pattern="^${key} = "
security="$5"
security_pattern="^\\[${security}\\]$"
# 检查key是否已存在
if grep -q "${key_value_pattern}" "$toml_file"; then
	# 如果存在，则使用sed替换value
	if [[ $type == "str" ]]; then
		sed -i "s%^\(${key}[[:space:]]*=\).*$%${key} = \"${value}\"%" $toml_file
	else
		sed -i "s%^\(${key}[[:space:]]*=\).*$%${key} = ${value}%" $toml_file
	fi

fi	

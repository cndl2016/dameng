#!/bin/bash
echo "请输入要加密的明文:"
read pwd
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="${pwd}" password=manager algorithm=PBEWithMD5AndDES

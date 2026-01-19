# Java-Aes-encryption
加解密字符串 使用aes-128-cbc aes-192-cbc aes-256-cbc
对称加解密
### /帮助
> Usage: java AESUtil -K <hexKey> -iv <hexIv> <-e|-d> <data>
  -K <hexKey> AES key in hex format (required, 16/24/32 bytes)
  -iv <hexIv> Initialization vector in hex format (required, 16 bytes)
  -e: encrypt mode
  -d: decrypt mode
Example (encrypt): java AESUtil -K "hexKey" -iv "hexIv" -e "plaintext"
Example (decrypt): java AESUtil -K "hexKey" -iv "hexIv" -d "ciphertext"

### 在android上使用
#### 加密
> dalvikvm -cp aes.dex AESUtil -K hexkey -iv hexvi -e "data"
#### 解密
> dalvikvm -cp aes.dex AESUtil -K hexkey -iv hexvi -d "base64data"

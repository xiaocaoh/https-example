## References
- [How To Create a Self-Signed SSL Certificate for Nginx in Ubuntu 16.04](https://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-nginx-in-ubuntu-16-04)
- [self-signed-certificate-with-custom-ca.md](https://gist.github.com/fntlnz/cf14feb5a46b2eda428e000157447309)
- [OpenSSL Essentials: Working with SSL Certificates, Private Keys and CSRs](https://www.digitalocean.com/community/tutorials/openssl-essentials-working-with-ssl-certificates-private-keys-and-csrs)
- [Do web browsers cache SSL certificates?](https://superuser.com/questions/390664/do-web-browsers-cache-ssl-certificates)

## MISC
Command to import the Root certificate:
```
keytool -import -v -trustcacerts \
-alias gce-alias -file gce.cer \
-keystore cacerts.jks -keypass changeit \
-storepass changeit
```

Command to get the certificate from a HTTPS site:
```
openssl s_client -connect www.baidu.com:443 </dev/null 2>/dev/null | openssl x509 -outform DER > baidu.cer
```

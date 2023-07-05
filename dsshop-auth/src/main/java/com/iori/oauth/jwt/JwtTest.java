package com.iori.oauth.jwt;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTest {

    public static void main(String[] args) {
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjExOTc1NTQwOCwiYXV0aG9yaXRpZXMiOlsic2Vja2lsbF9saXN0IiwiZ29vZHNfbGlzdCJdLCJqdGkiOiJhZDNjMzI5YS03NGI3LTQ4ZTMtOGU1OS04YTIzYzljNTNkOGEiLCJjbGllbnRfaWQiOiJzaG9wIiwidXNlcm5hbWUiOiJpb3JpIn0.FoVJDNylTzjKouywhxu9gnl-jrMNszt9lH6_Iribr052w3kvd904j6GwrG20MWjIdPGzl-3catSRwzznJHMD_oTTlklNMJzaTCwO1KJDxkJyBl0D0L2R3UkjWIY_OUXspdGuNr1VMw72yNi7-UjMDLl5ajvYT1kt7UrK11vVsVxLLgoJusCiwF9l4odKznMDpxeQyualJsp8BKSh9jq63f3L6ZwcF7AH4qIm0v6eBzh3pKOMQ1_-2B9S00krfEgAKD3-5tV3igmfTNq9a-fMLwHkmSgQXn_H5sEyhv_NsNERqik07tqZfVgSOlLKX_2i4GNRlctYVZIIdddywCwafg";
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApb0L0i0t2vV/LrWVK0NRtnoZV3VWrSD4AxL9XNIQoVtgIgKbvKZcY2T1kkuZ5MxGulLy+ScuFH64NjTkBPb76JL+Hgyep4WdpVtWhJdPEWAWF2qqqNSYJ9KHns+G9pN+/bFWAMuhzVRtfo4zbGFWzCWh18kS7u4u+5WO4GL3Q3z1NXaFTS7LiqGGuH0Lx47CQsZGf9yCYwT9BmjYHGMkUlLQWp6Zs1HygkUM3zJXnSTMBG4SE7qXjM7CTGpoZ7HHfEJnjmHeBFR9h6L42PjaSM/bVns2/3SDyc+znm7uc9rHv3d5Z8sDYizNIRAFdTxpSgEMI3Md3EFRXZTYW7J7vwIDAQAB-----END PUBLIC KEY-----";
        //解析token
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        System.out.println(jwt.getClaims());


    }

    public static void create() {
        //证书文件路径
        String key_location = "myiori.jks";
        //密钥库密码
        String key_password = "myiori";
        //密钥密码
        String keypwd = "myiori";
        //密钥别名
        String alias = "myiori";

        //读取密钥文件
        ClassPathResource classPathResource = new ClassPathResource(key_location);
        //得到密钥工厂对象
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(classPathResource,key_password.toCharArray());
        //通过密钥工厂来获取密钥对象
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias, keypwd.toCharArray());
        //获取私钥对象
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String,String> map = new HashMap<>();
        map.put("id","1");
        map.put("username","admin");

        Jwt encode = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(privateKey));

        System.out.println(encode.getEncoded());
    }

}

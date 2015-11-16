package com.example.patientclient01;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class PassAuthenticator extends Authenticator
{
    public PasswordAuthentication getPasswordAuthentication()
    {
        /**
         * 这个地方需要添加上自己的邮箱的账号和密码
         */
        String username = "zhangruitest0113@sina.com";
        String pwd = "123456789Abc";
        return new PasswordAuthentication(username, pwd);
    }
}  

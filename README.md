# onlim-api
Onlim API is a tool that touristic service providers utilize to generate social media posts by injecting data about their offers into some templates.

# running jax-rs on tomcat 7:
Set $USERNAME and $PASSWORD to your tomcat user

1.) setup tomcat user access rights:
file: $CATALINA_HOME/conf/tomcat-users.xml
<role rolename="manager-script"/>
<user username="$USERNAME" password="$PASSWORD" roles="manager-script"/>

2.) maven set tomcat user/password:
file: ~/.m2/settings.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings>
    <servers>
        <server>
            <id>TomcatServerOnlim</id>
            <username>$USERNAME</username>
            <password>$PASSWORD</password>
        </server>
    </servers>
</settings>
```

3.) run application:
$ mvn tomcat7:run

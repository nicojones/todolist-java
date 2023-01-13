# Reinstall everything again

https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.0.2-SNAPSHOT&packaging=jar&jvmVersion=19&groupId=com.minimaltodo&artifactId=List&name=List&description=Minimaltodo%20Backend&packageName=com.minimaltodo.List&dependencies=web,security,oauth2-client,data-jdbc,mysql,data-jpa,devtools


# How to connect to MySQL

- After creating the container, you have to alter the password:  
    
    alter user 'root'@'localhost' identified by 'minimaltodo@0258!';

- Then, you have to create a new user, which is not on `localhost`, but instead lives in `%`:
    
    create user 'minimaltodo'@'%' identified by 'minimaltodo@0258!';
    grant all on *.* to 'minimaltodo'@'%';

- Now you should be able to connect via a MySQL client.
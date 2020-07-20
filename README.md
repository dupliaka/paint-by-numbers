##Draw a painting by numbers

This application splits your picture by color segments 
by [this](https://link.springer.com/article/10.1023%2FB%3AVISI.0000022288.19776.77)
method and enumerates each segment by 27 available colors

Required:
* [Java 1.8](https://openjdk.java.net/)
* [Maven 3.6](http://maven.apache.org/)
* [Git](http://help.github.com/git-installation-redirect)

To clone repo 
```shell
git clone git@github.com:dupliaka/paint-by-numbers.git
```
To build the app and create jar
```shell
mvn clean package  
```
To get numbered image
```shell
java -jar target/paint-by-numbers-1.0-SNAPSHOT.jar <path_to_image>
```
To get the picture as above:
```shell
java -jar target/paint-by-numbers-1.0-SNAPSHOT.jar test.png
```

NOTE: For better results polygon your picture 
to remove the small fractions of color segments
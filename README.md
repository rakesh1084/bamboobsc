# bamboobsc

<h2>bambooBSC is an opensource Balanced Scorecard (BSC) Business Intelligence</h2>
<br/>
<br/>
Features:<br/>
1. BSC's Vision, Perspectives, Objectives of strategy, Key Performance Indicators (KPIs). and Maintain measure-data.<br/>
2. KPI report, Personal and organization BSC report. can custom workspace's layout<br/>
3. Strategy Map, and BSC SWOT.<br/>
4. Can custom SQL query results show as Charts ( PIE, BAR, LINE, AREA ) on QCHARTS-WEB.<br/>
5. Provides a simple OLAP query.<br/>
<br/><br/>

<h2>Demo web: ( The web run on Raspberry pi 2 )</h2>
http://124.218.68.215:8888/core-web/login.action<br/>
account: tester<br/>
password: tester<br/>
<br/><br/>

<h2>bambooBSC 0.5 architecture</h2>
<img src="https://github.com/billchen198318/bamboobsc/blob/master/core-doc/bamboobsc_05_web_arch.png"/><br/>
<br/><br/>
<h2>License</h2>
bambooBSC is released under version 2.0 of the Apache Version 2.0
<br/><br/>
<h2>install document:</h2>
<a href="https://github.com/billchen198318/bamboobsc/blob/master/core-doc/install.pdf">install.pdf</a>
<br/><br/>
<h2>manual document:</h2>
<a href="https://github.com/billchen198318/bamboobsc/blob/master/core-doc/bamboobsc-guide.pdf">bamboobsc-guide.pdf</a>
<br/><br/>
<h2>Download:</h2> http://sourceforge.net/projects/bamboobsc/
<br/><br/>
contact: chen.xin.nien@gmail.com
<br/><br/>
Create a scorecard project demo video:<br/>
https://www.youtube.com/watch?v=A27wnMTtfOc
<br/>
Mobile web version demo video:<br/>
https://www.youtube.com/watch?v=56ocMktSRIs
<br/><br/>
<h2>Screenshot</h2>
<br/>
<br/>1.
<img src="http://a.fsdn.com/con/app/proj/bamboobsc/screenshots/E_fix01.png"/><br/>
<br/>2.
<img src="http://a.fsdn.com/con/app/proj/bamboobsc/screenshots/A_fix.png"/><br/>

<br/>
<br/>

###run on Raspberry pi 2
<img src="https://github.com/billchen198318/bamboobsc/blob/master/core-doc/IMGP4139.JPG"></img>
###1. must first install the MySQL and JDK8
```
# apt-get install mysql-server
# apt-get install oracle-java8-jdk
# java -version
```
java version "1.8.0"
Java(TM) SE Runtime Environment (build 1.8.0-b132)
Java HotSpot(TM) Client VM (build 25.0-b70, mixed mode)


###config MySQL root account's password
```
# mysqladmin -u root -p password
```

###2. Config MySQL enable lower_case_table_names=1
```
# service mysql stop
# vi /etc/mysql/my.cnf
```

add lower_case_table_names=1 
```
[mysqld]
lower_case_table_names=1
```
and save my.cnf
```
# service mysql restart
```

###3. Get bambooBSC environment file
```
# cd /home/pi
# wget http://www.mirrorservice.org/sites/ftp.sourceforge.net/pub/sourceforge/b/project/ba/bamboobsc/bamboobsc-05-20150615.7z
```

###4. Install P7ZIP
```
# apt-get install p7zip
```

###5. Extract the archive
```
# p7zip -d bamboobsc-05-20150615.7z
```

###6. Import bbcore.sql to MySQL
```
# cd /home/pi/bamboobsc-05/
# mysql -u root -p
```
mysql> create database bbcore;<br/>
mysql> exit;<br/>
```
# mysql bbcore -u root -p < bbcore.sql
```

###7. Config applicationContext-dataSource.properties
config A ( CORE system ).<br/> /home/pi/bamboobsc-05/apache-tomcat-7.0.62/webapps/<b>core-web</b>/WEB-INF/classes/applicationContext/conf/applicationContext-dataSource.properties<br/>
<br/>
config B (Balanced Scorecard system ).<br/> /home/pi/bamboobsc-05/apache-tomcat-7.0.62/webapps/<b>gsbsc-web</b>/WEB-INF/classes/applicationContext/conf/applicationContext-dataSource.properties<br/>
<br/>
config C (Simple OLAP system ).<br/> /home/pi/bamboobsc-05/apache-tomcat-7.0.62/webapps/<b>qcharts-web</b>/WEB-INF/classes/applicationContext/conf/applicationContext-dataSource.properties<br/>
<br/>
config D (Mobile web ).<br/> /home/pi/bamboobsc-05/apache-tomcat-7.0.62/webapps/<b>gsbsc-mobile-web</b>/WEB-INF/classes/applicationContext/conf/applicationContext-dataSource.properties<br/>
<br/>
settings <b>dataSource.user</b> and <b>dataSource.password</b> <br/>
dataSource.user is MySQL account<br/>
dataSource.password is MySQL password<br/>

example:
```
dataSource.user=root
dataSource.password=password
```

###8. The need to create the directory folder, for upload and report source file need.
```
# cd /var
# mkdir gsbsc gsbsc/upload gsbsc/jasperreport
```

###9. Run bambooBSC 0.5
```
# cd /home/pi/bamboobsc-05/apache-tomcat-7.0.62/bin
# chmod a+x catalina.sh
# chmod a+x shutdown.sh
# chmod a+x startup.sh
```

add JAVA_OPTS="-Xmx750M -server -XX:MinHeapFreeRatio=20 -XX:MaxHeapFreeRatio=40 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90" to catalina.sh
```
# ./startup.sh
```

Wait a few minutes ( the test results, will start more than 10 minutes' clock ), <br/> 
Use <b>google Chrome</b> or <b>Firefox</b> browser (<b>no support IE</b>) input url ( <b>[IP-Address]:[Port]/core-web/index.action</b> )
example (if raspberry pi 2 IP-Address is 192.168.1.100) :<br/>
192.168.1.100:8080/core-web/index.action
<br/>

the log file on /tmp/

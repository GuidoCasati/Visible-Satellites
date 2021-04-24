# Visible Satellites App

* Date: 28/01/2018
* Author: Guido Casati

# Introduction

## Purpose 

The satellites application is developed to reads the start and end times of satellite visibilities from an input file. The location of the input file is given as a command line parameter to the application. Each line of the input file must contain the start and the end time of the visibility period of exactly one satellite, separated by comma. For example:
```
12:34:56.789,15:43:21.012 
```
Based on the data from the input file, satellites app finds the time range(s) when the maximum number of satellites are visible to the ground station and how many satellites there are. The output is provided in the following format: 
```
<start time>-<end time>;<number of satellites> 
```

For instance: 
```
12:34:59.001-12:36:42.422;7 
```

The start and end times of communication periods are inclusive. For instance, if one satellite started communication with a ground station at 12:34:56.789 and another ended its communication at 12:34:56.789, there were 2 satellites visible to the ground station at 12:34:56.789.

## Scope

This application is conceived for ground station providers who want to sell time slots for satellite communication. The application requires log file containing the time intervals satellites are above the horizon and in principle visible to the ground station. This can be provided by the flight dynamics team. Scheduling hot spots during a day represent the output of the application.

# Application Overview

Satellites is written in Java language, hence it is a cross-platform application: the performance of the application does not change whether installed on Windows or Linux systems. 

## Localhost setup 
Satellites requires Java Runtime Environment and Development Kit to be installed on your local machine. In order to verify that, user can run the command “java –version” on the shell. Recommended version is Java 8.  
JRE can be downloaded at [jre8](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) while JDK is available at [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). On Linux environment JRE and JDK can be installed via terminal by launching the command 
```
sudo apt-get install default-jre
```
and
```
sudo apt-get install default-jdk
```

## Run Satellites app

Once the jar executable is generated the app can be easily run on command-line by the command “java –jar Satellites.jar”. 
The running application will ask for a valid path where the input file is located. Once provided, the algorithm will calculate and display all the time ranges when the maximum number of satellites is visible from the ground station. The maximum number of satellites in visibility is displayed as well.

An example follows:
```
user@vbox:~/$ sudo java -jar satellites.jar
Please enter the location of the input file: ./satellites.dat
09:43:22.426-09:54:25.346;97
09:47:43.235-09:56:23.347;97
```

In this case, 180 satellites are visible in two different ranges. 

## Application Design

The application is developed in a Java code of 200 lines. Development has been completed on Eclipse IDE and testing has been performed both on Windows and Linux environment.
Description of the algorithm

The algorithm reads the input file from the location provided through the command line. While looping through every line of the log file, the algorithm populates a list of pairs composed by a timestamp and a Boolean indicating the state of the timestamp, meaning if it represents a START (true) or END (false) time. The timestamp is converted to Date object for our purposes.
The obtained list is sorted by timestamp and scanned from the beginning to the end. A COUNTER is used in order to store the current number of satellites visible at a given time range. When a START is found in the list, the counter increments, while it decrements when an END is encountered. 
While scanning the sorted list, the value of the maximum number of visible satellites MAX is also updated:
-	When the stored MAX is lower than the current counter, MAX is set to the COUNTER value. The list RANGES containing the output visibility ranges is cleared and the current pair is added. 
-	When COUNTER decrements to MAX-1 and the state is END, the current pair is added to RANGES.
-	When COUNTER equals the MAX and the state is START, the current pair is added to RANGES.
The algorithm is handling also the case when timestamps are repeating in the log file. To do so, the CURRENT timestamp is compared to the OLD stored at the previous step and an EXTRA counter is used:
-	When the OLD timestamp has state START, the EXTRA counter is set to the COUNTER value
-	When the OLD timestamp has state END and the CURRENT has state START, the EXTRA counter is set to COUNTER+1
-	When both the OLD and the CURRENT timestamp have state END, EXTRA counter is set to COUNTER+2
In this way, all repetitions of the timestamps will be accounted in the inclusive logic of the satellites in visibility.
Finally, whenever the EXTRA counter exceeds the current COUNTER, the latter is set to the value of the former. The RANGES list is also updated.
At the end of the algorithm, a loop is going through the list RANGES and, upon conversion of the date objects to string, is printing on screen the range and the maximum number of visible satellites in the desired format.

# Future improvements

Although leaner implementation is possible and more efficient programming languages could be used, the developer chose for this implementation as it represented the best trade-off between short time of delivery and best efficiency.
The application contains roughly 150 lines of effective code. A custom class Pair is defined and nested loops are implemented. The algorithm can be improved by reducing the number of loops and making the logic leaner and more efficient. Also, it should be investigated in Java documentation in order to find more suitable Java interfaces to handle maps operations.
If more precise timestamps were provided in the log file, the date format should be updated accordingly in the algorithm. If the timestamps were spread over more days, the algorithm would be able to handle this as the timestamps are converted to date objects and sorted accordingly.
A better implementation can be achieved through multi-paradigm languages such as Python which would allow to achieve the same performance in fewer lines of code.


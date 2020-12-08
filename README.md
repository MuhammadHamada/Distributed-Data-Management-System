# Distributed-Data-Management-System
A java system that allowed distributing the data from an API for earthquakes across many tablets servers. 
The system contains master server and 2 tablet servers on different machines. It handles multiple clients requests.
The master server gets the data from the API and splits them into tablets. 
Each tablet server is responsible for some of tablets.

The system was developed by using Multithreading, Sockets, and MySQL.

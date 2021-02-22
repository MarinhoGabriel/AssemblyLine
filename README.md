# AssemblyLine
A sorting software used to organize a number of processes into assembly lines with lunch and gym times in some fixed periods.

| Build |
|-------|
[![Build Status](https://travis-ci.com/MarinhoGabriel/AssemblyLine.svg?branch=master)](https://travis-ci.com/MarinhoGabriel/AssemblyLine) [![Coverage Status](https://coveralls.io/repos/github/MarinhoGabriel/AssemblyLine/badge.svg?branch=master)](https://coveralls.io/github/MarinhoGabriel/AssemblyLine?branch=master) [![codecov](https://codecov.io/gh/MarinhoGabriel/AssemblyLine/branch/master/graph/badge.svg?token=1KKTR3B0QW)](https://codecov.io/gh/MarinhoGabriel/AssemblyLine)

An assembly line has multiple steps and the goal of this program is to organize these steps into assembly lines considering the lunch time, at noon, and the gym time, in a period between 16h and 17h.
The input of the program is a text file, calles "input.txt" and the first step of the program is to separate the productuon lines into a `Map<String, Integer>` with all production steps with their duration (in some cases, we can have the string `maintenance`, indicating that the step has a 5min duration). 
For instance, let's consider the following text file:

```
Step I 60min
Step II 30min
Step III 45min
Step IV 60min
Step V 45min
Step VI 30min
Step VII 45min
Step VIII - maintenance
Step IX 45min
Step X 60min
Step XI 60min
Step XII - maintenance
Step XIII 45min
Step XIV 60min
Step XV 45min
Step XVI 30min
Step XVII 45min
Step XVIII 30min
```

The first step of the program gives us a `HashMap` and we see the values, if we sort, as shown below
```java
("Step I", 60)
("Step II", 30)
("Step III", 45)
("Step IV", 60)
("Step V", 45)
("Step VI", 30)
("Step VII", 45)
("Step VIII", 5)
("Step IX", 45)
("Step X", 60)
("Step XI", 60)
("Step XII", 5)
("Step XIII", 45)
("Step XIV", 60)
("Step XV", 45)
("Step XVI", 30)
("Step XVII", 45)
("Step XVIII", 30)
```

Then, with this map, we can go to the second step of the program: the organizing.
The organization is quite simple: we need to consider each production step time and organize it on an assembly line. Each assembly line has a morning and an afternoon period, with lunch between them and a gym in the end, startig at least at 16h and in max at 17h. If there is stil production steps after the gym time, another production line is built. 
Organizing the above file, we have

```
Linha de montagem 1:
09:00 Step I 60min
10:00 Step II 30min
10:30 Step III 45min
12:00 Almoço
13:00 Step IV 60min
14:00 Step V 45min
14:45 Step VI 30min
15:15 Step VII 45min
16:00 Step VIII 
16:05 Step IX 45min
16:50 Ginástica laboral

Linha de montagem 2:
09:00 Step X 60min
10:00 Step XI 60min
11:00 Step XII 
11:05 Step XIII 45min
12:00 Almoço
13:00 Step XIV 60min
14:00 Step XV 45min
14:45 Step XVI 30min
15:15 Step XVII 45min
16:00 Step XVIII 30min
16:30 Ginástica laboral
```

As we can see, three assembly lines were created because there wasn't enough time in one to store all of the production steps.

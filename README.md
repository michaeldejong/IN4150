# Lab exercises distributed algorithms

## Exercise 3 - Byzantine Agreement

### Introduction

The Lamport, Pease and Shostak algorithm is designed for reaching consensus in a distributed system with faulty or malevolent participants. It's a recursive algorithm which is able to handle up to a specified amount of faults. In short: A commander process broadcasts an order to everybody else. Then the other processes have to come to concensus on what the commander's order was by exchanging information with each other. The pseudo-code of the algorithm is as followed:

#### Commander
1. Broadcast message `(order, f, [ my_id ])`

#### Lieutenant
* On receiving message `(order, 0, path)`:
	1. Reply with the order.
* On receiving message `(order, f, path)`:
	1. Multicast `(order, f-1, path + my_id)` to everyone not yet in path.
	2. Collect responses, use default (retreat) if a reply is not received in time.
	3. Determine the majority of responses (attack or retreat), and return this.

#### Making a decision
As soon as all the messages have been sent (or timed-out), it is time to decide on what all the loyal lieutenants should do (attack or retreat). To ensure that they all reach the same decision, each lieutentant records all received and sent messages in a tree format using the path variable found in each message. By reducing this tree recursively we can decide on a single value (attack or retreat). It's guaranteed that all loyal lieutenants will reach this same value.

### Test cases

| Case | Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|------|-----------|---|-------------------|----------|--------|-------------------|
| 1    | LOYAL     | 1 | 1                 | 1        | 0      | 4                 |
| 2    | TRAITOR   | 1 | 2                 | 0        | 0      | 4                 |
| 3    | LOYAL     | 1 | 2                 | 1        | 0      | 9                 |
| 4    | LOYAL     | 1 | 2                 | 0        | 1      | 7-9               |
| 5    | LOYAL     | 1 | 3                 | 2        | 0      | 25                |
| 6    | LOYAL     | 1 | 3                 | 0        | 2      | 17-25             |
| 7    | LOYAL     | 1 | 2                 | 0        | 3      | 13-25             |
| 8    | LOYAL     | 1 | 2                 | 0        | 4      | 16-36             |
| 9    | LOYAL     | 2 | 2                 | 0        | 4      | 24-156            |

In the following 9 testcases the amount of messages and decision is shown.
The amount of messages will vary if faulty processes are in the mix.
Traitors will not drop messages and screw up message parameters, like faulty processes do, other than the actual command.

### Results

#### Test case 1

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 1                 | 1        | 0      | 4                 |

#####Unanimous
![graph1](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 4                 | ATTACK   | YES       |
| 2   | 4                 | ATTACK   | YES       |
| 3   | 4                 | ATTACK   | YES       |
| 4   | 4                 | ATTACK   | YES       |
| 5   | 4                 | ATTACK   | YES       |
| 6   | 4                 | ATTACK   | YES       |
| 7   | 4                 | ATTACK   | YES       |
| 8   | 4                 | ATTACK   | YES       |
| 9   | 4                 | ATTACK   | YES       |
| 10  | 4                 | ATTACK   | YES       |

All the results are unanimous. The amount of traitors (1) is a third of everybody (1 loyal lieutenant and 1 traitor) but since we only look at the decision of loyal lieutenants the results are unanimous and correct.


#### Test case 2

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| TRAITOR   | 1 | 2                 | 0        | 0      | 4                 |

#####Unanimous
![graph2](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 4                  | RETREAT  | YES       |
| 2   | 4                  | RETREAT  | YES       |
| 3   | 4                  | RETREAT  | YES       |
| 4   | 4                  | RETREAT  | YES       |
| 5   | 4                  | RETREAT  | YES       |
| 6   | 4                  | RETREAT  | YES       |
| 7   | 4                  | RETREAT  | YES       |
| 8   | 4                  | RETREAT  | YES       |
| 9   | 4                  | RETREAT  | YES       |
| 10  | 4                  | RETREAT  | YES       |

All the results are unanimous and correct since there are no traitors or faulties and the algorithm works.


#### Test case 3

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 1        | 0      | 9                 |

#####Unanimous
![graph3](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 9                  | ATTACK   | YES       |
| 2   | 9                  | ATTACK   | YES       |
| 3   | 9                  | ATTACK   | YES       |
| 4   | 9                  | ATTACK   | YES       |
| 5   | 9                  | ATTACK   | YES       |
| 6   | 9                  | ATTACK   | YES       |
| 7   | 9                  | ATTACK   | YES       |
| 8   | 9                  | ATTACK   | YES       |
| 9   | 9                  | ATTACK   | YES       |
| 10  | 9                  | ATTACK   | YES       |

All the results are unanimous and correct because the algorithm can handle up to a third of everybody to not be loyal.
It is 3-1 loyal vs. not loyal in this case (1 traitor).


#### Test case 4

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 1      | 7-9               |

#####Unanimous
![graph4](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 7                  | ATTACK   | YES       |
| 2   | 7                  | ATTACK   | YES       |
| 3   | 9                  | ATTACK   | YES       |
| 4   | 9                  | ATTACK   | YES       |
| 5   | 9                  | ATTACK   | YES       |
| 6   | 7                  | ATTACK   | YES       |
| 7   | 7                  | ATTACK   | YES       |
| 8   | 9                  | ATTACK   | YES       |
| 9   | 9                  | ATTACK   | YES       |
| 10  | 7                  | ATTACK   | YES       |

All the results are unanimous and correct because the algorithm can handle up to a third of everybody to not be loyal.
It is 3-1 loyal vs. not loyal in this case (1 faulty).


#### Test case 5

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 3                 | 2        | 0      | 25                |

#####Unanimous
![graph5](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 25                 | ATTACK   | YES       |
| 2   | 25                 | ATTACK   | YES       |
| 3   | 25                 | ATTACK   | YES       |
| 4   | 25                 | ATTACK   | YES       |
| 5   | 25                 | ATTACK   | YES       |
| 6   | 25                 | ATTACK   | YES       |
| 7   | 25                 | ATTACK   | YES       |
| 8   | 25                 | ATTACK   | YES       |
| 9   | 25                 | ATTACK   | YES       |
| 10  | 25                 | ATTACK   | YES       |

These results are notable since the algorithm is not guaranteed to be correct at or beyond a third of everybody not being loyal.
It is 4-2 loyal vs. not loyal in this case, so exactly a third is not loyal (traitors in this case).
What happens in this case depends on the implementation of the traitor algorithm.


#### Test case 6

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 3                 | 0        | 2      | 17-25             |

#####Unanimous
![graph6](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 25                 | ATTACK   | YES       |
| 2   | 21                 | ATTACK   | YES       |
| 3   | 17                 | ATTACK   | YES       |
| 4   | 21                 | ATTACK   | YES       |
| 5   | 17                 | ATTACK   | YES       |
| 6   | 17                 | ATTACK   | YES       |
| 7   | 17                 | ATTACK   | YES       |
| 8   | 21                 | ATTACK   | YES       |
| 9   | 25                 | ATTACK   | YES       |
| 10  | 21                 | ATTACK   | YES       |

All the results are unanimous and correct because the algorithm can handle up to a third of everybody to not be loyal.
It is 4-2 loyal vs. not loyal in this case (2 faulty).


#### Test case 7

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 3      | 13-25             |

#####Unanimous
![graph7](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)

| Run | Number of messages | Decision | Unanimous |
|-----|--------------------|----------|-----------|
| 1   | 13                 | ATTACK   | YES       |
| 2   | 17                 | ATTACK   | YES       |
| 3   | 21                 | ATTACK   | YES       |
| 4   | 13                 | ATTACK   | YES       |
| 5   | 25                 | ATTACK   | YES       |
| 6   | 21                 | ATTACK   | YES       |
| 7   | 17                 | ATTACK   | YES       |
| 8   | 21                 | ATTACK   | YES       |
| 9   | 25                 | ATTACK   | YES       |
| 10  | 17                 | ATTACK   | YES       |

These results are notable since the algorithm is not guaranteed to be correct at or beyond a third of everybody not being loyal.
It is 3-3 loyal vs. not loyal in this case, so exactly half is not loyal (faulty processes in this case).
What happens in this case is a matter of chance, implemented with randomization in the faulty algorithm.
The amount of messages is varies a lot conseqently.


#### Test case 8

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 4      | 16-36             |

#####Unanimous
![graph8](https://github.com/michaeldejong/IN4150/blob/master/images/test8.PNG?raw=true)

| Run | Number of messages | Decision              | Unanimous |
|-----|--------------------|-----------------------|-----------|
| 1   | 31                 | ATTACK                | YES       |
| 2   | 21                 | ATTACK                | YES       |
| 3   | 21                 | ATTACK                | YES       |
| 4   | 21                 | ATTACK                | YES       |
| 5   | 31                 | ATTACK                | YES       |
| 6   | 26                 | **RETREAT**           | YES       |
| 7   | 26                 | ATTACK                | YES       |
| 8   | 31                 | ATTACK / **RETREAT**  | **NO**    |
| 9   | 26                 | ATTACK                | YES       |
| 10  | 31                 | ATTACK                | YES       |

These results are expected since the algorithm is not guaranteed to be correct at or beyond a third of everybody not being loyal.
It is 3-4 loyal vs. not loyal in this case so exactly half is not loyal (faulty processes in this case).
What happens in this case is a matter of chance, implemented with randomization in the faulty algorithm.
The results represent this probability well.


#### Test case 9

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 2 | 2                 | 0        | 4      | 24-156            |

#####Unanimous
![graph9](https://github.com/michaeldejong/IN4150/blob/master/images/test9.PNG?raw=true)

| Run | Number of messages | Decision              | Unanimous |
|-----|--------------------|-----------------------|-----------|
| 1   | 66                 | **RETREAT**           | YES       |
| 2   | 119                | ATTACK / **RETREAT**  | **NO**    |
| 3   | 57                 | ATTACK                | YES       |
| 4   | 62                 | **RETREAT**           | YES       |
| 5   | 99                 | ATTACK                | YES       |
| 6   | 40                 | ATTACK / **RETREAT**  | **NO**    |
| 7   | 103                | ATTACK                | YES       |
| 8   | 70                 | ATTACK / **RETREAT**  | **NO**    |
| 9   | 61                 | ATTACK                | YES       |
| 10  | 57                 | ATTACK                | YES       |

The results look alike to test case 8, but the difference here is F is one higher.
This results in more errors in messages and the results show this.


#### Test case 10
| Runs | Commander | F | Loyal lieutenants |
|------|-----------|---|-------------------|
|  10  | LOYAL     | 2 | 8                 |


| Traitors | Avg # messages | Avg Loyal Decision Correct |
|----------|----------------|----------------------------|
|     0    |      259       |             100%           |
|     1    |      259       |             100%           |
|     2    |      259       |             100%           |
|     3    |      259       |              50%           |
|     4    |      257       |              50%           |
|     5    |      254       |              50%           |

The percentage is the amount of loyal lieutenants that came to the correct decision regarding the command of the general.
The drop in percentage is notable because it drops from 100% to 50% and stays there.
This is most likely the effect of only counting the loyal lieutenants decision and the implementation of the traitor algorithm, that sends the same (traitorous) value to the same clients based on port number.

#### Test case 11
| Runs | Commander | F | Loyal lieutenants |
|------|-----------|---|-------------------|
|  10  | LOYAL     | 2 | 8                 |


| Faulties | Loyal Decision Correct (%) |
|----------|----------------------------|
|     0    |                            |
|     1    |                            |
|     2    |                            |
|     3    |                            |
|     4    |                            |
|     5    |                            |



#### Test case 12
| Runs | Commander | F | Loyal lieutenants |
|------|-----------|---|-------------------|
|  10  | LOYAL     | 2 | 16                 |


| Faulties | Loyal Decision Correct (%) |
|----------|----------------------------|
|     0    |                %           |
|     2    |                %           |
|     4    |                %           |
|     8    |                %           |



#### Test case 13
| Commander | Loyal lieutenants | Traitors | Faulties |
|-----------|-------------------|----------|----------|
| LOYAL     |       24          |     1    |     0    |


| F | Time to decision (ms) | Slowdown |
|---|-----------------------|----------|
| 0 |              ms       |      x   |
| 1 |              ms       |      x   |
| 2 |              ms       |      x   |
| 3 |              ms       |      x   |
| 4 |              ms       |      x   |


### Conclusion

Test cases 1 to 9 show the amount of messages being dependent on the amount of faulty processes.
Also they showed that luck was on our side when the amount of faulty processes was going to half of the total amount of nodes.
Only when there were more than half of faulty processes some cracks started to appear and wrong decisions showed up in our test results.

In the test cases 10 to 13 we found out that the RMI system does not scale very well.
The more messages we tried to send at the same time the worse the system responded (in a non linear way) causing RMI exceptions and timeouts in our algorithm.
Problems already formed beyond F=3 for 10 nodes in total.
We tweaked the amount of threads that talk to RMI.
Even with 10 threads we had hickups, but the more threads we used the more fluent it seemed to run.

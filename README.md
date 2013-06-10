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

### Results

#### Test case 1

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 1                 | 1        | 0      | 4                 |

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

![graph1](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 2

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| TRAITOR   | 1 | 2                 | 0        | 0      | 4                 |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 4                 | RETREAT  | YES       |
| 2   | 4                 | RETREAT  | YES       |
| 3   | 4                 | RETREAT  | YES       |
| 4   | 4                 | RETREAT  | YES       |
| 5   | 4                 | RETREAT  | YES       |
| 6   | 4                 | RETREAT  | YES       |
| 7   | 4                 | RETREAT  | YES       |
| 8   | 4                 | RETREAT  | YES       |
| 9   | 4                 | RETREAT  | YES       |
| 10  | 4                 | RETREAT  | YES       |

![graph2](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 3

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 1        | 0      | 9                 |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 9                 | ATTACK   | YES       |
| 2   | 9                 | ATTACK   | YES       |
| 3   | 9                 | ATTACK   | YES       |
| 4   | 9                 | ATTACK   | YES       |
| 5   | 9                 | ATTACK   | YES       |
| 6   | 9                 | ATTACK   | YES       |
| 7   | 9                 | ATTACK   | YES       |
| 8   | 9                 | ATTACK   | YES       |
| 9   | 9                 | ATTACK   | YES       |
| 10  | 9                 | ATTACK   | YES       |

![graph3](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 4

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 1      | 7-9               |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 7                 | ATTACK   | YES       |
| 2   | 7                 | ATTACK   | YES       |
| 3   | 9                 | ATTACK   | YES       |
| 4   | 9                 | ATTACK   | YES       |
| 5   | 9                 | ATTACK   | YES       |
| 6   | 7                 | ATTACK   | YES       |
| 7   | 7                 | ATTACK   | YES       |
| 8   | 9                 | ATTACK   | YES       |
| 9   | 9                 | ATTACK   | YES       |
| 10  | 7                 | ATTACK   | YES       |

![graph4](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 5

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 3                 | 2        | 0      | 25                |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 25                | ATTACK   | YES       |
| 2   | 25                | ATTACK   | YES       |
| 3   | 25                | ATTACK   | YES       |
| 4   | 25                | ATTACK   | YES       |
| 5   | 25                | ATTACK   | YES       |
| 6   | 25                | ATTACK   | YES       |
| 7   | 25                | ATTACK   | YES       |
| 8   | 25                | ATTACK   | YES       |
| 9   | 25                | ATTACK   | YES       |
| 10  | 25                | ATTACK   | YES       |

![graph5](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 6

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 3                 | 0        | 2      | 17-25             |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 25                | ATTACK   | YES       |
| 2   | 21                | ATTACK   | YES       |
| 3   | 17                | ATTACK   | YES       |
| 4   | 21                | ATTACK   | YES       |
| 5   | 17                | ATTACK   | YES       |
| 6   | 17                | ATTACK   | YES       |
| 7   | 17                | ATTACK   | YES       |
| 8   | 21                | ATTACK   | YES       |
| 9   | 25                | ATTACK   | YES       |
| 10  | 21                | ATTACK   | YES       |

![graph6](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 7

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 3      | 13-25             |

| Run | Number of message | Decision | Unanimous |
|-----|-------------------|----------|-----------|
| 1   | 13                | ATTACK   | YES       |
| 2   | 17                | ATTACK   | YES       |
| 3   | 21                | ATTACK   | YES       |
| 4   | 13                | ATTACK   | YES       |
| 5   | 25                | ATTACK   | YES       |
| 6   | 21                | ATTACK   | YES       |
| 7   | 17                | ATTACK   | YES       |
| 8   | 21                | ATTACK   | YES       |
| 9   | 25                | ATTACK   | YES       |
| 10  | 17                | ATTACK   | YES       |

![graph7](https://github.com/michaeldejong/IN4150/blob/master/images/test1to8.PNG?raw=true)


#### Test case 8

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 1 | 2                 | 0        | 4      | 16-36             |

| Run | Number of message | Decision              | Unanimous |
|-----|-------------------|-----------------------|-----------|
| 1   | 31                | ATTACK                | YES       |
| 2   | 21                | ATTACK                | YES       |
| 3   | 21                | ATTACK                | YES       |
| 4   | 21                | ATTACK                | YES       |
| 5   | 31                | ATTACK                | YES       |
| 6   | 26                | **RETREAT**           | YES       |
| 7   | 26                | ATTACK                | YES       |
| 8   | 31                | ATTACK / **RETREAT**  | **NO**    |
| 9   | 26                | ATTACK                | YES       |
| 10  | 31                | ATTACK                | YES       |

![graph8](https://github.com/michaeldejong/IN4150/blob/master/images/test8.PNG?raw=true)


#### Test case 9

| Commander | F | Loyal lieutenants | Traitors | Faulty | Expected messages |
|-----------|---|-------------------|----------|--------|-------------------|
| LOYAL     | 2 | 2                 | 0        | 4      | 24-156            |

| Run | Number of message | Decision              | Unanimous |
|-----|-------------------|-----------------------|-----------|
| 1   | 66                | **RETREAT**           | YES       |
| 2   | 119               | ATTACK / **RETREAT**  | **NO**    |
| 3   | 57                | ATTACK                | YES       |
| 4   | 62                | **RETREAT**           | YES       |
| 5   | 99                | ATTACK                | YES       |
| 6   | 40                | ATTACK / **RETREAT**  | **NO**    |
| 7   | 103               | ATTACK                | YES       |
| 8   | 70                | ATTACK / **RETREAT**  | **NO**    |
| 9   | 61                | ATTACK                | YES       |
| 10  | 57                | ATTACK                | YES       |

![graph9](https://github.com/michaeldejong/IN4150/blob/master/images/test9.PNG?raw=true)


#### Test case 10
| Commander | F | Loyal lieutenants |
|-----------|---|-------------------|
| LOYAL     | 2 | 8                 |


| Run | Traitors | Faulties | Decision             | Unanimous |
|-----|----------|----------|----------------------|-----------|
|  1  |     0    |     0    | ATTACK / **RETREAT** | **NO**    |
|  2  |    1/4   |     0    | ATTACK / **RETREAT** | **NO**    |
|  3  |    1/3   |     0    | ATTACK / **RETREAT** | **NO**    |
|  4  |     0    |    1/4   | ATTACK / **RETREAT** | **NO**    |
|  5  |     0    |    1/4   | ATTACK / **RETREAT** | **NO**    |
|  6  |     0    |    1/4   | ATTACK / **RETREAT** | **NO**    |
|  7  |     0    |    1/3   | ATTACK / **RETREAT** | **NO**    |
|  8  |     0    |    1/3   | ATTACK / **RETREAT** | **NO**    |
|  9  |     0    |    1/3   | ATTACK / **RETREAT** | **NO**    |
| 10  |    1/4   |    1/4   | ATTACK / **RETREAT** | **NO**    |



### Conclusion

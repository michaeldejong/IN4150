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
	3. Determine the majority of responses (attack or response), and return this.

### Test cases



* list the test cases of your program
* including the numbers of messages sent/received
* and the upper and lower bound on the expected number of messages.
* whereas of course the final result of your program is consensus, for the cases in which the numbers of faulty processes is too high, report the input values of the correct processes and their decisions.

### Results

* Table 

### Conclusion

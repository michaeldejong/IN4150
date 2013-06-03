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

### Results

| Commander | F | Loyal | Traitors | Faulty | Expected | Sent | Decision | Unanimous |
|-----------|---|-------|----------|--------|----------|------|----------|-----------|
|LOYAL      | 1 |1      |1         |0       |4         |      |          |           |
|TRAITOR    | 1 |1      |0         |0       |4         |      |          |           |
|LOYAL      | 1 |2      |1         |0       |9         |      |          |           |
|LOYAL      | 1 |3      |2         |0       |25        |      |          |           |

### Conclusion

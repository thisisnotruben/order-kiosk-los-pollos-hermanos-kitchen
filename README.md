# README

## Flow

1. A database showing each item completion time in seconds a two value: min/max
2. Controller pulling in that data to read and the entity here giving a random value between the min/max
3. State-machine for the kitchen:
    1. Order recieved
    2. Order pending
        - Kitchen might be overworked so order is being held off until free
    3. Preparing order
        - This is where the randomized preperation time comes in
    4. Order ready
    5. Order delievered
        - When customer has picked up the food

State-machine for the worker
- Idle
- Work

## Remarks

What qualifies as work load in the kitchen? 
Number of workers in the kitchen min:1, max:10.
Each worker can only work on one item in respect to the preperation time.

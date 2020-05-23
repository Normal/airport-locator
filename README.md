### Description 

This is an example of the nearest neighbour (knn) problem which has 
logarithmic complexity solution for points in spatial dimensions.

I don't know if there data structure which can handle knn query for spherical coordinates such as our input, 
so I considered the following approaches:

1. **Brute-force (naive)** - iterate over all airports and find the closets for each event using Haversine formula.
It has O(N*M) complexity. I consider this approach only for validation of other approaches
since from my understanding brute-force is the slowest but the most accurate one.

2. **Kd-tree**. Build kd-tree data structure for all airports then handle knn query for each event. 
Complexity is O( N * lgM).
There are a few considerations though:
    - It is not distributed so it builds it once and then broadcasts to all worker nodes.
    - It doesn't support spherical coordinates so need to transform it to cartesian first.
    
3. **R-tree**. I've noticed that geospark (which I use for coordinates transformation) provides knn query support 
for 2-dimensional spatial space by R-tree implementation so I decided give it a chance. 
As far as I understand, it pretty much the same idea as Kd-tree just some differences in a way how exactly space is partitioned into regions.
It should provide the same complexity and has the same considerations as above.
Since it supports 2-d only I use EPSG:3857 for it.      
  

### Performance

I didn't calculate any benchmarks, but this is very 
approximate order of grow
which I get on my local machine for calculations against given dataset (1m events, ~5k airports).

I chose naive implementation as the most accurate one by design so 
**error** means the difference between any approach and naive implementation.

| approach | running time | errors    |
|----------|--------------|------------|
| Kd-tree  | ~ 30 seconds       | up to 15 % |
| R-tree   | ~ 10 minutes   | up to 5 % |
| Naive    | ~ 1 hour       | 0          |


### Implementation

All mentioned approaches required no distribution for airports but this is fine I guess.
I've implemented the nearest airport location with distribution over events with apache spark and scala.
So it scalable against the events.

Although I provided just data pipeline which creates csv from csv, 
exact the same code can handle response/request pattern with some streaming involving.   

### Open issues

I believe Kd-tree/R-tree is the right way to go. Probably not this particular implementation.
It's not clear yet to me:

1. Why Kd-tree is so inaccurate
2. Why geospark's R-tree takes too much time

But even now probably R-tree performance is good enough to handle thousand events / per sec.
 
If I have more time I would try to move in this 2 directions in order to improve current implementation.

### Build and run

- Modify _application.conf_ to provide input
    
- Build jar:
        
        sbt assembly
        
- Run jar:

        spark-submit target/scala-2.11/airport-locator.jar

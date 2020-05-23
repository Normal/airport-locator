### Description 

This is an example of the nearest neighbour (knn) problem which has 
logarithmic complexity solution for points in spatial dimensions.

The following approaches were considered:

1. **Brute-force (naive)** - iterate over all airports and find the closets for each event using Haversine formula.
It has O(N*M) complexity. Considered to ne used as validator for other approaches.

2. **Kd-tree**. Build kd-tree data structure for all airports then handle knn query for each event. 
Complexity is O( N * lgM).
There are a few considerations though:
    - It is not distributed so it builds it once and then broadcasts to all worker nodes.
    - It doesn't support spherical coordinates so need to transform it to cartesian first.
    
3. **R-tree**. Pretty much the same idea as Kd-tree just some differences in a way how exactly space is partitioned into regions.
It should provide the same complexity and has the same considerations as above.
EPSG:3857 standard were used for coordination transformation.      
  

### Performance

Local machine calculation. 
**error** means the difference between any approach and naive implementation.

| approach | running time | errors    |
|----------|--------------|------------|
| Kd-tree  | ~ 30 seconds       | up to 15 % |
| R-tree   | ~ 10 minutes   | up to 5 % |
| Naive    | ~ 1 hour       | 0          |


### Implementation

All mentioned approaches required no distribution for airports .
The nearest airport location was implemented with distribution over events with apache spark and scala.
So it scalable against the events.

Although just data pipeline was provided which creates csv from csv, 
exact the same routine can handle response/request pattern with some streaming involving.   

### Open issues

1. Why Kd-tree is so inaccurate
2. Why geospark's R-tree takes too much time

But even now probably R-tree performance is good enough to handle thousand events / per sec.
 
### Build and run

- Modify _application.conf_ to provide input
    
- Build jar:
        
        sbt assembly
        
- Run jar:

        spark-submit target/scala-2.11/airport-locator.jar

### Readme

#### Problem Description

The input of this problem is to a directed acyclic graph(DAG) representing a ﬂow of computations, as well as the output node of this graph. Given the inputs, there are two outputs we want to have:

- The dependencies of the output in the graph, and order them in the execution order. Please note this order is not always the topological order of all the nodes in the graph as there may be multiple output nodes and only a part of the nodes are related to the output we are interested in.
- Calculate the elapsed nodes of the dependent nodes of the output. From the problem description, the elapsed time is the sum of the clock time of all dependent nodes. Thus we are treating it as a separate method to sum the processing time of a given list of nodes.

#### Solution

The solution of the first problem can be separated into two parts:

- Find the dependent nodes of an output node.
- Return the dependent nodes in the topological order.

Since it's not guaranteed the graph to have only one output, we can't simply topological sort all the nodes in the graph to get the result.

For a directed acyclic graph, to find all nodes related to an output node, one way is to reverse the graph and then start a DFS or BFS from the output. Another way usually used to find ancestor nodes in a tree is to search recursively and return the path when it hits the target node and join all the paths at last. But since reversing a DAG only has a time complexity of O(E) which is smaller than BFS/DFS's time complexity of O(V+E), and we can get rid of recursions in our code to improve the readability, I used the reverse graph method with BFS to find the dependencies of an output node.

As BFS doesn't necessarily give the dependencies in the topological order, we need a separate way to obtain the correct order. We definitely can do a topological sort on all nodes of the graph and then look up the dependencies of the output node to get the order. On the other hand, since we are doing BFS on the reverse graph, the outgoing edges from a node will become an incoming edge, which results in the following two situations:

1. A node having multiple incoming edges in the original graph will have multiple outgoing edges.

   In this case, BFS will process this node before going to nodes connected by the outgoing edges in the reverse graph, and these nodes will have order number larger than current node.

2. A node having multiple outgoing edges in the original graph will have multiple incoming edges.

   In this case, BFS may process the current node earlier than nodes connected by incoming edges as incoming nodes may have different path lengths. But as we are in the reverse graph, we can take the largest order as the order number of this node because in the original graph it would require this node to be processed earlier.

#### Class Design

To initialize the test graphs easily, I have implemented a `DAG` class to represent a directed acyclic graph holding all the methods going to be used. Here is a brief list of all the methods:

- `Initialization` : As a graph can be represented by an adjacent list, we are storing it in a private object `adjList`.  We are also holding the list of vertex ids in the `DAG` class to better tracking the nodes in `DAG`. Besides the vertices and adjacent list, each node has an additional attribute of processing time, which we choose a hash map to store this information. There might be cases a node having multiple attributes, we can implement a `Node` class in that case. 
- `addEdge`: This is a helper private function for initialization of the graph. I choose to initialize the graph edges with string arrays of length 2 for the readability of the code when initializing a `DAG` class, and in this way the edges in this code are more expressive than showing it in the adjacent list way.  Since we are not focusing on implementing a flexible graph, I didn't add the public methods to add or remove a vertex or an edge from a graph.
-  `getAdjacentVertices`: private method to help to get the adjacent nodes of an input node id.
- `reversed`: return the reversed `DAG` object of the current graph. It reverses the edges in the original graph and adds them to the reversed graph.
- `getDependencies`: get the dependencies of a node in a graph and return the string format ids in a list. In this method, `timeStamp` is to record the maximum order of a node get processed which can be easily extended if there are any followups.
-  `getTime`: get the elapsed time given a list of node ids and return the cumulated time in an integer. This is implemented by simply looking up the process timetable in the object and add the time to the result.
- `unit tests`: Unit tests are implemented using `Junit`.

#### Run and Outputs

There are two files under the folder: `DAG.java` and `DAGTest.java`. The first one is the implementation of the main class while the second one is the unit test. I also kept a main method in the `DAG`class as an alternative way of demoing the implementation.

There are also two `.jar` files included in order to run the `Junit` unit tests without setting up an environment.

To compile the java files, please run the following command:

```
javac -classpath  .:junit-4.13.2.jar:hamcrest-core-1.3.jar *.java
```

To run the unit tests:

```
java -classpath .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore DAGTest
```

The expected output would be:

```
JUnit version 4.13.2
........
Time: 0.011

OK (8 tests)
```

And this is the unit test screen shot from IDE.

![](/Users/tianqiyang/Library/Application Support/typora-user-images/Screen Shot 2021-06-11 at 12.25.58.png)

To run the demo graph in the main method of the `DAG` class:

```
java DAG
```

The expected output would be:

```
[A, H, B, C, D, E]
13 //the total processing time of B, C and D
```



#### Notes

There are different ways to solve this problem and address it in more or less generative ways. The main idea when I was implementing it is to keep the implementation easier to read. There are some tricks such as using a list of ids that nodes can repeat instead of a hashmap to store the timestamp that could reduce the complexity. But I'm still keeping it as it can be easily extended if we request to order the nodes not only by topological order but also consider their processing time.


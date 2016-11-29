# Hidden-Web-Database-Classification
Hidden Web Database Classification - COMS 6111 Project2

## Algorithm:
### Part1: Classification:
The classification algorithm is based on the QProber database classification algorithm that was discussed in class. The inputs contains a list of category C, a database D, coverage threshold, specificity threshold, and the Especificity vector. The output is the classfication of the database. If current input category C is a leaf, then return the leaf node {C} as result. Otherwise, for all subcategories Ci of C, if Especificity(D, Ci) is larger or equal than the specificity threshold and Ecoverage(D, Ci) is larger or equal than the coverage threshold, then classification result equals current result union with the classfication result of the subcategory Ci. If the result for classification is null, then return the input category C as result, else return classification result. The confusion matrix adjustment step was ignored for this project.

### Part2: Generate Content Summary:

#### Part2a: Document Sampling:
For each category node C that was visited while we classify database D, for each query q associated with this node retrieve the top 4 pages returned by Bing for the query.

#### Part2b: Content Summary Construction:
After we obtained one document sample for each category node, we compute teh document frequency of each word in the document. Then output them as txt files.

## Implementation:
Category class is used to represent informations of a category. It keeps the the set of queries that related to that category. For example, we issue the query "diet" at the Root level to compute the Specificity and Coverage of "Health" category. The "diet" query will be stored in the "Health", which is an instance of "Category". Each category class also stores the subcategories using a map. For classification, we implement the algorithm as a recursive function, which computes the Coverage and Specificity of each category at the current level. The function will recursively call itself and go to next level if we can found a subcategory with both Coverage and Specificity above threshold. Otherwise, it will stop at current level. We used the Bing Search API to get the total number of documents for each query and derive the Coverage by taking the sum of all queries associated with each category. ExecutorService was used to issue all the queries of each level in parallel. We also implemented a CoverageWorker class which is responsible for getting the total number of documents for a particular query. They will be submit to a thread pool and executed all at once. The performance is improved in this way. The classify function will then return a list of categories representing the path of the classification as result. After that, this result will be passed to the summary function. The summary function will first generate a corresponding list, each node of which stores a set of URLs of those top 4 pages for the queries for each category in the path. Then the summary function assign urls to each category node (add the urls of subcategories to the url list of parent category). Using this result, the summary function can get the set of words for each web page URL using SummaryWorker class, which is a wrapper for invoking lynx. Finally, the summary function construct a treemap that contains the word and it's document frequency for each category. TreeMap is used because the entries of the keymap are all sorted by key. In the end, the summary function generate a txt file that contains the word and document frequency for each category node.

## Build and run: 
`mvn clean install`

`mvn package`

`./run.sh <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>`


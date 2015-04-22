# JobExecutor
Job Executor 
============

Job Executor let's you execute a Job with a given JobId and a UserName. 

#Assumptions:
 	
   1.  It is possible to have multiple Job Executors can run for a given Application- this is to allow a use case specific ExecutorService per Job Executor
   2.  If only ONE Job Executor is needed, the design will be slightly different, by using the right creational pattern for the JobExecutor AND the map of current running jobs
   3. ONLY one instance of a given JobId can run at any time</li>
   4. If a user attempts to execute a current running job, the current userName associated with the job is returned
   5. This is a low throughput executor, uses the same lock for reads and writes
   6. runningJobsMap : holds a map of jobId and jobStatus for jobs currently running. 
   7. Note that we are using a HashMap instead of a ConcurrentHashMap, to enable blocking read-writes.
   8. A case specific Executor service can be injected into the Job Executor to run jobs
   9. The executor uses a ReentrantLock to keep the reads and writes to the map atomic.
 
 ##executeJobForUser:
  

Java Docs
---------

JavaDocs are located at : 

site/apidocs/index.html

Building JobExecutor
--------------------
Just run: 

mvn clean install

Running Tests
-------------

To execute all the unit tests, run: 

mvn test




 



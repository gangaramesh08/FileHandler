Problem statement:
------------------

There is a very big file present on the server. You need to develop/implement REST APIs for following functionality.

   1) Download file API : To download the given file.
                          On accessing this API the file should get started downloading.
   2) Delete file API   : To delete the big file.
                          On accessing this API the file should get deleted.
   3) Create file API   : To create a copy of the big file.
                          On accessing this API a copy of the file should get created.  
  

Note following :
   Use Spring boot.
   Use Spring MVC.
   Annotation based configuration.
   Usage of Exception handling & Logging.
   Swagger documentation for API.
   
 Design and Implementation:
 -------------------------

The assignment involves implementing a project which has the capability to handle operations on Large files. As part of the assignment, I have implemented the Download, Create and Delete APIs for large files. I have tried my best to make the design as simple and extensible as possible so that the project can be easily extended to cater to additional operations like multiple data sources, distributed processing, etc.

The generic design assumptions are:
Storage location
Local storage (local to the system where the application is hosted
Remote storage (external to the application server. This can be any external storage like FTP server, AWS S3, etc)
The Spring Repository JPA approach is followed so that any DB can be used.

The following are the design details that were considered as part of each of the APIs.

Create API (copy files)
---------------------
Since we are dealing with very large files here, there is a restriction on the direct copy of a source file as direct copy will lead to memory exhaustion.
In order to optimize this and to ensure that the application can cater to very large file processing, I have decided to introduce the concept of “chunking” data into smaller parts.
For a generic scenario, I have configured the CHUNK_SIZE = 10Mb. This is a configuration parameter that can be tweaked based on the requirement. In future, we can also introduce the possibility of dynamic CHUNK_SIZE (altering the chunk size based on the file size of the request) to improve the performance even further.
As part of the copy process, I have divided the original file to be copied into chunks and each of these are read via offsets. All of these chunks are read in parallel using the multithreading approach ( I have used Executor framework for the same). As a result of this, the file will be read in chunks parallelly, and we write these as individual “part-files” in the storage location. Since these are downloaded in parallel, the time taken will be Size of File / Chunk Size faster than using a single source stream.
Once all the chunks are created, I have used a merge processor to merge the “part-files” into the whole large file.

Delete API
------------
A delete request is honored if the file to be deleted is not locked by any present operation like download or copy. At present, the delete request is honored only if the file is not locked for any other operation.
In future, we can implement a queue based architecture to ensure that no requests are dropped.

Download API
------------
For download operation, I have followed the “part-file” approach. The advantage of this approach is that we can send the file as part to the client so that even the network bandwidth can be optimized for better performance.
In future, implementing a storage cache can further improve the performance. I.e. if the same file is requested from the server (which can be a local or remote server as mentioned above), the file can be directly served from the cache.



Future improvements
---------------------
Due to the shortage of time, I could only summarize and think about these possible enhancements
Using a distributed system to download data to further increase the performance and efficiency.
Implementing a Queue framework (Kafka, RabbitMQ, etc) to ensure that in case multiple requests are triggered on the same large file, all the requests are honored.
Implementing a storage cache so that if the same files are requested for copy or download, the file can be served from the cache directly.
I have mocked the DB structures in some scenarios due to storage of time. The code can be enhanced easily as we are following the Abstraction principle. By just switching out the contents of the Repository methods, we can alter the database system.

 

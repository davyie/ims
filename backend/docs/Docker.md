What is the difference between docker and docker desktop? 
- Docker Client is the one in the terminal which interacts with Docker desktop. Docker desktop includes the 
    - docker engine
    - docker compose containers

How does docker engine work? 
- this is the underlying technology which we use to run containers. 

How does networking work with containers? 
- The container has an IP address but the docker network exists within a virtual machine, ie. docker engine. 

How does docker work under the hood? 
- Docker engine creates a virtual machine. In this virtual machine we run containers. These containers map their IP addresses to IP addresses on the virtual machine. 

How can we create docker image with Maven? 
- Spring boot has it built-in. 
Additionally we can also specify the image name by adding <build> tag in the parent pom file. We just need to create a Dockerfile in the root directory, ie, the top folder of the project. 

How is a dockerfile built? 
- The keywords: 
FROM 
WORKDIR
COPY 
RUN 
EXPOSE 
USER 
CMD 

What does FROM do? 
- This command creates a base image which we are building our image from. 

What does WORKDIR do? 
- This specifies the working directory where files will be copied to and run from. 

What is COPY? 
- It is self-explanatory. It means that we copy file from host machine to image. 

What is RUN? 
- this tells the image to run a specific script or commands. This could be mvn clean install or npm commands. 

What is EXPOSE? 
- This exposes the port from container to outside. 

What is ENV? 
- This command tells docker to set environment variables. 

What is USER? 
- This instruction sets the default user for all subsequent instructions. 

What is CMD?
- This is the instruction which runs once containers are create from the image. 

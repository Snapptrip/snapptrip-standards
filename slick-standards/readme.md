Motivation
=============================================

This is a series of examples on how to map database/scala objects using slick. 


Usage
=============================================

Preparation
---------------------------------------------

\1. Install docker:
```
sudo apt-get install docker.io
sudo usermod -aG docker $(whoami)
```   

\2. Create a clean postgres docker image with postgis/python installed on it:
```
./docker-build.sh
```

\3. Run the docker image using (and halt after tests).
```
docker run -it --publish 33055:5432 postgres-sample
```

\3.1. if port **33055** is already being used, publish postgres connector on another port. then change the port on `DatabaseConnector` object `src.setPortNumber(33055)` accordingly. 
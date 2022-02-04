# Running

Build and run the container first:

`docker build --tag auth-proxy .`

`docker run -d -p 8080:8080 auth-proxy`

Give it a couple of minutes to build.

Then send some request that will be blocked(just for seconds, so be quick):

`curl -H 'Auth-Key: 42' 'localhost:8080/?q=whatevs' -v`

Unblock the request by sending a POST:

`curl -H 'Auth-Key: 42' -X POST 'http://localhost:8080/authorize'`

# Configuration

Tune `src/main/resources/application.conf` to your will and rebuild the container.
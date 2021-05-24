# Invoicify
Capstone project

### Team name: Chaos

Invoicify is tool for contractors to record and bill companies for services.

Each invoice contains brief details about the products and services purchased. These products and services are commonly referred to simply as items on the invoice. Overall the software works like so:

* A contractor creates an invoice when a company purchases a good or service.
* They add items to the invoice for services rendered.
* The invoice is sent to the companies for payment.
* The contractor marks the invoice as paid when company pays the invoice.

## Setup

### Docker

#### Run

##### Docker Compose

To bring up the docker containers you must create and start them.

```
docker-compose up
```

*_Note: This will actively log your terminal so you have to CTRL-C to stop._*

#### Docker Build _(Manual)_

```
docker build -t invoicify:dev .
```

##### Docker _(Manual)_

```
docker network create --driver bridge invoicify-network
docker run --name invoicify_pg --network invoicify-network -e POSTGRES_DB=invoicify_db -e POSTGRES_PASSWORD=rpvGg9DR32CakNu -d postgres
docker run --name invoicify_app --network invoicify-network -p 9000:8080 -e PORT=8080 -e SPRING_PROFILES_ACTIVE=docker -d invoicify:dev
```

##### Cleanup _(Manual)_

```
docker stop invoicify_app invoicify_pg
docker rm invoicify_app invoicify_pg
docker network rm invoicify-network
docker rmi invoicify:dev
```

Live App URL: http://chaos-invoicify.herokuapp.com

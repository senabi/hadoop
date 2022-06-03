# docker-apache-hadoop

Apache Hadoop 3 dockerisation

# Useful command

## Build the image manually

You can launch the build directly from a git branch

`docker build -t local-hadoop:3.3.3 .`

---

## Launching with docker compose

Build is automatic at first launch with _docker-compose_

`docker-compose up --remove-orphans`

Check **app\traefik\conf\traefik.yml** to deactivate swarm

### Undeploy and clean

`docker-compose down`

`docker-compose rm`

### TODO:

1. Hacer xd
1. Compilar InvertedIndex
1. Hacer PageRank
1. Buscar Datos
1. Aumentar la version del debian/ubuntu?

---

# KokoSky Inc Weather API service

#### Tasks done so far
- Created __version 1.0.0__ of the API documentation using __Swagger__ based on OPEN API specification.
- Created __add, delete, get, and update__ locations API with 100% unit test coverage of all methods in repository, service and controller using `jUnit and mockito`.
- Implemented soft delete for location data by setting `trashed` field to `true`

#### Work yet to be done
- [ ] Add CI/CD using Github actions to build the program and run unit and integration tests on push to main.
- [ ] PUT `api/v1/hourly/locationCode` still not fully implemented. [Service layer]

## Essential docker commands used in project
- `docker-compose up -d` to start the postgresSQL container
- `docker ps` to verify postgres is running
- `docker exec -it <container_name> psql -U koko -d weatherdb`
- Inside psql shell, `\l` to check if weatherdb exists, `\dt` to list tables, `\q` to exit the psql shell.
# RPG Bank

A microservice providing the RPG with a bank. Bank accounts
are associated with characters via character IDs.

Important [notes](#notes) are found below. So is a [TODO](#todo).

## Documentation

### Javadoc

`make doc`, then point browser at `./target/site/apidocs/index.html`
for app documentation, and at `./target/site/testapidocs/index.html`
for test documentation.

### Swagger

In development, `make run` then point browser to
`http://localhost:8080/swagger-ui/index.html`. For JSON navigate
to `http://localhost:8080/v3/api-docs`.

## Build

`make build`

## Execution

### Database

```
make launch_db
```

### Server

The Makefile expects `.env`; `.envs/sample` shows the variables expected.
They include:
* `API_VERSION`: the desired API version number, not including the prefix `v`.
  For example setting this variable to 1 anchors the API at `/v1`.
* `LOG_LEVEL`, which must be one of: `TRACE`, `DEBUG`, `INFO`, `WARN`,
  `ERROR`, or `FATAL`.
* `ENVIRONMENT`, which must be either `dev` or `prod`.

After setting them:
```
make run
```

### Client

```
make psql
```
Again, this uses the vars defined in `.env`.

## DB migrations

Each migration file should be placed in `db/migration`.

Run `make migrate` to bring the migrations up to date.

Baeldung [recommends](https://www.baeldung.com/database-migrations-with-flyway)
the use of IntelliJ IDEA plugin
[JPA Buddy](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)
to automate the generating of migrations. See also
Baeldung's [page on JPA Buddy](https://www.baeldung.com/jpa-buddy).
Unfortunately automated migration versioning requires IntelliJ Ultimate
Edition, which is not free of charge.

## Known Problems

See [this pull request](https://github.com/spring-projects/spring-boot/issues/37982)
about Spring's brokenness w.r.t. docker compose. Until they fix it, the
POM dependency should remain commented out.

### Warnings about annotation `When.MAYBE`

Ignore them. This doesn't appear to be the fault of Spring itself, but
the Spring team do
[have plans](https://github.com/spring-projects/spring-framework/issues/28797#issuecomment-1587380981)
to solve the problem.

### Javadoc `{@link}`

Javadoc does not always convert a `@link` to an HTML link. Oracle's
[Java SE 8 documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
says:
```
Note: The {@link} tag has a bug in overview documents in Java SE 1.2.
The text appears correctly but has no link.
```

## Testing

`make test`

### graphiql

`make run`, then point browser to `http://localhost:8080/graphiql`.

Enter `query { accounts { id } }` in the query window (top left, not bottom pane).
Response should appear in the right-hand pane.

## TODO

Containerize.

## Notes

### Private members of classes

Yes, owing to C++, I like to put them at the end of a class.
Yes, I know that in Java it's more common to put them at the beginning.
I'm more than happy to follow your coding style when working on your
project. :wink:
#!make

include .env
export $(shell sed 's/=.*//' .env)


.PHONY: build \
		build_tests \
		build_all \
        run \
        launch_db \
        install_dependencies \
        clean
#        migrate \


build_all: build build_tests

build:
	mvn compile

up: launch_db run

run: args
	spring_profiles_active=$(ENVIRONMENT) mvn spring-boot:run

build_tests:
	mvn test-compile

test: version
	mvn test

#migrate:
#	mvn clean flyway:migrate -Dflyway.configFiles=flyway.conf

launch_db: db
	docker run --name pg \
		-e POSTGRES_DB=$(DB_NAME) \
		-e POSTGRES_USER=$(DB_USER) \
		-e POSTGRES_PASSWORD=$(DB_PASS) \
		-p $(DB_HOST):$(DB_PORT):5432 \
		-d postgres:$(DB_IMAGE_TAG)

doc:
	mvn javadoc:javadoc
	mvn javadoc:test-javadoc

doc_quiet:
	mvn javadoc:javadoc -quiet
	mvn javadoc:test-javadoc -quiet

install_dependencies:
	mvn dependency:resolve

clean:
	mvn clean

###########################################################

args: env version

###########################################################

env:
ifndef ENVIRONMENT
	$(error ENVIRONMENT undefined)
endif

version:
ifndef API_VERSION
	$(error API_VERSION undefined)
endif

###########################################################

db: db_image_tag db_host db_port db_name db_user db_pass

###########################################################

db_image_tag:
ifndef DB_IMAGE_TAG
	$(error DB_IMAGE_TAG undefined)
endif

db_host:
ifndef DB_HOST
	$(error DB_HOST undefined)
endif

db_port:
ifndef DB_PORT
	$(error DB_PORT undefined)
endif

db_name:
ifndef DB_NAME
	$(error DB_NAME undefined)
endif

db_user:
ifndef DB_USER
	$(error DB_USER undefined)
endif

db_pass:
ifndef DB_PASS
	$(error DB_PASS undefined)
endif

.PHONY: docker-up
docker-up:
	mvn spring-boot:build-image
	USER=root PASSWORD=password docker-compose up -d

.PHONY: docker-down
docker-down: ## Stop docker containers and clear artefacts.
	docker-compose -f docker-compose.yaml down
	docker system prune

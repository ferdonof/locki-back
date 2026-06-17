.PHONY: help build build-admin build-reservations up down logs logs-admin logs-reservations restart clean push

# Colors
GREEN  := \033[0;32m
YELLOW := \033[0;33m
RED    := \033[0;31m
NC     := \033[0m # No Color

# Default target
.DEFAULT_GOAL := help

help: ## Show this help message
	@echo "$(GREEN)Locki Docker Management Commands$(NC)"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "$(YELLOW)%-30s$(NC) %s\n", $$1, $$2}'
	@echo ""
	@echo "$(GREEN)Examples:$(NC)"
	@echo "  make build              # Build both services"
	@echo "  make build-admin        # Build only admin service"
	@echo "  make up                 # Start all services"
	@echo "  make logs-admin         # Show admin service logs"
	@echo "  make down               # Stop all services"

build: ## Build Docker images for both services
	@echo "$(YELLOW)Building Docker images...$(NC)"
	@docker build -f manage-administration/Dockerfile -t locki-admin:latest .
	@docker build -f manage-reservations/Dockerfile -t locki-reservations:latest .
	@echo "$(GREEN)✓ Build completed$(NC)"

build-admin: ## Build Docker image for manage-administration
	@echo "$(YELLOW)Building locki-admin image...$(NC)"
	@docker build -f manage-administration/Dockerfile -t locki-admin:latest .
	@echo "$(GREEN)✓ locki-admin built$(NC)"

build-reservations: ## Build Docker image for manage-reservations
	@echo "$(YELLOW)Building locki-reservations image...$(NC)"
	@docker build -f manage-reservations/Dockerfile -t locki-reservations:latest .
	@echo "$(GREEN)✓ locki-reservations built$(NC)"

up: ## Start all services
	@echo "$(YELLOW)Starting services...$(NC)"
	@docker-compose -f docker-compose-full.yaml up -d
	@echo "$(GREEN)✓ Services started$(NC)"
	@echo ""
	@echo "$(GREEN)Services available at:$(NC)"
	@echo "  Admin:        http://localhost:8080"
	@echo "  Reservations: http://localhost:8081"

down: ## Stop all services
	@echo "$(YELLOW)Stopping services...$(NC)"
	@docker-compose -f docker-compose-full.yaml down
	@echo "$(GREEN)✓ Services stopped$(NC)"

down-volumes: ## Stop all services and remove volumes
	@echo "$(YELLOW)Stopping services and removing volumes...$(NC)"
	@docker-compose -f docker-compose-full.yaml down -v
	@echo "$(GREEN)✓ Services stopped and volumes removed$(NC)"

ps: ## Show running containers
	@docker-compose -f docker-compose-full.yaml ps

logs: ## Show logs for all services
	@docker-compose -f docker-compose-full.yaml logs -f

logs-admin: ## Show logs for manage-administration service
	@docker-compose -f docker-compose-full.yaml logs -f locki-admin

logs-reservations: ## Show logs for manage-reservations service
	@docker-compose -f docker-compose-full.yaml logs -f locki-reservations

logs-db-admin: ## Show logs for admin PostgreSQL
	@docker-compose -f docker-compose-full.yaml logs -f postgres-admin

logs-db-reservations: ## Show logs for reservations PostgreSQL
	@docker-compose -f docker-compose-full.yaml logs -f postgres-reservations

restart: ## Restart all services
	@echo "$(YELLOW)Restarting services...$(NC)"
	@docker-compose -f docker-compose-full.yaml restart
	@echo "$(GREEN)✓ Services restarted$(NC)"

restart-admin: ## Restart manage-administration service
	@echo "$(YELLOW)Restarting locki-admin...$(NC)"
	@docker-compose -f docker-compose-full.yaml restart locki-admin
	@echo "$(GREEN)✓ locki-admin restarted$(NC)"

restart-reservations: ## Restart manage-reservations service
	@echo "$(YELLOW)Restarting locki-reservations...$(NC)"
	@docker-compose -f docker-compose-full.yaml restart locki-reservations
	@echo "$(GREEN)✓ locki-reservations restarted$(NC)"

rebuild: ## Rebuild and restart all services
	@$(MAKE) build
	@$(MAKE) down
	@$(MAKE) up

rebuild-admin: ## Rebuild and restart manage-administration service
	@$(MAKE) build-admin
	@docker-compose -f docker-compose-full.yaml restart locki-admin

rebuild-reservations: ## Rebuild and restart manage-reservations service
	@$(MAKE) build-reservations
	@docker-compose -f docker-compose-full.yaml restart locki-reservations

clean: ## Clean up Docker resources (images, containers, volumes)
	@echo "$(RED)Cleaning Docker resources...$(NC)"
	@docker-compose -f docker-compose-full.yaml down -v
	@docker rmi locki-admin:latest locki-reservations:latest 2>/dev/null || true
	@echo "$(GREEN)✓ Cleanup completed$(NC)"

push: ## Push images to registry (requires DOCKER_REGISTRY env var)
	@if [ -z "$(DOCKER_REGISTRY)" ]; then \
		echo "$(RED)Error: DOCKER_REGISTRY not set$(NC)"; \
		exit 1; \
	fi
	@echo "$(YELLOW)Pushing images to $(DOCKER_REGISTRY)...$(NC)"
	@docker tag locki-admin:latest $(DOCKER_REGISTRY)/locki-admin:latest
	@docker tag locki-reservations:latest $(DOCKER_REGISTRY)/locki-reservations:latest
	@docker push $(DOCKER_REGISTRY)/locki-admin:latest
	@docker push $(DOCKER_REGISTRY)/locki-reservations:latest
	@echo "$(GREEN)✓ Images pushed$(NC)"

health: ## Check health status of all services
	@echo "$(YELLOW)Checking service health...$(NC)"
	@docker-compose -f docker-compose-full.yaml exec locki-admin curl -s http://localhost:8080/actuator/health | jq . && echo "$(GREEN)✓ Admin service healthy$(NC)" || echo "$(RED)✗ Admin service unhealthy$(NC)"
	@docker-compose -f docker-compose-full.yaml exec locki-reservations curl -s http://localhost:8080/actuator/health | jq . && echo "$(GREEN)✓ Reservations service healthy$(NC)" || echo "$(RED)✗ Reservations service unhealthy$(NC)"

# Development targets
dev-build: ## Build locally without Docker (Maven)
	@echo "$(YELLOW)Building with Maven...$(NC)"
	@mvn clean install -DskipTests

dev-admin: ## Run admin service locally
	@echo "$(YELLOW)Starting admin service (requires PostgreSQL running)...$(NC)"
	@mvn spring-boot:run -pl manage-administration/rest-endpoints/boot

dev-reservations: ## Run reservations service locally
	@echo "$(YELLOW)Starting reservations service (requires PostgreSQL running)...$(NC)"
	@mvn spring-boot:run -pl manage-reservations/manage-reservations-rest-endpoints/boot

.PHONY: $(MAKECMDGOALS)


# ☁️ CloudCompare AI

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://docker.com)
[![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)](https://jenkins.io)
[![SonarQube](https://img.shields.io/badge/SonarQube-4E9BCD?style=for-the-badge&logo=sonarqube&logoColor=white)](https://sonarqube.org)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

CloudCompare AI is a multi-cloud comparison and recommendation platform that helps users evaluate cloud infrastructure services across AWS, Azure, Google Cloud Platform (GCP), Oracle Cloud Infrastructure (OCI), and Alibaba Cloud.

The platform analyzes cloud resources such as compute, storage, pricing, and regional availability to generate intelligent recommendations based on cost, performance, and optimization priorities.

---

# 🚀 Features

- Multi-cloud infrastructure comparison
- AI-assisted recommendation engine using Groq + Llama 3.1
- Cost and performance analysis
- Region-wise cloud comparison
- Interactive analytics dashboard
- JWT-based authentication and authorization
- RESTful API architecture
- Dockerized deployment
- Jenkins CI/CD integration
- SonarQube static code analysis
- Rate limiting and centralized exception handling

---

# 🏗️ System Architecture

![CloudCompare AI Architecture](cloud_compare_ai_architecture_1778263045760.png)

```mermaid
graph TD
    User((User)) --> Frontend[Frontend Dashboard]
    Frontend --> API[Spring Boot REST API]

    API --> Auth[Spring Security + JWT]
    API --> Service[Business Logic Layer]
    Service --> AI[Groq AI API]
    Service --> DB[(MySQL Database)]

    subgraph DevOps Pipeline
        GitHub[GitHub Repository]
        Jenkins[Jenkins CI/CD]
        Sonar[SonarQube Analysis]
        Docker[Docker Deployment]
    end

    GitHub --> Jenkins
    Jenkins --> Sonar
    Jenkins --> Docker
```

---

# 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 3 |
| Security | Spring Security, JWT |
| Database | MySQL, H2 |
| ORM | Spring Data JPA, Hibernate |
| AI Integration | Groq API, Llama 3.1 |
| Frontend | HTML, CSS, JavaScript, Chart.js |
| DevOps | Docker, Jenkins, SonarQube |
| Testing | JUnit 5, Mockito, JaCoCo |

---

# 📂 Project Structure

```text
cloudcompare-ai/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   └── static/
│   │
│   └── test/
│
├── docker/
├── docs/
├── screenshots/
├── .env.example
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# 🔐 Security Features

- JWT-based stateless authentication
- Protected API endpoints
- Secure environment variable configuration
- Centralized exception handling
- Rate limiting for API abuse prevention
- Restricted CORS configuration

---

# ⚡ Performance Optimizations

- Java Virtual Threads (Project Loom)
- Caffeine caching
- GZIP compression
- Optimized REST API responses
- Resilience4J circuit breaker integration

---

# 📊 Core Functionalities

## Cloud Comparison

Compare services across:
- AWS
- Azure
- GCP
- OCI
- Alibaba Cloud

Comparison parameters:
- vCPUs
- RAM
- Storage
- Region
- Pricing
- Performance score

---

## AI Recommendation Engine

The AI recommendation engine:
- Analyzes infrastructure requirements
- Evaluates provider pricing
- Calculates ranking scores
- Suggests optimized cloud providers
- Generates AI-assisted recommendations

---

## Dashboard Analytics

Dashboard includes:
- Cost comparison charts
- Performance analysis graphs
- Ranking visualization
- Region-based insights
- Optimization recommendations

---

# 🔌 API Endpoints

| Method | Endpoint | Description | Authentication |
|---|---|---|---|
| POST | `/api/auth/signup` | Register new user | Public |
| POST | `/api/auth/login` | User login | Public |
| GET | `/api/test` | Health check API | Public |
| POST | `/api/compare` | Compare cloud services | JWT Required |
| POST | `/api/ai-compare` | AI-powered recommendations | JWT Required |
| GET | `/api/regions` | Fetch provider regions | JWT Required |

---

# 📥 Sample API Request

## Compare Cloud Services

```json
POST /api/compare

{
  "provider": "AWS",
  "vcpu": 4,
  "ram": 16,
  "storage": 200,
  "region": "ap-south-1",
  "priority": "cost"
}
```

---

# 📤 Sample API Response

```json
{
  "recommendedProvider": "AWS",
  "estimatedMonthlyCost": 82.45,
  "performanceScore": 91,
  "optimization": "Best balance between cost and performance"
}
```

---

# 🐳 Docker Deployment

## Build Application

```bash
./mvnw clean package
```

## Run Using Docker Compose

```bash
docker-compose up -d --build
```

---

# ⚙️ Environment Variables

Create a `.env` file:

```env
GROQ_API_KEY=your_groq_api_key
JWT_SECRET=your_jwt_secret
DB_URL=jdbc:mysql://localhost:3306/cloudcompare_ai
DB_USERNAME=root
DB_PASSWORD=your_password
```

---

# 🧪 Testing

Run unit and integration tests:

```bash
./mvnw test
```

Generate JaCoCo coverage report:

```bash
./mvnw jacoco:report
```

---

# 📸 Screenshots

## Dashboard
(Add screenshot here)

## Cloud Comparison
(Add screenshot here)

## AI Recommendations
(Add screenshot here)

## Jenkins Pipeline
(Add screenshot here)

---

# 🚦 Troubleshooting

| Issue | Cause | Solution |
|---|---|---|
| 429 Too Many Requests | Rate limit exceeded | Wait before retrying |
| Invalid JWT Token | Token expired | Login again |
| MySQL Connection Failed | Database unavailable | Verify DB service |
| Groq API Error | Invalid API key | Check `.env` configuration |

---

# 🔮 Future Enhancements

- Kubernetes deployment support
- Real-time cloud pricing APIs
- Terraform integration
- AI chatbot assistant
- Multi-user collaboration
- Advanced cloud cost prediction
- Carbon footprint estimation

---

# 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push the branch
5. Open a Pull Request

---

# 📄 License

This project is licensed under the MIT License.

---

# 👨‍💻 Author

Developed by Raghavendra

GitHub:
https://github.com/raghavendra2006

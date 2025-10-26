Shodh AI – Contest Judge Backend

A high-performance backend service designed to power online coding contest platforms. Shodh AI provides live judging of user submissions by executing code in secure, sandboxed Docker environments.

✨ Key Features

Secure Code Execution: Leverages Docker to run user-submitted code in isolated, sandboxed containers, preventing malicious code and ensuring system stability.

Live Judging: Provides real-time feedback on submissions (e.g., Accepted, Wrong Answer, Time Limit Exceeded, Compilation Error).

RESTful API: A clean and robust API for handling problem submissions, retrieving results, and managing contest status.

Scalable Architecture: Built with Java and Gradle for a reliable and scalable service capable of handling concurrent submissions.

🛠️ Tech Stack

Core Framework: Java (Spring Boot recommended)

Build Tool: Gradle

Containerization: Docker

🚀 Getting Started

Follow these instructions to get a local copy up and running for development and testing.

Prerequisites

Ensure you have the following tools installed on your system:

Java (JDK 17 or newer)

Gradle

Docker Desktop (must be running)

An active internet connection (for initial dependency downloads)

Installation & Execution

Clone the repository:

git clone [https://github.com/utkarsh0830/shodh_ai.git](https://github.com/utkarsh0830/shodh_ai.git)
cd shodh_ai


Run the application:
This command will clean the previous build, build the project, and run the main application.

./gradlew clean bootRun


The server will start, and you can access it at http://localhost:8080 (or as configured in your application.properties).

⚙️ Configuration

Application settings can be configured in src/main/resources/application.properties.

Key properties to configure (examples):

# Server port
server.port=8080

# Docker connection settings
docker.host=unix:///var/run/docker.sock


You can also override these properties using environment variables.

🔌 API Documentation

Here are the primary endpoints for interacting with the service.

(Note: Please update these examples to match your actual API)

1. Submit Code

Submits a solution for a specific problem. The service will queue it for judging.

Endpoint: POST /api/v1/submit

Request Body:

{
  "problemId": "A101",
  "language": "cpp",
  "sourceCode": "#include <iostream>..."
}


Success Response (202 Accepted):
Returns a unique submission ID, which can be used to poll for results.

{
  "submissionId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}


2. Get Submission Status

Retrieves the current status and result of a submission.

Endpoint: GET /api/v1/status/{submissionId}

Example Path: /api/v1/status/f47ac10b-58cc-4372-a567-0e02b2c3d479

Success Response (200 OK - Judged):

{
  "submissionId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "problemId": "A101",
  "status": "Accepted",
  "executionTimeMs": 120,
  "memoryUsageMb": 4.5
}


Success Response (200 OK - Pending):

{
  "submissionId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
  "problemId": "A101",
  "status": "Pending"
}


🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are greatly appreciated.

Fork the Project

Create your Feature Branch (git checkout -b feature/AmazingFeature)

Commit your Changes (git commit -m 'Add some AmazingFeature')

Push to the Branch (git push origin feature/AmazingFeature)

Open a Pull Request


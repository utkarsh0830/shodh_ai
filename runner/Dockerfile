
FROM eclipse-temurin:21-jdk-jammy


RUN apt-get update && apt-get install -y --no-install-recommends \
    bash coreutils procps \
 && rm -rf /var/lib/apt/lists/*


RUN useradd -m -u 1000 runner
USER runner


WORKDIR /work


CMD [ "bash" ]

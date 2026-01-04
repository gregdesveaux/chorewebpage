# Shared chore board

A lightweight Spring Boot app to let two kids take turns on chores. The UI lists chores, shows who is responsible next, and lets the current child mark chores done. When a chore is completed its next due date is calculated (daily or every 3 days) and the task rotates to the other child. The backend can send reminders by email and optionally ntfy push notifications when a chore is due.

## Running locally

1. Ensure Java 17+ and Maven are installed.
2. Install dependencies and launch the server:
   ```bash
   mvn spring-boot:run
   ```
3. Open http://localhost:8080 to use the chore board.

> Note: If your network blocks access to Maven Central you will need connectivity (or a proxy) to download dependencies on first run.

## Configuration

Configuration lives in `src/main/resources/application.yaml` and can be overridden with environment variables or custom YAML/Properties files on the classpath.

Key sections:
- **Kids** (`kids.contacts`): Names plus optional email/phone for each child. Example:
  ```yaml
  kids:
    contacts:
      ONE:
        name: Sam
        email: sam@example.com
        phone-number: +15551234567
        ntfy-topic: kid-one-topic
      TWO:
        name: Riley
        email: riley@example.com
        phone-number: +15557654321
        ntfy-topic: kid-two-topic
  ```
- **Email** (`spring.mail.*`): Configure SMTP host, port, and credentials to deliver email reminders.
- **Push notifications** (`ntfy.*`): Configure the ntfy server base URL (defaults to `https://ntfy.sh`) and optional access token for authenticated servers. Each kid's `ntfy-topic` will receive reminder messages.
- **Notification cadence** (`chore.notifications.interval-ms`): How often the scheduler checks for due chores (defaults to 15 minutes).

The app ships with an H2 file database at `./data/chore-db` and seeds a few sample chores on first run.

## Docker

A simple container build is included:

```bash
docker build -t chore-board .
docker run -p 8080:8080 chore-board
```

Provide mail/SMS environment variables when starting the container to enable notifications.

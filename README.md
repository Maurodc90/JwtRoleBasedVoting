# JWT Role-Based Voting

A small Spring Boot REST API that demonstrates stateless authentication with JSON Web Tokens (JWT) and role-based access control. Four voters can log in and vote for a candidate; an admin can log in to see the results.

## Features

- Login endpoint that issues a signed JWT after verifying the username and password
- Stateless security: no sessions, no cookies, every request carries its own token
- Role-based access: only voters can vote, only the admin can see or reset results
- Passwords hashed with BCrypt
- In-memory vote storage

## Tech stack

- Java 21
- Spring Boot (Web, Security)
- JJWT for token creation and validation
- Gradle

## How it works

1. The client sends its username and password to `/login`.
2. Spring Security's `AuthenticationManager` verifies them. If they are wrong, no token is issued.
3. The server returns a signed JWT.
4. The client sends the token on every later request in the header `Authorization: Bearer <token>`.
5. `JwtAuthFilter` validates the token (signature and expiry) and identifies the user.
6. The access rules in `SecurityConfig` then check that the user's role may reach the endpoint.

## Endpoints

| Method | Endpoint         | Access      | Description                                              |
|--------|------------------|-------------|----------------------------------------------------------|
| POST   | `/login`         | Public      | Form fields `username` and `password`; returns a JWT     |
| POST   | `/vote`          | VOTER       | Plain-text body with the candidate name; 200 if counted, 404 if the candidate does not exist |
| GET    | `/results`       | ADMIN       | Returns the vote count of every candidate                |
| POST   | `/results/reset` | ADMIN       | Sets all counts back to zero                             |
| GET    | `/`              | Any logged-in user | Welcome message                                   |

Requests without a valid token, or with the wrong role, receive `403 Forbidden`.

## Demo users

Hardcoded for practice only. All passwords are `1234`.

| Username | Role  |
|----------|-------|
| Mauro    | ADMIN |
| Elcin    | VOTER |
| Nurten   | VOTER |
| Yagmur   | VOTER |
| Orhan    | VOTER |

Candidates: `Gultekin`, `Ozhan`, `Ozer`.

## Running the project

```bash
./gradlew bootRun
```

The app starts on `http://localhost:8080`.

## Trying it out

Log in as a voter and copy the token from the response:

```bash
curl -X POST http://localhost:8080/login -d "username=Elcin" -d "password=1234"
```

Vote for a candidate:

```bash
curl -X POST http://localhost:8080/vote \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: text/plain" \
  -d "Gultekin"
```

Log in as `Mauro` and read the results:

```bash
curl http://localhost:8080/results -H "Authorization: Bearer <admin-token>"
```

## Project structure

```
controller/   VotingController: login, vote, results endpoints
service/      VotingService: in-memory vote storage
security/     SecurityConfig, JwtUtil, JwtAuthFilter
```

## Known limitations and possible improvements

- The signing key is generated at startup, so every token becomes invalid when the app restarts. It should be loaded from configuration.
- A wrong password returns 403; 401 Unauthorized would be more accurate.
- Users and votes are stored in memory. A database would make them persistent.
- A voter can vote more than once.
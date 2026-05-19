# API Specification - be-management-autentikasi

## Runtime

- Default port: `8080`
- Config key: `server.port` via `SERVER_PORT`
- Local database: H2 in-memory

## Roles

- `ADMIN` (represents Admin Utama in current phase)
- `BURUH`
- `MANDOR`
- `SUPIR`

## Auth Endpoints

### POST `/api/auth/signin`

Request:

```json
{
  "email": "user@example.com",
  "password": "secret"
}
```

Response:

```json
{
  "token": "jwt-token"
}
```

### POST `/api/auth/signup`

Registers `BURUH`, `MANDOR`, or `SUPIR` users.
`ADMIN` registration is rejected by current implementation.

### POST `/api/auth/signout`

Stateless logout acknowledgment endpoint.

## User Endpoints

### GET `/api/users/me`

Returns authenticated user identity (resolved from JWT/email subject).

### GET `/api/users`

Returns list of users.

### GET `/api/users/{id}`

Returns user by id.

### POST `/api/users/{buruhId}/assign-mandor/{mandorId}`

Assign/reassign buruh to mandor.

### POST `/api/users/{buruhId}/unassign-mandor`

Unassign buruh from mandor.

## Internal Read Endpoints (for service integration)

### GET `/internal/users/{id}/identity`

Safe identity DTO (no password).

### GET `/internal/buruh/{buruhId}/supervisor`

Returns supervisor mapping:

```json
{
  "buruhId": 2,
  "buruhNama": "Budi",
  "mandorId": 3,
  "mandorNama": "Pak Mandor",
  "active": true
}
```

### GET `/internal/mandors/{mandorId}/buruh`

Returns buruh list under mandor.

### GET `/internal/mandors/{mandorId}/buruh/{buruhId}/assignment`

Returns assignment check:

```json
{
  "mandorId": 3,
  "buruhId": 2,
  "assigned": true
}
```


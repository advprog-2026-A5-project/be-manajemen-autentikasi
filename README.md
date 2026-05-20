# be-management-autentikasi

## Local Run

- Default port: `8080`
- Override port: `SERVER_PORT`
- Database: in-memory H2 (dev/local)

Run:

```powershell
./gradlew bootRun
```

## Integration Notes

- Role mapping in this phase:
  - `ADMIN` = Admin Utama
  - `BURUH` = Buruh Sawit
  - `MANDOR` = Mandor
  - `SUPIR` = Supir Truk
- JWT subject is email.
- Login response remains:

```json
{ "token": "..." }
```


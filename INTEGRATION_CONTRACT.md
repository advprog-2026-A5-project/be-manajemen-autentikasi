# Integration Contract (Auth-Kebun-Hasil Panen)

## Scope
This document defines integration ownership and identifiers for MySawit sibling services.

## Source of Truth
- Auth (`be-management-autentikasi`) owns:
  - user identity
  - login/JWT
  - roles
  - Buruh-Mandor assignment
- Kebun (`be-managemen-kebun`) owns:
  - kebun identity
  - kebun code
  - kebun coordinates
  - Mandor-Kebun assignment
- Hasil Panen (`be-management-hasil-panen`) owns:
  - harvest report
  - harvest status lifecycle
  - approval/rejection decisions
  - transport eligibility
  - payroll outbox payload

## Role Mapping
Current Auth role strings are used as-is:
- `ADMIN` = Admin Utama
- `BURUH` = Buruh Sawit
- `MANDOR` = Mandor
- `SUPIR` = Supir Truk

Note: do not rename `ADMIN` to `ADMIN_UTAMA` in this integration phase.

## Identifier Strategy
- Auth user identifier is `Long` (`User.id`).
- Cross-service user references for Buruh and Mandor must use Auth `Long` IDs.
- JWT subject remains email in current phase.

## Integration Read APIs Required by Consumers
Auth must provide read-only APIs for:
- current authenticated user (`/api/users/me`)
- Buruh supervisor lookup
- Buruh-to-Mandor assignment verification
- Buruh list under Mandor

All integration responses must be DTO-based and must not expose password.

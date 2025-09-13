INSERT INTO users (full_name, email, password_hash, role, is_active)
VALUES (
  'Super Admin',
  'admin@coffee.local',
  '$2a$10$ZlD1D8L4iI6iE0NcR2EHeuJd1kDpAExfYfBKoKfA.3gIVUovrQfhm', -- bcrypt("09072006")
  'ADMIN',
  1
);

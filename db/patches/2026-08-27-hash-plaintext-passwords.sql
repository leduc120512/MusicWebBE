-- ---------------------------------------------------------------------------
--  2026-08-27  Re-hash the remaining clear-text passwords with BCrypt.
--
--  SecurityConfig used NoOpPasswordEncoder and AuthController compared the raw
--  string, so 30 of 36 accounts - the ones already holding a BCrypt hash from an
--  earlier version - could never sign in. The encoder is now BCryptPasswordEncoder;
--  this script converts the 6 rows that were still clear text so they keep the
--  same passwords.
--
--  Idempotent: rows already hashed are left alone by the WHERE clause.
-- ---------------------------------------------------------------------------

USE `music_db`;

UPDATE `users` SET `password` = '$2a$10$XbN6vjR/ZSDczuM1cMxoBuRHZB6LcJZjR4rWxbc/5RNhLMucLzxuW' WHERE `id` = 20;  -- demo123 / leduc
UPDATE `users` SET `password` = '$2a$10$f8oYETqQnASJxkMdqGkxsuqi.GhXA9YfCvU3TqczdQfbTgAxFsk06' WHERE `id` = 36;  -- duc12 / 123456
UPDATE `users` SET `password` = '$2a$10$oLK3/TQ04jEQzjSu3rW4h.nILR/B/.CPPDsyxwkkcqZaT.fQH6YC2' WHERE `id` = 37;  -- testu48962 / 123456
UPDATE `users` SET `password` = '$2a$10$hBZIIyGhGh0iQq7b6nk3pu1meXjVRG5T6zaBA/ZCIiiamjDdiu78u' WHERE `id` = 38;  -- hunghn / 1234
UPDATE `users` SET `password` = '$2a$10$hNTsisVoiTeONjaiKYux2u4bUBPHxxYnLiotwx/ZW3MPOU0BVa3UW' WHERE `id` = 39;  -- user1 / 123456
UPDATE `users` SET `password` = '$2a$10$16eobRPBQNdjTAHcXJUQb.95UG4r0qz2LExSHDXHcj2Vk1h2vbVPW' WHERE `id` = 40;  -- user12 / 1234

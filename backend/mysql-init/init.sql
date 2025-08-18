-- Garante que o usuário app existe para qualquer host usando caching_sha2_password
CREATE USER IF NOT EXISTS 'app'@'%' IDENTIFIED BY 'app';
ALTER USER 'app'@'%' IDENTIFIED WITH caching_sha2_password BY 'app';
GRANT ALL PRIVILEGES ON english.* TO 'app'@'%';
FLUSH PRIVILEGES;

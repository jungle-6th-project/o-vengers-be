CREATE
    USER 'bbodok-local'@'localhost' IDENTIFIED BY 'bbodok-local';
CREATE
    USER 'bbodok-local'@'%' IDENTIFIED BY 'bbodok-local';

GRANT ALL PRIVILEGES ON *.* TO
    'bbodok-local'@'localhost';
GRANT ALL PRIVILEGES ON *.* TO
    'bbodok-local'@'%';

FLUSH PRIVILEGES;

CREATE
DATABASE bbodok DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

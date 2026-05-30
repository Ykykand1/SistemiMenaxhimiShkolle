-- 1. Teachers
CREATE TABLE IF NOT EXISTS mesuesit (
    id      SERIAL PRIMARY KEY,
    emri    VARCHAR(50)  NOT NULL,
    mbiemri VARCHAR(50)  NOT NULL,
    email   VARCHAR(100) UNIQUE NOT NULL,
    telefon VARCHAR(20)
);

-- 2. Subjects
CREATE TABLE IF NOT EXISTS lendet (
    id          SERIAL PRIMARY KEY,
    emri_lendes VARCHAR(100) NOT NULL,
    pershkrimi  TEXT,
    kreditet    INTEGER DEFAULT 1
);

-- 3. Teacher <-> Subject (many-to-many, BCNF)
CREATE TABLE IF NOT EXISTS mesuesi_lendet (
    mesuesi_id  INTEGER NOT NULL REFERENCES mesuesit(id) ON DELETE CASCADE,
    lenda_id    INTEGER NOT NULL REFERENCES lendet(id)   ON DELETE CASCADE,
    PRIMARY KEY (mesuesi_id, lenda_id)
);

-- 4. Students
CREATE TABLE IF NOT EXISTS nxenesit (
    id                SERIAL PRIMARY KEY,
    emri              VARCHAR(50)  NOT NULL,
    mbiemri           VARCHAR(50)  NOT NULL,
    email             VARCHAR(100) UNIQUE,
    data_lindjes      DATE,
    klasa             VARCHAR(10),
    data_regjistrimit DATE DEFAULT CURRENT_DATE
);

-- 5. Grades
CREATE TABLE IF NOT EXISTS notat (
    id           SERIAL PRIMARY KEY,
    nxenesi_id   INTEGER      NOT NULL REFERENCES nxenesit(id) ON DELETE CASCADE,
    lenda_id     INTEGER      NOT NULL REFERENCES lendet(id)   ON DELETE CASCADE,
    mesuesi_id   INTEGER      REFERENCES mesuesit(id) ON DELETE SET NULL,
    nota         NUMERIC(4,1) CHECK (nota >= 1.0 AND nota <= 10.0),
    lloji        VARCHAR(50),
    data_dhënies DATE DEFAULT CURRENT_DATE
);

-- 6. Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(20)  NOT NULL CHECK (role IN ('admin','teacher','student')),
    ref_id     INTEGER
);

-- CLEANUP OLD SEED DATA (optional, for clean runs)
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE notat CASCADE;
TRUNCATE TABLE mesuesi_lendet CASCADE;
TRUNCATE TABLE lendet CASCADE;
TRUNCATE TABLE nxenesit CASCADE;
TRUNCATE TABLE mesuesit CASCADE;

-- Restart sequences
ALTER SEQUENCE mesuesit_id_seq RESTART WITH 1;
ALTER SEQUENCE lendet_id_seq RESTART WITH 1;
ALTER SEQUENCE nxenesit_id_seq RESTART WITH 1;
ALTER SEQUENCE notat_id_seq RESTART WITH 1;
ALTER SEQUENCE users_id_seq RESTART WITH 1;

-- SEED TEACHERS
INSERT INTO mesuesit (emri, mbiemri, email, telefon) VALUES
('Arben', 'Hoxha', 'arben.hoxha@school.edu', '+355681112222'),
('Elira', 'Gjoni', 'elira.gjoni@school.edu', '+355683334444'),
('Valbona', 'Duka', 'valbona.duka@school.edu', '+355685556666');

-- SEED SUBJECTS
INSERT INTO lendet (emri_lendes, pershkrimi, kreditet) VALUES
('Matematike', 'Matematika e avancuar dhe algjebra linjare', 5),
('Fizike', 'Mekanika klasike dhe termodinamika', 4),
('Kimi', 'Kimia organike dhe inorganike', 3),
('Programim ne Java', 'Bazat e programimit dhe OOP ne Java', 6),
('Letersi', 'Letersia Shqipe dhe Boterore', 4);

-- SEED TEACHER-SUBJECT MAPPING
INSERT INTO mesuesi_lendet (mesuesi_id, lenda_id) VALUES
(1, 1), -- Arben teaches Matematike
(1, 2), -- Arben teaches Fizike
(2, 3), -- Elira teaches Kimi
(2, 4), -- Elira teaches Programim ne Java
(3, 5); -- Valbona teaches Letersi

-- SEED STUDENTS
INSERT INTO nxenesit (emri, mbiemri, email, data_lindjes, klasa, data_regjistrimit) VALUES
('Enea', 'Krasniqi', 'enea.krasniqi@student.edu', '2008-05-12', 'X-A', '2024-09-01'),
('Sara', 'Leka', 'sara.leka@student.edu', '2008-08-22', 'X-A', '2024-09-01'),
('Artan', 'Basha', 'artan.basha@student.edu', '2008-02-14', 'X-B', '2024-09-01'),
('Besa', 'Rama', 'besa.rama@student.edu', '2008-11-30', 'X-B', '2024-09-01'),
('Genci', 'Pashako', 'genci.pashako@student.edu', '2007-04-18', 'XI-A', '2023-09-01'),
('Lira', 'Kurti', 'lira.kurti@student.edu', '2007-09-05', 'XI-A', '2023-09-01'),
('Ilir', 'Morina', 'ilir.morina@student.edu', '2007-01-25', 'XI-B', '2023-09-01'),
('Teuta', 'Kola', 'teuta.kola@student.edu', '2007-10-10', 'XI-B', '2023-09-01'),
('Krenar', 'Gashi', 'krenar.gashi@student.edu', '2006-03-03', 'XII-A', '2022-09-01'),
('Dona', 'Sinani', 'dona.sinani@student.edu', '2006-07-07', 'XII-A', '2022-09-01');

-- SEED GRADES
-- nota checking constraints: range 1.0 to 10.0
INSERT INTO notat (nxenesi_id, lenda_id, mesuesi_id, nota, lloji, data_dhënies) VALUES
(1, 1, 1, 9.5, 'Test', '2026-05-10'),
(1, 2, 1, 8.0, 'Provim', '2026-05-15'),
(1, 4, 2, 10.0, 'Projekt', '2026-05-20'),
(2, 1, 1, 7.5, 'Test', '2026-05-10'),
(2, 4, 2, 9.0, 'Detyre', '2026-05-18'),
(3, 1, 1, 6.0, 'Test', '2026-05-10'),
(3, 3, 2, 8.5, 'Test', '2026-05-12'),
(4, 5, 3, 9.0, 'Provim', '2026-05-22'),
(5, 4, 2, 9.5, 'Projekt', '2026-05-20'),
(6, 4, 2, 8.0, 'Detyre', '2026-05-18'),
(7, 2, 1, 7.0, 'Test', '2026-05-15'),
(8, 5, 3, 10.0, 'Provim', '2026-05-22'),
(9, 1, 1, 8.5, 'Provim', '2026-05-25'),
(10, 4, 2, 9.0, 'Projekt', '2026-05-20');

-- SEED USERS (SHA-256 encrypted passwords)
-- 'admin' -> 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
-- 'teacher1' -> 59ab7336e76cf0e3e2326759c253ffbc3854eb108157778b77626245a498fa8f
-- 'teacher2' -> df375084931a722ec16a04e532b217a9446d328cf348e3cfc7379f8249d34208
-- 'teacher3' -> 18fef9b8b0e8c8b6ad8565fb9be40f7b0f7961b7f005d5d6bc0fcfc30a7d90d7
-- 'student1' -> d30bc129528f895c10ee08770c538cb31b9ebef1f6305a4176fb16c39f0b182d
-- 'student2' -> 89c565d774d6c41b8c1ca665c71167448dbf3b0e35b756b185ec016c68a0a996
-- ...
INSERT INTO users (username, password, role, ref_id) VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin', NULL),

('teacher1', '59ab7336e76cf0e3e2326759c253ffbc3854eb108157778b77626245a498fa8f', 'teacher', 1),
('teacher2', 'df375084931a722ec16a04e532b217a9446d328cf348e3cfc7379f8249d34208', 'teacher', 2),
('teacher3', '18fef9b8b0e8c8b6ad8565fb9be40f7b0f7961b7f005d5d6bc0fcfc30a7d90d7', 'teacher', 3),

('student1', 'd30bc129528f895c10ee08770c538cb31b9ebef1f6305a4176fb16c39f0b182d', 'student', 1),
('student2', '89c565d774d6c41b8c1ca665c71167448dbf3b0e35b756b185ec016c68a0a996', 'student', 2),
('student3', 'c86d814272186f9ee5d7a6e11894d075ebf9175344ad7b1b4bc5bb4fe2070bb8', 'student', 3),
('student4', '193d5089e02c6114eb130f6b3e7bc23019803ae285888a7ef90731f822aa2f2f', 'student', 4),
('student5', '2df28fa2d515a45a332a688b488737c7689d06bcfeb0df9b3e15b3c3b018b1d9', 'student', 5),
('student6', '89c4456efb96b4bf2d09db3d4d420311f92e032338cf48c3f71c4c810d7e48b8', 'student', 6),
('student7', '2ec1a6dbdb4b2ad60b378036d75d31cf2cf5b6ff4f21db5a7c5c00e1261fa0aa', 'student', 7),
('student8', 'a53569429780076a51d8b4f49463b7e4521448b29db972e2cfc3a033f1ff72d0', 'student', 8),
('student9', 'bf8e146cbbf066e4a2b97087640243454b5df01c107f9c2d11019623e1e21b71', 'student', 9),
('student10', 'a93be9cd5758cfdf139589d812ed3b7497d39c049964526d179c3132e4d07949', 'student', 10);

-- =====================================================
-- SEED DATA — aligned with V1__init.sql
-- All inserts are idempotent (NOT EXISTS guards)
-- =====================================================

-- =====================================================
-- 1. ROLES
-- =====================================================

INSERT INTO roles (name) SELECT 'ADMIN'     WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');
INSERT INTO roles (name) SELECT 'RECRUITER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'RECRUITER');
INSERT INTO roles (name) SELECT 'CANDIDATE' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'CANDIDATE');

-- =====================================================
-- 2. PERMISSIONS
-- =====================================================

INSERT INTO permissions (name) SELECT 'USER_READ'    WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'USER_READ');
INSERT INTO permissions (name) SELECT 'USER_WRITE'   WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'USER_WRITE');
INSERT INTO permissions (name) SELECT 'USER_DELETE'   WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'USER_DELETE');
INSERT INTO permissions (name) SELECT 'JOB_READ'     WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'JOB_READ');
INSERT INTO permissions (name) SELECT 'JOB_WRITE'    WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'JOB_WRITE');
INSERT INTO permissions (name) SELECT 'JOB_DELETE'    WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'JOB_DELETE');
INSERT INTO permissions (name) SELECT 'JOB_APPROVE'  WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'JOB_APPROVE');
INSERT INTO permissions (name) SELECT 'COMPANY_READ'  WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'COMPANY_READ');
INSERT INTO permissions (name) SELECT 'COMPANY_WRITE' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'COMPANY_WRITE');
INSERT INTO permissions (name) SELECT 'COMPANY_VERIFY' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'COMPANY_VERIFY');
INSERT INTO permissions (name) SELECT 'APPLICATION_READ'  WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'APPLICATION_READ');
INSERT INTO permissions (name) SELECT 'APPLICATION_WRITE' WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE name = 'APPLICATION_WRITE');

-- =====================================================
-- 3. ROLE_PERMISSIONS
-- =====================================================

-- ADMIN gets all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- RECRUITER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'RECRUITER' AND p.name IN ('JOB_READ', 'JOB_WRITE', 'JOB_DELETE', 'COMPANY_READ', 'COMPANY_WRITE', 'APPLICATION_READ')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- CANDIDATE permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CANDIDATE' AND p.name IN ('JOB_READ', 'COMPANY_READ', 'APPLICATION_READ', 'APPLICATION_WRITE')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- =====================================================
-- 4. RESOURCES (default avatar)
-- =====================================================

INSERT INTO resources (content_type, resource_type, public_id, size, name)
SELECT 'image/png', 'IMAGE', 'bot-cv/avatar/default-avatar', 10240, 'default-avatar'
WHERE NOT EXISTS (SELECT 1 FROM resources WHERE public_id = 'bot-cv/avatar/default-avatar');

-- =====================================================
-- 5. ACCOUNTS
-- =====================================================

-- Admin — password: Admin@123
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'admin@botcv.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'admin@botcv.com');

-- Moderator — password: Moderator@123
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'moderator@botcv.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'ADMIN'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'moderator@botcv.com');

-- Recruiter 1 — password: Recruiter@123
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'recruiter1@techcorp.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'RECRUITER'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'recruiter1@techcorp.com');

-- Recruiter 2
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'recruiter2@fptsoft.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'RECRUITER'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'recruiter2@fptsoft.com');

-- Candidate 1 — password: Candidate@123
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'candidate1@gmail.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'CANDIDATE'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'candidate1@gmail.com');

-- Candidate 2
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'candidate2@gmail.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'CANDIDATE'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'candidate2@gmail.com');

-- Candidate 3
INSERT INTO accounts (email, password, role_id, status, provider, verified_at)
SELECT 'candidate3@gmail.com',
       '$2a$10$NfCbsSflss4JNAAzS.T0QOa5AvOoNm333IdIyZLlr3UlEr6179kom',
       r.id, 'ACTIVE', 'LOCAL', NOW()
FROM roles r WHERE r.name = 'CANDIDATE'
  AND NOT EXISTS (SELECT 1 FROM accounts WHERE email = 'candidate3@gmail.com');

-- =====================================================
-- 6. LOCATIONS
-- =====================================================

INSERT INTO locations (street_address, ward, district, province_city, country, lat, lng)
SELECT '1 Vo Van Ngan', 'Linh Chieu', 'Thu Duc', 'Ho Chi Minh', 'Vietnam', 10.8505, 106.7720
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE street_address = '1 Vo Van Ngan' AND province_city = 'Ho Chi Minh');

INSERT INTO locations (street_address, ward, district, province_city, country, lat, lng)
SELECT '17 Duy Tan', 'Dich Vong Hau', 'Cau Giay', 'Ha Noi', 'Vietnam', 21.0318, 105.7838
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE street_address = '17 Duy Tan' AND province_city = 'Ha Noi');

INSERT INTO locations (street_address, ward, district, province_city, country, lat, lng)
SELECT '72 Le Thanh Ton', 'Ben Nghe', 'Quan 1', 'Ho Chi Minh', 'Vietnam', 10.7769, 106.7009
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE street_address = '72 Le Thanh Ton' AND province_city = 'Ho Chi Minh');

INSERT INTO locations (street_address, ward, district, province_city, country, lat, lng)
SELECT '29 Nguyen Hue', 'Ben Nghe', 'Quan 1', 'Ho Chi Minh', 'Vietnam', 10.7741, 106.7030
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE street_address = '29 Nguyen Hue' AND province_city = 'Ho Chi Minh');

INSERT INTO locations (street_address, ward, district, province_city, country, lat, lng)
SELECT '8A Ton That Thuyet', 'My Dinh 2', 'Nam Tu Liem', 'Ha Noi', 'Vietnam', 21.0285, 105.7823
WHERE NOT EXISTS (SELECT 1 FROM locations WHERE street_address = '8A Ton That Thuyet' AND province_city = 'Ha Noi');

-- =====================================================
-- 7. COMPANIES
-- =====================================================

INSERT INTO companies (name, website, size, description, phone, email, industry, is_verified)
SELECT 'TechCorp Vietnam', 'https://techcorp.vn', 'LARGE',
       'Leading technology solutions provider in Southeast Asia', '028-1234-5678',
       'contact@techcorp.vn', 'Information Technology', true
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name = 'TechCorp Vietnam');

INSERT INTO companies (name, website, size, description, phone, email, industry, is_verified)
SELECT 'FPT Software', 'https://fpt-software.com', 'ENTERPRISE',
       'Vietnam''s largest IT services company', '024-7300-7300',
       'contact@fpt-software.com', 'Information Technology', true
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name = 'FPT Software');

INSERT INTO companies (name, website, size, description, phone, email, industry, is_verified)
SELECT 'VNG Corporation', 'https://vng.com.vn', 'LARGE',
       'Leading internet company in Vietnam', '028-3910-9876',
       'hr@vng.com.vn', 'Internet / E-commerce', true
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name = 'VNG Corporation');

-- =====================================================
-- 8. COMPANY_LOCATIONS
-- =====================================================

INSERT INTO company_locations (company_id, location_id, is_headquarter)
SELECT c.id, l.id, true
FROM companies c, locations l
WHERE c.name = 'TechCorp Vietnam' AND l.street_address = '72 Le Thanh Ton'
  AND NOT EXISTS (
    SELECT 1 FROM company_locations cl WHERE cl.company_id = c.id AND cl.location_id = l.id
  );

INSERT INTO company_locations (company_id, location_id, is_headquarter)
SELECT c.id, l.id, true
FROM companies c, locations l
WHERE c.name = 'FPT Software' AND l.street_address = '17 Duy Tan'
  AND NOT EXISTS (
    SELECT 1 FROM company_locations cl WHERE cl.company_id = c.id AND cl.location_id = l.id
  );

INSERT INTO company_locations (company_id, location_id, is_headquarter)
SELECT c.id, l.id, true
FROM companies c, locations l
WHERE c.name = 'VNG Corporation' AND l.street_address = '29 Nguyen Hue'
  AND NOT EXISTS (
    SELECT 1 FROM company_locations cl WHERE cl.company_id = c.id AND cl.location_id = l.id
  );

-- =====================================================
-- 9. MODERATOR PROFILE
-- =====================================================

INSERT INTO moderators (account_id, full_name, phone)
SELECT a.id, 'System Moderator', '0900000001'
FROM accounts a WHERE a.email = 'moderator@botcv.com'
  AND NOT EXISTS (SELECT 1 FROM moderators WHERE account_id = a.id);

-- =====================================================
-- 10. RECRUITER PROFILES
-- =====================================================

INSERT INTO recruiters (account_id, full_name, phone, company_id)
SELECT a.id, 'Nguyen Van Tuan', '0901234567', c.id
FROM accounts a, companies c
WHERE a.email = 'recruiter1@techcorp.com' AND c.name = 'TechCorp Vietnam'
  AND NOT EXISTS (SELECT 1 FROM recruiters WHERE account_id = a.id);

INSERT INTO recruiters (account_id, full_name, phone, company_id)
SELECT a.id, 'Tran Thi Mai', '0912345678', c.id
FROM accounts a, companies c
WHERE a.email = 'recruiter2@fptsoft.com' AND c.name = 'FPT Software'
  AND NOT EXISTS (SELECT 1 FROM recruiters WHERE account_id = a.id);

-- =====================================================
-- 11. CANDIDATE PROFILES
-- =====================================================

INSERT INTO candidates (account_id, full_name, phone, location_id, experience_years, salary_expect, currency, remote_pref, relocation_pref, bio)
SELECT a.id, 'Le Minh Duc', '0923456789', l.id,
       'THREE_YEARS', 25000000, 'VND', true, false,
       'Backend developer with 3 years of experience in Java and Spring Boot'
FROM accounts a, locations l
WHERE a.email = 'candidate1@gmail.com' AND l.street_address = '1 Vo Van Ngan'
  AND NOT EXISTS (SELECT 1 FROM candidates WHERE account_id = a.id);

INSERT INTO candidates (account_id, full_name, phone, location_id, experience_years, salary_expect, currency, remote_pref, relocation_pref, bio)
SELECT a.id, 'Pham Thi Lan', '0934567890', l.id,
       'ONE_YEAR', 15000000, 'VND', false, true,
       'Frontend developer passionate about React and UI/UX design'
FROM accounts a, locations l
WHERE a.email = 'candidate2@gmail.com' AND l.street_address = '72 Le Thanh Ton'
  AND NOT EXISTS (SELECT 1 FROM candidates WHERE account_id = a.id);

INSERT INTO candidates (account_id, full_name, phone, location_id, experience_years, salary_expect, currency, remote_pref, relocation_pref, bio)
SELECT a.id, 'Hoang Anh Khoa', '0945678901', l.id,
       'FIVE_YEARS', 40000000, 'VND', true, true,
       'Full-stack engineer specialized in cloud-native microservices'
FROM accounts a, locations l
WHERE a.email = 'candidate3@gmail.com' AND l.street_address = '8A Ton That Thuyet'
  AND NOT EXISTS (SELECT 1 FROM candidates WHERE account_id = a.id);

-- =====================================================
-- 12. SKILLS
-- =====================================================

INSERT INTO skills (name, aliases) SELECT 'Java',         '["java", "Java SE", "Java EE"]'::jsonb            WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Java');
INSERT INTO skills (name, aliases) SELECT 'Spring Boot',  '["spring-boot", "springboot"]'::jsonb              WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Spring Boot');
INSERT INTO skills (name, aliases) SELECT 'PostgreSQL',   '["postgres", "pgsql"]'::jsonb                      WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'PostgreSQL');
INSERT INTO skills (name, aliases) SELECT 'React',        '["reactjs", "react.js"]'::jsonb                    WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'React');
INSERT INTO skills (name, aliases) SELECT 'TypeScript',   '["ts", "typescript"]'::jsonb                       WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'TypeScript');
INSERT INTO skills (name, aliases) SELECT 'Docker',       '["docker", "containerization"]'::jsonb             WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Docker');
INSERT INTO skills (name, aliases) SELECT 'Kubernetes',   '["k8s", "kube"]'::jsonb                            WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Kubernetes');
INSERT INTO skills (name, aliases) SELECT 'AWS',          '["amazon web services", "aws"]'::jsonb             WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'AWS');
INSERT INTO skills (name, aliases) SELECT 'Python',       '["python3", "py"]'::jsonb                          WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Python');
INSERT INTO skills (name, aliases) SELECT 'JavaScript',   '["js", "ES6"]'::jsonb                              WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'JavaScript');
INSERT INTO skills (name, aliases) SELECT 'Git',          '["git", "version control"]'::jsonb                 WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Git');
INSERT INTO skills (name, aliases) SELECT 'Redis',        '["redis"]'::jsonb                                  WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Redis');
INSERT INTO skills (name, aliases) SELECT 'MongoDB',      '["mongo"]'::jsonb                                  WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'MongoDB');
INSERT INTO skills (name, aliases) SELECT 'Node.js',      '["nodejs", "node"]'::jsonb                         WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Node.js');
INSERT INTO skills (name, aliases) SELECT 'Angular',      '["angularjs", "angular"]'::jsonb                   WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Angular');
INSERT INTO skills (name, aliases) SELECT 'Vue.js',       '["vuejs", "vue"]'::jsonb                           WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Vue.js');
INSERT INTO skills (name, aliases) SELECT 'MySQL',        '["mysql"]'::jsonb                                  WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'MySQL');
INSERT INTO skills (name, aliases) SELECT 'GraphQL',      '["graphql"]'::jsonb                                WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'GraphQL');
INSERT INTO skills (name, aliases) SELECT 'Kafka',        '["apache kafka"]'::jsonb                           WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'Kafka');
INSERT INTO skills (name, aliases) SELECT 'CI/CD',        '["continuous integration", "continuous delivery"]'::jsonb WHERE NOT EXISTS (SELECT 1 FROM skills WHERE name = 'CI/CD');

-- =====================================================
-- 13. CANDIDATE_SKILLS
-- =====================================================

-- Candidate 1 (Le Minh Duc) — backend stack
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND s.name = 'Java'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND s.name = 'Spring Boot'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND s.name = 'PostgreSQL'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND s.name = 'Docker'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND s.name = 'Redis'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);

-- Candidate 2 (Pham Thi Lan) — frontend stack
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id AND s.name = 'React'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id AND s.name = 'TypeScript'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id AND s.name = 'JavaScript'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);

-- Candidate 3 (Hoang Anh Khoa) — full-stack / cloud
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'Java'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'Spring Boot'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'React'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'AWS'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'Kubernetes'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);
INSERT INTO candidate_skills (candidate_id, skill_id)
SELECT c.id, s.id FROM candidates c, skills s, accounts a
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND s.name = 'Docker'
  AND NOT EXISTS (SELECT 1 FROM candidate_skills cs WHERE cs.candidate_id = c.id AND cs.skill_id = s.id);

-- =====================================================
-- 14. JOB CATEGORIES (hierarchical with ltree)
-- =====================================================

-- Top-level categories
INSERT INTO job_categories (name, slug, path, is_leaf) SELECT 'Engineering',   'engineering',   'engineering',   false WHERE NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'engineering');
INSERT INTO job_categories (name, slug, path, is_leaf) SELECT 'Design',        'design',        'design',        false WHERE NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'design');
INSERT INTO job_categories (name, slug, path, is_leaf) SELECT 'Marketing',     'marketing',     'marketing',     false WHERE NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'marketing');
INSERT INTO job_categories (name, slug, path, is_leaf) SELECT 'Sales',         'sales',         'sales',         false WHERE NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'sales');
INSERT INTO job_categories (name, slug, path, is_leaf) SELECT 'Data Science',  'data-science',  'data_science',  false WHERE NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'data-science');

-- Engineering subcategories
INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Backend', 'backend', p.id, 'engineering.backend', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'backend');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Frontend', 'frontend', p.id, 'engineering.frontend', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'frontend');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Full Stack', 'full-stack', p.id, 'engineering.full_stack', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'full-stack');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'DevOps', 'devops', p.id, 'engineering.devops', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'devops');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Mobile', 'mobile', p.id, 'engineering.mobile', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'mobile');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'QA / Testing', 'qa-testing', p.id, 'engineering.qa_testing', true
FROM job_categories p WHERE p.slug = 'engineering'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'qa-testing');

-- Design subcategories
INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'UI/UX Design', 'ui-ux-design', p.id, 'design.ui_ux', true
FROM job_categories p WHERE p.slug = 'design'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'ui-ux-design');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Graphic Design', 'graphic-design', p.id, 'design.graphic', true
FROM job_categories p WHERE p.slug = 'design'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'graphic-design');

-- Data Science subcategories
INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Machine Learning', 'machine-learning', p.id, 'data_science.machine_learning', true
FROM job_categories p WHERE p.slug = 'data-science'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'machine-learning');

INSERT INTO job_categories (name, slug, parent_id, path, is_leaf)
SELECT 'Data Engineering', 'data-engineering', p.id, 'data_science.data_engineering', true
FROM job_categories p WHERE p.slug = 'data-science'
  AND NOT EXISTS (SELECT 1 FROM job_categories WHERE slug = 'data-engineering');

-- =====================================================
-- 15. SCHOOLS
-- =====================================================

INSERT INTO schools (name) SELECT 'Ho Chi Minh City University of Technology (HCMUT)'       WHERE NOT EXISTS (SELECT 1 FROM schools WHERE name = 'Ho Chi Minh City University of Technology (HCMUT)');
INSERT INTO schools (name) SELECT 'Hanoi University of Science and Technology (HUST)'        WHERE NOT EXISTS (SELECT 1 FROM schools WHERE name = 'Hanoi University of Science and Technology (HUST)');
INSERT INTO schools (name) SELECT 'FPT University'                                           WHERE NOT EXISTS (SELECT 1 FROM schools WHERE name = 'FPT University');
INSERT INTO schools (name) SELECT 'University of Information Technology (UIT)'               WHERE NOT EXISTS (SELECT 1 FROM schools WHERE name = 'University of Information Technology (UIT)');
INSERT INTO schools (name) SELECT 'Ho Chi Minh City University of Science (HCMUS)'           WHERE NOT EXISTS (SELECT 1 FROM schools WHERE name = 'Ho Chi Minh City University of Science (HCMUS)');

-- =====================================================
-- 16. CANDIDATE EDUCATIONS
-- =====================================================

INSERT INTO candidate_educations (candidate_id, school_id, degree, field_of_study, start_year, end_year, is_current, description)
SELECT c.id, sch.id, 'Bachelor', 'Computer Science', 2018, 2022, false, 'GPA: 3.5/4.0'
FROM candidates c, accounts a, schools sch
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND sch.name = 'Ho Chi Minh City University of Technology (HCMUT)'
  AND NOT EXISTS (SELECT 1 FROM candidate_educations ce WHERE ce.candidate_id = c.id AND ce.school_id = sch.id);

INSERT INTO candidate_educations (candidate_id, school_id, degree, field_of_study, start_year, end_year, is_current, description)
SELECT c.id, sch.id, 'Bachelor', 'Software Engineering', 2021, 2025, false, 'GPA: 3.2/4.0'
FROM candidates c, accounts a, schools sch
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id AND sch.name = 'FPT University'
  AND NOT EXISTS (SELECT 1 FROM candidate_educations ce WHERE ce.candidate_id = c.id AND ce.school_id = sch.id);

INSERT INTO candidate_educations (candidate_id, school_id, degree, field_of_study, start_year, end_year, is_current, description)
SELECT c.id, sch.id, 'Master', 'Computer Science', 2016, 2021, false, 'Focus on distributed systems'
FROM candidates c, accounts a, schools sch
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND sch.name = 'Hanoi University of Science and Technology (HUST)'
  AND NOT EXISTS (SELECT 1 FROM candidate_educations ce WHERE ce.candidate_id = c.id AND ce.school_id = sch.id);

-- =====================================================
-- 17. CANDIDATE WORK EXPERIENCES
-- =====================================================

INSERT INTO candidate_work_experiences (candidate_id, company_id, job_title, start_date, end_date, is_current, description)
SELECT c.id, co.id, 'Junior Backend Developer', '2022-06-01', '2024-01-31', false,
       'Developed RESTful APIs using Spring Boot and PostgreSQL'
FROM candidates c, accounts a, companies co
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND co.name = 'FPT Software'
  AND NOT EXISTS (
    SELECT 1 FROM candidate_work_experiences cwe WHERE cwe.candidate_id = c.id AND cwe.company_id = co.id AND cwe.job_title = 'Junior Backend Developer'
  );

INSERT INTO candidate_work_experiences (candidate_id, company_id, job_title, start_date, is_current, description)
SELECT c.id, co.id, 'Backend Developer', '2024-02-01', true,
       'Building microservices for job matching platform'
FROM candidates c, accounts a, companies co
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND co.name = 'TechCorp Vietnam'
  AND NOT EXISTS (
    SELECT 1 FROM candidate_work_experiences cwe WHERE cwe.candidate_id = c.id AND cwe.company_id = co.id AND cwe.job_title = 'Backend Developer'
  );

INSERT INTO candidate_work_experiences (candidate_id, company_id, job_title, start_date, is_current, description)
SELECT c.id, co.id, 'Senior Software Engineer', '2021-03-01', true,
       'Leading a team of 5 engineers on cloud infrastructure'
FROM candidates c, accounts a, companies co
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND co.name = 'VNG Corporation'
  AND NOT EXISTS (
    SELECT 1 FROM candidate_work_experiences cwe WHERE cwe.candidate_id = c.id AND cwe.company_id = co.id AND cwe.job_title = 'Senior Software Engineer'
  );

-- =====================================================
-- 18. CANDIDATE POSITIONS (desired job categories)
-- =====================================================

INSERT INTO candidate_positions (candidate_id, category_id)
SELECT c.id, jc.id FROM candidates c, accounts a, job_categories jc
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id AND jc.slug = 'backend'
  AND NOT EXISTS (SELECT 1 FROM candidate_positions cp WHERE cp.candidate_id = c.id AND cp.category_id = jc.id);

INSERT INTO candidate_positions (candidate_id, category_id)
SELECT c.id, jc.id FROM candidates c, accounts a, job_categories jc
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id AND jc.slug = 'frontend'
  AND NOT EXISTS (SELECT 1 FROM candidate_positions cp WHERE cp.candidate_id = c.id AND cp.category_id = jc.id);

INSERT INTO candidate_positions (candidate_id, category_id)
SELECT c.id, jc.id FROM candidates c, accounts a, job_categories jc
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND jc.slug = 'full-stack'
  AND NOT EXISTS (SELECT 1 FROM candidate_positions cp WHERE cp.candidate_id = c.id AND cp.category_id = jc.id);

INSERT INTO candidate_positions (candidate_id, category_id)
SELECT c.id, jc.id FROM candidates c, accounts a, job_categories jc
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND jc.slug = 'devops'
  AND NOT EXISTS (SELECT 1 FROM candidate_positions cp WHERE cp.candidate_id = c.id AND cp.category_id = jc.id);

-- =====================================================
-- 19. JOBS
-- =====================================================

-- Job 1: Backend Developer at TechCorp
INSERT INTO jobs (company_id, recruiter_id, title, category_id, seniority, employment_type,
                  min_experience_years, location_id, work_mode, salary_min, salary_max, currency,
                  max_candidates, date_posted, date_expires, status)
SELECT co.id, r.id, 'Backend Developer (Java/Spring Boot)', jc.id,
       'MID', 'FULL_TIME', 2, l.id, 'HYBRID',
       20000000, 35000000, 'VND', 5,
       NOW() - INTERVAL '5 days', NOW() + INTERVAL '25 days', 'PUBLISHED'
FROM companies co, recruiters r, accounts a, job_categories jc, locations l
WHERE co.name = 'TechCorp Vietnam' AND a.email = 'recruiter1@techcorp.com' AND r.account_id = a.id
  AND jc.slug = 'backend' AND l.street_address = '72 Le Thanh Ton'
  AND NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Backend Developer (Java/Spring Boot)' AND company_id = co.id);

-- Job 2: Frontend Developer at TechCorp
INSERT INTO jobs (company_id, recruiter_id, title, category_id, seniority, employment_type,
                  min_experience_years, location_id, work_mode, salary_min, salary_max, currency,
                  max_candidates, date_posted, date_expires, status)
SELECT co.id, r.id, 'Frontend Developer (React/TypeScript)', jc.id,
       'JUNIOR', 'FULL_TIME', 1, l.id, 'ONSITE',
       15000000, 25000000, 'VND', 3,
       NOW() - INTERVAL '3 days', NOW() + INTERVAL '27 days', 'PUBLISHED'
FROM companies co, recruiters r, accounts a, job_categories jc, locations l
WHERE co.name = 'TechCorp Vietnam' AND a.email = 'recruiter1@techcorp.com' AND r.account_id = a.id
  AND jc.slug = 'frontend' AND l.street_address = '72 Le Thanh Ton'
  AND NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Frontend Developer (React/TypeScript)' AND company_id = co.id);

-- Job 3: DevOps Engineer at FPT Software
INSERT INTO jobs (company_id, recruiter_id, title, category_id, seniority, employment_type,
                  min_experience_years, location_id, work_mode, salary_min, salary_max, currency,
                  max_candidates, date_posted, date_expires, status)
SELECT co.id, r.id, 'DevOps Engineer (AWS/K8s)', jc.id,
       'SENIOR', 'FULL_TIME', 4, l.id, 'REMOTE',
       35000000, 55000000, 'VND', 2,
       NOW() - INTERVAL '7 days', NOW() + INTERVAL '23 days', 'PUBLISHED'
FROM companies co, recruiters r, accounts a, job_categories jc, locations l
WHERE co.name = 'FPT Software' AND a.email = 'recruiter2@fptsoft.com' AND r.account_id = a.id
  AND jc.slug = 'devops' AND l.street_address = '17 Duy Tan'
  AND NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'DevOps Engineer (AWS/K8s)' AND company_id = co.id);

-- Job 4: Full Stack Intern at FPT Software
INSERT INTO jobs (company_id, recruiter_id, title, category_id, seniority, employment_type,
                  min_experience_years, location_id, work_mode, salary_min, salary_max, currency,
                  max_candidates, date_posted, date_expires, status)
SELECT co.id, r.id, 'Full Stack Intern', jc.id,
       'INTERN', 'INTERNSHIP', 0, l.id, 'ONSITE',
       5000000, 8000000, 'VND', 10,
       NOW() - INTERVAL '1 day', NOW() + INTERVAL '29 days', 'PUBLISHED'
FROM companies co, recruiters r, accounts a, job_categories jc, locations l
WHERE co.name = 'FPT Software' AND a.email = 'recruiter2@fptsoft.com' AND r.account_id = a.id
  AND jc.slug = 'full-stack' AND l.street_address = '17 Duy Tan'
  AND NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Full Stack Intern' AND company_id = co.id);

-- Job 5: Draft job (not published)
INSERT INTO jobs (company_id, recruiter_id, title, category_id, seniority, employment_type,
                  min_experience_years, location_id, work_mode, salary_min, salary_max, currency, status)
SELECT co.id, r.id, 'Data Engineer', jc.id,
       'MID', 'FULL_TIME', 3, l.id, 'HYBRID',
       30000000, 45000000, 'VND', 'DRAFT'
FROM companies co, recruiters r, accounts a, job_categories jc, locations l
WHERE co.name = 'TechCorp Vietnam' AND a.email = 'recruiter1@techcorp.com' AND r.account_id = a.id
  AND jc.slug = 'data-engineering' AND l.street_address = '72 Le Thanh Ton'
  AND NOT EXISTS (SELECT 1 FROM jobs WHERE title = 'Data Engineer' AND company_id = co.id);

-- =====================================================
-- 20. JOB DESCRIPTIONS
-- =====================================================

INSERT INTO job_descriptions (job_id, summary, responsibilities, requirements, nice_to_have, benefits, hiring_process)
SELECT j.id,
  'We are looking for a Backend Developer to join our growing engineering team.',
  E'- Design and implement RESTful APIs\n- Write clean, maintainable code\n- Participate in code reviews\n- Collaborate with frontend and DevOps teams',
  E'- 2+ years of experience with Java and Spring Boot\n- Proficiency in SQL and relational databases\n- Understanding of RESTful API design\n- Familiarity with Git',
  E'- Experience with Docker and Kubernetes\n- Knowledge of message queues (Kafka, RabbitMQ)\n- Microservices architecture experience',
  E'- Competitive salary\n- 13th month salary\n- Premium healthcare\n- Flexible working hours\n- 15 days annual leave',
  E'1. CV screening\n2. Technical assessment\n3. Technical interview\n4. Culture fit interview\n5. Offer'
FROM jobs j, companies co
WHERE j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND NOT EXISTS (SELECT 1 FROM job_descriptions jd WHERE jd.job_id = j.id);

INSERT INTO job_descriptions (job_id, summary, responsibilities, requirements, nice_to_have, benefits)
SELECT j.id,
  'Join our team as a Frontend Developer building modern web applications.',
  E'- Build responsive user interfaces with React\n- Implement design mockups pixel-perfectly\n- Optimize application performance\n- Write unit and integration tests',
  E'- 1+ year of experience with React\n- Strong TypeScript/JavaScript skills\n- CSS/SCSS expertise\n- Understanding of state management',
  E'- Experience with Next.js\n- Familiarity with design systems\n- Knowledge of accessibility standards',
  E'- Competitive salary\n- Learning budget\n- Free lunch\n- Young and dynamic environment'
FROM jobs j, companies co
WHERE j.title = 'Frontend Developer (React/TypeScript)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND NOT EXISTS (SELECT 1 FROM job_descriptions jd WHERE jd.job_id = j.id);

INSERT INTO job_descriptions (job_id, summary, responsibilities, requirements, benefits)
SELECT j.id,
  'Senior DevOps Engineer to build and maintain our cloud infrastructure.',
  E'- Design and maintain CI/CD pipelines\n- Manage AWS cloud infrastructure\n- Implement monitoring and alerting\n- Ensure system reliability and scalability',
  E'- 4+ years of DevOps experience\n- Expert-level AWS knowledge\n- Kubernetes and Docker expertise\n- Infrastructure as Code (Terraform/CloudFormation)\n- Scripting (Bash, Python)',
  E'- Top-tier salary\n- Fully remote\n- Stock options\n- 20 days annual leave\n- Conference budget'
FROM jobs j, companies co
WHERE j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND NOT EXISTS (SELECT 1 FROM job_descriptions jd WHERE jd.job_id = j.id);

-- =====================================================
-- 21. JOB SKILL REQUIREMENTS
-- =====================================================

-- Backend Developer job requires: Java, Spring Boot, PostgreSQL, Git
INSERT INTO job_skill_requirements (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, companies co, skills s
WHERE j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND s.name IN ('Java', 'Spring Boot', 'PostgreSQL', 'Git')
  AND NOT EXISTS (SELECT 1 FROM job_skill_requirements jsr WHERE jsr.job_id = j.id AND jsr.skill_id = s.id);

-- Frontend Developer job requires: React, TypeScript, JavaScript
INSERT INTO job_skill_requirements (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, companies co, skills s
WHERE j.title = 'Frontend Developer (React/TypeScript)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND s.name IN ('React', 'TypeScript', 'JavaScript')
  AND NOT EXISTS (SELECT 1 FROM job_skill_requirements jsr WHERE jsr.job_id = j.id AND jsr.skill_id = s.id);

-- DevOps job requires: Docker, Kubernetes, AWS, CI/CD, Python
INSERT INTO job_skill_requirements (job_id, skill_id)
SELECT j.id, s.id FROM jobs j, companies co, skills s
WHERE j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND s.name IN ('Docker', 'Kubernetes', 'AWS', 'CI/CD', 'Python')
  AND NOT EXISTS (SELECT 1 FROM job_skill_requirements jsr WHERE jsr.job_id = j.id AND jsr.skill_id = s.id);

-- =====================================================
-- 22. JOB APPLICATIONS
-- =====================================================

-- Candidate 1 applies to Backend Developer
INSERT INTO job_applications (candidate_id, job_id, status, cv_resource_id)
SELECT c.id, j.id, 'REVIEWED', res.id
FROM candidates c, accounts a, jobs j, companies co, resources res
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id
  AND j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND res.public_id = 'bot-cv/avatar/default-avatar'
  AND NOT EXISTS (SELECT 1 FROM job_applications ja WHERE ja.candidate_id = c.id AND ja.job_id = j.id);

-- Candidate 3 applies to DevOps Engineer
INSERT INTO job_applications (candidate_id, job_id, status, cv_resource_id)
SELECT c.id, j.id, 'INTERVIEW', res.id
FROM candidates c, accounts a, jobs j, companies co, resources res
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id
  AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND res.public_id = 'bot-cv/avatar/default-avatar'
  AND NOT EXISTS (SELECT 1 FROM job_applications ja WHERE ja.candidate_id = c.id AND ja.job_id = j.id);

-- Candidate 2 applies to Frontend Developer
INSERT INTO job_applications (candidate_id, job_id, status, cv_resource_id)
SELECT c.id, j.id, 'SUBMITTED', res.id
FROM candidates c, accounts a, jobs j, companies co, resources res
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id
  AND j.title = 'Frontend Developer (React/TypeScript)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND res.public_id = 'bot-cv/avatar/default-avatar'
  AND NOT EXISTS (SELECT 1 FROM job_applications ja WHERE ja.candidate_id = c.id AND ja.job_id = j.id);

-- =====================================================
-- 23. INTERVIEWS
-- =====================================================

INSERT INTO interviews (application_id, scheduled_at, meeting_url, status, notes)
SELECT ja.id, NOW() + INTERVAL '3 days', 'https://meet.google.com/abc-defg-hij', 'SCHEDULED',
       'Technical interview with engineering team lead'
FROM job_applications ja, candidates c, accounts a, jobs j, companies co
WHERE a.email = 'candidate3@gmail.com' AND c.account_id = a.id AND ja.candidate_id = c.id
  AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id AND ja.job_id = j.id
  AND NOT EXISTS (SELECT 1 FROM interviews i WHERE i.application_id = ja.id);

-- =====================================================
-- 24. SAVED JOBS
-- =====================================================

INSERT INTO saved_jobs (candidate_id, job_id)
SELECT c.id, j.id
FROM candidates c, accounts a, jobs j, companies co
WHERE a.email = 'candidate1@gmail.com' AND c.account_id = a.id
  AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND NOT EXISTS (SELECT 1 FROM saved_jobs sj WHERE sj.candidate_id = c.id AND sj.job_id = j.id);

INSERT INTO saved_jobs (candidate_id, job_id)
SELECT c.id, j.id
FROM candidates c, accounts a, jobs j, companies co
WHERE a.email = 'candidate2@gmail.com' AND c.account_id = a.id
  AND j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND NOT EXISTS (SELECT 1 FROM saved_jobs sj WHERE sj.candidate_id = c.id AND sj.job_id = j.id);

-- =====================================================
-- 25. USER INTERACTIONS (analytics events)
-- =====================================================

INSERT INTO user_interactions (account_id, event_type, job_id, metadata)
SELECT a.id, 'CLICK_FROM_SEARCH', j.id, '{"source": "search", "query": "java spring boot"}'::jsonb
FROM accounts a, jobs j, companies co
WHERE a.email = 'candidate1@gmail.com' AND j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND NOT EXISTS (
    SELECT 1 FROM user_interactions ui WHERE ui.account_id = a.id AND ui.job_id = j.id AND ui.event_type = 'CLICK_FROM_SEARCH'
  );

INSERT INTO user_interactions (account_id, event_type, job_id, metadata)
SELECT a.id, 'APPLY', j.id, '{"source": "job_detail"}'::jsonb
FROM accounts a, jobs j, companies co
WHERE a.email = 'candidate1@gmail.com' AND j.title = 'Backend Developer (Java/Spring Boot)' AND co.name = 'TechCorp Vietnam' AND j.company_id = co.id
  AND NOT EXISTS (
    SELECT 1 FROM user_interactions ui WHERE ui.account_id = a.id AND ui.job_id = j.id AND ui.event_type = 'APPLY'
  );

INSERT INTO user_interactions (account_id, event_type, job_id, metadata)
SELECT a.id, 'SAVE', j.id, '{"source": "search_results"}'::jsonb
FROM accounts a, jobs j, companies co
WHERE a.email = 'candidate1@gmail.com' AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND NOT EXISTS (
    SELECT 1 FROM user_interactions ui WHERE ui.account_id = a.id AND ui.job_id = j.id AND ui.event_type = 'SAVE'
  );

INSERT INTO user_interactions (account_id, event_type, job_id, metadata)
SELECT a.id, 'CLICK_FROM_RECOMMENDED', j.id, '{"source": "recommendation_engine"}'::jsonb
FROM accounts a, jobs j, companies co
WHERE a.email = 'candidate3@gmail.com' AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND NOT EXISTS (
    SELECT 1 FROM user_interactions ui WHERE ui.account_id = a.id AND ui.job_id = j.id AND ui.event_type = 'CLICK_FROM_RECOMMENDED'
  );

INSERT INTO user_interactions (account_id, event_type, job_id, metadata)
SELECT a.id, 'APPLY', j.id, '{"source": "recommendation"}'::jsonb
FROM accounts a, jobs j, companies co
WHERE a.email = 'candidate3@gmail.com' AND j.title = 'DevOps Engineer (AWS/K8s)' AND co.name = 'FPT Software' AND j.company_id = co.id
  AND NOT EXISTS (
    SELECT 1 FROM user_interactions ui WHERE ui.account_id = a.id AND ui.job_id = j.id AND ui.event_type = 'APPLY'
  );

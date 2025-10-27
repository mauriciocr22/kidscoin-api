-- Script para corrigir constraint da coluna email
-- Este script permite que crianças sejam criadas com username (sem email)
-- Execute manualmente no PostgreSQL:
-- psql -U postgres -d educacao_financeira -f fix_email_nullable.sql

-- Alterar coluna email para aceitar NULL
ALTER TABLE users ALTER COLUMN email DROP NOT NULL;

-- Alterar coluna username para aceitar NULL
ALTER TABLE users ALTER COLUMN username DROP NOT NULL;

-- Verificar as mudanças
SELECT column_name, is_nullable, data_type
FROM information_schema.columns
WHERE table_name = 'users'
AND column_name IN ('email', 'username');

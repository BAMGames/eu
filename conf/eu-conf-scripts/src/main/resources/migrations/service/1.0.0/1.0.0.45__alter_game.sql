ALTER TABLE GAME
ADD COLUMN SEED BIGINT;

UPDATE GAME SET SEED = 12 WHERE SEED IS NULL;

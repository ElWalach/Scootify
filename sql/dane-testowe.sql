PRAGMA foreign_keys = ON;

INSERT OR IGNORE INTO modele (producent, nazwa, max_predkosc, zasieg_km) VALUES ('Xiaomi', 'Mi Pro 2', 25, 45);
INSERT OR IGNORE INTO modele (producent, nazwa, max_predkosc, zasieg_km) VALUES ('Segway', 'Ninebot Max G30', 25, 65);
INSERT OR IGNORE INTO modele (producent, nazwa, max_predkosc, zasieg_km) VALUES ('Okai', 'ES400', 25, 60);

INSERT OR IGNORE INTO strefy (nazwa) VALUES ('Rynek Główny');
INSERT OR IGNORE INTO strefy (nazwa) VALUES ('Kazimierz');
INSERT OR IGNORE INTO strefy (nazwa) VALUES ('Czyżyny – kampus PK');

INSERT OR IGNORE INTO hulajnogi (kod_qr, model_id, strefa_id, bateria, status, szerokosc, dlugosc)
SELECT 'SC-0001', m.id, s.id, 87, 'dostepna', 50.0614, 19.9372
FROM modele m, strefy s WHERE m.nazwa = 'Mi Pro 2' AND s.nazwa = 'Rynek Główny';

INSERT OR IGNORE INTO hulajnogi (kod_qr, model_id, strefa_id, bateria, status, szerokosc, dlugosc)
SELECT 'SC-0002', m.id, s.id, 12, 'do_naladowania', 50.0515, 19.9450
FROM modele m, strefy s WHERE m.nazwa = 'Ninebot Max G30' AND s.nazwa = 'Kazimierz';

INSERT OR IGNORE INTO hulajnogi (kod_qr, model_id, strefa_id, bateria, status, szerokosc, dlugosc)
SELECT 'SC-0003', m.id, s.id, 64, 'w_serwisie', 50.0717, 20.0071
FROM modele m, strefy s WHERE m.nazwa = 'ES400' AND s.nazwa = 'Czyżyny – kampus PK';

INSERT OR IGNORE INTO hulajnogi (kod_qr, model_id, strefa_id, bateria, status, szerokosc, dlugosc)
SELECT 'SC-0004', m.id, s.id, 95, 'w_uzyciu', 50.0590, 19.9400
FROM modele m, strefy s WHERE m.nazwa = 'Ninebot Max G30' AND s.nazwa = 'Rynek Główny';

INSERT INTO przejazdy (uzytkownik_id, hulajnoga_id, rozpoczecie, zakonczenie, koszt)
SELECT u.id, h.id, '2026-11-20 08:15', '2026-11-20 08:32', 9.30
FROM uzytkownicy u, hulajnogi h
WHERE u.login = 'JanKowalski' AND h.kod_qr = 'SC-0001'
  AND NOT EXISTS (SELECT 1 FROM przejazdy WHERE hulajnoga_id = h.id AND rozpoczecie = '2026-11-20 08:15');

INSERT INTO przejazdy (uzytkownik_id, hulajnoga_id, rozpoczecie, zakonczenie, koszt)
SELECT u.id, h.id, '2026-11-21 17:40', '2026-11-21 17:58', 10.10
FROM uzytkownicy u, hulajnogi h
WHERE u.login = 'AnnaNowak' AND h.kod_qr = 'SC-0001'
  AND NOT EXISTS (SELECT 1 FROM przejazdy WHERE hulajnoga_id = h.id AND rozpoczecie = '2026-11-21 17:40');
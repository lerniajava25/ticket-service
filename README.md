# Helpdesk – Ärendehanteringssystem (Ticket Service)

Ett webbaserat ärendehanteringssystem (Helpdesk) byggt med **Java**, **Spring Boot**, **Spring Security**, **Spring Data JPA (Hibernate)**, **Thymeleaf** och **MySQL**.

Applikationen hanterar supportärenden med rollbaserad åtkomstkontroll för tre olika användarroller (`USER`, `AGENT` och `ADMIN`), samt lagrar all data persistent i en MySQL-databas.

---

## 🛠️ Teknisk Stack

* **Backend:** Java, Spring Boot (Web, Security, Data JPA)
* **Frontend:** Thymeleaf, HTML5, CSS3
* **Databas:** MySQL 8+ (via Docker / Podman)
* **Byggverktyg:** Maven

---

## 👥 Roller och Behörigheter

Systemet är uppdelat i tre säkerhetsroller med tydligt separerade rättigheter i både backend och gränssnitt:

### 1. `USER` (Vanlig användare)
* Logga in och ut ur systemet.
* Skapa nya supportärenden.
* Se en lista över sina egna inskickade ärenden.
* Visa detaljer och lägga till kommentarer på sina egna ärenden.

### 2. `AGENT` (Supportpersonal)
* Se samtliga ärenden i systemet.
* Visa detaljer för alla ärenden.
* Tilldela sig själv ett obesvarat ärende (*"Ta på mig detta ärende"*), vilket automatiskt ändrar status till `IN_PROGRESS`.
* Uppdatera ärendestatus (`NEW`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`).
* Lägga till kommentarer och svara användare på alla ärenden.

### 3. `ADMIN` (Systemadministratör – VG-funktionalitet)
* Har tillgång till samtliga `AGENT`-funktioner för ärendehantering.
* Åtkomst till adminpanelen (`/admin/users`) via knappen **Administrera Användare**.
* Skapa nya användarkonton och tilldela roll (`USER`, `AGENT` eller `ADMIN`).
* Ändra roller på befintliga användare.
* Aktivera och inaktivera användarkonton (inaktiverade konton blockeras från inloggning).

---

## 🚀 Installation och Start (För Testning & Rättning)

### 1. Konfigurera miljövariabler (Lösenord för testkonton)
Av säkerhetsskäl innehåller detta repository inga hårdkodade lösenord i källkoden. Innan du startar applikationen:

1. Kopiera mallfilen `.env.example` i projektets rotmapp och döp kopian till `.env`.
2. Öppna `.env` och fyll i valfria testlösenord för de tre förskapade användarna:
   ```env
   USER_PASSWORD=valfrittLösenord1
   AGENT_PASSWORD=valfrittLösenord2
   ADMIN_PASSWORD=valfrittLösenord3
   ```

---

### 2. Starta databasen och applikationen

Projektet är konfigurerat för att ansluta direkt till en MySQL-databas på `localhost:3306` (`helpdesk_db`). Beroende på om du kör **Docker** eller **Podman**, följ stegen nedan:

#### Alternativ A: Starta med Docker (Rekommenderas för rättning)
Projektet innehåller en färdig `compose.yml` / `docker-compose.yml` som startar databasen med exakt de uppgifter som `application.properties` förväntar sig.

1. Öppna en terminal i projektets rotmapp.
2. Starta MySQL-databasen i bakgrunden:
   ```bash
   docker compose up -d
   ```
3. Vänta cirka 10–15 sekunder så att databasen hinner initieras.
4. Starta Spring Boot-applikationen via IntelliJ (`TicketServiceApplication.java`) eller via terminalen:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Alternativ B: Starta med Podman (Utvecklingsmiljö på Windows)
Under utvecklingen användes Podman istället för Docker Desktop. Därför är den automatiska Docker Compose-hanteringen i Spring Boot inaktiverad (`spring.docker.compose.enabled=false`) för att förhindra krockar på Windows, och databascontainern startas manuellt med samma port och lösenord som i Compose-filen:

1. Starta Podman-motorn och MySQL-containern:
   ```bash
   podman machine start
   podman run --name ticket-mysql-db -p 3306:3306 -e MYSQL_DATABASE=helpdesk_db -e MYSQL_ROOT_PASSWORD=verysecret -d mysql:latest
   ```
   *(Om containern redan är skapad sedan tidigare, kör enbart `podman start ticket-mysql-db`).*
2. Starta `TicketServiceApplication.java` i IntelliJ.

---

## 🌐 Testa Applikationen i Webbläsaren

När applikationen har startat (`Tomcat started on port 8080`), öppna din webbläsare och gå till:

 **`http://localhost:8080/login`**

### Förskapade Testkonton
Vid första uppstart läser `DataInitializer` in dina valda lösenord från `.env` och skapar automatiskt tre testkonton i databasen med krypterade lösenord (BCrypt):

* **Användarnamn:** `user` (Roll: `USER`)
* **Användarnamn:** `agent` (Roll: `AGENT`)
* **Användarnamn:** `admin` (Roll: `ADMIN`)

### Förslag på Testflöde
1. **Logga in som `user`:** Skapa ett nytt ärende och kontrollera att det syns i din lista. Logga ut.
2. **Logga in som `agent`:** Kontrollera att knappen *"Skapa nytt ärende"* är dold. Öppna användarens ärende, klicka på *"Ta på mig detta ärende"*, skriv en kommentar och byt status. Logga ut.
3. **Logga in som `admin`:** Klicka på *"Administrera Användare"* uppe till höger. Skapa ett nytt konto, ändra en roll och testa att inaktivera ett konto för att verifiera att inloggning nekas för det kontot.

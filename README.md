# 📚 SiwBooks - Sistema di Gestione Libreria

Un'applicazione web completa per la gestione di una libreria digitale, sviluppata con Spring Boot e PostgreSQL.

## ✨ Funzionalità Principali

### 👤 **Gestione Utenti**
- **Registrazione e Login**: Sistema di autenticazione sicuro
- **Ruoli**: USER (utenti normali) e ADMIN (amministratori)
- **Profili**: Gestione informazioni personali

### 📖 **Gestione Libri**
- **Catalogo**: Visualizzazione completa dei libri
- **Ricerca Avanzata**: Per titolo, autore, anno di pubblicazione
- **Upload Immagini**: Copertine dei libri
- **Dettagli**: Informazioni complete su ogni libro

### ✍️ **Gestione Autori**
- **Anagrafica Completa**: Nome, cognome, nazionalità, date
- **Foto Profilo**: Upload immagini degli autori
- **Bibliografia**: Lista dei libri per ogni autore

### ⭐ **Sistema Recensioni**
- **Valutazioni**: Rating da 1 a 5 stelle
- **Recensioni Testuali**: Commenti dettagliati
- **Vincoli**: Una recensione per utente per libro
- **Statistiche**: Rating medio e conteggio recensioni

### 🔧 **Pannello Amministrazione**
- **CRUD Completo**: Gestione libri, autori e utenti
- **Upload File**: Gestione immagini
- **Statistiche**: Panoramica del sistema

## 🏗️ Architettura

### Modello Dati
- **User ↔ Review**: One-to-Many (un utente può scrivere più recensioni)
- **Book ↔ Review**: One-to-Many (un libro può avere più recensioni)
- **Book ↔ Author**: Many-to-Many (un libro può avere più autori, un autore più libri)
- **User → Review**: One-to-Many (un utente può scrivere più recensioni)
- **Book + User → Review**: Unique constraint (max 1 recensione per utente per libro)

## 🛠️ Tecnologie Utilizzate

- **Backend**: Spring Boot 3.5.0
- **Database**: PostgreSQL (produzione)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **Template Engine**: Thymeleaf
- **Frontend**: Bootstrap 5.3.0 + Bootstrap Icons
- **Build Tool**: Maven

## 🚀 Come Avviare l'Applicazione

### Prerequisiti
- Java 21+
- Maven 3.6+
- PostgreSQL 12+ installato e in esecuzione

### Setup Database PostgreSQL

1. **Installa PostgreSQL** (se non già installato):
   ```bash
   # macOS con Homebrew
   brew install postgresql
   brew services start postgresql
   
   # Ubuntu/Debian
   sudo apt update
   sudo apt install postgresql postgresql-contrib
   sudo systemctl start postgresql
   
   # Windows
   # Scarica e installa da https://www.postgresql.org/download/windows/
   ```

2. **Crea il database**:
   ```bash
   # Accedi a PostgreSQL
   psql -U postgres
   
   # Crea il database
   CREATE DATABASE siwbooks2;
   
   # Esci da psql
   \q
   ```

3. **Configura le credenziali** (se diverse da quelle di default):
   - Username: `postgres`
   - Password: `postgres`
   - Database: `siwbooks2`
   - Porta: `5432`

### Avvio Applicazione

```bash
# Clona il repository
git clone <repository-url>
cd siwbooks

# Compila ed esegui
mvn spring-boot:run
```

L'applicazione sarà disponibile su: `http://localhost:8080`

## 👥 Utente Amministratore di Default

L'applicazione crea automaticamente un utente amministratore al primo avvio:

| Username | Password | Ruolo | Email |
|----------|----------|-------|-------|
| admin | password | ADMIN | admin@siwbooks.com |

**Nota**: Il database PostgreSQL è persistente. I dati inseriti rimangono salvati tra i riavvii dell'applicazione.

## 🎨 Interfaccia Utente

L'interfaccia è stata progettata seguendo principi di UX moderni:
- **Responsive Design**: Compatibile con desktop, tablet e mobile
- **Bootstrap 5**: Framework CSS per un design professionale
- **Icone**: Bootstrap Icons per una migliore usabilità
- **Navigazione Intuitiva**: Menu di navigazione chiaro e breadcrumb
- **Feedback Utente**: Messaggi di successo/errore per le azioni
- **Accessibilità**: Etichette appropriate e struttura semantica

## 📁 Struttura Dettagliata del Progetto

### 🗂️ Directory Principale
```
siwbooks/
├── .gitattributes              # Configurazione attributi Git
├── .gitignore                  # File e directory da ignorare in Git
├── .DS_Store                   # File di sistema macOS (da ignorare)
├── cookies.txt                 # File di configurazione cookies
├── HELP.md                     # Documentazione di aiuto Spring Boot
├── MIGRATION_SUMMARY.md        # Riepilogo migrazione del progetto
├── README.md                   # Documentazione principale del progetto
├── pom.xml                     # Configurazione Maven e dipendenze
├── mvnw                        # Maven Wrapper per Unix/Linux/macOS
├── mvnw.cmd                    # Maven Wrapper per Windows
├── .mvn/                       # Directory Maven Wrapper
│   └── wrapper/
│       └── maven-wrapper.properties  # Proprietà Maven Wrapper
├── .vscode/                    # Configurazioni Visual Studio Code
├── target/                     # Directory build Maven (generata)
├── data/                       # Database H2 (backup/sviluppo)
│   ├── siwbooks2.mv.db        # File database H2
│   └── siwbooks2.trace.db     # File trace H2
└── uploads/                    # File caricati dall'applicazione
    ├── books/                  # Copertine dei libri
    └── authors/                # Foto degli autori
```

### ☕ Codice Sorgente Java
```
src/main/java/siwbooks/siwbooks/
├── SiwbooksApplication.java    # Classe principale Spring Boot
├── config/                     # Configurazioni dell'applicazione
│   ├── AdminInitializer.java   # Inizializzazione utente admin
│   ├── FileUploadConfig.java   # Configurazione upload file
│   └── SecurityConfig.java     # Configurazione Spring Security
├── controller/                 # Controller MVC
│   ├── AdminController.java    # Controller pannello amministrazione
│   ├── AuthController.java     # Controller autenticazione
│   ├── AuthorController.java   # Controller gestione autori
│   ├── BookController.java     # Controller gestione libri
│   ├── CustomErrorController.java  # Controller errori personalizzati
│   ├── HomeController.java     # Controller homepage
│   └── ReviewController.java   # Controller gestione recensioni
├── model/                      # Entità JPA/Hibernate
│   ├── Author.java            # Entità Autore
│   ├── Book.java              # Entità Libro
│   ├── Review.java            # Entità Recensione
│   └── User.java              # Entità Utente
├── repository/                 # Repository JPA
│   ├── AuthorRepository.java   # Repository per autori
│   ├── BookRepository.java     # Repository per libri
│   ├── ReviewRepository.java   # Repository per recensioni
│   └── UserRepository.java     # Repository per utenti
└── service/                    # Logica di business
    ├── AuthorService.java      # Servizio gestione autori
    ├── BookService.java        # Servizio gestione libri
    ├── CustomUserDetailsService.java  # Servizio autenticazione
    ├── FileUploadService.java  # Servizio upload file
    ├── ReviewService.java      # Servizio gestione recensioni
    └── UserService.java        # Servizio gestione utenti
```

### 🎨 Template e Risorse
```
src/main/resources/
├── application.properties      # Configurazione applicazione
├── data.sql                   # Dati di inizializzazione database
├── META-INF/                  # Metadati applicazione
├── static/                    # File statici (CSS, JS, immagini)
└── templates/                 # Template Thymeleaf
    ├── layout.html            # Layout base dell'applicazione
    ├── index.html             # Homepage
    ├── error.html             # Pagina errori generica
    ├── search-results.html    # Risultati ricerca
    ├── admin/                 # Template pannello amministrazione
    │   ├── dashboard.html     # Dashboard amministratore
    │   ├── books/
    │   │   ├── add.html       # Aggiunta nuovo libro
    │   │   └── edit.html      # Modifica libro esistente
    │   ├── authors/
    │   │   ├── add.html       # Aggiunta nuovo autore
    │   │   └── edit.html      # Modifica autore esistente
    │   └── users/
    │       └── add-admin.html # Aggiunta nuovo amministratore
    ├── auth/                  # Template autenticazione
    │   ├── login.html         # Pagina login
    │   └── register.html      # Pagina registrazione
    ├── authors/               # Template gestione autori
    │   ├── list.html          # Lista autori
    │   └── detail.html        # Dettaglio autore
    ├── books/                 # Template gestione libri
    │   ├── list.html          # Catalogo libri
    │   └── detail.html        # Dettaglio libro
    └── reviews/               # Template gestione recensioni
        ├── add.html           # Aggiunta recensione
        └── my-reviews.html    # Le mie recensioni
```

### 🧪 Test
```
src/test/java/siwbooks/siwbooks/
├── SiwbooksApplicationTests.java   # Test di base Spring Boot
└── FileUploadServiceTest.java     # Test servizio upload file
```

### 📋 Descrizione Dettagliata dei File Principali

#### **File di Configurazione**
- **`pom.xml`**: Configurazione Maven con dipendenze Spring Boot, PostgreSQL, Thymeleaf, Spring Security
- **`application.properties`**: Configurazioni database, JPA, logging, upload file
- **`data.sql`**: Script SQL per inizializzazione dati di esempio

#### **Entità del Modello**
- **`User.java`**: Gestione utenti con ruoli (USER/ADMIN), autenticazione
- **`Book.java`**: Entità libro con titolo, anno, ISBN, immagine copertina
- **`Author.java`**: Entità autore con anagrafica completa e foto
- **`Review.java`**: Entità recensione con rating e commento

#### **Controller MVC**
- **`AdminController.java`**: CRUD completo per libri, autori, utenti (solo ADMIN)
- **`BookController.java`**: Visualizzazione catalogo e dettagli libri
- **`AuthorController.java`**: Visualizzazione lista e dettagli autori
- **`ReviewController.java`**: Gestione recensioni utenti
- **`AuthController.java`**: Login, logout, registrazione
- **`HomeController.java`**: Homepage con ricerca e statistiche

#### **Servizi Business Logic**
- **`BookService.java`**: Logica gestione libri, ricerca, statistiche
- **`AuthorService.java`**: Logica gestione autori
- **`UserService.java`**: Gestione utenti e ruoli
- **`ReviewService.java`**: Logica recensioni e validazioni
- **`FileUploadService.java`**: Upload e gestione immagini
- **`CustomUserDetailsService.java`**: Integrazione Spring Security

#### **Repository JPA**
- **`BookRepository.java`**: Query personalizzate per ricerca libri
- **`AuthorRepository.java`**: Query per ricerca autori
- **`UserRepository.java`**: Query per gestione utenti
- **`ReviewRepository.java`**: Query per recensioni e statistiche

#### **Configurazioni**
- **`SecurityConfig.java`**: Configurazione sicurezza, autenticazione, autorizzazioni
- **`FileUploadConfig.java`**: Configurazione upload file e dimensioni massime
- **`AdminInitializer.java`**: Creazione automatica utente amministratore

#### **Template Thymeleaf**
- **`layout.html`**: Template base con navbar, footer, Bootstrap
- **`index.html`**: Homepage con ricerca e libri in evidenza
- **`admin/dashboard.html`**: Pannello controllo amministratore
- **Template CRUD**: Form per aggiunta/modifica entità
- **Template visualizzazione**: Liste e dettagli entità

## 🔧 Configurazione Database

### PostgreSQL (Produzione)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/siwbooks2
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.hikari.auto-commit=false
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Vantaggi PostgreSQL
- **Performance**: Ottimizzato per carichi di lavoro intensivi
- **Scalabilità**: Supporta grandi volumi di dati
- **Affidabilità**: ACID compliance e backup robusti
- **Funzionalità Avanzate**: JSON, full-text search, indici avanzati

## 🔒 Sicurezza

- **Autenticazione**: Spring Security con BCrypt
- **Autorizzazione**: Controllo accessi basato su ruoli
- **CSRF Protection**: Protezione contro attacchi CSRF
- **Password Encoding**: Hash sicuro delle password
- **Session Management**: Gestione sicura delle sessioni

## 📊 Monitoraggio e Logging

- **SQL Logging**: Query SQL visibili in sviluppo
- **Security Logging**: Log dettagliati per debug sicurezza
- **Application Logging**: Log personalizzati per troubleshooting

## 🚀 Deploy in Produzione

### Configurazioni Consigliate

1. **Database**:
   - Usa un server PostgreSQL dedicato
   - Configura backup automatici
   - Ottimizza le performance con indici appropriati

2. **Sicurezza**:
   - Cambia le password di default
   - Usa HTTPS in produzione
   - Configura firewall appropriato

3. **Performance**:
   - Abilita cache Thymeleaf (`spring.thymeleaf.cache=true`)
   - Configura connection pooling
   - Ottimizza JVM settings
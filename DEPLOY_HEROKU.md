# Deploy di SiwBooks su Heroku

## Prerequisiti

1. **Account Heroku**: Registrati su [heroku.com](https://heroku.com)
2. **Heroku CLI**: Installa da [qui](https://devcenter.heroku.com/articles/heroku-cli)
3. **Account Cloudinary**: Registrati su [cloudinary.com](https://cloudinary.com/users/register/free)
4. **Git**: Assicurati che il progetto sia un repository Git

## Setup Rapido

Usa lo script automatico:

```bash
./scripts/deploy-heroku.sh
```

## Setup Manuale

### 1. Login su Heroku
```bash
heroku login
```

### 2. Crea l'app Heroku
```bash
heroku create nome-tua-app --region eu
```

### 3. Aggiungi PostgreSQL
```bash
heroku addons:create heroku-postgresql:essential-0
```

### 4. Configura Cloudinary

Ottieni le credenziali dal tuo dashboard Cloudinary e configura:

```bash
heroku config:set \
  CLOUDINARY_CLOUD_NAME="tuo-cloud-name" \
  CLOUDINARY_API_KEY="tua-api-key" \
  CLOUDINARY_API_SECRET="tuo-api-secret" \
  SPRING_PROFILES_ACTIVE="prod"
```

### 5. Build del progetto
```bash
./mvnw clean package -DskipTests
```

### 6. Deploy
```bash
git add .
git commit -m "Deploy su Heroku"
git push heroku master
```

## Comandi Utili

### Visualizza i log
```bash
heroku logs --tail
```

### Apri l'app
```bash
heroku open
```

### Verifica lo stato
```bash
heroku ps
```

### Connettiti al database
```bash
heroku pg:psql
```

### Backup del database
```bash
heroku pg:backups:capture
heroku pg:backups:download
```

## Configurazione Cloudinary

1. Vai su [cloudinary.com](https://cloudinary.com) e accedi
2. Dal Dashboard, copia:
   - Cloud Name
   - API Key
   - API Secret
3. Usa questi valori quando richiesto dallo script

## Troubleshooting

### L'app non si avvia
- Controlla i log: `heroku logs --tail`
- Verifica che tutte le variabili d'ambiente siano configurate: `heroku config`

### Errori di database
- Verifica la connessione: `heroku pg:info`
- Controlla che le migrazioni siano state eseguite

### Problemi con le immagini
- Verifica le credenziali Cloudinary: `heroku config`
- Controlla i log per errori di upload

### Build fallisce
- Assicurati di usare Java 21
- Verifica che `system.properties` contenga: `java.runtime.version=21`

## Note Importanti

- **Primo avvio**: Al primo deploy, l'app potrebbe impiegare qualche minuto per avviarsi
- **Database**: I dati locali NON vengono migrati automaticamente
- **Immagini**: Le immagini caricate localmente devono essere ricaricate su Cloudinary
- **SSL**: Heroku fornisce SSL automaticamente
- **Limiti free tier**: 
  - Database: 10,000 righe
  - Cloudinary: 25 crediti mensili
  - Heroku: 550-1000 ore dyno al mese 
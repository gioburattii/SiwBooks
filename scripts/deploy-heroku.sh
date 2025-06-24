#!/bin/bash

echo "=== Heroku Deployment Script per SiwBooks ==="
echo

# Verifica se Heroku CLI è installato
if ! command -v heroku &> /dev/null; then
    echo "❌ Heroku CLI non è installato. Installalo da: https://devcenter.heroku.com/articles/heroku-cli"
    exit 1
fi

# Verifica se l'utente è loggato su Heroku
if ! heroku auth:whoami &> /dev/null; then
    echo "❌ Non sei loggato su Heroku. Esegui: heroku login"
    exit 1
fi

echo "✅ Heroku CLI installato e autenticato"
echo

# Chiedi il nome dell'app se non è già impostato
if [ -z "$HEROKU_APP_NAME" ]; then
    read -p "Inserisci il nome dell'app Heroku (lascia vuoto per generarne uno automatico): " HEROKU_APP_NAME
fi

# Crea l'app Heroku se non esiste
if [ -z "$HEROKU_APP_NAME" ]; then
    echo "📱 Creazione app Heroku con nome automatico..."
    heroku create --region eu
else
    echo "📱 Creazione app Heroku: $HEROKU_APP_NAME"
    heroku create $HEROKU_APP_NAME --region eu || echo "App già esistente o nome non disponibile"
fi

# Ottieni il nome dell'app
HEROKU_APP_NAME=$(heroku apps:info --json | grep -o '"name":"[^"]*' | grep -o '[^"]*$')
echo "📱 Nome app: $HEROKU_APP_NAME"

# Aggiungi il remote git di Heroku
heroku git:remote -a $HEROKU_APP_NAME

# Aggiungi l'addon PostgreSQL
echo
echo "🗄️  Aggiunta database PostgreSQL..."
heroku addons:create heroku-postgresql:essential-0 || echo "PostgreSQL potrebbe essere già configurato"

# Configura le variabili d'ambiente di Cloudinary
echo
echo "☁️  Configurazione Cloudinary..."
echo "Hai bisogno di un account Cloudinary. Registrati su: https://cloudinary.com/users/register/free"
echo
read -p "Inserisci CLOUDINARY_CLOUD_NAME: " CLOUDINARY_CLOUD_NAME
read -p "Inserisci CLOUDINARY_API_KEY: " CLOUDINARY_API_KEY
read -s -p "Inserisci CLOUDINARY_API_SECRET: " CLOUDINARY_API_SECRET
echo

# Imposta le variabili d'ambiente
heroku config:set \
    CLOUDINARY_CLOUD_NAME="$CLOUDINARY_CLOUD_NAME" \
    CLOUDINARY_API_KEY="$CLOUDINARY_API_KEY" \
    CLOUDINARY_API_SECRET="$CLOUDINARY_API_SECRET" \
    SPRING_PROFILES_ACTIVE="prod"

# Build del progetto
echo
echo "🔨 Build del progetto..."
./mvnw clean package -DskipTests

# Ottieni il branch corrente
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)

# Deploy
echo
echo "🚀 Deploy su Heroku dal branch $CURRENT_BRANCH..."
git add .
git commit -m "Deploy su Heroku con PostgreSQL e Cloudinary" || echo "Nessuna modifica da committare"
git push heroku $CURRENT_BRANCH:main || git push heroku $CURRENT_BRANCH:master

# Verifica lo stato
echo
echo "✅ Deploy completato!"
echo
echo "📊 Stato dell'applicazione:"
heroku ps
echo
echo "📝 Log recenti:"
heroku logs --tail -n 50
echo
echo "🌐 Apri l'app nel browser:"
echo "   https://$HEROKU_APP_NAME.herokuapp.com"
echo
echo "Per vedere i log in tempo reale: heroku logs --tail" 
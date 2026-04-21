# NEO4FLIX — Documentation Technique & Réponses d'Audit

> Plateforme de recommandation de films basée sur **Neo4j Graph Database**, **Spring Boot Microservices** et **Angular 17**.

---

## 🚀 LANCEMENT DU PROJET

### Prérequis
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installé et en cours d'exécution
- Git installé
- Ports libres : **80**, **443**, **7474**, **7687**

### Étape 1 — Cloner le dépôt
```bash
git clone https://github.com/Alkzim974/neo4flix.git
cd neo4flix
```

### Étape 2 — Lancer tous les services
```bash
docker compose up -d --build
```

> ⏳ La première exécution prend **3 à 5 minutes** (téléchargement des dépendances Maven et compilation).

### Étape 3 — Vérifier que tous les conteneurs sont actifs
```bash
docker compose ps
```
Vous devez voir **6 conteneurs** avec le statut `Up` :

| Conteneur | Rôle | Port |
|---|---|---|
| `neo4flix-neo4j` | Base de données graphe | 7474 (browser), 7687 (bolt) |
| `neo4flix-user-service` | Authentification & Utilisateurs | 8082 |
| `neo4flix-movie-service` | Catalogue de films | 8081 |
| `neo4flix-rating-service` | Gestion des notes | 8083 |
| `neo4flix-recommendation-service` | Moteur de recommandation | 8084 |
| `neo4flix-frontend` | Interface Angular (Nginx) | 80 |

### Étape 4 — Accéder à l'application
| URL | Description |
|---|---|
| **https://localhost** | Application Web sécurisée (Recommandé) |
| **http://localhost** | Redirige automatiquement vers HTTPS |
| **http://localhost:7474** | Neo4j Browser (visualisation des données) |

> [!CAUTION]
> **Certificat SSL Auto-signé** : Comme nous utilisons un certificat généré localement, votre navigateur affichera un avertissement de sécurité.
> 1. Cliquez sur **Paramètres avancés**.
> 2. Cliquez sur **Continuer vers le site localhost (non sécurisé)**.

**Identifiants Neo4j Browser :**
- Login : `neo4j`
- Password : `neo4flix_password`

### Étape 5 — Créer un premier compte utilisateur
1. Aller sur http://localhost
2. Cliquer sur **"S'inscrire"**
3. Remplir le formulaire (email, username, mot de passe)
4. Scanner le QR Code Google Authenticator avec l'application **Google Authenticator** sur votre téléphone
5. Se connecter avec votre username, mot de passe et le code TOTP à 6 chiffres

### Arrêt du projet
```bash
docker compose down
```

---

## 📋 RÉPONSES AUX QUESTIONS D'AUDIT

---

### ✅ FUNCTIONAL
**Question : L'application fonctionne-t-elle correctement et peut-on naviguer dans ses fonctionnalités principales ?**

Oui. L'application est entièrement fonctionnelle. Voici le scénario de navigation complet :

1. **Inscription** → https://localhost/register : Créer un compte avec username/email/mot de passe + QR Code 2FA
2. **Connexion** → https://localhost/login : Login avec username, mot de passe, et code TOTP (Google Authenticator)
3. **Catalogue** → Page d'accueil : Parcourir tous les films, noter un film depuis sa fiche, ajouter à la watchlist
4. **Recherche avancée** → Filtrer par titre, genre, et/ou année sur la page d'accueil
5. **Profil** → http://localhost/profile : Voir les films notés (avec score), la watchlist, et les amis
6. **Recommandations** → http://localhost/recommendations : 3 onglets — personnel, par amis, partagés

---

### 📊 DATA AND DESIGN
**Question : Les nœuds et relations représentent-ils correctement les films, utilisateurs et notes ?**

**Manipulation — Visualiser le graphe dans Neo4j Browser (http://localhost:7474) :**

```cypher
-- Voir tous les nœuds et leurs relations (échantillon)
MATCH (n)-[r]->(m) RETURN n, r, m LIMIT 50
```

```cypher
-- Voir les notes des utilisateurs
MATCH (u:User)-[r:RATED]->(m:Movie) RETURN u.username, r.score, m.title LIMIT 20
```

```cypher
-- Voir les genres des films
MATCH (m:Movie)-[:IN_GENRE]->(g:Genre) RETURN m.title, collect(g.name) LIMIT 20
```

```cypher
-- Voir les relations d'amitié
MATCH (u:User)-[:FRIENDS_WITH]->(f:User) RETURN u.username, f.username
```

**Structure des nœuds :**

| Nœud | Propriétés |
|---|---|
| `User` | username, email, password (BCrypt), mfaSecret, mfaEnabled |
| `Movie` | title, description, releaseDate, posterUrl, averageRating |
| `Genre` | name |

**Structure des relations :**

| Relation | De → Vers | Attributs |
|---|---|---|
| `RATED` | User → Movie | score (1.0 – 5.0) |
| `IN_GENRE` | Movie → Genre | — |
| `FRIENDS_WITH` | User → User | — |
| `WATCHLIST` | User → Movie | — |
| `RECOMMENDED` | User → User | movie (titre du film partagé) |

---

### 🎬 MOVIE MICROSERVICE
**Question : Le microservice Movie gère-t-il efficacement la lecture et l'écriture des données ?**

Oui. Le `movie-service` expose les endpoints suivants :

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/movies` | Tous les films (avec note personnelle si connecté) |
| `GET` | `/api/movies/{id}` | Détail d'un film |
| `GET` | `/api/movies/search?q=titre` | Recherche par titre |
| `GET` | `/api/movies/search/advanced?title=&genre=&year=` | Recherche multi-critères |
| `POST` | `/api/movies` | Ajouter un film |
| `GET` | `/api/movies/watchlist` | Films sauvegardés de l'utilisateur |

**Test avec Postman :**
```
GET http://localhost/api/movies
Authorization: Bearer <votre_token_jwt>
```

La note personnelle de l'utilisateur (`userScore`) est retournée pour chaque film grâce à un `OPTIONAL MATCH` Cypher — si l'utilisateur n'a pas noté le film, `userScore` vaut 0.

---

### 👤 USER MICROSERVICE
**Question : Le microservice User gère-t-il correctement les opérations sur les données utilisateur ?**

Oui. Le `user-service` gère :

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/register` | Inscription + génération secret 2FA |
| `POST` | `/api/auth/login` | Connexion + vérification TOTP |
| `GET` | `/api/users/me` | Profil de l'utilisateur connecté |
| `GET` | `/api/users/friends` | Liste des amis |
| `POST` | `/api/users/friends/{username}` | Ajouter un ami |
| `DELETE` | `/api/users/friends/{username}` | Supprimer un ami |

**Note de sécurité :** Les données sensibles (`password`, `mfaSecret`) ne sont **jamais** exposées dans les réponses API — un DTO `UserSummaryDto` (id, username, email uniquement) est utilisé à la place.

**Question : Stocke-t-il les notes des films ?**

Les notes sont gérées par le `rating-service`, mais stockées dans la même base Neo4j partagée. La relation `(User)-[:RATED {score}]->(Movie)` est lisible depuis tous les services.

---

### ⭐ RATING MICROSERVICE
**Question : Le microservice Rating gère-t-il correctement les opérations liées aux notes ?**

Oui. Le `rating-service` expose :

| Méthode | Endpoint | Description |
|---|---|---|
| `POST` | `/api/ratings/movie/{movieId}` | Noter un film (score 1.0 à 5.0) |
| `DELETE` | `/api/ratings/movie/{movieId}` | Supprimer une note |

Lors d'une notation, le service :
1. Crée ou met à jour la relation `[:RATED {score}]` entre le `User` et le `Movie`
2. Recalcule et met à jour le champ `averageRating` sur le nœud `Movie`

**Test Postman :**
```json
POST http://localhost/api/ratings/movie/123
Authorization: Bearer <token>
Content-Type: application/json

{ "score": 4.5 }
```

---

### 🤖 RECOMMENDATION MICROSERVICE

#### Spring Data Neo4j et OGM
**Spring Data Neo4j 7** (SDN7) est utilisé. Il intègre l'OGM (Object-Graph Mapper) qui mappe automatiquement :
- Les classes Java annotées `@Node` → Nœuds Neo4j (ex: `Movie`, `User`)
- Les champs annotés `@Relationship` → Relations Neo4j (ex: `FRIENDS_WITH`, `IN_GENRE`)
- Les interfaces `Neo4jRepository<T, ID>` → Opérations CRUD automatiques sans code SQL/Cypher boilerplate

#### Algorithmes de recommandation implementés

**1. Content-Based Filtering (Filtrage par contenu)**
```cypher
MATCH (u:User {username: $username})-[r:RATED]->(m:Movie)-[:IN_GENRE]->(g:Genre)
WHERE r.score >= 3.5
WITH u, g, count(g) as genreFreq
MATCH (g)<-[:IN_GENRE]-(rec:Movie)
WHERE NOT EXISTS((u)-[:RATED]->(rec))
RETURN rec ORDER BY genreFreq DESC LIMIT 10
```
→ Identifie les genres préférés de l'utilisateur (fréquence pondérée par les notes), puis recommande les films non encore vus dans ces genres.

**2. Social Collaborative Filtering (Filtrage collaboratif social)**
```cypher
MATCH (u:User {username: $username})-[:FRIENDS_WITH]->(friend:User)
MATCH (friend)-[r:RATED]->(m:Movie)
WHERE r.score >= 3.0 AND NOT EXISTS((u)-[:RATED]->(m))
WITH m, max(r.score) AS maxScore
RETURN m ORDER BY maxScore DESC LIMIT 10
```
→ Exploite le graphe social : remonte les films bien notés par les amis, que l'utilisateur n'a pas encore vus.

**3. Partage Direct** (`RECOMMENDED`)
→ Tout utilisateur peut envoyer une recommandation directe à un ami via la fiche d'un film.

---

### 🔍 SEARCH FUNCTIONALITY
**Question : Les utilisateurs peuvent-ils rechercher des films par différents critères ?**

Oui. La page d'accueil propose une **recherche avancée multi-critères** combinable :

- **Titre** : `toLower(m.title) CONTAINS toLower($title)` (insensible à la casse)
- **Genre** : `toLower(g.name) CONTAINS toLower($genre)`
- **Année** : `m.releaseDate CONTAINS $year`

**Test Postman :**
```
GET http://localhost/api/movies/search/advanced?title=dark&genre=Action&year=2008
Authorization: Bearer <token>
```

---

### 🎥 MOVIE DETAILS
**Question : Les détails du film sont-ils correctement affichés ?**

Oui. La page de détail (`/movie/:id`) affiche :
- Titre, description, date de sortie, genres
- Note moyenne de la communauté (calculée dynamiquement)
- Affiche du film (posterUrl)
- Formulaire de notation (score 1 à 5)
- Section partage (entrer le username d'un ami)
- Bouton "Ajouter à la watchlist"

---

### ⭐ RATING MOVIES
**Question : La page de notation fonctionne-t-elle ?**

Oui. Depuis la fiche d'un film :
1. Entrer une note entre 1 et 5
2. Cliquer "Valider"
3. La relation `(User)-[:RATED {score}]->(Movie)` est créée/mise à jour dans Neo4j
4. La note moyenne recalculée s'affiche immédiatement
5. Sur la page d'accueil, le badge "⭐ Ma note : X/5" apparaît sur la carte du film

---

### 💡 VIEWING RECOMMENDATIONS
**Question : Les utilisateurs peuvent-ils voir des recommandations basées sur leurs notes ?**

Oui. La page **Recommandations** (`/recommendations`) propose 3 onglets :

| Onglet | Algorithme | Condition d'activation |
|---|---|---|
| 🎯 **Pour moi** | Content-Based | Avoir noté au moins un film ≥ 3.5 |
| 👥 **Mes Amis aiment** | Collaborative Social | Avoir des amis qui ont noté des films ≥ 3.0 |
| 📩 **Partagés avec moi** | Partage direct | Un ami a partagé un film via la fiche |

---

### 💾 SAVING MOVIES (WATCHLIST)
**Question : Y a-t-il une fonctionnalité de sauvegarde de films ?**

Oui. Depuis la fiche d'un film :
- **Ajouter** : Bouton "Ajouter à la Watchlist" → crée `(User)-[:WATCHLIST]->(Movie)` dans Neo4j
- **Voir** : Onglet "Ma Watchlist" dans le profil utilisateur
- **Retirer** : Bouton "Enlever" depuis le profil

---

### 🔗 SHARING RECOMMENDATIONS
**Question : Les utilisateurs peuvent-ils partager des recommandations avec leurs amis ?**

Oui. Depuis la fiche d'un film :
1. Saisir le **username** de l'ami destinataire
2. Cliquer **"Partager"**
3. La relation `(User)-[:RECOMMENDED {movie: titre}]->(User)` est créée dans Neo4j
4. L'ami retrouve le film dans son onglet "📩 Partagés avec moi"

**Test Postman :**
```json
POST http://localhost/api/recommendations/share
Authorization: Bearer <token>
Content-Type: application/json

{
  "receiverUsername": "nom_ami",
  "movieId": 123
}
```

---

### 🖥️ USER-FRIENDLY INTERFACE
**Question : L'interface est-elle conviviale et intuitive ?**

Oui. L'interface Angular 17 propose :
- **Dark mode** glassmorphism avec palette teal/violet
- **Micro-animations** sur les cartes de films (hover translateY, transition CSS)
- **Navigation claire** en barre supérieure persistante
- **Messages de feedback** temps réel (succès/erreur colorés)
- **Badge "⭐ Ma note"** sur les films déjà évalués directement sur la page d'accueil
- **Onglets** dans le profil et les recommandations pour organiser l'information

---

### 📄 WEB PAGES
**Question : Les pages essentielles fonctionnent-elles correctement ?**

| Page | URL | Statut |
|---|---|---|
| Connexion | `/login` | ✅ |
| Inscription + 2FA | `/register` | ✅ |
| Accueil / Catalogue | `/` | ✅ |
| Détail film + Notation | `/movie/:id` | ✅ |
| Recommandations | `/recommendations` | ✅ |
| Profil (notes, watchlist, amis) | `/profile` | ✅ |

---

### 🔐 SECURITY

#### Authentication & Authorization (JWT)
- Tokens **JWT** signés avec HMAC-SHA256 et une clé secrète de 256+ bits
- Tous les endpoints sauf `/api/auth/**` exigent le header `Authorization: Bearer <token>`
- Architecture **stateless** (aucune session serveur, aucun cookie)
- Chaque microservice valide indépendamment le token

**Test Postman — Connexion :**
```json
POST http://localhost/api/auth/login
Content-Type: application/json

{
  "username": "votre_username",
  "password": "votre_mdp",
  "totpCode": "123456"
}
```
→ Retourne un token JWT à utiliser dans tous les appels suivants.

#### Two-Factor Authentication (2FA)
- Implémentée avec **Google Authenticator** (algorithme TOTP – RFC 6238)
- À l'inscription, un QR Code unique est généré avec la bibliothèque `googleauth`
- Le secret TOTP est stocké hashé côté serveur
- À chaque connexion, un code valide 30 secondes est obligatoire

#### Password Security
- Hashage avec **BCrypt** (algorithme adaptatif, résistant aux attaques par force brute)
- Le mot de passe en clair n'est jamais stocké ni loggué
- Champ `password` exclu de tous les DTOs de réponse API

#### Data Encryption
- Mots de passe chiffrés avec BCrypt en base de données
- Secrets 2FA stockés mais jamais exposés dans les réponses API
- **Note :** En environnement local Docker, HTTP est utilisé. En production, Nginx serait configuré avec un certificat SSL (Let's Encrypt) pour activer HTTPS sur le port 443.

---

### 🧪 TESTING

#### Tests Fonctionnels à Réaliser

**1. Inscription avec email déjà utilisé**
```json
POST /api/auth/register
{ "email": "email_existant@test.com", ... }
→ 400 Bad Request : "Email déjà utilisé"
```

**2. Connexion avec mauvais code 2FA**
```json
POST /api/auth/login
{ "username": "user", "password": "mdp", "totpCode": "000000" }
→ 401 Unauthorized
```

**3. Accès sans token JWT**
```
GET /api/movies
(sans header Authorization)
→ 401 Unauthorized
```

**4. Ajout d'un ami inexistant**
```
POST /api/users/friends/utilisateur_inexistant
→ 400 Bad Request : "Ami introuvable"
```

**5. Auto-ajout comme ami**
```
POST /api/users/friends/mon_propre_username
→ 400 Bad Request : "Vous ne pouvez pas vous ajouter vous-même"
```

**6. Note hors limites**
```json
POST /api/ratings/movie/1
{ "score": 10 }
→ Géré (score stocké tel quel, validation à renforcer côté backend)
```

**7. Film inexistant**
```
GET /api/movies/99999999
→ 500 : "Film introuvable"
```

**8. Recommandations sans notes**
```
GET /api/recommendations
→ 200 OK avec liste vide (comportement gracieux)
```

#### Commandes de Diagnostic
```bash
# Logs en temps réel
docker logs -f neo4flix-user-service
docker logs -f neo4flix-recommendation-service

# État des conteneurs
docker compose ps

# Redémarrer un service spécifique
docker compose restart movie-service
```

#### Requêtes Cypher de Vérification (Neo4j Browser)
```cypher
-- Statistiques globales
MATCH (n) RETURN labels(n) AS type, count(n) AS total

-- Notes d'un utilisateur
MATCH (u:User {username: 'votre_username'})-[r:RATED]->(m:Movie)
RETURN m.title, r.score ORDER BY r.score DESC

-- Relations FRIENDS_WITH
MATCH (u:User)-[:FRIENDS_WITH]->(f:User)
RETURN u.username AS utilisateur, f.username AS ami

-- Partages envoyés
MATCH (s:User)-[r:RECOMMENDED]->(recv:User)
RETURN s.username AS expediteur, r.movie AS film, recv.username AS destinataire
```

---

## 🏗️ ARCHITECTURE TECHNIQUE

```
┌──────────────────────────────────────────────────────┐
│              Angular 17 Frontend (Nginx :80)         │
└──────────┬───────────┬──────────┬────────────────────┘
           │           │          │
   ┌───────▼───┐ ┌─────▼──┐ ┌────▼────┐ ┌──────────────────────┐
   │  movie-   │ │ user-  │ │rating- │ │  recommendation-     │
   │  service  │ │service │ │service │ │     service          │
   │  :8081   │ │ :8082  │ │ :8083  │ │      :8084          │
   └─────┬─────┘ └───┬────┘ └────┬───┘ └────────────┬─────────┘
         │            │           │                   │
         └────────────┴───────────┴───────────────────┘
                                  │
                       ┌──────────▼──────────┐
                       │    Neo4j 5.18.0     │
                       │  Graph Database     │
                       │ APOC + GDS plugins  │
                       │   Bolt :7687        │
                       └─────────────────────┘
```

| Composant | Technologie |
|---|---|
| Backend | Spring Boot 4.0.5 (Java 17) |
| ORM / Graphe | Spring Data Neo4j 7 (SDN7) |
| Base de données | Neo4j 5.18.0 |
| Frontend | Angular 17, TypeScript |
| Reverse Proxy | Nginx |
| Sécurité | JWT (JJWT), BCrypt, TOTP (googleauth) |
| Infrastructure | Docker & Docker Compose |

# 🛡️ WinniKnight: MindGuardians

> **Entrega 4 — Paso 4 | Fundamentos de Aplicaciones Web | Grupo 3**
>
> Pablo Urbina (24001508) · Angel Camargo (24003664)

---

## 🎬 Videos Demo

| Recurso | Enlace |
|---------|--------|
| Demo — Propuesta inicial | https://drive.google.com/file/d/1ZyDlsCptiJ2eG7tsD6BAPbL74Yg7oNhM/view?usp=sharing |
| Demo — Aplicación Web | https://drive.google.com/file/d/1NNAQc6mQNKX52B78IO8z2aeazt5VI6Sr/view?usp=sharing |
| Demo — Aplicación Android | https://drive.google.com/file/d/1bxPxngGmwEKTlfBIntJgyHzvepCbwSsv/view?usp=sharing |

---

## Descripción del Proyecto

**WinniKnight: MindGuardians** gamifica el bienestar físico y mental. El usuario derrota monstruos que representan malos hábitos (sedentarismo, estrés, deshidratación) realizando acciones reales de salud.

El proyecto consta de **dos plataformas** que comparten el mismo proyecto Firebase (`mindguardians-b07d3`) y la misma Cloud Function de Python, por lo que el progreso del héroe se sincroniza en tiempo real entre dispositivos:

- **App Android** — Combate RPG completo con narración generativa en tiempo real.
- **Plataforma Web (Next.js)** — Dashboard orientado a escritorio con estadísticas, inventario, tienda, ranking y chat con IA.

---

## Tabla de Contenidos

- [Arquitectura del Sistema](#arquitectura-del-sistema)
- [Justificación de Frameworks](#justificación-de-frameworks)
- [Stack Tecnológico](#stack-tecnológico)
- [Estructura de Firestore](#estructura-de-firestore)
- [App Android](#app-android)
- [Plataforma Web (Next.js)](#plataforma-web-nextjs)
- [Cloud Function (Compartida)](#cloud-function-compartida)
- [Sincronización entre Plataformas](#sincronización-entre-plataformas)
- [Seguridad](#seguridad)
- [Desviaciones respecto a la Propuesta](#desviaciones-respecto-a-la-propuesta)
- [Changelog](#changelog)

---

## Arquitectura del Sistema

Ambas plataformas siguen una arquitectura de tres capas:

```
Cliente (Android / Web)
        │
        ▼
Firebase (Auth + Firestore)   ←→   Next.js API Routes (proxy)
        │
        ▼
Cloud Function Python  →  Gemini API (gemini-2.5-flash)
```

La API key de Gemini **nunca reside en el cliente**. Vive en Firebase Secrets y solo es accesible desde la Cloud Function.

### Flujo del Oráculo / Narración IA

| Paso | Origen | Destino | Protocolo |
|------|--------|---------|-----------|
| 1 | Cliente (Android/Web) | Cloud Function / API Route | HTTP POST |
| 2 | Next.js API Route | `ORACLE_FUNCTION_URL` | fetch server-side |
| 3 | Cloud Function | Gemini API | HTTPS + API key (Firebase Secrets) |
| 4 | Gemini API | Cloud Function | JSON `{reply: string}` |
| 5 | Cloud Function | Cliente | JSON `{reply: string}` |

> En Android, el cliente llama directamente a la Cloud Function vía Retrofit. En Web, pasa primero por `/api/oracle` (Next.js API Route) para que la URL de la Cloud Function nunca llegue al navegador.

---

## Justificación de Frameworks

### Android — Jetpack Compose (MVVM)

Se eligió desarrollo nativo sobre Flutter o React Native porque el combate RPG exige animaciones y respuesta táctil predecibles. **Jetpack Compose** es el framework de UI usado — construye toda la interfaz en Kotlin sin XML, con un modelo de estado reactivo donde cualquier cambio en el `GameViewModel` (HP, XP, cola de monstruos) reactualiza automáticamente solo los componentes afectados. La arquitectura sigue el patrón **MVVM**: los composables solo renderizan estado, los ViewModels coordinan la lógica, y los repositorios (`FirebaseRepository`, `GeminiRepository`) son la única capa que toca Firebase y Retrofit — los ViewModels nunca llaman al SDK directamente.

### Web — Next.js

**Next.js** es el framework que estructura toda la plataforma web. Se eligió principalmente por sus **API Routes**, que permiten correr el proxy a la Cloud Function en el servidor, de modo que la URL nunca llega al navegador. El dashboard corre en CSR (React del lado del cliente) porque los datos cambian frecuentemente y la interactividad es prioritaria. Tailwind CSS maneja los estilos con tokens propios (`mq-*`) sin CSS custom.

---

## Stack Tecnológico

### Android

| Capa | Tecnología |
|------|-----------|
| UI | Jetpack Compose + Material3 |
| Lenguaje | Kotlin 2.0.0 |
| Estado | ViewModel + MutableState |
| Autenticación | Firebase Auth (email/password) |
| Base de datos | Firebase Firestore SDK |
| HTTP | Retrofit 2.11.0 + OkHttp 4.12.0 |
| IA generativa | Gemini API (gemini-2.5-flash) vía Cloud Function |
| Build system | Android Gradle Plugin 8.5.2 / Min SDK API 26 |

### Web (Next.js)

| Capa | Tecnología |
|------|-----------|
| Framework | Next.js 14.2.5 (App Router) |
| Lenguaje | TypeScript 5.5.3 |
| Estilos | Tailwind CSS 3.4.6 (tokens `mq-*`) |
| Gráficas | Recharts 2.12.7 |
| Autenticación | Firebase Auth (email/password) |
| Base de datos | Firebase Firestore SDK 10.14.1 |
| Backend IA | Next.js API Routes + Cloud Function compartida |
| Rendering | CSR para dashboard, SSR en API Routes |

---

## Estructura de Firestore

Ambas plataformas leen y escriben los mismos documentos en el mismo proyecto Firebase.

```
users/{uid}
├── displayName        String    — Nombre único de héroe
├── heroLevel          Number
├── heroXp             Number    — XP en nivel actual (0–99)
├── heroGold           Number
├── heroHp             Number
├── heroMaxHP          Number    — Base + bonus de equipo
├── totalXp            Number    — XP histórico acumulado (ranking)
├── heroClass          String    
│
├── battles/{id}
│   ├── date           Timestamp
│   ├── habitType      String    — Tipo de hábito del monstruo
│   ├── result         String    — "Victoria" | "Derrota"
│   ├── goldEarned     Number
│   └── xpEarned       Number
│
├── inventory/{id}
│   ├── id / name / stat / emoji  String
│   ├── bonusHp / bonusPower      Number
│   └── purchasedAt    Timestamp
│
└── vitality/{yyyy-mm-dd}        — (Web únicamente)
    ├── weightLossKg   Number
    └── activityMinutes Number

shop_catalog/{id}
├── name / emoji / stat / price   String / Number
├── bonusHp / bonusPower          Number
├── category / rarity / iconName  String  — (Web)
```

---

## App Android

### Archivos Clave

| Archivo | Responsabilidad |
|---------|----------------|
| `GameState.kt` | ViewModel principal: estado completo del juego, lógica de combate, compras, Oráculo |
| `AuthViewModel.kt` | Autenticación con StateFlow: login, register, logout |
| `data/FirebaseRepository.kt` | Todas las operaciones Firestore |
| `data/GeminiRepository.kt` | Cliente Retrofit hacia la Cloud Function: `consultOracle`, `narrateHeroAttack`, `narrateMonsterAttack`, `generateBoss` |
| `functions/main.py` | Cloud Function HTTP compartida con la web |

### Flujos Funcionales

#### Autenticación
1. App inicia → verifica `FirebaseAuth.getInstance().currentUser`
2. Sin sesión → `LoginScreen`; sin cuenta → `RegisterScreen`
3. Registro: validación local → `isDisplayNameTaken()` → `createUserWithEmailAndPassword()` → `createUserProfile()` en Firestore
4. Logout: `signOut()` + `sessionKey++` — fuerza recreación del ViewModel, limpiando todo el estado de sesión

#### Combate
1. `attacksForMonster()` determina los 3 ataques disponibles según el tipo del monstruo
2. Usuario presiona ataque → `GameViewModel.attack(damage, actionName)`
3. Cálculo: `boostedDamage = damage + (damage × equipPower / 100)`; si `bonusActive`: `finalDamage × 1.5`
4. `narrateHeroAttack()` → narración épica vía Cloud Function → `BattleLog` actualizado
5. Si monstruo vivo: `monsterCounterattack()` → `narrateMonsterAttack()` → `heroHp` reducido
6. Victoria: `VictoryModal` → `continueAfterVictory()` → guardar en Firestore

#### Panel de Reporte (Hazaña / Debilidad)
- **Hazaña** (botón verde): `validateDeed()` → narración épica + `bonusActive = true` → próximo ataque ×1.5 daño
- **Debilidad** (botón rojo): `generateBoss()` → boss personalizado (`NOMBRE: / TIPO:`) insertado en posición 2 de `monsterQueue` (180 HP, `isBoss=true`)

### Sistema de Progresión

| Mecánica | Detalle |
|----------|---------|
| XP por monstruo | `30 + (monsterQueue.size % 3) × 15` |
| XP por boss | 80 puntos fijos |
| Level up | Al acumular 100 XP: `heroLevel++`, `heroXp -= 100`, `heroMaxHP += 10` |
| Oro por victoria | `20 + (monsterQueue.size % 3) × 10` |
| Oro por boss | 60 monedas fijas |
| `totalXp` | Se incrementa con cada victoria y nunca se resetea — es el valor del ranking global |

#### Ataques Dinámicos por Tipo de Monstruo

| Tipo de monstruo | Ataques | Daño base |
|-----------------|---------|-----------|
| Gravedad / Peso | Hidratación, Salto Galáctico, Aliento | 20 / 25 / 15 |
| Caos / Mental / Mente | Zen Cósmico, Sueño Estelar, Aliento | 30 / 25 / 20 |
| Vacío / Estelar / Energía | Hidratación, Zen Cósmico, Sueño Estelar | 20 / 20 / 20 |
| Oscura / Fuerza | Salto Galáctico, Zen Cósmico, Hidratación | 25 / 25 / 15 |
| Default | Elixir Estelar, Salto Galáctico, Zen Cósmico | 15 / 20 / 25 |


---

## Plataforma Web (Next.js)

### Archivos Clave

| Archivo | Responsabilidad |
|---------|----------------|
| `src/app/page.tsx` | Dashboard principal: sesión, carga paralela de Firestore, estado global |
| `src/app/api/oracle/route.ts` | Proxy seguro hacia la Cloud Function — la URL nunca llega al browser |
| `src/lib/firestore.ts` | Todas las operaciones Firestore: `loadUserData`, `saveBattle`, `loadRanking`, etc. |
| `src/lib/auth.ts` | `login`, `register` (con validación de nombre único), `logout` |
| `src/components/dashboard/VitalityStats.tsx` | Gráfica Recharts de vitalidad semanal — exclusiva de la web |
| `src/components/oracle/GeminiOracle.tsx` | Chat en tiempo real con Gemini AI |

### Rutas

| Ruta | Descripción |
|------|-------------|
| `/login` | Inicio de sesión |
| `/register` | Registro |
| `/` | Dashboard principal (protegido por Auth) |
| `/api/oracle` | Proxy POST hacia Cloud Function |

### Secciones del Dashboard

| Sección | Componentes |
|---------|------------|
| `dashboard` | WelcomeBanner + VitalityStats + GeminiOracle + HeroesRanking + EquipmentInventory + BattlesHistory + GoldShop |
| `battles` / `inventory` / `oracle` / `ranking` / `profile` / `settings` / `guide` | Vista individual de cada módulo |

### Flujos Funcionales

#### Carga del Dashboard (paralela)
```
Promise.all([
  loadUserData(), loadBattles(), loadRanking(), loadInventory(), loadShopCatalog()
])
→ computeStatBonuses() → estado actualizado
→ refreshVitality()     // últimos 7 días para VitalityStats
```

#### Oráculo Gemini
1. `fetch('/api/oracle', {method: 'POST', body: {message}})` desde el browser
2. `route.ts` (servidor) → `fetch(ORACLE_FUNCTION_URL)` con la URL oculta
3. Cloud Function → Gemini API → respuesta épica → `ChatMessage {role: 'oracle'}`

#### Compra en la Tienda
1. Validación local: `user.gold >= item.price`
2. `spendGold()`: lee `heroGold` de Firestore → descuenta → `addDoc` en `inventory/` con `serverTimestamp()`

### Variables de Entorno

```env
# .env.local — nunca subido al repositorio

NEXT_PUBLIC_FIREBASE_API_KEY=
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=
NEXT_PUBLIC_FIREBASE_PROJECT_ID=
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=
NEXT_PUBLIC_FIREBASE_APP_ID=

ORACLE_FUNCTION_URL=   # sin NEXT_PUBLIC_ — solo existe en el servidor
```

---

## Cloud Function (Compartida)

**Endpoint:** `https://consult-oracle-ajesprbufa-uc.a.run.app/`  
**Método:** `POST` · `Content-Type: application/json`

Actúa como proxy seguro entre los clientes y Gemini API. Es el único punto del sistema donde existe la API key de Gemini.

| Escenario | Respuesta |
|-----------|-----------|
| `OPTIONS` (preflight CORS) | 204 No Content |
| Método distinto a `POST` | 405 |
| Body sin campo `message` | 400 |
| Error Gemini API | 502 |
| Éxito | 200 `{reply: string}` |

### Prompts por Función (Android)

| Función | Descripción | Máx |
|---------|-------------|-----|
| `consultOracle` | Consejero de salud épico (prompt en `main.py`) | ~3 oraciones |
| `narrateHeroAttack` | Narrador épico: ataque del héroe + daño + bonus | 20 palabras |
| `narrateMonsterAttack` | Narrador épico: contraataque del monstruo | 20 palabras |
| `validateDeed` | Celebrar hazaña + mencionar bonus daño | 20 palabras |
| `generateBoss` | Crear jefe desde debilidad (`NOMBRE: / TIPO:`) | Formato fijo |

**Timeouts OkHttp (Android):** connect 30s · read 60s · write 30s

---

## Sincronización entre Plataformas

Las credenciales, el progreso del héroe, las batallas, el inventario, el ranking y el catálogo de la tienda son compartidos en tiempo real entre ambas plataformas. Un ítem comprado en Android aparece en la web y viceversa.

### Diferencias entre Plataformas

| Característica | Android | Web |
|----------------|:-------:|:---:|
| Combate RPG completo | ✅ | ❌ |
| Narración de ataques (Gemini) | ✅ | ❌ |
| Bosses dinámicos por debilidades | ✅ | ❌ |
| Chat Oráculo | ✅ | ✅ |
| Vitalidad semanal (Recharts) | ❌ | ✅ |
| Perfil editable | ✅ | ✅ |
| Inventario / Tienda / Ranking | ✅ | ✅ |

---

## Seguridad

| Aspecto | Implementación |
|---------|---------------|
| API key de Gemini | Firebase Secrets — nunca en código fuente ni en APK/browser |
| URL de Cloud Function | Variable sin `NEXT_PUBLIC_` — solo existe en el servidor Next.js |
| `.env.local` / `google-services.json` | En `.gitignore` |
| Autenticación de sesión | Firebase Auth con `onAuthStateChanged` — redirige si no hay sesión |
| Validación de nombre de héroe | `isDisplayNameTaken()` consulta Firestore antes de registrar |
| Validación de compras | `spendGold()` verifica `heroGold` en Firestore antes de descontar |
| CORS (Cloud Function) | `Access-Control-Allow-Origin: *` + preflight `OPTIONS` |
| Reglas de Firestore | Modo Test durante desarrollo — reglas de producción en entrega final |

---

## Desviaciones respecto a la Propuesta

| Propuesto | Implementado | Por qué |
|-----------|-------------|---------|
| Servidor Express como intermediario de Gemini | Firebase Cloud Functions (Python) | Cloud Functions gestiona los secrets de forma nativa, su deploy es parte del mismo proyecto Firebase y elimina la necesidad de un servidor separado. Express habría añadido infraestructura sin beneficio real. |
| Firebase Auth con Google + Email | Solo Email/Password | Requiere configuración OAuth adicional fuera del alcance de tiempo. La funcionalidad principal no se ve afectada. |
| Firebase Storage para avatares y sprites | Emojis como avatares | Elimina uploads y URLs de Storage sin impactar la experiencia central. |
| Smart Push Notifications (FCM) | ❌ No implementado | Por falta de tiempo. FCM requiere un servidor de envío programado (o Cloud Scheduler) y configuración de permisos en el dispositivo, lo que lo hacía la feature más costosa en tiempo. La feature habría enviado alertas del tipo: *"Llevas 2 horas sentado, un Golem de Piedra bloquea tu camino"*. |

---

## Caracteristicas planeadas que no se implementaron en la entrega

| Caracteristica | Estado | Función |
|-----------|-------------|---------|
| Configuración | - Implementado sin funcionalidad | Permitir personalizar la aplicación, alternando entre modo claro y oscuro, idioma, activar o desactivar las notificaciones, privacidad del perfil y eliminar cuenta. |
| Clases de heroe | - Implementado sin funcionalidad | Las distintas clases ortogarian distintas estadisticas al usuario y una ligera personalización sobre la narración IA de los combates, además de cambiar el icono de su perfil por un icono que represente la clase seleccionada. |

---

## Cambios fuera de tiempo

| Fecha | Cambio |
|-------|--------|
| 4 mayo 2026 | **Fix — cálculo de `heroHP` al subir de nivel** (`GameState.kt`): al momento de subir de nivel, la vida no se asignaba al valor máximo correctamente debido a que esto se manejaba de la siguiente forma: `heroHp = heroMaxHP... if (heroXp >= 100) ... heroMaxHP += 10 `, por lo que no se estaba manejando correctamente la recuperación de vida del usuario despues del combate, se realizó un cambio minimo para solucionarlo: `heroHp = heroMaxHP... if (heroXp >= 100) ... heroMaxHP += 10 heroHp = heroMaxHP` |
| 6 mayo 2026 | Presentación de Canva subida (formato PDF) al repositorio. |
| 6 mayo 2026 | README original modificado con los links actualizados de los videos DEMO, eliminacion de la documentación individual para Android y Web en formato PDF, sustituyendola por una documentación unificada Android + Web en este README. |

---


# FOCO

Timer de foco pessoal — Kotlin Multiplatform + Compose Multiplatform (Android-first).

Package / applicationId: `com.foco.app`

## Abrir no Android Studio

1. Abra Android Studio (Ladybug / 2024.2+ recomendado).
2. **File → Open** → selecione a pasta `foco/`.
3. Aguarde o sync do Gradle.
4. Selecione o run configuration `composeApp` e um emulador/device.

## Build APK (debug)

```bash
# precisa de JDK 17+ e Android SDK
export ANDROID_HOME=/caminho/para/android-sdk
# Client ID (não commitar): env ou local.properties → spotify.client.id=
export SPOTIFY_CLIENT_ID=seu_client_id
./gradlew :composeApp:assembleDebug
```

APK gerado em:

```
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

Confirmar injeção sem vazar o segredo:

```bash
./gradlew :composeApp:verifySpotifyClientId
```

## Spotify (Android)

- Fluxo: Authorization Code + **PKCE** (sem client secret).
- Redirect URI (exata): `https://foco-bzo.pages.dev/config`
- Scopes: `user-read-currently-playing`, `user-read-playback-state`, `user-modify-playback-state`
- Tokens: `EncryptedSharedPreferences` no Android
- Miniplayer: now playing, play/pause, skip; pausa ao fim do foco se o toggle estiver ligado
- Client ID: `SPOTIFY_CLIENT_ID` (env) ou `spotify.client.id` em `local.properties` → `BuildConfig.SPOTIFY_CLIENT_ID`

### App Links (necessário para o redirect HTTPS fechar o Custom Tab no app)

O Spotify redireciona para HTTPS. Sem App Links verificados, o Chrome Custom Tabs pode permanecer na página web.

Publique em `https://foco-bzo.pages.dev/.well-known/assetlinks.json`:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.foco.app",
    "sha256_cert_fingerprints": ["<SHA-256 do keystore de debug ou release>"]
  }
}]
```

SHA-256 (debug típico):

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep SHA256
```

Também confirme no [Spotify Dashboard](https://developer.spotify.com/dashboard) que o redirect `https://foco-bzo.pages.dev/config` está cadastrado para este Client ID (Android + web).

## Estrutura

- `composeApp` — app Android + shared UI/logic (`commonMain`)
- Telas: Timer, Histórico, Config, Entrar
- Prefs: multiplatform-settings (SharedPreferences no Android)
- Histórico: in-memory (scaffold)
- Spotify: OAuth PKCE + Web API no Android; stub em iOS

## Escopo v1

Android only. iOS targets no template, UI iOS não implementada.

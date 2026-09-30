package com.foco.app.ui

/** All pt-BR microcopy from /workspace/pomodoro/COPY.md — do not invent English CTAs. */
object Strings {
    const val APP_NAME = "FOCO"

    // Modes
    const val MODE_FOCUS = "FOCO"
    const val MODE_BREAK_SHORT = "PAUSA CURTA"
    const val MODE_BREAK_LONG = "PAUSA LONGA"
    const val MODE_SHORT = "CURTA"
    const val MODE_LONG = "LONGA"

    // Status
    const val STATUS_IDLE = "PARADO"
    const val STATUS_RUNNING = "RODANDO"
    const val STATUS_PAUSED = "PAUSADO"

    // Controls
    const val PLAY = "INICIAR"
    const val PAUSE = "PAUSAR"
    const val RESET = "ZERAR"
    const val A11Y_PLAY = "Iniciar timer"
    const val A11Y_PAUSE = "Pausar timer"
    const val A11Y_RESET = "Zerar timer"

    // Session
    const val SESSION = "SESSÃO"
    const val SESSION_PLACEHOLDER = "sem nome"
    const val SESSION_EMPTY = "sem nome"

    // Nav
    const val HISTORY = "HISTÓRICO"
    const val CONFIG = "CONFIG"
    const val LOGIN = "ENTRAR"
    const val BACK_TIMER = "← Timer"
    const val CONTINUE_GUEST = "Continuar sem conta"

    // Feedback end of cycle
    const val FEEDBACK_FOCUS_DONE = "FOCO FEITO. PAUSA."
    const val FEEDBACK_SHORT_DONE = "PAUSA CURTA FEITA. DE VOLTA."
    const val FEEDBACK_LONG_DONE = "PAUSA LONGA FEITA. DE VOLTA."

    // Config
    const val CONFIG_TITLE = "CONFIG"
    const val DURATIONS = "DURAÇÕES"
    const val LABEL_FOCUS = "Foco"
    const val LABEL_BREAK_SHORT = "Pausa curta"
    const val LABEL_BREAK_LONG = "Pausa longa"
    const val DURATION_HINT = "Padrão: minutos. Interno: segundos."
    const val UNIT_S = "s"
    const val UNIT_MIN = "min"
    const val UNIT_H = "h"
    const val ALERTA = "ALERTA"
    const val SOUND = "Som ao fim"
    const val NOTIFICATIONS = "Notificação ao fim"
    const val THEME = "TEMA"
    const val THEME_LIGHT = "Claro"
    const val THEME_DARK = "Escuro"
    const val A11Y_THEME_LIGHT = "Tema claro"
    const val A11Y_THEME_DARK = "Tema escuro"
    const val DEFAULT_SESSION_NAME = "NOME PADRÃO DA SESSÃO"
    const val INVALID_DURATION = "Precisa ser um número maior que zero."
    const val CONFIG_SAVED = "Config salva."

    // Spotify (config)
    const val SPOTIFY = "SPOTIFY"
    const val CONNECT_SPOTIFY = "Conectar Spotify"
    const val DISCONNECT_SPOTIFY = "Desconectar"
    const val SPOTIFY_CONNECTING = "Conectando…"
    const val SPOTIFY_CONNECTED = "Conectado"
    const val SPOTIFY_DISCONNECTED = "Desconectado"
    const val PAUSE_ON_FOCUS_END = "Pausar música quando o foco acabar"
    const val SPOTIFY_MINIPLAYER_HINT = "Miniplayer no timer: play · pausa · próxima"
    const val SPOTIFY_ERROR = "Spotify não conectou. Tenta de novo."

    // Miniplayer
    const val NOTHING_PLAYING = "NADA TOCANDO"
    const val SPOTIFY_PLAY_TRACK = "TOCAR"
    const val SPOTIFY_PAUSE_TRACK = "PAUSAR"
    const val SKIP = "PRÓXIMA"
    const val SPOTIFY_FAILED = "SPOTIFY FALHOU"

    // Histórico
    const val HISTORY_TITLE = "HISTÓRICO"
    const val HISTORY_NOTE = "Neste aparelho. Sem conta, não sobe pra nuvem."
    const val HISTORY_EMPTY_TITLE = "NADA AINDA."
    const val HISTORY_EMPTY_BODY = "Quando um ciclo terminar, aparece aqui."

    // Entrar
    const val LOGIN_TITLE = "ENTRAR"
    const val LOGIN_PROMISE = "Guarda histórico e config além deste aparelho."
    const val EMAIL = "E-mail"
    const val PASSWORD = "Senha"
    const val LOGIN_SUBMIT = "Entrar"
    const val LOGIN_ERROR = "Não deu. Confere e-mail e senha."
    const val LOGIN_STUB = "Entrar"

    // Legacy aliases kept so older call sites compile during migration
    const val TIMER = "Timer"
    const val SESSION_NAME = SESSION
    const val SESSION_NAME_HINT = SESSION_PLACEHOLDER
    const val MODE_FOCUS_LEGACY = MODE_FOCUS
    const val MODE_BREAK = MODE_BREAK_SHORT
    const val UNIT = "Unidade"
    const val DISPLAY_NAME = DEFAULT_SESSION_NAME
    const val SPOTIFY_SUMMARY = SPOTIFY_MINIPLAYER_HINT
    const val SPOTIFY_CONNECTED_PREFIX = "Conectado"
    const val HISTORY_EMPTY = HISTORY_EMPTY_BODY
    const val LOGIN_SUBTITLE = LOGIN_PROMISE
    const val SAVE = "Salvar"
    const val COMPLETED = "FOCO FEITO"
    const val INTERRUPTED = "Interrompida"
    const val GUEST = "Convidado"
}

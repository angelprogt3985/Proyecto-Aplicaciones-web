import json
import os
import requests
from firebase_functions import https_fn
from firebase_functions.options import set_global_options
from firebase_admin import initialize_app
from firebase_functions import params
set_global_options(max_instances=10)
initialize_app()



GEMINI_API_KEY = params.SecretParam("GEMINI_API_KEY")


@https_fn.on_request()
def consult_oracle(req: https_fn.Request) -> https_fn.Response:
    api_key = GEMINI_API_KEY.value

    GEMINI_URL = f"https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key={api_key}"

    if req.method == "OPTIONS":
        headers = {
            "Access-Control-Allow-Origin":  "*",
            "Access-Control-Allow-Methods": "POST",
            "Access-Control-Allow-Headers": "Content-Type",
        }
        return https_fn.Response("", status=204, headers=headers)

    headers = {"Access-Control-Allow-Origin": "*"}

    if req.method != "POST":
        return https_fn.Response("Metodo no permitido", status=405, headers=headers)

    body = req.get_json(silent=True)
    if not body or "message" not in body:
        return https_fn.Response("Falta el campo message", status=400, headers=headers)

    user_message = body["message"]

    prompt = f"""
Eres el Oráculo de Gemini en un juego RPG de bienestar llamado WinniKnight: MindGuardians.
El jugador derrota monstruos que representan malos hábitos realizando acciones reales de salud.
El usuario te describe una hazaña de salud que realizó hoy.
Responde con una narración épica corta en español, máximo 3 oraciones, y otorga un bonificador de daño.
Hazaña del jugador: {user_message}
    """.strip()

    payload = {
        "contents": [
            {"parts": [{"text": prompt}]}
        ]
    }

    gemini_response = requests.post(
        GEMINI_URL,
        headers={"Content-Type": "application/json"},
        data=json.dumps(payload),
    )

    if gemini_response.status_code != 200:
        return https_fn.Response("Error al contactar Gemini", status=502, headers=headers)

    result = gemini_response.json()
    reply  = result["candidates"][0]["content"]["parts"][0]["text"]

    return https_fn.Response(
        json.dumps({"reply": reply}),
        status=200,
        headers={**headers, "Content-Type": "application/json"},
    )
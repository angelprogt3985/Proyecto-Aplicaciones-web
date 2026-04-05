import { NextRequest, NextResponse } from "next/server";

const FUNCTION_URL = process.env.ORACLE_FUNCTION_URL;

export async function POST(req: NextRequest) {
  try {
    const body = await req.json();
    const { message } = body;

    if (!message || typeof message !== "string") {
      return NextResponse.json(
        { error: "Falta el campo message" },
        { status: 400 }
      );
    }

    const response = await fetch(`${FUNCTION_URL}`, {
      method:  "POST",
      headers: { "Content-Type": "application/json" },
      body:    JSON.stringify({ message }),
    });

    if (!response.ok) {
      return NextResponse.json(
        { error: "Error al contactar al Oráculo" },
        { status: 502 }
      );
    }

    const data = await response.json();
    return NextResponse.json({ reply: data.reply });

  } catch {
    return NextResponse.json(
      { error: "Error interno del servidor" },
      { status: 500 }
    );
  }
}
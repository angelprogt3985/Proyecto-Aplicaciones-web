"use client";

import { useState, useRef, useEffect } from "react";
import { Sparkles, Send } from "lucide-react";
import type { ChatMessage } from "@/lib/types";

interface GeminiOracleProps {
  initialMessages: ChatMessage[];
}

export function GeminiOracle({ initialMessages }: GeminiOracleProps) {
  const [messages, setMessages]   = useState<ChatMessage[]>(initialMessages);
  const [input, setInput]         = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError]         = useState<string | null>(null);
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSend = async () => {
    const trimmed = input.trim();
    if (!trimmed || isLoading) return;

    const userMsg: ChatMessage = {
      id:        `msg_${Date.now()}_user`,
      role:      "user",
      text:      trimmed,
      timestamp: new Date().toISOString(),
    };

    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch("/api/oracle", {
        method:  "POST",
        headers: { "Content-Type": "application/json" },
        body:    JSON.stringify({ message: trimmed }),
      });

      if (!response.ok) {
        throw new Error("Error al contactar al Oráculo");
      }

      const data = await response.json();

      const oracleMsg: ChatMessage = {
        id:        `msg_${Date.now()}_oracle`,
        role:      "oracle",
        text:      data.reply,
        timestamp: new Date().toISOString(),
      };

      setMessages((prev) => [...prev, oracleMsg]);
    } catch {
      setError("El cosmos guarda silencio. Intenta de nuevo.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="relative flex h-full flex-col overflow-hidden rounded-xl border border-mq-gold/30 bg-mq-card p-6 shadow-lg">
      <div className="pointer-events-none absolute right-0 top-0 h-40 w-40 rounded-full bg-mq-gold opacity-15 blur-[70px]" />

      <div className="relative mb-5 flex items-center gap-3">
        <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-mq-gold to-mq-gold2 shadow-lg shadow-mq-gold/30">
          <Sparkles className="h-6 w-6 text-mq-bg" />
        </div>
        <div>
          <p className="text-lg font-semibold text-mq-text">Oráculo Gemini</p>
          <p className="text-xs text-mq-gold">Consejero de Salud con IA</p>
        </div>
      </div>

      <div
        ref={scrollRef}
        className="relative mb-4 flex flex-1 flex-col gap-3 overflow-y-auto rounded-xl border border-mq-blue/15 bg-mq-bg p-4"
        style={{ maxHeight: 280 }}
      >
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${msg.role === "user" ? "justify-end" : "justify-start"}`}
          >
            <div
              className={`max-w-[82%] rounded-xl px-4 py-2.5 text-sm leading-relaxed ${
                msg.role === "user"
                  ? "bg-mq-blue text-mq-bg"
                  : "border border-mq-gold/25 bg-mq-card text-mq-text"
              }`}
            >
              {msg.role === "oracle" && (
                <div className="mb-1.5 flex items-center gap-1.5">
                  <Sparkles className="h-3 w-3 text-mq-gold" />
                  <span className="text-[11px] text-mq-gold">Oráculo</span>
                </div>
              )}
              {msg.text}
            </div>
          </div>
        ))}

        {isLoading && (
          <div className="flex justify-start">
            <div className="rounded-xl border border-mq-gold/25 bg-mq-card px-4 py-2.5 text-sm text-mq-muted">
              El Oráculo consulta el cosmos...
            </div>
          </div>
        )}

        {error && (
          <div className="text-center text-xs text-red-400">{error}</div>
        )}
      </div>

      <div className="flex gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          placeholder="Escribe tu pregunta al oráculo..."
          disabled={isLoading}
          className="flex-1 rounded-xl border border-mq-gold/30 bg-mq-bg px-4 py-3 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-gold disabled:opacity-50"
        />
        <button
          onClick={handleSend}
          disabled={isLoading}
          className="flex items-center justify-center rounded-xl bg-gradient-to-br from-mq-gold to-mq-gold2 px-4 shadow-lg shadow-mq-gold/25 transition-opacity hover:opacity-85 disabled:opacity-50"
        >
          <Send className="h-4 w-4 text-mq-bg" />
        </button>
      </div>

      <div className="mt-3 flex items-center justify-center gap-2 text-[11px] text-mq-muted">
        <span className="h-1.5 w-1.5 rounded-full bg-mq-gold" />
        IA Activa · Gemini
      </div>
    </div>
  );
}
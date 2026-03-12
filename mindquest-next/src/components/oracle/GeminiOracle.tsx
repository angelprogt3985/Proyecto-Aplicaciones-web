"use client";

import { useState, useRef, useEffect } from "react";
import { Sparkles, Send } from "lucide-react";
import type { ChatMessage } from "@/lib/types";
import { MOCK_ORACLE_REPLIES } from "@/lib/data/mock";

interface GeminiOracleProps {
  initialMessages: ChatMessage[];
}

export function GeminiOracle({ initialMessages }: GeminiOracleProps) {
  const [messages, setMessages] = useState<ChatMessage[]>(initialMessages);
  const [input, setInput] = useState("");
  const scrollRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  /**
   * handleSend — swap this function body for a real API call later.
   * e.g. POST /api/oracle with { userId, message }
   */
  const handleSend = () => {
    const trimmed = input.trim();
    if (!trimmed) return;

    const userMsg: ChatMessage = {
      id: `msg_${Date.now()}_user`,
      role: "user",
      text: trimmed,
      timestamp: new Date().toISOString(),
    };

    const oracleReply: ChatMessage = {
      id: `msg_${Date.now()}_oracle`,
      role: "oracle",
      text: MOCK_ORACLE_REPLIES[
        Math.floor(Math.random() * MOCK_ORACLE_REPLIES.length)
      ],
      timestamp: new Date().toISOString(),
    };

    setMessages((prev) => [...prev, userMsg, oracleReply]);
    setInput("");
  };

  return (
    <div className="relative flex h-full flex-col overflow-hidden rounded-xl border border-mq-gold/30 bg-mq-card p-6 shadow-lg">
      {/* Glow */}
      <div className="pointer-events-none absolute right-0 top-0 h-40 w-40 rounded-full bg-mq-gold opacity-15 blur-[70px]" />

      {/* Header */}
      <div className="relative mb-5 flex items-center gap-3">
        <div className="flex h-12 w-12 flex-shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-mq-gold to-mq-gold2 shadow-lg shadow-mq-gold/30">
          <Sparkles className="h-6 w-6 text-mq-bg" />
        </div>
        <div>
          <p className="text-lg font-semibold text-mq-text">Oráculo Gemini</p>
          <p className="text-xs text-mq-gold">Consejero de Salud con IA</p>
        </div>
      </div>

      {/* Messages */}
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
      </div>

      {/* Input */}
      <div className="flex gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleSend()}
          placeholder="Escribe tu pregunta al oráculo..."
          className="flex-1 rounded-xl border border-mq-gold/30 bg-mq-bg px-4 py-3 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-gold"
        />
        <button
          onClick={handleSend}
          className="flex items-center justify-center rounded-xl bg-gradient-to-br from-mq-gold to-mq-gold2 px-4 shadow-lg shadow-mq-gold/25 transition-opacity hover:opacity-85"
        >
          <Send className="h-4 w-4 text-mq-bg" />
        </button>
      </div>

      <div className="mt-3 flex items-center justify-center gap-2 text-[11px] text-mq-muted">
        <span className="h-1.5 w-1.5 rounded-full bg-mq-blue" />
        IA Activa · Respuestas simuladas
      </div>
    </div>
  );
}

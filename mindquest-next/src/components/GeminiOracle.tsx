"use client";
import { Sparkles, Send } from 'lucide-react';
import { useState } from 'react';

interface Message { role: 'oracle' | 'user'; text: string; }

const oracleResponses = [
  '¡Excelente pregunta! Basándome en tus estadísticas, te recomiendo incrementar tu actividad cardiovascular en un 15% esta semana.',
  'Veo que has progresado mucho. Para optimizar tu recuperación, considera agregar 30 minutos de estiramientos después de tu entrenamiento.',
  'Tu dedicación es admirable. Recuerda que el descanso es tan importante como el ejercicio. Asegúrate de dormir 7-8 horas.',
  'Consejo del oráculo: Mantén una hidratación constante. Tu cuerpo necesita al menos 2 litros de agua al día para rendir al máximo.',
];

export function GeminiOracle() {
  const [message, setMessage] = useState('');
  const [conversation, setConversation] = useState<Message[]>([
    { role: 'oracle', text: '¡Saludos, valiente aventurero! Soy el Oráculo Gemini, tu guía en el camino hacia la salud y el bienestar. ¿En qué puedo asistirte hoy?' },
  ]);

  const handleSend = () => {
    if (!message.trim()) return;
    const oracleMsg = { role: 'oracle' as const, text: oracleResponses[Math.floor(Math.random() * oracleResponses.length)] };
    setConversation(prev => [...prev, { role: 'user', text: message }, oracleMsg]);
    setMessage('');
  };

  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(224,179,94,0.3)] p-6 h-full flex flex-col shadow-lg relative overflow-hidden">
      <div className="absolute top-0 right-0 w-40 h-40 bg-[#e0b35e] rounded-full blur-[80px] opacity-25 pointer-events-none" />
      
      <div className="relative flex items-center gap-3 mb-6">
        <div className="w-12 h-12 bg-gradient-to-br from-[#e0b35e] to-[#d4a855] rounded-full flex items-center justify-center shadow-lg shadow-[#e0b35e]/30">
          <Sparkles className="w-7 h-7 text-[#1a1f2e]" />
        </div>
        <div>
          <h2 className="text-xl font-medium text-[#f0f6fc]">Oráculo Gemini</h2>
          <p className="text-sm text-[#e0b35e]">Consejero de Salud con IA</p>
        </div>
      </div>

      <div className="flex-1 bg-[#1a1f2e] rounded-lg p-4 mb-4 overflow-y-auto max-h-[300px] space-y-4 border border-[rgba(88,166,255,0.15)]">
        {conversation.map((msg, index) => (
          <div key={index} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[80%] px-4 py-3 rounded-lg ${
              msg.role === 'user'
                ? 'bg-[#58a6ff] text-[#1a1f2e]'
                : 'bg-[#242b3d] text-[#f0f6fc] border border-[rgba(224,179,94,0.3)]'
            }`}>
              {msg.role === 'oracle' && (
                <div className="flex items-center gap-2 mb-1">
                  <Sparkles className="w-4 h-4 text-[#e0b35e]" />
                  <span className="text-xs text-[#e0b35e]">Oráculo</span>
                </div>
              )}
              <p className="text-sm">{msg.text}</p>
            </div>
          </div>
        ))}
      </div>

      <div className="relative flex gap-2">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Escribe tu pregunta al oráculo..."
          className="flex-1 bg-[#1a1f2e] border border-[rgba(224,179,94,0.3)] rounded-lg px-4 py-3 text-[#f0f6fc] placeholder-[#a0aec0] focus:outline-none focus:ring-2 focus:ring-[#e0b35e] text-sm"
        />
        <button
          onClick={handleSend}
          className="px-4 py-3 bg-gradient-to-r from-[#e0b35e] to-[#d4a855] hover:from-[#d4a855] hover:to-[#e0b35e] text-[#1a1f2e] rounded-lg transition-all shadow-lg shadow-[#e0b35e]/30"
        >
          <Send className="w-5 h-5" />
        </button>
      </div>

      <div className="mt-4 flex items-center justify-center gap-2 text-xs text-[#a0aec0]">
        <div className="w-2 h-2 rounded-full bg-[#58a6ff]" />
        <span>IA Activa • Respuestas simuladas</span>
      </div>
    </div>
  );
}

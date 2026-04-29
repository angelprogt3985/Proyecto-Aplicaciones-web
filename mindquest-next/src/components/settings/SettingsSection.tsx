"use client";

import { useState } from "react";
import { Bell, Moon, Globe, Shield, Trash2, Check } from "lucide-react";
import { cn } from "@/lib/utils";

export function SettingsSection() {
  const [notifications, setNotifications] = useState(true);
  const [darkMode, setDarkMode] = useState(true);
  const [language, setLanguage] = useState("es");
  const [privacy, setPrivacy] = useState("public");
  const [saved, setSaved] = useState(false);

  const handleSave = () => {
    setSaved(true);
    setTimeout(() => setSaved(false), 3000);
  };

  const Toggle = ({ value, onChange }: { value: boolean; onChange: (v: boolean) => void }) => (
    <button onClick={() => onChange(!value)}
      className={cn("relative h-6 w-11 rounded-full transition-colors", value ? "bg-mq-blue" : "bg-mq-bg border border-mq-blue/30")}>
      <span className={cn("absolute top-0.5 h-5 w-5 rounded-full bg-white shadow transition-all", value ? "left-5" : "left-0.5")} />
    </button>
  );

  return (
    <div className="flex flex-col gap-6 max-w-2xl">
      <div>
        <h2 className="text-2xl font-bold text-mq-text">Ajustes</h2>
        <p className="mt-1 text-sm text-mq-muted">Personaliza tu experiencia en MindQuest</p>
      </div>

      {/* Notifications */}
      <div className="rounded-xl border border-mq-blue/25 bg-mq-card p-6">
        <div className="mb-4 flex items-center gap-2">
          <Bell className="h-5 w-5 text-mq-blue" />
          <h3 className="font-semibold text-mq-text">Notificaciones</h3>
        </div>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-mq-text">Notificaciones del juego</p>
            <p className="text-xs text-mq-muted">Recibe alertas de combates, ranking y recompensas</p>
          </div>
          <Toggle value={notifications} onChange={setNotifications} />
        </div>
      </div>

      {/* Appearance */}
      <div className="rounded-xl border border-mq-blue/25 bg-mq-card p-6">
        <div className="mb-4 flex items-center gap-2">
          <Moon className="h-5 w-5 text-mq-blue" />
          <h3 className="font-semibold text-mq-text">Apariencia</h3>
        </div>
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-mq-text">Modo oscuro</p>
            <p className="text-xs text-mq-muted">Interfaz optimizada para poca luz</p>
          </div>
          <Toggle value={darkMode} onChange={setDarkMode} />
        </div>
      </div>

      {/* Language */}
      <div className="rounded-xl border border-mq-blue/25 bg-mq-card p-6">
        <div className="mb-4 flex items-center gap-2">
          <Globe className="h-5 w-5 text-mq-blue" />
          <h3 className="font-semibold text-mq-text">Idioma</h3>
        </div>
        <select value={language} onChange={(e) => setLanguage(e.target.value)}
          className="w-full rounded-xl border border-mq-blue/25 bg-mq-bg px-4 py-2.5 text-sm text-mq-text outline-none focus:border-mq-blue">
          <option value="es">Español</option>
          <option value="en">English</option>
          <option value="pt">Português</option>
        </select>
      </div>

      {/* Privacy */}
      <div className="rounded-xl border border-mq-blue/25 bg-mq-card p-6">
        <div className="mb-4 flex items-center gap-2">
          <Shield className="h-5 w-5 text-mq-blue" />
          <h3 className="font-semibold text-mq-text">Privacidad</h3>
        </div>
        <div className="flex gap-3">
          {[{ id:"public",label:"Público" },{ id:"friends",label:"Amigos" },{ id:"private",label:"Privado" }].map((opt) => (
            <button key={opt.id} onClick={() => setPrivacy(opt.id)}
              className={cn("flex-1 rounded-xl border px-4 py-2 text-sm transition-all",
                privacy === opt.id ? "border-mq-blue bg-mq-blue text-mq-bg" : "border-mq-blue/25 text-mq-muted hover:border-mq-blue/40 hover:text-mq-text")}>
              {opt.label}
            </button>
          ))}
        </div>
      </div>

      {/* Danger zone */}
      <div className="rounded-xl border border-red-500/20 bg-red-500/5 p-6">
        <div className="mb-4 flex items-center gap-2">
          <Trash2 className="h-5 w-5 text-red-400" />
          <h3 className="font-semibold text-red-400">Zona de Peligro</h3>
        </div>
        <p className="mb-4 text-sm text-mq-muted">Estas acciones son permanentes y no se pueden deshacer.</p>
        <button className="rounded-xl border border-red-500/40 px-4 py-2 text-sm text-red-400 transition-colors hover:bg-red-500/10">
          Eliminar cuenta
        </button>
      </div>

      <div className="flex items-center gap-4">
        <button onClick={handleSave}
          className="flex items-center gap-2 rounded-xl bg-mq-blue px-6 py-2.5 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-all hover:bg-mq-blue2">
          <Check className="h-4 w-4" /> Guardar Cambios
        </button>
        {saved && <span className="text-sm text-mq-blue">✓ Ajustes guardados</span>}
      </div>
    </div>
  );
}
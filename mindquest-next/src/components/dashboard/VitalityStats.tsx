"use client";

import { useState } from "react";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend,
} from "recharts";
import { TrendingDown, Activity, Plus, Check, Loader2 } from "lucide-react";
import type { WeeklyVitalityStats } from "@/lib/types";
import { saveVitalityEntry } from "@/lib/firestore";

interface VitalityStatsProps {
  stats: WeeklyVitalityStats;
  onStatsUpdated?: () => void;
}

const DAY_LABELS = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

export function VitalityStats({ stats, onStatsUpdated }: VitalityStatsProps) {
  const [showForm, setShowForm] = useState(false);
  const [weightLoss, setWeightLoss] = useState("");
  const [activity, setActivity] = useState("");
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const chartData = stats.entries.map((entry, i) => ({
    day: DAY_LABELS[i] ?? i,
    pesoKg: entry.weightLossKg,
    actividad: entry.activityMinutes,
  }));

  const todayISO = new Date().toISOString().split("T")[0];

  const handleSave = async () => {
    const kg = parseFloat(weightLoss);
    const min = parseInt(activity, 10);
    if (isNaN(kg) || kg < 0) { setError("Ingresa un valor de peso válido (ej: 0.5)"); return; }
    if (isNaN(min) || min < 0) { setError("Ingresa minutos de actividad válidos (ej: 30)"); return; }
    setSaving(true); setError(null);
    try {
      await saveVitalityEntry({ date: todayISO, weightLossKg: kg, activityMinutes: min });
      setSaved(true); setShowForm(false); setWeightLoss(""); setActivity("");
      onStatsUpdated?.();
      setTimeout(() => setSaved(false), 3000);
    } catch { setError("Error al guardar. Intenta de nuevo."); }
    finally { setSaving(false); }
  };

  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-6 shadow-lg">
      <div className="pointer-events-none absolute right-0 top-0 h-48 w-48 rounded-full bg-mq-blue opacity-10 blur-[90px]" />

      <div className="relative mb-6 flex items-start justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold text-mq-text">Estadísticas Semanales de Vitalidad</h2>
          <p className="mt-0.5 text-sm text-mq-muted">Progreso de esta semana</p>
        </div>
        <div className="flex items-center gap-3">
          <div className="rounded-xl border border-mq-blue/30 bg-mq-bg px-4 py-2">
            <div className="flex items-center gap-1.5 text-mq-blue">
              <TrendingDown className="h-3.5 w-3.5" />
              <span className="text-xs">Peso total</span>
            </div>
            <p className="mt-1 text-base font-semibold text-mq-text">{stats.totalWeightLossKg.toFixed(1)} kg</p>
          </div>
          <div className="rounded-xl border border-mq-gold/30 bg-mq-bg px-4 py-2">
            <div className="flex items-center gap-1.5 text-mq-gold">
              <Activity className="h-3.5 w-3.5" />
              <span className="text-xs">Promedio</span>
            </div>
            <p className="mt-1 text-base font-semibold text-mq-text">{stats.avgActivityMinutes} min</p>
          </div>
          <button
            onClick={() => setShowForm((v) => !v)}
            className="flex items-center gap-2 rounded-xl border border-mq-blue/40 bg-mq-blue px-4 py-2 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-all hover:bg-mq-blue2"
          >
            {saved ? <><Check className="h-4 w-4" /> ¡Guardado!</> : <><Plus className="h-4 w-4" /> Registrar Hoy</>}
          </button>
        </div>
      </div>

      {showForm && (
        <div className="relative mb-6 rounded-xl border border-mq-blue/30 bg-mq-bg p-4">
          <p className="mb-3 text-sm font-medium text-mq-text">
            Registro de hoy — <span className="text-xs text-mq-muted">{new Date().toLocaleDateString("es-ES", { weekday: "long", day: "numeric", month: "long" })}</span>
          </p>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="mb-1.5 block text-xs text-mq-muted">Pérdida de peso (kg)</label>
              <input type="number" min="0" step="0.1" placeholder="ej: 0.3" value={weightLoss}
                onChange={(e) => setWeightLoss(e.target.value)}
                className="w-full rounded-xl border border-mq-blue/25 bg-mq-card px-4 py-2 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-blue" />
            </div>
            <div>
              <label className="mb-1.5 block text-xs text-mq-muted">Minutos de actividad física</label>
              <input type="number" min="0" step="1" placeholder="ej: 45" value={activity}
                onChange={(e) => setActivity(e.target.value)}
                className="w-full rounded-xl border border-mq-blue/25 bg-mq-card px-4 py-2 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-blue" />
            </div>
          </div>
          {error && <p className="mt-2 text-xs text-red-400">{error}</p>}
          <div className="mt-4 flex justify-end gap-2">
            <button onClick={() => { setShowForm(false); setError(null); }}
              className="rounded-xl border border-mq-blue/20 px-4 py-2 text-sm text-mq-muted transition-colors hover:text-mq-text">Cancelar</button>
            <button onClick={handleSave} disabled={saving}
              className="flex items-center gap-2 rounded-xl bg-mq-blue px-5 py-2 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-all hover:bg-mq-blue2 disabled:opacity-60">
              {saving ? <><Loader2 className="h-4 w-4 animate-spin" /> Guardando…</> : <><Check className="h-4 w-4" /> Guardar</>}
            </button>
          </div>
        </div>
      )}

      <ResponsiveContainer width="100%" height={290}>
        <BarChart data={chartData} margin={{ top: 10, right: 20, left: 0, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(88,166,255,0.12)" vertical={false} />
          <XAxis dataKey="day" stroke="#a0aec0" tick={{ fontSize: 12 }} axisLine={false} tickLine={false} />
          <YAxis stroke="#a0aec0" tick={{ fontSize: 12 }} axisLine={false} tickLine={false} width={32} />
          <Tooltip contentStyle={{ backgroundColor: "#1a1f2e", border: "1px solid rgba(88,166,255,0.3)", borderRadius: "10px", color: "#f0f6fc", fontSize: 13 }} cursor={{ fill: "rgba(88,166,255,0.07)" }} />
          <Legend wrapperStyle={{ fontSize: 12, color: "#a0aec0", paddingTop: 12 }} />
          <Bar dataKey="pesoKg" name="Pérdida de Peso (kg)" fill="#58a6ff" radius={[6, 6, 0, 0]} />
          <Bar dataKey="actividad" name="Actividad Física (min)" fill="#e0b35e" radius={[6, 6, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
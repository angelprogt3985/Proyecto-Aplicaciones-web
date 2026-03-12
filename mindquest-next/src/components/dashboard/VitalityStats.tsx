"use client";

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import { TrendingDown, Activity } from "lucide-react";
import type { WeeklyVitalityStats } from "@/lib/types";

interface VitalityStatsProps {
  stats: WeeklyVitalityStats;
}

const DAY_LABELS = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

export function VitalityStats({ stats }: VitalityStatsProps) {
  const chartData = stats.entries.map((entry, i) => ({
    day: DAY_LABELS[i] ?? i,
    pesoKg: entry.weightLossKg,
    actividad: entry.activityMinutes,
  }));

  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-6 shadow-lg">
      {/* Glow */}
      <div className="pointer-events-none absolute right-0 top-0 h-48 w-48 rounded-full bg-mq-blue opacity-10 blur-[90px]" />

      <div className="relative mb-6 flex items-start justify-between gap-4">
        <div>
          <h2 className="text-lg font-semibold text-mq-text">
            Estadísticas Semanales de Vitalidad
          </h2>
          <p className="mt-0.5 text-sm text-mq-muted">Progreso de esta semana</p>
        </div>

        <div className="flex gap-3">
          <div className="rounded-xl border border-mq-blue/30 bg-mq-bg px-4 py-2">
            <div className="flex items-center gap-1.5 text-mq-blue">
              <TrendingDown className="h-3.5 w-3.5" />
              <span className="text-xs">Peso</span>
            </div>
            <p className="mt-1 text-base font-semibold text-mq-text">
              {stats.totalWeightLossKg.toFixed(1)} kg
            </p>
          </div>
          <div className="rounded-xl border border-mq-gold/30 bg-mq-bg px-4 py-2">
            <div className="flex items-center gap-1.5 text-mq-gold">
              <Activity className="h-3.5 w-3.5" />
              <span className="text-xs">Actividad</span>
            </div>
            <p className="mt-1 text-base font-semibold text-mq-text">
              {stats.avgActivityMinutes} min
            </p>
          </div>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={290}>
        <BarChart
          data={chartData}
          margin={{ top: 10, right: 20, left: 0, bottom: 0 }}
        >
          <CartesianGrid
            strokeDasharray="3 3"
            stroke="rgba(88,166,255,0.12)"
            vertical={false}
          />
          <XAxis
            dataKey="day"
            stroke="#a0aec0"
            tick={{ fontSize: 12 }}
            axisLine={false}
            tickLine={false}
          />
          <YAxis
            stroke="#a0aec0"
            tick={{ fontSize: 12 }}
            axisLine={false}
            tickLine={false}
            width={32}
          />
          <Tooltip
            contentStyle={{
              backgroundColor: "#1a1f2e",
              border: "1px solid rgba(88,166,255,0.3)",
              borderRadius: "10px",
              color: "#f0f6fc",
              fontSize: 13,
            }}
            cursor={{ fill: "rgba(88,166,255,0.07)" }}
          />
          <Legend
            wrapperStyle={{ fontSize: 12, color: "#a0aec0", paddingTop: 12 }}
          />
          <Bar
            dataKey="pesoKg"
            name="Pérdida de Peso (kg)"
            fill="#58a6ff"
            radius={[6, 6, 0, 0]}
          />
          <Bar
            dataKey="actividad"
            name="Actividad Física (min)"
            fill="#e0b35e"
            radius={[6, 6, 0, 0]}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

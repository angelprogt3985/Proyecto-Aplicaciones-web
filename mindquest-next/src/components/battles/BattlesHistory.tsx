"use client";

import { useState } from "react";
import { Filter, Droplet, Users, Brain, CalendarDays } from "lucide-react";
import type {
  BattleRecord,
  HabitFilter,
  DateFilter,
} from "@/lib/types";
import { formatDate } from "@/lib/utils";
import { cn } from "@/lib/utils";

const HABIT_STYLES = {
  Agua:    { bg: "bg-[#1e3a5f]", text: "text-mq-blue",   border: "border-mq-blue/40",   icon: Droplet },
  Postura: { bg: "bg-[#3d3020]", text: "text-mq-gold",   border: "border-mq-gold/40",   icon: Users   },
  Mente:   { bg: "bg-[#3d2d5f]", text: "text-mq-purple", border: "border-mq-purple/40", icon: Brain   },
} as const;

const DATE_OPTIONS = [
  { value: "all",        label: "Todas las fechas" },
  { value: "2026-02-22", label: "22 Feb 2026"       },
  { value: "2026-02-21", label: "21 Feb 2026"       },
  { value: "2026-02-20", label: "20 Feb 2026"       },
  { value: "2026-02-19", label: "19 Feb 2026"       },
];

interface BattlesHistoryProps {
  battles: BattleRecord[];
}

export function BattlesHistory({ battles }: BattlesHistoryProps) {
  const [habitFilter, setHabitFilter] = useState<HabitFilter>("Todos");
  const [dateFilter, setDateFilter] = useState<DateFilter>("all");

  const filtered = battles.filter((b) => {
    const habitOk = habitFilter === "Todos" || b.habitType === habitFilter;
    const dateOk = dateFilter === "all" || b.date === dateFilter;
    return habitOk && dateOk;
  });

  const totalGold = filtered.reduce((s, b) => s + b.goldEarned, 0);
  const totalXP   = filtered.reduce((s, b) => s + b.xpEarned, 0);
  const victories = filtered.filter((b) => b.result === "Victoria").length;

  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-6 shadow-lg">
      <div className="pointer-events-none absolute right-0 top-0 h-52 w-52 rounded-full bg-mq-blue opacity-08 blur-[110px]" />

      <div className="relative mb-5">
        <h2 className="flex items-center gap-2 text-lg font-semibold text-mq-text">
          <Filter className="h-5 w-5 text-mq-blue" />
          Historial de Combates – Wellness Dashboard
        </h2>
        <p className="mt-0.5 text-sm text-mq-muted">Filtra por fecha y tipo de hábito</p>
      </div>

      {/* Filters */}
      <div className="relative mb-5 grid grid-cols-2 gap-4">
        <div>
          <label className="mb-2 flex items-center gap-1.5 text-xs text-mq-muted">
            <CalendarDays className="h-3.5 w-3.5" />
            Filtrar por Fecha
          </label>
          <select
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
            className="w-full rounded-xl border border-mq-blue/25 bg-mq-bg px-4 py-2.5 text-sm text-mq-text outline-none transition-colors focus:border-mq-blue"
          >
            {DATE_OPTIONS.map((o) => (
              <option key={o.value} value={o.value}>{o.label}</option>
            ))}
          </select>
        </div>

        <div>
          <label className="mb-2 block text-xs text-mq-muted">
            Filtrar por Tipo de Hábito
          </label>
          <div className="flex gap-2">
            {(["Todos", "Agua", "Postura", "Mente"] as HabitFilter[]).map((h) => (
              <button
                key={h}
                onClick={() => setHabitFilter(h)}
                className={cn(
                  "flex-1 rounded-xl border px-3 py-2 text-xs font-medium transition-all",
                  habitFilter === h
                    ? "border-mq-blue bg-mq-blue text-mq-bg shadow-md shadow-mq-blue/25"
                    : "border-mq-blue/25 bg-mq-bg text-mq-muted hover:border-mq-blue/40 hover:text-mq-text"
                )}
              >
                {h}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* Summary cards */}
      <div className="relative mb-5 grid grid-cols-4 gap-3">
        {[
          { label: "Combates",  value: filtered.length, cls: "text-mq-text"   },
          { label: "Victorias", value: victories,        cls: "text-mq-blue"   },
          { label: "Oro Total", value: totalGold,        cls: "text-mq-gold"   },
          { label: "XP Total",  value: totalXP,          cls: "text-mq-teal"   },
        ].map((s) => (
          <div
            key={s.label}
            className="rounded-xl border border-mq-blue/20 bg-mq-bg p-4 text-center"
          >
            <p className={cn("text-2xl font-bold", s.cls)}>
              {s.value.toLocaleString()}
            </p>
            <p className="mt-1 text-xs text-mq-muted">{s.label}</p>
          </div>
        ))}
      </div>

      {/* Table */}
      <div className="relative overflow-hidden rounded-xl border border-mq-blue/20">
        <table className="w-full border-collapse">
          <thead className="border-b border-mq-blue/20 bg-mq-bg text-[11px] uppercase tracking-wider text-mq-muted">
            <tr>
              <th className="px-4 py-3 text-left">Fecha</th>
              <th className="px-4 py-3 text-left">Tipo de Hábito</th>
              <th className="px-4 py-3 text-left">Resultado</th>
              <th className="px-4 py-3 text-left">Oro Ganado</th>
              <th className="px-4 py-3 text-left">XP Ganado</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-mq-blue/10">
            {filtered.map((b) => {
              const habit = HABIT_STYLES[b.habitType];
              const HabitIcon = habit.icon;
              return (
                <tr key={b.id} className="transition-colors hover:bg-mq-bg">
                  <td className="px-4 py-3.5 text-sm text-mq-text">
                    {formatDate(b.date)}
                  </td>
                  <td className="px-4 py-3.5">
                    <span
                      className={cn(
                        "inline-flex items-center gap-1.5 rounded-full border px-3 py-0.5 text-xs",
                        habit.bg, habit.text, habit.border
                      )}
                    >
                      <HabitIcon className="h-3 w-3" />
                      {b.habitType}
                    </span>
                  </td>
                  <td className="px-4 py-3.5">
                    <span
                      className={cn(
                        "rounded-full border px-3 py-0.5 text-xs",
                        b.result === "Victoria"
                          ? "border-mq-blue/40 bg-[#1e3a5f] text-mq-blue"
                          : "border-mq-red/40 bg-[#4a2830] text-mq-red"
                      )}
                    >
                      {b.result}
                    </span>
                  </td>
                  <td className="px-4 py-3.5">
                    <span className="flex items-center gap-1 text-sm text-mq-gold">
                      🪙 +{b.goldEarned}
                    </span>
                  </td>
                  <td className="px-4 py-3.5 text-sm text-mq-teal">
                    +{b.xpEarned} XP
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

"use client";
import { Filter, Droplet, Users, Brain, Calendar } from 'lucide-react';
import { useState } from 'react';

type HabitType = 'Agua' | 'Postura' | 'Mente';
type HabitFilter = 'Todos' | HabitType;

const battleData = [
  { id: 1, date: '2026-02-22', habitType: 'Agua' as HabitType, result: 'Victoria', goldEarned: 150, xpEarned: 200 },
  { id: 2, date: '2026-02-22', habitType: 'Postura' as HabitType, result: 'Victoria', goldEarned: 120, xpEarned: 180 },
  { id: 3, date: '2026-02-21', habitType: 'Mente' as HabitType, result: 'Victoria', goldEarned: 180, xpEarned: 250 },
  { id: 4, date: '2026-02-21', habitType: 'Agua' as HabitType, result: 'Derrota', goldEarned: 50, xpEarned: 75 },
  { id: 5, date: '2026-02-20', habitType: 'Postura' as HabitType, result: 'Victoria', goldEarned: 140, xpEarned: 190 },
  { id: 6, date: '2026-02-20', habitType: 'Mente' as HabitType, result: 'Victoria', goldEarned: 200, xpEarned: 280 },
  { id: 7, date: '2026-02-19', habitType: 'Agua' as HabitType, result: 'Victoria', goldEarned: 160, xpEarned: 210 },
  { id: 8, date: '2026-02-19', habitType: 'Postura' as HabitType, result: 'Derrota', goldEarned: 40, xpEarned: 60 },
];

const habitIcons = { Agua: Droplet, Postura: Users, Mente: Brain };
const habitColors = {
  Agua: { bg: 'bg-[#1e3a5f]', text: 'text-[#58a6ff]', border: 'border-[rgba(88,166,255,0.4)]' },
  Postura: { bg: 'bg-[#3d3020]', text: 'text-[#e0b35e]', border: 'border-[rgba(224,179,94,0.4)]' },
  Mente: { bg: 'bg-[#3d2d5f]', text: 'text-[#b8a3e0]', border: 'border-[rgba(184,163,224,0.4)]' },
};

export function WellnessDashboard() {
  const [selectedHabit, setSelectedHabit] = useState<HabitFilter>('Todos');
  const [selectedDate, setSelectedDate] = useState('all');

  const filteredBattles = battleData.filter(b => {
    const habitMatch = selectedHabit === 'Todos' || b.habitType === selectedHabit;
    const dateMatch = selectedDate === 'all' || b.date === selectedDate;
    return habitMatch && dateMatch;
  });

  const stats = {
    total: filteredBattles.length,
    victories: filteredBattles.filter(b => b.result === 'Victoria').length,
    totalGold: filteredBattles.reduce((acc, b) => acc + b.goldEarned, 0),
    totalXP: filteredBattles.reduce((acc, b) => acc + b.xpEarned, 0),
  };

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
  };

  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(88,166,255,0.25)] p-6 shadow-lg relative overflow-hidden">
      <div className="absolute top-0 right-0 w-64 h-64 bg-[#58a6ff] rounded-full blur-[120px] opacity-15 pointer-events-none" />
      
      <div className="relative">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl font-medium text-[#f0f6fc] flex items-center gap-2">
              <Filter className="w-6 h-6 text-[#58a6ff]" />
              Historial de Combates - Wellness Dashboard
            </h2>
            <p className="text-sm text-[#a0aec0] mt-1">Filtra por fecha y tipo de hábito</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
          <div>
            <label className="text-sm text-[#a0aec0] mb-2 flex items-center gap-2">
              <Calendar className="w-4 h-4" />
              Filtrar por Fecha
            </label>
            <select
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="w-full bg-[#1a1f2e] border border-[rgba(88,166,255,0.25)] rounded-lg px-4 py-2.5 text-[#f0f6fc] focus:outline-none focus:ring-2 focus:ring-[#58a6ff] text-sm"
            >
              <option value="all">Todas las fechas</option>
              <option value="2026-02-22">22 Feb 2026</option>
              <option value="2026-02-21">21 Feb 2026</option>
              <option value="2026-02-20">20 Feb 2026</option>
              <option value="2026-02-19">19 Feb 2026</option>
            </select>
          </div>
          <div>
            <label className="text-sm text-[#a0aec0] mb-2 block">Filtrar por Tipo de Hábito</label>
            <div className="flex gap-2">
              {(['Todos', 'Agua', 'Postura', 'Mente'] as HabitFilter[]).map((habit) => (
                <button
                  key={habit}
                  onClick={() => setSelectedHabit(habit)}
                  className={`flex-1 px-3 py-2.5 rounded-lg text-sm transition-all ${
                    selectedHabit === habit
                      ? 'bg-[#58a6ff] text-[#1a1f2e] shadow-lg shadow-[#58a6ff]/30 font-medium'
                      : 'bg-[#1a1f2e] text-[#a0aec0] border border-[rgba(88,166,255,0.25)] hover:border-[rgba(88,166,255,0.4)]'
                  }`}
                >
                  {habit}
                </button>
              ))}
            </div>
          </div>
        </div>

        <div className="grid grid-cols-4 gap-4 mb-6">
          <div className="bg-[#1a1f2e] border border-[rgba(88,166,255,0.2)] rounded-lg p-4 text-center">
            <p className="text-2xl font-medium text-[#f0f6fc]">{stats.total}</p>
            <p className="text-xs text-[#a0aec0] mt-1">Combates</p>
          </div>
          <div className="bg-[#1a1f2e] border border-[rgba(88,166,255,0.2)] rounded-lg p-4 text-center">
            <p className="text-2xl font-medium text-[#58a6ff]">{stats.victories}</p>
            <p className="text-xs text-[#a0aec0] mt-1">Victorias</p>
          </div>
          <div className="bg-[#1a1f2e] border border-[rgba(224,179,94,0.3)] rounded-lg p-4 text-center">
            <p className="text-2xl font-medium text-[#e0b35e]">{stats.totalGold}</p>
            <p className="text-xs text-[#a0aec0] mt-1">Oro Total</p>
          </div>
          <div className="bg-[#1a1f2e] border border-[rgba(88,166,255,0.2)] rounded-lg p-4 text-center">
            <p className="text-2xl font-medium text-[#79c0ff]">{stats.totalXP}</p>
            <p className="text-xs text-[#a0aec0] mt-1">XP Total</p>
          </div>
        </div>

        <div className="overflow-hidden rounded-lg border border-[rgba(88,166,255,0.2)]">
          <table className="w-full">
            <thead className="bg-[#1a1f2e] border-b border-[rgba(88,166,255,0.2)]">
              <tr>
                <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Fecha</th>
                <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Tipo de Hábito</th>
                <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Resultado</th>
                <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Oro Ganado</th>
                <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">XP Ganado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[rgba(88,166,255,0.15)]">
              {filteredBattles.map((battle) => {
                const Icon = habitIcons[battle.habitType];
                const colors = habitColors[battle.habitType];
                return (
                  <tr key={battle.id} className="hover:bg-[#1a1f2e] transition-colors">
                    <td className="px-4 py-4 text-sm text-[#f0f6fc]">{formatDate(battle.date)}</td>
                    <td className="px-4 py-4">
                      <span className={`inline-flex items-center gap-2 px-3 py-1 ${colors.bg} ${colors.text} text-xs rounded-full border ${colors.border}`}>
                        <Icon className="w-3 h-3" />
                        {battle.habitType}
                      </span>
                    </td>
                    <td className="px-4 py-4">
                      <span className={`px-3 py-1 text-xs rounded-full ${
                        battle.result === 'Victoria'
                          ? 'bg-[#1e3a5f] text-[#58a6ff] border border-[rgba(88,166,255,0.4)]'
                          : 'bg-[#4a2830] text-[#ff6b6b] border border-[rgba(255,107,107,0.4)]'
                      }`}>
                        {battle.result}
                      </span>
                    </td>
                    <td className="px-4 py-4">
                      <span className="text-[#e0b35e] text-sm flex items-center gap-1">
                        <span>🪙</span>+{battle.goldEarned}
                      </span>
                    </td>
                    <td className="px-4 py-4">
                      <span className="text-[#79c0ff] text-sm">+{battle.xpEarned} XP</span>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

"use client";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { TrendingDown, Activity } from 'lucide-react';

const weeklyData = [
  { day: 'Lun', pesoKg: 2.5, actividad: 45 },
  { day: 'Mar', pesoKg: 2.2, actividad: 60 },
  { day: 'Mié', pesoKg: 2.8, actividad: 75 },
  { day: 'Jue', pesoKg: 2.0, actividad: 50 },
  { day: 'Vie', pesoKg: 3.1, actividad: 80 },
  { day: 'Sáb', pesoKg: 2.7, actividad: 65 },
  { day: 'Dom', pesoKg: 2.4, actividad: 55 },
];

export function VitalityStats() {
  const totalWeightLoss = weeklyData.reduce((acc, curr) => acc + curr.pesoKg, 0);
  const avgActivity = Math.round(weeklyData.reduce((acc, curr) => acc + curr.actividad, 0) / weeklyData.length);

  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(88,166,255,0.25)] p-6 shadow-lg relative overflow-hidden">
      <div className="absolute top-0 right-0 w-48 h-48 bg-[#58a6ff] rounded-full blur-[100px] opacity-20 pointer-events-none" />
      
      <div className="relative flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-medium text-[#f0f6fc]">Estadísticas Semanales de Vitalidad</h2>
          <p className="text-sm text-[#a0aec0] mt-1">Progreso de esta semana</p>
        </div>
        <div className="flex gap-3">
          <div className="bg-[#1a1f2e] border border-[rgba(88,166,255,0.3)] rounded-lg px-4 py-2">
            <div className="flex items-center gap-2 text-[#58a6ff]">
              <TrendingDown className="w-4 h-4" />
              <span className="text-xs">Peso</span>
            </div>
            <p className="text-lg font-medium text-[#f0f6fc] mt-1">{totalWeightLoss.toFixed(1)} kg</p>
          </div>
          <div className="bg-[#1a1f2e] border border-[rgba(224,179,94,0.3)] rounded-lg px-4 py-2">
            <div className="flex items-center gap-2 text-[#e0b35e]">
              <Activity className="w-4 h-4" />
              <span className="text-xs">Actividad</span>
            </div>
            <p className="text-lg font-medium text-[#f0f6fc] mt-1">{avgActivity} min</p>
          </div>
        </div>
      </div>

      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={weeklyData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="rgba(88,166,255,0.15)" />
          <XAxis dataKey="day" stroke="#a0aec0" style={{ fontSize: '12px' }} />
          <YAxis stroke="#a0aec0" style={{ fontSize: '12px' }} />
          <Tooltip
            contentStyle={{
              backgroundColor: '#1a1f2e',
              border: '1px solid rgba(88,166,255,0.3)',
              borderRadius: '8px',
              color: '#f0f6fc',
            }}
          />
          <Legend wrapperStyle={{ fontSize: '12px', color: '#a0aec0' }} />
          <Bar dataKey="pesoKg" fill="#58a6ff" name="Pérdida de Peso (kg)" radius={[8, 8, 0, 0]} />
          <Bar dataKey="actividad" fill="#e0b35e" name="Actividad Física (min)" radius={[8, 8, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

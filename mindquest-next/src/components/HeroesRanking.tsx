"use client";
import { Crown, Medal, Award, Trophy } from 'lucide-react';

const heroesData = [
  { rank: 1, name: 'DragonSlayer42', level: 89, gold: 45230, avatar: '🛡️', class: 'Guerrero' },
  { rank: 2, name: 'MysticHealer', level: 87, gold: 42100, avatar: '⚕️', class: 'Sanador' },
  { rank: 3, name: 'ShadowNinja', level: 85, gold: 39800, avatar: '🥷', class: 'Asesino' },
  { rank: 4, name: 'FireMage', level: 82, gold: 36500, avatar: '🔥', class: 'Mago' },
  { rank: 5, name: 'IronKnight', level: 80, gold: 34200, avatar: '⚔️', class: 'Caballero' },
  { rank: 6, name: 'StormArcher', level: 78, gold: 31900, avatar: '🏹', class: 'Arquero' },
  { rank: 7, name: 'NatureWarden', level: 75, gold: 29600, avatar: '🌿', class: 'Druida' },
  { rank: 8, name: 'LightPaladin', level: 73, gold: 27300, avatar: '✨', class: 'Paladín' },
];

const getRankIcon = (rank: number) => {
  if (rank === 1) return <Crown className="w-5 h-5 text-[#e0b35e]" />;
  if (rank === 2) return <Medal className="w-5 h-5 text-[#a0aec0]" />;
  if (rank === 3) return <Award className="w-5 h-5 text-[#cd9575]" />;
  return <span className="text-[#a0aec0] text-sm">#{rank}</span>;
};

export function HeroesRanking() {
  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(88,166,255,0.25)] p-6 shadow-lg relative overflow-hidden">
      <div className="absolute bottom-0 left-0 w-48 h-48 bg-[#e0b35e] rounded-full blur-[100px] opacity-20 pointer-events-none" />
      
      <div className="relative flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-medium text-[#f0f6fc] flex items-center gap-2">
            <Trophy className="w-6 h-6 text-[#e0b35e]" />
            Ranking Global de Héroes
          </h2>
          <p className="text-sm text-[#a0aec0] mt-1">Los mejores aventureros de la semana</p>
        </div>
        <button className="px-4 py-2 bg-[#58a6ff] hover:bg-[#6eb5ff] text-[#1a1f2e] rounded-lg text-sm transition-colors shadow-lg shadow-[#58a6ff]/30 font-medium">
          Ver Todos
        </button>
      </div>

      <div className="relative overflow-hidden rounded-lg border border-[rgba(88,166,255,0.2)]">
        <table className="w-full">
          <thead className="bg-[#1a1f2e] border-b border-[rgba(88,166,255,0.2)]">
            <tr>
              <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Rank</th>
              <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Héroe</th>
              <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Clase</th>
              <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Nivel</th>
              <th className="px-4 py-3 text-left text-xs text-[#a0aec0] uppercase tracking-wider">Oro</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[rgba(88,166,255,0.15)]">
            {heroesData.map((hero) => (
              <tr key={hero.rank} className="hover:bg-[#1a1f2e] transition-colors cursor-pointer">
                <td className="px-4 py-4">
                  <div className="flex items-center justify-center w-8">{getRankIcon(hero.rank)}</div>
                </td>
                <td className="px-4 py-4">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-gradient-to-br from-[#58a6ff] to-[#6eb5ff] flex items-center justify-center text-xl shadow-md shadow-[#58a6ff]/30">
                      {hero.avatar}
                    </div>
                    <span className="text-[#f0f6fc] text-sm">{hero.name}</span>
                  </div>
                </td>
                <td className="px-4 py-4">
                  <span className="px-3 py-1 bg-[#2d3548] text-[#58a6ff] text-xs rounded-full border border-[rgba(88,166,255,0.3)]">
                    {hero.class}
                  </span>
                </td>
                <td className="px-4 py-4">
                  <div className="flex items-center gap-2">
                    <div className="w-full max-w-[80px] bg-[#1a1f2e] rounded-full h-2 overflow-hidden">
                      <div
                        className="h-full bg-gradient-to-r from-[#58a6ff] to-[#e0b35e]"
                        style={{ width: `${hero.level}%` }}
                      />
                    </div>
                    <span className="text-[#f0f6fc] text-sm min-w-[30px]">{hero.level}</span>
                  </div>
                </td>
                <td className="px-4 py-4">
                  <div className="flex items-center gap-2 text-[#e0b35e]">
                    <span>🪙</span>
                    <span className="text-[#f0f6fc] text-sm">{hero.gold.toLocaleString()}</span>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

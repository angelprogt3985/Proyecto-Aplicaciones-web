"use client";
import { Sword, Shield, Heart, Zap, Star, Lock } from 'lucide-react';

type Rarity = 'common' | 'rare' | 'epic' | 'legendary';

const equipmentData = [
  { id: 1, name: 'Espada del Alba', type: 'weapon', rarity: 'legendary' as Rarity, icon: Sword, stats: { power: 95 } },
  { id: 2, name: 'Escudo de Titán', type: 'armor', rarity: 'epic' as Rarity, icon: Shield, stats: { defense: 85 } },
  { id: 3, name: 'Espada de Hierro', type: 'weapon', rarity: 'rare' as Rarity, icon: Sword, stats: { power: 65 } },
  { id: 4, name: 'Escudo Guardián', type: 'armor', rarity: 'rare' as Rarity, icon: Shield, stats: { defense: 60 } },
  { id: 5, name: 'Amuleto de Vida', type: 'accessory', rarity: 'epic' as Rarity, icon: Heart, stats: { health: 150 } },
  { id: 6, name: 'Reliquia del Trueno', type: 'accessory', rarity: 'legendary' as Rarity, icon: Zap, stats: { power: 80 } },
  { id: 7, name: 'Espada Mística', type: 'weapon', rarity: 'common' as Rarity, icon: Sword, stats: { power: 45 }, locked: true },
  { id: 8, name: 'Escudo Celestial', type: 'armor', rarity: 'legendary' as Rarity, icon: Shield, stats: { defense: 100 }, locked: true },
];

const rarityColors: Record<Rarity, { bg: string; border: string; text: string }> = {
  common: { bg: 'bg-[#2d3548]', border: 'border-[#4a5568]', text: 'text-[#a0aec0]' },
  rare: { bg: 'bg-[#1e3a5f]', border: 'border-[rgba(88,166,255,0.4)]', text: 'text-[#58a6ff]' },
  epic: { bg: 'bg-[#3d2d5f]', border: 'border-[rgba(157,138,199,0.4)]', text: 'text-[#b8a3e0]' },
  legendary: { bg: 'bg-[#3d3020]', border: 'border-[rgba(224,179,94,0.4)]', text: 'text-[#e0b35e]' },
};

const starCount: Record<Rarity, number> = { legendary: 5, epic: 4, rare: 3, common: 2 };

export function EquipmentInventory() {
  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(88,166,255,0.25)] p-6 shadow-lg relative overflow-hidden">
      <div className="absolute top-0 right-1/4 w-32 h-32 bg-[#58a6ff] rounded-full blur-[100px] opacity-15 pointer-events-none" />
      <div className="absolute bottom-0 left-1/4 w-32 h-32 bg-[#e0b35e] rounded-full blur-[100px] opacity-15 pointer-events-none" />
      
      <div className="relative flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-medium text-[#f0f6fc] flex items-center gap-2">
            <Sword className="w-6 h-6 text-[#58a6ff]" />
            Inventario de Equipo
          </h2>
          <p className="text-sm text-[#a0aec0] mt-1">Tu arsenal de batalla</p>
        </div>
        <div className="flex items-center gap-2 text-sm text-[#a0aec0]">
          <Star className="w-4 h-4 text-[#e0b35e]" />
          <span>8/12 Equipos</span>
        </div>
      </div>

      <div className="relative grid grid-cols-4 gap-4">
        {equipmentData.map((item) => {
          const Icon = item.icon;
          const rarity = rarityColors[item.rarity];
          return (
            <div
              key={item.id}
              className={`relative ${rarity.bg} border ${rarity.border} rounded-xl p-4 hover:scale-105 transition-all cursor-pointer shadow-lg ${item.locked ? 'opacity-60' : ''}`}
            >
              {item.locked && (
                <div className="absolute top-2 right-2 bg-[#1a1f2e] rounded-full p-1">
                  <Lock className="w-3 h-3 text-[#a0aec0]" />
                </div>
              )}
              <div className="flex flex-col items-center">
                <div className={`w-16 h-16 ${rarity.bg} rounded-full flex items-center justify-center mb-3 border ${rarity.border} shadow-inner`}>
                  <Icon className={`w-8 h-8 ${rarity.text}`} />
                </div>
                <h3 className={`text-sm text-center text-[#f0f6fc] mb-2 ${item.locked ? 'blur-sm' : ''}`}>{item.name}</h3>
                <div className="flex gap-1 mb-2">
                  {Array.from({ length: starCount[item.rarity] }).map((_, i) => (
                    <Star key={i} className={`w-3 h-3 ${rarity.text} fill-current`} />
                  ))}
                </div>
                <div className="w-full space-y-1">
                  {'power' in item.stats && item.stats.power && (
                    <div className="flex justify-between items-center text-xs">
                      <span className="text-[#a0aec0]">Poder</span>
                      <span className="text-[#ff6b6b]">+{item.stats.power}</span>
                    </div>
                  )}
                  {'defense' in item.stats && item.stats.defense && (
                    <div className="flex justify-between items-center text-xs">
                      <span className="text-[#a0aec0]">Defensa</span>
                      <span className="text-[#58a6ff]">+{item.stats.defense}</span>
                    </div>
                  )}
                  {'health' in item.stats && item.stats.health && (
                    <div className="flex justify-between items-center text-xs">
                      <span className="text-[#a0aec0]">Vida</span>
                      <span className="text-[#79c0ff]">+{item.stats.health}</span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="relative mt-6 flex items-center justify-between p-4 bg-[#1a1f2e] rounded-lg border border-[rgba(88,166,255,0.2)]">
        <div className="flex items-center gap-3">
          <div className="flex -space-x-2">
            <div className="w-8 h-8 bg-gradient-to-br from-[#e0b35e] to-[#d4a855] rounded-full border-2 border-[#1a1f2e] flex items-center justify-center">
              <Star className="w-4 h-4 text-[#1a1f2e]" />
            </div>
            <div className="w-8 h-8 bg-gradient-to-br from-[#9d8ac7] to-[#b8a3e0] rounded-full border-2 border-[#1a1f2e] flex items-center justify-center">
              <Star className="w-4 h-4 text-white" />
            </div>
            <div className="w-8 h-8 bg-gradient-to-br from-[#58a6ff] to-[#6eb5ff] rounded-full border-2 border-[#1a1f2e] flex items-center justify-center">
              <Star className="w-4 h-4 text-white" />
            </div>
          </div>
          <div>
            <p className="text-sm text-[#f0f6fc]">Equipo Legendario Completo</p>
            <p className="text-xs text-[#a0aec0]">Bonus: +25% a todas las estadísticas</p>
          </div>
        </div>
        <button className="px-4 py-2 bg-[#58a6ff] hover:bg-[#6eb5ff] text-[#1a1f2e] rounded-lg text-sm transition-colors shadow-lg shadow-[#58a6ff]/30 font-medium">
          Equipar Set
        </button>
      </div>
    </div>
  );
}

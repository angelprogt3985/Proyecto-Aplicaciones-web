"use client";
import { ShoppingCart, Star, Sword, Shield, Heart, Zap, TrendingUp, Award } from 'lucide-react';
import { useState } from 'react';

type Category = 'all' | 'weapon' | 'armor' | 'accessory' | 'boost';
type Rarity = 'common' | 'rare' | 'epic' | 'legendary';

const shopItems = [
  { id: 1, name: 'Espada Estelar', description: 'Una espada forjada con fragmentos de estrellas', price: 2500, icon: Sword, category: 'weapon' as Category, rarity: 'legendary' as Rarity, stats: { power: 120 } },
  { id: 2, name: 'Escudo Galáctico', description: 'Protección digna de un héroe espacial', price: 2200, icon: Shield, category: 'armor' as Category, rarity: 'epic' as Rarity, stats: { defense: 100 } },
  { id: 3, name: 'Amuleto de Energía', description: 'Aumenta tu vitalidad considerablemente', price: 1800, icon: Heart, category: 'accessory' as Category, rarity: 'epic' as Rarity, stats: { health: 200 } },
  { id: 4, name: 'Reliquia del Poder', description: 'Potencia tus ataques con energía cósmica', price: 3000, icon: Zap, category: 'accessory' as Category, rarity: 'legendary' as Rarity, stats: { power: 90 } },
  { id: 5, name: 'Espada de Batalla', description: 'Un arma confiable para cualquier aventurero', price: 800, icon: Sword, category: 'weapon' as Category, rarity: 'rare' as Rarity, stats: { power: 70 } },
  { id: 6, name: 'Escudo Reforzado', description: 'Defensa sólida contra enemigos', price: 750, icon: Shield, category: 'armor' as Category, rarity: 'rare' as Rarity, stats: { defense: 65 } },
  { id: 7, name: 'Boost de XP', description: 'Duplica XP ganado por 24 horas', price: 500, icon: TrendingUp, category: 'boost' as Category, rarity: 'rare' as Rarity, stats: {} },
  { id: 8, name: 'Boost de Oro', description: 'Aumenta oro ganado en un 50% por 24 horas', price: 450, icon: Award, category: 'boost' as Category, rarity: 'rare' as Rarity, stats: {} },
];

const rarityColors: Record<Rarity, { text: string; border: string; bg: string }> = {
  common: { text: 'text-[#a0aec0]', border: 'border-[#4a5568]', bg: 'bg-[#2d3548]' },
  rare: { text: 'text-[#58a6ff]', border: 'border-[rgba(88,166,255,0.4)]', bg: 'bg-[#1e3a5f]' },
  epic: { text: 'text-[#b8a3e0]', border: 'border-[rgba(184,163,224,0.4)]', bg: 'bg-[#3d2d5f]' },
  legendary: { text: 'text-[#e0b35e]', border: 'border-[rgba(224,179,94,0.4)]', bg: 'bg-[#3d3020]' },
};

const starCount: Record<Rarity, number> = { legendary: 5, epic: 4, rare: 3, common: 2 };

const categories = [
  { id: 'all' as Category, label: 'Todo', icon: Star },
  { id: 'weapon' as Category, label: 'Armas', icon: Sword },
  { id: 'armor' as Category, label: 'Armaduras', icon: Shield },
  { id: 'accessory' as Category, label: 'Accesorios', icon: Heart },
  { id: 'boost' as Category, label: 'Potenciadores', icon: TrendingUp },
];

export function GoldShop() {
  const [selectedCategory, setSelectedCategory] = useState<Category>('all');
  const userGold = 1250;

  const filteredItems = shopItems.filter(item => selectedCategory === 'all' || item.category === selectedCategory);

  return (
    <div className="bg-[#242b3d] rounded-xl border border-[rgba(224,179,94,0.3)] p-6 shadow-lg relative overflow-hidden">
      <div className="absolute bottom-0 left-1/3 w-64 h-64 bg-[#e0b35e] rounded-full blur-[120px] opacity-20 pointer-events-none" />
      
      <div className="relative">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-xl font-medium text-[#f0f6fc] flex items-center gap-2">
              <ShoppingCart className="w-6 h-6 text-[#e0b35e]" />
              Tienda de Oro & Equipo
            </h2>
            <p className="text-sm text-[#a0aec0] mt-1">Mejora las estadísticas de tu héroe</p>
          </div>
          <div className="flex items-center gap-2 px-4 py-2 bg-[#1a1f2e] border border-[rgba(224,179,94,0.3)] rounded-lg">
            <span className="text-[#e0b35e]">🪙</span>
            <span className="text-[#f0f6fc] font-medium">{userGold}</span>
            <span className="text-xs text-[#a0aec0]">Oro Disponible</span>
          </div>
        </div>

        <div className="flex gap-2 mb-6">
          {categories.map((cat) => {
            const Icon = cat.icon;
            return (
              <button
                key={cat.id}
                onClick={() => setSelectedCategory(cat.id)}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all ${
                  selectedCategory === cat.id
                    ? 'bg-[#e0b35e] text-[#1a1f2e] shadow-lg shadow-[#e0b35e]/30 font-medium'
                    : 'bg-[#1a1f2e] text-[#a0aec0] border border-[rgba(88,166,255,0.25)] hover:border-[rgba(224,179,94,0.4)]'
                }`}
              >
                <Icon className="w-4 h-4" />
                {cat.label}
              </button>
            );
          })}
        </div>

        <div className="grid grid-cols-4 gap-4">
          {filteredItems.map((item) => {
            const Icon = item.icon;
            const colors = rarityColors[item.rarity];
            const canAfford = userGold >= item.price;
            return (
              <div
                key={item.id}
                className={`${colors.bg} border ${colors.border} rounded-xl p-4 hover:scale-105 transition-all cursor-pointer shadow-lg flex flex-col ${!canAfford ? 'opacity-50' : ''}`}
              >
                <div className={`w-16 h-16 mx-auto ${colors.bg} rounded-full flex items-center justify-center mb-3 border ${colors.border} shadow-inner`}>
                  <Icon className={`w-8 h-8 ${colors.text}`} />
                </div>
                <h3 className="text-sm text-[#f0f6fc] text-center mb-1 font-medium">{item.name}</h3>
                <div className="flex justify-center gap-1 mb-2">
                  {Array.from({ length: starCount[item.rarity] }).map((_, i) => (
                    <Star key={i} className={`w-3 h-3 ${colors.text} fill-current`} />
                  ))}
                </div>
                <p className="text-xs text-[#a0aec0] text-center mb-3 flex-grow">{item.description}</p>
                {Object.keys(item.stats).length > 0 && (
                  <div className="space-y-1 mb-3">
                    {'power' in item.stats && item.stats.power && (
                      <div className="flex justify-between text-xs">
                        <span className="text-[#a0aec0]">Poder</span>
                        <span className="text-[#ff6b6b]">+{item.stats.power}</span>
                      </div>
                    )}
                    {'defense' in item.stats && item.stats.defense && (
                      <div className="flex justify-between text-xs">
                        <span className="text-[#a0aec0]">Defensa</span>
                        <span className="text-[#58a6ff]">+{item.stats.defense}</span>
                      </div>
                    )}
                    {'health' in item.stats && item.stats.health && (
                      <div className="flex justify-between text-xs">
                        <span className="text-[#a0aec0]">Vida</span>
                        <span className="text-[#79c0ff]">+{item.stats.health}</span>
                      </div>
                    )}
                  </div>
                )}
                <div className="mt-auto">
                  <div className="flex items-center justify-center gap-1 mb-2 text-[#e0b35e]">
                    <span>🪙</span>
                    <span className="text-sm">{item.price}</span>
                  </div>
                  <button
                    disabled={!canAfford}
                    className={`w-full py-2 rounded-lg text-sm transition-all font-medium ${
                      canAfford
                        ? 'bg-[#58a6ff] hover:bg-[#6eb5ff] text-[#1a1f2e] shadow-sm'
                        : 'bg-[#2d3548] text-[#a0aec0] cursor-not-allowed'
                    }`}
                  >
                    {canAfford ? 'Comprar' : 'Sin Oro'}
                  </button>
                </div>
              </div>
            );
          })}
        </div>

        <div className="mt-6 p-4 bg-[#1a1f2e] rounded-lg border border-[rgba(88,166,255,0.2)]">
          <p className="text-sm text-[#a0aec0] text-center">
            💡 El oro se gana completando combates y mejorando tus hábitos diarios. Las mejoras de equipo afectan las estadísticas en la base de datos.
          </p>
        </div>
      </div>
    </div>
  );
}

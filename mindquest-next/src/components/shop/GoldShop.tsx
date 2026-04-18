"use client";
import { ShopCategoryFilter, ShopItem } from '@/lib/types';
import { ShoppingCart, Star, Sword, Shield, Heart, Zap, TrendingUp, Award } from 'lucide-react';
import { useState } from 'react';

type Rarity = 'common' | 'rare' | 'epic' | 'legendary';

const rarityColors: Record<Rarity, { text: string; border: string; bg: string }> = {
  common:    { text: 'text-[#a0aec0]', border: 'border-[#4a5568]',                bg: 'bg-[#2d3548]' },
  rare:      { text: 'text-[#58a6ff]', border: 'border-[rgba(88,166,255,0.4)]',   bg: 'bg-[#1e3a5f]' },
  epic:      { text: 'text-[#b8a3e0]', border: 'border-[rgba(184,163,224,0.4)]',  bg: 'bg-[#3d2d5f]' },
  legendary: { text: 'text-[#e0b35e]', border: 'border-[rgba(224,179,94,0.4)]',   bg: 'bg-[#3d3020]' },
};

const starCount: Record<Rarity, number> = { legendary: 5, epic: 4, rare: 3, common: 2 };

const ICON_MAP: Record<string, React.ElementType> = {
  Sword, Shield, Heart, Zap, Star, TrendingUp, Award,
};

const categories = [
  { id: 'all' as ShopCategoryFilter,       label: 'Todo',          icon: Star       },
  { id: 'weapon' as ShopCategoryFilter,    label: 'Armas',         icon: Sword      },
  { id: 'armor' as ShopCategoryFilter,     label: 'Armaduras',     icon: Shield     },
  { id: 'accessory' as ShopCategoryFilter, label: 'Accesorios',    icon: Heart      },
  { id: 'boost' as ShopCategoryFilter,     label: 'Potenciadores', icon: TrendingUp },
];

interface GoldShopProps {
  items:      ShopItem[];
  userGold:   number;
  onPurchase: (item: ShopItem) => Promise<void>;
}

export function GoldShop({ items, userGold, onPurchase }: GoldShopProps) {
  const [category, setCategory]       = useState<ShopCategoryFilter>("all");
  const [purchasingId, setPurchasingId] = useState<string | null>(null);

  const filtered = items.filter(
    (i) => category === "all" || i.category === category
  );

  const handlePurchase = async (item: ShopItem) => {
    if (purchasingId) return;
    setPurchasingId(item.id);
    try {
      await onPurchase(item);
    } finally {
      setPurchasingId(null);
    }
  };

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
            <span className="text-[#f0f6fc] font-medium">{userGold.toLocaleString()}</span>
            <span className="text-xs text-[#a0aec0]">Oro Disponible</span>
          </div>
        </div>

        <div className="flex gap-2 mb-6">
          {categories.map((cat) => {
            const Icon = cat.icon;
            return (
              <button
                key={cat.id}
                onClick={() => setCategory(cat.id)}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all ${
                  category === cat.id
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
          {filtered.map((item) => {
            const Icon = ICON_MAP[item.iconName] ?? Sword;
            const colors = rarityColors[item.rarity];
            const canAfford = userGold >= item.price;
            const isThisOne = purchasingId === item.id;
            const blocked   = !!purchasingId;

            return (
              <div
                key={item.id}
                className={`${colors.bg} border ${colors.border} rounded-xl p-4 transition-all shadow-lg flex flex-col ${!canAfford || blocked ? 'opacity-50' : 'hover:scale-105 cursor-pointer'}`}
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
                {(item.stats.power != null || item.stats.defense != null || item.stats.health != null) && (
                  <div className="space-y-1 mb-3">
                    {item.stats.power != null && (
                      <div className="flex justify-between text-xs">
                        <span className="text-[#a0aec0]">Poder</span>
                        <span className="text-[#ff6b6b]">+{item.stats.power}</span>
                      </div>
                    )}
                    {item.stats.defense != null && (
                      <div className="flex justify-between text-xs">
                        <span className="text-[#a0aec0]">Defensa</span>
                        <span className="text-[#58a6ff]">+{item.stats.defense}</span>
                      </div>
                    )}
                    {item.stats.health != null && (
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
                    <span className="text-sm">{item.price.toLocaleString()}</span>
                  </div>
                  <button
                    disabled={!canAfford || blocked}
                    onClick={() => canAfford && !blocked && handlePurchase(item)}
                    className={`w-full py-2 rounded-lg text-sm transition-all font-medium ${
                      canAfford && !blocked
                        ? 'bg-[#58a6ff] hover:bg-[#6eb5ff] text-[#1a1f2e] shadow-sm'
                        : 'bg-[#2d3548] text-[#a0aec0] cursor-not-allowed'
                    }`}
                  >
                    {isThisOne ? 'Comprando...' : canAfford ? 'Comprar' : 'Sin Oro'}
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
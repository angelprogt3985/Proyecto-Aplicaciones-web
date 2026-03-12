"use client";

import { useState } from "react";
import {
  ShoppingCart,
  Sword,
  Shield,
  Heart,
  Zap,
  Star,
  TrendingUp,
  Award,
} from "lucide-react";
import type { ShopItem, ShopCategoryFilter } from "@/lib/types";
import { RARITY_STYLES } from "@/lib/utils";
import { cn } from "@/lib/utils";

const ICON_MAP: Record<string, React.ElementType> = {
  Sword, Shield, Heart, Zap, Star, TrendingUp, Award,
};

const CATEGORIES: { id: ShopCategoryFilter; label: string; icon: React.ElementType }[] = [
  { id: "all",       label: "Todo",          icon: Star       },
  { id: "weapon",    label: "Armas",         icon: Sword      },
  { id: "armor",     label: "Armaduras",     icon: Shield     },
  { id: "accessory", label: "Accesorios",    icon: Heart      },
  { id: "boost",     label: "Potenciadores", icon: TrendingUp },
];

interface GoldShopProps {
  items: ShopItem[];
  userGold: number;
}

export function GoldShop({ items, userGold }: GoldShopProps) {
  const [category, setCategory] = useState<ShopCategoryFilter>("all");

  const filtered = items.filter(
    (i) => category === "all" || i.category === category
  );

  /**
   * handlePurchase — stub for future API integration.
   * Replace with: POST /api/shop/purchase { itemId, userId }
   */
  const handlePurchase = (item: ShopItem) => {
    console.log("Purchase item:", item.id);
    // TODO: deduct gold, add item to inventory, refresh state
  };

  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-gold/30 bg-mq-card p-6 shadow-lg">
      <div className="pointer-events-none absolute bottom-0 left-1/3 h-52 w-52 rounded-full bg-mq-gold opacity-12 blur-[110px]" />

      {/* Header */}
      <div className="relative mb-5 flex items-center justify-between">
        <div>
          <h2 className="flex items-center gap-2 text-lg font-semibold text-mq-text">
            <ShoppingCart className="h-5 w-5 text-mq-gold" />
            Tienda de Oro & Equipo
          </h2>
          <p className="mt-0.5 text-sm text-mq-muted">
            Mejora las estadísticas de tu héroe
          </p>
        </div>
        <div className="flex items-center gap-2 rounded-xl border border-mq-gold/30 bg-mq-bg px-4 py-2">
          <span>🪙</span>
          <span className="font-semibold text-mq-text">
            {userGold.toLocaleString()}
          </span>
          <span className="text-xs text-mq-muted">Oro Disponible</span>
        </div>
      </div>

      {/* Category tabs */}
      <div className="relative mb-5 flex gap-2">
        {CATEGORIES.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            onClick={() => setCategory(id)}
            className={cn(
              "flex items-center gap-1.5 rounded-xl border px-4 py-2 text-sm font-medium transition-all",
              category === id
                ? "border-mq-gold bg-mq-gold text-mq-bg shadow-md shadow-mq-gold/25"
                : "border-mq-blue/25 bg-mq-bg text-mq-muted hover:border-mq-gold/40 hover:text-mq-text"
            )}
          >
            <Icon className="h-3.5 w-3.5" />
            {label}
          </button>
        ))}
      </div>

      {/* Items grid */}
      <div className="relative grid grid-cols-4 gap-3">
        {filtered.map((item) => {
          const styles = RARITY_STYLES[item.rarity];
          const Icon = ICON_MAP[item.iconName] ?? Sword;
          const canAfford = userGold >= item.price;

          return (
            <div
              key={item.id}
              className={cn(
                "flex flex-col rounded-xl border p-4 transition-transform",
                styles.bg,
                styles.border,
                canAfford
                  ? "cursor-pointer hover:scale-[1.04]"
                  : "opacity-50"
              )}
            >
              {/* Icon */}
              <div
                className={cn(
                  "mx-auto mb-3 flex h-14 w-14 items-center justify-center rounded-full border shadow-inner",
                  styles.bg,
                  styles.border
                )}
              >
                <Icon className={cn("h-7 w-7", styles.text)} />
              </div>

              {/* Name */}
              <p className="mb-1 text-center text-xs font-semibold text-mq-text">
                {item.name}
              </p>

              {/* Stars */}
              <div className="mb-2 flex justify-center gap-0.5">
                {Array.from({ length: styles.stars }).map((_, i) => (
                  <Star key={i} className={cn("h-2.5 w-2.5 fill-current", styles.text)} />
                ))}
              </div>

              {/* Description */}
              <p className="mb-3 flex-1 text-center text-[11px] leading-relaxed text-mq-muted">
                {item.description}
              </p>

              {/* Stats */}
              {(item.stats.power != null || item.stats.defense != null || item.stats.health != null) && (
                <div className="mb-3 space-y-0.5">
                  {item.stats.power != null && (
                    <div className="flex justify-between text-[11px]">
                      <span className="text-mq-muted">Poder</span>
                      <span className="text-mq-red">+{item.stats.power}</span>
                    </div>
                  )}
                  {item.stats.defense != null && (
                    <div className="flex justify-between text-[11px]">
                      <span className="text-mq-muted">Defensa</span>
                      <span className="text-mq-blue">+{item.stats.defense}</span>
                    </div>
                  )}
                  {item.stats.health != null && (
                    <div className="flex justify-between text-[11px]">
                      <span className="text-mq-muted">Vida</span>
                      <span className="text-mq-teal">+{item.stats.health}</span>
                    </div>
                  )}
                </div>
              )}

              {/* Price + buy */}
              <div className="mt-auto">
                <div className="mb-2 flex items-center justify-center gap-1 text-sm text-mq-gold">
                  🪙 {item.price.toLocaleString()}
                </div>
                <button
                  disabled={!canAfford}
                  onClick={() => canAfford && handlePurchase(item)}
                  className={cn(
                    "w-full rounded-xl py-2 text-xs font-semibold transition-colors",
                    canAfford
                      ? "bg-mq-blue text-mq-bg hover:bg-mq-blue2"
                      : "cursor-not-allowed bg-mq-card2 text-mq-muted"
                  )}
                >
                  {canAfford ? "Comprar" : "Sin Oro"}
                </button>
              </div>
            </div>
          );
        })}
      </div>

      <div className="relative mt-5 rounded-xl border border-mq-blue/20 bg-mq-bg p-4 text-center text-xs text-mq-muted">
        💡 El oro se gana completando combates y mejorando tus hábitos diarios. Las mejoras de equipo afectan las estadísticas en la base de datos.
      </div>
    </div>
  );
}

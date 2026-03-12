import {
  Sword,
  Shield,
  Heart,
  Zap,
  Star,
  Lock,
  TrendingUp,
  Award,
} from "lucide-react";
import type { Equipment, EquipmentSet } from "@/lib/types";
import { RARITY_STYLES } from "@/lib/utils";
import { cn } from "@/lib/utils";

// Icon registry — add more as needed
const ICON_MAP: Record<string, React.ElementType> = {
  Sword,
  Shield,
  Heart,
  Zap,
  Star,
  TrendingUp,
  Award,
};

interface EquipmentCardProps {
  item: Equipment;
}

function EquipmentCard({ item }: EquipmentCardProps) {
  const styles = RARITY_STYLES[item.rarity];
  const Icon = ICON_MAP[item.iconName] ?? Sword;

  return (
    <div
      className={cn(
        "relative rounded-xl border p-4 transition-transform hover:scale-[1.04] cursor-pointer",
        styles.bg,
        styles.border,
        item.locked && "opacity-55"
      )}
    >
      {item.locked && (
        <div className="absolute right-2 top-2 rounded-full bg-mq-bg p-1">
          <Lock className="h-3 w-3 text-mq-muted" />
        </div>
      )}

      <div className="flex flex-col items-center">
        {/* Icon */}
        <div
          className={cn(
            "mb-3 flex h-14 w-14 items-center justify-center rounded-full border shadow-inner",
            styles.bg,
            styles.border
          )}
        >
          <Icon className={cn("h-7 w-7", styles.text)} />
        </div>

        {/* Name */}
        <p
          className={cn(
            "mb-2 text-center text-xs font-medium text-mq-text",
            item.locked && "blur-[4px]"
          )}
        >
          {item.name}
        </p>

        {/* Stars */}
        <div className="mb-2 flex gap-0.5">
          {Array.from({ length: styles.stars }).map((_, i) => (
            <Star key={i} className={cn("h-2.5 w-2.5 fill-current", styles.text)} />
          ))}
        </div>

        {/* Stats */}
        <div className="w-full space-y-0.5">
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
      </div>
    </div>
  );
}

interface EquipmentInventoryProps {
  equipment: Equipment[];
  activeSet: EquipmentSet;
  totalSlots: number;
}

export function EquipmentInventory({
  equipment,
  activeSet,
  totalSlots,
}: EquipmentInventoryProps) {
  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-6 shadow-lg">
      <div className="pointer-events-none absolute right-1/4 top-0 h-28 w-28 rounded-full bg-mq-blue opacity-10 blur-[80px]" />
      <div className="pointer-events-none absolute bottom-0 left-1/4 h-28 w-28 rounded-full bg-mq-gold opacity-10 blur-[80px]" />

      <div className="relative mb-5 flex items-center justify-between">
        <div>
          <h2 className="flex items-center gap-2 text-lg font-semibold text-mq-text">
            <Sword className="h-5 w-5 text-mq-blue" />
            Inventario de Equipo
          </h2>
          <p className="mt-0.5 text-sm text-mq-muted">Tu arsenal de batalla</p>
        </div>
        <div className="flex items-center gap-1.5 text-sm text-mq-muted">
          <Star className="h-4 w-4 text-mq-gold" />
          {equipment.length}/{totalSlots} Equipos
        </div>
      </div>

      <div className="relative grid grid-cols-4 gap-3">
        {equipment.map((item) => (
          <EquipmentCard key={item.id} item={item} />
        ))}
      </div>

      {/* Set footer */}
      <div className="relative mt-5 flex items-center justify-between rounded-xl border border-mq-blue/20 bg-mq-bg px-4 py-3">
        <div className="flex items-center gap-3">
          <div className="flex">
            {[
              { cls: "from-mq-gold to-mq-gold2", textCls: "text-mq-bg" },
              { cls: "from-[#9d8ac7] to-mq-purple", textCls: "text-white" },
              { cls: "from-mq-blue to-mq-blue2", textCls: "text-white" },
            ].map((s, i) => (
              <div
                key={i}
                className={cn(
                  "flex h-8 w-8 items-center justify-center rounded-full border-2 border-mq-bg bg-gradient-to-br",
                  s.cls,
                  i > 0 && "-ml-2"
                )}
              >
                <Star className={cn("h-3.5 w-3.5", s.textCls)} />
              </div>
            ))}
          </div>
          <div>
            <p className="text-sm text-mq-text">{activeSet.name}</p>
            <p className="text-xs text-mq-muted">Bonus: {activeSet.bonus}</p>
          </div>
        </div>
        {/* TODO: call equip set API */}
        <button className="rounded-xl bg-mq-blue px-4 py-2 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-colors hover:bg-mq-blue2">
          Equipar Set
        </button>
      </div>
    </div>
  );
}

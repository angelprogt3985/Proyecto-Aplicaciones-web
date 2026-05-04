"use client";

import { useState } from "react";
import { Shield, Zap, Star, Award, Edit2, Check, Loader2 } from "lucide-react";
import type { User as UserType } from "@/lib/types";
import { saveUserData } from "@/lib/firestore";
import { cn } from "@/lib/utils";

interface ProfileSectionProps {
  user: UserType;
  onUserUpdated?: (updated: Partial<UserType>) => void;
}

const HERO_CLASSES = ["Guerrero","Sanador","Asesino","Mago","Caballero","Arquero","Druida","Paladín"] as const;
const CLASS_ICONS: Record<string,string> = { Guerrero:"⚔️",Sanador:"⚕️",Asesino:"🥷",Mago:"🔮",Caballero:"🛡️",Arquero:"🏹",Druida:"🌿",Paladín:"✨" };

export function ProfileSection({ user, onUserUpdated }: ProfileSectionProps) {
  const [editing, setEditing] = useState(false);
  const [displayName, setDisplayName] = useState(user.displayName);
  const [heroClass, setHeroClass] = useState(user.heroClass);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  const xpPercent = Math.min(100, Math.round((user.xp / user.xpToNextLevel) * 100));

  const handleSave = async () => {
  setSaving(true);
  try {
    await saveUserData({
      heroLevel:   user.level,
      heroXp:      user.xp,
      heroGold:    user.gold,
      heroHp:      user.heroHp,
      totalXp:     user.totalXp,
      displayName: displayName,
      heroClass:   heroClass,
    });
    onUserUpdated?.({ displayName, heroClass });
    setSaved(true);
    setEditing(false);
    setTimeout(() => setSaved(false), 3000);
  } catch { /* ignore */ } finally { setSaving(false); }
};

  return (
    <div className="flex flex-col gap-7">
      <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-8 shadow-lg">
        <div className="pointer-events-none absolute right-0 top-0 h-64 w-64 rounded-full bg-mq-blue opacity-10 blur-[120px]" />
        <div className="pointer-events-none absolute bottom-0 left-0 h-48 w-48 rounded-full bg-mq-gold opacity-10 blur-[90px]" />
        <div className="relative flex items-center gap-8">
          <div className="flex h-28 w-28 flex-shrink-0 items-center justify-center rounded-2xl border-2 border-mq-gold/40 bg-gradient-to-br from-mq-blue/20 to-mq-gold/10 text-5xl shadow-xl">
            {CLASS_ICONS[user.heroClass] ?? "⚔️"}
          </div>
          <div className="flex-1">
            {editing ? (
              <input value={displayName} onChange={(e) => setDisplayName(e.target.value)}
                className="mb-2 w-full rounded-xl border border-mq-blue/40 bg-mq-bg px-4 py-2 text-2xl font-bold text-mq-text outline-none focus:border-mq-blue" />
            ) : (
              <h2 className="mb-1 text-3xl font-bold text-mq-text">{user.displayName}</h2>
            )}
            <div className="flex items-center gap-3 text-sm text-mq-muted">
              <span className="rounded-full border border-mq-gold/40 bg-mq-gold/10 px-3 py-0.5 text-mq-gold">Nivel {user.level}</span>
              <span>{user.heroClass}</span>
              <span className="flex items-center gap-1"><span className="h-1.5 w-1.5 rounded-full bg-mq-blue" />En Línea</span>
            </div>
            <div className="mt-4">
              <div className="mb-1.5 flex justify-between text-xs text-mq-muted">
                <span>Experiencia</span>
                <span>{user.xp.toLocaleString()} / {user.xpToNextLevel.toLocaleString()} XP</span>
              </div>
              <div className="h-2.5 w-full overflow-hidden rounded-full bg-mq-bg">
                <div className="h-full rounded-full bg-gradient-to-r from-mq-blue to-mq-blue2 transition-all duration-700" style={{ width: `${xpPercent}%` }} />
              </div>
            </div>
          </div>
          <button onClick={() => { if (editing) handleSave(); else setEditing(true); }} disabled={saving}
            className="flex items-center gap-2 rounded-xl border border-mq-blue/40 bg-mq-blue px-4 py-2 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-all hover:bg-mq-blue2 disabled:opacity-60">
            {saving ? <Loader2 className="h-4 w-4 animate-spin" /> : editing ? <Check className="h-4 w-4" /> : <Edit2 className="h-4 w-4" />}
            {saving ? "Guardando…" : editing ? "Guardar" : "Editar"}
          </button>
        </div>
        {editing && (
          <div className="relative mt-6">
            <p className="mb-3 text-sm text-mq-muted">Elige tu clase de héroe:</p>
            <div className="grid grid-cols-4 gap-3">
              {HERO_CLASSES.map((cls) => (
                <button key={cls} onClick={() => setHeroClass(cls)}
                  className={cn("flex flex-col items-center gap-1.5 rounded-xl border p-3 text-sm transition-all",
                    heroClass === cls ? "border-mq-blue bg-mq-blue/20 text-mq-blue" : "border-mq-blue/20 text-mq-muted hover:border-mq-blue/40 hover:text-mq-text")}>
                  <span className="text-2xl">{CLASS_ICONS[cls]}</span>
                  <span className="text-xs">{cls}</span>
                </button>
              ))}
            </div>
          </div>
        )}
      </div>
      <div className="grid grid-cols-4 gap-4">
        {[
          { label:"Nivel",value:user.level,icon:Star,color:"text-mq-gold",border:"border-mq-gold/30" },
          { label:"Oro",value:user.gold.toLocaleString(),icon:Award,color:"text-mq-gold",border:"border-mq-gold/30" },
          { label:"Energía",value:`${user.energy}%`,icon:Zap,color:"text-mq-blue",border:"border-mq-blue/30" },
          { label:"Clase",value:user.heroClass,icon:Shield,color:"text-mq-purple",border:"border-mq-purple/30" },
        ].map(({ label,value,icon:Icon,color,border }) => (
          <div key={label} className={cn("rounded-xl border bg-mq-card p-5 text-center",border)}>
            <Icon className={cn("mx-auto mb-2 h-6 w-6",color)} />
            <p className={cn("text-xl font-bold",color)}>{value}</p>
            <p className="mt-1 text-xs text-mq-muted">{label}</p>
          </div>
        ))}
      </div>
      {saved && <div className="rounded-xl border border-mq-blue/30 bg-mq-blue/10 px-5 py-3 text-sm text-mq-blue">✓ Perfil actualizado correctamente</div>}
    </div>
  );
}
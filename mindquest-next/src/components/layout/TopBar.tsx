"use client";

import { Search, Bell, Settings, User, ChevronDown } from "lucide-react";
import type { User as UserType } from "@/lib/types";

interface TopBarProps {
  user: UserType;
}

export function TopBar({ user }: TopBarProps) {
  const energyPct = user.energy;
  const xpPct = Math.round((user.xp / user.xpToNextLevel) * 100);

  return (
    <header className="relative z-10 flex flex-shrink-0 items-center justify-between border-b border-mq-blue/25 bg-mq-card px-8 py-4 overflow-hidden">
      {/* Ambient glow */}
      <div className="pointer-events-none absolute left-1/3 top-0 h-20 w-52 rounded-full bg-mq-blue opacity-10 blur-[90px]" />

      {/* Search */}
      <div className="relative max-w-sm flex-1">
        <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-mq-muted" />
        <input
          type="text"
          placeholder="Buscar..."
          className="w-full rounded-xl border border-mq-blue/25 bg-mq-bg py-2.5 pl-10 pr-4 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-blue"
        />
      </div>

      {/* Right cluster */}
      <div className="ml-6 flex items-center gap-4">
        {/* Quick stats pill */}
        <div className="flex items-center gap-4 rounded-xl border border-mq-blue/20 bg-mq-bg px-4 py-2">
          {/* Energy */}
          <div>
            <p className="text-[10px] text-mq-muted">Energía</p>
            <div className="mt-1 flex items-center gap-1.5">
              <div className="h-1.5 w-16 overflow-hidden rounded-full bg-mq-card2">
                <div
                  className="h-full rounded-full bg-gradient-to-r from-mq-blue to-mq-blue2 transition-all"
                  style={{ width: `${energyPct}%` }}
                />
              </div>
              <span className="text-[11px] text-mq-blue">{energyPct}%</span>
            </div>
          </div>

          <div className="h-8 w-px bg-mq-blue/20" />

          {/* XP */}
          <div>
            <p className="text-[10px] text-mq-muted">XP Diaria</p>
            <p className="mt-1 text-xs text-mq-text">
              {user.xp.toLocaleString()} / {user.xpToNextLevel.toLocaleString()}
            </p>
          </div>
        </div>

        {/* Notification */}
        <button className="relative rounded-lg p-2 text-mq-muted transition-colors hover:bg-mq-bg hover:text-mq-text">
          <Bell className="h-5 w-5" />
          <span className="absolute right-1.5 top-1.5 h-1.5 w-1.5 rounded-full bg-mq-blue" />
        </button>

        {/* Settings */}
        <button className="rounded-lg p-2 text-mq-muted transition-colors hover:bg-mq-bg hover:text-mq-text">
          <Settings className="h-5 w-5" />
        </button>

        {/* User */}
        <div className="flex items-center gap-3 border-l border-mq-blue/20 pl-4">
          <div className="text-right">
            <p className="text-sm text-mq-text">{user.displayName}</p>
            <p className="text-[11px] text-mq-blue">Nivel {user.level}</p>
          </div>
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-mq-blue to-mq-blue2 shadow-md shadow-mq-blue/30">
            <User className="h-4 w-4 text-mq-bg" />
          </div>
          <ChevronDown className="h-4 w-4 text-mq-muted" />
        </div>
      </div>
    </header>
  );
}

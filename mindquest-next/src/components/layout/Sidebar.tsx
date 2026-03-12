"use client";

import {
  LayoutDashboard,
  Trophy,
  Sword,
  Sparkles,
  Crown,
  User,
  Settings,
  LogOut,
} from "lucide-react";
import type { NavSection } from "@/lib/types";
import { cn } from "@/lib/utils";

interface NavItem {
  id: NavSection;
  label: string;
  icon: React.ElementType;
}

const NAV_ITEMS: NavItem[] = [
  { id: "dashboard", label: "Dashboard",  icon: LayoutDashboard },
  { id: "battles",   label: "Combates",   icon: Trophy           },
  { id: "inventory", label: "Inventario", icon: Sword            },
  { id: "oracle",    label: "Oráculo",    icon: Sparkles         },
  { id: "ranking",   label: "Ranking",    icon: Crown            },
  { id: "profile",   label: "Perfil",     icon: User             },
];

const BOTTOM_ITEMS: NavItem[] = [
  { id: "settings", label: "Ajustes", icon: Settings },
];

interface SidebarProps {
  activeSection: NavSection;
  onNavigate: (section: NavSection) => void;
  userGold: number;
}

export function Sidebar({ activeSection, onNavigate, userGold }: SidebarProps) {
  return (
    <aside className="relative z-10 flex h-screen w-64 flex-shrink-0 flex-col overflow-hidden border-r border-mq-blue/20 bg-mq-bg">
      {/* Decorative glows */}
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute right-4 top-8 h-32 w-32 rounded-full bg-mq-blue opacity-20 blur-[70px]" />
        <div className="absolute bottom-20 left-2 h-24 w-24 rounded-full bg-mq-gold opacity-15 blur-[55px]" />
      </div>

      {/* Logo */}
      <div className="relative flex items-center gap-3 border-b border-mq-blue/20 p-6">
        <div className="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-xl bg-gradient-to-br from-mq-blue to-mq-blue2 shadow-lg shadow-mq-blue/30">
          <Sparkles className="h-5 w-5 text-mq-bg" />
        </div>
        <div>
          <p className="text-lg font-semibold text-mq-text tracking-wide">MindQuest</p>
          <p className="text-xs text-mq-blue">Nivel 42 · Héroe</p>
        </div>
      </div>

      {/* Main nav */}
      <nav className="relative flex flex-1 flex-col gap-1 px-3 py-5">
        {NAV_ITEMS.map(({ id, label, icon: Icon }) => {
          const isActive = activeSection === id;
          return (
            <button
              key={id}
              onClick={() => onNavigate(id)}
              className={cn(
                "flex w-full items-center gap-3 rounded-xl border px-3 py-2.5 text-sm transition-all",
                isActive
                  ? "border-mq-blue/40 bg-mq-card text-mq-blue shadow-lg shadow-mq-blue/15"
                  : "border-transparent text-mq-muted hover:bg-mq-card hover:text-mq-text"
              )}
            >
              <Icon className="h-5 w-5 flex-shrink-0" />
              <span>{label}</span>
              {isActive && (
                <span className="ml-auto h-1.5 w-1.5 rounded-full bg-mq-blue shadow-sm shadow-mq-blue" />
              )}
            </button>
          );
        })}
      </nav>

      {/* Bottom nav */}
      <div className="relative flex flex-col gap-1 border-t border-mq-blue/20 px-3 py-3">
        {BOTTOM_ITEMS.map(({ id, label, icon: Icon }) => (
          <button
            key={id}
            onClick={() => onNavigate(id)}
            className="flex w-full items-center gap-3 rounded-xl border border-transparent px-3 py-2.5 text-sm text-mq-muted transition-all hover:bg-mq-card hover:text-mq-text"
          >
            <Icon className="h-5 w-5 flex-shrink-0" />
            <span>{label}</span>
          </button>
        ))}
        <button className="flex w-full items-center gap-3 rounded-xl border border-transparent px-3 py-2.5 text-sm text-mq-muted transition-all hover:bg-mq-card hover:text-mq-text">
          <LogOut className="h-5 w-5 flex-shrink-0" />
          <span>Salir</span>
        </button>
      </div>

      {/* Footer */}
      <div className="relative flex items-center justify-between border-t border-mq-blue/20 px-4 py-3">
        <div className="flex items-center gap-2 text-xs text-mq-muted">
          <span className="h-2 w-2 rounded-full bg-mq-blue shadow-sm shadow-mq-blue" />
          En Línea
        </div>
        <div className="flex items-center gap-1 text-xs text-mq-gold">
          <span className="font-medium">{userGold.toLocaleString()}</span>
          <span>🪙</span>
        </div>
      </div>
    </aside>
  );
}

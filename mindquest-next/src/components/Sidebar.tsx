"use client";
import { Home, Trophy, Sparkles, Sword, User, Settings, LogOut, Crown } from 'lucide-react';

interface SidebarProps {
  activeSection?: string;
  onNavigate?: (section: string) => void;
}

export function Sidebar({ activeSection = 'dashboard', onNavigate }: SidebarProps) {
  const menuItems = [
    { icon: Home, label: 'Dashboard', id: 'dashboard' },
    { icon: Trophy, label: 'Combates', id: 'battles' },
    { icon: Sword, label: 'Inventario', id: 'inventory' },
    { icon: Sparkles, label: 'Oráculo', id: 'oracle' },
    { icon: Crown, label: 'Ranking', id: 'ranking' },
    { icon: User, label: 'Perfil', id: 'profile' },
  ];

  const bottomItems = [
    { icon: Settings, label: 'Ajustes', id: 'settings' },
    { icon: LogOut, label: 'Salir', id: 'logout' },
  ];

  return (
    <aside className="w-64 h-screen bg-[#1a1f2e] border-r border-[rgba(88,166,255,0.2)] flex flex-col relative overflow-hidden flex-shrink-0">
      <div className="absolute inset-0 opacity-30 pointer-events-none">
        <div className="absolute top-10 right-10 w-32 h-32 bg-[#58a6ff] rounded-full blur-[80px]" />
        <div className="absolute bottom-20 left-10 w-24 h-24 bg-[#e0b35e] rounded-full blur-[60px]" />
      </div>

      {/* Logo */}
      <div className="relative p-6 border-b border-[rgba(88,166,255,0.2)]">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-[#58a6ff] to-[#6eb5ff] rounded-lg flex items-center justify-center shadow-lg shadow-[#58a6ff]/30">
            <Sparkles className="w-6 h-6 text-[#1a1f2e]" />
          </div>
          <div>
            <h1 className="text-xl font-medium text-[#f0f6fc] tracking-wide">MindQuest</h1>
            <p className="text-xs text-[#58a6ff]">Nivel 42 • Héroe</p>
          </div>
        </div>
      </div>

      {/* Navigation */}
      <nav className="relative flex-1 px-3 py-6 space-y-1">
        {menuItems.map((item) => {
          const Icon = item.icon;
          const isActive = activeSection === item.id;
          return (
            <button
              key={item.id}
              onClick={() => onNavigate?.(item.id)}
              className={`w-full flex items-center gap-3 px-3 py-2.5 rounded-lg transition-all text-left ${
                isActive
                  ? 'bg-[#242b3d] text-[#58a6ff] border border-[rgba(88,166,255,0.4)] shadow-lg shadow-[#58a6ff]/20'
                  : 'text-[#a0aec0] hover:text-[#f0f6fc] hover:bg-[#242b3d]'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span className="text-sm">{item.label}</span>
              {isActive && (
                <div className="ml-auto w-1.5 h-1.5 rounded-full bg-[#58a6ff] shadow-sm shadow-[#58a6ff]" />
              )}
            </button>
          );
        })}
      </nav>

      {/* Bottom Menu */}
      <div className="relative px-3 py-4 border-t border-[rgba(88,166,255,0.2)] space-y-1">
        {bottomItems.map((item) => {
          const Icon = item.icon;
          return (
            <button
              key={item.id}
              className="w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-[#a0aec0] hover:text-[#f0f6fc] hover:bg-[#242b3d] transition-all"
            >
              <Icon className="w-5 h-5" />
              <span className="text-sm">{item.label}</span>
            </button>
          );
        })}
      </div>

      {/* User Stats Footer */}
      <div className="relative px-4 py-4 bg-[#1a1f2e] border-t border-[rgba(88,166,255,0.2)]">
        <div className="flex items-center justify-between text-xs">
          <div className="flex items-center gap-2">
            <div className="w-2 h-2 rounded-full bg-[#58a6ff] shadow-sm shadow-[#58a6ff]" />
            <span className="text-[#a0aec0]">En Línea</span>
          </div>
          <div className="flex items-center gap-1 text-[#e0b35e]">
            <span>1,250</span>
            <span>🪙</span>
          </div>
        </div>
      </div>
    </aside>
  );
}

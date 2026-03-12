"use client";
import { Search, Bell, Settings, User, ChevronDown } from 'lucide-react';

export function DashboardHeader() {
  return (
    <header className="bg-[#242b3d] border-b border-[rgba(88,166,255,0.25)] px-8 py-4 relative flex-shrink-0">
      <div className="absolute top-0 left-1/3 w-64 h-32 bg-[#58a6ff] rounded-full blur-[120px] opacity-15 pointer-events-none" />
      
      <div className="relative flex items-center justify-between">
        <div className="flex-1 max-w-xl">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-[#a0aec0]" />
            <input
              type="text"
              placeholder="Buscar..."
              className="w-full bg-[#1a1f2e] border border-[rgba(88,166,255,0.25)] rounded-lg pl-10 pr-4 py-2.5 text-[#f0f6fc] placeholder-[#a0aec0] focus:outline-none focus:ring-2 focus:ring-[#58a6ff] text-sm"
            />
          </div>
        </div>

        <div className="flex items-center gap-4 ml-8">
          {/* Stats Quick View */}
          <div className="flex items-center gap-4 px-4 py-2 bg-[#1a1f2e] rounded-lg border border-[rgba(88,166,255,0.2)]">
            <div className="text-center">
              <p className="text-xs text-[#a0aec0]">Energía</p>
              <div className="flex items-center gap-1 mt-1">
                <div className="w-16 h-1.5 bg-[#2d3548] rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-[#58a6ff] to-[#6eb5ff] w-[85%]" />
                </div>
                <span className="text-xs text-[#58a6ff]">85%</span>
              </div>
            </div>
            <div className="w-px h-8 bg-[rgba(88,166,255,0.2)]" />
            <div className="text-center">
              <p className="text-xs text-[#a0aec0]">XP Diaria</p>
              <p className="text-sm text-[#f0f6fc] mt-1">2,450 / 3,000</p>
            </div>
          </div>

          <button className="relative p-2 hover:bg-[#1a1f2e] rounded-lg transition-colors">
            <Bell className="w-5 h-5 text-[#a0aec0]" />
            <span className="absolute top-1 right-1 w-2 h-2 bg-[#58a6ff] rounded-full" />
          </button>

          <button className="p-2 hover:bg-[#1a1f2e] rounded-lg transition-colors">
            <Settings className="w-5 h-5 text-[#a0aec0]" />
          </button>

          <div className="flex items-center gap-3 pl-4 border-l border-[rgba(88,166,255,0.2)]">
            <div className="text-right">
              <p className="text-sm text-[#f0f6fc]">Aventurero</p>
              <p className="text-xs text-[#58a6ff]">Nivel 42</p>
            </div>
            <div className="w-10 h-10 bg-gradient-to-br from-[#58a6ff] to-[#6eb5ff] rounded-full flex items-center justify-center shadow-md shadow-[#58a6ff]/30">
              <User className="w-5 h-5 text-[#1a1f2e]" />
            </div>
            <ChevronDown className="w-4 h-4 text-[#a0aec0]" />
          </div>
        </div>
      </div>
    </header>
  );
}

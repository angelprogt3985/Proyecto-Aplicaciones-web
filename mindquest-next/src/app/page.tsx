"use client";

import { useState } from "react";
import type { NavSection } from "@/lib/types";

// Layout
import { Sidebar } from "@/components/layout/Sidebar";
import { TopBar }   from "@/components/layout/TopBar";

// Sections
import { WelcomeBanner }      from "@/components/dashboard/WelcomeBanner";
import { VitalityStats }      from "@/components/dashboard/VitalityStats";
import { GeminiOracle }       from "@/components/oracle/GeminiOracle";
import { HeroesRanking }      from "@/components/ranking/HeroesRanking";
import { EquipmentInventory } from "@/components/inventory/EquipmentInventory";
import { BattlesHistory }     from "@/components/battles/BattlesHistory";
import { GoldShop }           from "@/components/shop/GoldShop";

// Mock data (swap for real API calls per section later)
import {
  MOCK_USER,
  MOCK_VITALITY,
  MOCK_BATTLES,
  MOCK_EQUIPMENT,
  MOCK_EQUIPMENT_SET,
  MOCK_SHOP_ITEMS,
  MOCK_RANKING,
  MOCK_ORACLE_MESSAGES,
} from "@/lib/data/mock";

export default function DashboardPage() {
  const [activeSection, setActiveSection] = useState<NavSection>("dashboard");

  return (
    <div className="relative flex h-screen overflow-hidden bg-mq-bg">
      {/* Global ambient background */}
      <div className="pointer-events-none fixed inset-0 z-0">
        <div className="absolute right-1/4 top-1/4 h-96 w-96 rounded-full bg-mq-blue opacity-[0.07] blur-[150px]" />
        <div className="absolute bottom-1/4 left-1/4 h-96 w-96 rounded-full bg-mq-gold opacity-[0.07] blur-[150px]" />
      </div>

      {/* Sidebar */}
      <Sidebar
        activeSection={activeSection}
        onNavigate={setActiveSection}
        userGold={MOCK_USER.gold}
      />

      {/* Main area */}
      <div className="relative z-10 flex flex-1 flex-col overflow-hidden">
        <TopBar user={MOCK_USER} />

        <main className="flex-1 overflow-y-auto px-8 py-8">
          <div className="mx-auto flex max-w-[1600px] flex-col gap-7">

            {/* ① Welcome banner */}
            <WelcomeBanner user={MOCK_USER} weeklyBattles={12} />

            {/* ② Vitality chart */}
            <VitalityStats stats={MOCK_VITALITY} />

            {/* ③ Oracle + Ranking side by side */}
            <div className="grid grid-cols-3 gap-7">
              <div className="col-span-1">
                <GeminiOracle initialMessages={MOCK_ORACLE_MESSAGES} />
              </div>
              <div className="col-span-2">
                <HeroesRanking heroes={MOCK_RANKING} />
              </div>
            </div>

            {/* ④ Equipment inventory */}
            <EquipmentInventory
              equipment={MOCK_EQUIPMENT}
              activeSet={MOCK_EQUIPMENT_SET}
              totalSlots={12}
            />

            {/* ⑤ Battle history */}
            <BattlesHistory battles={MOCK_BATTLES} />

            {/* ⑥ Gold shop */}
            <GoldShop items={MOCK_SHOP_ITEMS} userGold={MOCK_USER.gold} />

          </div>
        </main>
      </div>
    </div>
  );
}

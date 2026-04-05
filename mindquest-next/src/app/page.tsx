"use client";

import { useState, useEffect } from "react";
import type { NavSection } from "@/lib/types";
import type { User } from "@/lib/types";

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

import { loadUserData } from "@/lib/firestore";
import { auth }         from "@/lib/firebase";

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
  const [user, setUser] = useState<User>(MOCK_USER);
  const [isLoadingUser, setIsLoadingUser] = useState(true);

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(async (firebaseUser) => {
      if (firebaseUser) {
        const data = await loadUserData();
        if (data) {
          setUser({
            ...MOCK_USER,
            id:          firebaseUser.uid,
            username:    data.displayName ?? firebaseUser.email ?? "Guerrero",
            displayName: data.displayName ?? "Guerrero",
            level:       data.heroLevel   ?? 1,
            xp:          data.heroXp      ?? 0,
            gold:        data.heroGold    ?? 0,
            energy:      data.heroHp      ?? 100,
          });
        }
      }
      setIsLoadingUser(false);
    });

    return () => unsubscribe();
  }, []);

  if (isLoadingUser) {
    return (
      <div className="flex h-screen items-center justify-center bg-mq-bg">
        <p className="text-mq-muted text-sm">Cargando...</p>
      </div>
    );
  }

  return (
    <div className="relative flex h-screen overflow-hidden bg-mq-bg">
      <div className="pointer-events-none fixed inset-0 z-0">
        <div className="absolute right-1/4 top-1/4 h-96 w-96 rounded-full bg-mq-blue opacity-[0.07] blur-[150px]" />
        <div className="absolute bottom-1/4 left-1/4 h-96 w-96 rounded-full bg-mq-gold opacity-[0.07] blur-[150px]" />
      </div>

      <Sidebar
        activeSection={activeSection}
        onNavigate={setActiveSection}
        userGold={user.gold}
      />

      <div className="relative z-10 flex flex-1 flex-col overflow-hidden">
        <TopBar user={user} />

        <main className="flex-1 overflow-y-auto px-8 py-8">
          <div className="mx-auto flex max-w-[1600px] flex-col gap-7">

            <WelcomeBanner user={user} weeklyBattles={12} />

            <VitalityStats stats={MOCK_VITALITY} />

            <div className="grid grid-cols-3 gap-7">
              <div className="col-span-1">
                <GeminiOracle initialMessages={MOCK_ORACLE_MESSAGES} />
              </div>
              <div className="col-span-2">
                <HeroesRanking heroes={MOCK_RANKING} />
              </div>
            </div>

            <EquipmentInventory
              equipment={MOCK_EQUIPMENT}
              activeSet={MOCK_EQUIPMENT_SET}
              totalSlots={12}
            />

            <BattlesHistory battles={MOCK_BATTLES} />

            <GoldShop items={MOCK_SHOP_ITEMS} userGold={user.gold} />

          </div>
        </main>
      </div>
    </div>
  );
}
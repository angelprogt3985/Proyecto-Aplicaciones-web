"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import type { NavSection, User, BattleRecord, RankedHero, ShopItem } from "@/lib/types";
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

import { loadUserData, loadBattles, loadRanking, spendGold } from "@/lib/firestore";
import { auth } from "@/lib/firebase";

import {
  MOCK_USER,
  MOCK_VITALITY,
  MOCK_EQUIPMENT,
  MOCK_EQUIPMENT_SET,
  MOCK_SHOP_ITEMS,
  MOCK_ORACLE_MESSAGES,
} from "@/lib/data/mock";

export default function DashboardPage() {
  const router = useRouter();
  const [activeSection, setActiveSection] = useState<NavSection>("dashboard");
  const [user, setUser]                   = useState<User>(MOCK_USER);
  const [battles, setBattles]             = useState<BattleRecord[]>([]);
  const [ranking, setRanking]             = useState<RankedHero[]>([]);
  const [isLoadingUser, setIsLoadingUser] = useState(true);

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(async (firebaseUser) => {
      if (!firebaseUser) {
        router.push("/login");
        return;
      }

      const [data, rawBattles, rawRanking] = await Promise.all([
        loadUserData(),
        loadBattles(),
        loadRanking(),
      ]);

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

      setBattles(
        rawBattles.map((b) => ({
          id:         b.id,
          date:       b.date?.toDate?.().toISOString().split("T")[0] ?? new Date().toISOString().split("T")[0],
          habitType:  b.habitType  ?? "Agua",
          result:     b.result     ?? "Victoria",
          goldEarned: b.goldEarned ?? 0,
          xpEarned:   b.xpEarned   ?? 0,
          userId:     firebaseUser.uid,
        }))
      );

      setRanking(
        rawRanking.map((r, i) => ({
          rank:        i + 1,
          userId:      r.id,
          name:        r.displayName ?? "Héroe",
          level:       r.heroLevel   ?? 1,
          gold:        r.heroGold    ?? 0,
          heroClass:   r.heroClass   ?? "Guerrero",
          avatarEmoji: "⚔️",
        }))
      );

      setIsLoadingUser(false);
    });

    return () => unsubscribe();
  }, [router]);

  async function handlePurchase(item: ShopItem) {
    if (user.gold < item.price) return;
    await spendGold(item.price, {
      id:          item.id,
      name:        item.name,
      description: item.description,
      category:    item.category,
      rarity:      item.rarity,
      stats:       item.stats as Record<string, number>,
      iconName:    item.iconName,
    });
    setUser((prev) => ({ ...prev, gold: prev.gold - item.price }));
  }

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

            <WelcomeBanner user={user} weeklyBattles={battles.length} />

            <VitalityStats stats={MOCK_VITALITY} />

            <div className="grid grid-cols-3 gap-7">
              <div className="col-span-1">
                <GeminiOracle initialMessages={MOCK_ORACLE_MESSAGES} />
              </div>
              <div className="col-span-2">
                <HeroesRanking heroes={ranking} />
              </div>
            </div>

            <EquipmentInventory
              equipment={MOCK_EQUIPMENT}
              activeSet={MOCK_EQUIPMENT_SET}
              totalSlots={12}
            />

            <BattlesHistory battles={battles} />

          <GoldShop
          items={MOCK_SHOP_ITEMS}
          userGold={user.gold}
          onPurchase={handlePurchase}
          />

          </div>
        </main>
      </div>
    </div>
  );
}
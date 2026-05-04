"use client";

import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import type { NavSection, User, BattleRecord, RankedHero, ShopItem, WeeklyVitalityStats } from "@/lib/types";
import { Sidebar }            from "@/components/layout/Sidebar";
import { TopBar }             from "@/components/layout/TopBar";
import { WelcomeBanner }      from "@/components/dashboard/WelcomeBanner";
import { VitalityStats }      from "@/components/dashboard/VitalityStats";
import { GeminiOracle }       from "@/components/oracle/GeminiOracle";
import { HeroesRanking }      from "@/components/ranking/HeroesRanking";
import { EquipmentInventory } from "@/components/inventory/EquipmentInventory";
import { BattlesHistory }     from "@/components/battles/BattlesHistory";
import { GoldShop }           from "@/components/shop/GoldShop";
import { ProfileSection }     from "@/components/profile/ProfileSection";
import { SettingsSection }    from "@/components/settings/SettingsSection";
import { GuideSection }       from "@/components/guide/GuideSection";
import {
  loadUserData, loadBattles, loadRanking, spendGold, loadInventory, loadWeeklyVitality,
  loadShopCatalog,
} from "@/lib/firestore";
import { auth } from "@/lib/firebase";
import type { HeroClass } from "@/lib/types";
import {
  MOCK_USER, MOCK_VITALITY, MOCK_EQUIPMENT_SET, MOCK_SHOP_ITEMS, MOCK_ORACLE_MESSAGES,
} from "@/lib/data/mock";

const SECTION_TITLES: Record<NavSection, string> = {
  dashboard: "Dashboard",
  battles:   "Combates",
  inventory: "Inventario",
  oracle:    "Oráculo",
  ranking:   "Ranking",
  profile:   "Perfil",
  settings:  "Ajustes",
  guide:     "Guía de Usuario",
};

function computeStatBonuses(ownedIds: string[]): { bonusHp: number; bonusPower: number } {
  if (!Array.isArray(ownedIds)) return { bonusHp: 0, bonusPower: 0 };
  let bonusHp = 0;
  let bonusPower = 0;
  for (const item of MOCK_SHOP_ITEMS) {
    if (ownedIds.includes(item.id)) {
      bonusHp    += item.stats.health  ?? 0;
      bonusPower += (item.stats.power ?? 0) + (item.stats.defense ?? 0);
    }
  }
  return { bonusHp, bonusPower };
}

export default function DashboardPage() {
  const router = useRouter();
  const [activeSection, setActiveSection] = useState<NavSection>("dashboard");
  const [user, setUser]                   = useState<User>(MOCK_USER);
  const [battles, setBattles]             = useState<BattleRecord[]>([]);
  const [ranking, setRanking]             = useState<RankedHero[]>([]);
  const [vitalityStats, setVitalityStats] = useState<WeeklyVitalityStats>(MOCK_VITALITY);
  const [isLoadingUser, setIsLoadingUser] = useState(true);
  const [purchasedIds, setPurchasedIds]   = useState<string[]>([]);
  const [shopItems, setShopItems]         = useState<ShopItem[]>([]);

  const refreshVitality = useCallback(async () => {
    try {
      const entries = await loadWeeklyVitality();
      const totalWeightLossKg  = parseFloat(entries.reduce((s, e) => s + e.weightLossKg, 0).toFixed(2));
      const avgActivityMinutes = Math.round(entries.reduce((s, e) => s + e.activityMinutes, 0) / (entries.length || 1));
      setVitalityStats({ entries, totalWeightLossKg, avgActivityMinutes });
    } catch {
      // keep previous value
    }
  }, []);

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged(async (firebaseUser) => {
      if (!firebaseUser) { router.push("/login"); return; }

      const [data, rawBattles, rawRanking, inventoryIds, catalog] = await Promise.all([
        loadUserData(), loadBattles(), loadRanking(), loadInventory(), loadShopCatalog(),
      ]);

      const safeInventoryIds = Array.isArray(inventoryIds) ? inventoryIds : [];

      if (data) {
        const { bonusHp } = computeStatBonuses(safeInventoryIds);
        setUser({
          ...MOCK_USER,
          id:            firebaseUser.uid,
          username:      data.displayName ?? firebaseUser.email ?? "Guerrero",
          displayName:   data.displayName ?? "Guerrero",
          heroClass:     (data.heroClass  ?? "Guerrero") as HeroClass,
          level:         data.heroLevel   ?? 1,
          xp:            data.heroXp      ?? 0,
          xpToNextLevel: 100,
          totalXp:       data.totalXp     ?? 0,
          gold:          data.heroGold    ?? 0,
          heroHp:        (data.heroHp    ?? 100) + bonusHp,
          heroMaxHp:     (data.heroMaxHP ?? 100) + bonusHp,
          isOnline:      true,
          createdAt:     "",
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

      setShopItems(catalog);
      setPurchasedIds(safeInventoryIds);
      await refreshVitality();
      setIsLoadingUser(false);
    });

    return () => unsubscribe();
  }, [router, refreshVitality]);

  async function handlePurchase(item: ShopItem) {
    if (user.gold < item.price) return;
    await spendGold(item.price, item);
    const newPurchasedIds = [...purchasedIds, item.id];
    const { bonusHp } = computeStatBonuses(newPurchasedIds);
    setUser((prev) => ({
      ...prev,
      gold:      prev.gold - item.price,
      heroHp:    prev.heroHp    + (item.bonusHp ?? 0),
      heroMaxHp: prev.heroMaxHp + (item.bonusHp ?? 0),
    }));
    setPurchasedIds(newPurchasedIds);
  }

  if (isLoadingUser) {
    return (
      <div className="flex h-screen items-center justify-center bg-mq-bg">
        <p className="text-mq-muted text-sm">Cargando…</p>
      </div>
    );
  }

  const weekAgo = new Date(); weekAgo.setDate(weekAgo.getDate() - 7);
  const weeklyBattles = battles.filter((b) => new Date(b.date) >= weekAgo).length;

  function renderSection() {
    switch (activeSection) {
      case "dashboard":
        return (
          <>
            <WelcomeBanner user={user} weeklyBattles={weeklyBattles} />
            <VitalityStats stats={vitalityStats} onStatsUpdated={refreshVitality} />
            <div className="grid grid-cols-3 gap-7">
              <div className="col-span-1">
                <GeminiOracle initialMessages={MOCK_ORACLE_MESSAGES} />
              </div>
              <div className="col-span-2">
                <HeroesRanking heroes={ranking} onViewAll={() => setActiveSection("ranking")} />
              </div>
            </div>
            <EquipmentInventory
              equipment={MOCK_SHOP_ITEMS.filter((i) => purchasedIds.includes(i.id)).map((i) => ({
                id: i.id, name: i.name, type: i.category as "weapon" | "armor" | "accessory",
                rarity: i.rarity, stats: i.stats, locked: false, iconName: i.iconName,
              }))}
              activeSet={MOCK_EQUIPMENT_SET}
              totalSlots={12}
            />
            <BattlesHistory battles={battles} />
            <GoldShop items={shopItems} userGold={user.gold} onPurchase={handlePurchase} purchasedIds={purchasedIds} />
          </>
        );

      case "battles":
        return <BattlesHistory battles={battles} />;

      case "inventory":
        return (
          <>
            <EquipmentInventory
              equipment={MOCK_SHOP_ITEMS.filter((i) => purchasedIds.includes(i.id)).map((i) => ({
                id: i.id, name: i.name, type: i.category as "weapon" | "armor" | "accessory",
                rarity: i.rarity, stats: i.stats, locked: false, iconName: i.iconName,
              }))}
              activeSet={MOCK_EQUIPMENT_SET}
              totalSlots={12}
            />
            <GoldShop items={shopItems} userGold={user.gold} onPurchase={handlePurchase} purchasedIds={purchasedIds} />
          </>
        );

      case "oracle":
        return <GeminiOracle initialMessages={MOCK_ORACLE_MESSAGES} />;

      case "ranking":
        return <HeroesRanking heroes={ranking} onViewAll={() => {}} />;

      case "profile":
        return (
          <ProfileSection
            user={user}
            onUserUpdated={(updated) => setUser((prev) => ({ ...prev, ...updated }))}
          />
        );

      case "settings":
        return <SettingsSection />;

      case "guide":
        return <GuideSection />;

      default:
        return null;
    }
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
        userLevel={user.level}
        userName={user.displayName}
      />

      <div className="relative z-10 flex flex-1 flex-col overflow-hidden">
        <TopBar user={user} />
        <main className="flex-1 overflow-y-auto px-8 py-8">
          <div className="mx-auto flex max-w-[1600px] flex-col gap-7">
            {renderSection()}
          </div>
        </main>
      </div>
    </div>
  );
}
// ─── User / Profile ───────────────────────────────────────────────────────────

export type HeroClass =
  | "Guerrero"
  | "Sanador"
  | "Asesino"
  | "Mago"
  | "Caballero"
  | "Arquero"
  | "Druida"
  | "Paladín";

export interface User {
  id: string;
  username: string;
  displayName: string;
  heroClass: HeroClass;
  level: number;
  xp: number;
  xpToNextLevel: number;
  gold: number;
  energy: number; // 0–100
  avatarUrl?: string;
  isOnline: boolean;
  createdAt: string;
}

// ─── Stats / Vitality ─────────────────────────────────────────────────────────

export interface DailyVitalityEntry {
  date: string; // ISO yyyy-mm-dd
  weightLossKg: number;
  activityMinutes: number;
}

export interface WeeklyVitalityStats {
  entries: DailyVitalityEntry[];
  totalWeightLossKg: number;
  avgActivityMinutes: number;
}

// ─── Battles / Habits ─────────────────────────────────────────────────────────

export type HabitType = "Agua" | "Postura" | "Mente";
export type BattleResult = "Victoria" | "Derrota";

export interface BattleRecord {
  id: string;
  date: string; // ISO yyyy-mm-dd
  habitType: HabitType;
  result: BattleResult;
  goldEarned: number;
  xpEarned: number;
  userId: string;
}

// ─── Equipment / Inventory ────────────────────────────────────────────────────

export type EquipmentRarity = "common" | "rare" | "epic" | "legendary";
export type EquipmentType = "weapon" | "armor" | "accessory";

export interface EquipmentStats {
  power?: number;
  defense?: number;
  health?: number;
}

export interface Equipment {
  id: string;
  name: string;
  type: EquipmentType;
  rarity: EquipmentRarity;
  stats: EquipmentStats;
  locked: boolean;
  iconName: string; // lucide icon name, resolved at render time
}

export interface EquipmentSet {
  id: string;
  name: string;
  bonus: string;
  equipmentIds: string[];
}

// ─── Shop ─────────────────────────────────────────────────────────────────────

export type ShopItemCategory = EquipmentType | "boost";

export interface ShopItem {
  id: string;
  name: string;
  description: string;
  price: number;
  category: ShopItemCategory;
  rarity: EquipmentRarity;
  stats: EquipmentStats;
  iconName: string;
}

// ─── Ranking ──────────────────────────────────────────────────────────────────

export interface RankedHero {
  rank: number;
  userId: string;
  name: string;
  level: number;
  gold: number;
  heroClass: HeroClass;
  avatarEmoji: string;
}

// ─── Oracle / Chat ────────────────────────────────────────────────────────────

export type ChatRole = "oracle" | "user";

export interface ChatMessage {
  id: string;
  role: ChatRole;
  text: string;
  timestamp: string;
}

// ─── UI State ─────────────────────────────────────────────────────────────────

export type NavSection =
  | "dashboard"
  | "battles"
  | "inventory"
  | "oracle"
  | "ranking"
  | "profile"
  | "settings";

export type HabitFilter = "Todos" | HabitType;
export type DateFilter = "all" | string;
export type ShopCategoryFilter = "all" | ShopItemCategory;

/**
 * Mock data layer.
 *
 * Each export mirrors what a real API response would look like.
 * To wire up real data: replace these with fetch() calls or
 * your preferred data-fetching strategy (SWR, React Query, etc.).
 */

import type {
  User,
  WeeklyVitalityStats,
  BattleRecord,
  Equipment,
  EquipmentSet,
  ShopItem,
  RankedHero,
  ChatMessage,
} from "@/lib/types";

// ─── Current User ─────────────────────────────────────────────────────────────

export const MOCK_USER: User = {
  id: "usr_001",
  username: "aventurero",
  displayName: "Aventurero",
  heroClass: "Guerrero",
  level: 42,
  xp: 2450,
  xpToNextLevel: 3000,
  gold: 1250,
  energy: 85,
  isOnline: true,
  createdAt: "2025-01-01T00:00:00Z",
};

// ─── Vitality ─────────────────────────────────────────────────────────────────

export const MOCK_VITALITY: WeeklyVitalityStats = {
  entries: [
    { date: "2026-03-04", weightLossKg: 2.5, activityMinutes: 45 },
    { date: "2026-03-05", weightLossKg: 2.2, activityMinutes: 60 },
    { date: "2026-03-06", weightLossKg: 2.8, activityMinutes: 75 },
    { date: "2026-03-07", weightLossKg: 2.0, activityMinutes: 50 },
    { date: "2026-03-08", weightLossKg: 3.1, activityMinutes: 80 },
    { date: "2026-03-09", weightLossKg: 2.7, activityMinutes: 65 },
    { date: "2026-03-10", weightLossKg: 2.4, activityMinutes: 55 },
  ],
  totalWeightLossKg: 17.7,
  avgActivityMinutes: 61,
};

// ─── Battles ──────────────────────────────────────────────────────────────────

export const MOCK_BATTLES: BattleRecord[] = [
  { id: "b1",  date: "2026-02-22", habitType: "Agua",    result: "Victoria", goldEarned: 150, xpEarned: 200, userId: "usr_001" },
  { id: "b2",  date: "2026-02-22", habitType: "Postura", result: "Victoria", goldEarned: 120, xpEarned: 180, userId: "usr_001" },
  { id: "b3",  date: "2026-02-21", habitType: "Mente",   result: "Victoria", goldEarned: 180, xpEarned: 250, userId: "usr_001" },
  { id: "b4",  date: "2026-02-21", habitType: "Agua",    result: "Derrota",  goldEarned: 50,  xpEarned: 75,  userId: "usr_001" },
  { id: "b5",  date: "2026-02-20", habitType: "Postura", result: "Victoria", goldEarned: 140, xpEarned: 190, userId: "usr_001" },
  { id: "b6",  date: "2026-02-20", habitType: "Mente",   result: "Victoria", goldEarned: 200, xpEarned: 280, userId: "usr_001" },
  { id: "b7",  date: "2026-02-19", habitType: "Agua",    result: "Victoria", goldEarned: 160, xpEarned: 210, userId: "usr_001" },
  { id: "b8",  date: "2026-02-19", habitType: "Postura", result: "Derrota",  goldEarned: 40,  xpEarned: 60,  userId: "usr_001" },
];

// ─── Equipment ────────────────────────────────────────────────────────────────

export const MOCK_EQUIPMENT: Equipment[] = [
  { id: "eq1", name: "Espada del Alba",    type: "weapon",    rarity: "legendary", stats: { power: 95 },    locked: false, iconName: "Sword"  },
  { id: "eq2", name: "Escudo de Titán",    type: "armor",     rarity: "epic",      stats: { defense: 85 },  locked: false, iconName: "Shield" },
  { id: "eq3", name: "Espada de Hierro",   type: "weapon",    rarity: "rare",      stats: { power: 65 },    locked: false, iconName: "Sword"  },
  { id: "eq4", name: "Escudo Guardián",    type: "armor",     rarity: "rare",      stats: { defense: 60 },  locked: false, iconName: "Shield" },
  { id: "eq5", name: "Amuleto de Vida",    type: "accessory", rarity: "epic",      stats: { health: 150 },  locked: false, iconName: "Heart"  },
  { id: "eq6", name: "Reliquia del Trueno",type: "accessory", rarity: "legendary", stats: { power: 80 },    locked: false, iconName: "Zap"    },
  { id: "eq7", name: "Espada Mística",     type: "weapon",    rarity: "common",    stats: { power: 45 },    locked: true,  iconName: "Sword"  },
  { id: "eq8", name: "Escudo Celestial",   type: "armor",     rarity: "legendary", stats: { defense: 100 }, locked: true,  iconName: "Shield" },
];

export const MOCK_EQUIPMENT_SET: EquipmentSet = {
  id: "set1",
  name: "Equipo Legendario Completo",
  bonus: "+25% a todas las estadísticas",
  equipmentIds: ["eq1", "eq2", "eq6"],
};

// ─── Shop ─────────────────────────────────────────────────────────────────────

export const MOCK_SHOP_ITEMS: ShopItem[] = [
  { id: "s1", name: "Espada Estelar",    description: "Una espada forjada con fragmentos de estrellas",   price: 2500, category: "weapon",    rarity: "legendary", stats: { power: 120 },   iconName: "Sword"      },
  { id: "s2", name: "Escudo Galáctico",  description: "Protección digna de un héroe espacial",            price: 2200, category: "armor",     rarity: "epic",      stats: { defense: 100 }, iconName: "Shield"     },
  { id: "s3", name: "Amuleto de Energía",description: "Aumenta tu vitalidad considerablemente",           price: 1800, category: "accessory", rarity: "epic",      stats: { health: 200 },  iconName: "Heart"      },
  { id: "s4", name: "Reliquia del Poder",description: "Potencia tus ataques con energía cósmica",        price: 3000, category: "accessory", rarity: "legendary", stats: { power: 90 },    iconName: "Zap"        },
  { id: "s5", name: "Espada de Batalla", description: "Un arma confiable para cualquier aventurero",      price: 800,  category: "weapon",    rarity: "rare",      stats: { power: 70 },    iconName: "Sword"      },
  { id: "s6", name: "Escudo Reforzado",  description: "Defensa sólida contra enemigos",                   price: 750,  category: "armor",     rarity: "rare",      stats: { defense: 65 },  iconName: "Shield"     },
  { id: "s7", name: "Boost de XP",       description: "Duplica XP ganado por 24 horas",                  price: 500,  category: "boost",     rarity: "rare",      stats: {},               iconName: "TrendingUp" },
  { id: "s8", name: "Boost de Oro",      description: "Aumenta oro ganado en un 50% por 24 horas",       price: 450,  category: "boost",     rarity: "rare",      stats: {},               iconName: "Award"      },
];

// ─── Ranking ──────────────────────────────────────────────────────────────────

export const MOCK_RANKING: RankedHero[] = [
  { rank: 1, userId: "usr_101", name: "DragonSlayer42", level: 89, gold: 45230, heroClass: "Guerrero", avatarEmoji: "🛡️" },
  { rank: 2, userId: "usr_102", name: "MysticHealer",   level: 87, gold: 42100, heroClass: "Sanador",  avatarEmoji: "⚕️" },
  { rank: 3, userId: "usr_103", name: "ShadowNinja",    level: 85, gold: 39800, heroClass: "Asesino",  avatarEmoji: "🥷" },
  { rank: 4, userId: "usr_104", name: "FireMage",       level: 82, gold: 36500, heroClass: "Mago",     avatarEmoji: "🔥" },
  { rank: 5, userId: "usr_105", name: "IronKnight",     level: 80, gold: 34200, heroClass: "Caballero",avatarEmoji: "⚔️" },
  { rank: 6, userId: "usr_106", name: "StormArcher",    level: 78, gold: 31900, heroClass: "Arquero",  avatarEmoji: "🏹" },
  { rank: 7, userId: "usr_107", name: "NatureWarden",   level: 75, gold: 29600, heroClass: "Druida",   avatarEmoji: "🌿" },
  { rank: 8, userId: "usr_108", name: "LightPaladin",   level: 73, gold: 27300, heroClass: "Paladín",  avatarEmoji: "✨" },
];

// ─── Oracle initial messages ───────────────────────────────────────────────────

export const MOCK_ORACLE_MESSAGES: ChatMessage[] = [
  {
    id: "msg_001",
    role: "oracle",
    text: "¡Saludos, valiente aventurero! Soy el Oráculo Gemini, tu guía en el camino hacia la salud y el bienestar. ¿En qué puedo asistirte hoy?",
    timestamp: new Date().toISOString(),
  },
];

// Oracle simulated replies — replace with real AI API call later
export const MOCK_ORACLE_REPLIES: string[] = [
  "¡Excelente pregunta! Basándome en tus estadísticas, te recomiendo incrementar tu actividad cardiovascular en un 15% esta semana.",
  "Veo que has progresado mucho. Para optimizar tu recuperación, considera agregar 30 minutos de estiramientos después de tu entrenamiento.",
  "Tu dedicación es admirable. Recuerda que el descanso es tan importante como el ejercicio. Asegúrate de dormir 7-8 horas.",
  "Consejo del oráculo: Mantén una hidratación constante. Tu cuerpo necesita al menos 2 litros de agua al día para rendir al máximo.",
];

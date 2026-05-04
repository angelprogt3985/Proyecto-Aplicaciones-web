import {
  doc,
  getDoc,
  setDoc,
  updateDoc,
  collection,
  addDoc,
  query,
  orderBy,
  limit,
  getDocs,
  serverTimestamp,
} from "firebase/firestore";
import { db, auth } from "@/lib/firebase";
import { ShopItem } from "./types";

// Leer datos del usuario actual
export async function loadUserData(): Promise<Record<string, any> | null> {
  const uid = auth.currentUser?.uid;
  if (!uid) return null;

  const snap = await getDoc(doc(db, "users", uid));
  return snap.exists() ? snap.data() : null;
}

// Guardar o actualizar datos del héroe
export async function saveUserData(data: {
  heroLevel:    number;
  heroXp:       number;
  heroGold:     number;
  heroHp:       number;
  totalXp:      number;
  heroMaxHP?:   number;
  displayName?: string;
  heroClass?:   string;
}): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;
  await setDoc(doc(db, "users", uid), data, { merge: true });
}

// Guardar una batalla
export async function saveBattle(data: {
  habitType:  string;
  result:     string;
  goldEarned: number;
  xpEarned:   number;
}): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;

  await addDoc(collection(db, "users", uid, "battles"), {
    ...data,
    date: serverTimestamp(),
  });
}

// Leer ranking global
export async function loadRanking(): Promise<Record<string, any>[]> {
  const q = query(
    collection(db, "users"),
    orderBy("totalXp", "desc"),
    limit(10)
  );
  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

// Leer batallas del usuario actual, ordenadas de más reciente a más antigua
export async function loadBattles(): Promise<Record<string, any>[]> {
  const uid = auth.currentUser?.uid;
  if (!uid) return [];

  const q = query(
    collection(db, "users", uid, "battles"),
    orderBy("date", "desc"),
    limit(50)
  );
  const snap = await getDocs(q);
  return snap.docs.map((d) => ({ id: d.id, ...d.data() }));
}

// Descontar oro del usuario al comprar en la tienda
export async function spendGold(
  amount: number,
  item: { id: string; name: string; description: string; category: string; rarity: string; stats: Record<string, number>; iconName: string }
): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;

  const userRef = doc(db, "users", uid);
  const snap = await getDoc(userRef);
  if (!snap.exists()) return;

  const currentGold: number = snap.data().heroGold ?? 0;
  if (currentGold < amount) return;

  await updateDoc(userRef, { heroGold: currentGold - amount });

  await addDoc(collection(db, "users", uid, "inventory"), {
    id:          item.id,
    name:        item.name,
    stat:        item.description,
    emoji:       "",
    price:       amount,
    purchasedAt: serverTimestamp(),
  });
}

// Leer inventario del usuario
export async function loadInventory(): Promise<string[]> {
  const uid = auth.currentUser?.uid;
  if (!uid) return [];

  const snap = await getDocs(collection(db, "users", uid, "inventory"));
  return snap.docs.map((d) => d.data().id as string);
}
// ─── Vitality Stats ───────────────────────────────────────────────────────────

export interface VitalityEntry {
  date: string;
  weightLossKg: number;
  activityMinutes: number;
}

export async function saveVitalityEntry(entry: VitalityEntry): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;
  await setDoc(
    doc(db, "users", uid, "vitality", entry.date),
    { ...entry },
    { merge: true }
  );
}

export async function loadWeeklyVitality(): Promise<VitalityEntry[]> {
  const uid = auth.currentUser?.uid;
  if (!uid) return [];

  const today = new Date();
  const dates: string[] = [];
  for (let i = 6; i >= 0; i--) {
    const d = new Date(today);
    d.setDate(d.getDate() - i);
    dates.push(d.toISOString().split("T")[0]);
  }

  const entries: VitalityEntry[] = [];
  for (const date of dates) {
    try {
      const snap = await getDoc(doc(db, "users", uid, "vitality", date));
      if (snap.exists()) {
        entries.push(snap.data() as VitalityEntry);
      } else {
        entries.push({ date, weightLossKg: 0, activityMinutes: 0 });
      }
    } catch {
      entries.push({ date, weightLossKg: 0, activityMinutes: 0 });
    }
  }
  return entries;
}

// Lee todos los documentos de shop_catalog y los convierte a ShopItem
export async function loadShopCatalog(): Promise<ShopItem[]> {
  const snap = await getDocs(collection(db, "shop_catalog"));
  return snap.docs.map((d) => {
    const data = d.data();
    return {
      icon:       null,
      id:         d.id,
      name:       data.name        ?? "",
      description: data.description ?? data.stat ?? "",
      price:      data.price       ?? 0,
      category:   data.category    ?? "accessory",
      rarity:     data.rarity      ?? "common",
      stats: {
        power:   data.bonusPower > 0 ? data.bonusPower : undefined,
        health:  data.bonusHp    > 0 ? data.bonusHp    : undefined,
      },
      iconName:   data.iconName    ?? "Sword",
      bonusHp:    data.bonusHp     ?? 0,
      bonusPower: data.bonusPower  ?? 0,
    } as ShopItem;
  });
}

// Carga el inventario actualizado del usuario
export async function loadInventoryFull(): Promise<Array<{ id: string; name: string; stat: string; price: number }>> {
  const uid = auth.currentUser?.uid;
  if (!uid) return [];

  const snap = await getDocs(collection(db, "users", uid, "inventory"));
  return snap.docs.map((d) => ({
    id:    d.data().id    as string,
    name:  d.data().name  as string,
    stat:  d.data().stat  as string,
    price: d.data().price as number,
  }));
}


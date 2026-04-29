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
import { ShopItemCategory, EquipmentRarity } from "./types";

// Leer datos del usuario actual
export async function loadUserData(): Promise<Record<string, any> | null> {
  const uid = auth.currentUser?.uid;
  if (!uid) return null;

  const snap = await getDoc(doc(db, "users", uid));
  return snap.exists() ? snap.data() : null;
}

// Guardar o actualizar datos del héroe
export async function saveUserData(data: {
  heroLevel: number;
  heroXp:    number;
  heroGold:  number;
  heroHp:    number;
  totalXp:   number;
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
export async function spendGold(amount: number, p0: { id: string; name: string; description: string; category: ShopItemCategory; rarity: EquipmentRarity; stats: Record<string, number>; iconName: string; }): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;

  const userRef = doc(db, "users", uid);
  const snap    = await getDoc(userRef);
  if (!snap.exists()) return;

  const currentGold: number = snap.data().heroGold ?? 0;
  if (currentGold < amount) return;

  await updateDoc(userRef, { heroGold: currentGold - amount });
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
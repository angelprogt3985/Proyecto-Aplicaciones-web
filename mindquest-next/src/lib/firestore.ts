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
export async function spendGold(amount: number): Promise<void> {
  const uid = auth.currentUser?.uid;
  if (!uid) return;

  const userRef = doc(db, "users", uid);
  const snap    = await getDoc(userRef);
  if (!snap.exists()) return;

  const currentGold: number = snap.data().heroGold ?? 0;
  if (currentGold < amount) return;

  await updateDoc(userRef, { heroGold: currentGold - amount });
}
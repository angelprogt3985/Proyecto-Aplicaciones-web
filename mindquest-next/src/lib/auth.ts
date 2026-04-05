import {
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  updateProfile,
} from "firebase/auth";
import { doc, getDoc, getDocs, collection, query, where, setDoc } from "firebase/firestore";
import { auth, db } from "@/lib/firebase";

// Login 
export async function login(email: string, password: string): Promise<void> {
  await signInWithEmailAndPassword(auth, email, password);
}

// Registro
export async function register(
  email:       string,
  password:    string,
  displayName: string
): Promise<void> {
  const name = displayName.trim() || "Guerrero";

  const nameTaken = await isDisplayNameTaken(name);
  if (nameTaken) {
    throw new Error("Ese nombre de héroe ya está en uso. Elige otro.");
  }

  const result = await createUserWithEmailAndPassword(auth, email, password);

  await updateProfile(result.user, { displayName: name });

  await setDoc(doc(db, "users", result.user.uid), {
    displayName: name,
    email:       email,
    heroLevel:   1,
    heroXp:      0,
    heroGold:    0,
    heroHp:      100,
    totalXp:     0,
  });
}

// Logout
export async function logout(): Promise<void> {
  await signOut(auth);
}

// Verificar nombre de héroe
async function isDisplayNameTaken(displayName: string): Promise<boolean> {
  const q = query(
    collection(db, "users"),
    where("displayName", "==", displayName)
  );
  const snap = await getDocs(q);
  return !snap.empty;
}
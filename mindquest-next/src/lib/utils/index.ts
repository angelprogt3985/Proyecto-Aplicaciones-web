import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import type { EquipmentRarity } from "@/lib/types";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString("es-ES", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });
}

export function formatNumber(n: number): string {
  return n.toLocaleString("es-ES");
}

export const RARITY_STYLES: Record<
  EquipmentRarity,
  { bg: string; border: string; text: string; stars: number }
> = {
  common:    { bg: "bg-mq-card2",                border: "border-[#4a5568]",              text: "text-mq-muted",   stars: 2 },
  rare:      { bg: "bg-[#1e3a5f]",               border: "border-mq-blue/40",             text: "text-mq-blue",   stars: 3 },
  epic:      { bg: "bg-[#3d2d5f]",               border: "border-mq-purple/40",           text: "text-mq-purple", stars: 4 },
  legendary: { bg: "bg-[#3d3020]",               border: "border-mq-gold/40",             text: "text-mq-gold",   stars: 5 },
};

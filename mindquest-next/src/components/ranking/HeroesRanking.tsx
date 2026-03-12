import { Trophy, Crown, Medal, Award } from "lucide-react";
import type { RankedHero } from "@/lib/types";

interface HeroesRankingProps {
  heroes: RankedHero[];
}

function RankBadge({ rank }: { rank: number }) {
  if (rank === 1) return <Crown className="h-5 w-5 text-mq-gold" />;
  if (rank === 2) return <Medal className="h-5 w-5 text-mq-muted" />;
  if (rank === 3) return <Award className="h-5 w-5 text-[#cd9575]" />;
  return <span className="text-sm text-mq-muted">#{rank}</span>;
}

export function HeroesRanking({ heroes }: HeroesRankingProps) {
  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue/25 bg-mq-card p-6 shadow-lg">
      {/* Glow */}
      <div className="pointer-events-none absolute bottom-0 left-0 h-48 w-48 rounded-full bg-mq-gold opacity-12 blur-[90px]" />

      <div className="relative mb-5 flex items-center justify-between">
        <div>
          <h2 className="flex items-center gap-2 text-lg font-semibold text-mq-text">
            <Trophy className="h-5 w-5 text-mq-gold" />
            Ranking Global de Héroes
          </h2>
          <p className="mt-0.5 text-sm text-mq-muted">
            Los mejores aventureros de la semana
          </p>
        </div>
        {/* TODO: link to /ranking full page */}
        <button className="rounded-xl bg-mq-blue px-4 py-2 text-sm font-semibold text-mq-bg shadow-md shadow-mq-blue/25 transition-colors hover:bg-mq-blue2">
          Ver Todos
        </button>
      </div>

      <div className="relative overflow-hidden rounded-xl border border-mq-blue/20">
        <table className="w-full border-collapse">
          <thead className="border-b border-mq-blue/20 bg-mq-bg text-[11px] uppercase tracking-wider text-mq-muted">
            <tr>
              <th className="px-4 py-3 text-left">Rank</th>
              <th className="px-4 py-3 text-left">Héroe</th>
              <th className="px-4 py-3 text-left">Clase</th>
              <th className="px-4 py-3 text-left">Nivel</th>
              <th className="px-4 py-3 text-left">Oro</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-mq-blue/10">
            {heroes.map((hero) => (
              <tr
                key={hero.userId}
                className="cursor-pointer transition-colors hover:bg-mq-bg"
              >
                <td className="px-4 py-3.5">
                  <div className="flex w-7 items-center justify-center">
                    <RankBadge rank={hero.rank} />
                  </div>
                </td>
                <td className="px-4 py-3.5">
                  <div className="flex items-center gap-3">
                    <div className="flex h-9 w-9 flex-shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-mq-blue to-mq-blue2 text-lg shadow-sm shadow-mq-blue/25">
                      {hero.avatarEmoji}
                    </div>
                    <span className="text-sm text-mq-text">{hero.name}</span>
                  </div>
                </td>
                <td className="px-4 py-3.5">
                  <span className="rounded-full border border-mq-blue/30 bg-mq-card2 px-3 py-0.5 text-xs text-mq-blue">
                    {hero.heroClass}
                  </span>
                </td>
                <td className="px-4 py-3.5">
                  <div className="flex items-center gap-2">
                    <div className="h-1.5 w-20 overflow-hidden rounded-full bg-mq-bg">
                      <div
                        className="h-full rounded-full bg-gradient-to-r from-mq-blue to-mq-gold"
                        style={{ width: `${hero.level}%` }}
                      />
                    </div>
                    <span className="min-w-[28px] text-sm text-mq-text">
                      {hero.level}
                    </span>
                  </div>
                </td>
                <td className="px-4 py-3.5">
                  <div className="flex items-center gap-1.5 text-sm">
                    <span>🪙</span>
                    <span className="text-mq-text">
                      {hero.gold.toLocaleString()}
                    </span>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

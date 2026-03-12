import type { User } from "@/lib/types";

interface WelcomeBannerProps {
  user: User;
  weeklyBattles: number;
}

export function WelcomeBanner({ user, weeklyBattles }: WelcomeBannerProps) {
  const remaining = user.xpToNextLevel - user.xp;

  return (
    <div className="relative overflow-hidden rounded-xl border border-mq-blue bg-gradient-to-r from-mq-blue to-mq-blue2 p-6 shadow-2xl shadow-mq-blue/25">
      {/* Inner vignette */}
      <div className="pointer-events-none absolute inset-0 bg-gradient-to-br from-transparent to-black/10" />

      <div className="relative flex items-center justify-between gap-6">
        <div>
          <h1 className="mb-1.5 text-2xl font-bold text-mq-bg">
            ¡Bienvenido de vuelta, {user.displayName}!
          </h1>
          <p className="text-base text-mq-bg/75">
            Has completado{" "}
            <span className="font-semibold">{weeklyBattles} combates</span>{" "}
            esta semana. ¡Sigue así para desbloquear tu próxima recompensa!
          </p>
        </div>

        <div className="flex-shrink-0 rounded-xl border-2 border-mq-gold bg-mq-bg px-6 py-3 text-center shadow-lg">
          <p className="text-xs text-mq-gold">Próximo Nivel</p>
          <p className="mt-0.5 text-2xl font-bold text-mq-text">
            {remaining.toLocaleString()} XP
          </p>
        </div>
      </div>
    </div>
  );
}

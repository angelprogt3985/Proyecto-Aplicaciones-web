"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { login } from "@/lib/auth";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError]       = useState<string | null>(null);

  const handleLogin = async () => {
    if (!email.trim() || !password.trim()) {
      setError("Completa todos los campos");
      return;
    }
    setIsLoading(true);
    setError(null);
    try {
      await login(email, password);
      router.push("/");
    } catch (e: any) {
      setError(mapError(e.message));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="relative flex h-screen items-center justify-center overflow-hidden bg-mq-bg">

      {/* Ambient glows */}
      <div className="pointer-events-none fixed inset-0 z-0">
        <div className="absolute right-1/4 top-1/4 h-96 w-96 rounded-full bg-mq-blue opacity-[0.07] blur-[150px]" />
        <div className="absolute bottom-1/4 left-1/4 h-96 w-96 rounded-full bg-mq-gold opacity-[0.07] blur-[150px]" />
      </div>

      {/* Card */}
      <div className="relative z-10 w-full max-w-md rounded-2xl border border-mq-gold/20 bg-mq-card p-10 shadow-2xl">

        {/* Logo */}
        <div className="mb-8 text-center">
          <p className="text-3xl font-black tracking-tight">
            <span className="text-mq-gold">Winni</span>
            <span className="text-mq-text">Knight</span>
          </p>
          <p className="mt-1 text-sm text-mq-muted">Inicia sesion para continuar tu aventura</p>
        </div>

        {/* Fields */}
        <div className="flex flex-col gap-4">
          <div>
            <label className="mb-1.5 block text-xs font-semibold uppercase tracking-wide text-mq-muted">
              Correo electronico
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
              placeholder="heroe@galaxia.com"
              className="w-full rounded-xl border border-mq-gold/20 bg-mq-bg px-4 py-3 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-gold"
            />
          </div>

          <div>
            <label className="mb-1.5 block text-xs font-semibold uppercase tracking-wide text-mq-muted">
              Contrasena
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleLogin()}
              placeholder="••••••••"
              className="w-full rounded-xl border border-mq-gold/20 bg-mq-bg px-4 py-3 text-sm text-mq-text placeholder-mq-muted outline-none transition-colors focus:border-mq-gold"
            />
          </div>

          {/* Error */}
          {error && (
            <p className="rounded-xl border border-red-500/30 bg-red-500/10 px-4 py-2.5 text-sm text-red-400">
              {error}
            </p>
          )}

          {/* Login button */}
          <button
            onClick={handleLogin}
            disabled={isLoading}
            className="mt-2 w-full rounded-xl bg-gradient-to-r from-mq-gold to-mq-gold2 py-3 text-sm font-black uppercase tracking-wide text-mq-bg shadow-lg shadow-mq-gold/20 transition-opacity hover:opacity-85 disabled:opacity-50"
          >
            {isLoading ? "Iniciando sesion..." : "Iniciar Sesion"}
          </button>

          {/* Register link */}
          <p className="text-center text-sm text-mq-muted">
            No tienes cuenta?{" "}
            <button
              onClick={() => router.push("/register")}
              className="font-semibold text-mq-gold hover:underline"
            >
              Registrate aqui
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}

function mapError(message: string): string {
  if (!message) return "Ocurrio un error inesperado";
  if (message.includes("no user record"))           return "No existe una cuenta con ese correo";
  if (message.includes("password is invalid") ||
      message.includes("INVALID_LOGIN_CREDENTIALS")) return "Correo o contrasena incorrectos";
  if (message.includes("badly formatted"))           return "Formato de correo invalido";
  if (message.includes("network error"))             return "Error de red. Verifica tu conexion";
  if (message.includes("too-many-requests"))         return "Demasiados intentos. Intenta mas tarde";
  return "Error: " + message;
}
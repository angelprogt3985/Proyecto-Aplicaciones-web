"use client";

export function GuideSection() {
  return (
    <div className="flex flex-col gap-8 max-w-4xl mx-auto pb-10">

      {/* ── HEADER ── */}
      <div className="relative overflow-hidden rounded-xl border border-mq-gold/30 bg-mq-card p-8 text-center shadow-lg">
        <div className="pointer-events-none absolute right-1/4 top-0 h-64 w-64 rounded-full bg-mq-gold opacity-10 blur-[100px]" />
        <div className="pointer-events-none absolute left-1/4 bottom-0 h-64 w-64 rounded-full bg-mq-blue opacity-10 blur-[100px]" />
        <p className="relative text-xs uppercase tracking-widest text-mq-gold mb-3">✦ Documentación Oficial ✦</p>
        <h1 className="relative font-bold text-3xl text-mq-text mb-2">
          Guía de Usuario — <span className="text-mq-gold">Winni</span><span className="text-mq-blue">Knight</span>
        </h1>
        <p className="relative text-mq-muted text-sm">
          Todo lo que necesitas saber para usar la plataforma web.
        </p>
      </div>

      {/* ── 1. ACCESO ── */}
      <Section icon="🔐" title="Acceso a la Plataforma" subtitle="Cómo crear tu cuenta e iniciar sesión" color="gold">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Card borderColor="border-mq-gold/25">
            <h3 className="text-mq-gold font-semibold mb-4">Registrarse por primera vez</h3>
            <Steps color="gold" items={[
              { title: "Ingresa al sitio web", desc: "Abre la URL de WinniKnight. Serás redirigido a la pantalla de login automáticamente." },
              { title: 'Haz clic en "Regístrate aquí"', desc: "Enlace al pie del formulario de inicio de sesión." },
              { title: "Completa el formulario", desc: "Nombre de Héroe (único, visible en ranking), correo electrónico, y contraseña de mínimo 6 caracteres." },
              { title: 'Presiona "Crear Cuenta"', desc: "Si todo está correcto, entrarás directo al dashboard. También puedes presionar Enter." },
            ]} />
          </Card>
          <Card borderColor="border-mq-blue/25">
            <h3 className="text-mq-blue font-semibold mb-4">Ya tengo cuenta — Iniciar sesión</h3>
            <Steps color="blue" items={[
              { title: "Ingresa tu correo", desc: "El mismo con el que creaste tu cuenta." },
              { title: "Ingresa tu contraseña", desc: "Puedes presionar Enter para iniciar sin hacer clic." },
              { title: 'Presiona "Iniciar Sesión"', desc: "El sistema cargará tus datos desde Firebase y te llevará al dashboard." },
            ]} />
            <Tip>
              <strong>Errores comunes:</strong> "Correo o contraseña incorrectos" → revisa mayúsculas. "Demasiados intentos" → espera unos minutos.
            </Tip>
          </Card>
        </div>
      </Section>

      {/* ── 2. INTERFAZ ── */}
      <Section icon="🗺️" title="Interfaz Principal" subtitle="Las tres zonas de la pantalla" color="blue">
        <div className="flex flex-col gap-3">
          <Card>
            <div className="flex items-start gap-3">
              <span className="text-2xl">📌</span>
              <div>
                <h3 className="font-semibold text-mq-text mb-1">Barra lateral (Sidebar)</h3>
                <p className="text-mq-muted text-sm">Panel izquierdo con tu nombre, nivel y botones de navegación. En la parte inferior siempre ves tu oro y el botón para cerrar sesión.</p>
              </div>
            </div>
          </Card>
          <Card>
            <div className="flex items-start gap-3">
              <span className="text-2xl">🔝</span>
              <div>
                <h3 className="font-semibold text-mq-text mb-1">Barra superior (TopBar)</h3>
                <p className="text-mq-muted text-sm">Franja horizontal arriba. Muestra el nombre de la sección activa y un resumen de tu nivel, XP y oro.</p>
              </div>
            </div>
          </Card>
          <Card>
            <div className="flex items-start gap-3">
              <span className="text-2xl">📄</span>
              <div>
                <h3 className="font-semibold text-mq-text mb-1">Área de contenido (Main)</h3>
                <p className="text-mq-muted text-sm">La parte central que cambia según la sección seleccionada. Aquí aparece todo el contenido del juego.</p>
              </div>
            </div>
          </Card>
        </div>
      </Section>

      {/* ── 3. DASHBOARD ── */}
      <Section icon="📊" title="Dashboard" subtitle="Tu pantalla de inicio con resumen completo" color="blue">
        <p className="text-mq-muted text-sm mb-4">Al iniciar sesión entras directo al Dashboard. Es un resumen con todos los módulos juntos, en este orden:</p>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
          {[
            { e: "🎉", t: "Banner de Bienvenida", d: "Saludo con tu nombre y combates de la semana." },
            { e: "📈", t: "Estadísticas de Vitalidad", d: "Gráfica de barras con peso y actividad física." },
            { e: "🔮", t: "Oráculo IA", d: "Chat rápido con el consejero de salud (Gemini)." },
            { e: "👑", t: "Ranking de Héroes", d: "Vista previa de los mejores aventureros." },
            { e: "🛡️", t: "Inventario de Equipo", d: "Piezas compradas en cuadrícula." },
            { e: "⚔️", t: "Historial de Combates", d: "Tabla con todos tus combates registrados." },
            { e: "🪙", t: "Tienda de Oro", d: "Ítems disponibles para comprar." },
          ].map(({ e, t, d }) => (
            <Card key={t}>
              <div className="text-2xl mb-2">{e}</div>
              <h3 className="text-sm font-semibold text-mq-text mb-1">{t}</h3>
              <p className="text-xs text-mq-muted">{d}</p>
            </Card>
          ))}
        </div>
        <Tip>Los datos se cargan automáticamente desde Firebase al iniciar sesión. No necesitas hacer nada.</Tip>
      </Section>

      {/* ── 4. VITALIDAD ── */}
      <Section icon="💪" title="Estadísticas de Vitalidad" subtitle="Registra tu progreso físico semanal" color="teal">
        <Card>
          <p className="text-mq-muted text-sm mb-5">Muestra una gráfica de barras con dos métricas de la semana. En la esquina superior derecha hay dos tarjetas de resumen: <span className="text-mq-blue font-medium">Peso total perdido (kg)</span> y <span className="text-mq-gold font-medium">Promedio de actividad (min)</span>.</p>
          <h3 className="font-semibold text-mq-text mb-4">📝 Cómo registrar tu día</h3>
          <Steps color="blue" items={[
            { title: 'Haz clic en "+ Registrar Hoy"', desc: "Botón azul en la esquina superior derecha del módulo." },
            { title: "Llena los dos campos", desc: "Pérdida de peso en kg (ej: 0.3) y minutos de actividad física (ej: 45)." },
            { title: 'Presiona "Guardar"', desc: 'El dato se guarda en Firebase y la gráfica se actualiza. Verás "¡Guardado!" brevemente.' },
          ]} />
        </Card>
        <TipGold>Solo puedes registrar <strong>un dato por día</strong>. La gráfica muestra de Lunes a Domingo de la semana actual.</TipGold>
      </Section>

      {/* ── 5. COMBATES ── */}
      <Section icon="⚔️" title="Historial de Combates" subtitle="Revisa y filtra todos tus combates" color="blue">
        <Card>
          <p className="text-mq-muted text-sm mb-4">Los combates se generan desde la <strong className="text-mq-text">app Android</strong>. En la web puedes ver el historial completo con filtros.</p>
          <div className="grid grid-cols-2 gap-4 mb-5">
            <div>
              <p className="text-sm text-mq-muted"><strong className="text-mq-text">Por fecha:</strong> menú desplegable con todas las fechas disponibles.</p>
            </div>
            <div>
              <p className="text-sm text-mq-muted"><strong className="text-mq-text">Por hábito:</strong> botones Todos / Agua / Postura / Mente.</p>
            </div>
          </div>
          <h3 className="font-semibold text-mq-text mb-3">Tipos de hábito</h3>
          <div className="flex flex-col gap-2">
            {[
              { color: "text-mq-blue",   bg: "bg-[#1e3a5f]", border: "border-mq-blue/40",   e: "💧", t: "Agua",    d: "Hábito de hidratación" },
              { color: "text-mq-gold",   bg: "bg-[#3d3020]", border: "border-mq-gold/40",   e: "🧍", t: "Postura", d: "Hábito de buena postura" },
              { color: "text-mq-purple", bg: "bg-[#3d2d5f]", border: "border-mq-purple/40", e: "🧠", t: "Mente",   d: "Salud mental o meditación" },
              { color: "text-mq-muted",  bg: "bg-[#2a1a3d]", border: "border-mq-purple/40", e: "⚔️", t: "Combate", d: "Combate genérico o contra jefes IA" },
            ].map(({ color, bg, border, e, t, d }) => (
              <div key={t} className={`flex items-center gap-3 rounded-xl border ${border} ${bg} px-4 py-2.5`}>
                <span>{e}</span>
                <span className={`text-sm font-medium ${color}`}>{t}</span>
                <span className="text-xs text-mq-muted ml-auto">{d}</span>
              </div>
            ))}
          </div>
        </Card>
      </Section>

      {/* ── 6. INVENTARIO ── */}
      <Section icon="🛡️" title="Inventario de Equipo" subtitle="Visualiza las piezas que ya compraste" color="purple">
        <Card>
          <p className="text-mq-muted text-sm mb-4">Muestra todas las piezas compradas en la Tienda, organizadas en una cuadrícula de hasta <strong className="text-mq-text">12 espacios</strong>. Cada pieza muestra nombre, rareza (estrellas) y estadísticas.</p>
          <h3 className="font-semibold text-mq-text mb-3">Niveles de rareza</h3>
          <div className="flex flex-wrap gap-2">
            <span className="px-3 py-1.5 rounded-xl text-sm font-medium bg-[#2d3548] text-[#a0aec0] border border-[#4a5568]">⭐⭐ Común</span>
            <span className="px-3 py-1.5 rounded-xl text-sm font-medium bg-[#1e3a5f] text-mq-blue border border-mq-blue/40">⭐⭐⭐ Raro</span>
            <span className="px-3 py-1.5 rounded-xl text-sm font-medium bg-[#3d2d5f] text-mq-purple border border-mq-purple/40">⭐⭐⭐⭐ Épico</span>
            <span className="px-3 py-1.5 rounded-xl text-sm font-medium bg-[#3d3020] text-mq-gold border border-mq-gold/40">⭐⭐⭐⭐⭐ Legendario</span>
          </div>
        </Card>
        <Tip>Si el inventario está vacío, aún no has comprado nada. Ve a <strong>Inventario → Tienda de Oro</strong>.</Tip>
      </Section>

      {/* ── 7. TIENDA ── */}
      <Section icon="🪙" title="Tienda de Oro & Equipo" subtitle="Gasta tu oro para mejorar tu héroe" color="gold">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Card borderColor="border-mq-gold/25">
            <h3 className="text-mq-gold font-semibold mb-4">Cómo comprar</h3>
            <Steps color="gold" items={[
              { title: "Filtra por categoría", desc: "Botones: Todo, Armas, Armaduras, Accesorios, Potenciadores." },
              { title: "Verifica tu oro", desc: 'Si no tienes suficiente, el botón dirá "Sin Oro" y estará bloqueado.' },
              { title: 'Presiona "Comprar"', desc: "El oro se deduce y el ítem pasa a tu inventario. No se puede deshacer." },
            ]} />
          </Card>
          <Card>
            <h3 className="text-mq-text font-semibold mb-4">Categorías disponibles</h3>
            <div className="flex flex-col gap-2">
              {[
                { e: "⚔️", t: "Armas",         d: "Aumentan Poder" },
                { e: "🛡️", t: "Armaduras",     d: "Aumentan Defensa" },
                { e: "❤️", t: "Accesorios",    d: "Aumentan Vida" },
                { e: "⚡", t: "Potenciadores", d: "Boosts mixtos de estadísticas" },
              ].map(({ e, t, d }) => (
                <div key={t} className="flex items-center gap-3 rounded-xl border border-mq-blue/20 bg-mq-bg px-4 py-2.5">
                  <span>{e}</span>
                  <span className="text-sm text-mq-text font-medium">{t}</span>
                  <span className="text-xs text-mq-muted ml-auto">{d}</span>
                </div>
              ))}
            </div>
          </Card>
        </div>
        <TipGold><strong>¿Cómo consigo más oro?</strong> Ganando combates desde la app Android. Cuantos más combates ganes, más oro acumulas.</TipGold>
      </Section>

      {/* ── 8. ORÁCULO ── */}
      <Section icon="✨" title="Oráculo Gemini" subtitle="Consejero de salud con inteligencia artificial" color="gold">
        <Card borderColor="border-mq-gold/25">
          <p className="text-mq-muted text-sm mb-5">Chat con IA (Google Gemini) enfocado en salud, bienestar y hábitos. Disponible en el Dashboard y en su propia sección.</p>
          <Steps color="gold" items={[
            { title: "Escribe tu pregunta", desc: "Cualquier consulta sobre salud, ejercicio, alimentación o hábitos en el campo de texto." },
            { title: "Envía tu mensaje", desc: 'Presiona el botón ✈️ o pulsa Enter. Verás "El Oráculo consulta el cosmos..." mientras responde.' },
            { title: "Lee la respuesta", desc: "Las respuestas del Oráculo aparecen con ✨ en el lado izquierdo. Tus mensajes en azul a la derecha." },
          ]} />
        </Card>
        <Tip>El chat no guarda el historial entre sesiones. Cada vez que entras, comienza con los mensajes de bienvenida.</Tip>
      </Section>

      {/* ── 9. RANKING ── */}
      <Section icon="👑" title="Ranking Global de Héroes" subtitle="Compara tu progreso con los demás" color="gold">
        <Card>
          <p className="text-mq-muted text-sm mb-4">Todos los jugadores ordenados por nivel y oro. El top 3 tiene íconos especiales:</p>
          <div className="flex gap-3 mb-5">
            {[["👑","1er lugar"],["🥈","2do lugar"],["🥉","3er lugar"],["#N","Resto"]].map(([e,l]) => (
              <div key={l} className="flex-1 rounded-xl border border-mq-blue/20 bg-mq-bg p-3 text-center">
                <div className="text-2xl mb-1">{e}</div>
                <div className="text-xs text-mq-muted">{l}</div>
              </div>
            ))}
          </div>
          <div className="flex flex-col gap-2">
            {[
              ["Rank","Posición en el ranking global"],
              ["Héroe","Avatar y nombre de héroe"],
              ["Clase","Clase seleccionada en el perfil"],
              ["Nivel","Nivel actual con barra de progreso"],
              ["Oro","Total de oro acumulado"],
            ].map(([col, desc]) => (
              <div key={col} className="flex items-center gap-3 rounded-xl border border-mq-blue/20 bg-mq-bg px-4 py-2.5">
                <span className="text-sm font-semibold text-mq-text w-16">{col}</span>
                <span className="text-sm text-mq-muted">{desc}</span>
              </div>
            ))}
          </div>
        </Card>
        <Tip>Para subir posiciones, gana más combates desde la app Android y acumula más oro y XP.</Tip>
      </Section>

      {/* ── 10. PERFIL ── */}
      <Section icon="🧙" title="Perfil de Héroe" subtitle="Personaliza tu personaje" color="blue">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <Card>
            <h3 className="font-semibold text-mq-text mb-3">Información mostrada</h3>
            <ul className="flex flex-col gap-2 text-sm text-mq-muted">
              {["Avatar (emoji de tu clase)","Nombre de héroe editable","Nivel y clase actual","Barra de XP con progreso","Stats: Nivel, Oro, Energía, Clase"].map(i => (
                <li key={i} className="flex items-center gap-2"><span className="text-mq-blue">·</span>{i}</li>
              ))}
            </ul>
          </Card>
          <Card borderColor="border-mq-gold/25">
            <h3 className="text-mq-gold font-semibold mb-4">Cómo editar tu perfil</h3>
            <Steps color="gold" items={[
              { title: 'Clic en "Editar"', desc: "Botón azul en la esquina superior derecha de tu tarjeta." },
              { title: "Cambia tu nombre", desc: "El campo se vuelve editable." },
              { title: "Elige tu clase", desc: "Aparece cuadrícula con 8 clases disponibles." },
              { title: 'Clic en "Guardar"', desc: "Los cambios se guardan en Firebase." },
            ]} />
          </Card>
        </div>
        <Card>
          <h3 className="font-semibold text-mq-text mb-3">Clases de héroe disponibles</h3>
          <div className="flex flex-wrap gap-2">
            {[["⚔️","Guerrero"],["⚕️","Sanador"],["🥷","Asesino"],["🔮","Mago"],["🛡️","Caballero"],["🏹","Arquero"],["🌿","Druida"],["✨","Paladín"]].map(([e,n]) => (
              <span key={n} className="flex items-center gap-1.5 rounded-xl border border-mq-blue/20 bg-mq-bg px-3 py-2 text-sm text-mq-text">
                {e} {n}
              </span>
            ))}
          </div>
        </Card>
      </Section>

      {/* ── 11. AJUSTES ── */}
      <Section icon="⚙️" title="Ajustes" subtitle="Personaliza la experiencia" color="blue">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-4">
          {[
            { e: "🔔", t: "Notificaciones", d: "Activa o desactiva alertas del juego." },
            { e: "🌙", t: "Modo Oscuro",    d: "Interfaz oscura por defecto." },
            { e: "🌐", t: "Idioma",         d: "Español, English o Português." },
            { e: "🛡️", t: "Privacidad",    d: "Público, Amigos o Privado." },
          ].map(({ e, t, d }) => (
            <Card key={t}>
              <div className="text-2xl mb-2">{e}</div>
              <h3 className="text-sm font-semibold text-mq-text mb-1">{t}</h3>
              <p className="text-xs text-mq-muted">{d}</p>
            </Card>
          ))}
        </div>
        <div className="flex items-start gap-3 rounded-xl border border-red-500/25 bg-red-500/5 px-5 py-4 text-sm text-red-400">
          <span className="text-xl flex-shrink-0">⚠️</span>
          <div><strong>Zona de Peligro:</strong> El botón <strong>"Eliminar cuenta"</strong> es permanente e irreversible. Perderás todos tus datos, combates e inventario.</div>
        </div>
        <Tip>Siempre presiona <strong>"Guardar Cambios"</strong> al terminar para que los ajustes se apliquen.</Tip>
      </Section>

      {/* ── CERRAR SESIÓN ── */}
      <Section icon="🚪" title="Cerrar Sesión" subtitle="Cómo salir de forma segura" color="blue">
        <Card>
          <p className="text-mq-muted text-sm">En la parte inferior de la barra lateral encontrarás el botón <strong className="text-red-400">Salir</strong>. Al hacer clic, la sesión se cierra y serás redirigido al login. Tus datos siempre están guardados en la nube — no perderás nada.</p>
        </Card>
      </Section>

    </div>
  );
}

// ── Componentes internos de utilidad ──────────────────────────────────────────

function Section({ icon, title, subtitle, color, children }: {
  icon: string; title: string; subtitle: string;
  color: "gold" | "blue" | "purple" | "teal";
  children: React.ReactNode;
}) {
  const borderMap = { gold: "border-mq-gold/30", blue: "border-mq-blue/25", purple: "border-mq-purple/25", teal: "border-[rgba(57,208,192,0.25)]" };
  const bgMap     = { gold: "bg-mq-gold/10",     blue: "bg-mq-blue/10",     purple: "bg-mq-purple/10",     teal: "bg-[rgba(57,208,192,0.1)]" };
  const textMap   = { gold: "text-mq-gold",       blue: "text-mq-blue",      purple: "text-mq-purple",      teal: "text-[#39d0c0]" };
  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center gap-4">
        <div className={`w-11 h-11 rounded-xl flex items-center justify-center text-xl flex-shrink-0 border ${borderMap[color]} ${bgMap[color]}`}>
          {icon}
        </div>
        <div>
          <h2 className={`text-xl font-bold ${textMap[color]}`}>{title}</h2>
          <p className="text-xs text-mq-muted">{subtitle}</p>
        </div>
      </div>
      {children}
    </div>
  );
}

function Card({ children, borderColor = "border-mq-blue/20" }: { children: React.ReactNode; borderColor?: string }) {
  return (
    <div className={`rounded-xl border ${borderColor} bg-mq-card p-5`}>
      {children}
    </div>
  );
}

function Steps({ items, color }: { items: { title: string; desc: string }[]; color: "gold" | "blue" }) {
  const numBg   = color === "gold" ? "bg-mq-gold/15 border-mq-gold text-mq-gold" : "bg-mq-blue/12 border-mq-blue text-mq-blue";
  const lineBg  = "bg-mq-blue/20";
  return (
    <div className="flex flex-col gap-0">
      {items.map(({ title, desc }, i) => (
        <div key={i} className="flex gap-4 pb-5 last:pb-0">
          <div className="flex flex-col items-center flex-shrink-0 w-8">
            <div className={`w-8 h-8 rounded-full border flex items-center justify-center text-xs font-bold flex-shrink-0 ${numBg}`}>
              {i + 1}
            </div>
            {i < items.length - 1 && <div className={`flex-1 w-px mt-1 ${lineBg}`} />}
          </div>
          <div className="pt-1 flex-1">
            <h4 className="text-sm font-semibold text-mq-text mb-1">{title}</h4>
            <p className="text-sm text-mq-muted">{desc}</p>
          </div>
        </div>
      ))}
    </div>
  );
}

function Tip({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex items-start gap-3 rounded-xl border border-mq-blue/20 bg-mq-blue/5 px-4 py-3 text-sm text-mq-muted">
      <span className="text-lg flex-shrink-0">💡</span>
      <div>{children}</div>
    </div>
  );
}

function TipGold({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex items-start gap-3 rounded-xl border border-mq-gold/25 bg-mq-gold/5 px-4 py-3 text-sm text-mq-muted">
      <span className="text-lg flex-shrink-0">🏅</span>
      <div>{children}</div>
    </div>
  );
}
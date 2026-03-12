import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: "class",
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        "mq-bg": "#1a1f2e",
        "mq-card": "#242b3d",
        "mq-card2": "#2d3548",
        "mq-blue": "#58a6ff",
        "mq-blue2": "#6eb5ff",
        "mq-gold": "#e0b35e",
        "mq-gold2": "#d4a855",
        "mq-text": "#f0f6fc",
        "mq-muted": "#a0aec0",
        "mq-purple": "#b8a3e0",
        "mq-red": "#ff6b6b",
        "mq-teal": "#79c0ff",
      },
    },
  },
  plugins: [],
};

export default config;

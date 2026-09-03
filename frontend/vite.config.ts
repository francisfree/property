import path from "node:path"
import { fileURLToPath } from "node:url"
import { defineConfig } from "vitest/config"
import react from "@vitejs/plugin-react"

const __dirname = path.dirname(fileURLToPath(import.meta.url))

// Build output lands in the Spring Boot static resources directory so the
// frontend is served from the same origin/context-path in production.
const backendStaticDir = path.resolve(__dirname, "../src/main/resources/static")

// Allow overriding the Spring Boot context-path used by the dev proxy.
const backendContextPath = process.env.VITE_BACKEND_CONTEXT_PATH ?? "/property"

export default defineConfig({
  plugins: [react()],
  // The production build is served under the Spring Boot context-path, so asset
  // URLs must carry that prefix. During development Vite serves at the root.
  base: process.env.NODE_ENV === "production" ? "/property/" : "/",
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: ["./src/test/setup.ts"],
    css: false,
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: `http://localhost:8080${backendContextPath}`,
        changeOrigin: true,
      },
    },
  },
  build: {
    outDir: backendStaticDir,
    emptyOutDir: true,
  },
})
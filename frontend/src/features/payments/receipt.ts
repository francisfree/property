export function downloadBlob(blob: Blob, filename: string) {
  const url = URL.createObjectURL(blob)
  const a = document.createElement("a")
  a.href = url
  a.download = filename
  document.body.appendChild(a)
  a.click()
  a.remove()
  URL.revokeObjectURL(url)
}

export function printBlob(blob: Blob, win: Window | null) {
  if (!win) {
    throw new Error("Pop-up blocked. Allow pop-ups to print.")
  }
  const url = URL.createObjectURL(blob)

  const cleanup = () => {
    URL.revokeObjectURL(url)
    if (!win.closed) win.close()
  }
  const fallback = window.setTimeout(cleanup, 5 * 60_000)

  win.addEventListener(
    "load",
    () => {
      window.setTimeout(() => {
        if (win.closed) {
          cleanup()
          return
        }
        win.focus()
        win.print()
      }, 600)
    },
    { once: true },
  )
  win.addEventListener(
    "afterprint",
    () => {
      window.clearTimeout(fallback)
      cleanup()
    },
    { once: true },
  )

  win.location.href = url
}
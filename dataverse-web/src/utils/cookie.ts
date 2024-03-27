// 设置Cookie
export const setCookie = (name: string, value: any, days?: number) => {
  let expires = ''
  const path = '; /'

  if (days) {
    const date = new Date()

    // 将天数转换为毫秒并添加到当前日期上
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000)

    expires = '; expires=' + date.toUTCString()
  }

  document.cookie = name + '=' + encodeURIComponent(value) + path + expires
}

// 获取Cookie
export const getCookie = (name: string) => {
  const cookies = document.cookie.split('; ')

  for (let i = 0; i < cookies.length; i++) {
    const parts = cookies[i].split('=')

    if (parts[0] === name) {
      return decodeURIComponent(parts[1])
    }
  }

  return null
}

// 删除Cookie
export const deleteCookie = (name: string) => {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC'
}

// 检测cookie是否过期
export const isCookieExpired = (cookieName: string) => {
  const cookies = document.cookie.split('; ')
  for (let i = 0; i < cookies.length; i++) {
    const cookie = cookies[i].split('=')
    if (cookie[0] === cookieName) {
      const expires = cookie[1].split(';')[0]
      if (expires) {
        // 如果expires是一个UTC时间字符串
        const expirationDate = new Date(expires)
        return expirationDate < new Date()
      } else {
        // 如果max-age存在
        const maxAge = cookie[1].split(';')[1]
        if (maxAge) {
          const creationTime = new Date()
          return (
            creationTime.getTime() - creationTime.getTimezoneOffset() * 60000 >
            maxAge * 1000 + creationTime.getTime()
          )
        }
      }
    }
  }
  return true // 如果找不到cookie，我们假设它已经过期
}

// 查看cookie有效期
export const getCookiesWithExpiration = () => {
  const cookies = document.cookie.split('; ')
  const cookieExpirationMap = {}

  cookies.forEach((cookie) => {
    const [name, ...otherParts] = cookie.split('=')
    const cookieExpiration = otherParts.join('=') // 假设cookie的其余部分包含了有效期信息
    cookieExpirationMap[name] = cookieExpiration
  })

  return cookieExpirationMap
}

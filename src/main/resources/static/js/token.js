const token = searchparam('token')

if (token) {
    localStorage.setItem("access_token", token)
}

function searchparam(key) {
    return new URLSearchParams(location.search).get(key)
}
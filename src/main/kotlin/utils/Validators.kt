package ru.fanofstars.utils

import kotlinx.html.Entities
import ru.fanofstars.cache.InMemoryCache
import ru.fanofstars.register.RegisterReceiveRemote
import ru.fanofstars.register.RegisterResponseRemote

fun String.isValidEmail(): Boolean {
    if(this.isNotEmpty()){
        if(!InMemoryCache.userList.map{it.email}.contains(this)){
            return true
        }
    }
    return false
}
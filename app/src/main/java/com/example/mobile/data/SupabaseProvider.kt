package com.example.mobile.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient

const val SUPABASE_URL = "https://ttvwtixclzcivxoiksnf.supabase.co"
const val SUPABASE_KEY = "sb_publishable_kvREyCVi4qvr4KnFectafg_kq9Ej3vl"

object SupabaseProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth){
                scheme= "ecopulse"
                host= "reset-password"
            }
        }
    }
}

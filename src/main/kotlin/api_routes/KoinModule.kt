package api_routes

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repository.UserDBOps
import service.NumUsers
import service.CacheOps
import repository.FakeUserOps

val appModule = module {
    singleOf(::UserDBOps)
    singleOf(::NumUsers)
    singleOf(::CacheOps)
    singleOf(::FakeUserOps)
}
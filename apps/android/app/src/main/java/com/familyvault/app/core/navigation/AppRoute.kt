package com.familyvault.app.core.navigation

sealed interface AppRoute {
    val route: String

    data object Auth : AppRoute {
        override val route = "auth"
    }

    data object Onboarding : AppRoute {
        override val route = "onboarding"
    }

    data object Documents : AppRoute {
        override val route = "documents"
    }

    data object ImportFile : AppRoute {
        override val route = "import_file"
    }
    data object Profile : AppRoute {
        override val route = "profile"
    }
}
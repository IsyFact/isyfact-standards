val allowedLicenses = licenseClassifications.licensesByCategory["allow"].orEmpty()

fun PackageRule.LicenseRule.isHandled() =
        object : RuleMatcher {
            override val description = "isHandled($license)"

            override fun matches() =
                    license in allowedLicenses && ("-exception" !in license.toString() || " WITH " in license.toString())
        }

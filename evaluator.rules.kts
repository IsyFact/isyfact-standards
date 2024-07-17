val allowedLicenses = licenseClassifications.licensesByCategory["allow"].orEmpty()

//fun PackageRule.LicenseRule.isHandled() =
//        object : RuleMatcher {
//            override val description = "isHandled($license)"
//
//            override fun matches() =
//                    license in allowedLicenses && ("-exception" !in license.toString() || " WITH " in license.toString())
//        }

rule("LicenseWhitelist") {
    licenseFindings.forEach { finding ->
        val license = finding.license
        if (license !in allowedLicenses) {
            issue(
                    severity = Severity.ERROR,
                    message = "License $license is not in the allowed list."
            )
        }
    }
}
}

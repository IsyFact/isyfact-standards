val allowedLicenses = licenseClassifications.licensesByCategory["allow"].orEmpty()

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
